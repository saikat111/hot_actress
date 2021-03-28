package com.codingburg.actresshot.pic.FragmentHot2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.ActivitieHot2.ImageSlideractresshot;
import com.codingburg.actresshot.pic.ActivitieHot2.HomeScreenactresshot;
import com.codingburg.actresshot.pic.AdapterHot2.AdapterWallpaperactresshot;
import com.codingburg.actresshot.pic.ModelHot2.WallpaperA;
import com.codingburg.actresshot.pic.UtilHot2.Const;
import com.codingburg.actresshot.pic.UtilHot2.DataBaseHelp;
import com.codingburg.actresshot.pic.UtilHot2.OffsetD;

import java.util.ArrayList;

public class FragmentFavoriteactresshot extends Fragment {

    DataBaseHelp dataBaseHelp;
    GridLayoutManager gridLayoutManager;
    RecyclerView recyclerView;
    AdapterWallpaperactresshot adapterImage;
    ArrayList<WallpaperA> itemPhotos;
    LinearLayout lyt_no_favorite;
    private HomeScreenactresshot homeScreenactresshot;
    private Toolbar toolbar;

    public FragmentFavoriteactresshot() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        homeScreenactresshot = (HomeScreenactresshot) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        dataBaseHelp = new DataBaseHelp(getActivity());

        toolbar = view.findViewById(R.id.toolbar);
        lyt_no_favorite = view.findViewById(R.id.lyt_no_favorite);

        gridLayoutManager = new GridLayoutManager(getActivity(), ConfigerationsHot2.NUM_OF_COLUMNS);

        itemPhotos = dataBaseHelp.getAllFavorite(Const.TABLE_FAVORITE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        OffsetD itemDecoration = new OffsetD(getActivity(), R.dimen.grid_space_wallpaper);
        recyclerView.addItemDecoration(itemDecoration);

        adapterImage = new AdapterWallpaperactresshot(getActivity(), itemPhotos);
        recyclerView.setAdapter(adapterImage);
        onItemClickListener();

        if (itemPhotos.size() == 0) {
            lyt_no_favorite.setVisibility(View.VISIBLE);
        } else {
            lyt_no_favorite.setVisibility(View.INVISIBLE);
        }

        setupToolbar();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (itemPhotos != null) {
                itemPhotos = dataBaseHelp.getAllFavorite(Const.TABLE_FAVORITE);
                adapterImage = new AdapterWallpaperactresshot(getActivity(), itemPhotos);
                recyclerView.setAdapter(adapterImage);
                onItemClickListener();
                if (itemPhotos.size() == 0) {
                    lyt_no_favorite.setVisibility(View.VISIBLE);
                } else {
                    lyt_no_favorite.setVisibility(View.INVISIBLE);
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        if (itemPhotos != null && adapterImage.getItemCount() > 0) {
            itemPhotos = dataBaseHelp.getAllFavorite(Const.TABLE_FAVORITE);
            adapterImage = new AdapterWallpaperactresshot(getActivity(), itemPhotos);
            recyclerView.setAdapter(adapterImage);
            onItemClickListener();
            if (itemPhotos.size() == 0) {
                lyt_no_favorite.setVisibility(View.VISIBLE);
            } else {
                lyt_no_favorite.setVisibility(View.INVISIBLE);
            }
        }
        super.onResume();
    }

    public void onItemClickListener() {
        adapterImage.setOnItemClickListener(new AdapterWallpaperactresshot.OnItemClickListener() {
            @Override
            public void onItemClick(View v, WallpaperA obj, int position) {
                Intent intent = new Intent(getActivity(), ImageSlideractresshot.class);
                intent.putExtra("POSITION_ID", position);
                Const.arrayList.clear();
                Const.arrayList.addAll(itemPhotos);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeScreenactresshot.setupNavigationDrawer(toolbar);
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.drawer_favorite));
        homeScreenactresshot.setSupportActionBar(toolbar);
    }

}
