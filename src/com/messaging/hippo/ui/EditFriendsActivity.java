package com.messaging.hippo.ui;

import java.util.List;

import com.messaging.hippo.R;
import com.messaging.hippo.R.layout;
import com.messaging.hippo.R.string;
import com.messaging.hippo.utils.ParseConstant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
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

public class EditFriendsActivity extends ListActivity {
	
	public static final String TAG = EditFriendsActivity.class.getSimpleName();
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_edit_friends);
		setupActionBar();
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		mCurrentUser=ParseUser.getCurrentUser();
		
		
		mFriendsRelation=mCurrentUser.getRelation(ParseConstant.KEY_FRIENDS_RELATION);
			
		setProgressBarIndeterminateVisibility(true);
		 
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.orderByAscending(ParseConstant.KEY_USERNAME);
		query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				
				 setProgressBarIndeterminateVisibility(false);
				if(e == null)
				{
					//success
					mUsers = users;
					
					String[] usernames = new String[mUsers.size()-1];
					int i = 0,j=0,p=0;
					
					for(i=0;i < mUsers.size();i++)
					{
						if(mUsers.get(i).getObjectId().equals(mCurrentUser.getObjectId())==true)
						{
							p=i;
						}
						else
						{
							usernames[j]=mUsers.get(i).getString(ParseConstant.KEY_USERNAME);
							j++;
						}
						
					}
					mUsers.remove(p);
					
					/*String[] usernames = new String[mUsers.size()];
					int i = 0;
						
					for(ParseUser user : mUsers) 
					{
					
						usernames[i]=user.getString(ParseConstant.KEY_USERNAME);
						i++;
					}*/
								
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							EditFriendsActivity.this,android.R.layout.simple_list_item_checked,
							usernames);
					setListAdapter(adapter);
					
					addFriendsCheckmarks();

				}
				else
				{
						//failure
						 Log.e(TAG,e.getMessage());
						 AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId())
		{
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if(getListView().isItemChecked(position))
		{
			//add friends
			mFriendsRelation.add(mUsers.get(position));
			
		}
		else
		{
			//remove friends
			mFriendsRelation.remove(mUsers.get(position));
			
		}
		
		mCurrentUser.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
					if(e!=null)
					{
						Log.e(TAG, e.getMessage());
					}
				
				}
			});
		
	}
	
	private void addFriendsCheckmarks()
	{
		mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				
				if(e == null)
				{
					//list returned- look for a match
					for(int i=0;i<mUsers.size();i++)
					{
						ParseUser user=mUsers.get(i);
						int j=0;
						for(ParseUser friend : friends)
						{
							if(friend.getObjectId().equals(user.getObjectId())==true)
							{
								getListView().setItemChecked(i,true);
							}
							j++;
						}
					}
				}
				
				
			}
		});
	}
}
