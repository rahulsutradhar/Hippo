package com.messaging.hippo.ui;

import com.messaging.hippo.HippoActivity;
import com.messaging.hippo.R;
import com.messaging.hippo.R.id;
import com.messaging.hippo.R.layout;
import com.messaging.hippo.R.string;
import com.messaging.hippo.utils.NetworkHelper;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity{
	
	
	protected EditText mPhone;
	protected EditText mPassword;
	protected Button mLogin;
	protected TextView mSignup;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	        setContentView(R.layout.activity_login);
	        
	        ActionBar actionBar =getActionBar();
	        actionBar.hide();
	        
	        mPhone=(EditText) findViewById(R.id.loginphoneField);
	        mPassword=(EditText) findViewById(R.id.loginPasswordField);
	        
	        mLogin=(Button)findViewById(R.id.loginButton);
	        mLogin.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {

					String phone=mPhone.getText().toString();
	                String password=mPassword.getText().toString();

	                phone.trim();
	                password.trim();
	                
	                if(phone.isEmpty() || password.isEmpty())
	                {
	                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
	                        builder.setMessage(R.string.login_error_message);
	                        builder.setTitle(R.string.signup_error_title);
	                        builder.setPositiveButton(android.R.string.ok, null);
	                        AlertDialog dialog = builder.create();
	                        dialog.show();
	                }
	                else
	                {
	                	
	                	if(NetworkHelper.isNetworkAvailable(getApplicationContext()) == true)
	                	{
		                		setProgressBarIndeterminateVisibility(true);
			                	ParseUser.logInInBackground(phone, password, new LogInCallback() {
			                        
									@Override
									public void done(ParseUser user, ParseException e) {
										
										setProgressBarIndeterminateVisibility(false);
										  if (e == null)
				                            {
											  
											  HippoActivity.updateParseInstallation(user);
											    //succcess
				                                Intent nextScreen = new Intent(LoginActivity.this, MainActivity.class);
				                                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				                                nextScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				                                startActivity(nextScreen);
				                            }
				                            else
				                            {
				                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
				                                builder.setMessage(e.getMessage());
				                                builder.setTitle(R.string.signup_error_title);
				                                builder.setPositiveButton(android.R.string.ok, null);
				                                AlertDialog dialog = builder.create();
				                                dialog.show();
				                            }
									}
			                    });
		                	
	                	}
	                	else
	                	{
	                		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
	                        builder.setMessage(R.string.error_internet_connect_message);
	                        builder.setTitle(R.string.app_name);
	                        builder.setCancelable(true);
	                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	                			
	                			@Override
	                			public void onClick(DialogInterface dialog, int which) {
	                				Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
	                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                                startActivity(dialogIntent);
	                			}
	                		});
	                        AlertDialog dialog = builder.create();
	                        
	                        dialog.show();
	                	}
	                
	                }

					
					
				}
			});
	        
	        
	        
	        
	        
	        mSignup=(TextView) findViewById(R.id.signupTextView);
	        mSignup.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					

			        Intent nextScreen = new Intent(LoginActivity.this, SignUpActivity.class);
			         startActivity(nextScreen);
					
				}
			});
	        
	 }

}
