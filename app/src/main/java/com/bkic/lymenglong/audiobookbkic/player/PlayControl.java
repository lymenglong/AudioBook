package com.bkic.lymenglong.audiobookbkic.player;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.account.login.Session;
import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.checkInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;
import com.bkic.lymenglong.audiobookbkic.download.DownloadedStatus;
import com.bkic.lymenglong.audiobookbkic.download.Utils;
import com.bkic.lymenglong.audiobookbkic.handleLists.history.PlaybackHistory;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Book;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.overrideTalkBack.PresenterOverrideTalkBack;
import com.bkic.lymenglong.audiobookbkic.utils.Const;
import com.bkic.lymenglong.audiobookbkic.download.PresenterDownloadTaskManager;
import com.bkic.lymenglong.audiobookbkic.handleLists.favorite.PresenterUpdateFavorite;
import com.bkic.lymenglong.audiobookbkic.handleLists.history.PresenterUpdateHistory;
import com.bkic.lymenglong.audiobookbkic.review.PresenterReview;
import com.bkic.lymenglong.audiobookbkic.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_Audio;

public class PlayControl extends AppCompatActivity
        implements PlayerImp, ConnectivityReceiver.ConnectivityReceiverListener, DownloadReceiver.DownloadReceiverListener{
    private Activity playControlActivity = PlayControl.this;
    private PresenterPlayer presenterPlayer = new PresenterPlayer(this);
    private PresenterUpdateHistory presenterUpdateHistory = new PresenterUpdateHistory(this);
    private PresenterUpdateFavorite presenterUpdateFavorite = new PresenterUpdateFavorite(this);
    private PresenterOverrideTalkBack presenterOverrideTalkBack = new PresenterOverrideTalkBack(this);
    private PresenterReview presenterReview = new PresenterReview(this);
    private PresenterDownloadTaskManager presenterDownloadTaskManager = new PresenterDownloadTaskManager();
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    private static final String TAG = "PlayControl";
    private Session session;
    private int ResumeTime;
    private DBHelper dbHelper;
    private Button btnPlay, btnStop, btnPause, btnForward, btnBackward, btnNext, btnPrev, btnFavorite, btnDownload;
    private SeekBar seekBar;
    private TextView txtSongTotal;
    private TextView txtCurrentDuration;
    private int RateNumber;
    private String Review;
    private Chapter chapterFromIntent;
    private String AudioUrl;
    private Boolean isFavoriteBook = false;
    private View imBackHome;


    public int getResumeTime() {
        return ResumeTime;
    }

    public void setResumeTime(int resumeTime) {
        ResumeTime = resumeTime;
    }

    public void setRateNumber(int rateNumber) {
        RateNumber = rateNumber;
    }

    public int getRateNumber() {
        return RateNumber;
    }

    public void setReview(String review) {
        Review = review;
    }

    public String getReview() {
        return Review;
    }

    //    DiskLruCache diskLruCache = DiskLruCache.open(getCacheDir(), 1, 1, 50 * 1024 * 1024);
//
//    public PlayControl() throws IOException {
//    }

    public TextView getTxtSongTotal() {
        return txtSongTotal;
    }
    public TextView getTxtCurrentDuration() {
        return txtCurrentDuration;
    }
    public SeekBar getSeekBar() {
        return seekBar;
    }
    private int indexChapterMap = -1;
    private HashMap<String, Chapter> hashMapChapter = new HashMap<>();
    private Boolean InitialState = true;
    private HashMap<String, PlaybackHistory> historyHashMap = new HashMap<>();

    //connectionReceiver
    private IntentFilter intentFilter;
    private ConnectivityReceiver receiver;
    //downloadReceiver
    private IntentFilter filter;
    private DownloadReceiver downloadReceiver;
    private HashMap<String, DownloadedStatus> hashMapDownloaded = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_control);
        initIntentFilter();
        initDataFromIntent();
        initView();
        initToolbar(chapterFromIntent.getTitle());
        initObject();
        initGetChapterData();
        initGetChapterDownloaded();
        initCheckChapterDownloadStatus();
        initPlayHistoryState();
        initCheckAudioUrl();
        intListener();
        initCheckFavoriteBookState();
    }

    private void initEnableButton(Button btn) {
        if(isFavoriteBook) btn.setEnabled(false);
        else btn.setEnabled(true);
    }

    private void initCheckFavoriteBookState() {
        String SELECT_DATA = "SELECT * FROM favorite WHERE BookId = '"+chapterFromIntent.getBookId()+"'";
        Cursor cursor = dbHelper.GetData(SELECT_DATA);
        if(cursor.moveToFirst())
            if(cursor.getCount()!=0){
                isFavoriteBook = true;
                initEnableButton(btnFavorite);
            }

    }

    //Module checkIfAlreadyHavePermission() is implemented as :
    private boolean checkIfAlreadyHavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    //Module requestForSpecificPermission() is implemented as :
    private void requestForSpecificPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions
                    (
                            this,
                            new String[]
                                    {
    //                                        Manifest.permission.GET_ACCOUNTS,
    //                                        Manifest.permission.RECEIVE_SMS,
    //                                        Manifest.permission.READ_SMS,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    },
                            101
                    );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadTask();
                } else {
                    //not granted
                    Toast.makeText(playControlActivity, "Check", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initGetChapterDownloaded(){
        String SELECT_DATA = "SELECT * FROM downloadStatus WHERE BookId = '"+chapterFromIntent.getBookId()+"'";
        Cursor cursor = dbHelper.GetData(SELECT_DATA);
        while (cursor.moveToNext()){
            DownloadedStatus status =
                    new DownloadedStatus
                    (
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getInt(2)
                    );
            hashMapDownloaded.put(String.valueOf(cursor.getInt(0)),status); //key = chapterId
        }
    }

    @Override
    public Boolean initCheckChapterDownloadStatus() { //downloaded yet?
        if (!InitialState) {
//            initReloadChapterData();
            initGetChapterDownloaded();
        }
//        Boolean isDownloaded = hashMapChapter.get(String.valueOf(indexChapterMap)).getStatus() != 0;
        String keyMap = String.valueOf(hashMapChapter.get(String.valueOf(indexChapterMap)).getId());
        Boolean isDownloaded;
        isDownloaded =
                        hashMapDownloaded.get(keyMap) != null
                        &&
                        hashMapDownloaded.get(keyMap).getDownloadedState() != 0;

        if (isDownloaded) {
            btnDownload.setEnabled(false);
            btnDownload.setText(R.string.downloadCompleted);//If Download completed then change button text
        } else {
            btnDownload.setEnabled(true);
            btnDownload.setText(R.string.download);//If Download completed then change button text
        }
        return isDownloaded;
    }

    private void initIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();

        //set filter to only when download is complete and register broadcast receiver
        filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new DownloadReceiver();

    }

    private void initPlayHistoryState() {
        String SELECT_PLAY_BACK_HISTORY =
                "SELECT * FROM playHistory " +
                    "WHERE " +
                        "BookId = '"+chapterFromIntent.getBookId()+"' " +
                        "AND " +
                        "ChapterId = '"+chapterFromIntent.getId()+"'" +
                ";";
        Cursor cursor = dbHelper.GetData(SELECT_PLAY_BACK_HISTORY);
        if(cursor.moveToFirst()){
            PlaybackHistory history = new PlaybackHistory
                    (
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getString(3)
                    );
            historyHashMap.put(String.valueOf(cursor.getInt(0)),history); //key = ChapterId
        }
    }

    private void initGetChapterData(){
        String SELECT_FROM_CHAPTER = "SELECT * FROM CHAPTER WHERE BookId = '"+chapterFromIntent.getBookId()+"'";
        Cursor cursor = dbHelper.GetData(SELECT_FROM_CHAPTER);
        while(cursor.moveToNext()){
            Chapter chapterModel = new Chapter();
            chapterModel.setId(cursor.getInt(0));
            chapterModel.setTitle(cursor.getString(1));
            chapterModel.setFileUrl(cursor.getString(2));
            chapterModel.setLength(cursor.getInt(3));
            chapterModel.setBookId(cursor.getInt(4));
            chapterModel.setStatus(cursor.getInt(5));
            hashMapChapter.put(String.valueOf(cursor.getPosition()), chapterModel);
            if(chapterModel.getId() == chapterFromIntent.getId()){
                indexChapterMap = cursor.getPosition();
            }
        }
    }

/*    private void initReloadChapterData(){
        String SELECT_FROM_CHAPTER = "SELECT * FROM CHAPTER WHERE BookId = '"+chapterFromIntent.getBookId()+"'";
        Cursor cursor = dbHelper.GetData(SELECT_FROM_CHAPTER);
        while(cursor.moveToNext()){
            Chapter chapterModel = new Chapter();
            chapterModel.setId(cursor.getInt(0));
            chapterModel.setTitle(cursor.getString(1));
            chapterModel.setFileUrl(cursor.getString(2));
            chapterModel.setLength(cursor.getInt(3));
            chapterModel.setBookId(cursor.getInt(4));
            chapterModel.setStatus(cursor.getInt(5));
            hashMapChapter.put(String.valueOf(cursor.getPosition()), chapterModel);
        }
    }*/

    /*private static final String TALKBACK_SERVICE_NAME = "com.google.android.marvin.talkback/.TalkBackService";

    private void updateTalkBackState(boolean enableTalkBack) {
        if (enableTalkBack) {
            enableAccessibilityService(TALKBACK_SERVICE_NAME);
        } else {
            disableAccessibilityServices();
        }
    }

    private void enableAccessibilityService(String name) {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, name);
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, VALUE_ENABLED);
    }

    private void disableAccessibilityServices() {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "");
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, VALUE_DISABLED);
    }*/

    private void initCheckAudioUrl() {
        if (chapterFromIntent.getFileUrl().isEmpty()) {
            Toast.makeText(playControlActivity, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
            playControlActivity.finish();
        } else{
            PrepareChapter();
        }
    }

    @Override
    public void PrepareChapter() {
        if (0 <= indexChapterMap && indexChapterMap < hashMapChapter.size()) {
            if (!InitialState) presenterPlayer.StopMedia();
            String indexChapterTitle = hashMapChapter.get(String.valueOf(indexChapterMap)).getTitle();
            initToolbar(indexChapterTitle);
            chapterFromIntent.setId(hashMapChapter.get(String.valueOf(indexChapterMap)).getId());
            initPlayHistoryState();
            try {
                int ChapterIdFromIndex = hashMapChapter.get(String.valueOf(indexChapterMap)).getId();
                int ResumePosition = historyHashMap.get(String.valueOf(ChapterIdFromIndex)).getPauseTime();
                setResumeTime(ResumePosition);
            } catch (Exception ignored) {
                setResumeTime(0);
            }
            Boolean isDownloadedAudio = initCheckChapterDownloadStatus();
            if(isDownloadedAudio){
                AudioUrl =      Environment.getExternalStorageDirectory().getPath()+ "/"
                        + Utils.downloadDirectory + "/"
                        + chapterFromIntent.getBookId() + "/"
                        + chapterFromIntent.getId() + ".mp3";
            } else{
                String ChapterUrlFromIndex = hashMapChapter.get(String.valueOf(indexChapterMap)).getFileUrl();
                AudioUrl = HttpURL_Audio + ChapterUrlFromIndex;
            }
            presenterPlayer.PrepareMediaPlayer(AudioUrl, isDownloadedAudio);
            InitialState = false;
        } else if(indexChapterMap < 0 ) {
            indexChapterMap = 0;
            String message = getString(R.string.message_chapter_not_exists);
            Toast.makeText(playControlActivity, message, Toast.LENGTH_SHORT).show();
        } else {
            indexChapterMap = hashMapChapter.size()-1;
            String message = getString(R.string.message_chapter_not_exists);
            Toast.makeText(playControlActivity, message, Toast.LENGTH_SHORT).show();
        }
    }

    //Update when file mp3 is missing
    @Override
    public void UpdateChapterStatus() {
        Toast.makeText(playControlActivity, "file offline đã bị mất", Toast.LENGTH_SHORT).show();
        dbHelper.QueryData(
                "UPDATE chapter " +
                        "SET ChapterStatus = '0' " +
                        "WHERE " +
                            "ChapterId = '"+chapterFromIntent.getId()+"' " +
                            "AND " +
                            "BookId = '"+chapterFromIntent.getBookId()+"'" +
                        ";"
        );
        dbHelper.QueryData(
                "UPDATE downloadStatus " +
                        "SET DownloadedStatus = '0' " +
                        "WHERE " +
                        "ChapterId = '"+chapterFromIntent.getId()+"' " +
                        "AND " +
                        "BookId = '"+chapterFromIntent.getBookId()+"'" +
                        ";"
        );
       /* Cursor cursor = dbHelper.GetData(
                "SELECT BookId " +
                        "FROM downloadStatus " +
                        "WHERE " +
                        "ChapterId = '1' " +
                        "AND " +
                        "BookId = '"+chapterFromIntent.getBookId()+"'");
        if(cursor.moveToFirst())
            if(cursor.getCount()!=0)
        dbHelper.QueryData(
                "UPDATE "
        );
        dbHelper.close();*/
    }

    private void initObject() {
        context = PlayControl.this;
        session = new Session(playControlActivity);
        dbHelper = new DBHelper(this,DB_NAME,null,DB_VERSION);
        /*@SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        String ChapterLengthConverted = timeFormat.format(ChapterLength*1000); //ChapterLength is second unit so we convert to millisecond
        txtSongTotal.setText(ChapterLengthConverted);*/

    }

    private void initDataFromIntent() {
        chapterFromIntent = new Chapter
                (
                        getIntent().getIntExtra("ChapterId",-1),
                        getIntent().getStringExtra("ChapterTitle"),
                        getIntent().getStringExtra("ChapterUrl"),
                        getIntent().getIntExtra("ChapterLength", 0),
                        getIntent().getIntExtra("BookId",-1)
                );

    }

    private void initToolbar(String ChapterTitle) {
        setTitle(ChapterTitle);
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, ChapterTitle, false);
        imBackHome = findViewById(R.id.imBackHome);
        imBackHome.setVisibility(View.VISIBLE);
//        findViewById(R.id.imBack).setVisibility(View.GONE);

    }

    private void initView() {
        btnFavorite = findViewById(R.id.btn_add_favorite_book);
        btnPlay = findViewById(R.id.btn_play);
        btnPause = findViewById(R.id.btn_pause);
        btnForward = findViewById(R.id.btn_ffw);
        btnBackward = findViewById(R.id.btn_backward);
        btnNext = findViewById(R.id.btn_next);
        btnPrev = findViewById(R.id.btn_previous);
        btnStop = findViewById(R.id.btn_replay);
        btnDownload = findViewById(R.id.btn_download);
        seekBar = findViewById(R.id.seekBar);
        txtSongTotal = findViewById(R.id.text_total_duration_label);
        txtCurrentDuration = findViewById(R.id.text_current_duration_label);

        //Do allow talk back to read content when user touch screen
        presenterOverrideTalkBack.DisableTouchForTalkBack(btnFavorite);
        presenterOverrideTalkBack.DisableTouchForTalkBack(btnPlay);
        presenterOverrideTalkBack.DisableTouchForTalkBack(btnPause);
        presenterOverrideTalkBack.DisableTouchForTalkBack(btnForward);
        presenterOverrideTalkBack.DisableTouchForTalkBack(btnBackward);
        presenterOverrideTalkBack.DisableTouchForTalkBack(btnNext);
        presenterOverrideTalkBack.DisableTouchForTalkBack(btnPrev);
        presenterOverrideTalkBack.DisableTouchForTalkBack(btnStop);
        presenterOverrideTalkBack.DisableTouchForTalkBack(btnDownload);
        presenterOverrideTalkBack.DisableTouchForTalkBack(seekBar);
        presenterOverrideTalkBack.DisableTouchForTalkBack(txtSongTotal);
        presenterOverrideTalkBack.DisableTouchForTalkBack(txtCurrentDuration);

        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.seekBar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        //todo fix talk back read wrong mm:ss
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.text_current_duration_label), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.text_total_duration_label), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    private void intListener() {
        btnFavorite.setOnClickListener(onClickListener);
        btnPlay.setOnClickListener(onClickListener);
        btnPause.setOnClickListener(onClickListener);
        btnForward.setOnClickListener(onClickListener);
        btnBackward.setOnClickListener(onClickListener);
        btnNext.setOnClickListener(onClickListener);
        btnPrev.setOnClickListener(onClickListener);
        btnStop.setOnClickListener(onClickListener);
        btnDownload.setOnClickListener(onClickListener);
        seekBar.setOnSeekBarChangeListener(presenterPlayer);
        imBackHome.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(@NotNull View v) {
            //region Switch Button
            switch (v.getId()){
                case R.id.btn_add_favorite_book:
                    AddFavoriteBook();
                    break;
                case R.id.btn_play :
                    presenterPlayer.PlayMedia();
                    break;
                case R.id.btn_pause:
                    presenterPlayer.PauseMedia();
//                    MyNotification myNotification = new MyNotification(playControlActivity);
//                    myNotification.createNotification();
//                    presenterReview.ReviewBookDialog(playControlActivity);
//                presenterReview.ReviewBookDialog2(playControlActivity);
//                presenterReview.ReviewBookDialog3(playControlActivity);
                    break;
                case R.id.btn_replay:
                    presenterPlayer.ReplayMedia();
                    break;
                case R.id.btn_next:
                    NextMedia();
                    break;
                case R.id.btn_previous:
                    PrevMedia();
                    break;
                case R.id.btn_ffw:
                    presenterPlayer.ForwardMedia();
                    break;
                case R.id.btn_backward:
                    presenterPlayer.RewindMedia();
                    break;
                case R.id.btn_download:
                    //Check internet connection
                    if(!ConnectivityReceiver.isConnected()){
                        Toast.makeText(playControlActivity, getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //check if my app has permission and than request if it does not have permission
                    int MyVersion = Build.VERSION.SDK_INT;
                    if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (!checkIfAlreadyHavePermission()) {
                            requestForSpecificPermission();
                        } else{
                            DownloadTask();
                        }
                    }else {
                        DownloadTask();
                    }
                    break;
                case R.id.imBackHome:
                    Intent intent=new Intent();
                    intent.putExtra(Const.STRING_BACK_HOME, true);
                    setResult(Const.REQUEST_CODE_BACK_HOME, intent);
                    finish();//finishing activity
                    break;
            }
            //endregion
        }
    };

    private void PrevMedia() {
        presenterPlayer.ReleaseTimeLabel();
        UpdateHistoryData();
        indexChapterMap--;
        PrepareChapter();
    }

    @Override
    public void NextMedia() {
        presenterPlayer.ReleaseTimeLabel();
        UpdateHistoryData();
        indexChapterMap++;
        PrepareChapter();
    }

    private void DownloadTask(){
        presenterDownloadTaskManager.DownloadTaskManager
                (
                        playControlActivity.getApplicationContext(),
                        btnDownload,
                        AudioUrl,
                        String.valueOf(chapterFromIntent.getBookId()),
                        String.valueOf(chapterFromIntent.getId()),
                        chapterFromIntent.getBookId(),
                        chapterFromIntent.getId()
                );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        registerReceiver(receiver, intentFilter);
        registerReceiver(downloadReceiver, filter);
        // register status listener
        MyApplication.getInstance().setConnectivityListener(this);
        MyApplication.getInstance().setDownloadListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister receiver
        unregisterReceiver(receiver);
        unregisterReceiver(downloadReceiver);
        //Pause Media
//        presenterPlayer.PauseMedia();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
//        ToastConnectionMessage(isConnected);
    }

    /*private void ToastConnectionMessage(boolean isConnected) {
        String message;
        if (isConnected) {
            message = getString(R.string.message_internet_connected);
        } else {
            message = getString(R.string.message_internet_not_connected);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void AddReviewBookToServer() {
        int userId = session.getUserIdLoggedIn();
        int bookId = chapterFromIntent.getBookId();
        int rateNumber = getRateNumber();
        String review = getReview();
        presenterReview.RequestReviewBook(playControlActivity,userId,bookId,rateNumber,review);
    }

    @Override
    public void AddReviewChapterToServer() {
        int userId = session.getUserIdLoggedIn();
        int bookId = chapterFromIntent.getBookId();
        int chapterId = chapterFromIntent.getId();
        int rateNumber = getRateNumber();
        String review = getReview();
        presenterReview.RequestAddChapterReview(playControlActivity, userId, bookId, chapterId, rateNumber, review);
    }

    private void AddFavoriteBook() {
        //region Update to favorite with httpWebCall
        String jsonAction = "addFavourite";
        String IdUserHolder = String.valueOf(session.getUserIdLoggedIn());
        String IdBookHolder = String.valueOf(chapterFromIntent.getBookId());
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String InsertTimeHolder = simpledateformat.format(calendar.getTime());
        if (ConnectivityReceiver.isConnected()&&!CheckBookSynced("favorite")) {
            presenterUpdateFavorite.RequestUpdateToServer(jsonAction,IdUserHolder,IdBookHolder,InsertTimeHolder);
        }
        //endregion

        //region ADD BOOK IN TO TABLE FAVORITE SQLite
        String SELECT_BOOK_BY_BOOK_ID =
                "SELECT " +
                        "BookId, " +
                        "BookTitle, " +
                        "BookImage, " +
                        "BookLength, " +
                        "BookAuthor " +
                "FROM " +
                        "book " +
                "WHERE " +
                        "BookId = '"+chapterFromIntent.getBookId()+"'" +
                ";";
        Cursor cursor = dbHelper.GetData(SELECT_BOOK_BY_BOOK_ID);
        Book bookModel = new Book();
        while (cursor.moveToNext()) {
            bookModel = new Book(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4)
            );
        }
        cursor.close();
        if (bookModel.getLength() != 0) {
            try {
                String INSERT_BOOK_INTO_TABLE_FAVORITE =
                        "INSERT INTO favorite VALUES" +
                                "(" +
                                        "'"+bookModel.getId()+"', " +
                                        "'"+bookModel.getTitle()+"', " +
                                        "'"+bookModel.getUrlImage()+"', " +
                                        "'"+bookModel.getLength()+"', " +
                                        "'"+bookModel.getAuthor()+"', " +
                                        "'"+Const.BOOK_NOT_SYNCED_WITH_SERVER+"', " +// BookSync Is Default Equal 0
                                        "'"+Const.BOOK_NOT_REQUEST_REMOVE_SYNCED_WITH_SERVER+"'" +// BookRemoved Is Default Equal 0
                                ");";
                dbHelper.QueryData(INSERT_BOOK_INTO_TABLE_FAVORITE);
                dbHelper.close();
            } catch (Exception ignored) {
                String UPDATE_BOOK_IN_TABLE_FAVORITE =
                        "UPDATE " +
                                "favorite " +
                        "SET " +
                                "BookTitle = '"+bookModel.getTitle()+"', " +
                                "BookImage = '"+bookModel.getUrlImage()+"', " +
                                "BookLength = '"+bookModel.getLength()+"', " +
                                "BookAuthor = '"+bookModel.getAuthor()+"', " +
                                "BookRemoved = '"+Const.BOOK_NOT_REQUEST_REMOVE_SYNCED_WITH_SERVER+"', "+
                                "BookSync = '"+Const.BOOK_NOT_REQUEST_REMOVE_SYNCED_WITH_SERVER+"' " +
                        "WHERE " +
                                "BookId = '"+bookModel.getId()+"'" +
                        ";";
                dbHelper.QueryData(UPDATE_BOOK_IN_TABLE_FAVORITE);
                dbHelper.close();
            }
        }
        dbHelper.close();
        initCheckFavoriteBookState();
        //endregion
    }

    @Override
    public void MediaPlayerOnCompletion() {
        String message = "Chương này đã chạy xong";
        Toast.makeText(playControlActivity, message, Toast.LENGTH_SHORT).show();
        if(ConnectivityReceiver.isConnected()) {
            presenterReview.ReviewBookDialog4(playControlActivity);
        }
        UpdateHistoryData();
    }

    @Override
    protected void onDestroy() {
        //<editor-fold desc="Remove event handler from seek bar">
        presenterPlayer.RemoveCallBacksUpdateHandler();
        //</editor-fold>
        presenterPlayer.ReleaseMediaPlayer();
        //StopService
//        stopService(new Intent(this, MyDownloadService.class));
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateHistoryData();
    }

    @NonNull
    private Boolean CheckBookSynced(String tableName){
        String SELECT_BOOK_SYNC =
                "SELECT " +
                        "BookSync " +
                "FROM " +
                        ""+tableName+" " +
                "WHERE BookId = '"+chapterFromIntent.getBookId()+"'";
        Cursor cursor = dbHelper.GetData(SELECT_BOOK_SYNC);
        int BookSync = 0;
        while (cursor.moveToNext()){
            BookSync = cursor.getInt(0);
        }
        cursor.close();
        return BookSync == 1;
    }

    @Override
    public void UpdateHistoryData() {
        if (!chapterFromIntent.getFileUrl().isEmpty()) {
            int lastPlayDuration = presenterPlayer.GetLastMediaData();
            String jsonAction = "addHistory";
            String IdUserHolder = String.valueOf(session.getUserIdLoggedIn());
            String IdBookHolder = String.valueOf(chapterFromIntent.getBookId());
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                    new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String InsertTimeHolder = simpledateformat.format(calendar.getTime());
            if(ConnectivityReceiver.isConnected() && !CheckBookSynced("history"))
                presenterUpdateHistory.RequestUpdateToServer
                    (jsonAction,IdUserHolder,IdBookHolder,InsertTimeHolder);

            //region INSERT VALUE TO SQLite DATABASE
            //region Update Table playHistory
            try {
                String INSERT_PLAY_HISTORY =
                        "INSERT INTO playHistory VALUES" +
                                "(" +
                                        "'"+chapterFromIntent.getId()+"', " +
                                        "'"+IdBookHolder+"', " +
                                        "'"+lastPlayDuration +"', " +
                                        "'"+InsertTimeHolder+"'"+
                                ");";
                dbHelper.QueryData(INSERT_PLAY_HISTORY);
                dbHelper.close();
            } catch (Exception ignored) {
                String UPDATE_PLAY_HISTORY =
                        "UPDATE " +
                                "playHistory " +
                        "SET " +
                                "PauseTime = '"+ lastPlayDuration +"', " +
                                "LastDate ='"+InsertTimeHolder+"' " +
                        "WHERE " +
                                "ChapterId = '"+chapterFromIntent.getId()+"' " +
                                "AND " +
                                "BookId = '"+chapterFromIntent.getBookId()+"'" +
                        ";";
                dbHelper.QueryData(UPDATE_PLAY_HISTORY);
                dbHelper.close();
            }
            //endregion
            //region INSERT BOOK VALUE TO HISTORY (SQLite)
            String SELECT_BOOK_BY_BOOK_ID =
                    "SELECT " +
                            "BookId, " +
                            "BookTitle, " +
                            "BookImage, " +
                            "BookLength, " +
                            "BookAuthor " +
                    "FROM " +
                            "book " +
                    "WHERE " +
                            "BookId = '"+chapterFromIntent.getBookId()+"'" +
                    ";";
            Cursor cursor = dbHelper.GetData(SELECT_BOOK_BY_BOOK_ID);
            Book bookModel = new Book();
            if (cursor.moveToFirst()) {
                do{
                    bookModel = new Book(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getString(4)
                    );
                }while (cursor.moveToNext());
            }
            cursor.close();
            if(bookModel.getLength() != 0) {
                try {
                    String INSERT_BOOK_INTO_TABLE_HISTORY =
                            "INSERT INTO history VALUES" +
                                    "(" +
                                    "'" + bookModel.getId() + "', " +
                                    "'" + bookModel.getTitle() + "', " +
                                    "'" + bookModel.getUrlImage() + "', " +
                                    "'" + bookModel.getLength() + "', " +
                                    "'" + bookModel.getAuthor() + "', " +
                                    "'" + Const.BOOK_NOT_SYNCED_WITH_SERVER + "', " +
                                    "'" + Const.BOOK_NOT_REQUEST_REMOVE_SYNCED_WITH_SERVER + "'" +
                                    ");";
                    dbHelper.QueryData(INSERT_BOOK_INTO_TABLE_HISTORY);
                    dbHelper.close();
                } catch (Exception ignored) {
                    String UPDATE_BOOK_IN_TABLE_HISTORY =
                            "UPDATE " +
                                    "history " +
                                    "SET " +
                                    "BookTitle = '" + bookModel.getTitle() + "', " +
                                    "BookImage = '" + bookModel.getUrlImage() + "', " +
                                    "BookLength = '" + bookModel.getLength() + "', " +
                                    "BookAuthor = '" + bookModel.getAuthor() + "' " +
                                    "WHERE " +
                                    "BookId = '" + bookModel.getId() + "'" +
                                    ";";
                    dbHelper.QueryData(UPDATE_BOOK_IN_TABLE_HISTORY);
                    dbHelper.close();
                }
                //endregion
            }
            //endregion
            //todo Rating Book
        }
    }

    //SyncBook Update SQLite Status For History & Favorite
    private void UpdateBookSyncStatus(String tableName) {
        String UPDATE_BOOK_SYNC =
                "UPDATE " +
                        "'"+tableName+"' " +
                "SET " +
                        "BookSync = '"+ Const.BOOK_SYNCED_WITH_SERVER+"' " +
                "WHERE " +
                        "BookId = '"+chapterFromIntent.getBookId()+"'" +
                ";";
        dbHelper.QueryData(UPDATE_BOOK_SYNC);
        dbHelper.close();
    }

    @Override
    public void UpdateHistorySuccess(String message) {
        //update phone data status
        UpdateBookSyncStatus("history");
        Log.d(TAG, "UpdateHistorySuccess: "+message);
    }

    @Override
    public void UpdateHistoryFailed(String message) {
        UpdateBookSyncStatus("history");
        Log.d(TAG, "UpdateHistoryFailed: "+message);
    }

    @Override
    public void UpdateFavoriteSuccess(String message) {
        UpdateBookSyncStatus("favorite");
        Log.d(TAG, "UpdateFavoriteSuccess: "+message);
        if(!message.isEmpty())
        Toast.makeText(playControlActivity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void UpdateFavoriteFailed(String message) {
        UpdateBookSyncStatus("favorite");
        Log.d(TAG, "UpdateFavoriteSuccess: "+message);
    }

    @Override
    public void UpdateReviewSuccess(String message) {
        String ms = getString(R.string.message_thank_review);
        Toast.makeText(playControlActivity, message.isEmpty()?ms:message , Toast.LENGTH_SHORT).show();
        Log.d(TAG, "UpdateReviewSuccess: "+message);
    }

    @Override
    public void UpdateReviewFailed(String message) {
        Log.d(TAG, "UpdateReviewFailed: "+message);
    }

    @Override
    public void UpdateChapterReviewSuccess(String message) {
        String ms = getString(R.string.message_thank_review);
        Toast.makeText(playControlActivity, message.isEmpty()?ms:message , Toast.LENGTH_SHORT).show();
        Log.d(TAG, "UpdateReviewSuccess: "+message);
    }

    @Override
    public void UpdateReviewTable() {
        //Update review sqlite
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String insertTime = simpledateformat.format(calendar.getTime());
        int chapterId = chapterFromIntent.getId();
        int bookId = chapterFromIntent.getBookId();
        int rateNumber = getRateNumber();
        String review = getReview();
        Cursor cursor;
        try {
            cursor = dbHelper.GetData("SELECT * FROM review WHERE ChapterId = '"+chapterId+"'");
            if(cursor.moveToFirst()){
                if(cursor.getCount()!=0) return;
            }
        } catch (Exception e) {
            dbHelper.QueryData("DROP TABLE IF EXISTS review"); // to drop old version of review table
            dbHelper.QueryData(Const.CREATE_TABLE_REVIEW);
        }
        dbHelper.QueryData(
                "INSERT INTO review VALUES" +
                        "(" +
                        "'"+chapterId+"', " +
                        "'"+bookId+"', " +
                        "'"+insertTime+"', " +
                        "'"+rateNumber+"', " +
                        "'"+review+"'" +
                        ");"
                );
        dbHelper.close();
    }

    @Override
    public void UpdateChapterReviewFailed(String message) {
        Log.d(TAG, "UpdateReviewFailed: "+message);
    }

/*    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, @NotNull Intent intent) {
            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            presenterDownloadTaskManager.UpdateChapterTable(String.valueOf(referenceId));
            presenterDownloadTaskManager.UpdateBookTable(String.valueOf(referenceId));
            if (presenterDownloadTaskManager.isCurrentChapter(referenceId,chapterFromIntent.getId())) {
                btnDownload.setEnabled(false);
                btnDownload.setText(R.string.downloadCompleted);//If Download completed then change button text
            }
            Toast toast = Toast.makeText(context,
                    "Download Complete " + referenceId, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
        }
    };*/

    @Override
    public void onDownloadCompleted(long downloadId) {
        if (presenterDownloadTaskManager.isCurrentChapter(downloadId,chapterFromIntent.getId())) {
            btnDownload.setEnabled(false);
            btnDownload.setText(R.string.downloadCompleted);//If Download completed then change button text
        }
    }

    public void mToastMessage(String ms) {
        Toast.makeText(playControlActivity, ms, Toast.LENGTH_SHORT).show();
    }
}
