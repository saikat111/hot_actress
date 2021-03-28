package com.codingburg.actresshot.pic.UtilHot2;

import com.codingburg.actresshot.pic.ConfigerationsHot2;
import com.codingburg.actresshot.pic.ModelHot2.WallpaperA;

import java.io.Serializable;
import java.util.ArrayList;

public class Const implements Serializable {

    public static final String URL_CATEGORY = ConfigerationsHot2.ADMIN_PANEL_URL + "/api/api.php?action=get_category";
    public static final String URL_CATEGORY_DETAIL = ConfigerationsHot2.ADMIN_PANEL_URL + "/api/api.php?action=get_category_detail";
    public static final String URL_RECENT_WALLPAPER = ConfigerationsHot2.ADMIN_PANEL_URL + "/api/api.php?action=get_recent&offset=";
    public static final String URL_POPULAR_WALLPAPER = ConfigerationsHot2.ADMIN_PANEL_URL + "/api/api.php?action=get_popular&offset=";
    public static final String URL_RANDOM_WALLPAPER = ConfigerationsHot2.ADMIN_PANEL_URL + "/api/api.php?action=get_random&offset=";
    public static final String URL_FEATURED_WALLPAPER = ConfigerationsHot2.ADMIN_PANEL_URL + "/api/api.php?action=get_featured&offset=";
    public static final String URL_SEARCH_WALLPAPER = ConfigerationsHot2.ADMIN_PANEL_URL + "/api/api.php?action=get_search";
    public static final String URL_PRIVACY_POLICY = ConfigerationsHot2.ADMIN_PANEL_URL + "/api/api.php?action=get_privacy_policy";
    public static final String URL_VIEW_COUNT = ConfigerationsHot2.ADMIN_PANEL_URL + "/api/api.php?action=view_count&id=";
    public static final String URL_DOWNLOAD_COUNT = ConfigerationsHot2.ADMIN_PANEL_URL + "/api/api.php?action=download_count&id=";

    public static final String TABLE_CATEGORY = "tbl_category";
    public static final String TABLE_CATEGORY_DETAIL = "tbl_category_detail";
    public static final String TABLE_FAVORITE = "tbl_favorite";
    public static final String TABLE_RECENT = "tbl_recent";
    public static final String TABLE_POPULAR = "tbl_popular";
    public static final String TABLE_RANDOM = "tbl_random";
    public static final String TABLE_FEATURED = "tbl_featured";

    public static final String NO = "no";
    public static final String IMAGE_ID = "image_id";
    public static final String IMAGE_UPLOAD = "image_upload";
    public static final String IMAGE_URL = "image_url";
    public static final String TYPE = "type";
    public static final String VIEW_COUNT = "view_count";
    public static final String DOWNLOAD_COUNT = "download_count";
    public static final String FEATURED = "featured";
    public static final String TAGS = "tags";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_IMAGE = "category_image";
    public static final String TOTAL_WALLPAPER = "total_wallpaper";

    public static ArrayList<WallpaperA> arrayList = new ArrayList<WallpaperA>();
    public static final int DELAY_PROGRESS = 200;
    public static final int DELAY_REFRESH = 1000;
    public static final int DELAY_LOAD_MORE = 1500;
    public static final int DELAY_SET_WALLPAPER = 2000;

}
