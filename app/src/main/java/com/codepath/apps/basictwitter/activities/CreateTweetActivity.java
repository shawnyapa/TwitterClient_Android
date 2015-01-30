package com.codepath.apps.basictwitter.activities;

import org.json.JSONObject;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateTweetActivity extends Activity {
	public TwitterClient client;
	private EditText etTweet;
	private TextView etCharCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_tweet);
		etTweet = (EditText) findViewById(R.id.etTweet);
		etCharCount = (TextView) findViewById(R.id.tvCharCount);
		client = TwitterApplication.getRestClient();
        getActionBar().setTitle("Compose Tweet");
		etTweet.addTextChangedListener(new TextWatcher() {

	          public void afterTextChanged(Editable s) {
                  int charCount = s.length();
                  int charRemaining = 140-charCount;
                  etCharCount.setText(Integer.toString(charRemaining));
	        	  //etCharCount.setText(Integer.toString(s.length()));
	          }
	          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	          public void onTextChanged(CharSequence s, int start, int before, int count) {
    	  
	          }
	       });
	}

    public void postTweetFromMenu() {
        Tweet tweet = new Tweet();
        String tweetBody = etTweet.getText().toString();
        tweet.setBody(tweetBody);
        //Toast.makeText(this, "Body: " + tweetBody, Toast.LENGTH_SHORT).show();
        // Initiate Spinner

        client.postTweet(tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                //Log.d("debug", json.toString());
                //Remove Spinner
                createTweetandPosttoTimeline(json);
                //fromJSON(JSONObject jsonObject)
                //CreateTweetActivity.this.finish();
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("debug", e.toString());
                Toast.makeText(CreateTweetActivity.this, "Unable to Tweet your Status", Toast.LENGTH_SHORT).show();
                // Remove Spinner
            }
        });
    }
	
	private void createTweetandPosttoTimeline(JSONObject json) {
		Tweet newTweet = Tweet.fromJSON(json);
		Intent i = new Intent(CreateTweetActivity.this, TimelineActivity.class);
        i.putExtra("newTweet", newTweet);
        setResult(RESULT_OK, i);
        finish();	
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_tweet, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.submitTweet:
                postTweetFromMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
