package com.messaging.hippo.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.messaging.hippo.R;
import com.messaging.hippo.R.array;
import com.messaging.hippo.R.id;
import com.messaging.hippo.R.layout;
import com.messaging.hippo.R.menu;
import com.messaging.hippo.R.string;
import com.messaging.hippo.adapter.SectionsPagerAdapter;
import com.messaging.hippo.utils.NetworkHelper;
import com.messaging.hippo.utils.ParseConstant;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements ActionBar.TabListener {

	public static final String TAG = MainActivity.class.getSimpleName();
	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int TAKE_VIDEO_REQUEST = 1;
	public static final int PICK_PHOTO_REQUEST = 2;
	public static final int PICK_VIDEO_REQUEST = 3;
	
	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final int MEDIA_TYPE_VIDEO = 5;
	
	public static final int FILE_SIZE_LIMIT = 1024*1024*10; // 10 MB
	
	protected Uri mMediaUri;
	
	protected DialogInterface.OnClickListener mDialogListener = new 
			DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			switch(which)
			{
			case 0:
				//take a picture
					takePhotoUsingCamera();
				
				break;
			case 1:
				//take a video
				takeVideoUsingCamera();
				break;
			case 2:
				//choose a picture
				choosePhotoUsingGallery();
				
				break;
			case 3:
				//choose a video
				chooseVideoUsingGallery();
				break;
			
			}
			
			
		}
		
	    public void takePhotoUsingCamera()
	    {
	    	Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mMediaUri=getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
			if(mMediaUri == null)
			{
				Toast.makeText(getApplicationContext(),R.string.error_external_storage, Toast.LENGTH_LONG).show();
			}
			else
			{
				takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
				startActivityForResult(takePhotoIntent,TAKE_PHOTO_REQUEST );
				
			}
	    }
	    
	    public void takeVideoUsingCamera()
	    {
	    	Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			mMediaUri=getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
			if(mMediaUri == null)
			{
				Toast.makeText(getApplicationContext(),R.string.error_external_storage, Toast.LENGTH_LONG).show();
			}
			else
			{
				videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
				videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
				videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
				startActivityForResult(videoIntent,TAKE_VIDEO_REQUEST );
				
			}
	    }
	    
	    public void choosePhotoUsingGallery()
	    {
	    	Intent choosePhotoIntent=new Intent(Intent.ACTION_GET_CONTENT);
	    	choosePhotoIntent.setType("image/*");
	    	startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST); 
	    }
	    
	    public void chooseVideoUsingGallery()
	    {
	    	Intent chooseVideoIntent=new Intent(Intent.ACTION_GET_CONTENT);
	    	chooseVideoIntent.setType("video/*");
	    	Toast.makeText(MainActivity.this,R.string.video_file_size_warning, Toast.LENGTH_LONG).show();
	    	startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
	    }

	    protected Uri getOutputMediaFileUri(int mediaType) {
			// To be safe, you should check that the SDcardis mounted
	    	//using Environment.getExternalStorageState() before doing this.
	    	
	    	if(isExternalStorageAvailable()==true)
	    	{
	    		//get the Uri
	    		//1. Get the external storage directory
	    		String appName=MainActivity.this.getString(R.string.app_name);
	    		File mediaStorageDir =  new File(
	    				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appName);
	    		
	    		
	    		//2. Create our subdirectory
	    		if(!mediaStorageDir.exists())
	    		{
	    			if(!mediaStorageDir.mkdirs())
	    			{
	    				Log.e(TAG,"Fail to create Directory");
	    				return null;
	    			}
	    		}
	    		
	    		//3. Create a FileName
	    		//4. Create the File
	    		File mediaFile;
	    		Date now=new Date();
	    		String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(now);
	    		
	    		String path=mediaStorageDir.getPath()+File.separator;
	    		if(mediaType == MEDIA_TYPE_IMAGE)
	    		{
	    			mediaFile=new File(path + "IMG_" + timestamp + ".jpg");
	    		}
	    		else if (mediaType == MEDIA_TYPE_VIDEO)
	    		{
	    			mediaFile=new File(path + "VID_" + timestamp + ".mp4");
	    		}
	    		else
	    		{
	    			return null;
	    		}
	    		
	    		Log.d("File : ", Uri.fromFile(mediaFile).toString());
	    		//5. Return the file's Uri
	    		return Uri.fromFile(mediaFile);
	    	}
	    	else
	    	{
	    		return null;
	    	} 
		}
	    private boolean isExternalStorageAvailable()
	    {
	    	String state=Environment.getExternalStorageState();
	    	
	    	if(state.equals(Environment.MEDIA_MOUNTED))
	    		return true;
	    	else
	    		return false;
	    }
		
		
	};
	
	
	/**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser == null)
        {
            //Not Loged in
            navigateToLogin();
        }
        else
        {
            //Loged In
            Log.i(TAG, currentUser.getUsername());
        }
        
        if(!NetworkHelper.isNetworkAvailable(getApplicationContext()))
        {
        	Toast.makeText(getApplicationContext(), "Please Check your Data Connection.", Toast.LENGTH_LONG).show();
        }
        

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(mSectionsPagerAdapter.getIcon(i))
                            .setTabListener(this));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	
    		if(resultCode == RESULT_OK)
        	{
    			
    			if(requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST)
    			{
    				if(data == null)
    				{
    					Toast.makeText(this,getString(R.string.general_error), Toast.LENGTH_LONG).show();
    				}
    				else
    				{
    					mMediaUri=data.getData();
    				}
    				
    				Log.i(TAG,"Media Uri : "+mMediaUri);
    				if(requestCode == PICK_VIDEO_REQUEST)
    				{
    					//make sure that file less than 10 MB
    					InputStream inputStream = null;
    					int fileSize=0;
    					
    					try
    					{
    						inputStream = getContentResolver().openInputStream(mMediaUri);
    						fileSize = inputStream.available();
    						
    					}catch(FileNotFoundException e)
    					{
    						Toast.makeText(this,getString(R.string.error_selecting_file), Toast.LENGTH_LONG).show();
    						return;
    					}catch (IOException e) {
    						Toast.makeText(this,getString(R.string.error_selecting_file), Toast.LENGTH_LONG).show();
    						return;
    					}
    					finally
    					{
    						try {
    							inputStream.close();
    						} catch (IOException e) {
    							e.printStackTrace();
    						}
    					}
    					
    					if(fileSize >= FILE_SIZE_LIMIT)
    					{
    						Toast.makeText(this,getString(R.string.error_file_size_too_large), Toast.LENGTH_LONG).show();
    						return;
    					}

    				}
    			}
    			else
    			{
    				//add Picture or Video to Gallery
            		Intent mediaScanIntent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            		mediaScanIntent.setData(mMediaUri);
            		this.sendBroadcast(mediaScanIntent);
    			}
    			
    			Intent recipientsIntent = new Intent(this,RecipientsActivity.class);
    			recipientsIntent.setData(mMediaUri);
    			
    			String fileType;
    			if(requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST)
    			{
    				fileType=ParseConstant.TYPE_IMAGE;
    			}
    			else
    			{
    				fileType=ParseConstant.TYPE_VIDEO;
    			}
    			
    			recipientsIntent.putExtra(ParseConstant.KEY_FILE_TYPE,fileType);
    			startActivity(recipientsIntent);
    			
        	}
        	else if(resultCode != RESULT_CANCELED)
        	{
        		Toast.makeText(MainActivity.this, R.string.general_error, Toast.LENGTH_LONG).show();
        	}
    	
    }
    
        

	@Override 
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
       
        
        //noinspection SimplifiableIfStatement
        switch(id)
        {
        case R.id.user_profile:
        	showUserProfileDialog();
        	break;
        case R.id.action_logout:
        	 ParseUser.logOut();
             navigateToLogin();
             break;
        case R.id.edit_friend_activity:
        	Intent nextScreen=new Intent(this,EditFriendsActivity.class);
        	startActivity(nextScreen);
        	break;
        case R.id.action_camera:
        	 AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setItems(R.array.camera_choices,mDialogListener);
             AlertDialog dialog = builder.create();
             dialog.show();
             break;
        	
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    
    

    public void navigateToLogin()
    {
    	Intent nextScreen = new Intent(this, LoginActivity.class);
        nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        nextScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(nextScreen);
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
    
    public void showUserProfileDialog()
    {
    	
    	
    	final Dialog dialog=new Dialog(MainActivity.this);
		dialog.setContentView(R.layout.profile_layout);
		dialog.setTitle(R.string.user_profile_lebel);
		dialog.setCancelable(true);
		
		TextView t1 =(TextView)dialog.findViewById(R.id.userName);
    	TextView t2 =(TextView)dialog.findViewById(R.id.userEmail);
    	TextView t3 =(TextView)dialog.findViewById(R.id.userPhone);
		
		ParseUser user= ParseUser.getCurrentUser();
		
		String s1=user.getString(ParseConstant.KEY_USERNAME);
		String s2=user.getEmail();
		String s3=user.getUsername();
		
		t1.setText(s1);
		t2.setText(s2);
		t3.setText(s3);
		
		dialog.show();
    	
    }

}
