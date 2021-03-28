package com.codingburg.actresshot.pic.AdapterHot2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.ModelHot2.CategoryAactresshot;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterCategoryactresshot extends RecyclerView.Adapter<AdapterCategoryactresshot.MyViewHolder> implements Filterable {

    private ArrayList<CategoryAactresshot> categories;
    private ArrayList<CategoryAactresshot> categoriesFiltered;
    private OnItemClickListener mOnItemClickListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View view, CategoryAactresshot obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemOverflowClickListener) {
        this.mOnItemClickListener = mItemOverflowClickListener;
    }

    public AdapterCategoryactresshot(Context context, ArrayList<CategoryAactresshot> categories) {
        this.context = context;
        this.categories = categories;
        this.categoriesFiltered = categories;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_category;
        TextView txt_category_name, txt_total_wallpaper;
        private MaterialRippleLayout lyt_parent;

        MyViewHolder(View view) {
            super(view);
            img_category = view.findViewById(R.id.category_image);
            txt_category_name = view.findViewById(R.id.category_name);
            txt_total_wallpaper = view.findViewById(R.id.total_wallpaper);
            lyt_parent = view.findViewById(R.id.lyt_parent);

        }
    }

    @Override
    public AdapterCategoryactresshot.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemcategory, parent, false));
    }

    @Override
    public void onBindViewHolder(AdapterCategoryactresshot.MyViewHolder holder, final int position) {

        Picasso.with(context)
                .load(ConfigerationsHot2.ADMIN_PANEL_URL + "/upload/category/" + categoriesFiltered.get(position).getCategory_image())
                .placeholder(R.drawable.ic_transparent)
                .resizeDimen(R.dimen.category_width, R.dimen.category_height)
                .centerCrop()
                .into(holder.img_category);

        holder.txt_category_name.setText("" + categoriesFiltered.get(position).getCategory_name());
        holder.txt_total_wallpaper.setText("" + categoriesFiltered.get(position).getTotal_wallpaper() + " " + context.getResources().getString(R.string.wallpapers));

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, categoriesFiltered.get(position), position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoriesFiltered.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    categoriesFiltered = categories;
                } else {
                    ArrayList<CategoryAactresshot> filteredList = new ArrayList<>();
                    for (CategoryAactresshot row : categories) {
                        if (row.getCategory_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    categoriesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = categoriesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                categoriesFiltered = (ArrayList<CategoryAactresshot>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}