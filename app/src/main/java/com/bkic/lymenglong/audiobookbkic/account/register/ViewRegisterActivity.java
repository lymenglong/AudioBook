package com.bkic.lymenglong.audiobookbkic.account.register;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.account.utils.InputValidation;
import com.bkic.lymenglong.audiobookbkic.account.utils.User;
import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.account.login.ViewLoginActivity;

import java.util.HashMap;

import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_API;


public class ViewRegisterActivity extends AppCompatActivity implements ViewRegisterImp, View.OnClickListener {
    PresenterRegisterLogic presenterRegisterLogic;
    private Activity registerActivity = ViewRegisterActivity.this;
//    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutFirstName;
    private TextInputLayout textInputLayoutLastName;
    private TextInputLayout textInputLayoutUserName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;
    private TextInputLayout textInputLayoutAddress;
    private TextInputLayout textInputLayoutPhoneNumber;

    private TextInputEditText textInputEditTextFirstName;
    private TextInputEditText textInputEditTextLastName;
    private TextInputEditText textInputEditTextUserName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;
    private TextInputEditText textInputEditTextAddress;
    private TextInputEditText textInputEditTextPhoneNumber;

    private AppCompatButton appCompatButtonRegister;
//    private AppCompatTextView appCompatTextViewLoginLink;

    private InputValidation inputValidation;

    private AppCompatTextView textViewLinkLogin;
    private ProgressBar progressBar;
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initObject();
        initListener();
    }

    private void initObject() {
        presenterRegisterLogic = new PresenterRegisterLogic(this);
        inputValidation = new InputValidation(registerActivity);
    }

    @SuppressLint("CutPasteId")
    private void initView() {
        textViewLinkLogin = findViewById(R.id.appCompatTextViewLoginLink);
        progressBar = findViewById(R.id.progressBarRegister);
//        nestedScrollView = findViewById(R.id.nestedScrollView);

        textInputLayoutFirstName = findViewById(R.id.textInputLayoutFirstName);
        textInputLayoutLastName = findViewById(R.id.textInputLayoutLastName);
        textInputLayoutUserName = findViewById(R.id.textInputLayoutUserName);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);
        textInputLayoutAddress = findViewById(R.id.textInputLayoutAddress);
        textInputLayoutPhoneNumber = findViewById(R.id.textInputLayoutPhoneNumber);

        textInputEditTextFirstName = findViewById(R.id.textInputEditTextFirstName);
        textInputEditTextLastName = findViewById(R.id.textInputEditTextLastName);
        textInputEditTextUserName = findViewById(R.id.textInputEditTextUserName);
        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword = findViewById(R.id.textInputEditTextConfirmPassword);
        textInputEditTextAddress = findViewById(R.id.textInputEditTextAddress);
        textInputEditTextPhoneNumber = findViewById(R.id.textInputEditTextPhoneNumber);

        appCompatButtonRegister = findViewById(R.id.appCompatButtonRegister);

