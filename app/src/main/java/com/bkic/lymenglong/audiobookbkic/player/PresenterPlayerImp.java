package com.bkic.lymenglong.audiobookbkic.player;


public interface PresenterPlayerImp {

    void PrepareMediaPlayer(String httpUrlMedia, Boolean isDownloadedAudio);

    void ReplayMedia();

    void RewindMedia();

    void ForwardMedia();

    void PreviousMedia();

    void NextMedia();

    void PauseMedia();

    void PlayMedia();

    void StopMedia();

    void ReleaseTimeLabel();

    void RemoveCallBacksUpdateHandler();

    int GetLastMediaData();

    void ReleaseMediaPlayer();

}
