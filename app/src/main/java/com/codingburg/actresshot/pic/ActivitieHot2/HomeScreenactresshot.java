package com.codingburg.actresshot.pic.ActivitieHot2;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.FragmentHot2.FClassicactresshot;
import com.codingburg.actresshot.pic.FragmentHot2.FExploreactresshot;
import com.codingburg.actresshot.pic.FragmentHot2.FragmentFavoriteactresshot;
import com.codingburg.actresshot.pic.UtilHot2.DataBaseHelp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

/*import com.facebook.ads.*;*/
import java.io.IOException;
import java.util.List;

public class HomeScreenactresshot extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static String COLLAPSING_TOOLBAR_FRAGMENT_TAG = "collapsing_toolbar";
    private final static String CATEGORY_FRAGMENT_TAG = "category";
    private final static String FAVORITE_FRAGMENT_TAG = "favorite";
    private final static String RATE_FRAGMENT_TAG = "rate";
    private final static String MORE_FRAGMENT_TAG = "more";
    private final static String ABOUT_FRAGMENT_TAG = "about";
    private final static String SELECTED_TAG = "selected_index";
    private final static int COLLAPSING_TOOLBAR = 0;
    private final static int CATEGORY = 1;
    private final static int FAVORITE = 2;
    private final static int RATE = 3;
    private final static int MORE = 4;
    private final static int SHARE = 5;
    private final static int ABOUT = 6;
    private static int selectedIndex;
    static final String TAG = "MainActivity";
    private NavigationView navView;
    private DrawerLayout dLayout;
    private ActionBarDrawerToggle acBarDrT;
    private AdView adView;
    private static final int REQUEST = 112;
    DataBaseHelp databaseHelp;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        if (ConfigerationsHot2.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        //admob

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        databaseHelp = new DataBaseHelp(HomeScreenactresshot.this);
        try {
            databaseHelp.createDataBase();
            Log.d("Database", "Database created");
        } catch (IOException e) {
            e.printStackTrace();
        }

       /* AudienceNetworkAds.initialize(this);

        adView = new AdView(this, getString(R.string.facebook_banner_ads), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
*/

        navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(this);
        dLayout = findViewById(R.id.drawer_layout);


        View header = navView.getHeaderView(0);
        LinearLayout linearLayout = header.findViewById(R.id.lyt_drawer_info);
        if (ConfigerationsHot2.ENABLE_NAVIGATION_DRAWER_HEADER_INFO) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }

        if (savedInstanceState != null) {
            navView.getMenu().getItem(savedInstanceState.getInt(SELECTED_TAG)).setChecked(true);
            return;
        }

        selectedIndex = COLLAPSING_TOOLBAR;

        if (ConfigerationsHot2.ENABLE_CLASSIC_MODE) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    new FClassicactresshot(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    new FExploreactresshot(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
        }

        //requestStoragePermission();


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAG, selectedIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.drawer_explore:
                if (!menuItem.isChecked()) {
                    selectedIndex = COLLAPSING_TOOLBAR;
                    menuItem.setChecked(true);
                    if (ConfigerationsHot2.ENABLE_CLASSIC_MODE) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new FClassicactresshot(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new FExploreactresshot(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
                    }
                }
                dLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_favorite:
                if (!menuItem.isChecked()) {
                    selectedIndex = FAVORITE;
                    menuItem.setChecked(true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new FragmentFavoriteactresshot(), FAVORITE_FRAGMENT_TAG).commit();
                }
                dLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_rate:
                if (!menuItem.isChecked()) {
                    selectedIndex = RATE;
                    menuItem.setChecked(true);

                    final String appName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    }
                }
                dLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_more:
                if (!menuItem.isChecked()) {
                    selectedIndex = MORE;
                    menuItem.setChecked(true);

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));

                }
                dLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_share:
                if (!menuItem.isChecked()) {
                    selectedIndex = SHARE;
                    menuItem.setChecked(true);

                    Intent sendInt = new Intent(Intent.ACTION_SEND);
                    sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    sendInt.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
                    sendInt.setType("text/plain");
                    startActivity(Intent.createChooser(sendInt, "Share"));

                }
                dLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_settings:
                startActivity(new Intent(getApplicationContext(), Settingsactresshot.class));
                dLayout.closeDrawer(GravityCompat.START);
                return true;

        }
        return false;
    }


    public void setupNavigationDrawer(Toolbar toolbar) {
        acBarDrT = new ActionBarDrawerToggle(this, dLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        dLayout.addDrawerListener(acBarDrT);
        acBarDrT.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (ConfigerationsHot2.ENABLE_EXIT_DIALOG) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(HomeScreenactresshot.this);
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setTitle(R.string.app_name);
                dialog.setMessage(R.string.dialog_close_msg);
                dialog.setPositiveButton(R.string.dialog_option_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HomeScreenactresshot.this.finish();
                    }
                });

                dialog.setNegativeButton(R.string.dialog_option_rate_us, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String appName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                        }

                        HomeScreenactresshot.this.finish();
                    }
                });

                dialog.setNeutralButton(R.string.dialog_option_more, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
                        HomeScreenactresshot.this.finish();
                    }
                });
                dialog.show();

            } else {
                super.onBackPressed();
            }
        }
    }


    @TargetApi(16)
    private void requestStoragePermission() {
        Dexter.withActivity(HomeScreenactresshot.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Log.d("Log", "permission granted");
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            settingsDialog();
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

    private void settingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenactresshot.this);
        builder.setTitle(R.string.permisson_title);
        builder.setMessage(R.string.permisson_message);
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                opSet();
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

    private void opSet() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}
