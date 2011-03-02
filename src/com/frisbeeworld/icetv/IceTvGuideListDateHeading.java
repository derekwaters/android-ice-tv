package com.frisbeeworld.icetv;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IceTvGuideListDateHeading extends LinearLayout
{
	private TextView	timeLabel;
	
	public IceTvGuideListDateHeading(Context context, Date time)
	{
		super(context);
		
		SimpleDateFormat	formatter = new SimpleDateFormat("EEEE, MMMM dd");
		String dateFormat = formatter.format(time);

		setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 3, 5, 0);
		this.timeLabel = new TextView(context);
		this.timeLabel.setGravity(Gravity.CENTER_HORIZONTAL);
		this.timeLabel.setText(dateFormat);
		this.timeLabel.setTextSize(16f);
		this.timeLabel.setTextColor(Color.LTGRAY);
		this.timeLabel.setBackgroundColor(Color.GRAY);
		this.addView(this.timeLabel, params);
	}	
};
