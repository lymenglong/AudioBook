package com.bkic.lymenglong.audiobookbkic.speechRecognizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SpeechRecognizerManager {
    private final static String TAG = "SpeechRecognizerManager";

    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    private Context mContext;

    protected boolean mIsListening;
    protected String language = "vi"; //for Vietnamese
    //https://cloud.google.com/speech-to-text/docs/languages

    private onResultsReady mListener;

    public SpeechRecognizerManager(Context context, onResultsReady listener)
    {
        try {
            mListener = listener;
        } catch(ClassCastException e) {
            Log.e(TAG, e.toString());
        }

        mContext = context;

        Log.d(TAG, "Start...");
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());

        // Create new intent
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true); // For streaming result
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L); // 2000 ms

        // Start listening
        startListening();
    }

    private void startListening()
    {
        if (!mIsListening) {
            mIsListening = true;
            Log.i(TAG, "Listening...");
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    }

    public void stop() {
        if (mIsListening && mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }

        mIsListening = false;
    }

    public void destroy()
    {
        mIsListening = false;
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }

    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {
        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.i(TAG, "Buffer Received");
        }

        @Override
        public void onEndOfSpeech()
        {}

        @Override
        public synchronized void onError(int error)
        {
            if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                if (mListener != null) {
                    ArrayList<String> errorList = new ArrayList<String>(1);
                    errorList.add("ERROR RECOGNIZER BUSY");
                    Toast.makeText(mContext, "ERROR RECOGNIZER BUSY", Toast.LENGTH_SHORT).show();
                    if (mListener != null) {
                        mListener.onResults(errorList);
                    }
                }

                return;
            }

            if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                if (mListener != null) {
                    mListener.onResults(null);
                    Toast.makeText(mContext, "CANNOT FOUND ERROR", Toast.LENGTH_SHORT).show();
                }
            }

            if (error == SpeechRecognizer.ERROR_NETWORK) {
                ArrayList<String> errorList = new ArrayList<String>(1);
                errorList.add("STOPPED LISTENING");
                if (mListener != null) {
                    mListener.onResults(errorList);
                    Toast.makeText(mContext, "NETWORK ERROR", Toast.LENGTH_SHORT).show();
                }
            }

            Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {}

        @Override
        public void onPartialResults(Bundle partialResults)
        {
            if (partialResults != null && mListener != null) {
                ArrayList<String> texts = partialResults.getStringArrayList("android.speech.extra.UNSTABLE_TEXT");
                mListener.onStreamingResult(texts);
            }
        }

        @Override
        public void onReadyForSpeech(Bundle params) {}

        @Override
        public void onResults(Bundle results)
        {
            if (results != null && mListener != null) {
                ArrayList<String> ahihi = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                mListener.onResults(ahihi);
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {}

    }

    public boolean ismIsListening() {
        return mIsListening;
    }


    public interface onResultsReady
    {
        void onResults(ArrayList<String> results);
        void onStreamingResult(ArrayList<String> partialResults);
    }


/*  Giải thích 1 chút về đoạn code trên:
    từ Activity chúng ta khởi tạo một SpeechRecognizerManager với 2 tham số Context và onResultsReady
    Trong constructor của SpeechRecognizerManager, chúng ta tạo mới một SpeechRecognizer sử dụng SpeechRecognitionListener ở trên.
    Tiếp theo tạo một Intent và startListening. Thế là xong @@
*/

    /*private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi");//Locale.getDefault()
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    *//**
     * Receiving speech input
     * *//*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

        }
    }*/

}