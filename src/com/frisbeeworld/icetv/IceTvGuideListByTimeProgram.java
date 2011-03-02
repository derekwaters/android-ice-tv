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

public class IceTvGuideListByTimeProgram extends IceTvGuideListProgram
{
	private TextView		titleLabel;
	private TextView		subTitleLabel;
	private	TextView		stationLabel;
	private	LinearLayout	subLayout;
	
	public IceTvGuideListByTimeProgram(Context context, IceProgram program, IceTvGuide guide)
	{
		super(context, program, guide);
	}

	@Override
	public void SetupLayout(Context context, IceTvGuide guide)
	{
		setOrientation(LinearLayout.HORIZONTAL);
		
		
		LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams fillParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		wrapParams.setMargins(5, 3, 5, 0);
		fillParams.setMargins(5, 3, 5, 0);
		
		this.subLayout = new LinearLayout(context);
		this.subLayout.setOrientation(LinearLayout.VERTICAL);
		
		this.titleLabel = new TextView(context);
		this.titleLabel.setGravity(Gravity.LEFT);
		this.titleLabel.setSingleLine();
		this.titleLabel.setEllipsize(TextUtils.TruncateAt.END);
		this.titleLabel.setText(theProgram.GetTitle());
		this.titleLabel.setTextSize(16f);

		Date currentDate = new Date();
		if (theProgram.GetStart().before(currentDate) &&
				theProgram.GetEnd().after(currentDate))
		{
			this.titleLabel.setTextColor(Color.YELLOW);
		}
		else
		{
			this.titleLabel.setTextColor(Color.WHITE);
		}
		this.subLayout.addView(this.titleLabel, fillParams);

		this.subTitleLabel = new TextView(context);
		this.subTitleLabel.setGravity(Gravity.LEFT);
		this.subTitleLabel.setSingleLine();
		this.subTitleLabel.setEllipsize(TextUtils.TruncateAt.END);
		this.subTitleLabel.setText(theProgram.GetSubTitle());
		this.subTitleLabel.setTextSize(12f);
		this.subTitleLabel.setTextColor(Color.CYAN);
		this.subLayout.addView(this.subTitleLabel, fillParams);
		
		fillParams.weight = 1;
		this.addView(this.subLayout, fillParams);
		
		this.stationLabel = new TextView(context);
		this.stationLabel.setGravity(Gravity.RIGHT);
		this.stationLabel.setSingleLine();
		IceStation theStation = guide.GetStationList().Get(theProgram.GetStation());
		if (theStation != null)
		{
			this.stationLabel.setText(theStation.GetMainDisplayName());
		}
		this.stationLabel.setTextSize(16f);
		this.stationLabel.setTextColor(Color.LTGRAY);
		wrapParams.weight = 0;
		this.addView(this.stationLabel, wrapParams);
		
	}
}
