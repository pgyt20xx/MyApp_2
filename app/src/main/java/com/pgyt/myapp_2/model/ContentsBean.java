package com.pgyt.myapp_2.model;


import android.content.ContentValues;

public class ContentsBean
{
	
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
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * idを設定
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * カテゴリー名を取得
     * @return
     */
    public String getCategory_name() {
        return category_name;
    }

    /**
     * カテゴリー名を設定
     * @param category_name
     */
    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    /**
     * コンテンツタイトルを取得
     * @return
     */
    public String getContents_title() {
        return contents_title;
    }

    /**
     * コンテンツタイトルを設定
     * @param contents_title
     */
    public void setContents_title(String contents_title) {
        this.contents_title = contents_title;
    }

    /**
     * コンテンツを取得
     * @return
     */
    public String getContents() {
        return contents;
    }

    /**
     * コンテンツを設定
     * @param contents
     */
    public void setContents(String contents) {
        this.contents = contents;
    }

    /**
     * ContentValuesに値を設定して返却
     * @return
     */
    public ContentValues getParams(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("category_name", getCategory_name());
        contentValues.put("contents_title", getContents_title());
        contentValues.put("contents", getContents());
        return contentValues;
    }
}
