package com.frisbeeworld.icetv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.frisbeeworld.icetv.IceTvGuideListItem.ListItemType;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IceTvGuideListAdapter extends BaseAdapter
{
	private final	Context							context;
	private final	ArrayList<IceTvGuideListItem>	itemList;
	private final	IceTvGuide						tvGuide;
	
	public IceTvGuideListAdapter(Context context, IceTvGuide guide)
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
			return new IceTvGuideListByTimeProgram(context, itemList.get(position).GetProgram(), tvGuide);
		case Date:
			return new IceTvGuideListDateHeading(context, itemList.get(position).GetTime());
		case Time:
			return new IceTvGuideListTimeHeading(context, itemList.get(position).GetTime());
		}
		return null;
	}
}
