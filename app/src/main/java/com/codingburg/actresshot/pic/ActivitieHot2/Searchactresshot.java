package com.codingburg.actresshot.pic.ActivitieHot2;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.AdapterHot2.AdapterSearchactresshot;
import com.codingburg.actresshot.pic.AdapterHot2.AdapterWallpaperactresshot;
import com.codingburg.actresshot.pic.ModelHot2.WallpaperA;
import com.codingburg.actresshot.pic.UtilHot2.Const;
import com.codingburg.actresshot.pic.UtilHot2.GDPR;
import com.codingburg.actresshot.pic.UtilHot2.OffsetD;
import com.codingburg.actresshot.pic.UtilHot2.Tool;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Searchactresshot extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBar actionBar;
    private EditText et_search;
    private String lastId = "0";
    private RecyclerView recyclerView;
    private AdapterWallpaperactresshot mAdapter;
    private boolean itShouldLoadMore = true;
    private RecyclerView recyclerSuggestion;
    private AdapterSearchactresshot mAdapterSuggestion;
    private LinearLayout lyt_suggestion;
    private ArrayList<WallpaperA> arrayList;
    private ImageButton bt_clear;
    private ProgressBar progressBar1;
    private ProgressBar progressBar;
    private View parent_view, lyt_no_item;
    private AdView adView;
    String tags;
    Tool tool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        parent_view = findViewById(android.R.id.content);

        if (ConfigerationsHot2.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        tool = new Tool(Searchactresshot.this);

        initComponent();

        setupToolbar();
        loadBannerAd();

        if (getIntent().hasExtra("tags")) {
            tags = getIntent().getStringExtra("tags");
            searchTags();
        } else {
            et_search.requestFocus();
        }

    }

    private void initComponent() {
        lyt_suggestion = findViewById(R.id.lyt_suggestion);
        et_search = findViewById(R.id.et_search);
        bt_clear = findViewById(R.id.bt_clear);
        bt_clear.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar1 = findViewById(R.id.progressBar1);
        recyclerView = findViewById(R.id.recyclerView);
        lyt_no_item = findViewById(R.id.lyt_no_item);
        recyclerSuggestion = findViewById(R.id.recyclerSuggestion);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(Searchactresshot.this, ConfigerationsHot2.NUM_OF_COLUMNS));
        recyclerView.setHasFixedSize(true);
        OffsetD itemDecoration = new OffsetD(Searchactresshot.this, R.dimen.grid_space_wallpaper);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerSuggestion.setLayoutManager(new LinearLayoutManager(this));
        recyclerSuggestion.setHasFixedSize(true);

        et_search.addTextChangedListener(textWatcher);

        //set data and list adapter
        arrayList = new ArrayList<>();
        mAdapter = new AdapterWallpaperactresshot(Searchactresshot.this, arrayList);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdapterWallpaperactresshot.OnItemClickListener() {
            @Override
            public void onItemClick(View v, WallpaperA obj, int position) {
                Intent intent = new Intent(getApplicationContext(), ImageSlideractresshot.class);
                intent.putExtra("POSITION_ID", position);
                Const.arrayList.clear();
                Const.arrayList.addAll(arrayList);
                startActivity(intent);
            }
        });

        //set data and list adapter suggestion
        mAdapterSuggestion = new AdapterSearchactresshot(this);
        recyclerSuggestion.setAdapter(mAdapterSuggestion);
        showSuggestionSearch();
        mAdapterSuggestion.setOnItemClickListener(new AdapterSearchactresshot.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String viewModel, int pos) {
                et_search.setText(viewModel);
                lyt_suggestion.setVisibility(View.GONE);
                hideKeyboard();
                searchAction();
            }
        });

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
                    return true;
                }
                return false;
            }
        });

        et_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showSuggestionSearch();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });

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

    }

    public void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence c, int i, int i1, int i2) {
            if (c.toString().trim().length() == 0) {
                bt_clear.setVisibility(View.GONE);
            } else {
                bt_clear.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void searchTags() {
        lyt_suggestion.setVisibility(View.GONE);
        showNotFoundView(false);
        et_search.setText(tags);
        final String query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            mAdapterSuggestion.addSearchHistory(query);
            arrayList.clear();
            mAdapter.notifyDataSetChanged();
            progressBar1.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestSearchApi(query);
                }
            }, Const.DELAY_REFRESH);
        } else {
            Toast.makeText(this, "Please input keyword!", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchAction() {
        lyt_suggestion.setVisibility(View.GONE);
        showNotFoundView(false);
        final String query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            mAdapterSuggestion.addSearchHistory(query);
            arrayList.clear();
            mAdapter.notifyDataSetChanged();
            progressBar1.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestSearchApi(query);
                }
            }, Const.DELAY_REFRESH);
        } else {
            Toast.makeText(this, "Please input keyword!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMore() {
        showNotFoundView(false);
        final String query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestSearchNextApi(query);
                }
            }, Const.DELAY_PROGRESS);
        } else {
            Toast.makeText(this, "Please input keyword!", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestSearchApi(String query) {

        if (tool.isNetworkAvailable()) {

            itShouldLoadMore = false;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Const.URL_SEARCH_WALLPAPER + "&search=" + query + "&offset=0", null, new Response.Listener<JSONArray>() {
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
            Toast.makeText(this, "no network!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void requestSearchNextApi(String query) {

        itShouldLoadMore = false;
        progressBar.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Const.URL_SEARCH_WALLPAPER + "&search=" + query + "&offset=" + lastId, null, new Response.Listener<JSONArray>() {
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
            }
        });

        MyAppactresshot.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    private void showSuggestionSearch() {
        mAdapterSuggestion.refreshItems();
        lyt_suggestion.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            Snackbar.make(parent_view, item.getTitle() + " clicked", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

//    private void showFailedView(boolean show, String message) {
//        View lyt_failed = findViewById(R.id.lyt_failed);
//        ((TextView) findViewById(R.id.failed_message)).setText(message);
//        if (show) {
//            recyclerView.setVisibility(View.GONE);
//            lyt_failed.setVisibility(View.VISIBLE);
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//            lyt_failed.setVisibility(View.GONE);
//        }
//        findViewById(R.id.failed_retry).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                searchAction();
//            }
//        });
//    }

    private void showNotFoundView(boolean show) {
        View lyt_no_item = findViewById(R.id.lyt_no_item);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (et_search.length() > 0) {
            et_search.setText("");
        } else {
            super.onBackPressed();
        }
    }

    public void loadBannerAd() {
       /* if (ConfigerationsHot2.ENABLE_ADMOB_BANNER_ADS_MAIN_PAGE) {
            MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
            adView = findViewById(R.id.adView);
            adView.loadAd(GDPR.getAdRequest(this));
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }
            });
        }*/
    }

    private void showRefresh(boolean show) {
        if (show) {
            progressBar1.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar1.setVisibility(View.GONE);
                }
            }, Const.DELAY_PROGRESS);
        }
    }

}
