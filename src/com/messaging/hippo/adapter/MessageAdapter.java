package com.messaging.hippo.adapter;

import java.util.Date; 
import java.util.List;

import com.messaging.hippo.R;
import com.messaging.hippo.R.drawable;
import com.messaging.hippo.R.id;
import com.messaging.hippo.R.layout;
import com.messaging.hippo.utils.ParseConstant;
import com.parse.ParseObject;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<ParseObject>{
	
	protected Context mContext;
	protected List<ParseObject> mMessages;
	
	public MessageAdapter(Context context,List<ParseObject> message)
	{
		super(context,R.layout.message_item,message);
		mContext = context;
		mMessages = message;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
			holder = new ViewHolder();
			holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
			holder.nameLebel = (TextView) convertView.findViewById(R.id.senderLebel);
			holder.timeLebel = (TextView) convertView.findViewById(R.id.timeLebel);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		ParseObject message = mMessages.get(position);
		
		Date createdAt = message.getCreatedAt();
		long now = new Date().getTime();
		String convertedDate = DateUtils.getRelativeTimeSpanString(createdAt.getTime(), 
				now, DateUtils.SECOND_IN_MILLIS).toString();
		
		
		holder.timeLebel.setText(convertedDate);
		if(message.get(ParseConstant.KEY_FILE_TYPE).equals(ParseConstant.TYPE_IMAGE))
		{
			holder.iconImageView.setImageResource(R.drawable.ic_action_ic_action_picture);
		}
		else
		{
			holder.iconImageView.setImageResource(R.drawable.ic_action_ic_action_play_over_video);
		}
			holder.nameLebel.setText(message.getString(ParseConstant.KEY_SENDER_NAME));
		return convertView;
	}

	private static class ViewHolder
	{
		ImageView iconImageView;
		TextView nameLebel;
		TextView timeLebel;
	}
	
	public void refill(List<ParseObject> messages)
	{
		mMessages.clear();
		mMessages.addAll(messages);
		notifyDataSetChanged();
	}
}
