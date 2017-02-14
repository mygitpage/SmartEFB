package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
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

    // pull parcer factory reference
    XmlPullParserFactory xppf;

    // pull parser reference
    XmlPullParser xpp;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // context of xmlParser
    Context xmlContext;

    // true -> <main></main> block of xml file for 'normal' interaction is ok
    Boolean xmlMainBlockNormalOk = false;
    // true -> <main></main> block of xml file for 'make meeting' interaction is ok
    Boolean xmlMainBlockMeetingOk = false;
    // true -> <main></main> block of xml file for 'first' interachtion is ok
    Boolean xmlMainBlockFirstOk = false;



    public EfbXmlParser (Context tmpXmlContext) {

        // init context
        xmlContext = tmpXmlContext;

        // init prefs and editor
        prefs = xmlContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, xmlContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();




    }






    public void parseXmlInput () throws XmlPullParserException, IOException {

        // true -> master element of xml file was found
        Boolean masterElementFound = false;




        try {

            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();

            xpp = xppf.newPullParser();


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




                 if (eventType == XmlPullParser.START_TAG) {
                    Log.d("XMLParser","Start tag " + xpp.getName());


                    switch (xpp.getName().trim()) {

                        case ConstansClassXmlParser.xmlNameForMasterElement:
                            masterElementFound = true;
                            break;

                        case ConstansClassXmlParser.xmlNameForMain:
                            Log.d("XMLParser","Main Zeile " + xpp.getLineNumber());
                            if (xpp.getLineNumber() < 3 && masterElementFound) { // strict -> main element must begin line < 3
                                readMainTag();
                                Log.d("XMLParser","ZURUECK AUS READ MAIN");
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook:
                            Log.d("XMLParser","ConnectBook Zeile " + xpp.getLineNumber());
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement:
                            Log.d("XMLParser","OurArrangement Zeile " + xpp.getLineNumber());
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals:
                            Log.d("XMLParser","OurGoals Zeile " + xpp.getLineNumber());
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting:
                            Log.d("XMLParser","Meeting Zeile " + xpp.getLineNumber());
                            break;
                        case ConstansClassXmlParser.xmlNameForSettings:
                            Log.d("XMLParser","Settings Zeile " + xpp.getLineNumber());
                            break;
                    }


                }
                 /*
                 else if (eventType == XmlPullParser.END_TAG) {
                    Log.d("XMLParser","End tag " + xpp.getName());

                    //System.out.println("End tag " + xpp.getName());

                } else if (eventType == XmlPullParser.TEXT) {

                    if (xpp.getText().trim().length() > 0){
                        Log.d("XMLParser", "Text " + xpp.getText());
                    }


                    //System.out.println("Text " + xpp.getText());

                }
                */

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






    private void readMainTag() {

        Boolean readMoreXml = true;

        try {

            int eventType = xpp.next();




            while (readMoreXml) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("ReadMain","Start tag " + xpp.getName());


                    switch (xpp.getName().trim()) {

                        case ConstansClassXmlParser.xmlNameForMain_AppId: // xml data normal

                            eventType = xpp.next();

                            if (eventType == XmlPullParser.TEXT) {

                                if (xpp.getText().trim().length() > 0) { // check if appid from xml > 0
                                    String tmpAppId = xpp.getText().trim();

                                    //Log.d("ReadMain","APPID PREFS: " + prefs.getString(ConstansClassSettings.namePrefsAppId, "TEST"));

                                    //
                                    if (tmpAppId.equals(prefs.getString(ConstansClassSettings.namePrefsAppId, ""))) { // check if appid local equal appid from xml

                                        //Log.d("ReadMain","APPID GLEICH!!!!!!!!!!!!!!!!!");

                                        xmlMainBlockNormalOk = true; // set xmlBlockOk = true

                                    }
                                }
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForMain_MeetingId: // xml data make meeting

                            eventType = xpp.next();

                            if (eventType == XmlPullParser.TEXT) {

                                if (xpp.getText().trim().length() > 0) { // check if meetingid from xml > 0
                                    String tmpMeetingId = xpp.getText().trim();

                                    //Log.d("ReadMain","MEETINGID PREFS: " + prefs.getString(ConstantsClassMeeting.namePrefsMeetingId, "TEST"));


                                    if (tmpMeetingId.equals(prefs.getString(ConstantsClassMeeting.namePrefsMeetingId, ""))) { // check if appid local equal appid from xml



                                        xmlMainBlockMeetingOk = true; // set xmlBlockMeetingOk = true

                                    }
                                }
                            }






                            break;

                        case ConstansClassXmlParser.xmlNameForMain_ConnectNumber:
                            break;

                    }


                } else if (eventType == XmlPullParser.END_DOCUMENT) {
                        Log.d("ReadMain","END OF DOCUMENT");
                        readMoreXml = false;


                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.d("ReadMain", "End tag " + xpp.getName());


                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMain)) {

                        readMoreXml = false;


                    }

                }

                eventType = xpp.next();



            }



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
