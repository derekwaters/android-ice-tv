package com.frisbeeworld.icetv;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class IceTvSeriesRecording extends Activity
{	
	public final static String	SeriesTitle = "SeriesTitle";
	public final static String	BundleDevice = "Device";
	public final static String	BundleNetwork = "Network";
	public final static String	BundleTime = "Time";
	public final static String	BundleRecord = "Record";
	public final static String	BundleQuality = "Quality";
	public final static String	BundleLimit = "Limit";
	
	private TextView	textTitle;
	private Spinner		spinDevice;
	private Spinner		spinNetwork;
	private Spinner		spinTime;
	private Spinner		spinRecord;
	private Spinner		spinQuality;
	private Spinner		spinLimit;
	private Button		btnRecord;
	private Button		btnCancel;
	
	private class SpinnerItem
	{
		public int		id;
		public String	label;
		
		public SpinnerItem(int id, String label)
		{
			this.id = id;
			this.label = label;
		}
		@Override
		public String toString()
		{
			return label;
		}
	}
	
	private ArrayList<SpinnerItem>	listDevices;
	private ArrayList<SpinnerItem>	listNetworks;
	private ArrayList<SpinnerItem>	listTimes;
	private ArrayList<SpinnerItem>	listRecords;
	private ArrayList<SpinnerItem>	listQualities;
	private ArrayList<SpinnerItem>	listLimits;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seriesrecording);
    
        textTitle = (TextView) findViewById(R.id.series_record_title);
        spinDevice = (Spinner) findViewById(R.id.series_record_device);
        spinNetwork = (Spinner) findViewById(R.id.series_record_network);
        spinTime = (Spinner) findViewById(R.id.series_record_time);
        spinRecord = (Spinner) findViewById(R.id.series_record_record);
        spinQuality = (Spinner) findViewById(R.id.series_record_quality);
        spinLimit = (Spinner) findViewById(R.id.series_record_limit);
        btnRecord = (Button) findViewById(R.id.series_record_record_btn);
        btnCancel = (Button) findViewById(R.id.series_record_cancel_btn);
             
        // textTitle.setText(savedInstanceState.getString(IceTvSeriesRecording.SeriesTitle));
        
        listDevices = new ArrayList<SpinnerItem>();
    	listDevices.add(new SpinnerItem(58156, "Beyonwiz"));
    	
    	listNetworks = new ArrayList<SpinnerItem>();
    	listNetworks.add(new SpinnerItem(-1, getResources().getString(R.string.series_record_network_any)));
    	listNetworks.add(new SpinnerItem(2, getResources().getString(R.string.series_record_network_abc)));
    	listNetworks.add(new SpinnerItem(6, getResources().getString(R.string.series_record_network_nine)));
    	listNetworks.add(new SpinnerItem(8, getResources().getString(R.string.series_record_network_sbs)));
    	listNetworks.add(new SpinnerItem(9, getResources().getString(R.string.series_record_network_seven)));
    	listNetworks.add(new SpinnerItem(12, getResources().getString(R.string.series_record_network_ten)));
    	
    	listTimes = new ArrayList<SpinnerItem>();
    	listTimes.add(new SpinnerItem(0, getResources().getString(R.string.series_record_time_any)));
    	listTimes.add(new SpinnerItem(1, getResources().getString(R.string.series_record_time_this)));
    	
    	listRecords = new ArrayList<SpinnerItem>();
    	listRecords.add(new SpinnerItem(1, getResources().getString(R.string.series_record_record_first)));
    	listRecords.add(new SpinnerItem(0, getResources().getString(R.string.series_record_record_all)));
    	
    	listQualities = new ArrayList<SpinnerItem>();
    	listQualities.add(new SpinnerItem(1, getResources().getString(R.string.series_record_quality_pref_high)));
    	listQualities.add(new SpinnerItem(3, getResources().getString(R.string.series_record_quality_only_high)));
    	listQualities.add(new SpinnerItem(2, getResources().getString(R.string.series_record_quality_pref_standard)));
    	listQualities.add(new SpinnerItem(0, getResources().getString(R.string.series_record_quality_only_standard)));
    	
    	listLimits = new ArrayList<SpinnerItem>();
    	listLimits.add(new SpinnerItem(1, getResources().getString(R.string.series_record_limit_1)));
    	listLimits.add(new SpinnerItem(2, getResources().getString(R.string.series_record_limit_2)));
    	listLimits.add(new SpinnerItem(3, getResources().getString(R.string.series_record_limit_3)));
    	listLimits.add(new SpinnerItem(4, getResources().getString(R.string.series_record_limit_4)));
    	listLimits.add(new SpinnerItem(5, getResources().getString(R.string.series_record_limit_5)));
    	listLimits.add(new SpinnerItem(0, getResources().getString(R.string.series_record_limit_all)));
        
    	
        ArrayAdapter	deviceAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listDevices);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDevice.setAdapter(deviceAdapter);
        
        ArrayAdapter	networkAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listNetworks);
        networkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNetwork.setAdapter(networkAdapter);
        
        ArrayAdapter	timeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listTimes);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTime.setAdapter(timeAdapter);

        ArrayAdapter	recordAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listRecords);
        recordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRecord.setAdapter(recordAdapter);

        ArrayAdapter	qualityAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listQualities);
        qualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinQuality.setAdapter(qualityAdapter);

        ArrayAdapter	limitAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listLimits);
        limitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLimit.setAdapter(limitAdapter);

        btnRecord.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{		
            	Bundle bundle = new Bundle();
                
            	bundle.putInt(BundleDevice, listDevices.get(spinDevice.getSelectedItemPosition()).id);
            	bundle.putInt(BundleNetwork, listNetworks.get(spinNetwork.getSelectedItemPosition()).id);
            	bundle.putInt(BundleTime, listTimes.get(spinTime.getSelectedItemPosition()).id);
            	bundle.putInt(BundleRecord, listRecords.get(spinRecord.getSelectedItemPosition()).id);
            	bundle.putInt(BundleQuality, listQualities.get(spinQuality.getSelectedItemPosition()).id);
            	bundle.putInt(BundleLimit, listLimits.get(spinLimit.getSelectedItemPosition()).id);
            	
             	Intent resultIntent = new Intent();
            	resultIntent.putExtras(bundle);
            	setResult(RESULT_OK, resultIntent);
            	finish();

			}
        });
        
        btnCancel.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		setResult(RESULT_CANCELED);
        		finish();
        	}
        });
    }
}