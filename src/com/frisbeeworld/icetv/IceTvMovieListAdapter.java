package com.frisbeeworld.icetv;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class IceTvMovieListAdapter extends BaseAdapter
{
	private final	Context							context;
	private final	ArrayList<IceMovie>				itemList;
	private final	IceTvGuide						tvGuide;
	
	public IceTvMovieListAdapter(Context context, IceTvGuide guide)
	{
		this.context = context;
		this.itemList = guide.GetMovieList();
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
		return itemList.get(position).GetProgram().GetSeriesId();
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		return new IceTvMovieListItem(context, itemList.get(position), tvGuide);
	}
}
