package com.codepath.apps.basictwitter.activities;


import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.activities.CreateTweetActivity;
import com.codepath.apps.basictwitter.activities.ProfileActivity;
import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.basictwitter.listeners.FragmentTabListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.FragmentActivity;
import android.util.Log;



public class TimelineActivity extends FragmentActivity {

	public final int REQUEST_CODE_COMPOSE = 10;
    private final String FIRST_TAB_TAG = "first";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_timeline);
		setupTabs();
	}
	
	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		
		Tab tab1 = actionBar
			.newTab()
			.setText("Home")
			.setIcon(R.drawable.ic_home)
			//.setTag("HomeTimelineFragment")
			.setTabListener(
				new FragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this, FIRST_TAB_TAG,
						HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
			.newTab()
			.setText("Mentions")
			.setIcon(R.drawable.ic_mentions)
			.setTag("MentionsTimelineFragment")
			.setTabListener(
			    new FragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this, "second",
								MentionsTimelineFragment.class));
		actionBar.addTab(tab2);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }
	
	public void onProfileView(MenuItem mi) {
		Intent i = new Intent(this, ProfileActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.postTweet:
	            onCompose(item);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public void onCompose(MenuItem mi) {
		Intent i = new Intent(this, CreateTweetActivity.class);
		startActivityForResult(i, REQUEST_CODE_COMPOSE);
	}
	
	public void onUserProfile(View v) {
		User user = (User) v.getTag();
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("User", user);
		startActivity(i);	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // REQUEST_CODE is defined above
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
			Tweet newTweet = (Tweet) data.getSerializableExtra("newTweet");
			checkAndAddNewTweet(newTweet);
		}
	}
	
	public void checkAndAddNewTweet(Tweet newTweet) {
        HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment)getSupportFragmentManager().findFragmentByTag(FIRST_TAB_TAG);
        Log.d("debug", "Tweet Body:"+newTweet.getBody().toString());
        Log.d("debug", "TweetList Count:"+homeTimelineFragment.tweets.size());
		homeTimelineFragment.tweets.add(0, newTweet);
		// clear ArrayList
		// Pull Data from Active Android
		//clearAndReloadTweetsfromActiveAndroid();
		homeTimelineFragment.sinceId = newTweet.getUid();
        homeTimelineFragment.adapterTweets.notifyDataSetChanged();
	}

}
