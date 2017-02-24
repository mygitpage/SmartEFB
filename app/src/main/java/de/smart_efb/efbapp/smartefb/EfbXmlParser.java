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

    // reference to the DB
    DBAdapter myDb;

    // true -> <main></main> block of xml file for 'normal' interaction is ok
    Boolean xmlMainBlockNormalOk = false;
    // true -> <main></main> block of xml file for 'make meeting' interaction is ok
    Boolean xmlMainBlockMeetingOk = false;
    // true -> <main></main> block of xml file for 'first' interachtion is ok
    Boolean xmlMainBlockFirstOk = false;


    // refresh activity ourarrangement
    Boolean refreshOurArrangement = false;
        // refresh activity ourarrangement fragement now
        Boolean refreshOurArrangementNow = false;
        // refresh activity ourarrangement fragement sketch
        Boolean refreshOurArrangementSketch = false;
        // refresh activity ourarrangement fragement now comment
        Boolean refreshOurArrangementNowComment = false;
        // refresh activity ourarrangement fragement sketch comment
        Boolean refreshOurArrangementSketchComment = false;
        // refresh settings of our arrangement
        Boolean refreshOurArrangementSettings = false;
        // refresh data of evaluation process
        Boolean refreshOurArrangementSettingsEvaluationProcess = false;
        // refresh data of comment process
        Boolean refreshOurArrangementSettingsCommentProcess = false;
        // refresh data of sketch comment process
        Boolean refreshOurArrangementSettingsSketchCommentProcess = false;
        // refresh author name of sketch arragement
        Boolean refreshOurArrangementSettingsSketchArrangmentAuthorName = false;
        // refresh current date of sketch arrangement
        Boolean refreshOurArrangementSettingsSketchCurrentDateOfSketchArrangement = false;
        // refresg current date of arrangement
        Boolean refreshOurArrangementSettingsCurrentDateOfArrangement = false;




    public EfbXmlParser (Context tmpXmlContext) {

        // init context
        xmlContext = tmpXmlContext;

        // init the DB
        myDb = new DBAdapter(xmlContext);

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

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("XMLParser","Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {

                        case ConstansClassXmlParser.xmlNameForMasterElement:
                            masterElementFound = true;
                            break;

                        case ConstansClassXmlParser.xmlNameForMain:
                            if (xpp.getLineNumber() < 3 && masterElementFound) { // strict -> main element must begin line < 3
                                readMainTag();
                                Log.d("XMLParser","ZURUECK AUS READ MAIN");
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook:
                            Log.d("XMLParser","ConnectBook Zeile " + xpp.getLineNumber());
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement:
                            readOurArrangementTag();
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


    //
    // Begin read main element -----------------------------------------------------------------------------------
    //

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

                                    if (tmpAppId.equals(prefs.getString(ConstansClassSettings.namePrefsAppId, ""))) { // check if appid local equal appid from xml

                                        //Log.d("ReadMain","APPID GLEICH!!!!!!!!!!!!!!!!!");

                                        xmlMainBlockNormalOk = true; // set xmlBlockNormalOk = true
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

                        case ConstansClassXmlParser.xmlNameForMain_ConnectId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get connectId text

                                if (xpp.getText().trim().length() > 0) { // check if connectId from xml > 0
                                    int tmpConnectId = Integer.valueOf(xpp.getText().trim());
                                    if (tmpConnectId == prefs.getInt(ConstansClassSettings.namePrefsRandomNumberForConnection, 0)) {

                                        // get end tag from xml element connectId
                                        eventType = xpp.next();
                                        if (eventType == XmlPullParser.END_TAG) { // get end tag connectID
                                            if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMain_ConnectId)) {

                                                do { // look for next start tag
                                                    eventType = xpp.next();
                                                    if (eventType == XmlPullParser.END_DOCUMENT) {break;}
                                                } while (eventType != XmlPullParser.START_TAG);

                                                if (eventType == XmlPullParser.START_TAG) { // get start tag appId

                                                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMain_AppId)) {
                                                        eventType = xpp.next();
                                                        if (eventType == XmlPullParser.TEXT) { // get appId text
                                                            if (xpp.getText().trim().length() > 0) { // check if appid from xml > 0
                                                                String tmpAppId = xpp.getText().trim();
                                                                // write appId to prefs
                                                                prefsEditor.putString(ConstansClassSettings.namePrefsAppId, "");
                                                                // set connection status to connect
                                                                prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus,2);
                                                                prefsEditor.commit();

                                                                xmlMainBlockFirstOk = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

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

    // End read main element -----------------------------------------------------------------------------------





    //
    // Begin read our arrangement -----------------------------------------------------------------------------------
    //

    // read element ourarrangement
    private void readOurArrangementTag() {

        Boolean parseAnymore = true;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readOurArrangementTag", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now:
                            readOurArrangementTag_Now();
                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch:
                            readOurArrangementTag_Sketch();
                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment:
                            readOurArrangementTag_NowComment();
                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment:
                            readOurArrangementTag_SketchComment();
                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate:
                            readOurArrangementTag_Evaluate();
                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings:
                            readOurArrangementTag_Settings();
                            break;

                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {parseAnymore = false;
                    Log.d("readOurArrangementTag", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of ourarrangement
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement)) {

                        Log.d("readOurArrangementTag", "End Tag ourarrangement gefunden!");
                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // read tag our arrangement now and push to database
    private void readOurArrangementTag_Now() {

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our arrangement now tag
        Boolean error = false;

        // tmp data for database insert
        String tmpArrangementText = "";
        String tmpAuthorName = "";
        Long tmpArrangementTime = 0L;
        Boolean tmpSketchCurrent = false;
        String tmpOrder = "";
        String tmpOldMd5 = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readOurArrTag_NOW_MORE", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) {
                                        error = true;
                                        tmpOrder = "";
                                    }
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_ArrangementText:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementText text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementText from xml > 0
                                    tmpArrangementText = xpp.getText().trim();

                                    Log.d("Arrangement_NOW::MD5","MD5:"+EfbHelperClass.md5(tmpArrangementText));


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_AuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get authorName text
                                if (xpp.getText().trim().length() > 0) { // check if authorName from xml > 0
                                    tmpAuthorName = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_ArrangementTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementTime text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementTime from xml > 0
                                    tmpArrangementTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_SketchCurrent:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get sketchCurrent text, only 0 is possible because 1 is sketch arrangement, look readOurArrangementTag_Sketch()
                                if (xpp.getText().trim().length() > 0) { // check if sketchCurrent from xml > 0
                                    if (xpp.getText().trim().equals("0")) { // arrangement is a current arrangement?
                                        tmpSketchCurrent = false;
                                    } else {
                                        error = true;
                                    }
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_OldMd5:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get oldMd5 text
                                if (xpp.getText().trim().length() > 0) { // check if oldMd5 from xml > 0
                                    tmpOldMd5 = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;

                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {parseAnymore = false;
                    Log.d("ABBRUCH!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of ourarrangement now
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_Now)) {

                        // check all data for arrangement now correct?
                        if (!error) {

                            Log.d("NOW__DB","Te:"+tmpArrangementText+" - Au:"+tmpAuthorName+" - ATi:"+tmpArrangementTime+" - STi"+tmpSketchCurrent);

                            // our arrangement order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpArrangementText.length() > 0 && tmpAuthorName.length() > 0 && tmpArrangementTime > 0) {
                                // insert new arrangement in DB
                                myDb.insertRowOurArrangement(tmpArrangementText, tmpAuthorName, tmpArrangementTime, true, tmpSketchCurrent, 0, 4);

                                // refresh activity ourarrangement and fragement now
                                refreshOurArrangement = true;
                                refreshOurArrangementNow = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our arrangement order -> delete entry?

                                // delete arrangement in DB
                                myDb.deleteRowOurArrangement(tmpOldMd5);

                                // refresh activity ourarrangement and fragement now
                                refreshOurArrangement = true;
                                refreshOurArrangementNow = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpArrangementText.length() > 0 && tmpAuthorName.length() > 0 && tmpArrangementTime > 0) { // our arrangement order -> update entry?

                                // update arrangement in DB
                                myDb.updateRowOurArrangement(tmpArrangementText, tmpAuthorName, tmpArrangementTime, true, tmpSketchCurrent, 0, 4, tmpOldMd5);

                                // refresh activity ourarrangement and fragement now
                                refreshOurArrangement = true;
                                refreshOurArrangementNow = true;
                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // read tag our arrangement now comment and push to database ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void readOurArrangementTag_NowComment() {
        Log.d("read_NOWCOMMENT", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our arrangement now tag
        Boolean error = false;

        // tmp data for database insert
        String tmpCommentText = "";
        String tmpAuthorName = "";
        Long tmpCommentTime = 0L;
        int tmpArrangementId = 0;
        Long tmpArrangementTime = 0L;
        String tmpOrder = "";
        String tmpOldMd5 = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readOurTag_NOW_COMMENT", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) {
                                        error = true;
                                        tmpOrder = "";
                                    }
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_Comment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get comment text
                                if (xpp.getText().trim().length() > 0) { // check if commentText from xml > 0
                                    tmpCommentText = xpp.getText().trim();

                                    Log.d("Arrangement_NOWComment","MD5:"+EfbHelperClass.md5(tmpCommentText));


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_AuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get authorName text
                                if (xpp.getText().trim().length() > 0) { // check if authorName from xml > 0
                                    tmpAuthorName = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_CommentTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get commentTime text
                                if (xpp.getText().trim().length() > 0) { // check if commentTime from xml > 0
                                    tmpCommentTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_ArrangementId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementId text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementId from xml > 0
                                    tmpArrangementId = Integer.valueOf(xpp.getText().trim()); // make int from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_DateOfArrangement:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementTime text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementTime from xml > 0
                                    tmpArrangementTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_OldMd5:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get oldMd5 text
                                if (xpp.getText().trim().length() > 0) { // check if oldMd5 from xml > 0
                                    tmpOldMd5 = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;

                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {parseAnymore = false;
                    Log.d("ABBRUCH!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of ourarrangement now
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_NowComment)) {

                        // check all data for arrangement now correct?
                        if (!error) {

                            Log.d("NowComment_DB","C:"+tmpCommentText+" - Au:"+tmpAuthorName+" - CTi:"+tmpCommentTime+" - AId"+tmpArrangementId+" - CoA:"+tmpArrangementTime);

                            // our arrangement now comment order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpArrangementId >= 0 && tmpArrangementTime > 0) {
                                // insert new comment in DB
                                myDb.insertRowOurArrangementComment(tmpCommentText, tmpAuthorName, tmpCommentTime, tmpArrangementId, true, tmpArrangementTime, 4);

                                // refresh activity ourarrangement and fragment now comment
                                refreshOurArrangement = true;
                                refreshOurArrangementNowComment = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our arrangement now comment order -> delete entry?

                                // delete arrangement comment in DB
                                myDb.deleteRowOurArrangementComment(tmpOldMd5);

                                // refresh activity ourarrangement and fragment now comment
                                refreshOurArrangement = true;
                                refreshOurArrangementNowComment = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpArrangementId >= 0 && tmpArrangementTime > 0) { // our arrangement now comment order -> update entry?

                                // update arrangement comment in DB
                                myDb.updateRowOurArrangementComment(tmpCommentText, tmpAuthorName, tmpCommentTime, tmpArrangementId, true,  tmpArrangementTime, 4, tmpOldMd5);

                                // refresh activity ourarrangement and fragment now comment
                                refreshOurArrangement = true;
                                refreshOurArrangementNowComment = true;
                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // read tag our arrangement sketch and push to database ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void readOurArrangementTag_Sketch() {
        Log.d("read_SKETCH", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our arrangement now tag
        Boolean error = false;

        // tmp data for database insert
        String tmpArrangementText = "";
        String tmpAuthorName = "";
        Long tmpSketchTime = 0L;
        Boolean tmpSketchCurrent = false;
        String tmpOrder = "";
        String tmpOldMd5 = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readOurArrTag_SKETCH", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) {
                                        error = true;
                                        tmpOrder = "";
                                    }
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_ArrangementText:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementText text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementText from xml > 0
                                    tmpArrangementText = xpp.getText().trim();


                                    Log.d("Arrangement_SKETCH::MD5","MD5:"+EfbHelperClass.md5(tmpArrangementText));

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_AuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get authorName text
                                if (xpp.getText().trim().length() > 0) { // check if authorName from xml > 0
                                    tmpAuthorName = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_SketchCurrent:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get sketchCurrent text, only 1 is possible because 0 is current arrangement, look readOurArrangementTag_Now()
                                if (xpp.getText().trim().length() > 0) { // check if sketchCurrent from xml > 0
                                    if (xpp.getText().trim().equals("1")) { // arrangement is a sketch arrangement?
                                        tmpSketchCurrent = true;
                                    } else {
                                        error = true;
                                    }
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_SketchTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get sketchTime text
                                if (xpp.getText().trim().length() > 0) { // check if sketchTime from xml > 0
                                    tmpSketchTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_OldMd5:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get oldMd5 text
                                if (xpp.getText().trim().length() > 0) { // check if oldMd5 from xml > 0
                                    tmpOldMd5 = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {parseAnymore = false;
                    Log.d("ABBRUCH!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of ourarrangement now
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_Sketch)) {

                        // check all data for arrangement sketch correct?
                        if (!error) {

                            Log.d("SKETCH__DB","Te:"+tmpArrangementText+" - Au:"+tmpAuthorName+" - SATi:"+tmpSketchTime+" - STi"+tmpSketchCurrent);

                            // our arrangement sketch order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New)) {
                                // insert new sketch arrangement in DB
                                myDb.insertRowOurArrangement(tmpArrangementText,tmpAuthorName,0,true,tmpSketchCurrent,tmpSketchTime, 4);

                                // refresh activity ourarrangement and fragement sketch
                                refreshOurArrangement = true;
                                refreshOurArrangementSketch = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) { // our arrangement sketch order -> delete entry?

                                // delete arrangement in DB
                                myDb.deleteRowOurArrangement(tmpOldMd5);

                                // refresh activity ourarrangement and fragement sketch
                                refreshOurArrangement = true;
                                refreshOurArrangementSketch = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update)) { // our arrangement sketch order -> update entry?

                                // update sketch arrangement with tmpOldMd5
                                myDb.updateRowOurArrangement(tmpArrangementText, tmpAuthorName, 0, true, tmpSketchCurrent, tmpSketchTime, 4, tmpOldMd5);

                                // refresh activity ourarrangement and fragement sketch
                                refreshOurArrangement = true;
                                refreshOurArrangementSketch = true;
                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }







    }

    private void readOurArrangementTag_SketchComment() {
        Log.d("read_SKETCHCOMMENT", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our arrangement sketch comment tag
        Boolean error = false;

        // tmp data for database insert
        String tmpCommentText = "";
        int tmpREsultQuestionA = 0;
        int tmpREsultQuestionB = 0;
        int tmpREsultQuestionC = 0;
        String tmpAuthorName = "";
        Long tmpCommentTime = 0L;
        int tmpArrangementId = 0;
        Long tmpArrangementTime = 0L;
        String tmpOrder = "";
        String tmpOldMd5 = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readOur_SKETCH_COMMENT", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) {
                                        error = true;
                                        tmpOrder = "";
                                    }

                                    Log.d("SKETCH COMMENT","ORDER: "+tmpOrder);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_Comment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get comment text
                                if (xpp.getText().trim().length() > 0) { // check if commentText from xml > 0
                                    tmpCommentText = xpp.getText().trim();

                                    Log.d("Arrang_SKETCHComment","MD5:"+EfbHelperClass.md5(tmpCommentText));


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ResultQuestionA:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get result question A text
                                if (xpp.getText().trim().length() > 0) { // check if result question A from xml > 0
                                    tmpREsultQuestionA = Integer.valueOf(xpp.getText().trim()); // make int from xml-text

                                    Log.d("readOur_SKETCH_COMMENT", "Question A: " + tmpREsultQuestionA);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ResultQuestionB:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get result question B text
                                if (xpp.getText().trim().length() > 0) { // check if result question B from xml > 0
                                    tmpREsultQuestionB = Integer.valueOf(xpp.getText().trim()); // make int from xml-text

                                    Log.d("readOur_SKETCH_COMMENT", "Question B: " + tmpREsultQuestionB);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ResultQuestionC:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get result question C text
                                if (xpp.getText().trim().length() > 0) { // check if result question C from xml > 0
                                    tmpREsultQuestionC = Integer.valueOf(xpp.getText().trim()); // make int from xml-text

                                    Log.d("readOur_SKETCH_COMMENT", "Question C: " + tmpREsultQuestionC);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_AuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get authorName text
                                if (xpp.getText().trim().length() > 0) { // check if authorName from xml > 0
                                    tmpAuthorName = xpp.getText().trim();

                                    Log.d("readOur_SKETCH_COMMENT", "Author Name: " + tmpAuthorName);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_CommentTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get commentTime text
                                if (xpp.getText().trim().length() > 0) { // check if commentTime from xml > 0
                                    tmpCommentTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text


                                    Log.d("readOur_SKETCH_COMMENT", "Comment Time: " + tmpCommentTime);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ArrangementId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementId text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementId from xml > 0
                                    tmpArrangementId = Integer.valueOf(xpp.getText().trim()); // make int from xml-text

                                    Log.d("readOur_SKETCH_COMMENT", "Arrangement ID: " + tmpArrangementId);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_DateOfArrangement:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementTime text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementTime from xml > 0
                                    tmpArrangementTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text


                                    Log.d("readOur_SKETCH_COMMENT", "Arrangement Date: " + tmpArrangementTime);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_OldMd5:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get oldMd5 text
                                if (xpp.getText().trim().length() > 0) { // check if oldMd5 from xml > 0
                                    tmpOldMd5 = xpp.getText().trim();

                                    Log.d("readOur_SKETCH_COMMENT", "Old MD5: " + tmpOldMd5);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;

                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {parseAnymore = false;
                    Log.d("ABBRUCH!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of ourarrangement sketch comment
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment)) {

                        // check all data for arrangement now correct?
                        if (!error) {

                            Log.d("SketchComment_DB","C:"+tmpCommentText+" - Au:"+tmpAuthorName+" - CTi:"+tmpCommentTime+" - AId"+tmpArrangementId+" - CoA:"+tmpArrangementTime);

                            // our arrangement sketch comment order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpArrangementId >= 0 && tmpArrangementTime > 0 && tmpREsultQuestionA >= 0 && tmpREsultQuestionB >= 0 && tmpREsultQuestionC >= 0) {

                                Log.d("SKETCH COMMENT","NEW AUSführen");

                                // insert new comment in DB
                                myDb.insertRowOurArrangementSketchComment(tmpCommentText, tmpREsultQuestionA, tmpREsultQuestionB, tmpREsultQuestionC, tmpAuthorName, tmpCommentTime, tmpArrangementId, true, tmpArrangementTime, 4);

                                // refresh activity ourarrangement and fragment sketch comment
                                refreshOurArrangement = true;
                                refreshOurArrangementSketchComment = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our arrangement sketch comment order -> delete entry?

                                Log.d("SKETCH COMMENT","DELETE AUSführen");

                                // delete arrangement sketch comment in DB
                                myDb.deleteRowOurArrangementSketchComment(tmpOldMd5);

                                // refresh activity ourarrangement and fragment sketch comment
                                refreshOurArrangement = true;
                                refreshOurArrangementSketchComment = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpArrangementId >= 0 && tmpArrangementTime > 0 && tmpREsultQuestionA >= 0 && tmpREsultQuestionB >= 0 && tmpREsultQuestionC >= 0) { // our arrangement sketch comment order -> update entry?

                                Log.d("SKETCH COMMENT","UPDATE AUSführen");

                                // update arrangement sketch comment in DB
                                myDb.updateRowOurArrangementSketchComment(tmpCommentText, tmpREsultQuestionA, tmpREsultQuestionB, tmpREsultQuestionC, tmpAuthorName, tmpCommentTime, tmpArrangementId, true,  tmpArrangementTime, 4, tmpOldMd5);

                                // refresh activity ourarrangement and fragment sketch comment
                                refreshOurArrangement = true;
                                refreshOurArrangementSketchComment = true;
                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }











    private void readOurArrangementTag_Evaluate() {
        Log.d("read_EVALUATE", "Zeile " + xpp.getLineNumber());

        // TODO: Implematation, when needed!!!!


    }

    private void readOurArrangementTag_Settings() {
        Log.d("read_SETTINGS", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our arrangement settings comment tag
        Boolean error = false;

        // tmp data for prefs and database insert
        String tmpOrder = "";
        int tmpEvaluatePauseTime = 0;
        int tmpEvaluateActiveTime = 0;
        Long tmpEvaluateStartDate = 0L;
        Long tmpEvaluateEndDate = 0L;

        int tmpCommentMaxComment = 0;
        int tmpCommentCountComment = 0;
        Long tmpCommentCountCommentSinceTime = 0L;

        int tmpSketchCommentMaxComment = 0;
        int tmpSketchCommentCountComment = 0;
        Long tmpSketchCommentCountCommentSinceTime = 0L;

        String tmpSketchAuthorName = "";

        Long tmpSketchCurrentDateOfArrangement = 0L;
        Long tmpCurrentDateOfArrangement = 0L;


        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readOur_SETTINGS", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) {
                                        error = true;
                                        tmpOrder = "";
                                    }

                                    Log.d("Arr Settings","ORDER: "+tmpOrder);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_EvaluatePauseTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get evaluate pause time
                                if (xpp.getText().trim().length() > 0) { // check if pause time from xml > 0
                                    tmpEvaluatePauseTime = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","EvaluatePauseTime"+tmpEvaluatePauseTime);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_EvaluateActiveTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get evaluate active time
                                if (xpp.getText().trim().length() > 0) { // check if active time from xml > 0
                                    tmpEvaluateActiveTime = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","EvaluateActiveTime"+tmpEvaluateActiveTime);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_EvaluateStartDate:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get evaluate start date
                                if (xpp.getText().trim().length() > 0) { // check if start date from xml > 0
                                    tmpEvaluateStartDate = Long.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","EvaluateStartDate"+tmpEvaluateStartDate);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_EvaluateEndDate:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get evaluate end date
                                if (xpp.getText().trim().length() > 0) { // check if end date from xml > 0
                                    tmpEvaluateEndDate = Long.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","EvaluateEndDate"+tmpEvaluateEndDate);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_CommentMaxComment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max comment
                                if (xpp.getText().trim().length() > 0) { // check if max comment from xml > 0
                                    tmpCommentMaxComment = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","CommentMaxComment"+tmpCommentMaxComment);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_CommentCountComment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get count comment
                                if (xpp.getText().trim().length() >= 0) { // check if count comment from xml >= 0
                                    tmpCommentCountComment = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","CommentCountComment"+tmpCommentCountComment);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_CommentCountCommentSinceTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get since time
                                if (xpp.getText().trim().length() > 0) { // check if since time from xml > 0
                                    tmpCommentCountCommentSinceTime = Long.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","CommentCountCommentSinceTime"+tmpCommentCountCommentSinceTime);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_SketchCommentMaxComment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max comment sketch
                                if (xpp.getText().trim().length() > 0) { // check if max comment sketch from xml > 0
                                    tmpSketchCommentMaxComment = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","SketchCommentMaxComment"+tmpSketchCommentMaxComment);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_SketchCommentCountComment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get count comment sketch
                                if (xpp.getText().trim().length() >= 0) { // check if count comment sketch from xml >= 0
                                    tmpSketchCommentCountComment = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","SketchCommentCountComment"+tmpSketchCommentCountComment);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_SketchCommentCountCommentSinceTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get since time sketch
                                if (xpp.getText().trim().length() > 0) { // check if since time sketch from xml > 0
                                    tmpSketchCommentCountCommentSinceTime = Long.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","SketchCommentCountCommentSinceTime"+tmpSketchCommentCountCommentSinceTime);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_SketchArrangementAuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get author name sketch
                                if (xpp.getText().trim().length() > 0) { // check if author name sketch from xml > 0
                                    tmpSketchAuthorName = xpp.getText().trim();

                                    Log.d("Arrang_Settings","SketchAuthor Name"+tmpSketchAuthorName);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_SketchCurrentDateOfSketchArrangement:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get sketch current date of arragement
                                if (xpp.getText().trim().length() > 0) { // check if sketch date of arrangement from xml > 0
                                    tmpSketchCurrentDateOfArrangement = Long.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","SketchCurrentDateOfArrangement"+tmpSketchCurrentDateOfArrangement);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_CurrentDateOfArrangement:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get current date of arrangement
                                if (xpp.getText().trim().length() > 0) { // check if date of arrangement from xml > 0
                                    tmpCurrentDateOfArrangement = Long.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","CurrentDateOfArrangement"+tmpCurrentDateOfArrangement);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {parseAnymore = false;
                    Log.d("ABBRUCH!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of ourarrangement settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_Settings)) {

                        // check all data for arrangement settings correct?
                        if (!error) {


                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) ) { // our arrangement settings order -> delete?

                                Log.d("Settings","DELETE AUSführen");


                                // refresh activity ourarrangement because settings have change
                                refreshOurArrangement = true;
                                refreshOurArrangementSettings = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // our arrangement settings order -> update?

                                Log.d("Settings","UPDATE AUSführen");

                                // update evaluation of arrangements?
                                if (tmpEvaluatePauseTime > 0 && tmpEvaluateActiveTime > 0 && tmpEvaluateStartDate > 0 && tmpEvaluateEndDate > 0) {

                                    Log.d ("Settings--","PT:"+tmpEvaluatePauseTime+"AT:"+tmpEvaluateActiveTime+"SD:"+tmpEvaluateStartDate+"ED:"+tmpEvaluateEndDate);

                                    // write data to prefs
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, tmpEvaluatePauseTime);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, tmpEvaluateActiveTime);
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, tmpEvaluateStartDate);
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, tmpEvaluateEndDate);
                                    prefsEditor.commit();

                                    // something change in evaluation process
                                    refreshOurArrangementSettingsEvaluationProcess = true;

                                }

                                // update comment max/count of arrangements?
                                if (tmpCommentMaxComment > 0 && tmpCommentCountComment >= 0 && tmpCommentCountCommentSinceTime > 0) {

                                    // write data to prefs
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, tmpSketchCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentCountComment, tmpCommentCountComment);
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, tmpCommentCountCommentSinceTime);
                                    prefsEditor.commit();

                                    // something change in arrangement comment process
                                    refreshOurArrangementSettingsCommentProcess = true;

                                }

                                // update sketch comment max/count of sketch arrangements?
                                if (tmpSketchCommentMaxComment > 0 && tmpSketchCommentCountComment >= 0 && tmpSketchCommentCountCommentSinceTime > 0) {

                                    // write data to prefs
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,tmpSketchCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, tmpSketchCommentCountComment);
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, tmpSketchCommentCountCommentSinceTime);
                                    prefsEditor.commit();

                                    // something change in sketch arragement comment process
                                    refreshOurArrangementSettingsSketchCommentProcess = true;

                                }

                                // update sketch arrangement author name?
                                if (tmpSketchAuthorName.length() > 0) {

                                    // write data to prefs
                                    prefsEditor.putString(ConstansClassOurArrangement.namePrefsAuthorOfSketchArrangement, tmpSketchAuthorName);
                                    prefsEditor.commit();

                                    // something change in sketch arrangement author name
                                    refreshOurArrangementSettingsSketchArrangmentAuthorName = true;

                                }

                                // update sketch current date of sketch arrangement?
                                if (tmpSketchCurrentDateOfArrangement > 0) {

                                    // write data to prefs
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, tmpSketchCurrentDateOfArrangement);
                                    prefsEditor.commit();

                                    // something change in sketch arrangement author name
                                    refreshOurArrangementSettingsSketchCurrentDateOfSketchArrangement = true;

                                }

                                // update current date of arrangement?
                                if (tmpCurrentDateOfArrangement > 0) {

                                    // write data to prefs
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, tmpCurrentDateOfArrangement);
                                    prefsEditor.commit();

                                    // something change in sketch arrangement author name
                                    refreshOurArrangementSettingsCurrentDateOfArrangement = true;

                                }





                                // refresh activity ourarrangement because settings have change
                                refreshOurArrangement = true;
                                refreshOurArrangementSettings = true;
                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // End read our arrangement -----------------------------------------------------------------------------------




}
