package com.pgyt.myapp_2;

/**
 * CommonConstants
 */
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
    static final String PATTERN_DIALOG_CONFIRM = "0";
    static final String PATTERN_DIALOG_EDIT_1 = "1";
    static final String PATTERN_DIALOG_EDIT_2 = "2";

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
    static final int CLIPBOARD_TAB_POSITION = 0;
    static final int REQUEST_IO_PERMISSION = 1000;
    static final int REQUEST_CODE_EDIT_CONTENTS = 1001;
    static final int REQUEST_CODE_SETTING = 1002;
    static final int REQUEST_CODE_CHOSE_FILE = 1003;
    static final int MAX_ROW_SIZE_DEFAULT = 20;
    static final int MAX_ROW_SIZE_MAXIMUM = 50;
    static final String BLANK_STRING = "";
    static final String CLIPBOARD_TAB_NAME = "CLIPBOARD";
    static final String CLIP_BOARD_TITLE_NAME = "ClipBoard";
    static final String STATUS_BAR_TITLE = "Current ClipBoard";
    static final String CSV_EXPORT = "1";
    static final String CSV_IMPORT = "2";
    static final int NOTIFICATION_ID = 10;
    static final String ESCAPE_DOUBLE_QUOTE = "\"";
    static final String BRACE_LEFT = "{";
    static final String BRACE_RIGHT = "}";
    static final String COLON = ":";
    static final String COMMA = ",";
    static final String EXPORT_SUCCESS = "export is succeed";
    static final String IMPORT_SUCCESS = "import is succeed";
    static final String EXPORT_FAILURE = "export is failed";
    static final String IMPORT_FAILURE = "import is failed";

}
