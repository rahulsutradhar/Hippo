package com.messaging.hippo.ui;

import com.messaging.hippo.HippoActivity;
import com.messaging.hippo.R;
import com.messaging.hippo.R.id;
import com.messaging.hippo.R.layout;
import com.messaging.hippo.R.string;
import com.messaging.hippo.utils.NetworkHelper;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SignUpActivity extends Activity {
	
	
	protected TextView mName;
	protected TextView mEmail;
	protected TextView mPassword;
	protected TextView mPhone;
	protected Button mSignup,mCancel;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);
        

        ActionBar actionBar =getActionBar();
        actionBar.hide();
        
        mName=(TextView)findViewById(R.id.usernameField);
        mEmail=(TextView)findViewById(R.id.emailField);
        mPassword=(TextView)findViewById(R.id.passwordField);
        mPhone=(TextView)findViewById(R.id.phonenumberField);
        mSignup=(Button)findViewById(R.id.signUpButton);
        mCancel=(Button)findViewById(R.id.cancelButton);
        
        mCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
        
        mSignup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				String name=mName.getText().toString();
                String email=mEmail.getText().toString();
                String password=mPassword.getText().toString();
                String phone=mPhone.getText().toString();

                name.trim();
                email.trim();
                password.trim();
                phone.trim();


                    if(name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty())
                    {
                        AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
                            builder.setMessage(R.string.signup_error_message);
                            builder.setTitle(R.string.signup_error_title);
                            builder.setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog=builder.create();
                        dialog.show();

                    }
                     else
                    {
                    	 //Save in BackEnd
                    	 
                    	 if(NetworkHelper.isNetworkAvailable(getApplicationContext()) == true)
                    	 {
		                    	 setProgressBarIndeterminateVisibility(true);
		                    	 
		                    	 ParseUser user = new ParseUser();
		                         user.setUsername(phone); //phonenumberas UserName Unique ID
		                         user.setPassword(password);
		                         user.setEmail(email); //email id Unique ID
		                         // other fields can be set just like with ParseObject
		                         user.put("name",name);
		                     
		
		                         user.signUpInBackground(new SignUpCallback() {
		                        	 @Override
		                        	 public void done(com.parse.ParseException e) {
		
		                        		 setProgressBarIndeterminateVisibility(false);
			                             if (e == null) {
			                            	 //succcess
			                            	 
			                            	 HippoActivity.updateParseInstallation(ParseUser.getCurrentUser());
			                            	 
			                                 Intent nextScreen = new Intent(SignUpActivity.this, LoginActivity.class);
			                                 nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                                 nextScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			                                 startActivity(nextScreen);
			                             } 
			                             else 
			                             {
			                                 AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
                    		 	AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
        
	}


}
