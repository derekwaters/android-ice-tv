package com.frisbeeworld.icetv;

import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class IceTvGuideFilters extends Dialog
{
	private EditText		editSearch;
	private Spinner			spinStation;
	private Spinner			spinTime;
	private Spinner			spinGenre;
	private Button			btnFilter;
	
	private IceTvGuide				tvGuide;
	private ArrayList<IceStation>	currentStationList;
	private ArrayList<String>		tempGenreList;

	public IceTvGuideFilters(Context context, final IceTvGuide tvGuide) {
		super(context);
		this.tvGuide = tvGuide;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.guidefilters);
            	
        setTitle(R.string.guide_filter_title);
        editSearch = (EditText) findViewById(R.id.guide_filter_search);
    	spinStation = (Spinner) findViewById(R.id.guide_filter_station);
    	spinTime = (Spinner) findViewById(R.id.guide_filter_time);
    	spinGenre = (Spinner) findViewById(R.id.guide_filter_genre);
    	btnFilter = (Button) findViewById(R.id.guide_filter_filter_btn);
    	
    	IceStation	allStations = new IceStation();
    	allStations.AddDisplayName(getContext().getResources().getString(R.string.guide_filter_all_stations));
    	if (this.tvGuide.GetStationList() != null)
    	{
	    	currentStationList = this.tvGuide.GetStationList().GetList();
	    	
	    	TreeSet<IceStation>	sortMeSet = new TreeSet<IceStation>(new IceStationComparator());
	    	for (IceStation eachStation : currentStationList)
	    	{
	    		sortMeSet.add(eachStation);
	    	}
	    	currentStationList.clear();
	    	for (IceStation sortedStation : sortMeSet)
	    	{
	    		currentStationList.add(sortedStation);
	    	}
    	}
    	currentStationList.add(0, allStations);
    	
	    ArrayAdapter	stationAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, currentStationList);
	    stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinStation.setAdapter(stationAdapter);
	    
	    ArrayAdapter timeAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.timeranges, android.R.layout.simple_spinner_item);
	    timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinTime.setAdapter(timeAdapter);
	    
	    spinTime.setSelection(tvGuide.GetTimeRangeFilter().ordinal());

	    tempGenreList = (ArrayList<String>)tvGuide.GetCategories().clone();
	    tempGenreList.add(0, getContext().getResources().getString(R.string.guide_filter_genre_no_filter));
	    ArrayAdapter genreAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, tempGenreList);
	    genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinGenre.setAdapter(genreAdapter);
	    String genreCurrent = tvGuide.GetCategoryFilter();
	    if (genreCurrent.length() == 0)
	    {
	    	spinGenre.setSelection(0);
	    }
	    else
	    {
	    	int checkPosition;
	    	for (checkPosition = 1; checkPosition < tempGenreList.size(); checkPosition++)
	    	{
	    		if (tempGenreList.get(checkPosition).compareToIgnoreCase(genreCurrent) == 0)
	    		{
	    			spinGenre.setSelection(checkPosition);
	    			break;
	    		}
	    	}
	    }
	    if (tvGuide.IsByTimeMode())
	    {
	    	spinStation.setSelection(0);
	    }
	    else
	    {
		    int checkPosition;
		    for (checkPosition = 1; checkPosition < currentStationList.size(); checkPosition++)
		    {
		    	if (currentStationList.get(checkPosition).GetId() == tvGuide.GetShowStationGuide())
		    	{
		    		spinStation.setSelection(checkPosition);
		    		break;
		    	}
		    }
	    }
	    
    	editSearch.setText(this.tvGuide.GetFilterText());
    	
    	btnFilter.setOnClickListener(new View.OnClickListener()
    	{

			@Override
			public void onClick(View view)
			{
				tvGuide.SetFilter(editSearch.getText().toString());
				tvGuide.SetShowStationGuide(currentStationList.get(spinStation.getSelectedItemPosition()).GetId());

				IceTvGuide.TimeRange newValue = IceTvGuide.TimeRange.class.getEnumConstants()[spinTime.getSelectedItemPosition()];
				tvGuide.SetTimeRangeFilter(newValue);
				
				if (spinGenre.getSelectedItemPosition() == 0)
				{
					tvGuide.SetCategoryFilter("");
				}
				else
				{
					tvGuide.SetCategoryFilter(tempGenreList.get(spinGenre.getSelectedItemPosition()));
				}
				
				dismiss();
			}    		
    	});
	}
}
