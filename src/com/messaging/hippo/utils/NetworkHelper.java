package com.messaging.hippo.utils;

import com.messaging.hippo.R;
import com.messaging.hippo.ui.LoginActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {

	public static boolean isNetworkAvailable(Context context)
	{
		boolean isAvailable = false;
		
		ConnectivityManager manager = (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		
		if(networkInfo != null && networkInfo.isConnected())
		{
			isAvailable = true;
		}
		
		
		return isAvailable;
	}
	}
