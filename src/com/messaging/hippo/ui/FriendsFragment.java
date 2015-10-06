package com.messaging.hippo.ui;

import java.util.List;

import com.messaging.hippo.R;
import com.messaging.hippo.R.layout;
import com.messaging.hippo.R.string;
import com.messaging.hippo.adapter.FriendsAdapter;
import com.messaging.hippo.adapter.MessageAdapter;
import com.messaging.hippo.utils.NetworkHelper;
import com.messaging.hippo.utils.ParseConstant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsFragment extends ListFragment {
	
	protected static final String TAG = FriendsFragment.class.getSimpleName();
	
	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;

	 
	@Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
         
         return rootView;
     }
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		mCurrentUser=ParseUser.getCurrentUser();
		mFriendsRelation=mCurrentUser.getRelation(ParseConstant.KEY_FRIENDS_RELATION);
		
		getActivity().setProgressBarIndeterminateVisibility(true);
		
			ParseQuery<ParseUser> query=mFriendsRelation.getQuery();
				query.addAscendingOrder(ParseConstant.KEY_USERNAME);
				query.findInBackground(new FindCallback<ParseUser>() {
		
					@Override
					public void done(List<ParseUser> friends, ParseException e) {
						
						getActivity().setProgressBarIndeterminateVisibility(false);
						if(e == null)
						{
							mFriends=friends;
							String[] usernames = new String[mFriends.size()];
							int i=0;
							for(ParseUser user : mFriends) 
							{
							
								usernames[i]=user.getString(ParseConstant.KEY_USERNAME);
								i++;
							}
										
							if(getListView().getAdapter() == null)
							{
								FriendsAdapter adapter = new FriendsAdapter(getListView().getContext(),mFriends);			
								setListAdapter(adapter);
							}
							else
							{
								//refill the adapter
								((FriendsAdapter )getListView().getAdapter()).refill(mFriends);
							}
						}
						else
						{
							Log.e(TAG,e.getMessage());
							 AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
		                    builder.setMessage(e.getMessage());
		                    builder.setTitle(R.string.error_title);
		                    builder.setPositiveButton(android.R.string.ok, null);
		                    AlertDialog dialog = builder.create();
		                    dialog.show();
						}
					}
				});

			}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		showUserProfileDialog(position);
		
	}
	
	 public void showUserProfileDialog(int position)
	    {
	    	
		 	ParseUser user= mFriends.get(position);
			
			String s1=user.getString(ParseConstant.KEY_USERNAME);
			String s2=user.getEmail();
			String s3=user.getUsername();
		 
		 
	    	final Dialog dialog=new Dialog(getListView().getContext());
	    	dialog.setContentView(R.layout.friends_profile);
			dialog.setCancelable(true);
			
			TextView t2 =(TextView)dialog.findViewById(R.id.friendEmail);
	    	TextView t3 =(TextView)dialog.findViewById(R.id.friendsPhone);
			
			
			dialog.setTitle(s1);
			
			t2.setText(s2);
			t3.setText(s3);
			
			dialog.show();
	    	
	    }
			
}


