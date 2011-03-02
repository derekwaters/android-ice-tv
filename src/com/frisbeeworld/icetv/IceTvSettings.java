package com.frisbeeworld.icetv;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IceTvSettings extends Activity {

	// Definitions
	public final static	int			CHECK_SETTINGS	= 1;
	public final static	int			SETTINGS_OK	= 1;
	public final static int			SETTINGS_REFRESH_GUIDE = 2;
	
	public final static String		ICETV_SETTINGS_LABEL = "IceTvSettings";
	public final static String		ICETV_SETTING_USERNAME = "Username";
	public final static String		ICETV_SETTING_PASSWORD = "Password";
	public final static String		ICETV_SETTING_CREDENTIALS_SET = "CredentialsSet";
	
	// UI Members
	private	EditText	mUsername;
	private EditText	mPassword;
	private Button		mConfirm;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
             
        mUsername = (EditText) findViewById(R.id.settings_username);
        mPassword = (EditText) findViewById(R.id.settings_password);
        mConfirm = (Button) findViewById(R.id.settings_confirm);
            	
    	SharedPreferences tsSettings = getSharedPreferences(ICETV_SETTINGS_LABEL, 0);
    	mUsername.setText(tsSettings.getString(ICETV_SETTING_USERNAME, ""));
    	mPassword.setText(tsSettings.getString(ICETV_SETTING_PASSWORD, ""));
    	    	
        mConfirm.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	SharedPreferences 			tsSettings = getSharedPreferences(ICETV_SETTINGS_LABEL, 0);
            	SharedPreferences.Editor	editor = tsSettings.edit();
            	boolean						anyChanges = false;
            	String						check;
            	
            	check = mUsername.getText().toString();
            	if (check.compareTo(tsSettings.getString(ICETV_SETTING_USERNAME, "")) != 0)
            	{
            		editor.putString(ICETV_SETTING_USERNAME, check);
            		anyChanges = true;
            	}
            	check = mPassword.getText().toString();
            	if (check.compareTo(tsSettings.getString(ICETV_SETTING_PASSWORD, "")) != 0)
            	{
            		editor.putString(ICETV_SETTING_PASSWORD, check);
            		anyChanges = true;
            	}
            	
            	editor.putBoolean(ICETV_SETTING_CREDENTIALS_SET, true);
            	editor.commit();
            	
            	setResult(anyChanges ? SETTINGS_REFRESH_GUIDE : SETTINGS_OK);
            	
            	finish();
            }   
        });
    }
    
    public static boolean UsernameAndPasswordSet(Context ctx)
    {
    	SharedPreferences tsSettings = ctx.getSharedPreferences(ICETV_SETTINGS_LABEL, 0);
    	return tsSettings.getBoolean(ICETV_SETTING_CREDENTIALS_SET, false);
    }
    
}
