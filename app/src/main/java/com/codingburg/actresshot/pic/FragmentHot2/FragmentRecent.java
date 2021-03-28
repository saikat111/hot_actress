package com.codingburg.actresshot.pic.FragmentHot2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.ActivitieHot2.ImageSlideractresshot;
import com.codingburg.actresshot.pic.ActivitieHot2.Searchactresshot;
import com.codingburg.actresshot.pic.ActivitieHot2.MyAppactresshot;
import com.codingburg.actresshot.pic.AdapterHot2.AdapterWallpaperactresshot;
import com.codingburg.actresshot.pic.ModelHot2.WallpaperA;
import com.codingburg.actresshot.pic.UtilHot2.Const;
import com.codingburg.actresshot.pic.UtilHot2.DataBaseHelp;
import com.codingburg.actresshot.pic.UtilHot2.OffsetD;
import com.codingburg.actresshot.pic.UtilHot2.Tool;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.snackbar.Snackbar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.os.ParcelFileDescriptor.MODE_APPEND;

public class FragmentRecent extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout = null;
    RecyclerView recyclerView;
    RelativeLayout lyt_parent;
    DataBaseHelp dataBaseHelp;
    private String lastId = "0";
    private boolean itShouldLoadMore = true;
    private AdapterWallpaperactresshot mAdapter;
    private ArrayList<WallpaperA> arrayList;
    private InterstitialAd interstitialAd;

    ProgressBar progressBar;
    View lyt_no_item, view;
    Tool tool;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wallpaper, container, false);
        lyt_parent = view.findViewById(R.id.lyt_parent);
        lyt_no_item = view.findViewById(R.id.lyt_no_item);

        if (ConfigerationsHot2.ENABLE_RTL_MODE) {
            lyt_parent.setRotationY(180);
        }

        setHasOptionsMenu(true);



        dataBaseHelp = new DataBaseHelp(getActivity());
        tool = new Tool(getActivity());

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);
        showRefresh(true);

        progressBar = view.findViewById(R.id.progressBar);

        arrayList = new ArrayList<>();
        mAdapter = new AdapterWallpaperactresshot(getActivity(), arrayList);

        recyclerView = view.findViewById(R.id.recyclerView);
        /*Configerations.NUM_OF_COLUMNS*/
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setHasFixedSize(true);
        OffsetD itemDecoration = new OffsetD(getActivity(), R.dimen.grid_space_wallpaper);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(mAdapter);

        firstLoadData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            loadMore();
                        }
                    }
                }
            }
        });





















        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mAdapter.setOnItemClickListener(new AdapterWallpaperactresshot.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final WallpaperA obj, final int position) {

                Intent intent = new Intent(getActivity(), ImageSlideractresshot.class);
                intent.putExtra("POSITION_ID", position);
                Const.arrayList.clear();
                Const.arrayList.addAll(arrayList);
                startActivity(intent);

                showInterstitialAd();
            }
        });

        return view;
    }

    private void firstLoadData() {

        if (tool.isNetworkAvailable()) {

            itShouldLoadMore = false;
            dataBaseHelp.deleteData(Const.TABLE_RECENT);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Const.URL_RECENT_WALLPAPER + 0, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    showRefresh(false);
                    itShouldLoadMore = true;

                    if (response.length() <= 0) {
                        lyt_no_item.setVisibility(View.VISIBLE);
                        return;
                    }

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            lastId = jsonObject.getString(Const.NO);
                            String image_id = jsonObject.getString(Const.IMAGE_ID);
                            String image_upload = jsonObject.getString(Const.IMAGE_UPLOAD);
                            String image_url = jsonObject.getString(Const.IMAGE_URL);
                            String type = jsonObject.getString(Const.TYPE);
                            int view_count = jsonObject.getInt(Const.VIEW_COUNT);
                            int download_count = jsonObject.getInt(Const.DOWNLOAD_COUNT);
                            String featured = jsonObject.getString(Const.FEATURED);
                            String tags = jsonObject.getString(Const.TAGS);
                            String category_id = jsonObject.getString(Const.CATEGORY_ID);
                            String category_name = jsonObject.getString(Const.CATEGORY_NAME);

                            arrayList.add(new WallpaperA(image_id, image_upload, image_url, type, view_count, download_count, featured, tags, category_id, category_name));
                            dataBaseHelp.saveWallpaper(arrayList.get(i), Const.TABLE_RECENT);
                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    itShouldLoadMore = true;
                    showRefresh(false);
                    Toast.makeText(getActivity(), getResources().getString(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            });

            MyAppactresshot.getInstance().addToRequestQueue(jsonArrayRequest);

        } else {
            showRefresh(false);
            arrayList = dataBaseHelp.getAllData(Const.TABLE_RECENT);
            mAdapter = new AdapterWallpaperactresshot(getActivity(), arrayList);
            recyclerView.setAdapter(mAdapter);
        }

    }

    private void loadMore() {

        itShouldLoadMore = false;
        progressBar.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Const.URL_RECENT_WALLPAPER + lastId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        showRefresh(false);
                        progressBar.setVisibility(View.GONE);
                        itShouldLoadMore = true;

                        if (response.length() <= 0) {
                            return;
                        }

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);

                                lastId = jsonObject.getString(Const.NO);
                                String image_id = jsonObject.getString(Const.IMAGE_ID);
                                String image_upload = jsonObject.getString(Const.IMAGE_UPLOAD);
                                String image_url = jsonObject.getString(Const.IMAGE_URL);
                                String type = jsonObject.getString(Const.TYPE);
                                int view_count = jsonObject.getInt(Const.VIEW_COUNT);
                                int download_count = jsonObject.getInt(Const.DOWNLOAD_COUNT);
                                String featured = jsonObject.getString(Const.FEATURED);
                                String tags = jsonObject.getString(Const.TAGS);
                                String category_id = jsonObject.getString(Const.CATEGORY_ID);
                                String category_name = jsonObject.getString(Const.CATEGORY_NAME);

                                arrayList.add(new WallpaperA(image_id, image_upload, image_url, type, view_count, download_count, featured, tags, category_id, category_name));
                                mAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, Const.DELAY_LOAD_MORE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                showRefresh(false);
                itShouldLoadMore = true;
                isOffline();
            }
        });

        MyAppactresshot.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    private void refreshData() {
        if (tool.isNetworkAvailable()) {
            lyt_no_item.setVisibility(View.GONE);
            arrayList.clear();
            mAdapter.notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    firstLoadData();
                }
            }, Const.DELAY_REFRESH);
        } else {
            showRefresh(false);
            isOffline();
        }
    }

    private void isOffline() {
        Snackbar snackBar = Snackbar.make(lyt_parent, getResources().getString(R.string.msg_offline), Snackbar.LENGTH_LONG);
        snackBar.setAction(getResources().getString(R.string.option_retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRefresh(true);
                refreshData();
            }
        });
        snackBar.show();
    }

    private void showRefresh(boolean show) {
        if (show) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, Const.DELAY_PROGRESS);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.searchwallpaper, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            Intent intent = new Intent(getActivity(), Searchactresshot.class);
            startActivity(intent);
            return false;
        }
        return false;
    }

    private void LoadInterstitialAd() {


        interstitialAd = new InterstitialAd(view.getContext(), view.getContext().getString(R.string.facebook_interstitial_ads));
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed

                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback

            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());






    }
    private void showInterstitialAd() {
        SharedPreferences sh = view.getContext().getSharedPreferences("countclick", MODE_APPEND);
        int counter = sh.getInt("count", 0);

        if (counter == ConfigerationsHot2.INTERSTITIAL_ADS_INTERVAL) {
            LoadInterstitialAd();
            SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("countclick",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putInt("count", 0);
            myEdit.commit();


        } else {
            SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("countclick",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            counter = counter + 1;
            myEdit.putInt("count", counter);
            myEdit.commit();
        }


    }



}
