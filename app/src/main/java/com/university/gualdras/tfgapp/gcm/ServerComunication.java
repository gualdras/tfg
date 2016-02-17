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

    public static void post(HttpURLConnection httpUrlConnection, JSONObject jsonParams) throws IOException {

        httpUrlConnection.setRequestMethod("POST");

        httpUrlConnection.setRequestProperty("Content-Type", "application/json");

        DataOutputStream wr = new DataOutputStream(httpUrlConnection.getOutputStream ());
        wr.writeBytes(jsonParams.toString());
        wr.flush();
        wr.close();
    }

}
