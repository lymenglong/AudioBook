package com.bkic.lymenglong.audiobookbkic.handleLists.utils;


public class Book {
    private int id;
    private String title;
    private String content;
    private String fileUrl;
    private String urlImage;
    private int length;
    private String author;
    private String publishDate;
    private int numOfChapter;
    private int categoryId;
    private String categoryList;

    public Book() {
    }

    public Book(int id, String title, String urlImage, int length, String author) {
        this.id = id;
        this.title = title;
        this.urlImage = urlImage;
        this.length = length;
        this.author = author;
    }

    public Book(int id, String title, String urlImage, int length, int categoryId) {
        this.id = id;
        this.title = title;
        this.urlImage = urlImage;
        this.length = length;
        this.categoryId = categoryId;
    }

    public Book
            (
                    int id,
                    String title,
                    String author,
                    String urlImage,
                    int length,
                    int categoryId


            )
    {
        this.id = id;
        this.title = title;
        this.urlImage = urlImage;
        this.length = length;
        this.author = author;
        this.categoryId = categoryId;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getNumOfChapter() {
        return numOfChapter;
    }

    public void setNumOfChapter(int numOfChapter) {
        this.numOfChapter = numOfChapter;
    }

    public void setCategoryList(String categoryList) {
        this.categoryList = categoryList;
    }

    public String getCategoryList() {
        return categoryList;
    }
}
