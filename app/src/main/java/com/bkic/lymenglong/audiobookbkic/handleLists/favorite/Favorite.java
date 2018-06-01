package com.bkic.lymenglong.audiobookbkic.handleLists.favorite;


public class Favorite {
    private int id;
    private String title;
    private String content;
    private int insertTime;
    private String fileUrl;
    private int categoryId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    public int getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(int insertTime) {
        this.insertTime = insertTime;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }


    public Favorite(int id, String title, String content, int insertTime, String fileUrl, int categoryId, int status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.insertTime = insertTime;
        this.fileUrl = fileUrl;
        this.categoryId = categoryId;
        this.status = status;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Favorite() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
