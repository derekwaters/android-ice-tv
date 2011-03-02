package com.frisbeeworld.icetv;

import java.util.Date;

public class IceTvGuideListItem
{
	public enum ListItemType
	{
		Program,
		Date,
		Time
	};
	
	public IceTvGuideListItem(IceProgram withProgram)
	{
		this.program = withProgram;
		this.time = null;
		this.type = ListItemType.Program;
	}
	
	public IceTvGuideListItem(Date withTime, ListItemType itemType)
	{
		this.program = null;
		this.time = withTime;
		this.type = itemType;
	}
	
	public ListItemType GetType()
	{
		return type;
	}
	
	public IceProgram GetProgram()
	{
		return program;
	}
	
	public Date GetTime()
	{
		return time;
	}
	
	private IceProgram 		program;
	private Date			time;
	private ListItemType	type;
}
