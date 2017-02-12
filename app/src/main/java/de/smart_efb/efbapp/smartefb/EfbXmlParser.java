package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Created by ich on 06.02.2017.
 */
public class EfbXmlParser {






    public void parseXmlInput (Context xmlContext) throws XmlPullParserException, IOException {


        try {
            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = xppf.newPullParser();


            AssetManager manager = xmlContext.getResources().getAssets(); //getBaseContext().getAssets(); //getBaseContext().getResources().getAssets();
            InputStream input = manager.open("configuration.xml");
            xpp.setInput(input, null);
            int eventType = xpp.getEventType();


            //AssetManager assetManager = getAssets();
            //InputStream is = assetManager.open("configuration.xml");
            // http://www.mysamplecode.com/2012/10/android-parse-xml-file-from-assets-or.html


            //xpp.setInput( new StringReader( "<foo>Hello World!"+ConstantsClassMeeting.numberSimultaneousMeetings+"</foo>" ) );
            //int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("XMLParser","Start document");

                } else if (eventType == XmlPullParser.START_TAG) {
                    Log.d("XMLParser","Start tag " + xpp.getName());



                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.d("XMLParser","End tag " + xpp.getName());

                    //System.out.println("End tag " + xpp.getName());

                } else if (eventType == XmlPullParser.TEXT) {

                    Log.d("XMLParser","Text " + xpp.getText());

                    //System.out.println("Text " + xpp.getText());

                }

                eventType = xpp.next();

            }

            Log.d("XMLParser","End document");
            //System.out.println("End document");
        }
        catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }






}
