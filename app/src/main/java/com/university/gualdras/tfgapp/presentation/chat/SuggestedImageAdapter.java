package com.university.gualdras.tfgapp.presentation.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.university.gualdras.tfgapp.R;

import java.util.ArrayList;

/**
 * Created by gualdras on 12/05/16.
 */
class SuggestedImageAdapter extends RecyclerView.Adapter<SuggestedImageAdapter.ViewHolder> {

    ArrayList<Bitmap> images;

    public SuggestedImageAdapter(ArrayList<Bitmap> arrayList){
        images = arrayList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.image, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap image = images.get(position);

        // Set item views based on the data model
        ImageView imageView = holder.imageView;
        imageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
    public void add(Bitmap bitmap){
        images.add(bitmap);
        notifyDataSetChanged();
    }

    public void clear(){
        images.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView imageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }


}
