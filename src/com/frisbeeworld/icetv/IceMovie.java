package com.frisbeeworld.icetv;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


public class IceMovie
{
	private IceProgram	baseProgram;
	private String		imdbUrl;
	private Bitmap		imdbPhoto;
	private double		imdbRating;
	private HashMap<String, IceMovie>	movieCache;
		
	public IceMovie(IceProgram basedOnProgram)
	{
		if (movieCache == null)
		{
			movieCache = new HashMap<String, IceMovie>();
		}
		baseProgram = basedOnProgram;
		imdbPhoto = null;
		imdbRating = 0.0;
	}
	
	public IceProgram GetProgram()
	{
		return baseProgram;
	}
	
	public double GetRating()
	{
		return imdbRating;
	}
	
	public String GetUrl()
	{
		return imdbUrl;
	}
	
	public Bitmap GetImage()
	{
		return imdbPhoto;
	}
	
	public String GetBaseMovieName()
	{
		String returnVal = baseProgram.GetTitle();
		if (returnVal.regionMatches(true, 0, "MOVIE:", 0, 6))
		{
			returnVal = returnVal.substring(6).trim();
		}
		return returnVal;
	}
	
	public void RetrieveImdbInfo() throws IceTvException
	{
		IceMovie	copyMe = (IceMovie)movieCache.get(GetBaseMovieName());
		if (copyMe != null)
		{
			imdbRating = copyMe.GetRating();
			imdbUrl = copyMe.GetUrl();
			imdbPhoto = copyMe.GetImage();
		}
		else
		{
			String		url = "http://www.frisbeeworld.com/phpimdb/imdbsimplesearch.php?name=" + URLEncoder.encode(GetBaseMovieName());
			String		response = IceTvHttpHelpers.GetUrl(url);
			if (response != null)
			{
				if (response.compareTo("null") != 0)
				{
					try
					{
						JSONObject responseObject = new JSONObject(response);
						imdbRating = responseObject.getDouble("rating");
						imdbUrl = responseObject.getString("url");
						imdbPhoto = DownloadPhoto(responseObject.getString("photo"));
						
						movieCache.put(GetBaseMovieName(), this);
					}
					catch (JSONException je)
					{
						throw new IceTvException(IceTvException.Cause_JSONParseError, je.getMessage());
					}
				}
			}
		}
	}	
	
	private Bitmap DownloadPhoto(String url)
	{
        Bitmap returnVal = null;
        try
        {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            returnVal = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
       } 
       catch (IOException e)
       {
           Log.e(IceMovie.class.getName(), "Error getting bitmap", e);
       }
       return returnVal;
    } 
}
