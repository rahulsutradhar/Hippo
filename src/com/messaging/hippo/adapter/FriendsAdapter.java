package com.messaging.hippo.adapter;

import java.util.Date; 
import java.util.List;

import com.messaging.hippo.R;
import com.messaging.hippo.R.drawable;
import com.messaging.hippo.R.id;
import com.messaging.hippo.R.layout;
import com.messaging.hippo.utils.ParseConstant;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsAdapter extends ArrayAdapter<ParseUser>{
	
	protected Context mContext;
	protected List<ParseUser> mFriends;
	
	public FriendsAdapter(Context context,List<ParseUser> friend)
	{
		super(context,R.layout.friends_item,friend);
		mContext = context;
		mFriends = friend;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.friends_item, null);
			holder = new ViewHolder();
			holder.iconImageView = (ImageView) convertView.findViewById(R.id.contactIcon);
			holder.nameLebel = (TextView) convertView.findViewById(R.id.friendsLebel);
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		ParseUser friend = mFriends.get(position);
		
		
			holder.iconImageView.setImageResource(R.drawable.ic_action_action_account_circle);
			holder.nameLebel.setText(friend.getString(ParseConstant.KEY_USERNAME));
		return convertView;
	}

	private static class ViewHolder
	{
		ImageView iconImageView;
		TextView nameLebel;
		
	}
	
	public void refill(List<ParseUser> friends)
	{
		mFriends.clear();
		mFriends.addAll(friends);
		notifyDataSetChanged();
	}
}
