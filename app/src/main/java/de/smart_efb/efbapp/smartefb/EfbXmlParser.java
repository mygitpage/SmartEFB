package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

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

    // return information for change
    Map<String, String> returnMap;




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


    // refresh activity ourgoals
    Boolean refreshOurGoals = false;
        // refresh activity ourgoals fragment jointly now
        Boolean refreshOurGoalsJointlyNow = false;
        // refresh activity ourgoals fragment debetable now
        Boolean refreshOurGoalsDebetableNow = false;
        // refresh activity ourgoals fragment jointly comment
        Boolean refreshOurGoalsJointlyComment = false;
        // refresh activity ourgoals fragment debetable goals
        Boolean refreshOurGoalsDebetableComment = false;


    // refresh settings of activity our goals
    Boolean refreshOurGoalsSettings = false;
        // refresh data of evaluation process
        Boolean refreshOurGoalsSettingsEvaluationProcess = false;
        // refresh data of jointly comment process
        Boolean refreshOurGoalsSettingsCommentProcess = false;
        // refresh data of debetable comment process
        Boolean refreshOurGoalsSettingsDebetableCommentProcess = false;
        // refresh author name of debetable goals
        Boolean refreshOurGoalsSettingsDebetableGoalsAuthorName = false;
        // refresh current date of debetable goals
        Boolean refreshOurGoalsSettingsDebetableCurrentDateOfDebetableGoals = false;
        // refresh current date of jointly goals
        Boolean refreshOurGoalsSettingsJointlyCurrentDateOfJointlyGoals = false;


    // refresh activity meeting
    Boolean refreshMeeting = false;
        // refresh activity meeting settings
        Boolean refreshMeetingSettings = false;
        // refresh meeting setting date A
        Boolean refreshMeetingSettingsUpdateDateA = false;
        // refresh meeting setting date B
        Boolean refreshMeetingSettingsUpdateDateB = false;
        // refresh meeting status
        Boolean refreshMeetingSettingsUpdateStatus = false;
        // refresh new meeting suggestion
        Boolean refreshMeetingNewSuggestion = false;
        // refresh new author of suggestions
        Boolean refreshMeetingAuthorSuggestion = false;
        // refresh response deadline
        Boolean refreshMeetingResponseDeadline = false;





    public EfbXmlParser (Context tmpXmlContext) {

        // init context
        xmlContext = tmpXmlContext;

        // init the DB
        myDb = new DBAdapter(xmlContext);

        // init prefs and editor
        prefs = xmlContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, xmlContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();



        // init return map
        returnMap = new HashMap<String, String>();

        returnMap.put("MainOrder","");
        returnMap.put("ErrorText","");
        returnMap.put("ClientId","");
        returnMap.put("ConnectionStatus","0");



    }



    public Map<String, String> parseXmlInput (String xmlInput) throws XmlPullParserException, IOException {

        // true -> master element of xml file was found
        Boolean masterElementFound = false;

        try {

            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();

            xpp = xppf.newPullParser();

            //AssetManager manager = xmlContext.getResources().getAssets(); //getBaseContext().getAssets(); //getBaseContext().getResources().getAssets();
            //InputStream input = manager.open("configuration.xml");



            xpp.setInput( new StringReader (xmlInput));
            int eventType = xpp.getEventType();

            Log.d("XML","Starten!!!!!!!!");


            while (eventType != XmlPullParser.END_DOCUMENT) {

                Log.d("XML","In der While Schleife");

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("XMLParser","Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {

                        case ConstansClassXmlParser.xmlNameForMasterElement:

                            Log.d("XML","Master Element gefunden");

                            masterElementFound = true;
                            break;

                        case ConstansClassXmlParser.xmlNameForMain:

                            Log.d("XML","Main Element gefunden");

                            if (masterElementFound) {
                                readMainTag();
                                Log.d("XMLParser","ZURUECK AUS READ MAIN");
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook:
                            readConnectBookTag();
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement:
                            readOurArrangementTag();
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals:
                            readOurGoalsTag();
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting:
                            readMeetingTag();
                            break;
                        case ConstansClassXmlParser.xmlNameForSettings:
                            Log.d("XMLParser","Settings Zeile " + xpp.getLineNumber());
                            break;
                    }

                }

                // Next XML Element
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



        return returnMap;

    }


    //
    // Begin read main element -----------------------------------------------------------------------------------
    //

    private void readMainTag() {

        Boolean readMoreXml = true;

        String tmpClientId = "";
        String tmpMainOrder = "";
        String tmpErrorText = "";
        String tmpMeetingId = "";

        try {

            int eventType = xpp.next();

            while (readMoreXml) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("ReadMain","Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {

                        case ConstansClassXmlParser.xmlNameForMain_Order: // xml data order
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if clientid from xml > 0
                                    tmpMainOrder = xpp.getText().trim(); // copy main order
                                }
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForMain_ErrorText: // xml data error text
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if clientid from xml > 0
                                    tmpErrorText = xpp.getText().trim(); // copy main order
                               }
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForMain_ClientID: // xml data client id
                            eventType = xpp.next();

                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if clientid from xml > 0
                                    tmpClientId = xpp.getText().trim(); // copy client id
                                }
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForMain_MeetingId: // xml data make meeting
                            eventType = xpp.next();

                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if meetingid from xml > 0
                                    tmpMeetingId = xpp.getText().trim();
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

                        switch (tmpMainOrder) {
                            case "init":

                                Log.d("XML","Order: init");

                                if (tmpClientId.trim().length() > 0 ) {


                                    // write client id to prefs
                                    prefsEditor.putString(ConstansClassSettings.namePrefsClientId, tmpClientId);
                                    // set connection status to connect
                                    prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus,3);
                                    // write last error messages to prefs
                                    prefsEditor.putString(ConstansClassSettings.namePrefsLastErrorMessages, "");

                                    prefsEditor.commit();


                                    returnMap.put("ClientId",tmpClientId);
                                    returnMap.put("MainOrder","init");
                                    returnMap.put("ConnectionStatus","3");


                                    Log.d("XML","Order: init ausgefuehrt!!!!!!!!!!");
                                }



                                break;
                            case "data":

                                // TODO:

                                break;
                            case "error":


                                // write last error messages to prefs
                                prefsEditor.putString(ConstansClassSettings.namePrefsLastErrorMessages, tmpErrorText);
                                // set connection status to error
                                prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus,1);
                                prefsEditor.commit();

                                returnMap.put("ClientId","");
                                returnMap.put("MainOrder","error");
                                returnMap.put("ConnectionStatus","1");
                                returnMap.put("ErrorText",tmpErrorText);

                                // TODO:

                                break;
                        }





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
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
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
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, tmpCommentMaxComment);
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




    //
    // Begin read our goals -----------------------------------------------------------------------------------
    //

    // read element ourgoals
    private void readOurGoalsTag() {

        Boolean parseAnymore = true;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readOurGoalsTag", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow:
                            readOurGoalsTag_JointlyNow();
                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow:
                            readOurGoalsTag_DebetableNow();
                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate:
                            readOurGoalsTag_JointlyEvaluate();
                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment:
                            readOurGoalsTag_JointlyComment();
                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment:
                            readOurGoalsTag_DebetableComment();
                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings:
                            readOurGoalsTag_Settings();
                            break;

                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {parseAnymore = false;
                    Log.d("readOurGoalsTag", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of ourgoals
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals)) {

                        Log.d("readOurGoalsTag", "End Tag ourgoals gefunden!");
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


    // read tag our goals jointly now and push to database
    private void readOurGoalsTag_JointlyNow() {

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our goals jointly now tag
        Boolean error = false;

        // tmp data for database insert
        String tmpGoalText = "";
        String tmpAuthorName = "";
        Long tmpGoalTime = 0L;
        Boolean tmpJointlyDebetable = false;
        String tmpOrder = "";
        String tmpOldMd5 = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readOurGoalTag_Jointly", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow_Order:
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

                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow_GoalText:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get goalText text
                                if (xpp.getText().trim().length() > 0) { // check if goalText from xml > 0
                                    tmpGoalText = xpp.getText().trim();

                                    Log.d("JointlyNOW::MD5","MD5:"+EfbHelperClass.md5(tmpGoalText));


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow_AuthorName:
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow_GoalTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get goalTime text
                                if (xpp.getText().trim().length() > 0) { // check if goalTime from xml > 0
                                    tmpGoalTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow_JointlyDebetable:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get jointlyDebetable text, only 0 is possible because 1 is debetable goal, look readOurGoalsTag_DebetableNow()
                                if (xpp.getText().trim().length() > 0) { // check if jointlyDebetable from xml > 0
                                    if (xpp.getText().trim().equals("0")) { // goal is a jointly goal?
                                        tmpJointlyDebetable = false;
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow_OldMd5:
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

                // look for end tag of ourgoals jointly now
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow)) {

                        // check all data for jointly goal now correct?
                        if (!error) {

                            Log.d("JointlyNOW_DB","Te:"+tmpGoalText+" - Au:"+tmpAuthorName+" - ATi:"+tmpGoalTime+" - STi"+tmpJointlyDebetable);

                            // our goal order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpGoalText.length() > 0 && tmpAuthorName.length() > 0 && tmpGoalTime > 0) {
                                // insert new jointly goal in DB
                                myDb.insertRowOurGoals(tmpGoalText, tmpAuthorName, tmpGoalTime, true, tmpJointlyDebetable, 4);

                                // refresh activity ourgoals and fragement jointly goal now
                                refreshOurGoals = true;
                                refreshOurGoalsJointlyNow = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our goal order -> delete entry?

                                // delete goal in DB
                                myDb.deleteRowOurGoals(tmpOldMd5);

                                // refresh activity ourgoals and fragement jointly goal now
                                refreshOurGoals = true;
                                refreshOurGoalsJointlyNow = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpGoalText.length() > 0 && tmpAuthorName.length() > 0 && tmpGoalTime > 0) { // our goal order -> update entry?

                                // update arrangement in DB
                                myDb.updateRowOurGoals(tmpGoalText, tmpAuthorName, tmpGoalTime, true, tmpJointlyDebetable, 4, tmpOldMd5);

                                // refresh activity ourgoals and fragement jointly goal now
                                refreshOurGoals = true;
                                refreshOurGoalsJointlyNow = true;
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



    // read tag our goals jointly comment and push to database
    private void readOurGoalsTag_JointlyComment() {


        Log.d("read_Jointly COMMENT", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our goals jointly comment tag
        Boolean error = false;

        // tmp data for database insert
        String tmpCommentText = "";
        String tmpAuthorName = "";
        Long tmpCommentTime = 0L;
        int tmpGoalId = 0;
        Long tmpGoalTime = 0L;
        String tmpOrder = "";
        String tmpOldMd5 = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("Tag_NOW_JointlyCOMMENT", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_Order:
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

                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_Comment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get comment text
                                if (xpp.getText().trim().length() > 0) { // check if commentText from xml > 0
                                    tmpCommentText = xpp.getText().trim();

                                    Log.d("Goals_JointlyComment","MD5:"+EfbHelperClass.md5(tmpCommentText));


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_AuthorName:
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_CommentTime:
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_GoalId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get goalId text
                                if (xpp.getText().trim().length() > 0) { // check if goalId from xml > 0
                                    tmpGoalId = Integer.valueOf(xpp.getText().trim()); // make int from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_DateOfJointlyGoal:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get goalTime text
                                if (xpp.getText().trim().length() > 0) { // check if goalTime from xml > 0
                                    tmpGoalTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_OldMd5:
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
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
                    Log.d("ABBRUCH!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of our goals jointly comment
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment)) {

                        // check all data for arrangement now correct?
                        if (!error) {

                            Log.d("JointlyComment_DB","C:"+tmpCommentText+" - Au:"+tmpAuthorName+" - CTi:"+tmpCommentTime+" - AId"+tmpGoalId+" - CoA:"+tmpGoalTime);

                            // our goals jointly comment order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpGoalId >= 0 && tmpGoalTime > 0) {
                                // insert new comment in DB
                                myDb.insertRowOurGoalJointlyGoalComment(tmpCommentText, tmpAuthorName, tmpCommentTime, tmpGoalId, true, tmpGoalTime, 4);

                                // refresh activity ourgoals and fragment jointly comment
                                refreshOurGoals = true;
                                refreshOurGoalsJointlyComment = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our goals jointly comment order -> delete entry?

                                // delete arrangement comment in DB
                                myDb.deleteRowOurGoalJointlyGoalComment(tmpOldMd5);

                                // refresh activity ourgoals and fragment jointly comment
                                refreshOurGoals = true;
                                refreshOurGoalsJointlyComment = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpGoalId >= 0 && tmpGoalTime > 0) { // our goals jointly comment order -> update entry?

                                // update jointly comment in DB
                                myDb.updateRowOurGoalJointlyGoalComment(tmpCommentText, tmpAuthorName, tmpCommentTime, tmpGoalId, true,  tmpGoalTime, 4, tmpOldMd5);

                                // refresh activity ourgoals and fragment jointly comment
                                refreshOurGoals = true;
                                refreshOurGoalsJointlyComment = true;
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



    // read tag our goals jointly evaluate and push to database
    private void readOurGoalsTag_JointlyEvaluate() {

        // TODO: Implement Function

    }



    // read tag our goals debetable comment and push to database
    private void readOurGoalsTag_DebetableComment() {



        Log.d("read_Debetable COMMENT", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our goals debetable comment tag
        Boolean error = false;

        // tmp data for database insert
        String tmpCommentText = "";
        int tmpREsultQuestionA = 0;
        int tmpREsultQuestionB = 0;
        int tmpREsultQuestionC = 0;
        String tmpAuthorName = "";
        Long tmpCommentTime = 0L;
        int tmpGoalId = 0;
        Long tmpGoalTime = 0L;
        String tmpOrder = "";
        String tmpOldMd5 = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("TagNOW_DebetableCOMMENT", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_Order:
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

                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_Comment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get comment text
                                if (xpp.getText().trim().length() > 0) { // check if commentText from xml > 0
                                    tmpCommentText = xpp.getText().trim();

                                    Log.d("Goals_DebetableComment","MD5:"+EfbHelperClass.md5(tmpCommentText));


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ResultQuestionA:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get result question A text
                                if (xpp.getText().trim().length() > 0) { // check if result question A from xml > 0
                                    tmpREsultQuestionA = Integer.valueOf(xpp.getText().trim()); // make int from xml-text

                                    Log.d("reOur_DEBETABLE_COMMENT", "Question A: " + tmpREsultQuestionA);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ResultQuestionB:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get result question B text
                                if (xpp.getText().trim().length() > 0) { // check if result question B from xml > 0
                                    tmpREsultQuestionB = Integer.valueOf(xpp.getText().trim()); // make int from xml-text

                                    Log.d("reOur_DEBETABLE_COMMENT", "Question B: " + tmpREsultQuestionB);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ResultQuestionC:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get result question C text
                                if (xpp.getText().trim().length() > 0) { // check if result question C from xml > 0
                                    tmpREsultQuestionC = Integer.valueOf(xpp.getText().trim()); // make int from xml-text

                                    Log.d("reOur_DEBETABLE_COMMENT", "Question C: " + tmpREsultQuestionC);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_AuthorName:
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_CommentTime:
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_GoalId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get goalId text
                                if (xpp.getText().trim().length() > 0) { // check if goalId from xml > 0
                                    tmpGoalId = Integer.valueOf(xpp.getText().trim()); // make int from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_DateOfDebetableGoal:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get goalTime text
                                if (xpp.getText().trim().length() > 0) { // check if goalTime from xml > 0
                                    tmpGoalTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_OldMd5:
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
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
                    Log.d("ABBRUCH!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of our goals jointly comment
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment)) {

                        // check all data for arrangement now correct?
                        if (!error) {

                            Log.d("DebetableComment_DB","C:"+tmpCommentText+" - Au:"+tmpAuthorName+" - CTi:"+tmpCommentTime+" - AId"+tmpGoalId+" - CoA:"+tmpGoalTime);

                            // our goals debetable comment order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpGoalId >= 0 && tmpGoalTime > 0) {
                                // insert new comment in DB
                                myDb.insertRowOurGoalsDebetableGoalsComment(tmpCommentText, tmpREsultQuestionA, tmpREsultQuestionB, tmpREsultQuestionC, tmpAuthorName, tmpCommentTime, tmpGoalId, true, tmpGoalTime, 4);

                                // refresh activity ourgoals and fragment debetable comment
                                refreshOurGoals = true;
                                refreshOurGoalsDebetableComment = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our goals jointly comment order -> delete entry?

                                // delete arrangement comment in DB
                                myDb.deleteRowOurGoalsDebetableGoalsComment(tmpOldMd5);

                                // refresh activity ourgoals and fragment debetable comment
                                refreshOurGoals = true;
                                refreshOurGoalsDebetableComment = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpGoalId >= 0 && tmpGoalTime > 0) { // our goals jointly comment order -> update entry?

                                // update jointly comment in DB
                                myDb.updateRowOurGoalsDebetableGoalsComment(tmpCommentText, tmpREsultQuestionA, tmpREsultQuestionB, tmpREsultQuestionC, tmpAuthorName, tmpCommentTime, tmpGoalId, true,  tmpGoalTime, 4, tmpOldMd5);

                                // refresh activity ourgoals and fragment debetable comment
                                refreshOurGoals = true;
                                refreshOurGoalsDebetableComment = true;
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


    // read tag our goals debetable now and push to database
    private void readOurGoalsTag_DebetableNow() {

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our goals debetable now tag
        Boolean error = false;

        // tmp data for database insert
        String tmpDebetableGoalText = "";
        String tmpDebetableAuthorName = "";
        Long tmpDebetableGoalTime = 0L;
        Boolean tmpJointlyDebetable = false;
        String tmpOrder = "";
        String tmpOldMd5 = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("reOurGoalTag_Debetable", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_Order:
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

                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_GoalText:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get goalText text
                                if (xpp.getText().trim().length() > 0) { // check if goalText from xml > 0
                                    tmpDebetableGoalText = xpp.getText().trim();

                                    Log.d("DebetableNOW::MD5","MD5:"+EfbHelperClass.md5(tmpDebetableGoalText));


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_AuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get authorName text
                                if (xpp.getText().trim().length() > 0) { // check if authorName from xml > 0
                                    tmpDebetableAuthorName = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_GoalTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get goalTime text
                                if (xpp.getText().trim().length() > 0) { // check if goalTime from xml > 0
                                    tmpDebetableGoalTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_JointlyDebetable:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get jointlyDebetable text, only 1 is possible because 0 is jointly goal, look readOurGoalsTag_DebetableNow()
                                if (xpp.getText().trim().length() > 0) { // check if jointlyDebetable from xml > 0
                                    if (xpp.getText().trim().equals("1")) { // goal is a debetable goal?
                                        tmpJointlyDebetable = true;
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_OldMd5:
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
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
                    Log.d("ABBRUCH!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of ourgoals jointly now
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow)) {

                        // check all data for debetable goal now correct?
                        if (!error) {

                            Log.d("DebetableNOW_DB","Te:"+tmpDebetableGoalText+" - Au:"+tmpDebetableAuthorName+" - ATi:"+tmpDebetableGoalTime+" - STi"+tmpJointlyDebetable);

                            // our goal order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpDebetableGoalText.length() > 0 && tmpDebetableAuthorName.length() > 0 && tmpDebetableGoalTime > 0) {
                                // insert new debetable goal in DB
                                myDb.insertRowOurGoals(tmpDebetableGoalText, tmpDebetableAuthorName, tmpDebetableGoalTime, true, tmpJointlyDebetable, 4);

                                // refresh activity ourgoals and fragement debetable goal now
                                refreshOurGoals = true;
                                refreshOurGoalsDebetableNow = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our goal order -> delete entry?

                                // delete goal in DB
                                myDb.deleteRowOurGoals(tmpOldMd5);

                                // refresh activity ourgoals and fragement debetable goal now
                                refreshOurGoals = true;
                                refreshOurGoalsDebetableNow = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpDebetableGoalText.length() > 0 && tmpDebetableAuthorName.length() > 0 && tmpDebetableGoalTime > 0) { // our goal order -> update entry?

                                // update goal in DB
                                myDb.updateRowOurGoals(tmpDebetableGoalText, tmpDebetableAuthorName, tmpDebetableGoalTime, true, tmpJointlyDebetable, 4, tmpOldMd5);

                                // refresh activity ourgoals and fragement jointly goal now
                                refreshOurGoals = true;
                                refreshOurGoalsDebetableNow = true;
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


    // read tag our goals settings and push to database/prefs
    private void readOurGoalsTag_Settings() {

        Log.d("read_SETTINGS", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our goals settings tag
        Boolean error = false;

        // tmp data for prefs and database insert
        String tmpOrder = "";
        int tmpJointlyEvaluatePauseTime = 0;
        int tmpJointlyEvaluateActiveTime = 0;
        Long tmpJointlyEvaluateStartDate = 0L;
        Long tmpJointlyEvaluateEndDate = 0L;

        int tmpJointlyCommentMaxComment = 0;
        int tmpJointlyCommentCountComment = 0;
        Long tmpJointlyCommentCountCommentSinceTime = 0L;

        int tmpDebetableCommentMaxComment = 0;
        int tmpDebetableCommentCountComment = 0;
        Long tmpDebetableCommentCountCommentSinceTime = 0L;

        String tmpDebetableGoalsAuthorName = "";

        Long tmpDebetableCurrentDateOfGoals = 0L;
        Long tmpJointlyCurrentDateOfGoals = 0L;


        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readGoals_SETTINGS", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) {
                                        error = true;
                                        tmpOrder = "";
                                    }

                                    Log.d("Goals Settings","ORDER: "+tmpOrder);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_EvaluatePauseTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get evaluate pause time
                                if (xpp.getText().trim().length() > 0) { // check if pause time from xml > 0
                                    tmpJointlyEvaluatePauseTime = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","EvaluatePauseTime"+tmpJointlyEvaluatePauseTime);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_EvaluateActiveTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get evaluate active time
                                if (xpp.getText().trim().length() > 0) { // check if active time from xml > 0
                                    tmpJointlyEvaluateActiveTime = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","EvaluateActiveTime"+tmpJointlyEvaluateActiveTime);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_EvaluateStartDate:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get evaluate start date
                                if (xpp.getText().trim().length() > 0) { // check if start date from xml > 0
                                    tmpJointlyEvaluateStartDate = Long.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","EvaluateStartDate"+tmpJointlyEvaluateStartDate);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_EvaluateEndDate:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get evaluate end date
                                if (xpp.getText().trim().length() > 0) { // check if end date from xml > 0
                                    tmpJointlyEvaluateEndDate = Long.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","EvaluateEndDate"+tmpJointlyEvaluateEndDate);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_CommentMaxComment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max comment
                                if (xpp.getText().trim().length() > 0) { // check if max comment from xml > 0
                                    tmpJointlyCommentMaxComment = Integer.valueOf(xpp.getText().trim());

                                    Log.d("goals_Settings","JointlyCommentMaxComment"+tmpJointlyCommentMaxComment);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_CommentCountComment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get count comment
                                if (xpp.getText().trim().length() >= 0) { // check if count comment from xml >= 0
                                    tmpJointlyCommentCountComment = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","JointlyCommentCountComment"+tmpJointlyCommentCountComment);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_CommentCountCommentSinceTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get since time
                                if (xpp.getText().trim().length() > 0) { // check if since time from xml > 0
                                    tmpJointlyCommentCountCommentSinceTime = Long.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","JointlyCommentCountCommentSinceTime"+tmpJointlyCommentCountCommentSinceTime);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_DebetableCommentMaxComment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max comment debetable goals
                                if (xpp.getText().trim().length() > 0) { // check if max comment debetable goals from xml > 0
                                    tmpDebetableCommentMaxComment = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","DebetableCommentMaxComment"+tmpDebetableCommentMaxComment);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_DebetableCommentCountComment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get count comment debetable goals
                                if (xpp.getText().trim().length() >= 0) { // check if count comment debetable from xml >= 0
                                    tmpDebetableCommentCountComment = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","DebetableCommentCountComment"+tmpDebetableCommentCountComment);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_DebetableCommentCountCommentSinceTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get since time sketch
                                if (xpp.getText().trim().length() > 0) { // check if since time sketch from xml > 0
                                    tmpDebetableCommentCountCommentSinceTime = Long.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","DebetableCommentCountCommentSinceTime"+tmpDebetableCommentCountCommentSinceTime);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_DebetableGoalsAuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get author name sketch
                                if (xpp.getText().trim().length() > 0) { // check if author name sketch from xml > 0
                                    tmpDebetableGoalsAuthorName = xpp.getText().trim();

                                    Log.d("Goals_Settings","Debetable goals Author Name"+tmpDebetableGoalsAuthorName);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_DebetableCurrentDateOfGoals:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get sketch current date of arragement
                                if (xpp.getText().trim().length() > 0) { // check if sketch date of arrangement from xml > 0
                                    tmpDebetableCurrentDateOfGoals = Long.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","DebetableCurrentDateOfGoals"+tmpDebetableCurrentDateOfGoals);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_JointlyCurrentDateOfGoals:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get current date of arrangement
                                if (xpp.getText().trim().length() > 0) { // check if date of arrangement from xml > 0
                                    tmpJointlyCurrentDateOfGoals = Long.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","JointlyCurrentDateOfGoals"+tmpJointlyCurrentDateOfGoals);


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
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals_Settings)) {

                        // check all data for goals settings correct?
                        if (!error) {


                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) ) { // our goals settings order -> delete?

                                Log.d("Goals Settings","DELETE AUSführen");


                                // refresh activity ourgoals because settings have change
                                refreshOurGoals = true;
                                refreshOurGoalsSettings = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // our goals settings order -> update?

                                Log.d("Goals Settings","UPDATE AUSführen");

                                // update evaluation of jointly goals?
                                if (tmpJointlyEvaluatePauseTime > 0 && tmpJointlyEvaluateActiveTime > 0 && tmpJointlyEvaluateStartDate > 0 && tmpJointlyEvaluateEndDate > 0) {

                                    Log.d ("Goals Settings--","PT:"+tmpJointlyEvaluatePauseTime+"AT:"+tmpJointlyEvaluateActiveTime+"SD:"+tmpJointlyEvaluateStartDate+"ED:"+tmpJointlyEvaluateEndDate);

                                    // write data to prefs
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, tmpJointlyEvaluatePauseTime);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, tmpJointlyEvaluateActiveTime);
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, tmpJointlyEvaluateStartDate);
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, tmpJointlyEvaluateEndDate);
                                    prefsEditor.commit();

                                    // something change in evaluation process
                                    refreshOurGoalsSettingsEvaluationProcess = true;

                                }

                                // update comment max/count of jointly goals?
                                if (tmpJointlyCommentMaxComment > 0 && tmpJointlyCommentCountComment >= 0 && tmpJointlyCommentCountCommentSinceTime > 0) {

                                    // write data to prefs
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, tmpJointlyCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, tmpJointlyCommentCountComment);
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsJointlyCommentTimeSinceInMills, tmpJointlyCommentCountCommentSinceTime);
                                    prefsEditor.commit();

                                    // something change in jointly goals comment process
                                    refreshOurGoalsSettingsCommentProcess = true;

                                }

                                // update debetable comment max/count of debetable goals?
                                if (tmpDebetableCommentMaxComment > 0 && tmpDebetableCommentCountComment >= 0 && tmpDebetableCommentCountCommentSinceTime > 0) {

                                    // write data to prefs
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment,tmpDebetableCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, tmpDebetableCommentCountComment);
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsDebetableCommentTimeSinceInMills, tmpDebetableCommentCountCommentSinceTime);
                                    prefsEditor.commit();

                                    // something change in debetable goals comment process
                                    refreshOurGoalsSettingsDebetableCommentProcess = true;

                                }

                                // update debetable goals author name?
                                if (tmpDebetableGoalsAuthorName.length() > 0) {

                                    // write data to prefs
                                    prefsEditor.putString(ConstansClassOurGoals.namePrefsAuthorOfDebetableGoals, tmpDebetableGoalsAuthorName);
                                    prefsEditor.commit();

                                    // something change in debetable goals author name
                                    refreshOurGoalsSettingsDebetableGoalsAuthorName = true;

                                }

                                // update debetable current date of debetable goals?
                                if (tmpDebetableCurrentDateOfGoals > 0) {

                                    Log.d ("Set Debet date","Set: "+tmpDebetableCurrentDateOfGoals);
                                    Log.d ("Set Debet Systime","SystemTime: "+System.currentTimeMillis());

                                    // write data to prefs
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, tmpDebetableCurrentDateOfGoals);
                                    prefsEditor.commit();

                                    // something change in current date of debetable goals
                                    refreshOurGoalsSettingsDebetableCurrentDateOfDebetableGoals = true;

                                }

                                // update current date of jointly goals?
                                if (tmpJointlyCurrentDateOfGoals > 0) {

                                    // write data to prefs
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, tmpJointlyCurrentDateOfGoals);
                                    prefsEditor.commit();

                                    // something change in current date of jointly goals
                                    refreshOurGoalsSettingsJointlyCurrentDateOfJointlyGoals = true;

                                }





                                // refresh activity ourarrangement because settings have change
                                refreshOurGoals = true;
                                refreshOurGoalsSettings = true;
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


    //
    // End read our goals -----------------------------------------------------------------------------------
    //




    //
    // Begin read meeting -----------------------------------------------------------------------------------
    //

    // read element meeting
    private void readMeetingTag() {

        Boolean parseAnymore = true;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readMeetingTag", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForMeeting_Settings:
                            readMeetingTag_Settings();
                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestions:
                            readMeetingTag_Suggestions();
                            break;
                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {parseAnymore = false;
                    Log.d("readMeetingTag", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of meeting
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMeeting)) {

                        Log.d("readMeetingTag", "End Tag meeting gefunden!");
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



    // read tag our meeting settings and push to prefs
    private void readMeetingTag_Settings() {

        Log.d("read_MeetingSettings", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml meeting settings tag
        Boolean error = false;

        // tmp data for prefs
        String tmpOrder = "";
        Long tmpMeetingDateA = 0L;
        Long tmpMeetingDateB = 0L;
        int tmpMeetingPlaceA = 0;
        int tmpMeetingPlaceB = 0;
        int tmpMeetingStatus = -1;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readMeeting_SETTINGS", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForMeeting_Settings_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
                                        error = true;
                                        tmpOrder = "";
                                    }

                                    Log.d("Meeting Settings","ORDER: "+tmpOrder);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_Settings_MeetingDateA:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { //  get meeting date B
                                if (xpp.getText().trim().length() > 0) { // check if meeting date B from xml > 0
                                    tmpMeetingDateA = Long.valueOf(xpp.getText().trim());

                                    Log.d("Meeting_Settings","Date A"+tmpMeetingDateA);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_Settings_MeetingDateB:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting date B
                                if (xpp.getText().trim().length() > 0) { // check if meeting date B from xml > 0
                                    tmpMeetingDateB = Long.valueOf(xpp.getText().trim());

                                    Log.d("Meeting_Settings","Date B"+tmpMeetingDateB);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_Settings_MeetingPlaceA:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting place a
                                if (xpp.getText().trim().length() > 0) { // check if meeting place a from xml > 0
                                    tmpMeetingPlaceA = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Meetings_Settings","Place A"+tmpMeetingPlaceA);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_Settings_MeetingPlaceB:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting place b
                                if (xpp.getText().trim().length() > 0) { // check if meeting place b from xml > 0
                                    tmpMeetingPlaceB = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Meetings_Settings","Place B"+tmpMeetingPlaceB);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_MeetingStatus:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting status
                                if (xpp.getText().trim().length() > 0) { // check if meeting status from xml > 0
                                    tmpMeetingStatus = Integer.valueOf(xpp.getText().trim());

                                    Log.d("meeting_Settings","Meeting status"+tmpMeetingStatus);


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

                // look for end tag of meeting settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMeeting_Settings)) {

                        // check all data for meeting settings correct?
                        if (!error) {

                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) ) { // meting settings order -> delete?

                                Log.d("meeting Settings","DELETE AUSführen");

                                // refresh activity meeting because settings have change
                                refreshMeeting = true;
                                refreshMeetingSettings = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) ) { // meeting settings order -> delete all?

                                // delete all meeting suggestions in DB
                                myDb.deleteAllRowsMeetingDateAndTime();


                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // meeting settings order -> update?

                                Log.d("meeting Settings","UPDATE AUSführen");

                                // update meeting date a and place a?
                                if (tmpMeetingDateA > 0 && tmpMeetingPlaceA > 0 ) {

                                    Log.d ("Meetings Settings--","Date A:"+tmpMeetingDateA+" PLace A:"+tmpMeetingPlaceA);

                                    // write data to prefs (A is index zero, B is index 1)
                                    prefsEditor.putInt(ConstantsClassMeeting.namePrefsMeetingPlace + ConstantsClassMeeting.prefsPraefixMeetings[0], tmpMeetingPlaceA);
                                    prefsEditor.putLong(ConstantsClassMeeting.namePrefsMeetingTimeAndDate + ConstantsClassMeeting.prefsPraefixMeetings[0], tmpMeetingDateA);
                                    // Sign new meeting date a
                                    prefsEditor.putBoolean(ConstantsClassMeeting.namePrefsNewMeetingDateAndTime + ConstantsClassMeeting.prefsPraefixMeetings[0], true);
                                    prefsEditor.commit();

                                    // something change in evaluation process
                                    refreshMeetingSettingsUpdateDateA = true;

                                }

                                // update meeting date b and place b?
                                if (tmpMeetingDateB > 0 && tmpMeetingPlaceB > 0 ) {

                                    Log.d ("Meetings Settings--","Date B:"+tmpMeetingDateB+" PLace A:"+tmpMeetingPlaceB);

                                    // write data to prefs (A is index zero, B is index 1)
                                    prefsEditor.putInt(ConstantsClassMeeting.namePrefsMeetingPlace + ConstantsClassMeeting.prefsPraefixMeetings[1], tmpMeetingPlaceB);
                                    prefsEditor.putLong(ConstantsClassMeeting.namePrefsMeetingTimeAndDate + ConstantsClassMeeting.prefsPraefixMeetings[1], tmpMeetingDateB);
                                    // Sign new meeting date b
                                    prefsEditor.putBoolean(ConstantsClassMeeting.namePrefsNewMeetingDateAndTime + ConstantsClassMeeting.prefsPraefixMeetings[1], true);
                                    prefsEditor.commit();

                                    // something change in evaluation process
                                    refreshMeetingSettingsUpdateDateB = true;

                                }

                                // update meeting status?
                                if (tmpMeetingStatus >= 0) {

                                    Log.d ("Meetings Settings--","Status:"+tmpMeetingStatus);

                                    // write data to prefs
                                    prefsEditor.putInt(ConstantsClassMeeting.namePrefsMeetingStatus, tmpMeetingStatus);
                                    prefsEditor.commit();

                                    // something change in meeting status
                                    refreshMeetingSettingsUpdateStatus = true;

                                }

                                // refresh activity meeting because settings have change
                                refreshMeeting = true;
                                refreshMeetingSettings = true;
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


    // read tag our meeting suggestions and push to database
    private void readMeetingTag_Suggestions() {


        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml meeting suggestions tag
        Boolean error = false;

        // tmp data for database insert
        Long tmpMeetingSuggestionTime = 0L;
        Long tmpResponseDeadline = 0L;
        String tmpMeetingSuggestionPlace = "";
        String tmpOrder = "";
        String tmpAuthorSuggestions = "";
        Long tmpOldMeetingSuggestionTime = 0L;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("MeetingTag_Suggestions", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestions_Order:
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

                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestions_DateTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting Time text
                                if (xpp.getText().trim().length() > 0) { // check if meeting Time from xml > 0
                                    tmpMeetingSuggestionTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestions_Place:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting Place text
                                if (xpp.getText().trim().length() > 0) { // check if meeting Place length from xml > 0
                                    tmpMeetingSuggestionPlace = xpp.getText().trim(); // make int from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestions_AuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get authorName text
                                if (xpp.getText().trim().length() > 0) { // check if authorName from xml > 0
                                    tmpAuthorSuggestions = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestions_ResponseDeadline:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting response deadline text
                                if (xpp.getText().trim().length() > 0) { // check if meeting response deadline from xml > 0
                                    tmpResponseDeadline = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestions_OldMeetingTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting old time text
                                if (xpp.getText().trim().length() > 0) { // check if meeting old time from xml > 0
                                    tmpOldMeetingSuggestionTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
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
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
                    Log.d("ABBRUCH!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of meeting suggestions
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMeeting_Suggestions)) {

                        // check all data for meeting suggestions correct?
                        if (!error) {

                            Log.d("MeetingSuggestion","Time:"+tmpMeetingSuggestionTime+" - Place:"+tmpMeetingSuggestionPlace);

                            // meeting suggestion -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpMeetingSuggestionTime > 0 && tmpMeetingSuggestionPlace.length() > 0) {
                                // insert new debetable goal in DB
                                myDb.insertNewMeetingDateAndTime(tmpMeetingSuggestionTime, tmpMeetingSuggestionPlace, true, 4);

                                // refresh activity meeting
                                refreshMeeting = true;
                                refreshMeetingNewSuggestion = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMeetingSuggestionTime > 0) { // meeting order -> delete suggestion entry?

                                // delete meeting suggestion in DB
                                myDb.deleteRowMeetingDateAndTime(tmpOldMeetingSuggestionTime);

                                // refresh activity meeting
                                refreshMeeting = true;
                                refreshMeetingNewSuggestion = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMeetingSuggestionTime > 0 && tmpMeetingSuggestionTime > 0 && tmpMeetingSuggestionPlace.length() > 0) { // meeting suggestion order -> update entry?

                                // update goal in DB
                                myDb.updateMeetingDateAndTime(tmpMeetingSuggestionTime, tmpMeetingSuggestionPlace, tmpOldMeetingSuggestionTime, true, 4);

                                // refresh activity meeting
                                refreshMeeting = true;
                                refreshMeetingNewSuggestion = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpAuthorSuggestions.length() > 0) { // meeting suggestion order -> update author name?


                                Log.d("Meetings Suggestions--", "Author NAme:" + tmpAuthorSuggestions);

                                // write new author name of suggestions to prefs
                                prefsEditor.putString(ConstantsClassMeeting.namePrefsAuthorMeetingSuggestion, tmpAuthorSuggestions);
                                prefsEditor.commit();

                                // refresh activity meeting
                                refreshMeeting = true;
                                refreshMeetingAuthorSuggestion = true;

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpResponseDeadline > 0) { // meeting suggestion order -> update response deadline?


                                Log.d("Meetings Suggestions--", "Response Deadline:" + tmpResponseDeadline);

                                // write new author name of suggestions to prefs
                                prefsEditor.putLong(ConstantsClassMeeting.namePrefsMeetingSuggestionsResponseDeadline, tmpResponseDeadline);
                                prefsEditor.commit();

                                // refresh activity meeting
                                refreshMeeting = true;
                                refreshMeetingResponseDeadline = true;
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

    //
    // End read meeting -----------------------------------------------------------------------------------
    //



    //
    // Begin read connect book -----------------------------------------------------------------------------------
    //

    // read element connect book
    private void readConnectBookTag() {

    }






}
