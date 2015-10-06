package com.messaging.hippo.ui;

import java.util.ArrayList;
import java.util.List;

import com.messaging.hippo.R;
import com.messaging.hippo.R.id;
import com.messaging.hippo.R.layout;
import com.messaging.hippo.R.menu;
import com.messaging.hippo.R.string;
import com.messaging.hippo.utils.FileHelper;
import com.messaging.hippo.utils.ParseConstant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RecipientsActivity extends ListActivity {

	protected static final String TAG = RecipientsActivity.class.getSimpleName();
	
	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;
	protected MenuItem mSendMenuItem;
	protected Uri mMediUri;
	protected String mFileType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_recipients);
		
		setupActionBar();
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mMediUri=getIntent().getData();
		mFileType=getIntent().getExtras().getString(ParseConstant.KEY_FILE_TYPE);
		
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		mCurrentUser=ParseUser.getCurrentUser();
		mFriendsRelation=mCurrentUser.getRelation(ParseConstant.KEY_FRIENDS_RELATION);
		
		setProgressBarIndeterminateVisibility(true);
		ParseQuery<ParseUser> query=mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstant.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				
				setProgressBarIndeterminateVisibility(false);
				if(e == null)
				{
					mFriends=friends;
					String[] usernames = new String[mFriends.size()];
					int i = 0;
						
					for(ParseUser user : mFriends) 
					{
						usernames[i]=user.getString(ParseConstant.KEY_USERNAME);
						i++;
					}
								
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getListView().getContext(),android.R.layout.simple_list_item_checked,
							usernames);
					setListAdapter(adapter);
				}
				else
				{
					Log.e(TAG,e.getMessage());
					 AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle(R.string.error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
				}
			}
		});
	}

	
	
	/*
	 * Set up {@link android.appActionBar}
	 */
	private void setupActionBar()
	{
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override 
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipients, menu);
        mSendMenuItem=menu.getItem(0);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId())
		{
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.action_send:
			
			ParseObject message = createMessage();
			
			if(message == null)
			{
				//error
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.error_selecting_file);
				builder.setTitle(R.string.error_selecting_file_title);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			else
			{	
				send(message);
				finish();
			}
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if(l.getCheckedItemCount() > 0)
		{
			mSendMenuItem.setVisible(true);
		}
		else
		{
			mSendMenuItem.setVisible(false);
		}
		
	}
	
	protected ParseObject createMessage()
	{
		ParseObject message = new ParseObject(ParseConstant.CLASS_MESSAGES);
			message.put(ParseConstant.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
			message.put(ParseConstant.KEY_SENDER_NAME,ParseUser.getCurrentUser().getString("name"));
			message.put(ParseConstant.KEY_RECIPIENT_IDS, getRecipientIds());
			message.put(ParseConstant.KEY_FILE_TYPE,mFileType);
			
			
			byte[] fileByte = FileHelper.getByteArrayFromFile(this,mMediUri);
			if(fileByte == null)
			{
				return null;
			}
			else
			{
				if(mFileType.equals(ParseConstant.TYPE_IMAGE))
				{
					fileByte = FileHelper.reduceImageForUpload(fileByte);
				}
				String fileName = FileHelper.getFileName(this,mMediUri,mFileType);
				ParseFile file = new ParseFile(fileName,fileByte);
				message.put(ParseConstant.KEY_FILE,file);
				
				return message;
				
			}
		
	}
	
	protected ArrayList<String> getRecipientIds()
	{
		ArrayList<String> recipientIds = new ArrayList<String>();
		
			for(int i=0;i<getListView().getCount();i++)
			{
				if(getListView().isItemChecked(i)==true)
				{
					recipientIds.add(mFriends.get(i).getObjectId());
				}
			}
		return recipientIds;
	}
	
	protected void send(ParseObject message)
	{
		message.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) 
			{
				if(e == null )
				{
					Toast.makeText(RecipientsActivity.this,getString(R.string.success_message),Toast.LENGTH_LONG).show();
					
					 sendPushNotifications();
				}
				else
				{
					//error
					AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
					builder.setMessage(R.string.error_sending_message);
					builder.setTitle(R.string.error_selecting_file_title);
					AlertDialog dialog = builder.create();
					dialog.show();
					
				}
			}
		});
	}
	
	protected void sendPushNotifications()
	{
		ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
		query.whereContainedIn(ParseConstant.KEY_USER_ID , getRecipientIds());
		
		//send push notifications
		ParsePush push = new ParsePush();
		push.setQuery(query);
		push.setMessage(getString(R.string.push_message, 
				ParseUser.getCurrentUser().getString(ParseConstant.KEY_USERNAME)));
		push.sendInBackground();
		
		
	}
}
