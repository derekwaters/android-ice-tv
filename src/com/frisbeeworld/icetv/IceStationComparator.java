package com.frisbeeworld.icetv;

import java.util.Comparator;

public class IceStationComparator implements Comparator
{
	@Override
	public int compare(Object object1, Object object2)
	{
		IceStation	station1 = (IceStation)object1;
		IceStation	station2 = (IceStation)object2;

		return station1.GetMainDisplayName().compareTo(station2.GetMainDisplayName());
	}
	
}
