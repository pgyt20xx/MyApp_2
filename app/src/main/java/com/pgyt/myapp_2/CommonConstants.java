package com.pgyt.myapp_2;

/**
 * Created by yuich on 2017/09/20.
 */

public class CommonConstants {
    // 引数
    public static final String ARG_TITLE = "title";
    public static final String ARG_MESSAGE = "message";
    public static final String ARG_EDIT_TITLE_1 = "editTextTitle1";
    public static final String ARG_EDIT_TITLE_2 = "editTextTitle2";
    public static final String ARG_PATTERN = "pattern";
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_TITLE_NAME = "title_name";

    // ダイアログ
    public static final String PETTERN_DIALOG_CONFIRM = "0";
    public static final String PETTERN_DIALOG_EDIT_1 = "1";
    public static final String PETTERN_DIALOG_EDIT_2 = "2";

    // DB
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "android_sqlite";
    public static final String TABLE_NAME_CATEGORY = "CATEGORY";
    public static final String TABLE_NAME_CONTENTS = "CONTENTS";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_CONTENTS_TITLE = "contents_title";
    public static final String COLUMN_CONTENTS = "contents";


    // 共通
    public static final boolean CHECK_VISIBLE_FLG_ON = true;
    public static final boolean CHECK_VISIBLE_FLG_OFF = false;
    public static final int CLIPBOARD_TAB_POSITON = 0;
    public static final int REQUEST_CODE_EDIT_CONTENTS = 1001;
    public static final int REQUEST_CODE_SETTING = 1002;
    public static final int MAX_ROWSIZE_DEFAULT = 50;
    public static final int MAX_ROWSIZE_MAXIMUM = 100;
    public static final String BLANK_STRING = "";
    public static final String CLIPBOARD_TAB_NAME = "CLIPBOARD";
    public static final String CLIP_BOARD_TITLE_NAME = "DummyContentsTitle";
    public static final String STATUS_BAR_TITLE = "Current ClipBoard";
    public static final int NOTIFICATION_ID = 10;

}
