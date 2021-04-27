package com.codingburg.actresshot.pic.FragmentHot2;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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
import com.codingburg.actresshot.pic.ActivitieHot2.Categoryactresshot;
import com.codingburg.actresshot.pic.ActivitieHot2.MyAppactresshot;
import com.codingburg.actresshot.pic.AdapterHot2.AdapterCategoryactresshot;
import com.codingburg.actresshot.pic.ModelHot2.CategoryAactresshot;
import com.codingburg.actresshot.pic.UtilHot2.Const;
import com.codingburg.actresshot.pic.UtilHot2.DataBaseHelp;
import com.codingburg.actresshot.pic.UtilHot2.OffsetD;
import com.codingburg.actresshot.pic.UtilHot2.Tool;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/*import com.facebook.ads.*;*/

import static android.content.Context.MODE_PRIVATE;
import static android.os.ParcelFileDescriptor.MODE_APPEND;

public class FCategoryactresshot extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout = null;
    RecyclerView recyclerView;
    RelativeLayout lyt_parent;
    DataBaseHelp dataBaseHelp;
    private AdapterCategoryactresshot mAdapter;
    private ArrayList<CategoryAactresshot> arrayList;
    ProgressBar progressBar;
    private SearchView searchView;
    private InterstitialAd mInterstitialAd;
    View lyt_no_item, view;
    Tool tool;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wallpaper, container, false);
        //admob

        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.interstitial.InterstitialAd.load(getContext(),view.getContext().getString(R.string.admob_interstitial_unit_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });
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

        recyclerView = view.findViewById(R.id.recyclerView);
        int padding = getResources().getDimensionPixelOffset(R.dimen.grid_space_wallpaper);
        recyclerView.setPadding(padding, padding, padding, padding);

        progressBar = view.findViewById(R.id.progressBar);

        arrayList = new ArrayList<>();
        mAdapter = new AdapterCategoryactresshot(getActivity(), arrayList);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setHasFixedSize(true);
        OffsetD itemDecoration = new OffsetD(getActivity(), R.dimen.grid_space_wallpaper);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setAdapter(mAdapter);

        firstLoadData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mAdapter.setOnItemClickListener(new AdapterCategoryactresshot.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final CategoryAactresshot obj, int position) {
                Intent intent = new Intent(getActivity(), Categoryactresshot.class);
                intent.putExtra("category_id", obj.getCategory_id());
                intent.putExtra("category_name", obj.getCategory_name());
                startActivity(intent);
                showInterstitialAd();
            }
        });

        return view;
    }

    private void firstLoadData() {

        if (tool.isNetworkAvailable()) {

            dataBaseHelp.deleteData(Const.TABLE_CATEGORY);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Const.URL_CATEGORY, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    showRefresh(false);

                    if (response.length() <= 0) {
                        lyt_no_item.setVisibility(View.VISIBLE);
                        return;
                    }

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            String category_id = jsonObject.getString(Const.CATEGORY_ID);
                            String category_name = jsonObject.getString(Const.CATEGORY_NAME);
                            String category_image = jsonObject.getString(Const.CATEGORY_IMAGE);
                            String total_wallpaper = jsonObject.getString(Const.TOTAL_WALLPAPER);

                            arrayList.add(new CategoryAactresshot(category_id, category_name, category_image, total_wallpaper));
                            dataBaseHelp.addtoCategory(arrayList.get(i), Const.TABLE_CATEGORY);
                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showRefresh(false);
                    Toast.makeText(getActivity(), getResources().getString(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            });

            MyAppactresshot.getInstance().addToRequestQueue(jsonArrayRequest);

        } else {
            showRefresh(false);
            arrayList = dataBaseHelp.getAllDataCategory(Const.TABLE_CATEGORY);
            mAdapter = new AdapterCategoryactresshot(getActivity(), arrayList);
            recyclerView.setAdapter(mAdapter);
        }

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
        inflater.inflate(R.menu.menu_search_category, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    private void LoadInterstitialAd() {
        //admob

        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) view.getContext());
        } else {

        }
//fb ads
      /*  interstitialAd = new InterstitialAd(view.getContext(), view.getContext().getString(R.string.facebook_interstitial_ads));
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


*/



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
