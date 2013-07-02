package com.fbphotos2flickr.app;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
public class SelectionFragment extends Fragment {

	private static final String TAG = "SelectionFragment";
	
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	GridView gvResults;
	ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
	ImageResultArrayAdapter imageAdapter;
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);

	    View view = inflater.inflate(R.layout.selection, container, false);

	    // Find the user's profile picture custom view
	    profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
	    profilePictureView.setCropped(true);
	    // Find the user's name view
	    userNameView = (TextView) view.findViewById(R.id.selection_user_name);
	    // Grid
	    gvResults = (GridView) view.findViewById(R.id.gvResults);
		imageAdapter = new ImageResultArrayAdapter(view.getContext(), imageResults);
		gvResults.setAdapter(imageAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position, long rowId) {
				Intent i = new Intent(getActivity(), ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				i.putExtra("result", imageResult);
				i.putExtra("position", position);
				getActivity().startActivity(i);
			}
		});
		
		
		//imageSearch(view);
	    
	    // Check for an open session
	    Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	    	makeMeRequest(session);
	    }
	    
	    return view;
	}
		
	private void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
		
		Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                    // Set the id for the ProfilePictureView
	                    // view that in turn displays the profile picture.
	                    profilePictureView.setProfileId(user.getId());
	                    // Set the Textview's text to the user's name.
	                    userNameView.setText("Photos from " + user.getName());
	                    Log.d(TAG, response.toString());
	                    
	                    String url = "https://graph.facebook.com/" + user.getId() 
	                    		+ "?fields=id,name,albums.fields(photos.fields(source,picture))&access_token=" 
	                    		+ session.getAccessToken();
	                    Log.d(TAG, url);
	                  
	                    AsyncHttpClient client = new AsyncHttpClient();
	            	    client.get(url,  
	            					new JsonHttpResponseHandler() {
	            						@Override
	            						public void onSuccess(JSONObject response) {
	            							JSONArray imageJsonResults = null;
	            							try {
	            								Log.d(TAG, response.toString());
	            								imageJsonResults = response.getJSONObject("albums").getJSONArray("data").getJSONObject(1).getJSONObject("photos").getJSONArray("data");
	            								Log.d(TAG, imageJsonResults.toString());		
	            						imageResults.clear();
	            						imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
	            						Log.d(TAG, imageResults.toString());
	            					} catch (JSONException e) {
	            						e.printStackTrace();
	            					}
	            				}
	            		});
	                }
	            }
	            if (response.getError() != null) {
					Log.e(TAG, response.getError().toString());
	            }
	        }
	    });
	    	    
	    request.executeAsync();
	} 
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        makeMeRequest(session);
	    }
	}
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	private static final int REAUTH_ACTIVITY_CODE = 100;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REAUTH_ACTIVITY_CODE) {
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
}
