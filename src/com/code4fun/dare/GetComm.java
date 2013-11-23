package com.code4fun.dare;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by vvu on 23/11/13.
 */
public class GetComm extends AsyncTask<String, Void, String> {

	final String TAG = "GetComm";
	String GETAnswer;

	public void post(final String json, final String path) {
		Runnable runnable = new Runnable()  {
			public void run() {

			}
		};
		Thread mythread = new Thread(runnable);
		mythread.start();
	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	@Override
	protected String doInBackground(String... strings) {
		HttpClient Client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://172.16.2.113:6969" + strings[0]);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			String answer = Client.execute(httpget, responseHandler);
			this.GETAnswer = answer;
			return this.GETAnswer;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(String result) {
		//PRODE IMPLEMENT GET ANSWER
	}
}