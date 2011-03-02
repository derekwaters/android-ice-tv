package com.frisbeeworld.icetv;

import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;

public class IceProgram
{
	private static final String BundleStart	= "Start";
	private static final String BundleEnd = "End";
	private static final String BundleChannel = "Channel";

	private static final String BundleTitle = "Title";
	private static final String BundleTitleLanguage = "TitleLanguage";
	private static final String BundleSubTitle = "SubTitle";
	private static final String BundleSubTitleLanguage = "SubTitleLanguage";
	private static final String BundleDescription = "Description";
	private static final String BundleDescriptionLanguage = "DescriptionLanguage";
	private static final String BundleDate = "Date";
	private static final String BundleCategories = "Categories";
	private static final String BundleActors = "Actors";
	private static final String BundleDirectors = "Directors";
	private static final String BundleEpisodeId = "EpisodeId";
	private static final String BundleSeriesId = "SeriesId";
	private static final String BundleVideoColour = "VideoColour";
	private static final String BundleVideoAspectRatio = "VideoAspectRatio";
	private static final String BundleVideoQuality = "VideoQuality";
	private static final String BundlePreviouslyShow = "PreviouslyShown";
	private static final String BundleSubtitles = "Subtitles";
	private static final String BundleSubtitleType = "SubtitleType";
	private static final String BundleRating = "Rating";

	
	public enum AspectRatio
	{
		Ratio_16_9,
		Ratio_4_3
	};
	
	public enum Quality
	{
		Quality_Standard,
		Quality_HDTV
	};
	
	public enum SubtitleType
	{
		Subtitle_Teletext,
		Subtitle_Broadcast
	};
	
	public enum Rating
	{
		C,
		PG,
		M,
		MA,
		AV,
		R
	};
	
	private Date				start;
	private Date				end;
	private int					onStation;
	
	private String				title;
	private	String				titleLanguage;
	private String				subTitle;
	private String				subTitleLanguage;
	private String				description;
	private String				descriptionLanguage;
	private String				date;
	private ArrayList<String>	categories;
	private ArrayList<String>	actors;
	private ArrayList<String>	directors;
	private int					episodeId;
	private int					seriesId;
	private boolean				videoColour;
	private AspectRatio			videoAspectRatio;
	private Quality				videoQuality;
	private boolean				previouslyShown;
	private boolean				subtitles;
	private SubtitleType		subtitleType;
	private Rating				rating;

	
	public IceProgram()
	{
		start = new Date();
		end = new Date();
		categories = new ArrayList<String>();
		actors = new ArrayList<String>();
		directors = new ArrayList<String>();
		videoAspectRatio = IceProgram.AspectRatio.Ratio_4_3;
		videoQuality = IceProgram.Quality.Quality_Standard;
		subtitleType = IceProgram.SubtitleType.Subtitle_Broadcast;
		rating = IceProgram.Rating.PG;
		episodeId = -1;
		seriesId = -1;
	}
	
	
	public IceProgram(Bundle fromBundle)
	{
		start = new Date();
		end = new Date();
		categories = new ArrayList<String>();
		actors = new ArrayList<String>();
		directors = new ArrayList<String>();
		videoAspectRatio = IceProgram.AspectRatio.Ratio_4_3;
		videoQuality = IceProgram.Quality.Quality_Standard;
		subtitleType = IceProgram.SubtitleType.Subtitle_Broadcast;
		rating = IceProgram.Rating.PG;
		episodeId = -1;
		seriesId = -1;
		LoadFromBundle(fromBundle);
	}
	
	public int GetEpisodeId()
	{
		return episodeId;
	}
	public int GetSeriesId()
	{
		return seriesId;
	}
	public void SetEpisodeNumber(String episodeNum)
	{
		int	findPos;
		findPos = episodeNum.indexOf('-');
		if (findPos > 0)
		{
			this.seriesId = Integer.parseInt(episodeNum.substring(0, findPos));
			this.episodeId = Integer.parseInt(episodeNum.substring(findPos + 1));
		}
	}
	
	public Date GetStart()
	{
		return start;
	}
	public void SetStart(Date newStart)
	{
		start = newStart;
	}
	
	public Date GetEnd()
	{
		return end;
	}
	public void SetEnd(Date newEnd)
	{
		end = newEnd;
	}
	
