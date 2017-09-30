package com.pgyt.myapp_2;

class CommonConstants {
    // 引数
    static final String ARG_TITLE = "title";
    static final String ARG_MESSAGE = "message";
    static final String ARG_EDIT_TITLE_1 = "editTextTitle1";
    static final String ARG_EDIT_TITLE_2 = "editTextTitle2";
    static final String ARG_PATTERN = "pattern";
    static final String ARG_SECTION_NUMBER = "section_number";
    static final String ARG_TITLE_NAME = "title_name";

    // ダイアログ
    static final String PETTERN_DIALOG_CONFIRM = "0";
    static final String PETTERN_DIALOG_EDIT_1 = "1";
    static final String PETTERN_DIALOG_EDIT_2 = "2";

    // DB
    static final int DB_VERSION = 1;
    static final String DB_NAME = "android_sqlite";
    static final String TABLE_NAME_CATEGORY = "CATEGORY";
    static final String TABLE_NAME_CONTENTS = "CONTENTS";
    static final String COLUMN_ID = "id";
    static final String COLUMN_CATEGORY_NAME = "category_name";
    static final String COLUMN_CONTENTS_TITLE = "contents_title";
    static final String COLUMN_CONTENTS = "contents";


    // 共通
    static final boolean CHECK_VISIBLE_FLG_ON = true;
    static final boolean CHECK_VISIBLE_FLG_OFF = false;
    static final int CLIPBOARD_TAB_POSITON = 0;
    static final int REQUEST_CODE_EDIT_CONTENTS = 1001;
    static final int REQUEST_CODE_SETTING = 1002;
    static final int MAX_ROWSIZE_DEFAULT = 50;
    static final int MAX_ROWSIZE_MAXIMUM = 100;
    static final String BLANK_STRING = "";
    static final String CLIPBOARD_TAB_NAME = "CLIPBOARD";
    static final String CLIP_BOARD_TITLE_NAME = "DummyContentsTitle";
    static final String STATUS_BAR_TITLE = "Current ClipBoard";
    static final int NOTIFICATION_ID = 10;

}
