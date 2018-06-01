package com.bkic.lymenglong.audiobookbkic.handleLists.utils;


public class Category {
    private int id;
    private String title;
    private String description;
    private int parentId;
    private int numOfChild;
    private String categoryChildren;

    public Category(int id, String title, String description, int parentId, int numOfChild) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.parentId = parentId;
        this.numOfChild = numOfChild;
    }
    public Category(){
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

    public String getDescription() {
        return description;
    }

    public int getParentId() {
        return parentId;
    }

    public int getNumOfChild() {
        return numOfChild;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setNumOfChild(int numOfChild) {
        this.numOfChild = numOfChild;
    }

    public void setCategoryChildren(String categoryChildren) {
        this.categoryChildren = categoryChildren;
    }

    public String getCategoryChildren() {
        return categoryChildren;
    }
}
