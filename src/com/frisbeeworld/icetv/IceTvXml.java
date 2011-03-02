package com.frisbeeworld.icetv;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class IceTvXml
{
	public static final double	CurrentIceXmlVersion = 2.1;
	
	public static final SimpleTimeZone	gmtTimezone = new SimpleTimeZone(0, TimeZone.getAvailableIDs(0)[0]);
	
	public static final String	TvTag = "tv";
	public static final String	DateAttr = "date";
	public static final String	SourceUrlAttr = "source-info-url";
	public static final String	SourceNameAttr = "source-info-name";
	public static final String	GeneratorName = "generator-info-name";
	public static final String	GeneratorUrl = "generator-info-url";
	public static final String	IceXmlVersionTag = "ice-xml-version";
	public static final String	ChannelTag = "channel";
	public static final String	IdAttr = "id";
	public static final String	DisplayNameTag = "display-name";
	public static final String	RegionNameTag = "region-name";
	public static final String	LcnTag = "lcn";
	public static final String	ProgrammeTag = "programme";
	public static final String	StartAttr = "start";
	public static final String	StopAttr = "stop";
	public static final String	ChannelAttr = "channel";
	public static final String	TitleTag = "title";
	public static final String	LanguageAttr = "lang";
	public static final String	SubTitleTag = "sub-title";
	public static final String	DescTag = "desc";
	public static final String	CreditsTag = "credits";
	public static final String	DirectorTag = "director";
	public static final String	ActorTag = "actor";
	public static final String	DateTag = "date";
	public static final String	CategoryTag = "category";
	public static final String	EpisodeNumTag = "episode-num";
	public static final String	VideoTag = "video";
	public static final String	ColourTag = "colour";
	public static final String	AspectTag = "aspect";
	public static final String	QualityTag = "quality";
	public static final String	PreviouslyShownTag = "previously-shown";
	public static final String	SubtitlesTag = "subtitles";
	public static final String	TypeAttr = "type";
	public static final String	RatingTag = "rating";
	public static final String	SystemAttr = "system";
	public static final String	ValueTag = "value";
	
	public static final String	AspectRatio_16_9 = "16:9";
	public static final String	Quality_HDTV = "HDTV";
	public static final String	Subtitle_Teletext = "teletext";
	public static final String	Rating_C = "C";
	public static final String	Rating_PG = "PG";
	public static final String	Rating_M = "M";
	public static final String	Rating_MA = "MA";
	public static final String	Rating_AV = "AV";
	public static final String	Rating_R = "R";	
	
	public static String GetElementText(Element parentElement)
	{
		String	returnVal = new String();
		Node	childNode;
		
		childNode = parentElement.getFirstChild();
		if (childNode != null)
		{
			if (childNode.getNodeType() == Node.TEXT_NODE ||
				childNode.getNodeType() == Node.CDATA_SECTION_NODE)
			{
				returnVal = childNode.getNodeValue();
			}
		}
		return returnVal;
	}
	
	public static Element GetFirstChildElement(Element parentNode, String tagName)
	{
		Element		returnVal = null;
		NodeList	childNodes;
		
		childNodes = parentNode.getElementsByTagName(tagName);
		if (childNodes != null && childNodes.getLength() > 0)
		{
			returnVal = (Element)childNodes.item(0);
		}
		return returnVal;
	}
	
	public static Date ParseIceTvDate(String fromString, int timeZoneOffsetInSeconds)
	{
		Calendar		returnVal = new GregorianCalendar(gmtTimezone);
		
		returnVal.set(Calendar.MILLISECOND, 0);
		if (fromString.length() >= 4)
		{
			returnVal.set(Calendar.YEAR, Integer.parseInt(fromString.substring(0, 4)));
			
			if (fromString.length() >= 6)
			{
				returnVal.set(Calendar.MONTH, Integer.parseInt(fromString.substring(4, 6)) - 1);
		
				if (fromString.length() >= 8)
				{
					returnVal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fromString.substring(6, 8)));
					
					if (fromString.length() >= 10)
					{
						returnVal.set(Calendar.HOUR, Integer.parseInt(fromString.substring(8, 10)));
						
						if (fromString.length() >= 12)
						{
							returnVal.set(Calendar.MINUTE, Integer.parseInt(fromString.substring(10, 12)));
							
							if (fromString.length() >= 14)
							{
								returnVal.set(Calendar.SECOND, Integer.parseInt(fromString.substring(12, 14)));
							}
						}
					}
				}
			}
		}
		
		//returnVal.add(Calendar.SECOND, timeZoneOffsetInSeconds);
		
		return returnVal.getTime();
	}
	/*

	<programme start="20050207080000 +0000" stop="20050207083000 +0000" channel="5">
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

</tv>
	*/

}
