package com.codepath.apps.mysimpletweets.network;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.io.IOException;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String SCREEN_NAME = "zhiyongtan1";	// TODO: Is there a way to get current user id?
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "dLn3eZDuoSBIGWvKpKHdKlqQ1";
	public static final String REST_CONSUMER_SECRET = "QyDn9f6C3CUCyJRJC86Y16yNQvkDCXlzpN5ssXUlLNcPtqJdfH";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// Gets us the home timeline
	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Specify the params
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if (maxId != Long.MAX_VALUE) params.put("max_id", maxId);
		// Execute the request
		getClient().get(apiUrl, params, handler);
	}

	// COMPOSE TWEET
	public void postTweet(AsyncHttpResponseHandler handler, String status) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
		getClient().post(apiUrl, params, handler);
    }

	public void getUsersShow(String screenName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Specify the params
		RequestParams params = new RequestParams();
		params.put("count", 25);
		// Execute the request
		getClient().get(apiUrl, params, handler);
	}

	public void getUserTimeline(long maxId, String screenName, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}

	public void getUserInfo(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);
	}

	public void getFriendsList(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("friends/list.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", SCREEN_NAME);
		getClient().get(apiUrl, params, handler);
	}

	public void getFollowersList(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("followers/list.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", SCREEN_NAME);
		getClient().get(apiUrl, params, handler);
	}


	public static boolean isOnline() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int     exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		} catch (IOException e)          { e.printStackTrace(); }
		catch (InterruptedException e) { e.printStackTrace(); }
		return false;
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}