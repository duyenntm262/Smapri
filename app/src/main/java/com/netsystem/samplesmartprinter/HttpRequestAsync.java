//package com.netsystem.samplesmartprinter;
//
//import android.annotation.SuppressLint;
//import android.os.AsyncTask;
//import android.util.Xml;
//import android.widget.Button;
//import android.widget.TextView;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.Proxy;
//import java.net.URL;
//
//public class HttpRequestAsync extends AsyncTask<URL, String, String> {
//
//    @SuppressLint("StaticFieldLeak")
//    private final TextView textView;
//    @SuppressLint("StaticFieldLeak")
//    private final Button button;
//
//    //コンストラクタ
//    public HttpRequestAsync(TextView textView, Button button) {
//        this.textView = textView;
//        this.button = button;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        textView.setText(null);
//    }
//
//    //HTTPリクエスト送信
//    @Override
//    protected String doInBackground(URL... urls) {
//        String responseData = "";
//        URL url = urls[0];
//        HttpURLConnection urlConnection = null;
//
//        try {
//            // GET送信（プロキシを使用しない）
//            urlConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
//
//            // HTTPステータスコードより送信結果を確認
//            int statusCode = urlConnection.getResponseCode();
//            String statusMessage = urlConnection.getResponseMessage();
//            publishProgress("HTTPステータスコード: " + statusCode + " " + statusMessage);
//            if (statusCode != HttpURLConnection.HTTP_OK) {
//                // 不正なアクションパスは"404"、別のリクエストを処理中の場合は"503"が返送されます
//                return "リクエスト送信に失敗";
//            }
//
//            // レスポンスボディよりリクエスト実行結果を取得(XML)
//            InputStream inputStream = urlConnection.getInputStream();
//            XmlPullParser xmlPullParser = Xml.newPullParser();
//            xmlPullParser.setInput(inputStream, "UTF-8");
//
//            int eventType = xmlPullParser.getEventType();
//            String result = "";
//            String message = "";
//
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//
//                if (eventType == XmlPullParser.START_TAG && "result".equals(xmlPullParser.getName())) {
//                    // <result>タグから処理結果(OK/NG)を確認
//                    result = xmlPullParser.nextText();
//                } else if (eventType == XmlPullParser.START_TAG && "message".equals(xmlPullParser.getName())) {
//                    // <message>タグから処理結果のメッセージ(SUCCESSFUL/エラー内容)を取得
//                    message = xmlPullParser.nextText();
//                }
//                eventType = xmlPullParser.next();
//            }
//            responseData = "処理結果: " + result + "\n" + "処理結果のメッセージ: " + message;
//
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            responseData = "XML解析エラー: " + e.getMessage();
//        } catch (IOException e) {
//            e.printStackTrace();
//            responseData = "リクエスト送信エラー: " + e.getMessage();
//        } finally {
//            // 切断
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//        }
//        return responseData;
//    }
//
//    @Override
//    protected void onProgressUpdate(String... progressMessage) {
//        textView.append(progressMessage[0] + "\n");
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        textView.append(result);
//        button.setEnabled(true);
//    }
//}


package com.netsystem.samplesmartprinter;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class HttpRequestAsync extends AsyncTask<URL, String, String> {

    @SuppressLint("StaticFieldLeak")
    private final TextView textView;
    @SuppressLint("StaticFieldLeak")
    private final Button button;

    public HttpRequestAsync(TextView textView, Button button) {
        this.textView = textView;
        this.button = button;
    }

    @Override
    protected void onPreExecute() {
        textView.setText(null);
    }

    @Override
    protected String doInBackground(URL... urls) {
        String responseData = "";
        URL url = urls[0];
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            int statusCode = urlConnection.getResponseCode();
            String statusMessage = urlConnection.getResponseMessage();
            publishProgress("HTTPステータスコード: " + statusCode + " " + statusMessage);

            if (statusCode != HttpURLConnection.HTTP_OK) {
                return "リクエスト送信に失敗";
            }

            InputStream inputStream = urlConnection.getInputStream();
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(inputStream, "UTF-8");

            int eventType = xmlPullParser.getEventType();
            String result = "";
            String message = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "result".equals(xmlPullParser.getName())) {
                    result = xmlPullParser.nextText();
                } else if (eventType == XmlPullParser.START_TAG && "message".equals(xmlPullParser.getName())) {
                    message = xmlPullParser.nextText();
                }
                eventType = xmlPullParser.next();
            }
            responseData = "処理結果: " + result + "\n" + "処理結果のメッセージ: " + message;

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            responseData = "XML解析エラー: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            responseData = "リクエスト送信エラー: " + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return responseData;
    }

    @Override
    protected void onProgressUpdate(String... progressMessage) {
        textView.append(progressMessage[0] + "\n");
    }

    @Override
    protected void onPostExecute(String result) {
        textView.append(result);
        button.setEnabled(true);
        Log.d("HttpRequestAsync", "HTTP Response: " + result);
    }
}
