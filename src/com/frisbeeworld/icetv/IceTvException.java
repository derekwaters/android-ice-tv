package com.frisbeeworld.icetv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class IceTvException extends Exception
{
	public static final int	Cause_XMLParseError	= 1;
	public static final int	Cause_FailedToCreateGuideFolder = 2;
	public static final int	Cause_FailedToOpenGuideFile = 3;
	public static final int	Cause_BadWebAddress = 4;
	public static final int	Cause_FailedToConnectToWebsite = 5;
	public static final int	Cause_FailedToReadWebsite = 6;
	public static final int	Cause_FailedToDownloadGuide = 7;
	public static final int	Cause_UnknownCredentials = 8;
	public static final int Cause_JSONParseError = 9;
	
	public static final String	Bundle_AdditionalInfo = "AdditionalInfo";
	public static final String	Bundle_Cause = "Cause";
	
	public IceTvException(int cause, String additionalInfo)
	{
		this.cause = cause;
		this.additionalInfo = additionalInfo;
	}
	
	public int GetCause()
	{
		return this.cause;
	}
	
	public String GetAdditionalInfo()
	{
		return this.additionalInfo;
	}
	
	public static String GetCauseTitle(int cause)
	{
		String	returnVal = new String();
		switch (cause)
		{
		case Cause_XMLParseError:
			returnVal = "IceGuide XML Error";
			break;
		case Cause_FailedToCreateGuideFolder:
			returnVal = "IceGuide Folder Error";
			break;
		case Cause_FailedToOpenGuideFile:
			returnVal = "IceGuide File Error";
			break;
		case Cause_BadWebAddress:
		case Cause_FailedToConnectToWebsite:
		case Cause_FailedToReadWebsite:
		case Cause_FailedToDownloadGuide:
			returnVal = "IceGuide Connection Error";
			break;
		case Cause_UnknownCredentials:
			returnVal = "IceGuide Incorrect Username or Password";
			break;
		}
		return returnVal;
	}
	
	private	int		cause;
	private String	additionalInfo;
}
