package com.frisbeeworld.icetv;


import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class IceTvProgramView extends Activity
{
	private static final int	ALERT_DIALOG_ID	= 12;
	
	private TextView	textTitle;
	private	TextView	textSubTitle;
	private TextView	textDateTime;
	private	TextView	textSummary;
	private ImageView	imageRating;
	private ImageView	imageSubtitles;
	private TextView	textDesc;
	private	TextView	textCast;
	private Button		btnRecordEpisode;
	private Button		btnRecordSeries;
	private Button		btnAddFavourite;
	
	private int			errorCause;
	private String		errorAdditionalInfo;
	
	private	IceProgram	theProgram = null;
	private IceTvGuide	tvGuide;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iceprogram);
    
        errorCause = 0;
        errorAdditionalInfo = new String();
        
        tvGuide = new IceTvGuide(getBaseContext());
        
        textTitle = (TextView) findViewById(R.id.program_view_title);
        textSubTitle = (TextView) findViewById(R.id.program_view_subtitle);
        textDateTime = (TextView) findViewById(R.id.program_view_datetime);
        textSummary = (TextView) findViewById(R.id.program_view_summary);
        textDesc = (TextView) findViewById(R.id.program_view_desc);
        textCast = (TextView) findViewById(R.id.program_view_cast);
        // imageRating = (ImageView) findViewById(R.id.program_view_rating);
        // imageSubtitles = (ImageView) findViewById(R.id.program_view_subtitles);

    	btnRecordEpisode = (Button) findViewById(R.id.program_view_record_episode);
    	btnRecordSeries = (Button) findViewById(R.id.program_view_record_series);
    	btnAddFavourite = (Button) findViewById(R.id.program_view_add_favourite);

    	// Debug mode only
    	btnRecordEpisode.setVisibility(View.GONE);
    	btnRecordSeries.setVisibility(View.GONE);
    	btnAddFavourite.setVisibility(View.GONE);
    	
        textSubTitle.setTextColor(Color.CYAN);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
        	theProgram = new IceProgram(extras);
        }
        
        btnRecordEpisode.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				try 
				{
					tvGuide.RecordEpisode(theProgram);
				} 
				catch (IceTvException e)
				{
					errorCause = e.GetCause();
					errorAdditionalInfo = e.GetAdditionalInfo();
					showDialog(ALERT_DIALOG_ID);
				}
			}
        });
        
        btnRecordSeries.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				RecordSeries();
			}
        });

        btnAddFavourite.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				try 
				{
					tvGuide.AddFavourite(theProgram);
				}
				catch (IceTvException e) 
				{
					errorCause = e.GetCause();
					errorAdditionalInfo = e.GetAdditionalInfo();
					showDialog(ALERT_DIALOG_ID);
				}
			}
        });
        if (theProgram != null)
        {
        	RefreshDisplay();
        }
    }
    
    public void RefreshDisplay()
    {
    	textTitle.setText(theProgram.GetTitle());
    	textSubTitle.setText(theProgram.GetSubTitle());
    	
    	SimpleDateFormat	myDateFormat = new SimpleDateFormat("E, MMM dd");
    	
    	String dateTimeSummary = String.format("%s, %02d:%02d - %02d:%02d, %s", 
    											tvGuide.GetStationList().Get(theProgram.GetStation()).GetMainDisplayName(), 
    											theProgram.GetStart().getHours(), 
    											theProgram.GetStart().getMinutes(),
    											theProgram.GetEnd().getHours(),
    											theProgram.GetEnd().getMinutes(),
    											myDateFormat.format(theProgram.GetStart()));
    	textDateTime.setText(dateTimeSummary);
    	textDesc.setText(theProgram.GetDescription());
    	
    	long durationInMs = theProgram.GetEnd().getTime() - theProgram.GetStart().getTime();
    	
    	String summary = (durationInMs / (1000 * 60)) + " mins"
			+ ", Rating: " + theProgram.GetRating().toString()
    		+ (theProgram.GetVideoQuality() == IceProgram.Quality.Quality_HDTV ? ", HD" : ", SD")
    		+ ((theProgram.GetAspectRatio() == IceProgram.AspectRatio.Ratio_16_9) ? ", WS" : "")
    		+ ((theProgram.GetSubtitles() && (theProgram.GetSubtitleType() == IceProgram.SubtitleType.Subtitle_Teletext)) ? ", Teletext" : "")
    		+ (theProgram.GetRepeat() ? ", Repeat" : "")
    		;
    	textSummary.setText(summary);
    	
    	/*
    	int resourceId = 0;
    	switch (theProgram.GetRating())
    	{
    	case C:
    		resourceId = R.drawable.rating_c;
    		break;
    	case PG:
    		resourceId = R.drawable.rating_pg;
    		break;
    	case M:
    		resourceId = R.drawable.rating_m;
    		break;
    	case MA:
    		resourceId = R.drawable.rating_ma;
    		break;
    	case AV:
    		resourceId = R.drawable.rating_av;
    		break;
    	case R:
    		resourceId = R.drawable.rating_r;
    		break;
    	}
    	imageRating.setImageResource(resourceId);
    	if (theProgram.GetSubtitles() && (theProgram.GetSubtitleType() == IceProgram.SubtitleType.Subtitle_Teletext))
    	{
    		imageSubtitles.setImageResource(R.drawable.subtitles_teletext);
    	}
    	else
    	{
    		imageSubtitles.setImageBitmap(null);
    	}
    	*/
    	
    	String cast = new String();
    	if (theProgram.GetDirectors().size() > 0)
    	{
    		cast += "Directed by: ";
    		boolean isFirst = true;
	    	for (String eachItem : theProgram.GetDirectors())
	    	{
	    		if (!isFirst)
	    		{
	    			cast += ", ";
	    		}
	    		isFirst = false;
	    		cast += eachItem;
	    	}
    	}
    	if (theProgram.GetActors().size() > 0)
    	{
    		if (cast.length() > 0)
    		{
    			cast += ", ";
    		}
    		cast += "Starring: ";
    		boolean isFirst = true;
    		for (String eachItem : theProgram.GetActors())
    		{
    			if (!isFirst)
    			{
    				cast += ", ";
    			}
    			isFirst = false;
    			cast += eachItem;
    		}
    	}
    	textCast.setText(cast);
    }
    
    public void RecordSeries()
    {
    	Intent i = new Intent(this, IceTvSeriesRecording.class);
    	i.putExtra(IceTvSeriesRecording.SeriesTitle, theProgram.GetTitle());
    	startActivityForResult(i, 0);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Bundle extras = data.getExtras();
		try 
		{
			tvGuide.RecordSeries(theProgram, 
								 extras.getInt(IceTvSeriesRecording.BundleDevice),
								 extras.getInt(IceTvSeriesRecording.BundleNetwork),
								 extras.getInt(IceTvSeriesRecording.BundleTime),
								 extras.getInt(IceTvSeriesRecording.BundleRecord),
								 extras.getInt(IceTvSeriesRecording.BundleQuality),
								 extras.getInt(IceTvSeriesRecording.BundleLimit));
		} 
		catch (IceTvException e) 
		{
			errorCause = e.GetCause();
			errorAdditionalInfo = e.GetAdditionalInfo();
			showDialog(ALERT_DIALOG_ID);
		}
	}

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id)
        {
        case ALERT_DIALOG_ID:
        	{
	    		String message = new String();
	    		message = "An error has occured downloading the IceTV guide: \n" + errorAdditionalInfo;
	    		
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setMessage(message)
	    		       .setCancelable(false)
	    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    		           public void onClick(DialogInterface dialog, int id) {
	    		                dialog.cancel();
	    		           }
	    		       });
	    		AlertDialog alert = builder.create();
	    		return alert;
        	}
        }
        return null;
    }   
}