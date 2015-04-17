package com.seneca.yuzhi.lab7web;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HandleXML {

    private String MYURL = null;
    private String worlddefination = null;
    private XmlPullParserFactory xmlFactoryObject;
    private InputStream in;
    public volatile boolean parsingComplete = true;
    public HandleXML(String url){
        this.MYURL = url;
    }
    public String getworkd() {

        return worlddefination;
    }
    public void setString( String string) {

        worlddefination = string;
    }
    public void parseXMLAndStoreIt( XmlPullParser myParser){

    int event;
         String text = null;
        try {
            myParser.setInput(in, null);
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();
                switch (event){
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:

                        if(name.equalsIgnoreCase("WordDefinition")){
                           setString("WordDefinition");

                }
                        break;
                }
                event = myParser.next();
            }
            parsingComplete = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fetch(){

    Thread thread = new Thread( new Runnable() {
        @Override
        public void run() {
            int response;
            try{
                URL url = new URL(MYURL);
                URLConnection conn = url.openConnection();
                if (!(conn instanceof HttpURLConnection))
                    throw new IOException("Not an HTTP connection");

                HttpURLConnection  httpConn= (HttpURLConnection) conn;
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
               in = httpConn.getInputStream();
                xmlFactoryObject =  XmlPullParserFactory.newInstance();
                XmlPullParser myparser = xmlFactoryObject.newPullParser();
                myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES
                        , false);
                myparser.setInput(in, null);
                parseXMLAndStoreIt(myparser);
               in.close();


            }catch (Exception e){
                e.printStackTrace();
            }

        }
    });
        thread.start();
    }


}
