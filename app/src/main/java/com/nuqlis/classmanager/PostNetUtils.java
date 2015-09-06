package com.nuqlis.classmanager;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostNetUtils extends AsyncTask<String, String, String> {
    private JSONObject obj;

    interface OnTaskCompleted{
        void onTaskCompleted(String response);
    }

    private OnTaskCompleted listener ;

    public void SetJSONObject(JSONObject obj){
        this.obj = obj;
    }

    public PostNetUtils(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection = null;
        String response = "";

        try {
            url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("SOAPAction", params[0]);

            OutputStream os = urlConnection.getOutputStream();
            os.write(obj.toString().getBytes());
            os.flush();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                String responseString = readStream(urlConnection.getInputStream());
                response = responseString;
            }else{
                Log.v("CatalogClient", "Response code:" + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }

        return response;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String s = response.toString();
        s = s.trim();
         s = s.substring(1, s.length()-1);
        s = s.replace("\\", "");
        return s;
    }

    @Override
    protected void onPostExecute(String s){
        listener.onTaskCompleted(s);
    }
}
