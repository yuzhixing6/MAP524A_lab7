package com.seneca.yuzhi.lab7web;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {

    TextView textView;
    EditText editText;

    private class AccessWebServicesTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            InputStream in;
            String strDefinition = "";
            try {
                in = openHttpConnection(urls[0]);
                XmlPullParser parser = Xml.newPullParser();
                try {
                    parser.setInput(in, null);
                    int eventType = parser.getEventType();
                    String text = "";
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String  tagName = parser.getName();
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                break;
                            case XmlPullParser.TEXT:
                                text = parser.getText();
                                break;
                            case XmlPullParser.START_TAG:
                                // name = parser.getName();
                                break;
                            case XmlPullParser.END_TAG:
                                if (tagName.equalsIgnoreCase("WordDefinition")) {
                                    System.out.println(text);
                                    strDefinition += text;
                                }
                                break;
                        }
                        eventType = parser.next();
                    } // end while
                } catch (XmlPullParserException ex) {
                    ex.printStackTrace();
                }
                in.close();
            } catch (IOException e1) {
                Log.d("NetworkingActivity", e1.getLocalizedMessage());
            }
            return strDefinition;
        }

        protected void onPostExecute(String result) {
            TextView tv = (TextView) findViewById(R.id.result);
            tv.setText(result);
        }

        private InputStream openHttpConnection(String urlString) throws IOException {
            InputStream in = null;
            int response;
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            if (!(conn instanceof HttpURLConnection))
                throw new IOException("Not an HTTP connection");
            try{
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                response = httpConn.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                }
            }
            catch (Exception ex) {
                Log.d("Networking", ex.getLocalizedMessage());
                throw new IOException("Error connecting");
            }
            return in;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.result);
        editText = (EditText) findViewById(R.id.input);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void search(View view) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


            // fetch data
            AccessWebServicesTask downloadTextTask = new AccessWebServicesTask();
            downloadTextTask.execute("http://services.aonaware.com/DictService/DictService.asmx/Define?word="+
                    editText.getText().toString());


    }
}