package com.university.gualdras.tfgapp.presentation.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.SuggestedImage;

import java.util.ArrayList;

/**
 * Created by gualdras on 12/05/16.
 */
class SuggestedImageAdapter extends RecyclerView.Adapter<SuggestedImageAdapter.ViewHolder> {

    ArrayList<SuggestedImage> sugestedImages;
    Context mContext;

    public SuggestedImageAdapter(Context context, ArrayList<SuggestedImage> arrayList){
        sugestedImages = arrayList;
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.image, parent, false);

        // Return a new holder instanceD
        ViewHolder viewHolder = new ViewHolder(contactView, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SuggestedImage suggestedImage = sugestedImages.get(position);
                ((ChatActivity)mContext).onSuggestedImageSelected(suggestedImage);

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap image = sugestedImages.get(position).getBitmap();

        // Set item views based on the data model
        ImageView imageView = holder.imageView;
        imageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return sugestedImages.size();
    }
    public void add(SuggestedImage suggestedImage){
        sugestedImages.add(suggestedImage);
        notifyDataSetChanged();
    }



    public void clear(){
        sugestedImages.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView imageView;
        public OnItemClickListener mListener;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, OnItemClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            mListener = listener;

            imageView = (ImageView) itemView.findViewById(R.id.image);
            imageView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

}
