package com.bkic.lymenglong.audiobookbkic.player;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.R;

import java.text.SimpleDateFormat;

public class PresenterPlayer
        extends MediaPlayer
        implements PresenterPlayerImp, SeekBar.OnSeekBarChangeListener {

    private PlayControl playControlActivity;
    private ProgressDialog progressDialog;
    private static String TAG = "PresenterPlayer";
    private MediaPlayer mediaPlayer;
    private int intSoundMax;
    private Boolean mediaIsPrepared = false;
    private Boolean isDownloaded = false;
    private Boolean isBufferComplete = false;
    private Boolean isMissingMp3 = false;
    private Boolean isPreparingCancel = false;
    private Boolean isShowingDialog = false;

    PresenterPlayer(PlayControl playControlActivity) {
        this.playControlActivity = playControlActivity;
    }

    @Override
    public void PrepareMediaPlayer(final String httpUrlMedia, final Boolean isDownloadedAudio) {
        @SuppressLint("StaticFieldLeak")
        class PrepareMediaPlayerClass extends AsyncTask<String, Void, Boolean> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mediaIsPrepared = false;
                isDownloaded = isDownloadedAudio;
                if(!isDownloaded) {
                    progressDialog = ProgressDialog.show(playControlActivity,null,playControlActivity.getString(R.string.buffering_data),true,true);
                    progressDialog.show();
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mediaIsPrepared = false;
                            isPreparingCancel = true;
                            StopMedia();
                        }
                    });
                }
            }
            @Override
            protected Boolean doInBackground(String... strings) {
                try {
                    if(!isDownloadedAudio && !ConnectivityReceiver.isConnected()) {
                        mediaIsPrepared = false;
                        return false;
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    if(isDownloadedAudio){
                        try {
                            mediaPlayer.setDataSource(playControlActivity.getApplicationContext(), Uri.parse(strings[0]));
                            mediaPlayer.prepare();
                            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    if(isPreparingCancel) {
                                        mp.release();
                                        mediaIsPrepared = false;
                                        isPreparingCancel = false;
                                    }
                                }

                            });
                        } catch (Exception e) {
                            Log.e(TAG, "doInBackground: mediaPlayer.setDataSource "+e.getMessage());
                            mediaIsPrepared = false;
                            isDownloaded = false;
                            isMissingMp3 = true;
                            return false;
                        }
                    } else {
                        mediaPlayer.setDataSource(playControlActivity.getApplicationContext(), Uri.parse(strings[0]));
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                if(isPreparingCancel) {
                                    mp.release();
                                    mediaIsPrepared = false;
                                    isPreparingCancel = false;
                                }
                            }

                        });
                    }
                    mediaIsPrepared = true;

                } catch (Exception e) {
                    mediaIsPrepared = false;
                    if(progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
                }

                return mediaIsPrepared;
            }

            @Override
            protected void onPostExecute(Boolean mediaIsPrepared) {
                super.onPostExecute(mediaIsPrepared);

                if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();

                if(isMissingMp3) {
                    playControlActivity.UpdateChapterStatus();
                    isMissingMp3 = false;
                    playControlActivity.PrepareChapter();
                    return;
                }

                if (mediaIsPrepared) {
                    isShowingDialog = true;
                    PlayMedia();
                } else{
                    if(!isPreparingCancel) {
                        String message = "Vui lòng kiểm tra lại mạng";
                        Toast.makeText(playControlActivity, message, Toast.LENGTH_SHORT).show();
                        isPreparingCancel = false;
                    }

                }
//                initialStage = false;
            }
        }
        PrepareMediaPlayerClass prepareMediaPlayerClass = new PrepareMediaPlayerClass();
        prepareMediaPlayerClass.execute(httpUrlMedia);
    }
    @Override
    public void ReplayMedia() { // nghe lại từ đầu
        if (mediaIsPrepared) {
            if(mediaPlayer.isPlaying())mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            //Update SeekBar
            mUpdateHandler.postDelayed(mUpdate,100);
        }
    }
    @Override
    public void RewindMedia() {
        if (mediaIsPrepared) {
            int intCurrentPosition = mediaPlayer.getCurrentPosition();
            // check if seekBackward time is greater than 0 sec
            int seekBackwardTime = 10000; //10sec
            int targetPosition = intCurrentPosition - seekBackwardTime;
            if(targetPosition >= 0){
                // forward song
                mediaPlayer.seekTo(targetPosition);
                Log.d(TAG, "RewindMedia: "+targetPosition);
            }else{
                // backward to starting position
                mediaPlayer.seekTo(0);
            }
            //Update SeekBar
            mUpdateHandler.postDelayed(mUpdate,100);
        }
    }
    @Override
    public void ForwardMedia() {
        if (mediaIsPrepared) {
            int intCurrentPosition = mediaPlayer.getCurrentPosition();
            int seekForwardTime = 10000;//10sec
            int targetPosition = intCurrentPosition + seekForwardTime;
            if(targetPosition < intSoundMax){
                // forward song
                mediaPlayer.seekTo(targetPosition);
                Log.d(TAG, "forwardMedia: "+ targetPosition);
            }else{
                // forward to end position
                mediaPlayer.seekTo(mediaPlayer.getDuration());
            }
            //Update SeekBar
            mUpdateHandler.postDelayed(mUpdate,100);
        }
    }
    @Override
    public void PreviousMedia() {
    }
    @Override
    public void NextMedia() {

    }

    @Override
    public void StopMedia() {
        playControlActivity.getSeekBar().setProgress(0);
        mUpdateHandler.removeCallbacks(mUpdate);
        ReleaseMediaPlayer();
//        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void PauseMedia() {
        if (mediaIsPrepared) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }
    @Override
    public void PlayMedia() {
        if (mediaIsPrepared && !isPreparingCancel) {
            if (!mediaPlayer.isPlaying()) {
                intSoundMax = mediaPlayer.getDuration();
                playControlActivity.getSeekBar().setMax(intSoundMax);
                //Update SeekBar
                mUpdateHandler.postDelayed(mUpdate, 100);
                if(isDownloaded) {
                    playControlActivity.getSeekBar().setSecondaryProgress(intSoundMax);
                    isBufferComplete = true;
                }
                mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        int AudioBuffered = mediaPlayer.getDuration() * percent / 100;
                        playControlActivity.getSeekBar().setSecondaryProgress(AudioBuffered);
                        Log.d(TAG, "onBufferingUpdate: percent = " + percent);
                        if(percent == 100) isBufferComplete = true;

                    }
                });
                if (mediaPlayer.getCurrentPosition() < playControlActivity.getResumeTime()) {
                    if(isShowingDialog) {
                        int resumeMinTime = 30000;
                        if(resumeMinTime < playControlActivity.getResumeTime() && playControlActivity.getResumeTime() < mediaPlayer.getDuration()-1000) {
                            ResumeMediaDialog(playControlActivity);
                            isShowingDialog = false;
                        } else if(playControlActivity.getResumeTime() >= mediaPlayer.getDuration()-1000){
                            NextMediaDialog(playControlActivity);
                        }
                    }
                } else if (playControlActivity.getResumeTime() <= mediaPlayer.getCurrentPosition()
                        && mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration()) {
                    mediaPlayer.start();
                } else {
                    ReplayMedia();
                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (isBufferComplete) {
                            playControlActivity.MediaPlayerOnCompletion();
                        }
                    }
                });
            } else { //mediaplayer.isPlaying() == true
                Toast.makeText(playControlActivity, "Sách nói đang chạy", Toast.LENGTH_SHORT).show();
            }
        } else { //mediaIsPrepared = false;
            playControlActivity.PrepareChapter();
        }
    }

    private void NextMediaDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Chọn Dạng Sách");
        builder.setMessage(
                context.getString(R.string.message_play_complete_chapter)+", "+
                context.getString(R.string.message_play_next_chapter_or_not)
        );
        builder.setCancelable(false);
        builder.setPositiveButton("Nghe tiếp", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                playControlActivity.NextMedia();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Nghe lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mediaPlayer.seekTo(0);
                playControlActivity.setResumeTime(0);
                Log.d(TAG, "PlayMedia: playControlActivity.getResumeTime()= " + playControlActivity.getResumeTime());
                mediaPlayer.start();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //fix talk back read incorrectly
    private String DurationContentDescription(long milliseconds){
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        //Format HH:mm:ss
        /*String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;*/

        String time;
        if (hour > 0) time = hour + " giờ " + min + " phút " + sec + "giây";
        else if (min > 0) time = min + " phút " + sec + " giây";
        else time = sec + " giây";

        return time;
    }

    private void ResumeMediaDialog(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Chọn Dạng Sách");
        builder.setMessage("Bạn đã nghe tới "+DurationContentDescription(playControlActivity.getResumeTime())+","+" bạn có muốn nghe tiếp không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Nghe tiếp", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mediaPlayer.seekTo(playControlActivity.getResumeTime());
                Log.d(TAG, "PlayMedia: playControlActivity.getResumeTime()= " + playControlActivity.getResumeTime());
                mediaPlayer.start();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Nghe lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mediaPlayer.seekTo(0);
                playControlActivity.setResumeTime(0);
                Log.d(TAG, "PlayMedia: playControlActivity.getResumeTime()= " + playControlActivity.getResumeTime());
                mediaPlayer.start();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //region Method to update time
    private Handler mUpdateHandler = new Handler();
    private Runnable mUpdate= new Runnable() {
        @Override
        public void run() {
            //todo fix talk back read wrong mm:ss
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("00:mm:ss");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            if(mediaIsPrepared) {
                playControlActivity.getTxtCurrentDuration().setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                playControlActivity.getTxtSongTotal().setText(simpleDateFormat.format(mediaPlayer.getDuration()));
                playControlActivity.getSeekBar().setProgress(mediaPlayer.getCurrentPosition());
            }
            mUpdateHandler.postDelayed(this, 1000);
        }
    };
    //endregion


    @Override
    public void ReleaseTimeLabel(){
        //todo fix talk back read wrong mm:ss
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm minutes ss seconds");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        playControlActivity.getTxtCurrentDuration().setText(simpleDateFormat.format(0));
        playControlActivity.getTxtSongTotal().setText(simpleDateFormat.format(0));
        playControlActivity.getSeekBar().setProgress(0);
        playControlActivity.getSeekBar().setSecondaryProgress(0);
    }

    @Override
    public void RemoveCallBacksUpdateHandler (){
        mUpdateHandler.removeCallbacks(mUpdate);
    }

    @Override
    public int GetLastMediaData(){
        if (mediaIsPrepared) {
            if(mediaPlayer.getCurrentPosition()==mediaPlayer.getDuration()){
                return mediaPlayer.getDuration();
            }else {
                return mediaPlayer.getCurrentPosition();
            }
        } else {
            return playControlActivity.getResumeTime();
        }
    }

    @Override
    public void ReleaseMediaPlayer() {
        if (mediaIsPrepared && !isPreparingCancel) {
            if(mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mediaPlayer != null) mediaPlayer.seekTo(seekBar.getProgress());
    }
}
