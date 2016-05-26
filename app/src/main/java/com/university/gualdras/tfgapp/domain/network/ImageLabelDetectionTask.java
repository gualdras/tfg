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
import com.university.gualdras.tfgapp.domain.LabeledImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gualdras on 22/04/16.
 */
public class ImageLabelDetectionTask extends AsyncTask <Void, Void, ArrayList<LabeledImage>> {
    private static final String TAG = "ImageFilter";
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCnc9PEMEajUk4E0zfT9CbMC1muNlMtwss";

    ArrayList<Bitmap> bitmaps;
    ImageInteractionListener mListener;

    public ImageLabelDetectionTask(ArrayList<Bitmap> bitmaps, Activity activity) {
        this.bitmaps = bitmaps;
        this.mListener = (ImageInteractionListener) activity;
    }



    @Override
    protected ArrayList<LabeledImage> doInBackground(Void... params) {
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
            for(Bitmap bitmap: bitmaps){
                AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                // Add the image
                Image base64EncodedImage = new Image();
                // Convert the bitmap to a JPEG
                // Just in case it's a format that Android understands but Cloud Vision
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Base64 encode the JPEG
                base64EncodedImage.encodeContent(imageBytes);
                annotateImageRequest.setImage(base64EncodedImage);

                // add the features we want
                annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                    Feature labelDetection = new Feature();
                    labelDetection.setType("LABEL_DETECTION");
                    labelDetection.setMaxResults(10);
                    add(labelDetection);
                }});
                requests.add(annotateImageRequest);
            }

            batchAnnotateImagesRequest.setRequests(requests);

            Vision.Images.Annotate annotateRequest =
                    vision.images().annotate(batchAnnotateImagesRequest);
            // Due to a bug: requests to Vision API containing large images fail when GZipped.
            annotateRequest.setDisableGZipContent(true);
            Log.d(TAG, "created Cloud Vision request object, sending request");

            BatchAnnotateImagesResponse response = annotateRequest.execute();
            return convertResponseToString(response);

        } catch (GoogleJsonResponseException e) {
            Log.d(TAG, "failed to make API request because " + e.getContent());
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<LabeledImage> imagesLabeled) {
        mListener.onImageLabeled(imagesLabeled);
    }

    private ArrayList<LabeledImage> convertResponseToString(BatchAnnotateImagesResponse responses) {
        ArrayList<LabeledImage> labeledImages = new ArrayList<>();
        String message;
        for(int i=0; i<responses.getResponses().size(); i++){
            List<EntityAnnotation> labels = responses.getResponses().get(i).getLabelAnnotations();
            if (labels != null) {
                message = "";
                for (EntityAnnotation label : labels) {
                    message += String.format("%.3f: %s", label.getScore(), label.getDescription());
                    message += ", ";
                }
            } else {
                message = "nothing";
            }
            labeledImages.add(new LabeledImage(bitmaps.get(i), message));
        }

        return labeledImages;
    }
}
