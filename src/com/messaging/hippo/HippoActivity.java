package com.messaging.hippo;

import com.messaging.hippo.ui.MainActivity;
import com.messaging.hippo.utils.ParseConstant;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import android.app.Application;

public class HippoActivity extends Application {
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		
		// Enable Local Datastore.
		Parse.enableLocalDatastore(this);
		 
		
		 Parse.initialize(this, "E44eJYUKH1xLUrOt2Kb3Cv779AhmrbrRPPn5G4Io", "lZo86I1HCkaztvC3x4eKTN7sc63fXtHtzUGHVCm6");
		 
		 ParseInstallation.getCurrentInstallation().saveInBackground();
	        
		
	}
	
	public static void updateParseInstallation (ParseUser user)
	{
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(ParseConstant.KEY_USER_ID, user.getObjectId());
		installation.saveInBackground();
	}

}
