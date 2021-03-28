package com.codingburg.actresshot.pic.ActivitieHot2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.R;
import com.codingburg.actresshot.pic.UtilHot2.Const;
import com.codingburg.actresshot.pic.UtilHot2.SharedP;
import com.balysv.materialripple.MaterialRippleLayout;
import com.onesignal.OneSignal;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;

public class Settingsactresshot extends AppCompatActivity {

    ImageView btn_clear_cache;
    TextView txt_cache_size;
    Switch snotification;
    MaterialRippleLayout lprivacy_policy;
    Boolean isNo = true;
    SharedP sharedP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        if (ConfigerationsHot2.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.drawer_settings);
        }

        sharedP = new SharedP(this);
        isNo = sharedP.getIsNotification();

        snotification = findViewById(R.id.switch_notif);

        if (isNo) {
            snotification.setChecked(true);
        } else {
            snotification.setChecked(false);
        }

        snotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("INFO", "" + isChecked);
                OneSignal.setSubscription(isChecked);
                sharedP.setIsNotification(isChecked);
            }
        });

        txt_cache_size = findViewById(R.id.txt_cache_size);
        initializeCache();

        btn_clear_cache = findViewById(R.id.btn_clear_cache);
        btn_clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCache();
            }
        });

        lprivacy_policy = findViewById(R.id.lyt_privacy_policy);
        getPrivacyPolicy();
    }

    private void clearCache() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Settingsactresshot.this);
        dialog.setMessage(R.string.msg_clear_cache);
        dialog.setPositiveButton(R.string.dialog_option_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                FileUtils.deleteQuietly(getCacheDir());
                FileUtils.deleteQuietly(getExternalCacheDir());

                final ProgressDialog progressDialog = new ProgressDialog(Settingsactresshot.this);
                progressDialog.setTitle(R.string.msg_clearing_cache);
                progressDialog.setMessage(getString(R.string.msg_please_wait));
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txt_cache_size.setText("0 Bytes");
                        Toast.makeText(Settingsactresshot.this, getString(R.string.msg_cache_cleared), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }, 3000);

            }
        });
        dialog.setNegativeButton(R.string.dialog_option_cancel, null);
        dialog.show();
    }

    private void initializeCache() {
        this.txt_cache_size.setText(readableFileSize((0 + getDirSize(getCacheDir())) + getDirSize(getExternalCacheDir())));
    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0 Bytes";
        }
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10((double) size) / Math.log10(1024.0d));
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double d = (double) size;
        double pow = Math.pow(1024.0d, (double) digitGroups);
        Double.isNaN(d);
        stringBuilder.append(decimalFormat.format(d / pow));
        stringBuilder.append(" ");
        stringBuilder.append(units[digitGroups]);
        return stringBuilder.toString();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getPrivacyPolicy() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Const.URL_PRIVACY_POLICY, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    final String result = response.getString("privacy_policy");

                    lprivacy_policy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(Settingsactresshot.this);
                            dialog.setTitle(R.string.menu_settings_privacy_policy);
                            dialog.setMessage(Html.fromHtml(result));
                            dialog.setPositiveButton(R.string.dialog_option_ok, null);
                            dialog.show();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MyAppactresshot.getInstance().addToRequestQueue(jsonObjReq);
    }

}
