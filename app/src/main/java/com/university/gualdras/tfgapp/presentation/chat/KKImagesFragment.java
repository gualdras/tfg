package com.university.gualdras.tfgapp.presentation.chat;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.ContactItem;
import com.university.gualdras.tfgapp.domain.LabeledImage;

import java.util.ArrayList;

/**
 * Created by gualdras on 13/04/16.
 */
public class KKImagesFragment extends ListFragment {

    ImageLabelsAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Set the list choice mode to allow only one selection at a time
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Set the list adapter for the ListView
        // Discussed in more detail in the user interface classes lesson

        mAdapter = new ImageLabelsAdapter(getActivity());
        setListAdapter(mAdapter);
    }

    public void add(ArrayList<LabeledImage> labeledImages){
        for(LabeledImage labeledImage: labeledImages){
            mAdapter.add(labeledImage);
        }
    }


}

 class ImageLabelsAdapter extends BaseAdapter {

     private static LayoutInflater mInflater = null;
     Context mContext;

     ArrayList<LabeledImage> mArrayList = new ArrayList<>();


     public ImageLabelsAdapter(Context context){
         mContext = context;
         mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     }

     public void add(LabeledImage labeledImage){
         mArrayList.add(labeledImage);
        notifyDataSetChanged();
     }

     @Override
     public int getCount() {
         return mArrayList.size();
     }

     @Override
     public Object getItem(int position) {
         return mArrayList.get(position);
     }

     @Override
     public long getItemId(int position) {
         return position;
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         View newView = convertView;
         final ViewHolder holder;

         final LabeledImage curr = (LabeledImage) getItem(position);

         if (null == convertView) {
             holder = new ViewHolder();
             newView = mInflater
                     .inflate(R.layout.contact_item, parent, false);
             holder.labels = (TextView) newView.findViewById(R.id.contact_name);
             holder.image = (ImageView) newView.findViewById(R.id.contact_profile_photo);

             newView.setTag(holder);

         } else {
             holder = (ViewHolder) newView.getTag();
         }


         holder.labels.setText(curr.getLabels());
         holder.image.setImageBitmap(curr.getImage());

         return newView;
     }



     static class ViewHolder {
         TextView labels;
         ImageView image;
     }
 }