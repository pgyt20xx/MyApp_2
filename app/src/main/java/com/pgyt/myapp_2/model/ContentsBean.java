package com.pgyt.myapp_2.model;


import android.content.ContentValues;

import java.io.Serializable;

public class ContentsBean implements Serializable
{


    /**
     * チェックボックス表示フラグ
     * false:表示しない
     * true:表示する
     */
    private boolean checkBoxVisibleFlg;

    /**
     * チェックフラグ
     * false:チェックなし
     * true:チェックあり
     */
    private boolean checkedFlg;
	
	/**
     * id
     */
    private int id;

    /**
     * カテゴリー名
     */
    private String category_name;

    /**
     * コンテンツタイトル
     */
    private String contents_title;

    /**
     * コンテンツ
     */
    private String contents;

    /**
     * idを取得
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * idを設定
     * @param id int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * カテゴリー名を取得
     * @return String
     */
    public String getCategory_name() {
        return category_name;
    }

    /**
     * カテゴリー名を設定
     * @param category_name String
     */
    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    /**
     * コンテンツタイトルを取得
     * @return String
     */
    public String getContents_title() {
        return contents_title;
    }

    /**
     * コンテンツタイトルを設定
     * @param contents_title String
     */
    public void setContents_title(String contents_title) {
        this.contents_title = contents_title;
    }

    /**
     * コンテンツを取得
     * @return String
     */
    public String getContents() {
        return contents;
    }

    /**
     * コンテンツを設定
     * @param contents String
     */
    public void setContents(String contents) {
        this.contents = contents;
    }

    /**
     * チェックフラグを取得
     * @return boolean
     */
    public boolean getCheckBoxVisibleFlg() {
        return checkBoxVisibleFlg;
    }

    /**
     * チェックフラグを設定
     * @param checkBoxVisibleFlg boolean
     */
    public void setCheckBoxVisibleFlg(boolean checkBoxVisibleFlg) {
        this.checkBoxVisibleFlg = checkBoxVisibleFlg;
    }

    /**
     * チェックフラグを取得
     * @return boolean
     */
    public boolean getCheckedFlg() {
        return checkedFlg;
    }

    /**
     * チェックフラグを設定
     * @param checkedFlg boolean
     */
    public void setCheckedFlg(boolean checkedFlg) {
        this.checkedFlg = checkedFlg;
    }

    /**
     * ContentValuesに値を設定して返却
     * @return ContentValues
     */
    public ContentValues getParams(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("category_name", getCategory_name());
        contentValues.put("contents_title", getContents_title());
        contentValues.put("contents", getContents());
        return contentValues;
    }
}
