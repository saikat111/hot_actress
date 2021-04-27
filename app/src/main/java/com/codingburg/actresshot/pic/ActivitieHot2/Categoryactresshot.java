package com.codingburg.actresshot.pic.ActivitieHot2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.AdapterHot2.AdapterWallpaperactresshot;
import com.codingburg.actresshot.pic.ModelHot2.WallpaperA;
import com.codingburg.actresshot.pic.UtilHot2.Const;
import com.codingburg.actresshot.pic.UtilHot2.DataBaseHelp;
import com.codingburg.actresshot.pic.UtilHot2.OffsetD;
import com.codingburg.actresshot.pic.UtilHot2.Tool;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;

/*import com.facebook.ads.*;*/
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Categoryactresshot extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout = null;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    DataBaseHelp dataBaseHelp;
    private String lastId = "0";
    private boolean itShouldLoadMore = true;
    private AdapterWallpaperactresshot mAdapter;
    private ArrayList<WallpaperA> arrayList;
    ProgressBar progressBar;
    View lytNoItem;
    Tool tool;
    String categoryId, categoryName;
    private AdView adView;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_detail);

        if (ConfigerationsHot2.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        InterstitialAd.load(Categoryactresshot.this,getString(R.string.admob_interstitial_unit_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
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

       /* AudienceNetworkAds.initialize(this);

        adView = new AdView(this, getString(R.string.facebook_banner_ads), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
*/





        relativeLayout = findViewById(R.id.lyt_parent);
        lytNoItem = findViewById(R.id.lyt_no_item);

        dataBaseHelp = new DataBaseHelp(Categoryactresshot.this);
        tool = new Tool(Categoryactresshot.this);

        Intent intent = getIntent();
        categoryId = intent.getStringExtra("category_id");
        categoryName = intent.getStringExtra("category_name");

        sToolbar();



        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);
        showRefresh(true);

        progressBar = findViewById(R.id.progressBar);

        arrayList = new ArrayList<>();
        mAdapter = new AdapterWallpaperactresshot(Categoryactresshot.this, arrayList);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(Categoryactresshot.this, ConfigerationsHot2.NUM_OF_COLUMNS));

     /*   recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));*/
        recyclerView.setHasFixedSize(true);
        OffsetD itemDecoration = new OffsetD(Categoryactresshot.this, R.dimen.grid_space_wallpaper);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(mAdapter);

        fLoadData();

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
            public void onItemClick(View v, final WallpaperA obj, int position) {
                Intent intent = new Intent(getApplicationContext(), ImageSlideractresshot.class);
                intent.putExtra("POSITION_ID", position);
                Const.arrayList.clear();
                Const.arrayList.addAll(arrayList);
                startActivity(intent);
                showInterstitialAd();
            }
        });

    }

    private void fLoadData() {

        if (tool.isNetworkAvailable()) {

            itShouldLoadMore = false;
            dataBaseHelp.resetCategoryDetail(Const.TABLE_CATEGORY_DETAIL, categoryId);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Const.URL_CATEGORY_DETAIL + "&id=" + categoryId + "&offset=0", null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    showRefresh(false);
                    itShouldLoadMore = true;

                    if (response.length() <= 0) {
                        lytNoItem.setVisibility(View.VISIBLE);
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
                            dataBaseHelp.saveWallpaper(arrayList.get(i), Const.TABLE_CATEGORY_DETAIL);
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
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            });

            MyAppactresshot.getInstance().addToRequestQueue(jsonArrayRequest);

        } else {
            showRefresh(false);
            arrayList = dataBaseHelp.getCategoryDetail(categoryId, Const.TABLE_CATEGORY_DETAIL);
            mAdapter = new AdapterWallpaperactresshot(Categoryactresshot.this, arrayList);
            recyclerView.setAdapter(mAdapter);
        }

    }

    private void loadMore() {

        itShouldLoadMore = false;
        progressBar.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Const.URL_CATEGORY_DETAIL + "&id=" + categoryId + "&offset=" + lastId, null, new Response.Listener<JSONArray>() {
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
            lytNoItem.setVisibility(View.GONE);
            arrayList.clear();
            mAdapter.notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fLoadData();
                }
            }, Const.DELAY_REFRESH);
        } else {
            showRefresh(false);
            isOffline();
        }
    }

    private void isOffline() {
        Snackbar snackBar = Snackbar.make(relativeLayout, getResources().getString(R.string.msg_offline), Snackbar.LENGTH_LONG);
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

    private void sToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(categoryName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchwallpaper, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.search:
                Intent intent = new Intent(getApplicationContext(), Searchactresshot.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void LoadInterstitialAd() {
        //admob

        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) Categoryactresshot.this);
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
        SharedPreferences sh = getSharedPreferences("countclick", MODE_APPEND);
        int counter = sh.getInt("count", 0);

        if (counter == ConfigerationsHot2.INTERSTITIAL_ADS_INTERVAL) {
            LoadInterstitialAd();
            SharedPreferences sharedPreferences = getSharedPreferences("countclick",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putInt("count", 0);
            myEdit.commit();


        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("countclick",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            counter = counter + 1;
            myEdit.putInt("count", counter);
            myEdit.commit();
        }


    }
}
