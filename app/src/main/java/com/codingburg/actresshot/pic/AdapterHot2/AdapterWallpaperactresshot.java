package com.codingburg.actresshot.pic.AdapterHot2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.ModelHot2.WallpaperA;
import com.codingburg.actresshot.pic.UtilHot2.Tool;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;

public class AdapterWallpaperactresshot extends RecyclerView.Adapter<AdapterWallpaperactresshot.MyViewHolder> {

    private ArrayList<WallpaperA> wallpaperAS;
    private OnItemClickListener mOnItemClickListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View view, WallpaperA obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemOverflowClickListener) {
        this.mOnItemClickListener = mItemOverflowClickListener;
    }

    public AdapterWallpaperactresshot(Context context, ArrayList<WallpaperA> wallpaperAS) {
        this.context = context;
        this.wallpaperAS = wallpaperAS;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_wallpaper;
        TextView txt_view_count, txt_download_count;
        private MaterialRippleLayout lyt_parent;
        private RelativeLayout lyt_view_download;
        private LinearLayout lyt_view_count, lyt_download_count;

        MyViewHolder(View view) {
            super(view);
            img_wallpaper = view.findViewById(R.id.img_wallpaper);
            txt_view_count = view.findViewById(R.id.txt_view_count);
            txt_download_count = view.findViewById(R.id.txt_download_count);
            lyt_parent = view.findViewById(R.id.lyt_parent);
            lyt_view_download = view.findViewById(R.id.lyt_view_download);
            lyt_view_count = view.findViewById(R.id.lyt_view_count);
            lyt_download_count = view.findViewById(R.id.lyt_download_count);

        }
    }

    @Override
    public AdapterWallpaperactresshot.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ConfigerationsHot2.ENABLE_DISPLAY_WALLPAPER_IN_SQUARE) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_square_wallpaper, parent, false));
        } else {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rectanglewallpaper, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(AdapterWallpaperactresshot.MyViewHolder holder, final int position) {

        if (wallpaperAS.get(position).getType().equals("url")) {
           /* Picasso.with(context)
                    .load(wallpaperAS.get(position).getImage_url().replace(" ", "%20"))
                    .placeholder(R.drawable.ic_transparent)
                  *//*  .resize(50,70)
                    .resizeDimen(R.dimen.list_wallpaper_width, R.dimen.list_wallpaper_height)*//*
                    .fit()
                    .centerCrop()
                    .into(holder.img_wallpaper);*/
            Glide.with(context).load(wallpaperAS.get(position).getImage_url()).into(holder.img_wallpaper);

        } else {
           /* Picasso.with(context)
                    .load(Configerations.ADMIN_PANEL_URL + "/upload/" + wallpaperAS.get(position).getImage_upload().replace(" ", "%5"))
                    .placeholder(R.drawable.ic_transparent)
                    .resize(50,70)
                    .resizeDimen(R.dimen.list_wallpaper_width, R.dimen.list_wallpaper_height)
                    .centerCrop()
                    .fit()
                    .into(holder.img_wallpaper);*/
            Glide.with(context).load(ConfigerationsHot2.ADMIN_PANEL_URL + "/upload/" + wallpaperAS.get(position).getImage_upload()).into(holder.img_wallpaper);



        }

        holder.txt_view_count.setText("" + Tool.withSuffix(wallpaperAS.get(position).getView_count()));
        holder.txt_download_count.setText("" + Tool.withSuffix(wallpaperAS.get(position).getDownload_count()));

        if (ConfigerationsHot2.SHOW_VIEW_COUNT) {
            holder.lyt_view_count.setVisibility(View.VISIBLE);
        } else {
            holder.lyt_view_count.setVisibility(View.GONE);
        }

        if (ConfigerationsHot2.SHOW_DOWNLOAD_COUNT) {
            holder.lyt_download_count.setVisibility(View.VISIBLE);
        } else {
            holder.lyt_download_count.setVisibility(View.GONE);
        }

        if (!ConfigerationsHot2.SHOW_VIEW_COUNT && !ConfigerationsHot2.SHOW_DOWNLOAD_COUNT) {
            holder.lyt_view_download.setVisibility(View.GONE);
        } else {
            holder.lyt_view_download.setVisibility(View.VISIBLE);
        }

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, wallpaperAS.get(position), position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return wallpaperAS.size();
    }

}