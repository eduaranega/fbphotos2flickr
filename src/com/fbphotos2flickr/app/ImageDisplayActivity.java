package com.fbphotos2flickr.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.loopj.android.image.SmartImageView;

public class ImageDisplayActivity extends Activity {
	
	private static final String TAG = "ImageDisplayActivity";
	
	private String filename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		ImageResult result = (ImageResult) getIntent().getSerializableExtra("result");
		SmartImageView ivImage = (SmartImageView) findViewById(R.id.ivResult);
		ivImage.setImageUrl(result.getFullUrl());
		
		int position = getIntent().getIntExtra("position", 0);
		
		filename = Integer.toString(position) + ".jpg";
		DownloadWebPageTask task = new DownloadWebPageTask();
	    task.execute(new String[] { result.getFullUrl() });
				
	}
	
	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
	    @Override
	    protected String doInBackground(String... urls) {
	      String response = "";
	      for (String url : urls) {
	        DefaultHttpClient client = new DefaultHttpClient();
	        long startTime = System.currentTimeMillis();
	        HttpGet httpGet = new HttpGet(url);
	        try {
	          
	          File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
	          File file = new File(path, filename);
	          Log.d(TAG, "Starting download from " + url + " to " + file.getAbsolutePath());
	          
	          HttpResponse execute = client.execute(httpGet);
	          InputStream content = execute.getEntity().getContent();                
              BufferedInputStream bis = new BufferedInputStream(content);
                
                // Read bytes to the Buffer until there is nothing more to read(-1).
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                }
                
                // write to file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baf.toByteArray());
                fos.close();
                Log.d(TAG, "Download Completed in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
	        	
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	      return response;
	    }
	}
	
}

