package com.frisbeeworld.icetv;

import java.util.ArrayList;

/*
<channel id="5">
	<display-name>Sydney Ten HD</display-name>
	<region-name>Sydney</region-name>
	<lcn>1</lcn>
	<lcn>12</lcn>
</channel>
*/

public class IceStation
{
	@Override
	public String toString()
	{
		return GetMainDisplayName();
	}

	private	int					id;
	private ArrayList<String>	displayNames;
	private String				regionName;
	private ArrayList<Integer>	lcn;
	
	public IceStation()
	{
		id = -1;
		regionName = "";
		displayNames = new ArrayList<String>();
		lcn = new ArrayList<Integer>();	
	}
	
	public int GetId()
	{
		return id;
	}
	public void SetId(int val)
	{
		id = val;
	}
	
	public String GetMainDisplayName()
	{
		return displayNames.get(0);
	}
	public ArrayList<String> GetDisplayNames()
	{
		return displayNames;
	}
	public void AddDisplayName(String newName)
	{
		displayNames.add(newName);
	}
	
	public String GetRegionName()
	{
		return regionName;
	}
	public void SetRegionName(String name)
	{
		regionName = name;
	}
	
	public void AddLcn(int newLcn)
	{
		lcn.add(newLcn);
	}
	
	
}
