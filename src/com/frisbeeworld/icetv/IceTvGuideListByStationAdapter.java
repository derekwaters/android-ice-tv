package com.frisbeeworld.icetv;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.frisbeeworld.icetv.IceTvGuideListItem.ListItemType;

public class IceTvGuideListByStationAdapter extends BaseAdapter
{
	private final	Context							context;
	private final	ArrayList<IceTvGuideListItem>	itemList;
	private final	IceTvGuide						tvGuide;
	
	public IceTvGuideListByStationAdapter(Context context, IceTvGuide guide)
	{
		this.context = context;
		this.itemList = guide.GetGuideList();
		this.tvGuide = guide;
	}

	public int getCount()
	{
		return itemList.size();
	}

	public Object getItem(int position)
	{
		return itemList.get(position);
	}
	
	public long getItemId(int position)
	{
		if (itemList.get(position).GetType() == ListItemType.Program)
		{
			return itemList.get(position).GetProgram().GetSeriesId();
		}
		return -1;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		switch (itemList.get(position).GetType())
		{
		case Program:
			return new IceTvGuideListByStationProgram(context, itemList.get(position).GetProgram(), tvGuide);
		case Date:
			return new IceTvGuideListDateHeading(context, itemList.get(position).GetTime());
		}
		return null;
		// IllegalArgumentException newOne = new IllegalArgumentException();
		// throw newOne;
	}
}
