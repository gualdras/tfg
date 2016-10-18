package com.university.gualdras.tfgapp.gcm;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;


public class ServerComunication {
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();


    private static HttpURLConnection post(String url, JSONObject jsonParams) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url)
                .openConnection();
        httpURLConnection.setRequestMethod("POST");

        httpURLConnection.setRequestProperty("Content-Type", "application/json");

        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream ());
        wr.writeBytes(jsonParams.toString());
        wr.flush();
        wr.close();

        switch (httpURLConnection.getResponseCode()){
            case HttpURLConnection.HTTP_OK:
                InputStream in = new BufferedInputStream(
                        httpURLConnection.getInputStream());

                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                InputStream err = new BufferedInputStream(httpURLConnection.getErrorStream());
                break;
        }
        return httpURLConnection;
    }

    public static HttpURLConnection post(String url, JSONObject jsonParams, int maxAttempts) throws IOException {
        boolean cont = true;
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        HttpURLConnection httpURLConnection =null;
        for (int i = 1; i <= maxAttempts && cont; i++) {
            try {
                httpURLConnection = post(url, jsonParams);
                cont = false;
            } catch (IOException e){
                if (i == maxAttempts) {
                    throw e;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                backoff *= 2;
            }
        }
        return httpURLConnection;
    }

}
