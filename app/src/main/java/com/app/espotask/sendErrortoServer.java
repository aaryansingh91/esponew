package com.aksofts.espotask;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

public interface sendErrortoServer {
        static void sendErrorToServer(Context context, String user_id, String errorMessage, String errortype, String previewMessage, String network_type) {
        try {

            String qry = context.getResources().getString(R.string.app_url) + "/user/update_error.php?";
            String datatohash = "a=" + user_id.trim() + "&m=" + URLEncoder.encode(errorMessage, "UTF-8") + "&p=" + URLEncoder.encode(previewMessage, "UTF-8") + "&t=" + URLEncoder.encode(errortype, "UTF-8") + "&n=" + URLEncoder.encode(network_type, "UTF-8").trim();
            try {
                String token = temp.sha256_temp(datatohash);
                qry = qry+datatohash+"&token="+token;
            } catch (
                    NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


            class dbprocess extends AsyncTask<String, Void, String> implements com.aksofts.espotask.dbprocess {
                @Override
                protected void onPostExecute(String data) {
                    System.out.println("Log Sent to Server about this Error");
                    // System.out.println(datatohash);
                }

                @Override
                protected String doInBackground(String... params) {
                    String furl = params[0];
                    try {
                        URL url = new URL(furl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        return br.readLine();
                    } catch (Exception ex) {
                        return ex.getMessage();
                    }
                }
            }

            dbprocess obj = new dbprocess();
            obj.execute(qry);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}