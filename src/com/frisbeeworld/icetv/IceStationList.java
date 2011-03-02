package com.frisbeeworld.icetv;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class IceStationList
{
	private Map<Integer, IceStation>	mapStations;
	
	public IceStationList()
	{
		mapStations = new HashMap<Integer, IceStation>();
	}
	
	public void Clear()
	{
		mapStations = new HashMap<Integer, IceStation>();
	}
	
	public boolean Add(IceStation newStation)
	{
		boolean returnVal = false;
		
		if (mapStations.get(newStation.GetId()) == null)
		{
			mapStations.put(newStation.GetId(), newStation);
			returnVal = true;
		}
		return returnVal;
	}
	
	public IceStation Get(int withId)
	{
		return mapStations.get(withId);
	}
	
	public ArrayList<IceStation> GetList()
	{
		ArrayList<IceStation> returnVal = new ArrayList<IceStation>();
		
		for (int theKey : mapStations.keySet())
		{	
			returnVal.add(mapStations.get(theKey));
		}
		return returnVal;
	}
}
