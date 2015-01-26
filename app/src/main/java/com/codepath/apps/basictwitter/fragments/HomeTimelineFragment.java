package com.codepath.apps.basictwitter.fragments;

import android.os.Bundle;
import android.os.Debug;
//import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeTimelineFragment extends TweetsListFragment {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setType("home");
        if(isNetworkAvailable()) {
		addTweetstoTimeline(count, maxId, 0);
        } else { 
        	networkUnavailableToast();
        	// Check if this is the First Ever Application Launch and if the DB Exists
        	if (doesDatabaseExist()) {
        		clearAndReloadTweetsfromActiveAndroid();
        	}
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater,
    		ViewGroup container, Bundle savedInstanceState) {
    	return super.onCreateView(inflater, container, savedInstanceState);
        
    }


	

}
