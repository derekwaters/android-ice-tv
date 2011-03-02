package com.frisbeeworld.icetv;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class IceTvMovieListItem extends LinearLayout
{
	protected IceMovie		theMovie;
	protected IceTvGuide	guide;
	
	protected TextView		titleText;
	protected TextView		dateTimeText;
	protected TextView		stationText;
	protected RatingBar		ratingBar;
	protected ImageView		previewImage;
	
	public IceTvMovieListItem(Context context, IceMovie theMovie, IceTvGuide guide)
	{
		super(context);
		
		this.theMovie = theMovie;
		this.guide = guide;
	
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.icemovie, this, true);
		
		this.setOrientation(LinearLayout.HORIZONTAL);
		
		titleText = (TextView) findViewById(R.id.movie_view_title);
		dateTimeText = (TextView) findViewById(R.id.movie_view_datetime);
		stationText = (TextView) findViewById(R.id.movie_view_station);
		ratingBar = (RatingBar) findViewById(R.id.movie_view_rating);
		previewImage = (ImageView) findViewById(R.id.movie_view_image);
		
		SimpleDateFormat	formatter = new SimpleDateFormat("dd-MMM-yyyy h:mmaa");
		
		titleText.setText(theMovie.GetBaseMovieName());
		dateTimeText.setText(formatter.format(theMovie.GetProgram().GetStart()));
		stationText.setText(guide.GetStationList().Get(theMovie.GetProgram().GetStation()).GetMainDisplayName());
		ratingBar.setStepSize(0.1f);
		ratingBar.setNumStars(10);
		ratingBar.setRating((float)theMovie.GetRating());
		previewImage.setImageBitmap(theMovie.GetImage());
	}
	
	public IceMovie GetMovie()
	{
		return theMovie;
	}
}
