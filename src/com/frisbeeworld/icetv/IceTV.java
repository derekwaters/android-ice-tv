package com.frisbeeworld.icetv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

public class IceTV extends TabActivity {
	
	private static final int DATE_DIALOG_ID = 0;
	private static final int FILTER_DIALOG_ID = 1;
	private static final int ALERT_DIALOG_ID = 2;
	
	public static final int REFRESH_COMPLETE_MSG = 0;
	public static final int DOWNLOAD_COMPLETE_MSG = 1;
	public static final int DOWNLOAD_PROGRESS_MSG = 2;

	private Button			btnDate;
	private Button			btnPrevious;
	private Button			btnNext;
	private TabHost			tabHost;
	private ListView		tvGuideList;
	
	private int				errorCause;
	private String			errorAdditionalInfo;
	
	private ProgressDialog	progressDialog;
	private final Handler 	handler = new Handler()
	{
		public void handleMessage(final Message msg)
		{
			if (msg != null)
			{
				switch (msg.arg1)
				{
				case REFRESH_COMPLETE_MSG:
					{
						Bundle getData = msg.peekData();
						if (getData != null)
						{
							errorCause = getData.getInt(IceTvException.Bundle_Cause);
							errorAdditionalInfo = getData.getString(IceTvException.Bundle_AdditionalInfo);
							showDialog(ALERT_DIALOG_ID);
						}
						progressDialog.dismiss();
						RefreshGuideList();
					}
					break;
				case DOWNLOAD_COMPLETE_MSG:
					{
						progressDialog.setMessage(getResources().getString(R.string.parsing_tv_guide));
					}
					break;
				case DOWNLOAD_PROGRESS_MSG:
					{
						// progressDialog.setMessage(getResources().getString(R.string.downloading_tv_guide) + " " + msg.arg2 + "%");
						progressDialog.setProgress(msg.arg2);
					}
					break;
				}
			}
		}
	};
	
	
	private IceTvGuide				tvGuide;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tabHost = getTabHost();

    	tabHost.addTab(tabHost.newTabSpec("tab_tv_guide").setIndicator("TV Guide", getResources().getDrawable(/*R.drawable.icon_tvguide*/android.R.drawable.ic_menu_gallery)).setContent(R.id.tab_tv_guide));
    	tabHost.addTab(tabHost.newTabSpec("tab_popular").setIndicator("TBD", getResources().getDrawable(/*R.drawable.icon_popular*/android.R.drawable.ic_menu_compass)).setContent(R.id.tab_popular));
    	tabHost.addTab(tabHost.newTabSpec("tab_upcoming").setIndicator("TBD", getResources().getDrawable(/*R.drawable.icon_remote*/android.R.drawable.ic_menu_camera)).setContent(R.id.tab_upcoming));
    	tabHost.addTab(tabHost.newTabSpec("tab_my_shows").setIndicator("TBD", getResources().getDrawable(/*R.drawable.icon_myguide*/android.R.drawable.ic_menu_directions)).setContent(R.id.tab_my_shows));
        
    	tabHost.setCurrentTab(0);
 
    	tvGuideList = (ListView) findViewById(R.id.list_tv_guide);
    	btnDate = (Button) findViewById(R.id.btn_tv_guide_date);
    	btnPrevious = (Button) findViewById(R.id.btn_tv_guide_prev);
    	btnNext = (Button) findViewById(R.id.btn_tv_guide_next);
    	
    	btnPrevious.setText("<");
    	btnNext.setText(">");
    	    	
    	tvGuideList.setDivider(null);
    	
    	TextView emptyText = new TextView(this);
    	emptyText.setText("No Program Information");
    	tvGuideList.setEmptyView(emptyText);
    	
    	errorCause = 0;
    	errorAdditionalInfo = new String();
    	
    	tvGuide = new IceTvGuide(this);
    	
