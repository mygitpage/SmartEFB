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


    public EfbXmlParser(Context tmpXmlContext) {

        // init context
        xmlContext = tmpXmlContext;

        // init the DB
        myDb = new DBAdapter(xmlContext);

        // init prefs and editor
        prefs = xmlContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, xmlContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();


        // init return map
        returnMap = new HashMap<String, String>();

        returnMap.put("Error", "0");

        returnMap.put("MainOrder", "");
        returnMap.put("ErrorText", "");
        returnMap.put("ClientId", "");
        returnMap.put("ConnectionStatus", "0");
        returnMap.put("SendSuccessfull", "0");

        returnMap.put("ConnectBook", "0");
        returnMap.put("ConnectBookSettings", "0");
        returnMap.put("ConnectBookSettingsClientName", "0");

        returnMap.put("OurArrangement", "0");
        returnMap.put("OurArrangementNow", "0");
        returnMap.put("OurArrangementNowComment", "0");
        returnMap.put("OurArrangementSketch", "0");
        returnMap.put("OurArrangementSketchComment", "0");
        returnMap.put("OurArrangementSettings", "0");
        returnMap.put("OurArrangementSettingsEvaluationProcess", "0");
        returnMap.put("OurArrangementSettingsCommentProcess", "0");
        returnMap.put("OurArrangementSettingsSketchCurrentDateOfSketchArrangement", "0");
        returnMap.put("OurArrangementSettingsSketchArrangmentAuthorName", "0");
        returnMap.put("OurArrangementSettingsCurrentDateOfArrangement", "0");

        returnMap.put("OurGoals", "0");
        returnMap.put("OurGoalsJointlyNow", "0");
        returnMap.put("OurGoalsDebetableNow", "0");
        returnMap.put("OurGoalsJointlyComment", "0");
        returnMap.put("OurGoalsDebetableComment", "0");
        returnMap.put("OurGoalsSettings", "0");
        returnMap.put("OurGoalsSettingsEvaluationProcess", "0");
        returnMap.put("OurGoalsSettingsCommentProcess", "0");
        returnMap.put("OurGoalsSettingsDebetableCommentProcess", "0");
        returnMap.put("OurGoalsSettingsDebetableGoalsAuthorName", "0");
        returnMap.put("OurGoalsSettingsDebetableCurrentDateOfDebetableGoals", "0");
        returnMap.put("OurGoalsSettingsJointlyCurrentDateOfJointlyGoals", "0");

        returnMap.put("Meeting", "0");
        returnMap.put("MeetingSettings", "0");
        returnMap.put("MeetingSettingsUpdateStatus", "0");
        returnMap.put("MeetingSettingsUpdateDateB", "0");
        returnMap.put("MeetingSettingsUpdateDateA", "0");

        returnMap.put("TimeTable", "0");
        returnMap.put("TimeTableValue", "0");

        returnMap.put("Settings", "0");

    }


    public Map<String, String> parseXmlInput(String xmlInput) throws XmlPullParserException, IOException {

        // true -> master element of xml file was found
        Boolean masterElementFound = false;

        try {

            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();

            // new pull parser
            xpp = xppf.newPullParser();

            // set input for pull parser
            xpp.setInput(new StringReader(xmlInput));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                Log.d("XML", "In der While Schleife");

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("XMLParser", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {

                        case ConstansClassXmlParser.xmlNameForMasterElement:
                            masterElementFound = true;
                            break;
                        case ConstansClassXmlParser.xmlNameForMain:
                            if (masterElementFound) {
                                readMainTag();
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook:
                            if (masterElementFound) {
                                readConnectBookTag();
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement:
                            if (masterElementFound) {
                                readOurArrangementTag();
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals:
                            if (masterElementFound) {
                                readOurGoalsTag();
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting:
                            if (masterElementFound) {
                                readMeetingTag();
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForTimeTable:
                            if (masterElementFound) {
                                readTimeTableTag();
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForSettings:
                            if (masterElementFound) {
                                readSettingTag();
                            }
                            break;
                    }

                }

                // Next XML Element
                eventType = xpp.next();

            }

            Log.d("XMLParser", "End document +++++++++++++");
            //System.out.println("End document");
        } catch (XmlPullParserException e) {

            // set error
            setErrorMessageInPrefs(33);

            e.printStackTrace();
        } catch (IOException e) {

            // set error
            setErrorMessageInPrefs(34);

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
                    Log.d("ReadMain", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {

                        case ConstansClassXmlParser.xmlNameForMain_Order: // xml data order
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if clientid from xml > 0
                                    tmpMainOrder = xpp.getText().trim(); // copy main order

                                    Log.d("MAIN", "MAIN ORDER +++++++++++ :"+tmpMainOrder);

                                }
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForMain_ErrorText: // xml data error text
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if clientid from xml > 0
                                    tmpErrorText = xpp.getText().trim(); // copy main order

                                    Log.d("MAIN", "ERROR TEXT +++++++++++ :"+tmpErrorText);

                                }
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForMain_ClientID: // xml data client id
                            eventType = xpp.next();

                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if clientid from xml > 0
                                    tmpClientId = xpp.getText().trim(); // copy client id

                                    Log.d("MAIN", "CLIENT ID +++++++++++ :"+tmpClientId);


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
                    Log.d("ReadMain", "END OF DOCUMENT");
                    readMoreXml = false;

                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.d("ReadMain", "End tag ++++++++++++++++ " + xpp.getName());

                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMain)) {

                        Log.d("XML", "END MAIN TAG ++++++++++");

                        if (tmpMainOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Init) && tmpClientId.trim().length() > 0) { // init client smartphone

                            Log.d("XML", "Order: init");

                            // write client id to prefs
                            prefsEditor.putString(ConstansClassSettings.namePrefsClientId, tmpClientId);
                            // set connection status to connect
                            prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus, 3);
                            // write last error messages to prefs
                            prefsEditor.putString(ConstansClassSettings.namePrefsLastErrorMessages, "");
                            prefsEditor.commit();

                            returnMap.put("ClientId", tmpClientId);
                            returnMap.put("MainOrder", "init");
                            returnMap.put("ConnectionStatus", "3");
                            returnMap.put("Error", "0");
                            returnMap.put("ErrorText", "");

                            Log.d("XML", "Order: init ausgefuehrt!!!!!!!!!!");

                            readMoreXml = false;

                        }


                    } else if (tmpMainOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Receive_Ok_Send)) { // data send and now receive data

                        Log.d("XML", "Order: RECEIVE SEND AUSGEFUEHRT!");
                        returnMap.put("ClientId", tmpClientId);
                        returnMap.put("SendSuccessfull", "1");

                        readMoreXml = false;

                    } else if (tmpMainOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Data)) { // receive data

                        readMoreXml = false;

                    } else if (tmpMainOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Error)) { // Error in main tag

                        // write last error messages to prefs
                        prefsEditor.putString(ConstansClassSettings.namePrefsLastErrorMessages, tmpErrorText + " (Position:Main)");
                        // set connection status to error
                        prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus, 1);
                        prefsEditor.commit();

                        returnMap.put("ClientId", "");
                        returnMap.put("MainOrder", "error");
                        returnMap.put("ConnectionStatus", "1");
                        returnMap.put("Error", "1");
                        returnMap.put("ErrorText", tmpErrorText);

                        readMoreXml = false;
                    }




                }

                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(35);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(36);
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
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
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
        } catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(37);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(38);
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
        String tmpBlockId = "";
        int tmpServerId = 0;
        String tmpChangeTo = "";

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

                                    Log.d("NOW Arr", "Order:" + tmpOrder);

                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
                                        Log.d("NOW Arr", "Fehler Order!!!");
                                        error = true;
                                        tmpOrder = "";
                                    }
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_ArrangementText:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementText text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementText from xml > 0
                                    tmpArrangementText = xpp.getText().trim();

                                    Log.d("Arrangement_NOW:Text", "Text" + tmpArrangementText);


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_AuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get authorName text
                                if (xpp.getText().trim().length() > 0) { // check if authorName from xml > 0
                                    tmpAuthorName = xpp.getText().trim();

                                    Log.d("NOW Arr", "Author" + tmpAuthorName);

                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_ArrangementTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementTime text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementTime from xml > 0
                                    tmpArrangementTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!

                                    Log.d("NOW Arr", "Author" + tmpArrangementTime);

                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_SketchCurrent:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get sketchCurrent text
                                if (xpp.getText().trim().length() > 0) { // check if sketchCurrent from xml > 0

                                    Log.d("NOW Arr", "Arrangement Status: " + xpp.getText().trim());


                                    if (xpp.getText().trim().equals("normal")) { // arrangement is a current arrangement?
                                        tmpSketchCurrent = false;
                                    } else {
                                        error = true;
                                    }
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_ChangeTo:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get ChangeTo text
                                if (xpp.getText().trim().length() > 0) { // check if ChangeTo from xml > 0
                                    tmpChangeTo = xpp.getText().trim();

                                    Log.d("NOW Arr", "ChangeTo:" + tmpChangeTo);
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;


                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_BlockId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get BlockId text
                                if (xpp.getText().trim().length() > 0) { // check if Block id from xml > 0
                                    tmpBlockId = xpp.getText().trim();

                                    Log.d("NOW Arr", "BlockID:" + tmpBlockId);
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;


                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_ServerId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get server id text
                                if (xpp.getText().trim().length() > 0) { // check if server id from xml > 0
                                    tmpServerId = Integer.valueOf(xpp.getText().trim()); // make int from xml-text
                                } else {
                                    error = true;
                                }
                            } else {
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

                // look for end tag of ourarrangement now
                if (eventType == XmlPullParser.END_TAG) {

                    Log.d("NOW Arr", "END TAG gefunden!");


                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_Now)) {


                        Log.d("NOW Arr", "END TAG Arr NOW gefunden!");

                        // check all data for arrangement now correct?
                        if (!error) {

                            Log.d("NOW__DB", "Te:" + tmpArrangementText + " - Au:" + tmpAuthorName + " - ATi:" + tmpArrangementTime + " - STi" + tmpSketchCurrent);

                            // our arrangement order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpArrangementText.length() > 0 && tmpAuthorName.length() > 0 && tmpArrangementTime > 0 && tmpServerId > 0 && tmpBlockId.length() > 0 && tmpChangeTo.length() > 0) {

                                Log.d("NOW Arr", "NEW Arra Entry!!!");

                                // insert new arrangement in DB
                                myDb.insertRowOurArrangement(tmpArrangementText, tmpAuthorName, tmpArrangementTime, true, tmpSketchCurrent, 0, 4, tmpServerId, tmpBlockId, tmpChangeTo);

                                // write current date of arrangements to pref
                                prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, tmpArrangementTime);
                                // write block id of arrangements to prefs
                                prefsEditor.putString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, tmpBlockId);
                                prefsEditor.commit();


                                // refresh activity ourarrangement and fragement now
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementNow", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpServerId > 0) { // our arrangement order -> delete entry?

                                // delete arrangement in DB
                                myDb.deleteRowOurArrangement(tmpServerId);

                                // refresh activity ourarrangement and fragement now
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementNow", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpServerId > 0 && tmpArrangementText.length() > 0 && tmpAuthorName.length() > 0 && tmpArrangementTime > 0 && tmpChangeTo.length() > 0) { // our arrangement order -> update entry?

                                // update arrangement in DB
                                myDb.updateRowOurArrangement(tmpArrangementText, tmpAuthorName, tmpArrangementTime, true, tmpSketchCurrent, 0, 4, tmpServerId, tmpBlockId);


                                // refresh activity ourarrangement and fragement now
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementNow", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) {

                                Log.d("NOW Arr", "Delete All!!!");

                                // delete all arrangements now with the blockId
                                myDb.deleteAllRowsOurArrangement(tmpBlockId, false); // false -> all now arrangements; true -> all sketch arrangements


                            }
                        }

                        parseAnymore = false;
                    }
                }

                Log.d("NOW Arr", "Ende der Funktion Arr NOW!");

            }
        } catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(39);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(40);
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
        String tmpBlockId = "";
        Long tmpArrangementTime = 0L;
        Long tmpUploadTime = 0L;
        String tmpOrder = "";
        int tmpServerIdArrangement = 0;

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
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
                                        error = true;
                                        tmpOrder = "";
                                    }

                                    Log.d("Now Comment", "ORDER: " + tmpOrder);


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_Comment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get comment text
                                if (xpp.getText().trim().length() > 0) { // check if commentText from xml > 0
                                    tmpCommentText = xpp.getText().trim();

                                    Log.d("Arrangement_NOWComment", "MD5:" + EfbHelperClass.md5(tmpCommentText));


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_AuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get authorName text
                                if (xpp.getText().trim().length() > 0) { // check if authorName from xml > 0
                                    tmpAuthorName = xpp.getText().trim();
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_CommentTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get commentTime text
                                if (xpp.getText().trim().length() > 0) { // check if commentTime from xml > 0
                                    tmpCommentTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_UploadTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get upload time text
                                if (xpp.getText().trim().length() > 0) { // check if uploadTime from xml > 0
                                    tmpUploadTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_BlockId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get BlockId text
                                if (xpp.getText().trim().length() > 0) { // check if Block id from xml > 0
                                    tmpBlockId = xpp.getText().trim();

                                    Log.d("NOW Arr", "BlockID:" + tmpBlockId);
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_DateOfArrangement:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementTime text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementTime from xml > 0
                                    tmpArrangementTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_ServerIdArrangement:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get server id arrangement text
                                if (xpp.getText().trim().length() > 0) { // check if server id arrangement from xml > 0
                                    tmpServerIdArrangement = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Now Comment", "ServerID:" + tmpServerIdArrangement);


                                } else {
                                    error = true;
                                }
                            } else {
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

                // look for end tag of ourarrangement now
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_NowComment)) {

                        // check all data for arrangement now correct?
                        if (!error) {

                            // our arrangement now comment order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpServerIdArrangement >= 0 && tmpArrangementTime > 0 && tmpBlockId.length() > 0) {

                                Log.d("IN_NowComment_DB", "C:" + tmpCommentText + " - Au:" + tmpAuthorName + " - CTi:" + tmpCommentTime + " - AId" + tmpServerIdArrangement + " - CoA:" + tmpArrangementTime);

                                // set upload time on smartphone for commeent
                                tmpUploadTime = System.currentTimeMillis();

                                // insert new comment in DB
                                myDb.insertRowOurArrangementComment(tmpCommentText, tmpAuthorName, tmpCommentTime, tmpUploadTime, tmpBlockId, true, tmpArrangementTime, 4, tmpServerIdArrangement);

                                // refresh activity ourarrangement and fragment now comment
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementNowComment", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) { // delete all comments; needed by init process

                                Log.d("NOW Comment Arr", "Delete All!!!");

                                // delete all comments for all current arrangements now with the blockId
                                myDb.deleteAllRowsOurArrangementComment(tmpBlockId);


                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        } catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(41);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(42);
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
        String tmpBlockId = "";
        String tmpChangeTo = "";
        int tmpServerId = 0;

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
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
                                        error = true;
                                        tmpOrder = "";
                                    }
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_ArrangementText:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementText text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementText from xml > 0
                                    tmpArrangementText = xpp.getText().trim();


                                    Log.d("Arrangement_SKETCH::MD5", "MD5:" + EfbHelperClass.md5(tmpArrangementText));

                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_AuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get authorName text
                                if (xpp.getText().trim().length() > 0) { // check if authorName from xml > 0
                                    tmpAuthorName = xpp.getText().trim();
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_SketchCurrent:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get sketchCurrent text
                                if (xpp.getText().trim().length() > 0) { // check if sketchCurrent from xml > 0
                                    Log.d("Sketch Arr", "Arrangement Status: " + xpp.getText().trim());


                                    if (xpp.getText().trim().equals("sketch")) { // arrangement is a sketch arrangement?
                                        tmpSketchCurrent = true;
                                    } else {
                                        error = true;
                                    }
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_SketchTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get sketchTime text
                                if (xpp.getText().trim().length() > 0) { // check if sketchTime from xml > 0
                                    tmpSketchTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_ChangeTo:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get ChangeTo text
                                if (xpp.getText().trim().length() > 0) { // check if ChangeTo from xml > 0
                                    tmpChangeTo = xpp.getText().trim();

                                    Log.d("Sketch Arr", "ChangeTo:" + tmpChangeTo);
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_BlockId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get BlockId text
                                if (xpp.getText().trim().length() > 0) { // check if Block id from xml > 0
                                    tmpBlockId = xpp.getText().trim();
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_ServerId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get server id text
                                if (xpp.getText().trim().length() > 0) { // check if server id from xml > 0
                                    tmpServerId = Integer.valueOf(xpp.getText().trim()); // make int from xml-text
                                } else {
                                    error = true;
                                }
                            } else {
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

                // look for end tag of ourarrangement sketch
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_Sketch)) {

                        // check all data for arrangement sketch correct?
                        if (!error) {

                            Log.d("SKETCH__DB", "Te:" + tmpArrangementText + " - Au:" + tmpAuthorName + " - SATi:" + tmpSketchTime + " - STi" + tmpSketchCurrent);

                            // our arrangement sketch order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpArrangementText.length() > 0 && tmpAuthorName.length() > 0 && tmpSketchTime > 0 && tmpServerId > 0 && tmpBlockId.length() > 0 && tmpChangeTo.length() > 0) {

                                Log.d("Sketch Arr", "NEW Sketch Entry!!!");

                                // insert new sketch arrangement in DB
                                myDb.insertRowOurArrangement(tmpArrangementText, tmpAuthorName, 0, true, tmpSketchCurrent, tmpSketchTime, 4, tmpServerId, tmpBlockId, tmpChangeTo);

                                // write current date of sketch arrangements to pref
                                prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, tmpSketchTime);
                                // write block id of sketch arrangements to prefs
                                prefsEditor.putString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, tmpBlockId);
                                prefsEditor.commit();

                                // refresh activity ourarrangement and fragement sketch
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementSketch", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpServerId > 0) { // our arrangement order -> delete entry?

                                // delete arrangement in DB
                                myDb.deleteRowOurArrangement(tmpServerId);

                                // refresh activity ourarrangement and fragement sketch
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementSketch", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpServerId > 0 && tmpArrangementText.length() > 0 && tmpAuthorName.length() > 0 && tmpSketchTime > 0 && tmpChangeTo.length() > 0) { // our arrangement order -> update entry?

                                // update sketch arrangement with tmpServerId
                                myDb.updateRowOurArrangement(tmpArrangementText, tmpAuthorName, 0, true, tmpSketchCurrent, tmpSketchTime, 4, tmpServerId, tmpBlockId);

                                // refresh activity ourarrangement and fragement sketch
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementSketch", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) {

                                Log.d("Sketch Arr", "Delete All!!!");

                                // delete all arrangements sketch with the blockId
                                myDb.deleteAllRowsOurArrangement(tmpBlockId, true); // false -> all now arrangements; true -> all sketch arrangements


                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        } catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(1);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(2);
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
        int tmpResultQuestionA = 0;
        int tmpResultQuestionB = 0;
        int tmpResultQuestionC = 0;
        String tmpAuthorName = "";
        Long tmpCommentTime = 0L;
        Long tmpUploadTime = 0L;
        Long tmpArrangementTime = 0L;
        String tmpOrder = "";
        String tmpBlockId = "";
        int tmpServerIdArrangement = 0;



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
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New)  && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
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
                                    tmpResultQuestionA = Integer.valueOf(xpp.getText().trim()); // make int from xml-text

                                    Log.d("readOur_SKETCH_COMMENT", "Question A: " + tmpResultQuestionA);

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
                                    tmpResultQuestionB = Integer.valueOf(xpp.getText().trim()); // make int from xml-text

                                    Log.d("readOur_SKETCH_COMMENT", "Question B: " + tmpResultQuestionB);

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
                                    tmpResultQuestionC = Integer.valueOf(xpp.getText().trim()); // make int from xml-text

                                    Log.d("readOur_SKETCH_COMMENT", "Question C: " + tmpResultQuestionC);

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
                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_UploadTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get upload time text
                                if (xpp.getText().trim().length() > 0) { // check if upload time from xml > 0
                                    tmpUploadTime = Long.valueOf(xpp.getText().trim()); // make Long from xml-text

                                    Log.d("readOur_SKETCH_COMMENT", "Upload time: " + tmpUploadTime);

                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;



                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_BlockId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get BlockId text
                                if (xpp.getText().trim().length() > 0) { // check if Block id from xml > 0
                                    tmpBlockId = xpp.getText().trim();

                                    Log.d("Sketch Arr", "BlockID:"+tmpBlockId);
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

                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ServerIdArrangement:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get server id arrangement text
                                if (xpp.getText().trim().length() > 0) { // check if server id arrangement from xml > 0
                                    tmpServerIdArrangement = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Sketch Comment","ServerID:"+tmpServerIdArrangement);


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

                        // check all data for sketch arrangement comment correct?
                        if (!error) {

                            Log.d("SketchComment_DB","C:"+tmpCommentText+" - Au:"+tmpAuthorName+" - CTi:"+tmpCommentTime+" - AId"+tmpServerIdArrangement+" - CoA:"+tmpArrangementTime);

                            // our arrangement sketch comment order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpArrangementTime > 0 && tmpResultQuestionA >= 0 && tmpResultQuestionB >= 0 && tmpResultQuestionC >= 0 && tmpCommentTime > 0 && tmpServerIdArrangement >= 0 && tmpBlockId.length() > 0) {

                                Log.d("SKETCH COMMENT","NEW AUSführen");

                                // set upload time on smartphone for commeent; value from server is ot needed
                                tmpUploadTime = System.currentTimeMillis();

                                // insert new comment in DB
                                myDb.insertRowOurArrangementSketchComment(tmpCommentText, tmpResultQuestionA, tmpResultQuestionB, tmpResultQuestionC, tmpAuthorName, tmpCommentTime, tmpUploadTime, tmpBlockId, true, tmpArrangementTime, 4, tmpServerIdArrangement);

                                // refresh activity ourarrangement and fragment sketch comment
                                returnMap.put("OurArrangement","1");
                                returnMap.put("OurArrangementSketchComment","1");

                            }
                            else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) { // delete all comments for sketch arrangements; needed by init process

                                Log.d("Sketch Comment Arr", "Delete All!!!");

                                // delete all comments for all current sketch arrangements with the blockId
                                myDb.deleteAllRowsOurArrangementSketchComment (tmpBlockId);


                            }

                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(3);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(4);
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
        // switch for arrangement functions
        Boolean tmpArrangementOnOff = false;
        Boolean tmpArrangementSketchOnOff = false;
        Boolean tmpArrangementCommentOnOff = false;
        Boolean tmpArrangementSketchCommentOnOff = false;
        Boolean tmpArrangementEvaluationOnOff = false;
        Boolean tmpArrangementOldOnOff = false;

        // settings order
        String tmpOrder = "";

        // evaluation settings
        int tmpEvaluatePauseTime = 0;
        int tmpEvaluateActiveTime = 0;
        Long tmpEvaluateStartDate = 0L;
        Long tmpEvaluateEndDate = 0L;

        // arrangement comment settings
        int tmpCommentMaxComment = 0;
        int tmpCommentMaxLetters = 0;
        int tmpCommentDelaytime = 0;
        int tmpCommentCountComment = 0;
        Long tmpCommentCountCommentSinceTime = 0L;

        // arragnement sketch comment settings
        int tmpSketchCommentMaxComment = 0;
        int tmpSketchCommentMaxLetters = 0;
        int tmpSketchCommentDelaytime = 0;
        int tmpSketchCommentCountComment = 0;
        Long tmpSketchCommentCountCommentSinceTime = 0L;

        // current date for sketch arrangement and now arrangement
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


                        case ConstansClassXmlParser.xmlNameForOurArrangement_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our arrangement turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpArrangementOnOff = true;}
                                    else {tmpArrangementOnOff = false;}

                                    Log.d("Arrang_Settings","Arrangement On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForOurArrangement_Sketch_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our arrangement sketch turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpArrangementSketchOnOff = true;}
                                    else {tmpArrangementSketchOnOff = false;}

                                    Log.d("Arrang_Settings","Sketch Arrangement On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our arrangement comment turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpArrangementCommentOnOff = true;}
                                    else {tmpArrangementCommentOnOff = false;}

                                    Log.d("Arrang_Settings","Arrangement Comment On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForOurArrangementOld_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our arrangement old turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpArrangementOldOnOff = true;}
                                    else {tmpArrangementOldOnOff = false;}

                                    Log.d("Arrang_Settings","Arrangement Old On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;



                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our arrangement sketch comment turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpArrangementSketchCommentOnOff = true;}
                                    else {tmpArrangementSketchCommentOnOff = false;}

                                    Log.d("Arrang_Settings","Arrangement Sketch Comment On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our arrangement evaluation turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpArrangementEvaluationOnOff = true;}
                                    else {tmpArrangementEvaluationOnOff = false;}

                                    Log.d("Arrang_Settings","Arrangement Evaluation On/Off"+tmpSwitchValue);


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
                                    tmpEvaluatePauseTime = Integer.valueOf(xpp.getText().trim()) * 3600; // make seconds from hours

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
                                    tmpEvaluateActiveTime = Integer.valueOf(xpp.getText().trim())* 3600; // make seconds from hours

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
                                    tmpEvaluateStartDate = Long.valueOf(xpp.getText().trim())* 1000; // make mills from seconds

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
                                    tmpEvaluateEndDate = Long.valueOf(xpp.getText().trim())* 1000; // make mills from seconds

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

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_CommentMaxLetters:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max letters
                                if (xpp.getText().trim().length() > 0) { // check if max letters from xml > 0
                                    tmpCommentMaxLetters = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","CommentMaxLetters"+tmpCommentMaxLetters);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_CommentDelaytime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get delaytime
                                if (xpp.getText().trim().length() > 0) { // check if delaytime from xml > 0
                                    tmpCommentDelaytime = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","CommentDelaytime"+tmpCommentDelaytime);


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

                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_SketchCommentMaxLetters:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max comment sketch letters
                                if (xpp.getText().trim().length() > 0) { // check if max comment sketch letters from xml > 0
                                    tmpSketchCommentMaxLetters = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","SketchCommentMaxLetters"+tmpSketchCommentMaxLetters);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_SketchCommentDelaytime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get comment sketch delaytime
                                if (xpp.getText().trim().length() > 0) { // check if comment sketch delaytime from xml > 0
                                    tmpSketchCommentDelaytime = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Arrang_Settings","SketchCommentDelaytime"+tmpSketchCommentDelaytime);


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
                                returnMap.put("OurArrangement","1");
                                returnMap.put("OurArrangementSettings","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // our arrangement settings order -> update?

                                Log.d("Settings Arrangement","UPDATE AUSführen");

                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, tmpArrangementOnOff); // turn function our arrangement on/off
                                prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, tmpArrangementSketchOnOff); // turn function our arrangement sketch on/off
                                prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowOldArrangement, tmpArrangementOldOnOff); // turn function our arrangement old on/off
                                prefsEditor.commit();

                                // update evaluation of arrangements?
                                if (tmpArrangementEvaluationOnOff && tmpEvaluatePauseTime > 0 && tmpEvaluateActiveTime > 0 && tmpEvaluateStartDate > 0 && tmpEvaluateEndDate > 0) {

                                    Log.d("Arrangement Evaluation","einschalten");
                                    Log.d ("Settings--","PT:"+tmpEvaluatePauseTime+"AT:"+tmpEvaluateActiveTime+"SD:"+tmpEvaluateStartDate+"ED:"+tmpEvaluateEndDate);

                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, tmpArrangementEvaluationOnOff); // turn function on
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, tmpEvaluatePauseTime);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, tmpEvaluateActiveTime);
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, tmpEvaluateStartDate);
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, tmpEvaluateEndDate);
                                    prefsEditor.commit();

                                    // something change in evaluation process
                                    returnMap.put("OurArrangementSettingsEvaluationProcess","1");
                                }
                                else { // turn function arrangement evaluation off
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, tmpArrangementEvaluationOnOff); // turn function off
                                    prefsEditor.commit();
                                    // something change in evaluation process
                                    returnMap.put("OurArrangementSettingsEvaluationProcess","1");

                                    Log.d("Arrangement Evaluation","ausschalten");

                                }

                                // update comment max/count of arrangements?
                                if (tmpArrangementCommentOnOff && tmpCommentMaxComment > 0 && tmpCommentMaxLetters > 0 && tmpCommentDelaytime > 0 && tmpCommentCountComment >= 0 && tmpCommentCountCommentSinceTime > 0) {
                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, tmpArrangementCommentOnOff); // turn function on
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, tmpCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentMaxLetters, tmpCommentMaxLetters);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, tmpCommentDelaytime);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentCountComment, tmpCommentCountComment);
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, tmpCommentCountCommentSinceTime);
                                    prefsEditor.commit();

                                    // something change in arrangement comment process
                                    returnMap.put("OurArrangementSettingsCommentProcess","1");
                                }
                                else { // turn function arrangement comment off
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, tmpArrangementCommentOnOff); // turn function on
                                    prefsEditor.commit();
                                    // something change in arrangement comment process
                                    returnMap.put("OurArrangementSettingsCommentProcess","1");

                                    Log.d("Arrangement comment","ausschalten");
                                }

                                // update sketch comment max/count of sketch arrangements?
                                if (tmpArrangementSketchCommentOnOff && tmpSketchCommentMaxComment > 0 && tmpSketchCommentMaxLetters > 0 && tmpSketchCommentDelaytime > 0 && tmpSketchCommentCountComment >= 0 && tmpSketchCommentCountCommentSinceTime > 0) {
                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, tmpArrangementSketchCommentOnOff); // turn function on
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,tmpSketchCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime,tmpSketchCommentDelaytime);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsMaxSketchCommentLetters,tmpSketchCommentMaxLetters);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, tmpSketchCommentCountComment);
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, tmpSketchCommentCountCommentSinceTime);
                                    prefsEditor.commit();

                                    // something change in sketch arragement comment process
                                    returnMap.put("OurArrangementSettingsSketchCommentProcess","1");
                                }
                                else { // turn function arrangement sketch comment off
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, tmpArrangementSketchCommentOnOff); // turn function on
                                    prefsEditor.commit();
                                    // something change in sketch arragement comment process
                                    returnMap.put("OurArrangementSettingsSketchCommentProcess","1");

                                    Log.d("Arrang sketch comment","ausschalten");
                                }

                                // update sketch current date of sketch arrangement?
                                if (tmpSketchCurrentDateOfArrangement > 0) {

                                    // write data to prefs
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, tmpSketchCurrentDateOfArrangement);
                                    prefsEditor.commit();

                                    // something change in sketch arrangement author name
                                    returnMap.put("OurArrangementSettingsSketchCurrentDateOfSketchArrangement","1");

                                }

                                // update current date of arrangement?
                                if (tmpCurrentDateOfArrangement > 0) {

                                    // write data to prefs
                                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, tmpCurrentDateOfArrangement);
                                    prefsEditor.commit();

                                    // something change in sketch arrangement author name
                                    returnMap.put("OurArrangementSettingsCurrentDateOfArrangement","1");

                                }

                                // refresh activity ourarrangement because settings have change
                                returnMap.put("OurArrangement","1");
                                returnMap.put("OurArrangementSettings","1");

                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(5);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(6);
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
            // set error
            setErrorMessageInPrefs(7);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(8);
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
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsJointlyNow","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our goal order -> delete entry?

                                // delete goal in DB
                                myDb.deleteRowOurGoals(tmpOldMd5);

                                // refresh activity ourgoals and fragement jointly goal now
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsJointlyNow","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpGoalText.length() > 0 && tmpAuthorName.length() > 0 && tmpGoalTime > 0) { // our goal order -> update entry?

                                // update arrangement in DB
                                myDb.updateRowOurGoals(tmpGoalText, tmpAuthorName, tmpGoalTime, true, tmpJointlyDebetable, 4, tmpOldMd5);

                                // refresh activity ourgoals and fragement jointly goal now
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsJointlyNow","1");

                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(9);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(10);
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
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsJointlyComment","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our goals jointly comment order -> delete entry?

                                // delete arrangement comment in DB
                                myDb.deleteRowOurGoalJointlyGoalComment(tmpOldMd5);

                                // refresh activity ourgoals and fragment jointly comment
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsJointlyComment","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpGoalId >= 0 && tmpGoalTime > 0) { // our goals jointly comment order -> update entry?

                                // update jointly comment in DB
                                myDb.updateRowOurGoalJointlyGoalComment(tmpCommentText, tmpAuthorName, tmpCommentTime, tmpGoalId, true,  tmpGoalTime, 4, tmpOldMd5);

                                // refresh activity ourgoals and fragment jointly comment
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsJointlyComment","1");

                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(11);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(12);
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
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableComment","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our goals jointly comment order -> delete entry?

                                // delete arrangement comment in DB
                                myDb.deleteRowOurGoalsDebetableGoalsComment(tmpOldMd5);

                                // refresh activity ourgoals and fragment debetable comment
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableComment","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentTime > 0 && tmpGoalId >= 0 && tmpGoalTime > 0) { // our goals jointly comment order -> update entry?

                                // update jointly comment in DB
                                myDb.updateRowOurGoalsDebetableGoalsComment(tmpCommentText, tmpREsultQuestionA, tmpREsultQuestionB, tmpREsultQuestionC, tmpAuthorName, tmpCommentTime, tmpGoalId, true,  tmpGoalTime, 4, tmpOldMd5);

                                // refresh activity ourgoals and fragment debetable comment
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableComment","1");
                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(13);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(14);
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
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableNow","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMd5.length() > 1) { // our goal order -> delete entry?

                                // delete goal in DB
                                myDb.deleteRowOurGoals(tmpOldMd5);

                                // refresh activity ourgoals and fragement debetable goal now
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableNow","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMd5.length() > 1 && tmpDebetableGoalText.length() > 0 && tmpDebetableAuthorName.length() > 0 && tmpDebetableGoalTime > 0) { // our goal order -> update entry?

                                // update goal in DB
                                myDb.updateRowOurGoals(tmpDebetableGoalText, tmpDebetableAuthorName, tmpDebetableGoalTime, true, tmpJointlyDebetable, 4, tmpOldMd5);

                                // refresh activity ourgoals and fragement jointly goal now
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableNow","1");

                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(15);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(16);
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
        // switch for goals functions
        Boolean tmpGoalsOnOff = false;
        Boolean tmpGoalsJointlyCommentOnOff = false;
        Boolean tmpGoalsDebetableOnOff = false;
        Boolean tmpGoalsDebetableCommentOnOff = false;
        Boolean tmpGoalsEvaluationOnOff = false;
        Boolean tmpGoalsOldOnOff = false;

        String tmpOrder = "";

        int tmpJointlyEvaluatePauseTime = 0;
        int tmpJointlyEvaluateActiveTime = 0;
        Long tmpJointlyEvaluateStartDate = 0L;
        Long tmpJointlyEvaluateEndDate = 0L;

        int tmpJointlyCommentMaxComment = 0;
        int tmpJointlyCommentMaxLetters = 0;
        int tmpJointlyCommentCountComment = 0;
        Long tmpJointlyCommentCountCommentSinceTime = 0L;

        int tmpDebetableCommentMaxComment = 0;
        int tmpDebetableCommentMaxLetters = 0;
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



                        case ConstansClassXmlParser.xmlNameForOurGoals_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our goals turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpGoalsOnOff = true;}
                                    else {tmpGoalsOnOff = false;}

                                    Log.d("Goals_Settings","Goals On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our goals debetable turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpGoalsDebetableOnOff = true;}
                                    else {tmpGoalsDebetableOnOff = false;}

                                    Log.d("Goals_Settings","Goals Dedetable On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our goals evaluation turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpGoalsEvaluationOnOff = true;}
                                    else {tmpGoalsEvaluationOnOff = false;}

                                    Log.d("Goals_Settings","Goals Evaluation On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our goals jointly comment turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpGoalsJointlyCommentOnOff = true;}
                                    else {tmpGoalsJointlyCommentOnOff = false;}

                                    Log.d("Goals_Settings","Goals Join Comment On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our goals debetable comment turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpGoalsDebetableCommentOnOff = true;}
                                    else {tmpGoalsDebetableCommentOnOff = false;}

                                    Log.d("Goals_Settings","Goals Debet Comment On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForOurGoalsOld_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch our goals old turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpGoalsOldOnOff = true;}
                                    else {tmpGoalsOldOnOff = false;}

                                    Log.d("Goals_Settings","Goals Old On/Off"+tmpSwitchValue);


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

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_CommentMaxLetters:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max comment letters
                                if (xpp.getText().trim().length() > 0) { // check if max comment letters from xml > 0
                                    tmpJointlyCommentMaxLetters = Integer.valueOf(xpp.getText().trim());

                                    Log.d("goals_Settings","JointlyCommentMaxLetters"+tmpJointlyCommentMaxLetters);


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

                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_DebetableCommentMaxLetters:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max comment debetable letters goals
                                if (xpp.getText().trim().length() > 0) { // check if max comment debetable letters goals from xml > 0
                                    tmpDebetableCommentMaxLetters = Integer.valueOf(xpp.getText().trim());

                                    Log.d("Goals_Settings","DebetableCommentMaxLetters"+tmpDebetableCommentMaxLetters);


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
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsSettings","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // our goals settings order -> update?

                                Log.d("Goals Settings","UPDATE AUSführen");

                                // write data to prefs
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, tmpGoalsOnOff); // turn function our goals on/off
                                prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkDebetableGoals, tmpGoalsDebetableOnOff); // turn function our goals debetable on/off
                                prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkOldGoals, tmpGoalsOldOnOff); // turn function our goals old on/off
                                prefsEditor.commit();


                                // update evaluation of jointly goals?
                                if (tmpGoalsEvaluationOnOff && tmpJointlyEvaluatePauseTime > 0 && tmpJointlyEvaluateActiveTime > 0 && tmpJointlyEvaluateStartDate > 0 && tmpJointlyEvaluateEndDate > 0) {

                                    Log.d ("Goals Settings--","PT:"+tmpJointlyEvaluatePauseTime+"AT:"+tmpJointlyEvaluateActiveTime+"SD:"+tmpJointlyEvaluateStartDate+"ED:"+tmpJointlyEvaluateEndDate);

                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, tmpGoalsEvaluationOnOff); // turn function on
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, tmpJointlyEvaluatePauseTime);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, tmpJointlyEvaluateActiveTime);
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, tmpJointlyEvaluateStartDate);
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, tmpJointlyEvaluateEndDate);
                                    prefsEditor.commit();
                                    // something change in evaluation process
                                    returnMap.put ("OurGoalsSettingsEvaluationProcess","1");
                                }
                                else { // turn function our goals evaluation off
                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, tmpGoalsEvaluationOnOff); // turn function off
                                    prefsEditor.commit();
                                    // something change in evaluation process
                                    returnMap.put ("OurGoalsSettingsEvaluationProcess","1");
                                }

                                // update comment max/count of jointly goals?
                                if (tmpGoalsJointlyCommentOnOff && tmpJointlyCommentMaxComment > 0 && tmpJointlyCommentMaxLetters > 0 && tmpJointlyCommentCountComment >= 0 && tmpJointlyCommentCountCommentSinceTime > 0) {

                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentJointlyGoals, tmpGoalsJointlyCommentOnOff); // turn function on
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, tmpJointlyCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyLetters, tmpJointlyCommentMaxLetters);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, tmpJointlyCommentCountComment);
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsJointlyCommentTimeSinceInMills, tmpJointlyCommentCountCommentSinceTime);
                                    prefsEditor.commit();
                                    // something change in jointly goals comment process
                                    returnMap.put ("OurGoalsSettingsCommentProcess","1");
                                }
                                else { // turn function our goals jointly comment off
                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentJointlyGoals, tmpGoalsJointlyCommentOnOff); // turn function off
                                    prefsEditor.commit();
                                    // something change in jointly goals comment process
                                    returnMap.put ("OurGoalsSettingsCommentProcess","1");
                                }

                                // update debetable comment max/count of debetable goals?
                                if (tmpGoalsDebetableCommentOnOff && tmpDebetableCommentMaxComment > 0 && tmpDebetableCommentMaxLetters > 0 && tmpDebetableCommentCountComment >= 0 && tmpDebetableCommentCountCommentSinceTime > 0) {

                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentDebetableGoals, tmpGoalsDebetableCommentOnOff); // turn function on
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment,tmpDebetableCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableLetters,tmpDebetableCommentMaxLetters);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, tmpDebetableCommentCountComment);
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsDebetableCommentTimeSinceInMills, tmpDebetableCommentCountCommentSinceTime);
                                    prefsEditor.commit();
                                    // something change in debetable goals comment process
                                    returnMap.put ("OurGoalsSettingsDebetableCommentProcess","1");
                                }
                                else { // turn function our goals debetable comment off
                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentDebetableGoals, tmpGoalsDebetableCommentOnOff); // turn function off
                                    prefsEditor.commit();
                                    // something change in debetable goals comment process
                                    returnMap.put ("OurGoalsSettingsDebetableCommentProcess","1");
                                }

                                // update debetable goals author name?
                                if (tmpDebetableGoalsAuthorName.length() > 0) {

                                    // write data to prefs
                                    prefsEditor.putString(ConstansClassOurGoals.namePrefsAuthorOfDebetableGoals, tmpDebetableGoalsAuthorName);
                                    prefsEditor.commit();
                                    // something change in debetable goals author name
                                    returnMap.put ("OurGoalsSettingsDebetableGoalsAuthorName","1");
                                }

                                // update debetable current date of debetable goals?
                                if (tmpDebetableCurrentDateOfGoals > 0) {

                                    Log.d ("Set Debet date","Set: "+tmpDebetableCurrentDateOfGoals);
                                    Log.d ("Set Debet Systime","SystemTime: "+System.currentTimeMillis());

                                    // write data to prefs
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, tmpDebetableCurrentDateOfGoals);
                                    prefsEditor.commit();
                                    // something change in current date of debetable goals
                                    returnMap.put ("OurGoalsSettingsDebetableCurrentDateOfDebetableGoals","1");
                                }

                                // update current date of jointly goals?
                                if (tmpJointlyCurrentDateOfGoals > 0) {

                                    // write data to prefs
                                    prefsEditor.putLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, tmpJointlyCurrentDateOfGoals);
                                    prefsEditor.commit();
                                    // something change in current date of jointly goals
                                    returnMap.put ("OurGoalsSettingsJointlyCurrentDateOfJointlyGoals","1");
                                }

                                // refresh activity ourarrangement because settings have change
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsSettings","1");

                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(17);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(18);
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
            // set error
            setErrorMessageInPrefs(19);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(20);
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
        Boolean tmpMeetingOnOff = false;
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


                        case ConstansClassXmlParser.xmlNameForMeeting_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch meeting turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpMeetingOnOff = true;}
                                    else {tmpMeetingOnOff = false;}

                                    Log.d("Meeting_Settings","Meeting On/Off"+tmpSwitchValue);


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
                                returnMap.put ("Meeting","1");
                                returnMap.put ("MeetingSettings","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) ) { // meeting settings order -> delete all?

                                // delete all meeting suggestions in DB
                                myDb.deleteAllRowsMeetingDateAndTime();

                                // refresh activity meeting because settings have change
                                returnMap.put ("Meeting","1");
                                returnMap.put ("MeetingSettings","1");


                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // meeting settings order -> update?

                                Log.d("meeting Settings","UPDATE AUSführen");

                                // in every case -> write data meeting on off to prefs
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Meeting, tmpMeetingOnOff);
                                prefsEditor.commit();


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
                                    returnMap.put ("MeetingSettingsUpdateDateA","1");

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
                                    returnMap.put ("MeetingSettingsUpdateDateB","1");

                                }

                                // update meeting status?
                                if (tmpMeetingStatus >= 0) {

                                    Log.d ("Meetings Settings--","Status:"+tmpMeetingStatus);

                                    // write data to prefs
                                    prefsEditor.putInt(ConstantsClassMeeting.namePrefsMeetingStatus, tmpMeetingStatus);
                                    prefsEditor.commit();

                                    // something change in meeting status
                                    returnMap.put ("MeetingSettingsUpdateStatus","1");

                                }

                                // refresh activity meeting because settings have change
                                returnMap.put ("Meeting","1");
                                returnMap.put ("MeetingSettings","1");

                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(21);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(22);
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
                                    tmpMeetingSuggestionPlace = xpp.getText().trim();
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
                                returnMap.put ("Meeting","1");
                                returnMap.put ("MeetingNewSuggestion","1");


                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpOldMeetingSuggestionTime > 0) { // meeting order -> delete suggestion entry?

                                // delete meeting suggestion in DB
                                myDb.deleteRowMeetingDateAndTime(tmpOldMeetingSuggestionTime);

                                // refresh activity meeting
                                returnMap.put ("Meeting","1");
                                returnMap.put ("MeetingNewSuggestion","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpOldMeetingSuggestionTime > 0 && tmpMeetingSuggestionTime > 0 && tmpMeetingSuggestionPlace.length() > 0) { // meeting suggestion order -> update entry?

                                // update goal in DB
                                myDb.updateMeetingDateAndTime(tmpMeetingSuggestionTime, tmpMeetingSuggestionPlace, tmpOldMeetingSuggestionTime, true, 4);

                                // refresh activity meeting
                                returnMap.put ("Meeting","1");
                                returnMap.put ("MeetingNewSuggestion","1");

                             } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpAuthorSuggestions.length() > 0) { // meeting suggestion order -> update author name?


                                Log.d("Meetings Suggestions--", "Author NAme:" + tmpAuthorSuggestions);

                                // write new author name of suggestions to prefs
                                prefsEditor.putString(ConstantsClassMeeting.namePrefsAuthorMeetingSuggestion, tmpAuthorSuggestions);
                                prefsEditor.commit();

                                // refresh activity meeting
                                returnMap.put ("Meeting","1");
                                returnMap.put ("MeetingAuthorSuggestion","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpResponseDeadline > 0) { // meeting suggestion order -> update response deadline?


                                Log.d("Meetings Suggestions--", "Response Deadline:" + tmpResponseDeadline);

                                // write new author name of suggestions to prefs
                                prefsEditor.putLong(ConstantsClassMeeting.namePrefsMeetingSuggestionsResponseDeadline, tmpResponseDeadline);
                                prefsEditor.commit();

                                // refresh activity meeting
                                returnMap.put ("Meeting","1");
                                returnMap.put ("MeetingResponseDeadline","1");
                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(23);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(24);
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

        Boolean parseAnymore = true;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readConnectBTag", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForConnectBook_Messages:
                            readConnectBookTag_Messages();
                            break;

                        case ConstansClassXmlParser.xmlNameForConnectBook_Settings:
                            readConnectBookTag_Settings();
                            break;
                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {parseAnymore = false;
                    Log.d("readConnectBookTag", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of connect book
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForConnectBook)) {

                        Log.d("readConnect Book Tag", "End Tag connect book gefunden!");
                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(25);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(26);
            e.printStackTrace();
        }
    }




    // read element connect book messages
    private void readConnectBookTag_Messages() {


    }






    // read element connect book settings
    private void readConnectBookTag_Settings() {

        Log.d("read_ConnectBSettings", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml connect book settings tag
        Boolean error = false;

        // tmp data for prefs
        Boolean tmpConnectBookOnOff = false;
        String tmpOrder = "";
        String tmpClientName = "";
        int tmpMaxLetters = -1;
        int tmpMaxMessages = -1;
        int tmpDelayTime = -1;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readConnectB_SETTINGS", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForConnectBook_Order:
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


                        case ConstansClassXmlParser.xmlNameForConnectBook_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch meeting turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpConnectBookOnOff = true;}
                                    else {tmpConnectBookOnOff = false;}

                                    Log.d("Connect B_Settings","C Book On/Off"+tmpSwitchValue);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForConnectBook_ClientName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { //  get client name
                                if (xpp.getText().trim().length() > 0) { // check if client name from xml > 0
                                    tmpClientName =  xpp.getText().trim();

                                    Log.d("Connect B_Settings","Client Name"+tmpClientName);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForConnectBook_DelayTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get send delay time
                                if (xpp.getText().trim().length() > 0) { // check if delay time from xml > 0
                                    tmpDelayTime = Integer.valueOf(xpp.getText().trim());

                                    Log.d("ConnectB_Settings","SendDelayTime"+tmpDelayTime);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForConnectBook_MaxMessages:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max messages
                                if (xpp.getText().trim().length() > 0) { // check if max messages from xml > 0
                                    tmpMaxMessages = Integer.valueOf(xpp.getText().trim());

                                    Log.d("ConnectB_Settings","Max Messages"+tmpMaxMessages);


                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForConnectBook_MaxLetters:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max letters
                                if (xpp.getText().trim().length() > 0) { // check if max letters from xml > 0
                                    tmpMaxLetters = Integer.valueOf(xpp.getText().trim());

                                    Log.d("ConnectB_Settings","Max Letters"+tmpMaxLetters);


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

                // look for end tag of connect book settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForConnectBook_Settings)) {

                        // check all data for connect book settings correct?
                        if (!error) {

                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) ) { // connect book settings order -> delete?

                                Log.d("Connect Book Settings","DELETE AUSführen");

                                // refresh activity connect book because settings have change
                                returnMap.put ("ConnectBook","1");
                                returnMap.put ("ConnectBookSettings","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // connect book settings order -> update?

                                Log.d("connect book Settings","UPDATE AUSführen");

                                // in every case -> write data connect book on off to prefs
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_ConnectBook, tmpConnectBookOnOff);
                                prefsEditor.commit();

                                // write send delay time, max letters and max messages to prefs when all set
                                if (tmpDelayTime >=0 && tmpMaxLetters>=0 && tmpMaxMessages>=0) {
                                    prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectSendDelayTime, tmpDelayTime);
                                    prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectMaxLetters, tmpMaxLetters);
                                    prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectMaxMessages, tmpMaxMessages);
                                    prefsEditor.commit();
                                }

                                // update client name?
                                if (tmpClientName.length() > 0) {

                                    Log.d ("Connect Book Settings--","Nmae:"+tmpClientName);

                                    // write data to prefs
                                    prefsEditor.putString(ConstansClassConnectBook.namePrefsConnectBookUserName, tmpClientName);
                                    prefsEditor.commit();

                                    // something change in meeting status
                                    returnMap.put ("ConnectBookSettingsClientName","1");

                                }

                                // refresh activity connect book because settings have change
                                returnMap.put ("ConnectBook","1");
                                returnMap.put ("ConnectBookSettings","1");

                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(27);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(28);
            e.printStackTrace();
        }

    }



    //
    // End read connect book -----------------------------------------------------------------------------------
    //




    //
    // Begin read settings -----------------------------------------------------------------------------------
    //

    // read element settings
    private void readSettingTag() {


        Log.d("read_SETTINGS", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our arrangement settings comment tag
        Boolean error = false;

        // tmp data for prefs and database insert
        // switch for functions
        Boolean tmpPreventionOnOff = false;
        Boolean tmpFaqOnOff = false;
        Boolean tmpEmergencyOnOff = false;
        Boolean tmpSettingsOnOff = false;

        // settings order
        String tmpOrder = "";


        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("readOur_SETTINGS", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForSettings_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) {
                                        error = true;
                                        tmpOrder = "";
                                    }

                                    Log.d("Settings Settings", "ORDER: " + tmpOrder);


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForSettings_Prevention_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch prevention turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {
                                        tmpPreventionOnOff = true;
                                    } else {
                                        tmpPreventionOnOff = false;
                                    }

                                    Log.d("Settings", "Prevention On/Off" + tmpSwitchValue);


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForSettings_Faq_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch faq turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {
                                        tmpFaqOnOff = true;
                                    } else {
                                        tmpFaqOnOff = false;
                                    }

                                    Log.d("Settings", "Faq On/Off" + tmpSwitchValue);


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForSettings_Emergency_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch emergency help turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {
                                        tmpEmergencyOnOff = true;
                                    } else {
                                        tmpEmergencyOnOff = false;
                                    }

                                    Log.d("Settings", "Emergency On/Off" + tmpSwitchValue);


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;

                        case ConstansClassXmlParser.xmlNameForSettings_Settings_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch settings turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {
                                        tmpSettingsOnOff = true;
                                    } else {
                                        tmpSettingsOnOff = false;
                                    }

                                    Log.d("Settings", "Settings On/Off" + tmpSwitchValue);


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;

                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
                    Log.d("ABBRUCH Settings!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForSettings)) {

                        // check all data for settings correct?
                        if (!error) {


                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) ) { // settings order -> delete?

                                Log.d("Settings","DELETE AUSführen");


                                // refresh activity settings because settings have change
                                returnMap.put("Settings","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // settings order -> update?

                                Log.d("Settings","UPDATE AUSführen");


                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Prevention, tmpPreventionOnOff); // turn function prevention on/off
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Faq, tmpFaqOnOff); // turn function faq on/off
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_EmergencyHelp, tmpEmergencyOnOff); // turn function emergency help on/off
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Settings, tmpSettingsOnOff); // turn function settings on/off
                                prefsEditor.commit();

                                // refresh activity settings because settings have change
                                returnMap.put("Settings","1");


                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(29);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(30);
            e.printStackTrace();
        }
    }







    // read element time table
    private void readTimeTableTag() {


        Log.d("read_TimeTable", "Zeile " + xpp.getLineNumber());

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml time table tag
        Boolean error = false;

        // tmp data for prefs and database insert
        Boolean tmpTimeTableOnOff = false; // switch for functions
        int tmpTimeTableValue = -1;

        // time table order
        String tmpOrder = "";




        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("read TimeTable", "Start tag " + xpp.getName());

                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForTimeTable_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) {
                                        error = true;
                                        tmpOrder = "";
                                    }

                                    Log.d("Time Table", "ORDER: " + tmpOrder);


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForTimeTable_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch time table turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {
                                        tmpTimeTableOnOff = true;
                                    } else {
                                        tmpTimeTableOnOff = false;
                                    }

                                    Log.d("Settings", "Time Table On/Off" + tmpSwitchValue);


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;


                        case ConstansClassXmlParser.xmlNameForTimeTable_Value:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch faq turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    tmpTimeTableValue = Integer.valueOf(xpp.getText().trim());


                                    Log.d("TimeTable", "Value: " + tmpTimeTableValue);


                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }

                            break;



                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
                    Log.d("ABBRUCH Settings!!!!!", "ABBRUCH DURCH END DOCUMENT!");
                }

                // look for end tag of settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForTimeTable)) {

                        // check all data for time table correct?
                        if (!error) {


                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) ) { // settings order -> delete?

                                Log.d("TimeTable","DELETE AUSführen");


                                // refresh activity time table because settings have change
                                returnMap.put("TimeTable","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // settings order -> update?

                                Log.d("TimeTable","UPDATE AUSführen");

                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_TimeTable, tmpTimeTableOnOff); // turn function time table on/off

                                if (tmpTimeTableValue >= 0) { // change time table value?
                                    prefsEditor.putInt(ConstansClassTimeTable.namePrefsTimeTableValue, tmpTimeTableValue); // set value for time table
                                    returnMap.put("TimeTableValue","1");
                                }

                                prefsEditor.commit();


                                // refresh activity time table because settings have change
                                returnMap.put("TimeTable","1");


                            }
                        }

                        parseAnymore = false;
                    }
                }
            }
        }
        catch (XmlPullParserException e) {
            // set error
            setErrorMessageInPrefs(31);
            e.printStackTrace();
        } catch (IOException e) {
            // set error
            setErrorMessageInPrefs(32);
            e.printStackTrace();
        }
    }



    
    
    private void setErrorMessageInPrefs (int position) {

        String tmpErrorText = "Es ist ein Kommunikationsfehler (XML) aufgetreten (Position:" + position + ")";
        
        // write last error messages to prefs
        prefsEditor.putString(ConstansClassSettings.namePrefsLastErrorMessages, tmpErrorText);
        
        // set connection status to error
        prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus, 1);
        
        prefsEditor.commit();
        
        //setErrorMessageInPrefs();
        
    }
    
    
    



}
