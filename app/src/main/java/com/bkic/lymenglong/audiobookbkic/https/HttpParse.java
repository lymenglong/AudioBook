package com.bkic.lymenglong.audiobookbkic.https;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpParse {

    private String FinalHttpData = "";
    private StringBuilder stringBuilder = new StringBuilder();

    public String postRequest(HashMap<String, String> Data, String HttpUrlHolder) {

        try {

            URL url = new URL(HttpUrlHolder);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(14000);

            httpURLConnection.setConnectTimeout(14000);

            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setDoInput(true);

            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(

                    new OutputStreamWriter(outputStream, "UTF-8"));

            bufferedWriter.write(FinalDataParse(Data));

            bufferedWriter.flush();

            bufferedWriter.close();

            outputStream.close();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                httpURLConnection.getInputStream()
                        )
                );
                FinalHttpData = bufferedReader.readLine();
            }
            else {
                FinalHttpData = "Something Went Wrong";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return FinalHttpData;
    }

    private String FinalDataParse(HashMap<String, String> hashMap2) throws UnsupportedEncodingException {

        for(Map.Entry<String,String> map_entry : hashMap2.entrySet()){

            stringBuilder.append("&");

            stringBuilder.append(URLEncoder.encode(map_entry.getKey(), "UTF-8"));

            stringBuilder.append("=");

            stringBuilder.append(URLEncoder.encode(map_entry.getValue(), "UTF-8"));

        }

        return stringBuilder.toString();
    }
}