	public String GetTitle()
	{
		return title;
	}
	public void SetTitle(String newTitle)
	{
		title = newTitle;
	}

	public boolean IsMovie()
	{
		// TODO: Check too see if this can be determined using the Genres of the program.
		return title.regionMatches(true, 0, "movie:", 0, 6);
	}
	
	public String GetSubTitle()
	{
		return subTitle;
	}
	public void SetSubTitle(String newSubTitle)
	{
		subTitle = newSubTitle;
	}
	
	public int GetStation()
	{
		return onStation;
	}
	public void SetStation(int newStation)
	{
		onStation = newStation;
	}
	
	public void AddCategory(String category)
	{
		categories.add(category);
	}
	public boolean FindCategory(String checkCategory)
	{
		return categories.contains(checkCategory);
	}
	public void AddActor(String actor)
	{
		actors.add(actor);
	}
	public ArrayList<String> GetActors()
	{
		return actors;
	}
	public void AddDirector(String director)
	{
		directors.add(director);
	}
	public ArrayList<String> GetDirectors()
	{
		return directors;
	}
	
	public void SetDesc(String newDesc)
	{
		description = newDesc;
	}
	public String GetDescription()
	{
		return description;
	}

	public void SetDate(String newDate)
	{
		date = newDate;
	}

	public void SetVideoColour(boolean isColour)
	{
		videoColour = isColour;
	}
	public void SetVideoAspectRatio(IceProgram.AspectRatio ratio)
	{
		videoAspectRatio = ratio;
	}
	public AspectRatio GetAspectRatio()
	{
		return videoAspectRatio;
	}
	public void SetQuality(Quality quality)
	{
		videoQuality = quality;
	}
	public Quality GetVideoQuality()
	{
		return videoQuality;
	}
	public void SetRepeat(boolean repeat)
	{
		previouslyShown = repeat;
	}
	public boolean GetRepeat()
	{
		return previouslyShown;
	}
	public void SetHasSubtitles(boolean subtitles)
	{
		this.subtitles = subtitles;
	}
	public boolean GetSubtitles()
	{
		return subtitles;
	}
	public void SetSubtitleType(SubtitleType type)
	{
		subtitleType = type;
	}
	public SubtitleType GetSubtitleType()
	{
		return subtitleType;
	}
	public void SetRating(Rating rating)
	{
		this.rating = rating;
	}
	public Rating GetRating()
	{
		return this.rating;
	}
	
	
	
	public void LoadFromBundle(Bundle theBundle)
	{
		start.setTime(theBundle.getLong(BundleStart, start.getTime()));
		end.setTime(theBundle.getLong(BundleEnd, end.getTime()));
		onStation = theBundle.getInt(BundleChannel, onStation);

		title = theBundle.getString(BundleTitle);
		titleLanguage = theBundle.getString(BundleTitleLanguage);
		subTitle = theBundle.getString(BundleSubTitle);
		subTitleLanguage = theBundle.getString(BundleSubTitleLanguage);
		description = theBundle.getString(BundleDescription);
		descriptionLanguage = theBundle.getString(BundleDescriptionLanguage);
		date = theBundle.getString(BundleDate);
		categories = theBundle.getStringArrayList(BundleCategories);
		actors = theBundle.getStringArrayList(BundleActors);
		directors = theBundle.getStringArrayList(BundleDirectors);
		episodeId = theBundle.getInt(BundleEpisodeId, episodeId);
		seriesId = theBundle.getInt(BundleSeriesId, seriesId);
		videoColour = theBundle.getBoolean(BundleVideoColour, videoColour);
		videoAspectRatio = AspectRatio.valueOf(theBundle.getString(BundleVideoAspectRatio));
		videoQuality = Quality.valueOf(theBundle.getString(BundleVideoQuality));
		previouslyShown = theBundle.getBoolean(BundlePreviouslyShow, previouslyShown);
		subtitles = theBundle.getBoolean(BundleSubtitles, subtitles);
		subtitleType = SubtitleType.valueOf(theBundle.getString(BundleSubtitleType));
		rating = Rating.valueOf(theBundle.getString(BundleRating));		
	}
	public void	SaveToBundle(Bundle theBundle)
	{
		theBundle.putLong(BundleStart, start.getTime());
		theBundle.putLong(BundleEnd, end.getTime());
		theBundle.putInt(BundleChannel, onStation);

		theBundle.putString(BundleTitle, title);
		theBundle.putString(BundleTitleLanguage, titleLanguage);
		theBundle.putString(BundleSubTitle, subTitle);
		theBundle.putString(BundleSubTitleLanguage, subTitleLanguage);
		theBundle.putString(BundleDescription, description);
		theBundle.putString(BundleDescriptionLanguage, descriptionLanguage);
		theBundle.putString(BundleDate, date);
		theBundle.putStringArrayList(BundleCategories, categories);
		theBundle.putStringArrayList(BundleActors, actors);
		theBundle.putStringArrayList(BundleDirectors, directors);
		theBundle.putInt(BundleEpisodeId, episodeId);
		theBundle.putInt(BundleSeriesId, seriesId);
		theBundle.putBoolean(BundleVideoColour, videoColour);
		theBundle.putString(BundleVideoAspectRatio, videoAspectRatio.toString());
		theBundle.putString(BundleVideoQuality, videoQuality.toString());
		theBundle.putBoolean(BundlePreviouslyShow, previouslyShown);
		theBundle.putBoolean(BundleSubtitles, subtitles);
		theBundle.putString(BundleSubtitleType, subtitleType.toString());
		theBundle.putString(BundleRating, rating.toString());
	}

