package com.university.gualdras.tfgapp.domain.network;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.university.gualdras.tfgapp.domain.SuggestedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ImageLabelDetectionTask extends AsyncTask<Void, Void, SuggestedImage> {
    private static final String TAG = "ImageFilter";
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCHiOSu8pudqXgYKpzJRNnIBQLhHlFb07g";

    SuggestedImage suggestedImage;
    ImageInteractionListener mListener;

    public ImageLabelDetectionTask(Activity activity, SuggestedImage suggestedImage) {
        this.suggestedImage = suggestedImage;
        this.mListener = (ImageInteractionListener) activity;
    }


    @Override
    protected SuggestedImage doInBackground(Void... params) {
        try {
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
            builder.setVisionRequestInitializer(new
                    VisionRequestInitializer(CLOUD_VISION_API_KEY));
            Vision vision = builder.build();

            BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                    new BatchAnnotateImagesRequest();

            ArrayList<AnnotateImageRequest> requests = new ArrayList<>();
            Bitmap bitmap = suggestedImage.getBitmap();
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            Image base64EncodedImage = new Image();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(10);
                add(labelDetection);
            }});
            requests.add(annotateImageRequest);


            batchAnnotateImagesRequest.setRequests(requests);

            Vision.Images.Annotate annotateRequest =
                    vision.images().annotate(batchAnnotateImagesRequest);
            annotateRequest.setDisableGZipContent(true);
            Log.d(TAG, "created Cloud Vision request object, sending request");

            BatchAnnotateImagesResponse response = annotateRequest.execute();
            suggestedImage.setTags(convertResponseToMap(response));

        } catch (GoogleJsonResponseException e) {
            Log.d(TAG, "failed to make API request because " + e.getContent());
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
        return suggestedImage;
    }

    @Override
    protected void onPostExecute(SuggestedImage suggestedImage) {
        mListener.onImageLabeled(suggestedImage);
    }

    private HashMap<String, Float> convertResponseToMap(BatchAnnotateImagesResponse responses) {
        HashMap<String, Float> tags = new HashMap<>();

        for (int i = 0; i < responses.getResponses().size(); i++) {
            List<EntityAnnotation> labels = responses.getResponses().get(i).getLabelAnnotations();
            if (labels != null) {
                for (EntityAnnotation label : labels) {
                    tags.put(label.getDescription(), label.getScore());
                }
            }
        }

        return tags;
    }
}
