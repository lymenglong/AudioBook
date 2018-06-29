package com.bkic.lymenglong.audiobookbkic.player;

import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.bkic.lymenglong.audiobookbkic.R;

import java.io.FileDescriptor;
import java.util.ArrayList;

/**
 * Created by CHUNGNHIM on 5/14/2015.
 */
public class UtilsSong {

    /**
     * Lay all danh sach dinh dang file .mp3 trong the nho ngoai
     */

    public static ArrayList<SongBean> getAllAudioByAlbum(String allbum, Context context) {
        ArrayList<SongBean> arraySong = null;
        try {

            SongBean objSong;
            arraySong = new ArrayList<SongBean>();
            Uri uri;
            Cursor mCursor;
            String[] m_data = {MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.COMPOSER};

            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            mCursor = context.getContentResolver().query(uri, null, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);
            if (mCursor != null) {
                for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                    if (allbum.equals(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)))) {
                        objSong = new SongBean();
                        objSong.setMusicName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                        objSong.setArtist(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                        objSong.setAlbumName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                        objSong.setDuration(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                        objSong.setLink(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                        objSong.setAlbum_ID(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                        objSong.setAlbumImage(getAlbumart(context, objSong.getAlbum_ID()));
                        objSong.setComposerName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)));
                        arraySong.add(objSong);
                    }
                  else if(allbum.equals(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)))){
                      objSong = new SongBean();
                      objSong.setMusicName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                      objSong.setArtist(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                      objSong.setAlbumName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                      objSong.setDuration(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                      objSong.setLink(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                      objSong.setAlbum_ID(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                      objSong.setAlbumImage(getAlbumart(context, objSong.getAlbum_ID()));
                      objSong.setComposerName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)));
                      arraySong.add(objSong);
                  }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return arraySong;
    }

    public static ArrayList<SongBean> getAllAudio(final String endWith, Context context) {
        ArrayList<SongBean> arraySong = null;
        try {

            SongBean objSong;
            arraySong = new ArrayList<SongBean>();
            Uri uri;
            Cursor mCursor;

            String[] m_data = {MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.COMPOSER};

//            //Lay danh sach file nhac trong bo nho may
//            uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
//          mCursor = context.getContentResolver().query(uri, null, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);
//
//           if (mCursor != null) {
//             for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
//                 objSong = new SongBean();
//                   objSong.setMusicName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
//                   objSong.setArtist(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
//                   objSong.setAlbumName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
//                   objSong.setDuration(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
//                   objSong.setLink(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
//                   objSong.setAlbum_ID(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//                   objSong.setComposerName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)));
//                  arraySong.add(objSong);
//               }
//           }
            //Lay danh sach file nhac trong the nho ngoai
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            mCursor = context.getContentResolver().query(uri, null, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);
            try{
                if (mCursor != null) {
                for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                    objSong = new SongBean();
                    objSong.setMusicName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                    objSong.setArtist(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    objSong.setAlbumName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    objSong.setDuration(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    objSong.setLink(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                    objSong.setAlbum_ID(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                    objSong.setAlbumImage(getAlbumart(context, objSong.getAlbum_ID()));
                    objSong.setComposerName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)));
                    arraySong.add(objSong);
                }
            }

            }catch (Exception e){
                e.printStackTrace();

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return arraySong;
    }

    /**
     * Get the album image from albumId
     *
     * @param context
     * @param album_id
     * @param
     * @return
     */
    public static Bitmap getAlbumart(Context context, Long album_id) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
                pfd = null;
                fd = null;
            }
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }

    /**
     * @param context
     * @return
     */
    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }

    /**
     * Convert milliseconds into time hh:mm:ss
     *
     * @param milliseconds
     * @return time in String
     */
    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        return sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean currentVersionSupportLockScreenControls() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        return sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * Check if service is running or not
     *
     * @param serviceName
     * @param context
     * @return
     */
    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