	public static AspectRatio GetAspectRatioFromString(String input)
	{
		AspectRatio returnVal = IceProgram.AspectRatio.Ratio_4_3;
		if (input.compareToIgnoreCase(IceTvXml.AspectRatio_16_9) == 0)
		{
			returnVal = IceProgram.AspectRatio.Ratio_16_9;
		}
		return returnVal;
	}

	public static Quality GetQualityFromString(String input)
	{
		Quality returnVal = Quality.Quality_Standard;
		if (input.compareToIgnoreCase(IceTvXml.Quality_HDTV) == 0)
		{
			returnVal = Quality.Quality_HDTV;
		}
		return returnVal;
	}

	public static SubtitleType GetSubtitleTypeFromString(String input)
	{
		SubtitleType returnVal = SubtitleType.Subtitle_Broadcast;
		if (input.compareToIgnoreCase(IceTvXml.Subtitle_Teletext) == 0)
		{
			returnVal = SubtitleType.Subtitle_Teletext;
		}
		return returnVal;
	}

	public static Rating GetRatingFromString(String input)
	{
		Rating returnVal = Rating.C;
		if (input.compareToIgnoreCase(IceTvXml.Rating_PG) == 0)
		{
			returnVal = Rating.PG;
		}
		else if (input.compareToIgnoreCase(IceTvXml.Rating_M) == 0)
		{
			returnVal = Rating.M;
		}
		else if (input.compareToIgnoreCase(IceTvXml.Rating_MA) == 0)
		{
			returnVal = Rating.MA;
		}
		else if (input.compareToIgnoreCase(IceTvXml.Rating_AV) == 0)
		{
			returnVal = Rating.AV;
		}
		else if (input.compareToIgnoreCase(IceTvXml.Rating_R) == 0)
		{
			returnVal = Rating.R;
		}
		return returnVal;
	}
	
	
	/*
	 * 
	 * 
	 * 	<programme start="20050207080000 +0000" stop="20050207083000 +0000" channel="5">
		<title lang="en">The Simpsons</title>
		<sub-title lang="en">The Simpsons Go to Australia</sub-title>
		<desc lang="en">Animated series following the lives of the mundane, middle class Simpson family.</desc>
		<credits>
			<director>Mark Kirkland</director>
			<actor>Julie Kavner</actor>
			<actor>Hank Azaria</actor>
		</credits>
		<date>2008</date>
		<category lang="en">Cartoon</category>
		<category lang="en">Comedy</category>
		<episode-num system="icetv">12104</episode-num>
		<video>
			<colour>yes</colour>
			<aspect>16:9</aspect>
			<quality>HDTV</quality>
		</video>
		<previously-shown/>
		<subtitles type="teletext"/>
		<rating system="">
			<value>PG</value>
		</rating>
	</programme>

*/
}
