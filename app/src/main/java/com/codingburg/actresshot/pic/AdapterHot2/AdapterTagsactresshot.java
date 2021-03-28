package com.codingburg.actresshot.pic.AdapterHot2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.ActivitieHot2.Searchactresshot;

import java.util.ArrayList;

public class AdapterTagsactresshot extends RecyclerView.Adapter<AdapterTagsactresshot.ViewHolder> {

    private ArrayList<String> arrayList;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_tags;

        public ViewHolder(View view) {
            super(view);
            txt_tags = view.findViewById(R.id.item_tags);
        }

    }

    public AdapterTagsactresshot(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemtag, parent, false);
        return new AdapterTagsactresshot.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ((ViewHolder) holder).txt_tags.setText(arrayList.get(position).toLowerCase());

        ((ViewHolder) holder).txt_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Searchactresshot.class);
                intent.putExtra("tags", arrayList.get(position).toLowerCase());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}