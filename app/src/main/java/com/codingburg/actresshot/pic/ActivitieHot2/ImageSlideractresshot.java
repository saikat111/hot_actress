package com.codingburg.actresshot.pic.ActivitieHot2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.AdapterHot2.AdapterTagsactresshot;
import com.codingburg.actresshot.pic.ModelHot2.WallpaperA;
import com.codingburg.actresshot.pic.UtilHot2.Const;
import com.codingburg.actresshot.pic.UtilHot2.DataBaseHelp;
import com.codingburg.actresshot.pic.UtilHot2.Tool;
import com.balysv.materialripple.MaterialRippleLayout;
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.github.chrisbanes.photoview.PhotoView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*import com.facebook.ads.*;*/
public class ImageSlideractresshot extends AppCompatActivity implements SensorEventListener {

    DataBaseHelp dataBaseHelp;
    int position;
    ViewPager viewpager;
    int TOTAL_IMAGE;
    private SensorManager sensorManager;
    private boolean checkImage = false;
    private long lastUpdate;
    Handler handler;
    Runnable Update;
    String image_id;
    private BottomSheetBehavior bottomSheetBehavior;
    TextView txt_category_name, txt_resolution, txt_view_count, txt_download_count;
    ImageView img_thumb;
    ImageButton btn_favorite, btn_download, btn_share, btn_set;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    LinearLayout lyt_action;
    RecyclerView recyclerView_tags;
    AdapterTagsactresshot adapterTagsactresshot;
    ArrayList<String> arrayListTags;
    LinearLayout lyt_tags;
    Tool tool;
    private final String TAG = ImageSlideractresshot.class.getSimpleName();

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_slider);

        if (ConfigerationsHot2.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        lyt_action = findViewById(R.id.lyt_action);

        dataBaseHelp = new DataBaseHelp(ImageSlideractresshot.this);
        tool = new Tool(ImageSlideractresshot.this);

        //admob

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(ImageSlideractresshot.this,getString(R.string.admob_interstitial_unit_id), adRequest, new InterstitialAdLoadCallback() {
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
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest2);






        /*AudienceNetworkAds.initialize(this);

        adView = new AdView(this, getString(R.string.facebook_banner_ads), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
*/



        btn_favorite = findViewById(R.id.btn_favorite);
        btn_download = findViewById(R.id.btn_download);
        btn_share = findViewById(R.id.btn_share);
        btn_set = findViewById(R.id.btn_set);

        txt_category_name = findViewById(R.id.category_name);
        txt_resolution = findViewById(R.id.txt_resolution);
        txt_view_count = findViewById(R.id.txt_view_count);
        txt_download_count = findViewById(R.id.txt_download_count);
        img_thumb = findViewById(R.id.img_thumb);

        Intent i = getIntent();
        position = i.getIntExtra("POSITION_ID", 0);

        recyclerView_tags = findViewById(R.id.recyclerViewTags);
        lyt_tags = findViewById(R.id.lyt_tags);

        ChipsLayoutManager spanLayoutManager = ChipsLayoutManager.newBuilder(getApplicationContext()).setOrientation(ChipsLayoutManager.HORIZONTAL).build();
        recyclerView_tags.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelOffset(R.dimen.chips_space), getResources().getDimensionPixelOffset(R.dimen.chips_space)));
        recyclerView_tags.setLayoutManager(spanLayoutManager);

        loadViewed(position);

        TOTAL_IMAGE = Const.arrayList.size() - 1;
        viewpager = (ViewPager) findViewById(R.id.image_slider);
        handler = new Handler();

        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(position);

        if (tool.isNetworkAvailable()) {
            new MyTask().execute(Const.URL_VIEW_COUNT + Const.arrayList.get(position).getImage_id());
        }

        arrayListTags = new ArrayList(Arrays.asList(((WallpaperA) Const.arrayList.get(position)).getTags().split(",")));

        adapterTagsactresshot = new AdapterTagsactresshot(ImageSlideractresshot.this, arrayListTags);
        recyclerView_tags.setAdapter(adapterTagsactresshot);

        if (Const.arrayList.get(position).getTags().equals("")) {
            lyt_tags.setVisibility(View.GONE);
        } else {
            lyt_tags.setVisibility(View.VISIBLE);
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        FirstFav();

        viewpager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                position = viewpager.getCurrentItem();
                ArrayList<WallpaperA> itemPhotos = dataBaseHelp.getFavRow(Const.arrayList.get(position).getImage_id(), Const.TABLE_FAVORITE);
                if (itemPhotos.size() == 0) {
                    btn_favorite.setImageResource(R.drawable.ic_star_outline);
                } else {
                    btn_favorite.setImageResource(R.drawable.ic_star_white);
                }
                loadViewed(position);

                if (tool.isNetworkAvailable()) {
                    new MyTask().execute(Const.URL_VIEW_COUNT + Const.arrayList.get(position).getImage_id());
                }

                arrayListTags = new ArrayList(Arrays.asList(((WallpaperA) Const.arrayList.get(position)).getTags().split(",")));
                adapterTagsactresshot = new AdapterTagsactresshot(ImageSlideractresshot.this, arrayListTags);
                recyclerView_tags.setAdapter(adapterTagsactresshot);

                if (Const.arrayList.get(position).getTags().equals("")) {
                    lyt_tags.setVisibility(View.GONE);
                } else {
                    lyt_tags.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int position) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });

        initBottomSheet();

    }

    private void initBottomSheet() {

        RelativeLayout relativeLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(relativeLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        ((RelativeLayout) findViewById(R.id.lyt_expand)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

    }

    public void AddtoFav(int position) {
        dataBaseHelp.saveWallpaper(Const.arrayList.get(position), Const.TABLE_FAVORITE);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
        btn_favorite.setImageResource(R.drawable.ic_star_white);
    }

    public void RemoveFav(int position) {
        dataBaseHelp.removeFav(Const.arrayList.get(position).getImage_id());
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
        btn_favorite.setImageResource(R.drawable.ic_star_outline);
    }

    public void FirstFav() {
        int first = viewpager.getCurrentItem();
        //String Image_id = Constant.arrayList.get(first).getWallpaper_image();

        ArrayList<WallpaperA> itemPhotos = dataBaseHelp.getFavRow(Const.arrayList.get(first).getImage_id(), Const.TABLE_FAVORITE);
        if (itemPhotos.size() == 0) {
            btn_favorite.setImageResource(R.drawable.ic_star_outline);
        } else {
            btn_favorite.setImageResource(R.drawable.ic_star_white);
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        public ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return Const.arrayList.size();

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            if (ConfigerationsHot2.ENABLE_CENTER_CROP_IN_DETAIL_WALLPAPER) {

                View imageLayout = inflater.inflate(R.layout.view_pager_item_crop, container, false);
                assert imageLayout != null;
                final PhotoView imageView = imageLayout.findViewById(R.id.image);
                final ProgressBar spinner = imageLayout.findViewById(R.id.loading);

                if (Const.arrayList.get(position).getType().equals("url")) {
                    Picasso.with(ImageSlideractresshot.this)
                            .load(Const.arrayList.get(position).getImage_url().replace(" ", "%20"))
                            .placeholder(R.drawable.ic_transparent)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    spinner.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    spinner.setVisibility(View.GONE);
                                }
                            });
                } else {
                    Picasso.with(ImageSlideractresshot.this)
                            .load(ConfigerationsHot2.ADMIN_PANEL_URL + "/upload/" + Const.arrayList.get(position).getImage_upload().replace(" ", "%20"))
                            .placeholder(R.drawable.ic_transparent)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    spinner.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    spinner.setVisibility(View.GONE);
                                }
                            });
                }

                container.addView(imageLayout, 0);
                return imageLayout;

            } else {

                View imageLayout = inflater.inflate(R.layout.view_pager_item, container, false);
                assert imageLayout != null;
                final PhotoView imageView = imageLayout.findViewById(R.id.image);
                final ProgressBar spinner = imageLayout.findViewById(R.id.loading);

                if (Const.arrayList.get(position).getType().equals("url")) {
                    Picasso.with(ImageSlideractresshot.this)
                            .load(Const.arrayList.get(position).getImage_url().replace(" ", "%20"))
                            .placeholder(R.drawable.ic_transparent)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    spinner.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    spinner.setVisibility(View.GONE);
                                }
                            });
                } else {
                    Picasso.with(ImageSlideractresshot.this)
                            .load(ConfigerationsHot2.ADMIN_PANEL_URL + "/upload/" + Const.arrayList.get(position).getImage_upload().replace(" ", "%20"))
                            .placeholder(R.drawable.ic_transparent)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    spinner.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    spinner.setVisibility(View.GONE);
                                }
                            });
                }

                container.addView(imageLayout, 0);
                return imageLayout;

            }

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelerationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelerationSquareRoot >= 2) {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            if (checkImage) {
                position = viewpager.getCurrentItem();
                viewpager.setCurrentItem(position);
            } else {
                position = viewpager.getCurrentItem();
                position++;
                if (position == TOTAL_IMAGE) {
                    position = TOTAL_IMAGE;
                }
                viewpager.setCurrentItem(position);
            }
            checkImage = !checkImage;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadViewed(position);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(Update);
        sensorManager.unregisterListener(this);
    }

    public class ShareTask extends AsyncTask<String, String, String> {
        private Context context;
        private ProgressDialog pDialog;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        public ShareTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.msg_please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            try {

                myFileUrl = new URL(args[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/" + getResources().getString(R.string.saved_folder_name) + "/");
                dir.mkdirs();
                String fileName = idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                    startActivity(Intent.createChooser(share, "Share Image"));
                    pDialog.dismiss();
                }
            }, Const.DELAY_SET_WALLPAPER);

        }
    }

    public class SetWallpaperFromOtherApp extends AsyncTask<String, String, String> {

        private Context context;
        private ProgressDialog pDialog;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        public SetWallpaperFromOtherApp(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.msg_please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            try {
                myFileUrl = new URL(args[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/" + getResources().getString(R.string.saved_folder_name) + "/");
                dir.mkdirs();
                String fileName = idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(CompressFormat.JPEG, 99, fos);
                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "image/jpeg");
                    intent.putExtra("mimeType", "image/jpeg");
                    startActivity(Intent.createChooser(intent, "Set as:"));
                    pDialog.dismiss();
                }
            }, Const.DELAY_SET_WALLPAPER);
        }
    }

    private void loadViewed(final int position) {

        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSlideractresshot.this.position = viewpager.getCurrentItem();
                image_id = Const.arrayList.get(ImageSlideractresshot.this.position).getImage_id();
                ArrayList<WallpaperA> itemPhotos = dataBaseHelp.getFavRow(image_id, Const.TABLE_FAVORITE);
                if (itemPhotos.size() == 0) {
                    AddtoFav(ImageSlideractresshot.this.position);
                } else {
                    if (itemPhotos.get(0).getImage_id().equals(image_id)) {
                        RemoveFav(ImageSlideractresshot.this.position);
                    }
                }
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tool.isNetworkAvailable()) {
                    sharePermission(position);
                } else {
                    Toast.makeText(ImageSlideractresshot.this, R.string.alert_share, Toast.LENGTH_SHORT).show();
                }
            }
        });

        txt_category_name.setText(Const.arrayList.get(position).getCategory_name());
        txt_view_count.setText(Tool.withSuffix(Const.arrayList.get(position).getView_count()) + "");
        txt_download_count.setText(Tool.withSuffix(Const.arrayList.get(position).getDownload_count()) + "");

        if (Const.arrayList.get(position).getType().equals("url")) {
            Picasso.with(ImageSlideractresshot.this)
                    .load(Const.arrayList.get(position).getImage_url())
                    .placeholder(R.drawable.ic_transparent)
                    .into(img_thumb, new Callback() {
                        @Override
                        public void onSuccess() {

                            Bitmap resolution = ((BitmapDrawable) img_thumb.getDrawable()).getBitmap();
                            int width = resolution.getWidth();
                            int height = resolution.getHeight();
                            txt_resolution.setText(width + " x " + height);

                            btn_set.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (ConfigerationsHot2.ENABLE_SET_WALLPAPER_WITH_OTHER_APPS) {
                                        setWallpaperPermission(position);
                                    } else {
                                        PopupMenu popup = new PopupMenu(v.getContext(), v);
                                        popup.inflate(R.menu.menupopup);
                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            public boolean onMenuItemClick(MenuItem item) {
                                                final int itemId = item.getItemId();
                                                switch (itemId) {
                                                    case R.id.option_apply_now:
                                                        if (Build.VERSION.SDK_INT >= 24) {
                                                            dialogSetWallpaperOption(position);
                                                        } else {
                                                            dialogSetWallpaper();
                                                        }
                                                        return true;
                                                    case R.id.option_crop_wallpaper:
                                                        Intent intent = new Intent(getApplicationContext(), SetWallpaperactresshot.class);
                                                        intent.putExtra("WALLPAPER_IMAGE_URL", Const.arrayList.get(position).getImage_url());
                                                        startActivity(intent);
                                                        return true;
                                                    default:
                                                        return false;
                                                }
                                            }
                                        });
                                        Object menuHelper;
                                        Class[] argTypes;
                                        try {
                                            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                                            fMenuHelper.setAccessible(true);
                                            menuHelper = fMenuHelper.get(popup);
                                            argTypes = new Class[]{boolean.class};
                                            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                                        } catch (Exception e) {

                                        }
                                        popup.show();
                                    }

                                }
                            });

                            btn_download.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showInterstitialAd();
                                    if (tool.isNetworkAvailable()) {

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(ImageSlideractresshot.this);
                                        dialog.setMessage(R.string.msg_confirm_download);
                                        dialog.setPositiveButton(R.string.dialog_option_yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Tool.download(ImageSlideractresshot.this, Const.arrayList.get(position).getImage_url());
                                                new MyTask().execute(Const.URL_DOWNLOAD_COUNT + Const.arrayList.get(position).getImage_id());
                                                showInterstitialAd();
                                            }
                                        });
                                        dialog.setNegativeButton(R.string.dialog_option_cancel, null);
                                        dialog.show();
                                        showInterstitialAd();

                                    } else {
                                        showInterstitialAd();
                                        Toast.makeText(ImageSlideractresshot.this, R.string.alert_download, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onError() {
                        }
                    });
        } else {
            Picasso.with(ImageSlideractresshot.this)
                    .load(ConfigerationsHot2.ADMIN_PANEL_URL + "/upload/" + Const.arrayList.get(position).getImage_upload())
                    .placeholder(R.drawable.ic_transparent)
                    .into(img_thumb, new Callback() {
                        @Override
                        public void onSuccess() {

                            Bitmap resolution = ((BitmapDrawable) img_thumb.getDrawable()).getBitmap();
                            int width = resolution.getWidth();
                            int height = resolution.getHeight();
                            txt_resolution.setText(width + " x " + height);

                            btn_set.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (ConfigerationsHot2.ENABLE_SET_WALLPAPER_WITH_OTHER_APPS) {
                                        setWallpaperPermission(position);
                                    } else {
                                        PopupMenu popup = new PopupMenu(v.getContext(), v);
                                        popup.inflate(R.menu.menupopup);
                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            public boolean onMenuItemClick(MenuItem item) {
                                                final int itemId = item.getItemId();
                                                switch (itemId) {
                                                    case R.id.option_apply_now:
                                                        if (Build.VERSION.SDK_INT >= 24) {
                                                            dialogSetWallpaperOption(position);
                                                        } else {
                                                            dialogSetWallpaper();
                                                        }
                                                        return true;
                                                    case R.id.option_crop_wallpaper:
                                                        Intent intent = new Intent(getApplicationContext(), SetWallpaperactresshot.class);
                                                        intent.putExtra("WALLPAPER_IMAGE_URL", ConfigerationsHot2.ADMIN_PANEL_URL + "/upload/" + Const.arrayList.get(position).getImage_upload());
                                                        startActivity(intent);
                                                        return true;
                                                    default:
                                                        return false;
                                                }
                                            }
                                        });
                                        Object menuHelper;
                                        Class[] argTypes;
                                        try {
                                            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                                            fMenuHelper.setAccessible(true);
                                            menuHelper = fMenuHelper.get(popup);
                                            argTypes = new Class[]{boolean.class};
                                            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                                        } catch (Exception e) {

                                        }
                                        popup.show();
                                    }

                                }
                            });

                            btn_download.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (tool.isNetworkAvailable()) {

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(ImageSlideractresshot.this);
                                        dialog.setMessage(R.string.msg_confirm_download);
                                        dialog.setPositiveButton(R.string.dialog_option_yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Tool.download(ImageSlideractresshot.this, ConfigerationsHot2.ADMIN_PANEL_URL + "/upload/" + Const.arrayList.get(position).getImage_upload());
                                                new MyTask().execute(Const.URL_DOWNLOAD_COUNT + Const.arrayList.get(position).getImage_id());
                                            }
                                        });
                                        dialog.setNegativeButton(R.string.dialog_option_cancel, null);
                                        dialog.show();

                                    } else {
                                        Toast.makeText(ImageSlideractresshot.this, R.string.alert_download, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onError() {
                        }
                    });
        }

    }

    private static class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return Tool.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                Log.d("TAG", "no data found!");
            } else {

                try {

                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray("result");
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }

    public void dialogSetWallpaperOption(final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(ImageSlideractresshot.this);
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_set_wallpaper, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(ImageSlideractresshot.this);
        alert.setView(view);
        final MaterialRippleLayout btn_set_home_screen = view.findViewById(R.id.menu_set_home_screen);
        final MaterialRippleLayout btn_set_lock_screen = view.findViewById(R.id.menu_set_lock_screen);
        final MaterialRippleLayout btn_set_both = view.findViewById(R.id.menu_set_both);
        final LinearLayout lyt_root = view.findViewById(R.id.custom_dialog_layout_design_user_input);
        final LinearLayout lyt_option = view.findViewById(R.id.lyt_option);
        final LinearLayout lyt_progress = view.findViewById(R.id.lyt_progress);

        final AlertDialog alertDialog = alert.create();

        btn_set_home_screen.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                alertDialog.setCancelable(false);
                lyt_option.setVisibility(View.GONE);
                lyt_progress.setVisibility(View.VISIBLE);

                try {
                    Bitmap bitmap = ((BitmapDrawable) img_thumb.getDrawable()).getBitmap();
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(ImageSlideractresshot.this);
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            lyt_progress.setVisibility(View.GONE);
                            showInterstitialAd();
                        }
                    }, Const.DELAY_SET_WALLPAPER);

                } catch (IOException e) {
                    Tool.printStackTrace(e);
                    alertDialog.dismiss();
                    lyt_progress.setVisibility(View.GONE);
                    Toast.makeText(ImageSlideractresshot.this, R.string.msg_failed, Toast.LENGTH_SHORT).show();
                    Log.v("ERROR", "Wallpaper not set");
                }

            }
        });

        btn_set_lock_screen.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                alertDialog.setCancelable(false);
                lyt_option.setVisibility(View.GONE);
                lyt_progress.setVisibility(View.VISIBLE);

                try {
                    Bitmap bitmap = ((BitmapDrawable) img_thumb.getDrawable()).getBitmap();

                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(ImageSlideractresshot.this);
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            lyt_progress.setVisibility(View.GONE);
                            showInterstitialAd();
                        }
                    }, Const.DELAY_SET_WALLPAPER);

                } catch (IOException e) {
                    Tool.printStackTrace(e);
                    alertDialog.dismiss();
                    lyt_progress.setVisibility(View.GONE);
                    Toast.makeText(ImageSlideractresshot.this, R.string.msg_failed, Toast.LENGTH_SHORT).show();
                    Log.v("ERROR", "Wallpaper not set");
                }

            }
        });

        btn_set_both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.setCancelable(false);
                lyt_option.setVisibility(View.GONE);
                lyt_progress.setVisibility(View.VISIBLE);

                try {
                    Bitmap bitmap = ((BitmapDrawable) img_thumb.getDrawable()).getBitmap();
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(ImageSlideractresshot.this);
                    wallpaperManager.setBitmap(bitmap);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            lyt_progress.setVisibility(View.GONE);
                            showInterstitialAd();
                        }
                    }, Const.DELAY_SET_WALLPAPER);

                } catch (IOException e) {
                    Tool.printStackTrace(e);
                    alertDialog.dismiss();
                    lyt_progress.setVisibility(View.GONE);
                    Toast.makeText(ImageSlideractresshot.this, R.string.msg_failed, Toast.LENGTH_SHORT).show();
                    Log.v("ERROR", "Wallpaper not set");
                }

            }
        });

        alertDialog.show();
    }

    public void dialogSetWallpaper() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageSlideractresshot.this);
        builder.setMessage(R.string.msg_title_set_wallpaper)
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.option_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Bitmap bitmap = ((BitmapDrawable) img_thumb.getDrawable()).getBitmap();
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(ImageSlideractresshot.this);
                            wallpaperManager.setBitmap(bitmap);

                            final ProgressDialog progressDialog = new ProgressDialog(ImageSlideractresshot.this);
                            progressDialog.setMessage(getResources().getString(R.string.msg_apply_wallpaper));
                            progressDialog.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    showInterstitialAd();
                                }
                            }, Const.DELAY_SET_WALLPAPER);
                        } catch (IOException e) {
                            Tool.printStackTrace(e);
                            progressDialog.dismiss();
                            Toast.makeText(ImageSlideractresshot.this, R.string.msg_failed, Toast.LENGTH_SHORT).show();
                            Log.v("ERROR", "Wallpaper not set");
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void sharePermission(final int position) {
        Dexter.withActivity(ImageSlideractresshot.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (Const.arrayList.get(position).getType().equals("url")) {
                                (new ShareTask(ImageSlideractresshot.this)).execute(Const.arrayList.get(position).getImage_url());
                            } else {
                                (new ShareTask(ImageSlideractresshot.this)).execute(ConfigerationsHot2.ADMIN_PANEL_URL + "/upload/" + Const.arrayList.get(position).getImage_upload());
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void setWallpaperPermission(final int position) {
        Dexter.withActivity(ImageSlideractresshot.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (Const.arrayList.get(position).getType().equals("url")) {
                                (new SetWallpaperFromOtherApp(ImageSlideractresshot.this)).execute(Const.arrayList.get(position).getImage_url());
                            } else {
                                (new SetWallpaperFromOtherApp(ImageSlideractresshot.this)).execute(ConfigerationsHot2.ADMIN_PANEL_URL + "/upload/" + Const.arrayList.get(position).getImage_upload());
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageSlideractresshot.this);
        builder.setTitle(R.string.permisson_title);
        builder.setMessage(R.string.permisson_message);
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    private void showInterstitialAd(){
        //admob

        if (mInterstitialAd != null) {
            mInterstitialAd.show(ImageSlideractresshot.this);
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


}
