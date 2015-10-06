package com.messaging.hippo.ui;

import java.util.List;

import com.messaging.hippo.R;
import com.messaging.hippo.R.layout;
import com.messaging.hippo.adapter.MessageAdapter;
import com.messaging.hippo.utils.NetworkHelper;
import com.messaging.hippo.utils.ParseConstant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.ListFragment;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class InboxFragment extends ListFragment {
	 
	protected List<ParseObject> mMessages;
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	
	@Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
       
       
         mSwipeRefreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
         mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
         mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
         mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.RED);
         
         
         
         return rootView;
     }
	
	
	@Override
    public void onResume()
    {
    	super.onResume();
    	
    	getActivity().setProgressBarIndeterminateVisibility(true);
    	
    	retriveMessages();
    }


	private void retriveMessages() {
		
		if(mSwipeRefreshLayout.isRefreshing())
		{
			mSwipeRefreshLayout.setRefreshing(false);
		}
		
		if(NetworkHelper.isNetworkAvailable(getActivity())==true)
		{
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstant.CLASS_MESSAGES);
		    	query.whereEqualTo(ParseConstant.KEY_RECIPIENT_IDS,ParseUser.getCurrentUser().getObjectId());
		    	query.addDescendingOrder(ParseConstant.KEY_CREATED_AT);
		    	query.findInBackground(new FindCallback<ParseObject>() {
					
					@Override
					public void done(List<ParseObject> messages, ParseException e) {
						getActivity().setProgressBarIndeterminateVisibility(false);
						
						if(e == null)
						{
							//we have found the Messages!
							mMessages = messages;
							String[] usernames = new String[mMessages.size()];
							int i = 0;
								
							for(ParseObject message : mMessages) 
							{
							
								usernames[i]=message.getString(ParseConstant.KEY_SENDER_NAME);
								i++;
							}
							
							if(getListView().getAdapter() == null)
							{
								MessageAdapter adapter = new MessageAdapter(getListView().getContext(),mMessages);			
								setListAdapter(adapter);
							}
							else
							{
								//refill the adapter
								((MessageAdapter)getListView().getAdapter()).refill(mMessages);
							}
							
						}
						
					}
				});
		}
		else
		{
			
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ParseObject messsage = mMessages.get(position);
		String messageType = messsage.getString(ParseConstant.KEY_FILE_TYPE);
		ParseFile file = messsage.getParseFile(ParseConstant.KEY_FILE);
		Uri fileUri = Uri.parse(file.getUrl());
		
		if(messageType.equals(ParseConstant.TYPE_IMAGE))
		{
			//image
			Intent intent = new Intent(getActivity(),ViewImageActivity.class);
			intent.setData(fileUri);
			startActivity(intent);
		}
		else
		{
			//video
			Intent intent = new Intent(Intent.ACTION_VIEW ,fileUri);
			intent.setDataAndType(fileUri, "video/*");
			startActivity(intent);
		}
		
	}
	
	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			
			retriveMessages();
			
		}
	};
	
	
	
	


}
