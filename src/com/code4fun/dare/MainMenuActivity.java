package com.code4fun.dare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.content.DialogInterface;
import android.view.View;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.ParseTwitterUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainMenuActivity extends Activity {
	final String TAG = "MainMenuActivity";

    BaseAdapter mAdapter;
    String mUser;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.code4fun.dare.DARE_SCORE")) {
				GetComm fetch = new GetComm() {
					@Override
					protected void onPostExecute(String result) {
						try {
							JSONObject answer = new JSONObject(result);
							final String score = answer.getString("score");
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Button scoreButton = (Button) findViewById(R.id.scoreButton);
									scoreButton.setText("   " + score);
								}
							});

						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}
				};
				fetch.execute("/user/" + ParseTwitterUtils.getTwitter().getScreenName());
			}
		}
	};

	public void updateStatus() {
		GetComm fetch = new GetComm() {
			@Override
			protected void onPostExecute(String result) {
				try {
					JSONObject answer = new JSONObject(result);
					Log.d(TAG, "score: " + answer.get("score"));
					final String score = answer.getString("score");
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Button scoreButton = (Button) findViewById(R.id.scoreButton);
							scoreButton.setText("   " + score);
						}
					});

				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		};
		fetch.execute("/user/" + ParseTwitterUtils.getTwitter().getScreenName());
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
        mUser = ParseTwitterUtils.getTwitter().getScreenName();

		updateStatus();
		IntentFilter filterScore = new IntentFilter("com.code4fun.dare.DARE_SCORE");
		registerReceiver(mReceiver, filterScore);

        findViewById(R.id.createButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createActivity = new Intent(getApplicationContext(), CreateActivity.class);
                startActivity(createActivity);
            }
        });

		findViewById(R.id.starButton).setOnClickListener(startClick);
		findViewById(R.id.discoverButton).setOnClickListener(discoverClick);



        initLoader("/feed/latest");
 	}

    private void initLoader(String url) {
        GetComm retriever = new GetComm() {
            @Override
            protected void onPostExecute(String result) {
                try {
                    final ArrayList<Story> stories = new ArrayList<Story>();
                    JSONArray response = new JSONArray(result);
                    for (int i = 0; i < response.length(); i++) {
                        final JSONObject storyJSON = response.getJSONObject(i);

                        final Story story = new Story();
                        story.author = storyJSON.getString("creator");
                        story.imageUrl = storyJSON.getString("image");
                        story.title = storyJSON.getString("name");
                        story.description = storyJSON.getString("description");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (story.imageUrl.charAt(0) == '/') {
                                        story.imageUrl = GetComm.HOST + story.imageUrl;
                                    }
                                    URL url = new URL(story.imageUrl);
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoInput(true);
                                    connection.connect();
                                    final InputStream input = connection.getInputStream();
                                    final Bitmap image = BitmapFactory.decodeStream(input);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            story.image = image;
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        stories.add(story);
                    }
                    onLoadFinished(stories);
                } catch (NullPointerException e) {
                    Util.inform(getApplicationContext(), "Stories cannot be retrieved at this time");
                    e.printStackTrace();
                } catch (JSONException e){
                    Util.inform(getApplicationContext(), "Stories cannot be retrieved at this time");
                    e.printStackTrace();
                }
            }
        };

        retriever.execute(url);
    }

	View.OnClickListener startClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			GetComm retriever = new GetComm() {
				@Override
				protected void onPostExecute(String result) {
					try {
						final ArrayList<Story> stories = new ArrayList<Story>();
						JSONArray response = new JSONArray(result);
						for (int i = 0; i < response.length(); i++) {
							final JSONObject storyJSON = response.getJSONObject(i);

							final Story story = new Story();
							story.author = storyJSON.getString("creator");
							story.imageUrl = storyJSON.getString("image");
							story.title = storyJSON.getString("name");
							story.description = storyJSON.getString("description");

							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										if (story.imageUrl.charAt(0) == '/') {
											story.imageUrl = GetComm.HOST + story.imageUrl;
										}
										URL url = new URL(story.imageUrl);
										HttpURLConnection connection = (HttpURLConnection) url.openConnection();
										connection.setDoInput(true);
										connection.connect();
										final InputStream input = connection.getInputStream();
										final Bitmap image = BitmapFactory.decodeStream(input);
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												story.image = image;
												mAdapter.notifyDataSetChanged();
											}
										});
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}).start();

							stories.add(story);
						}
						onLoadFinished(stories);
						findViewById(R.id.starButton).setEnabled(false);
						findViewById(R.id.discoverButton).setEnabled(true);
					} catch (NullPointerException e) {
						Util.inform(getApplicationContext(), "Stories cannot be retrieved at this time");
						e.printStackTrace();
					} catch (JSONException e){
						Util.inform(getApplicationContext(), "Stories cannot be retrieved at this time");
						e.printStackTrace();
					}
				}
			};

			retriever.execute("/feed/starred/" + ParseTwitterUtils.getTwitter().getScreenName());
		}
	};

	View.OnClickListener discoverClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			GetComm retriever = new GetComm() {
				@Override
				protected void onPostExecute(String result) {
					try {
						final ArrayList<Story> stories = new ArrayList<Story>();
						JSONArray response = new JSONArray(result);
						for (int i = 0; i < response.length(); i++) {
							final JSONObject storyJSON = response.getJSONObject(i);

							final Story story = new Story();
							story.author = storyJSON.getString("creator");
							story.imageUrl = storyJSON.getString("image");
							story.title = storyJSON.getString("name");
							story.description = storyJSON.getString("description");

							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										if (story.imageUrl.charAt(0) == '/') {
											story.imageUrl = GetComm.HOST + story.imageUrl;
										}
										URL url = new URL(story.imageUrl);
										HttpURLConnection connection = (HttpURLConnection) url.openConnection();
										connection.setDoInput(true);
										connection.connect();
										InputStream input = connection.getInputStream();
										final Bitmap image = BitmapFactory.decodeStream(input);
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												story.image = image;
												mAdapter.notifyDataSetChanged();
											}
										});
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}).start();

							stories.add(story);
						}
						onLoadFinished(stories);
						findViewById(R.id.discoverButton).setEnabled(false);
						findViewById(R.id.starButton).setEnabled(true);
					} catch (NullPointerException e) {
						Util.inform(getApplicationContext(), "Stories cannot be retrieved at this time");
						e.printStackTrace();
					} catch (JSONException e){
						Util.inform(getApplicationContext(), "Stories cannot be retrieved at this time");
						e.printStackTrace();
					}
				}
			};

			retriever.execute("/feed/latest");
		}
	};

	@Override
	public void onPause() {
		super.onPause();  // Always call the superclass method first

		unregisterReceiver(mReceiver);
	}

    private void onLoadFinished(ArrayList<Story> stories) {
        mAdapter = new StoryAdapter(getApplicationContext(), stories);
		findViewById(R.id.discoverButton).setEnabled(false);
        ListView listView = (ListView) findViewById(R.id.app_inner);
        listView.setAdapter(mAdapter);
    }

	public void onBackPressed(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you dare to exit ?").setPositiveButton("I dare !", dialogClickListener)
				.setNegativeButton("No, scared !", dialogClickListener).show();
	}

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which){
				case DialogInterface.BUTTON_POSITIVE:
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
			}
		}
	};
}
