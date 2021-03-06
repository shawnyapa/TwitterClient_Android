package com.codepath.apps.basictwitter.fragments;

import java.io.File;
import java.util.ArrayList;
import org.json.JSONArray;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.codepath.apps.basictwitter.listeners.EndlessScrollListener;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TweetsListFragment extends Fragment implements OnRefreshListener {

	public TwitterClient client;
	public int count = 20;
	public long maxId;
	public long sinceId;
	public ArrayList<Tweet> tweets;
	public ArrayAdapter<Tweet> adapterTweets;
	public ListView lvTweets;
    private PullToRefreshLayout mPullToRefreshLayout;
    public enum TweetQueryType {
		FIRST_LOAD, OLDER_TWEETS, NEWER_TWEETS
		}
	public TweetQueryType newTweetType = TweetQueryType.FIRST_LOAD;

	public String user;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

    public String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tweets = new ArrayList<Tweet>();
		adapterTweets = new TweetArrayAdapter(getActivity(), tweets);
		client = TwitterApplication.getRestClient();

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
		lvTweets = (ListView) v.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(adapterTweets);
        lvTweets.setVisibility(View.VISIBLE);
		
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
        	@Override
	    	public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
        		if(isNetworkAvailable()) {
        		newTweetType = TweetQueryType.OLDER_TWEETS;
        		addTweetstoTimeline(count, maxId, 0);
        		} else { networkUnavailableToast(); }
	    	}
                });

		return v;
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup viewGroup = (ViewGroup) view;

        // As we're using a ListFragment we create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
                .insertLayoutInto(viewGroup)
                // Here we mark the ListView as pullable
                .theseChildrenArePullable(R.id.lvTweets)
                .listener(this)
                .setup(mPullToRefreshLayout);

    }

    @Override
    public void onRefreshStarted(View view) {
        // Hide the list
        lvTweets.setVisibility(View.INVISIBLE);
        refreshTimeline();
    }
	
	public void addTweetstoTimeline(int count, long maxId, long sinceId) {
		client.getTimeline(user, type, count, maxId, sinceId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {		
				ArrayList<Tweet> newTweets = Tweet.fromJSONArray(json);
				if (newTweets.size() > 0) {				
									
					if (newTweetType == TweetQueryType.NEWER_TWEETS) {
						tweets.addAll(0, newTweets);
					} else {
						tweets.addAll(newTweets);
					}
					
					// clear ArrayList
					// Pull Data from Active Android
					//clearAndReloadTweetsfromActiveAndroid();
					adapterTweets.notifyDataSetChanged();
					checkTweetTypeAndSetSinceIdAndMaxId();
				}
				stopRefreshing();
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("Debug", e.toString());
				Log.d("Debug", s.toString());
				stopRefreshing();
			}
		});
		
	}
	
		
    public void clearAndReloadTweetsfromActiveAndroid() {
    	ArrayList <Tweet> activeAndroidTweets = new ArrayList<Tweet>();
    	if (type.equals("home")) {
    		activeAndroidTweets =  (ArrayList) Tweet.getAllTweets();
    	}
    	if (type.equals("mentions")) {
    		activeAndroidTweets =  (ArrayList) Tweet.getAllMentions();
    	}
    	if (type.equals("user")) {
    		activeAndroidTweets =  (ArrayList) Tweet.getAllUserTweets();
    	}
		tweets.clear();
		tweets.addAll(activeAndroidTweets);
		adapterTweets.notifyDataSetChanged();
	}
	
	public void setmaxId(ArrayList<Tweet> tweets) {
		Tweet lastTweet = tweets.get(tweets.size() - 1);
		maxId = (lastTweet.getUid())-1;
	}
	
	public void setsinceId(ArrayList<Tweet> tweets) {
		Tweet firstTweet = tweets.get(0);
		sinceId = firstTweet.getUid();
	}
	
	public void checkTweetTypeAndSetSinceIdAndMaxId() {
		
		if(newTweetType == TweetQueryType.NEWER_TWEETS) {
			setsinceId(tweets);
		}
		if(newTweetType == TweetQueryType.OLDER_TWEETS) {
			setmaxId(tweets);
		}
		if(newTweetType == TweetQueryType.FIRST_LOAD) {
			setsinceId(tweets);
			setmaxId(tweets);
		}
		
	}
	
	public void refreshTimeline() {
		if(isNetworkAvailable()) {
			newTweetType= TweetQueryType.NEWER_TWEETS;
			addTweetstoTimeline(count, 0, sinceId);
		} else { networkUnavailableToast(); }
	}
	
	private void stopRefreshing() {
        lvTweets.setVisibility(View.VISIBLE);
        mPullToRefreshLayout.setRefreshComplete();
	}
	public boolean doesDatabaseExist() {
    	Context context = getActivity().getApplicationContext();
    	String dbName = "RestClient.db";
	    File dbFile = context.getDatabasePath(dbName);
	    return dbFile.exists();
	}
	
	public Boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public void networkUnavailableToast() {
		Toast.makeText(getActivity(), "Network is Unavailable", Toast.LENGTH_LONG).show(); 
	}

}
