/**
 * 
 */
package com.fbphotos2flickr.app;

import java.io.File;
import java.io.FileInputStream;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fbphotos2flickr.app.FlickrHelper;
import com.fbphotos2flickr.app.FlickrjActivity;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;

public class FlickrUploadPhotoTask extends AsyncTask<OAuth, Void, String> {
	/**
	 * 
	 */
	private final FlickrjActivity flickrjAndroidSampleActivity;
	private File file;

	// private final Logger logger = LoggerFactory
	// .getLogger(UploadPhotoTask.class);

	public FlickrUploadPhotoTask(FlickrjActivity flickrjAndroidSampleActivity,
			File file) {
		this.flickrjAndroidSampleActivity = flickrjAndroidSampleActivity;
		this.file = file;
	}

	/**
	 * The progress dialog before going to the browser.
	 */
	private ProgressDialog mProgressDialog;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(flickrjAndroidSampleActivity,
				"", "Uploading..."); //$NON-NLS-1$ //$NON-NLS-2$
		mProgressDialog.setCanceledOnTouchOutside(true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dlg) {
				FlickrUploadPhotoTask.this.cancel(true);
			}
		});
	}

	@Override
	protected String doInBackground(OAuth... params) {
		OAuth oauth = params[0];
		OAuthToken token = oauth.getToken();

		try {
			Flickr f = FlickrHelper.getInstance().getFlickrAuthed(
					token.getOauthToken(), token.getOauthTokenSecret());

			UploadMetaData uploadMetaData = new UploadMetaData();
			uploadMetaData.setTitle("" + file.getName());
			return f.getUploader().upload(file.getName(),
					new FileInputStream(file), uploadMetaData);
		} catch (Exception e) {
			Log.e("boom!!", "" + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String response) {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		if (response != null) {
			Log.e("", "" + response);
		} else {

		}

		if (monUploadDone != null) {
			monUploadDone.onComplete();
		}

		Toast.makeText(flickrjAndroidSampleActivity.getApplicationContext(),
				"Upload Completed", Toast.LENGTH_SHORT).show();

	}

	onUploadDone monUploadDone;

	public void setOnUploadDone(onUploadDone monUploadDone) {
		this.monUploadDone = monUploadDone;
	}

	public interface onUploadDone {
		void onComplete();
	}

}