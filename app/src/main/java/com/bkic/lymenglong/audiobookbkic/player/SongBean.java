package com.bkic.lymenglong.audiobookbkic.player;

import android.graphics.Bitmap;

/**
 * Created by Hùng ManUnt on 22/03/2017.
 */

public class SongBean {
    private int musicID;
    private String musicName;
    private String music_EN;
    private String link;
    private String lyrics;
    private int singerID;
    private String thumbImage;
    private int categoriesId;
    private int composerID;
    private String description;
    private String composerName;
    private long album_ID;
    private long duration;
    private String albumName;
    private String artist;
    private Bitmap albumImage;
    private boolean songsChecked;
    private String SingerName;

    public SongBean() {
    }

    public SongBean(int musicID, String musicName, String music_EN, String link, String lyrics, int singerID, String thumbImage, int categoriesId, int composerID, String description, String composerName, long album_ID, long duration, String albumName, String artist, Bitmap albumImage, boolean songsChecked, String singerName) {
        this.musicID = musicID;
        this.musicName = musicName;
        this.music_EN = music_EN;
        this.link = link;
        this.lyrics = lyrics;
        this.singerID = singerID;
        this.thumbImage = thumbImage;
        this.categoriesId = categoriesId;
        this.composerID = composerID;
        this.description = description;
        this.composerName = composerName;
        this.album_ID = album_ID;
        this.duration = duration;
        this.albumName = albumName;
        this.artist = artist;
        this.albumImage = albumImage;
        this.songsChecked = songsChecked;
        SingerName = singerName;
    }

    public String getSingerName() {
        return SingerName;
    }

    public void setSingerName(String singerName) {
        SingerName = singerName;
    }

    public int getMusicID() {
        return musicID;
    }

    public void setMusicID(int musicID) {
        this.musicID = musicID;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusic_EN() {
        return music_EN;
    }

    public void setMusic_EN(String music_EN) {
        this.music_EN = music_EN;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public int getSingerID() {
        return singerID;
    }

    public void setSingerID(int singerID) {
        this.singerID = singerID;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public int getCategoriesId() {
        return categoriesId;
    }

    public void setCategoriesId(int categoriesId) {
        this.categoriesId = categoriesId;
    }

    public int getComposerID() {
        return composerID;
    }

    public void setComposerID(int composerID) {
        this.composerID = composerID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setComposerName(String composername) {
        this.composerName = composername;
    }

    public String getComposerName() {
        return composerName;
    }

    public void setAlbum_ID(long album_ID) {
        this.album_ID = album_ID;
    }

    public long getAlbum_ID() {
        return album_ID;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setAlbumImage(Bitmap albumImage) {
        this.albumImage = albumImage;
    }

    public Bitmap getAlbumImage() {
        return albumImage;
    }

    public void setSongsChecked(boolean songsChecked) {
        this.songsChecked = songsChecked;
    }

    public boolean isSongsChecked() {
        return songsChecked;
    }
}