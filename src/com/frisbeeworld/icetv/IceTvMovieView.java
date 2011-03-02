package com.frisbeeworld.icetv;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class IceTvMovieView extends Activity
{
	private static final int	ALERT_DIALOG_ID	= 12;
	
	private ListView			listMovies;
	private TextView			textInfo;
	
	private IceTvGuide			tvGuide;
	private ArrayList<IceMovie>	movieList;
	private IceMovie			contextMovie;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.icemovieview);
        
        tvGuide = new IceTvGuide(getBaseContext());
        
        listMovies = (ListView) findViewById(R.id.movie_view_list);
        textInfo = (TextView) findViewById(R.id.movie_view_info);

        movieList = tvGuide.GetMovieList();
        if (movieList == null)
        {
        	RefreshMovieList();
        }
        else
        {
        	RefreshListAdapter();
        }
        
        registerForContextMenu(listMovies);

    	listMovies.setOnItemClickListener(new OnItemClickListener()
    	{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long id)
			{
				if (id >= 0)
				{
					IceTvMovieListItem	movieItem = (IceTvMovieListItem)view;
					
					ShowDetails(movieItem.GetMovie());
				}
			}
    	});
    	
    	listMovies.setOnItemLongClickListener(new OnItemLongClickListener()
    	{
    		@Override
    		public boolean onItemLongClick(AdapterView<?> adapterView, View view, int arg2, long id)
    		{
    			if (id >= 0)
    			{
    				IceTvMovieListItem movieItem = (IceTvMovieListItem)view;
    				contextMovie = movieItem.GetMovie();
					openContextMenu(listMovies);
					return true; 
    			}
    			return false;
    		}
    	});
    	
    }
    
    private void ViewOnImdb(IceMovie forMovie)
    {
    	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(forMovie.GetUrl())));
    }

    private void ShowDetails(IceMovie forMovie)
    {
    	Bundle bundle = new Bundle();
    	Intent i = new Intent(IceTvMovieView.this, IceTvProgramView.class);
    	forMovie.GetProgram().SaveToBundle(bundle);
    	i.putExtras(bundle);
    	startActivity(i);	
    }
    
    private void RefreshMovieList()
    {
    	this.progressDialog = new ProgressDialog(this);
    	this.progressDialog.setTitle(getResources().getString(R.string.app_name));
    	this.progressDialog.setMessage(getResources().getString(R.string.retrieving_movie_information));
    	this.progressDialog.show();

    	new Thread()
    	{
    		public void run()
    		{
    			try
    			{
	    			tvGuide.RefreshMovieList();
    			}
    			catch(IceTvException e)
    			{
    				e.printStackTrace();
    			}
	    		refreshHandler.sendEmptyMessage(0);
    		}
    	}.start();

    }
    
    private void RefreshListAdapter()
    {
    	listMovies.setAdapter(new IceTvMovieListAdapter(this, tvGuide));
    }
    
	private ProgressDialog	progressDialog;
	private final Handler 	refreshHandler = new Handler()
	{
		public void handleMessage(final Message msg)
		{
			progressDialog.dismiss();
			RefreshListAdapter();
		}
	};
	
	
	
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.movie_list_context_show_details:
			ShowDetails(contextMovie);
			return true;
		case R.id.movie_list_context_view_imdb:
			ViewOnImdb(contextMovie);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
}