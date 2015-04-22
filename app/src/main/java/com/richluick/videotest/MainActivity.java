package com.richluick.videotest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends YouTubeBaseActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener, YouTubePlayer.OnInitializedListener {

    private static final String API_KEY = "AIzaSyAUfaRIiXCPKtfSdeJ0Ua1hmbnbRVYIHAA";

    private EditText mSearchText;
    private ListView mListView;
    private YouTubePlayer mYouTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchText = (EditText) findViewById(R.id.editText);

        ImageButton searchButton = (ImageButton) findViewById(R.id.imageButton);
        searchButton.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);

        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.videoView);
        playerView.initialize(API_KEY, this);
    }

    @Override
    public void onClick(View v) {
        new GetVideosTask().execute();

        mSearchText.setText("");
        mSearchText.setEnabled(false);
        mSearchText.setEnabled(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long ids) {
        JSONObject object = (JSONObject) mListView.getItemAtPosition(position);

        String videoId = null;
        try {
            //get the video id from the selected JSONObject
            JSONObject id = object.getJSONObject("id");
            videoId = id.getString("videoId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mYouTubePlayer.loadVideo(videoId); //load the video on item click
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        mYouTubePlayer = youTubePlayer; //Set the youtube plater to the correct member variable
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {}

    /*
     * This Async task handles the network GET call to the REST API. It returns a JSONObject that
     * can then be parsed.
     */
    private class GetVideosTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject obj = null;
            try {
                URL url = new URL("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" +
                        mSearchText.getText().toString() + "&type=video&key=" + API_KEY);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == 201 || connection.getResponseCode() == 200) {
                    InputStream response = connection.getInputStream();
                    String jsonResponse = convertStreamToString(response);
                    obj = new JSONObject(jsonResponse); //convert String to JSON
                }

                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return obj;
        }

        @Override
        protected void onPostExecute(JSONObject jsonResponse) {
            super.onPostExecute(jsonResponse);

            ArrayList<JSONObject> jsonObjects = null;
            try {
                JSONArray jsonArray = jsonResponse.getJSONArray("items");
                jsonObjects = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = (JSONObject) jsonArray.get(i);

                    jsonObjects.add(object);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ItemAdapter adapter = new ItemAdapter(MainActivity.this, jsonObjects);
            mListView.setAdapter(adapter);
        }

        /*
         * This method converts an input stream into a String
         *
         * return String the converted String
         */
        private String convertStreamToString(InputStream is) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }
}
