package com.university.gualdras.tfgapp.gcm;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gualdras on 13/02/16.
 */
public class ServerComunication {

    public static HttpURLConnection post(String url, JSONObject jsonParams) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url)
                .openConnection();
        httpURLConnection.setRequestMethod("POST");

        httpURLConnection.setRequestProperty("Content-Type", "application/json");

        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream ());
        wr.writeBytes(jsonParams.toString());
        wr.flush();
        wr.close();

        return httpURLConnection;
    }

    //Todo: Implement incremental backoff post

}