//        appCompatTextViewLoginLink = findViewById(R.id.appCompatTextViewLoginLink);
    }

    private void initListener() {
        textViewLinkLogin.setOnClickListener(this);
        appCompatButtonRegister.setOnClickListener(this);
    }

    private void SetWaitingRegister(Boolean aBoolean){
        textViewLinkLogin.setEnabled(!aBoolean);
        progressBar.setVisibility(aBoolean?View.VISIBLE:View.GONE);
        findViewById(R.id.appCompatButtonRegister).setEnabled(!aBoolean);
    }

    @Override
    public void RegisterSuccess(String message) {
        SetWaitingRegister(false);
        Toast.makeText(registerActivity,message.isEmpty()?"Thành Công":message,Toast.LENGTH_SHORT).show();
        startActivity(new Intent(registerActivity,ViewLoginActivity.class));
        registerActivity.finish();
    }

    @Override
    public void RegisterFailed(String message) {
        SetWaitingRegister(false);
        Toast.makeText(registerActivity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        /////tranh viec bấm nút liên tuc trong 1s/////
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        /////////////////////////////////////////////
        switch (v.getId()){
            case R.id.appCompatTextViewLoginLink:
                    IntentViewLoginActivity();
                break;
            case R.id.appCompatButtonRegister:
                User userModel = new User();
                //<editor-fold desc="Check validation">
                if (!inputValidation.isInputEditTextFilled(textInputEditTextFirstName, textInputLayoutFirstName, getString(R.string.error_message_first_name))) {
                    break;
                } else {
                    userModel.setFirstName(textInputEditTextFirstName.getText().toString());
                }
                if (!inputValidation.isInputEditTextFilled(textInputEditTextLastName, textInputLayoutLastName, getString(R.string.error_message_last_name))) {
                    break;
                } else {
                    userModel.setLastName(textInputEditTextLastName.getText().toString());
                }

                if (!inputValidation.isInputEditTextFilled(textInputEditTextUserName, textInputLayoutUserName, getString(R.string.error_message_username))) {
                    break;
                } else {
                    userModel.setUsername(textInputEditTextUserName.getText().toString());
                }

                if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
                    break;
                } else{
                    userModel.setEmail(textInputEditTextEmail.getText().toString());
                }
                if (!inputValidation.isInputEditTextFilled(textInputEditTextPhoneNumber, textInputLayoutPhoneNumber, getString(R.string.error_message_phone_number))) {
                    break;
                } else{
                    userModel.setPhonenumber(textInputEditTextPhoneNumber.getText().toString());
                }
                if (!inputValidation.isInputEditTextFilled(textInputEditTextAddress, textInputLayoutAddress, getString(R.string.error_message_address))) {
                    break;
                } else {
                    userModel.setAddress(textInputEditTextAddress.getText().toString());
                }
                if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
                    break;
                } else{
                  userModel.setPassword(textInputEditTextPassword.getText().toString());
                }
                if(!inputValidation.isInputEditTextEmail(textInputEditTextEmail,textInputLayoutEmail,getString(R.string.error_invalid_email)))
                    break;
                if(!inputValidation.isPhoneNumber(textInputEditTextPhoneNumber,textInputLayoutPhoneNumber,getString(R.string.error_invalid_phone)))
                    break;
                if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword,
                        textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
                    break;
                } else userModel.setConfirmPassword(textInputEditTextConfirmPassword.getText().toString());
                //</editor-fold>
                if (ConnectivityReceiver.isConnected()) {
                    RequestRegisterAccount(userModel);
                } else {
                    String message = getString(R.string.message_internet_not_connected);
                    Toast.makeText(registerActivity, message, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void RequestRegisterAccount(User userModel) {
        HashMap<String, String> ResultHash = new HashMap<>();
        // GetUserDetail
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"register\", " +
                        "\"UserName\":\""+ userModel.getUsername()+"\"," +
                        "\"UserMail\":\""+ userModel.getEmail()+"\", " +
                        "\"UserFirstName\":\""+ userModel.getFirstName() +"\", " +
                        "\"UserLastName\":\""+ userModel.getLastName() +"\"," +
                        "\"UserPassword\":\""+ userModel.getPassword()+"\"," +
                        "\"UserAddress\":\""+ userModel.getAddress()+"\", " +
                        "\"UserPhone\":\""+ userModel.getPhonenumber()+"\" " +
                        "}";
        ResultHash.put(keyPost,valuePost);
        Toast.makeText(registerActivity, "Please Wait..", Toast.LENGTH_SHORT).show();
        presenterRegisterLogic.Register(registerActivity, ResultHash, HttpURL_API);
        SetWaitingRegister(true);
    }

    private void IntentViewLoginActivity() {
        Intent intentLogin = new Intent(registerActivity, ViewLoginActivity.class);
        startActivity(intentLogin);
        registerActivity.finish();
    }


    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private Toast backToast;
    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), R.string.message_exit, Toast.LENGTH_SHORT);
            backToast.show();
        }
        mBackPressed = System.currentTimeMillis();
    }
}
