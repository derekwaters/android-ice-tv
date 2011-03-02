package com.frisbeeworld.icetv;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class IceTvGuide
{
	private static boolean					isLoaded = false;
	private static IceStationList			stationList = new IceStationList();
	private static ArrayList<IceProgram>	programList = new ArrayList<IceProgram>();
	private ArrayList<IceTvGuideListItem>	guideList;
	private ArrayList<IceMovie>				movieList;
	private TreeSet<String>					genreSet;
	private	Context							context;
	private Date							currentDate;
	private String							programFilter;
	private IceStation						guideListStationFilter;
	private TimeRange						filterTimeRange;
	private String							categoryFilter;
	private int								contentLength;
	
	public enum TimeRange
	{
		Time_Range_None,
		Time_Range_Early,
		Time_Range_Morning,
		Time_Range_Afternoon,
		Time_Range_Evening
	}
	
	public IceTvGuide(Context context)
	{
		this.context = context;
		this.guideList = new ArrayList<IceTvGuideListItem>();
		this.movieList = null;
		this.currentDate = new Date();
		this.programFilter = new String();
		this.guideListStationFilter = null;
		this.filterTimeRange = TimeRange.Time_Range_None;
		this.categoryFilter = new String();
		this.genreSet = new TreeSet<String>();
	}
	
	public boolean IsByTimeMode()
	{
		return (guideListStationFilter == null);
	}
	public void SetByTimeMode()
	{
		guideListStationFilter = null;
	}
	public void SetShowStationGuide(int stationId)
	{
		guideListStationFilter = stationList.Get(stationId);
	}
	public int GetShowStationGuide()
	{
		if (guideListStationFilter == null)
		{
			return -1;
		}
		else
		{
			return guideListStationFilter.GetId();
		}
	}
	public void SetTimeRangeFilter(TimeRange newFilter)
	{
		this.filterTimeRange = newFilter;
	}
	public TimeRange GetTimeRangeFilter()
	{
		return this.filterTimeRange;
	}
	public void SetCategoryFilter(String filter)
	{
		categoryFilter = filter;
	}
	public String GetCategoryFilter()
	{
		return this.categoryFilter;
	}
	public ArrayList<String> GetCategories()
	{
		ArrayList<String> returnVal = new ArrayList<String>();
		for (String genre : genreSet)
		{
			returnVal.add(genre);
		}
		return returnVal;
	}
	
	public Date GetCurrentDate()
	{
		return currentDate;
	}
	public void SetCurrentDate(Date newDate)
	{
		currentDate = newDate;
		isLoaded = false;
	}
	
	public boolean IsGuideLoaded()
	{
		return isLoaded;
	}
	public void SetGuideLoaded(boolean loaded)
	{
		isLoaded = loaded;
	}
	
	public void SetFilter(String newFilter)
	{
		String	updateFilter = newFilter.toUpperCase();
		if (!updateFilter.equals(programFilter))
		{
			programFilter = newFilter.toUpperCase();
			GenerateGuideList();
		}
	}
	public String GetFilterText()
	{
		return programFilter;
	}
	
	public IceStationList GetStationList()
	{
		if (isLoaded)
		{
			return stationList;
		}
		return null;
	}
	
	public boolean RetrieveTvGuide(Handler notifyHandler) throws IceTvException
	{
		if (!isLoaded)
		{
			stationList.Clear();
			programList.clear();
			guideList.clear();
			movieList = null;
			genreSet.clear();
			
			int timeZoneOffsetInSeconds = 0; /*GetTimeZoneOffset();*/
			
			DownloadTvGuide(notifyHandler);
			
			// Notify the main app that the file is downloaded, and we're now parsing.
			//
			Message newMessage = Message.obtain(notifyHandler);
			newMessage.arg1 = IceTV.DOWNLOAD_COMPLETE_MSG;
			notifyHandler.sendMessage(newMessage);
			
			InputStream	theStream = LoadTvGuide();
			if (theStream != null)
			{
				isLoaded = ParseTvGuide(theStream, timeZoneOffsetInSeconds);
				
				try
				{
					theStream.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
					// No need to notify the user if there is a problem closing the file stream.
				}
			}
		}
		return isLoaded;
	}
	
	public void ClearCachedGuides()
	{
		File	checkFolder = new File("/sdcard/com.frisbeeworld.icetv/");
		Date 	currentDate = new Date();
		
		int		checkYear;
		int		checkMonth;
		int		checkDay;
		
		boolean	deleteFile;
		
		if (checkFolder.exists())
		{
			for (File eachFile : checkFolder.listFiles())
			{
				String	checkFilename = eachFile.getName();
				
				deleteFile = true;
				if (checkFilename.substring(0, 8).compareTo("iceguide") == 0 &&
						checkFilename.length() > 16)
				{
					checkYear = Integer.parseInt(checkFilename.substring(8, 12));
					checkMonth = Integer.parseInt(checkFilename.substring(12, 14));
					checkDay = Integer.parseInt(checkFilename.substring(14, 16));
				
					if (checkYear > currentDate.getYear() + 1900)
					{
						deleteFile = false;
					}
					else if (checkYear == currentDate.getYear() + 1900)
					{
						if (checkMonth > currentDate.getMonth() + 1)
						{
							deleteFile = false;
						}
						else if (checkMonth == currentDate.getMonth() + 1)
						{
							deleteFile = (checkDay < currentDate.getDate());
						}
					} 
				}
				if (deleteFile)
				{
					eachFile.delete();
				}
			}
		}
	}
	
	private int GetTimeZoneOffset() throws IceTvException
	{
		int				returnVal = 0;
		InputStream 	checkStream;
		StringBuilder	builder = new StringBuilder();
		int				readByte;
		int				utc_time;
		int				user_time;
		String			checkString;
		int				findNewLine;
		int				findEnd;
		
		checkStream = DownloadIceTvUrl("http://iceguide.icetv.com.au/cgi-bin/epg/iceguide.cgi?op=get_user_time");
		if (checkStream != null)
		{
			try
			{
				readByte = checkStream.read();
				while (readByte >= 0)
				{
					builder.append((char)readByte);
					readByte = checkStream.read();
				}
				
				checkString = builder.toString();
				if (checkString.substring(0, 10).compareTo("local_time") == 0)
				{
					findNewLine = checkString.indexOf("\n");
					if (findNewLine > 0)
					{
						user_time = Integer.parseInt(checkString.substring(11, findNewLine));
						findEnd = checkString.indexOf("\n", findNewLine + 1);
						if (findEnd > 0)
						{
							utc_time = Integer.parseInt(checkString.substring(findNewLine + 10, findEnd));
						
							returnVal = user_time - utc_time;
						}
					}
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
				// Swallow the exception - we'll just work off a zero time offset
			}
		}
		
		return returnVal;
	}
	
	private boolean ParseTvGuide(InputStream fromStream, int timeZoneOffsetInSeconds) throws IceTvException
	{
		boolean status = false;

		try
		{
			SAXParserFactory 	factory = SAXParserFactory.newInstance();
			SAXParser			parser = factory.newSAXParser();
			XMLReader			xmlReader = parser.getXMLReader();
			InputSource			inputSource = new InputSource(fromStream);
			
			IceTvGuideParser	tvGuideParserHandler = new IceTvGuideParser(context, stationList, programList, timeZoneOffsetInSeconds);
			xmlReader.setContentHandler(tvGuideParserHandler);
			xmlReader.parse(inputSource);
			fromStream.close();
			
			status = true;
		}
		catch (FactoryConfigurationError fce)
		{
			throw new IceTvException(IceTvException.Cause_XMLParseError, fce.getMessage());
		}
		catch (ParserConfigurationException pce)
		{
			throw new IceTvException(IceTvException.Cause_XMLParseError, pce.getMessage());
		}
		catch (SAXException se)
		{
			throw new IceTvException(IceTvException.Cause_XMLParseError, se.getMessage());
		}
		catch (IOException ie)
		{
			throw new IceTvException(IceTvException.Cause_XMLParseError, ie.getMessage());
		}
		return status;
	}
	
	private class IceTvGuideParser extends DefaultHandler
	{
		private final static int		PARSING_UNKNOWN = 0;
		private final static int		PARSING_STATION = 1;
		private final static int		PARSING_PROGRAM = 2;
		
		private IceStationList			stationList;
		private ArrayList<IceProgram>	programList;
		private StringBuilder			builder = null;
		private Context					context = null;
		private IceProgram				currentProgram;
		private IceStation				currentStation;
		private int						currentlyParsing;
		private int						timeZoneOffsetInSeconds;
		
		public IceTvGuideParser(Context context, IceStationList stationList, ArrayList<IceProgram> programList, int timeZoneOffsetInSeconds)
		{
			this.context = context;
			this.stationList = stationList;
			this.programList = programList;
			 
			this.timeZoneOffsetInSeconds = timeZoneOffsetInSeconds;
		}

	    public void startDocument() throws SAXException
	    {
	    	stationList.Clear();
	    	programList.clear();
	    	currentlyParsing = PARSING_UNKNOWN;
	    }
	                                                                
	    public void endDocument() throws SAXException
	    {
	    }
	    
	    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException 
	    {
	    	int	checkStation;
	    	
	    	   try {
				builder = new StringBuilder("");
				   if (localName.equals(IceTvXml.TvTag))
				   {
					   
				   }
				   else if (localName.equals(IceTvXml.ChannelTag))
				   {
					   currentlyParsing = PARSING_STATION;
					   currentStation = new IceStation();
					   currentStation.SetId(Integer.parseInt(atts.getValue(IceTvXml.IdAttr)));
				   }
				   else if (localName.equals(IceTvXml.ProgrammeTag))
				   {
					   currentlyParsing = PARSING_PROGRAM;
					   currentProgram = null;
					   
					   checkStation = Integer.parseInt(atts.getValue(IceTvXml.ChannelAttr));
					   currentStation = stationList.Get(checkStation);
					   if (currentStation != null)
					   {
						   currentProgram = new IceProgram();
						   
						   currentProgram.SetStart(IceTvXml.ParseIceTvDate(atts.getValue(IceTvXml.StartAttr), timeZoneOffsetInSeconds));
						   currentProgram.SetEnd(IceTvXml.ParseIceTvDate(atts.getValue(IceTvXml.StopAttr), timeZoneOffsetInSeconds));
						   currentProgram.SetStation(checkStation);
					   }
				   }
				   else if (localName.equals(IceTvXml.SubtitlesTag))
				   {
					   if (currentProgram != null)
					   {
						   currentProgram.SetHasSubtitles(true);
						   currentProgram.SetSubtitleType(IceProgram.GetSubtitleTypeFromString(atts.getValue(IceTvXml.TypeAttr)));
					   }
				   }
			}
	    	catch (NumberFormatException e)
	    	{
				throw new SAXException(e);
			}
	    }
	    
	      public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	      {
	    	  switch (currentlyParsing)
	    	  {
	    	  case PARSING_STATION:
		    	  if (localName.equals(IceTvXml.ChannelTag))
		    	  {
	    			  stationList.Add(currentStation);
	    			  currentlyParsing = PARSING_UNKNOWN;
		    	  }
		    	  else if (localName.equals(IceTvXml.DisplayNameTag))
		    	  {
		    		  currentStation.AddDisplayName(builder.toString());
		    	  }
		    	  else if (localName.equals(IceTvXml.RegionNameTag))
		    	  {
		    		  currentStation.SetRegionName(builder.toString());
		    	  }
		    	  else if (localName.equals(IceTvXml.LcnTag))
		    	  {
		    		  currentStation.AddLcn(Integer.parseInt(builder.toString()));
		    	  }
		    	  break;
	    	  case PARSING_PROGRAM:
	    		  if (currentProgram != null)
	    		  {
			    	  if (localName.equals(IceTvXml.ProgrammeTag))
			    	  {
		    			  programList.add(currentProgram);
			    	  }
			    	  else if (localName.equals(IceTvXml.TitleTag))
			    	  {
		    			  currentProgram.SetTitle(builder.toString());
			    	  }
			    	  else if (localName.equals(IceTvXml.SubTitleTag))
			    	  {
		    			  currentProgram.SetSubTitle(builder.toString());
			    	  }
			    	  else if (localName.equals(IceTvXml.EpisodeNumTag))
			    	  {
			    		  currentProgram.SetEpisodeNumber(builder.toString());
			    	  }
			    	  else if (localName.equals(IceTvXml.DescTag))
			    	  {
		    			  currentProgram.SetDesc(builder.toString());
			    	  }
			    	  else if (localName.equals(IceTvXml.CategoryTag))
			    	  {
			    		  String genre = builder.toString();
			    		  if (genre.length() > 0)
			    		  {
				    		  genreSet.add(genre);
				    		  currentProgram.AddCategory(genre);
			    		  }
			    	  }
			    	  else if (localName.equals(IceTvXml.DateTag))
			    	  {
		    			  currentProgram.SetDate(builder.toString());
			    	  }
			    	  else if (localName.equals(IceTvXml.QualityTag))
			    	  {
			    		  currentProgram.SetQuality(IceProgram.GetQualityFromString(builder.toString()));
			    	  }
			    	  else if (localName.equals(IceTvXml.AspectTag))
			    	  {
			    		  currentProgram.SetVideoAspectRatio(IceProgram.GetAspectRatioFromString(builder.toString()));
			    	  }
			    	  else if (localName.equals(IceTvXml.ColourTag))
			    	  {
			    		  currentProgram.SetVideoColour(true);
			    	  }
			    	  else if (localName.equals(IceTvXml.PreviouslyShownTag))
			    	  {
			    		  currentProgram.SetRepeat(true);
			    	  }
			    	  else if (localName.equals(IceTvXml.ValueTag))
			    	  {
			    		  currentProgram.SetRating(IceProgram.GetRatingFromString(builder.toString()));
			    	  }
			    	  else if (localName.equals(IceTvXml.DirectorTag))
			    	  {
			    		  currentProgram.AddDirector(builder.toString());
			    	  }
			    	  else if (localName.equals(IceTvXml.ActorTag))
			    	  {
			    		  currentProgram.AddActor(builder.toString());
			    	  }
	    		  }
		    	  break;
	    	  case PARSING_UNKNOWN:
	    		  break;
	    	  }
	      }
	      public void characters(char ch[], int start, int length)
	      {
	          String theString = new String(ch,start,length);
	          builder.append(theString);
	      }
	}

	
	private InputStream LoadTvGuide() throws IceTvException
	{
		InputStream				returnVal = null;
		
		try
		{
			File	checkFolder = new File("/sdcard/com.frisbeeworld.icetv/");
			if (!checkFolder.exists())
			{
				if (!checkFolder.mkdir())
				{
					throw new IceTvException(IceTvException.Cause_FailedToCreateGuideFolder, "");
				}
			}
			String	localFilename = String.format("iceguide%04d%02d%02d.xml", currentDate.getYear() + 1900, currentDate.getMonth() + 1, currentDate.getDate());
			File	tempFile = new File(checkFolder, localFilename);
			returnVal = new FileInputStream(tempFile);
		}
		catch (FileNotFoundException e)
		{
			throw new IceTvException(IceTvException.Cause_FailedToOpenGuideFile, e.getMessage());
		}
		return returnVal;
	}
	
	private class IceTvAuthenticator extends Authenticator
	{
		private PasswordAuthentication theAuthentication;
		private boolean					invalidUsernamePassword;
		
		public IceTvAuthenticator(String userName, String password)
		{
			this.theAuthentication = new PasswordAuthentication(userName, password.toCharArray());
			this.invalidUsernamePassword = false;
		}
		
		public boolean InvalidUsernamePassword()
		{
			return invalidUsernamePassword;
		}
		
		@Override
		protected PasswordAuthentication getPasswordAuthentication() 
		{
			PasswordAuthentication returnVal = this.theAuthentication;
			this.theAuthentication = null;	// Only try the username and password once.
			if (returnVal == null)
			{
				invalidUsernamePassword = true;
			}
			return returnVal;
		}
	}

	private final Handler 	HttpHandler = new Handler()
	{
		public void handleMessage(final Message msg)
		{
			if (msg != null)
			{
				Bundle getData = msg.peekData();
				if (getData != null)
				{
					String response = getData.getString("RESPONSE");
				}
			}
		}
	};
	
	private void PostIceTvRequest(String url, Map<String, String> params)
	{
		String					userName;
		String					password;
    	SharedPreferences 		tsSettings = context.getSharedPreferences(IceTvSettings.ICETV_SETTINGS_LABEL, 0);
    	
    	userName = tsSettings.getString(IceTvSettings.ICETV_SETTING_USERNAME, "");
    	password = tsSettings.getString(IceTvSettings.ICETV_SETTING_PASSWORD, "");

    	IceTvHttpHelpers.PostRequest(url, params, userName, password, HttpHandler);
	}
		
	private InputStream DownloadIceTvUrl(String url) throws IceTvException
	{
		InputStream				returnVal = null;
		String					userName;
		String					password;
		URL						getURL = null;
		IceTvAuthenticator		theAuth = null;

		// http://iceguide.icetv.com.au/cgi-bin/epg/iceguide.cgi?op=help
    	SharedPreferences 		tsSettings = context.getSharedPreferences(IceTvSettings.ICETV_SETTINGS_LABEL, 0);
    	
    	userName = tsSettings.getString(IceTvSettings.ICETV_SETTING_USERNAME, "");
    	password = tsSettings.getString(IceTvSettings.ICETV_SETTING_PASSWORD, "");
    	theAuth = new IceTvAuthenticator(userName, password);
    	Authenticator.setDefault(theAuth);
    	    	
		try
		{
			getURL = new URL(url);
		}
		catch (MalformedURLException e)
		{
			throw new IceTvException(IceTvException.Cause_BadWebAddress, e.getMessage() + "(" + url + ")");
		}
		if (getURL != null)
		{
			try
			{
				HttpURLConnection newConnection = (HttpURLConnection) getURL.openConnection();

				contentLength = newConnection.getContentLength();
				returnVal = newConnection.getInputStream();
			}
			catch (UnknownHostException uhe)
			{
				throw new IceTvException(IceTvException.Cause_FailedToConnectToWebsite, uhe.getMessage());
			}
			catch (FileNotFoundException fnfe)
			{
				if (theAuth.InvalidUsernamePassword())
				{
					throw new IceTvException(IceTvException.Cause_UnknownCredentials, fnfe.getMessage());
				}
				else
				{
					throw new IceTvException(IceTvException.Cause_FailedToReadWebsite, fnfe.getMessage());
				}
			}
			catch(IOException e)
			{
				throw new IceTvException(IceTvException.Cause_FailedToReadWebsite, e.getMessage());
			}
		}
		return returnVal;
		
	}
	
	private void DownloadTvGuide(Handler progressHandler) throws IceTvException
	{    	
		InputStream		checkStream;
		OutputStream	writeStream;
		File			outputFolder;
		File			writeFile;
		int				bytesRead = 0;	
		int				totalBytesRead = 0;
		byte[]			buffer = new byte[4096];
		
		String retrievePath = new String();
    	retrievePath = "http://iceguide.icetv.com.au/cgi-bin/epg/iceguide.cgi?op=xmlguide&start_date="
			+ String.format("%04d%02d%02d", currentDate.getYear() + 1900, currentDate.getMonth() + 1, currentDate.getDate())
			+ "&days=1&desc_length=0";

		outputFolder = new File("/sdcard/com.frisbeeworld.icetv/");
		if (!outputFolder.exists())
		{
			if (!outputFolder.mkdir())
			{
				throw new IceTvException(IceTvException.Cause_FailedToCreateGuideFolder, "");
			}
		}
		
		String	localFilename = String.format("iceguide%04d%02d%02d.xml", currentDate.getYear() + 1900, currentDate.getMonth() + 1, currentDate.getDate());
		writeFile = new File(outputFolder, localFilename);
		if (writeFile.exists())
		{
			// TODO: Here we should check the Ice Guide updates to make sure the file we
			// have cached on disk is valid.
			//
		}
		else
		{
			try
			{
				checkStream = DownloadIceTvUrl(retrievePath); 
					
				BufferedInputStream inputBuffer = new BufferedInputStream(checkStream);
				
				writeFile.createNewFile();
				
				writeStream = new FileOutputStream(writeFile);
				
				BufferedOutputStream outputBuffer = new BufferedOutputStream(writeStream);
				
				while ((bytesRead = inputBuffer.read(buffer)) != -1)
				{
					outputBuffer.write(buffer, 0, bytesRead);
					if (contentLength > 0)
					{
						totalBytesRead += bytesRead;
						
						Message			progressMessage = Message.obtain(progressHandler);
						progressMessage.arg1 = IceTV.DOWNLOAD_PROGRESS_MSG;
						progressMessage.arg2 = ((100 * totalBytesRead) / contentLength);
						progressHandler.sendMessage(progressMessage);
					}
	            }
				
				outputBuffer.close();
				inputBuffer.close();
			}
			catch (FileNotFoundException fnfe)
			{
				throw new IceTvException(IceTvException.Cause_FailedToDownloadGuide, "Couldn't open the output file");
			}
			catch (SecurityException se)
			{
				throw new IceTvException(IceTvException.Cause_FailedToDownloadGuide, "Not allowed to open the output file");
			}
			catch (IOException e)
			{
				throw new IceTvException(IceTvException.Cause_FailedToDownloadGuide, e.getMessage());
			}
		}
	}
		
	public boolean GenerateGuideList()
	{
		Date		lastTime = null;
		// int			lastDay = -1;
		
		guideList.clear();
		
		for (IceProgram eachProgram : programList)
		{
			if (programFilter.length() == 0 || eachProgram.GetTitle().toUpperCase().contains(programFilter))
			{
				if (guideListStationFilter == null || eachProgram.GetStation() == guideListStationFilter.GetId())
				{
					Date theStart = eachProgram.GetStart();
					Date theEnd	= eachProgram.GetEnd();
					
					if (CheckTimeRange(filterTimeRange, theStart, theEnd))
					{
						if (categoryFilter.length() == 0 || eachProgram.FindCategory(categoryFilter))
						{
							if (lastTime == null || theStart.compareTo(lastTime) != 0)
							{
								if (guideListStationFilter == null)
								{
									guideList.add(new IceTvGuideListItem(theStart, IceTvGuideListItem.ListItemType.Time));
									lastTime = theStart;
								}
							}
							guideList.add(new IceTvGuideListItem(eachProgram));
						}
					}
				}
			}
		}
		return true;
	}
	
	public boolean CheckTimeRange(TimeRange wantedRange, Date checkTime, Date endTime)
	{
		boolean result = false;
		
		if (filterTimeRange == TimeRange.Time_Range_None)
		{
			Date today = new Date();
			if (checkTime.getYear() == today.getYear() &&
				checkTime.getMonth() == today.getMonth() &&
				checkTime.getDate() == today.getDate())
			{
				// Cause it's today, only show currently running or future programs
				//
				if (endTime.compareTo(today) >= 0)
				{
					result = true;
				}
			}
			else
			{
				result = true;
			}
		}
		else
		{
			int	hours = checkTime.getHours();
			switch (wantedRange)
			{
			case Time_Range_None:
				result = true;
			case Time_Range_Early:
				result = (hours < 6);
			case Time_Range_Morning:
				result = (hours >= 6 && hours < 12);
			case Time_Range_Afternoon:
				result = (hours >= 12 && hours < 18);
			case Time_Range_Evening:
				result = (hours >= 18);
			}
		}
		return result;
	}
	
	public ArrayList<IceTvGuideListItem> GetGuideList()
	{
		return guideList;
	}

	public ArrayList<IceMovie> GetMovieList()
	{
		return movieList;
	}
	
	public void RefreshMovieList() throws IceTvException
	{
		movieList = new ArrayList<IceMovie>();
		
		for (IceProgram eachProgram : programList)
		{
			if (eachProgram.IsMovie())
			{
				IceMovie newMovie = new IceMovie(eachProgram);
				newMovie.RetrieveImdbInfo();
				movieList.add(newMovie);
			}
		}
		Collections.sort(movieList, new IceMovieComparator());
	}
	
	public void RecordEpisode(IceProgram theProgram) throws IceTvException
	{
		String				url;
		Map<String, String> params = new HashMap<String, String>();
		
		url = "http://m.icetv.com.au/interactive/index.cgi?op=add_recording";
		params.put("op", "add_recording_mobile");
		// TODO: show_id is NOT the episode id.
		params.put("show_id", Integer.toString(theProgram.GetEpisodeId()));
		params.put("series_id", Integer.toString(theProgram.GetSeriesId()));
		params.put("scroll_pos", "0");
		params.put("next_op", "view_show_mobile");
		params.put("pimp_device_id", "58156");

		// try
		// {
			
			/*
			

			url = "http://m.icetv.com.au/interactive/index.cgi?op=add_recording
			

			

			
				<input type=hidden name=op value=add_recording_mobile>
			<input type=hidden name=next_op value="view_show_mobile">
			<input type=hidden name=show_id value="94280491">
			<input type=hidden name=scroll_pos value="0">
			<input type=hidden name="keywords" value="">
			
			Device:&nbsp;&nbsp;
			<select name="pimp_device_id" id="pimp_device_id">

<option value="58156" >Beyonwiz</option>


			</select>&nbsp;&nbsp;
			<input type=submit value=Record>
		</form>
		<br>
		

		
		<form method="get" action="/interactive/index.cgi">
			<input type=hidden name=op value="view_show_mobile">

			<input type=hidden name=show_id value="94280491">
			<input type=hidden name="show_series_options" value="1">
			<input type=hidden name=scroll_pos value="0">
			<input type=hidden name="keywords" value="">
			<input type="submit" value="Show Series Options">
		</form>

			

			

			
			<form method=post action="/interactive/index.cgi">
				<input type=hidden name=op value="add_fav_mobile">
				<input type=hidden name=next_op value="view_show_mobile">
				<input type=hidden name=show_id value="94280491">
				<input type=hidden name=series_id value="8517">
				<input type=hidden name=pimp_device_id value="">
				<input type=hidden name=task_uid value="">
				<input type=hidden name=show_id value="94280491">
				<input type=hidden name=scroll_pos value="0">

				<input type=hidden name=is_show_upcoming value="">
				<input type=hidden name="keywords" value="">
				<input type=submit value="Add Favourite">
			</form>

			*/
			PostIceTvRequest(url, params);
			// DownloadIceTvUrl(url).close();
		// } 
		// catch (IOException e)
		// {
		// 	throw new IceTvException(IceTvException.Cause_FailedToDownloadGuide, e.getMessage());
		// }
	}
	
	public void AddFavourite(IceProgram theProgram) throws IceTvException
	{
		// TODO: Correct this - use POST not GET
		/*
		try
		{
			DownloadIceTvUrl("http://iceguide.icetv.com.au/cgi-bin/epg/iceguide.cgi?op=add_fav&series_id=" + theProgram.GetSeriesId()).close();
		} 
		catch (IOException e)
		{
			throw new IceTvException(IceTvException.Cause_FailedToDownloadGuide, e.getMessage());
		}
		*/
	}
	
	public void RecordSeries(IceProgram theProgram, int deviceId, int networkId, int timeId, int recordId, int qualityId, int limitId) throws IceTvException
	{
		// TODO: Implement this
	}
}
