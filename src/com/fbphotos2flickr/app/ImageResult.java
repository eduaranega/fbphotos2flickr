package com.fbphotos2flickr.app;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ImageResult implements Serializable {
	private static final long serialVersionUID = 2204361367302669814L;
	private String fullUrl;
	private String thumbUrl;
	
	private String TAG = "ImageResult";
	
	public ImageResult(JSONObject json) {
		try {
			//this.fullUrl = json.getJSONObject("photos").getJSONArray("data").getJSONObject(0).getString("source");
			this.fullUrl = json.getString("source");
			Log.d(TAG, fullUrl);
			this.thumbUrl = json.getString("picture");
			Log.d(TAG, thumbUrl);

		} catch (JSONException e) {
			this.fullUrl = null;
			this.thumbUrl = null;
		}
	}
	
	public String getFullUrl() {
		return fullUrl;
	}
	
	public String getThumbUrl() {
		return thumbUrl;
	}

	public String toString() {
		return this.thumbUrl;
	}


	public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
		ArrayList<ImageResult> results = new ArrayList<ImageResult>();
		for (int x = 0; x < array.length(); x++) {
			try {
				results.add(new ImageResult(array.getJSONObject(x)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
}
