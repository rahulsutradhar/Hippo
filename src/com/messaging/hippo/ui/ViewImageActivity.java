package com.messaging.hippo.ui;

import com.messaging.hippo.R;
import com.messaging.hippo.R.id;
import com.messaging.hippo.R.layout;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class ViewImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);
		
		ImageView imageView = (ImageView) findViewById(R.id.imageView);
		Uri imageUri = getIntent().getData();
		
		Picasso.with(this).load(imageUri).into(imageView);
		
		setupActionBar();
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
}
