package com.bkic.lymenglong.audiobookbkic.help;


import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;


public class PresenterHelp implements PresenterHelpImp {
    private HelpActivity helpActivity;

    PresenterHelp(HelpActivity helpActivity) {
        this.helpActivity = helpActivity;
    }

    @Override
    public TextView ShowHelp(TextView readFile) {
        //region Read File Text
        String text = "";
        try {
            InputStream inputStream = helpActivity.getAssets().open("help.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            text = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            helpActivity.ShowHelpFailed();
        }
        readFile.setText(text);
        //endregion
        helpActivity.ShowHelpDone();
        return readFile;
    }
}