    	tvGuideList.setOnItemClickListener(new OnItemClickListener()
    	{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long id)
			{
				if (id >= 0)
				{
					IceTvGuideListProgram	testView = (IceTvGuideListProgram)view;
		        	Bundle bundle = new Bundle();
		        	Intent i = new Intent(IceTV.this, IceTvProgramView.class);
		        	testView.GetProgram().SaveToBundle(bundle);
			    	i.putExtras(bundle);
			    	startActivity(i);
				}
			}
    	});
    	
    	RefreshDateButton();

    	btnDate.setOnClickListener(new OnClickListener()
    	{
    		@Override
    		public void onClick(View clicked)
    		{
    			showDialog(DATE_DIALOG_ID);
    		}
    	});
    	
    	btnNext.setOnClickListener(new OnClickListener()
    	{
    		@Override
    		public void onClick(View clicked)
    		{
    			Date newDate = new Date();
    			newDate.setTime(tvGuide.GetCurrentDate().getTime() + (24 * 60 * 60 * 1000));
    			tvGuide.SetCurrentDate(newDate);
    			RefreshDateButton();
    			RefreshTvGuide();
    		}
    	});

    	btnPrevious.setOnClickListener(new OnClickListener()
    	{
    		@Override
    		public void onClick(View clicked)
    		{
    			Date newDate = new Date();
    			newDate.setTime(tvGuide.GetCurrentDate().getTime() - (24 * 60 * 60 * 1000));
    			tvGuide.SetCurrentDate(newDate);
    			RefreshDateButton();
    			RefreshTvGuide();
    		}
    	});
    	
    	// If the username and password have not been set, open the settings page automatically.
    	//
    	tvGuide.ClearCachedGuides();

    	if (IceTvSettings.UsernameAndPasswordSet(this))
    	{
    		RefreshTvGuide();
    	}
    	else
    	{
    		OpenSettings();
    	}
    }

    private void RefreshDateButton()
    {
    	SimpleDateFormat	buttonFormat = new SimpleDateFormat("E, MMM dd");
    	btnDate.setText(buttonFormat.format(tvGuide.GetCurrentDate()));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id)
        {
        case DATE_DIALOG_ID:
        	{
        		Date currentDate = tvGuide.GetCurrentDate();
        		return new DatePickerDialog(this, mChangeDateListener, currentDate.getYear() + 1900, currentDate.getMonth(), currentDate.getDate());
        	}
        case FILTER_DIALOG_ID:
        	{
        		IceTvGuideFilters newdlg = new IceTvGuideFilters(this, tvGuide);
        		newdlg.setOnDismissListener(new OnDismissListener()
        		{
					@Override
					public void onDismiss(DialogInterface dialog)
					{
						tvGuide.GenerateGuideList();
						RefreshGuideList();
					}
        		});
        		return newdlg;
        	}
        case ALERT_DIALOG_ID:
        	{
	    		String message = new String();
	    		message = "An error has occured downloading the IceTV guide: \n" + errorAdditionalInfo;
	    		
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setMessage(message)
	    			   .setTitle(IceTvException.GetCauseTitle(errorCause))
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
    
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mChangeDateListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth)
                {
                	Date	newDate = new Date(year - 1900, monthOfYear, dayOfMonth);
                	tvGuide.SetCurrentDate(newDate);
                	RefreshDateButton();
                	RefreshTvGuide();
                }
            };

    public void RefreshTvGuide()
    {
    	this.progressDialog = new ProgressDialog(this);
    	this.progressDialog.setTitle(getResources().getString(R.string.app_name));
    	this.progressDialog.setMessage(getResources().getString(R.string.downloading_tv_guide));
    	this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	this.progressDialog.show();

    	// this.progressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), getResources().getString(R.string.downloading_tv_guide));

    	new Thread()
    	{
    		public void run()
    		{
    			Bundle	finishInfo = null;
    			
    			try
    			{
	    			tvGuide.RetrieveTvGuide(handler);
	    			tvGuide.GenerateGuideList();
	    			handler.sendEmptyMessage(0);
    			}
    			catch(IceTvException e)
    			{
    				finishInfo = new Bundle();
    				finishInfo.putString(IceTvException.Bundle_AdditionalInfo, e.GetAdditionalInfo());
    				finishInfo.putInt(IceTvException.Bundle_Cause, e.GetCause());
    			}
 				Message	newMessage = Message.obtain(handler);
				newMessage.arg1 = REFRESH_COMPLETE_MSG;
   			
    			newMessage.setData(finishInfo);
    			handler.sendMessage(newMessage);
    		}
    	}.start();
    }
    
    public void RefreshGuideList()
    {
    	if (tvGuide.IsByTimeMode())
    	{
    		tvGuideList.setAdapter(new IceTvGuideListAdapter(this, tvGuide));
    	}
    	else
    	{
    		tvGuideList.setAdapter(new IceTvGuideListByStationAdapter(this, tvGuide));
    	}
    }
    
    public void FilterList()
    {
    	/*
    	if (tvGuide.IsGuideLoaded())
    	{
    		tvGuide.SetFilter(editSearch.getText().toString());
    		RefreshGuideList();
    	}
    	*/
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater 	inflater = getMenuInflater();
    	inflater.inflate(R.menu.icetv_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
        case R.id.menu_settings:
            OpenSettings();
            return true;
        case R.id.menu_refresh:
        	tvGuide.SetGuideLoaded(false);
        	RefreshTvGuide();
        	return true;
        case R.id.menu_filters:
        	OpenFilters();
        	return true;
        case R.id.menu_movie_view:
        	OpenMovieView();
        	return true;
        }
        return false;
    }

    private void OpenSettings()
    {
    	startActivityForResult(new Intent(this, IceTvSettings.class), IceTvSettings.CHECK_SETTINGS);
    }

    private void OpenMovieView()
    {
    	startActivity(new Intent(this, IceTvMovieView.class));
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == IceTvSettings.CHECK_SETTINGS &&
			resultCode == IceTvSettings.SETTINGS_REFRESH_GUIDE)
		{
			RefreshTvGuide();
		}
	}
	
    private void OpenFilters()
    {
		showDialog(FILTER_DIALOG_ID);
    }   
}