package com.codingburg.actresshot.pic.ActivitieHot2;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.UtilHot2.Const;
import com.codingburg.actresshot.pic.UtilHot2.GDPR;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class SetWallpaperactresshot extends AppCompatActivity {

    private CropImageView mCropImageView;
    String str_image;
    Toolbar toolbar;
    Bitmap bitmap = null;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setas);

        if (ConfigerationsHot2.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        loadInterstitialAd();

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

        Intent i = getIntent();
        str_image = i.getStringExtra("WALLPAPER_IMAGE_URL");

        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        ImageLoader.getInstance().loadImage(str_image, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                mCropImageView.setImageBitmap(arg2);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_set:
                if (Build.VERSION.SDK_INT >= 24) {
                    dialogSetWallpaperOption();
                } else {
                    dialogSetWallpaper();
                }
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }


    public void dialogSetWallpaperOption() {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(SetWallpaperactresshot.this);
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_set_wallpaper, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(SetWallpaperactresshot.this);
        alert.setView(view);

        final MaterialRippleLayout btn_set_home_screen = view.findViewById(R.id.menu_set_home_screen);
        final MaterialRippleLayout btn_set_lock_screen = view.findViewById(R.id.menu_set_lock_screen);
        final MaterialRippleLayout btn_set_both = view.findViewById(R.id.menu_set_both);
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

                bitmap = mCropImageView.getCroppedImage();
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                        lyt_progress.setVisibility(View.GONE);
                        showInterstitialAd();
                    }
                }, Const.DELAY_SET_WALLPAPER);

                try {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                } catch (IOException e) {
                    e.printStackTrace();
                    alertDialog.dismiss();
                    lyt_progress.setVisibility(View.GONE);
                    Toast.makeText(SetWallpaperactresshot.this, R.string.msg_failed, Toast.LENGTH_SHORT).show();
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

                bitmap = mCropImageView.getCroppedImage();
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                        lyt_progress.setVisibility(View.GONE);
                        showInterstitialAd();
                    }
                }, Const.DELAY_SET_WALLPAPER);

                try {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                } catch (IOException e) {
                    e.printStackTrace();
                    alertDialog.dismiss();
                    lyt_progress.setVisibility(View.GONE);
                    Toast.makeText(SetWallpaperactresshot.this, R.string.msg_failed, Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_set_both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.setCancelable(false);
                lyt_option.setVisibility(View.GONE);
                lyt_progress.setVisibility(View.VISIBLE);

                bitmap = mCropImageView.getCroppedImage();
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                        lyt_progress.setVisibility(View.GONE);
                        showInterstitialAd();
                    }
                }, Const.DELAY_SET_WALLPAPER);

                try {
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    alertDialog.dismiss();
                    lyt_progress.setVisibility(View.GONE);
                    Toast.makeText(SetWallpaperactresshot.this, R.string.msg_failed, Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialog.show();
    }

    public void dialogSetWallpaper() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetWallpaperactresshot.this);
        builder.setMessage(R.string.msg_title_set_wallpaper)
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.option_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            bitmap = mCropImageView.getCroppedImage();
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                            wallpaperManager.setBitmap(bitmap);

                            final ProgressDialog progressDialog = new ProgressDialog(SetWallpaperactresshot.this);
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
                            e.printStackTrace();
                            Toast.makeText(SetWallpaperactresshot.this, R.string.msg_failed, Toast.LENGTH_SHORT).show();
                            Log.v("ERROR", "Wallpaper not set");
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
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



    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.admob_interstitial_unit_id), adRequest, new InterstitialAdLoadCallback() {
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
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(SetWallpaperactresshot.this);
        } else {
            closeApp();
        }
    }


    private void closeApp() {
        Intent intent = new Intent(SetWallpaperactresshot.this, HomeScreenactresshot.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        Toast.makeText(SetWallpaperactresshot.this, R.string.msg_success, Toast.LENGTH_SHORT).show();
    }

}