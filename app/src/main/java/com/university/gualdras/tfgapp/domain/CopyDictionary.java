package com.university.gualdras.tfgapp.domain;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.university.gualdras.tfgapp.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CopyDictionary extends AsyncTask <Void, Void, Void> {

    AssetManager am;
    Context mContext;

    public CopyDictionary(AssetManager am, Context mContext) {
        this.am = am;
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(Void ...params) {
        String directory = "dict";
        copyFileOrDir(directory);
        return null;
    }

    private void copyFileOrDir(String directory) {
        AssetManager assetManager = mContext.getAssets();
        String assets[];
        try {
            assets = assetManager.list(directory);
            if (assets.length == 0) {
                copyFile(directory);
            } else {
                String fullPath = Constants.PATH_TO_DIRECTORY + directory;
                File dir = new File(fullPath);
                if (!dir.exists())
                    dir.mkdirs();
                for (int i = 0; i < assets.length; ++i) {
                    copyFileOrDir(directory + "/" + assets[i]);
                }
            }
        } catch (IOException ex) {
        }
    }

    private void copyFile(String filename) {
        AssetManager assetManager = mContext.getAssets();

        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(filename);
            String newFileName = Constants.PATH_TO_DIRECTORY + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
        }
    }
}
