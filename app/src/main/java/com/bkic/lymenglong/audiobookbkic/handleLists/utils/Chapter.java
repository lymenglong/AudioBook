package com.bkic.lymenglong.audiobookbkic.handleLists.utils;


public class Chapter {
    private int id;
    private String title;
    private String fileUrl;
    private int length;
    private int bookId;
    private int status;

    public Chapter(int id, String title, String fileUrl, int length, int bookId) {
        this.id = id;
        this.title = title;
        this.fileUrl = fileUrl;
        this.length = length;
        this.bookId = bookId;
    }

    public Chapter() {
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
