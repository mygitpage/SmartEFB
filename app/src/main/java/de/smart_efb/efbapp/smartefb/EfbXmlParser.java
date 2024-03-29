package de.smart_efb.efbapp.smartefb;


import android.content.Context;
import android.content.SharedPreferences;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
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
    private XmlPullParser xpp;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // context of xmlParser
    private Context xmlContext;

    // reference to the DB
    private DBAdapter myDb;

    // return information for change
    Map<String, String> returnMap;

    // server time global
    Long globalServerTime = 0L; // time comes from server with main header from xml (in mills)


    EfbXmlParser(Context tmpXmlContext) {

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
        returnMap.put("ErrorCommunication", "0");

        returnMap.put("ErrorText", "");
        returnMap.put("ClientId", "");
        returnMap.put("ConnectionStatus", "0");
        returnMap.put("SendSuccessfull", "0");
        returnMap.put("SendNotSuccessfull", "0");

        returnMap.put("AskForTimeSuccessfull","0");
        returnMap.put("ServerTimeInMills", "");

        returnMap.put("ConnectBook", "0");
        returnMap.put("ConnectBookSettings", "0");
        returnMap.put("ConnectBookSettingsMessageAndDelay", "0");
        returnMap.put("ConnectBookSettingsMessageShareEnable","0");
        returnMap.put("ConnectBookSettingsMessageShareDisable","0");
        returnMap.put("ConnectBookMessageNewOrSend","0"); // only set in ExchangeService to refresh connect book view
        returnMap.put("ConnectBookMainActivityUpdateView", "0");
        returnMap.put("ConnectBookSettingsMainActivityUpdateView", "0");

        returnMap.put("OurArrangement", "0");
        returnMap.put("OurArrangementNow", "0");
        returnMap.put("OurArrangementNowComment", "0");
        returnMap.put("OurArrangementSketch", "0");
        returnMap.put("OurArrangementSketchComment", "0");
        returnMap.put("OurArrangementSettings", "0");
        returnMap.put("OurArrangementSettingsEvaluationProcess", "0");
        returnMap.put("OurArrangementSettingsCommentProcess", "0");
        returnMap.put("OurArrangementSettingsCommentShareDisable","0");
        returnMap.put("OurArrangementSettingsCommentShareEnable","0");
        returnMap.put("OurArrangementSettingsCommentCountComment", "0");
        returnMap.put("OurArrangementSettingsSketchCurrentDateOfSketchArrangement", "0");
        returnMap.put("OurArrangementSettingsCurrentDateOfArrangement", "0");
        returnMap.put("OurArrangementSettingsSketchCommentShareDisable","0");
        returnMap.put("OurArrangementSettingsSketchCommentShareEnable","0");
        returnMap.put("OurArrangementSettingsSketchCommentCountComment", "0");
        returnMap.put("OurArrangementNowMainActivityUpdateView", "0");
        returnMap.put("OurArrangementNowCommentMainActivityUpdateView", "0");
        returnMap.put("OurArrangementSketchMainActivityUpdateView", "0");
        returnMap.put("OurArrangementSketchCommentMainActivityUpdateView", "0");
        returnMap.put("OurArrangementSettingsMainActivityUpdateView", "0");

        returnMap.put("OurGoals", "0");
        returnMap.put("OurGoalsJointlyNow", "0");
        returnMap.put("OurGoalsDebetableNow", "0");
        returnMap.put("OurGoalsJointlyComment", "0");
        returnMap.put("OurGoalsDebetableComment", "0");
        returnMap.put("OurGoalsSettings", "0");
        returnMap.put("OurGoalsSettingsEvaluationProcess", "0");
        returnMap.put("OurGoalsSettingsCommentProcess", "0");
        returnMap.put("OurGoalsSettingsCommentShareDisable","0");
        returnMap.put("OurGoalsSettingsCommentShareEnable","0");
        returnMap.put("OurGoalsSettingsCommentCountComment", "0");
        returnMap.put("OurGoalsSettingsDebetableCommentProcess", "0");
        returnMap.put("OurGoalsSettingsDebetableGoalsAuthorName", "0");
        returnMap.put("OurGoalsSettingsDebetableCurrentDateOfDebetableGoals", "0");
        returnMap.put("OurGoalsSettingsJointlyCurrentDateOfJointlyGoals", "0");
        returnMap.put("OurGoalsSettingsDebetableCommentShareDisable","0");
        returnMap.put("OurGoalsSettingsDebetableCommentShareEnable","0");
        returnMap.put("OurGoalsSettingsDebetableCommentCountComment", "0");
        returnMap.put("OurGoalsJointlyMainActivityUpdateView", "0");
        returnMap.put("OurGoalsJointlyCommentMainActivityUpdateView", "0");
        returnMap.put("OurGoalsDebetableMainActivityUpdateView", "0");
        returnMap.put("OurGoalsDebetableCommentMainActivityUpdateView", "0");
        returnMap.put("OurGoalsSettingsMainActivityUpdateView", "0");

        returnMap.put("Meeting", "0");
        returnMap.put("MeetingSettings", "0");
        returnMap.put("MeetingNewMeeting", "0");
        returnMap.put("MeetingCanceledMeetingByCoach", "0");
        returnMap.put("MeetingNewSuggestion", "0");
        returnMap.put("MeetingCanceledSuggestionByCoach", "0");
        returnMap.put("MeetingFoundFromSuggestion", "0");
        returnMap.put("MeetingNewInvitationSuggestion", "0");
        returnMap.put("MeetingCanceledClientSuggestionByCoach", "0");
        returnMap.put("MeetingFoundFromClientSuggestion", "0");

        returnMap.put("MeetingNewMeetingMainActivityUpdateView", "0");
        returnMap.put("MeetingCancelMeetingMainActivityUpdateView", "0");
        returnMap.put("MeetingNewSuggestionMeetingMainActivityUpdateView", "0");
        returnMap.put("MeetingCancelSuggestionMainActivityUpdateView", "0");
        returnMap.put("MeetingFoundSuggestionMainActivityUpdateView", "0");
        returnMap.put("MeetingNewInvitationMainActivityUpdateView", "0");
        returnMap.put("MeetingInvitationFoundClientMainActivityUpdateView", "0");
        returnMap.put("MeetingInvitationCancelMainActivityUpdateView", "0");
        returnMap.put("MeetingSettingMainActivityUpdateView", "0");

        returnMap.put("TimeTable", "0");
        returnMap.put("TimeTableNewValue", "0");
        returnMap.put("TimeTableSettings","0");
        returnMap.put("TimeTableNewValueMainActivityUpdateView", "0");
        returnMap.put("TimeTableSettingMainActivityUpdateView","0");

        returnMap.put("MessagesMessage", "0");
        returnMap.put("MessageSettings", "0");
        returnMap.put("MessageMessageNewOrSend","0");
        returnMap.put("MessageSettingsMessageStopSendingEnable","0");
        returnMap.put("MessageSettingsMessageStopSendingDisable","0");
        returnMap.put("MessagesMessageMainActivityUpdateView","0");
        returnMap.put("MessagesSettingsMainActivityUpdateView","0");

        returnMap.put("Settings", "0");
        returnMap.put("SettingsOtherMenueItems","0");
        returnMap.put("InvolvedPerson","0");
        returnMap.put("Case_close", "0");

        returnMap.put("PreventionPrevention","0");
        returnMap.put("PreventionSettingMainActivityUpdateView", "0");
        returnMap.put("FaqFaq","0");
        returnMap.put("FaqSettingMainActivityUpdateView", "0");
        returnMap.put("EmergencyEmergency","0");
        returnMap.put("EmergencySettingMainActivityUpdateView", "0");
        returnMap.put("SettingSetting","0");
        returnMap.put("SettingSettingMainActivityUpdateView", "0");
    }


    Map<String, String> parseXmlInput(String xmlInput) throws XmlPullParserException, IOException {

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
                if (eventType == XmlPullParser.START_TAG) {
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
                        case ConstansClassXmlParser.xmlNameForSettings_CaseInvolvedPerson:
                            if (masterElementFound) {
                                readSettingInvolvedPersonTag();
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMessage:
                            if (masterElementFound) {
                                readMessageTag();
                            }
                            break;
                    }
                }
                // Next XML Element
                eventType = xpp.next();
            }
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
        String tmpActualAppVersion = "";
        String tmpContactId = "";
        String tmpServerTime = "";

        try {

            int eventType = xpp.next();

            while (readMoreXml) {

                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForMain_Order: // xml data order
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if main order from xml > 0
                                    tmpMainOrder = xpp.getText().trim(); // copy main order
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

                        case ConstansClassXmlParser.xmlNameForMain_ErrorText: // xml data error text
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if error text from xml > 0
                                    tmpErrorText = xpp.getText().trim(); // copy error text
                                }
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForMain_ActualAppVersion: // xml data error text
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if app version text from xml > 0
                                    tmpActualAppVersion = xpp.getText().trim(); // copy app version text
                                }
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForMain_ContactId: // xml data contact id
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if contact id from xml > 0
                                    tmpContactId = xpp.getText().trim(); // copy contact id text
                                }
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForMain_ServerTime: // xml data server time
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if server time from xml > 0
                                    tmpServerTime = xpp.getText().trim(); // copy server time text, must change to LONG!
                                }
                            }
                            break;
                    }
                }
                else if (eventType == XmlPullParser.END_DOCUMENT) {

                    readMoreXml = false;

                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMain)) {

                    if (tmpMainOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Init) && tmpClientId.trim().length() > 0 && tmpServerTime.length() > 0) { // init client smartphone

                        // save global server time
                        saveGlobalServerTime (tmpServerTime);

                        // write first init time in prefs (time is from server)
                        prefsEditor.putLong(ConstansClassSettings.namePrefsFirstInitTimeInMills, globalServerTime);
                        // set case close to false
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsCaseClose, false);
                        // write client id to prefs
                        prefsEditor.putString(ConstansClassSettings.namePrefsClientId, tmpClientId);
                        // delete contact id in prefs
                        prefsEditor.putString(ConstansClassSettings.namePrefsContactId, "");
                        // set connection status to connect
                        prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus, 3); // 0=connect to server; 1=no network available; 2=connection error; 3=connected
                        // write last error messages to prefs
                        prefsEditor.putString(ConstansClassSettings.namePrefsLastErrorMessages, "");
                        prefsEditor.apply();

                        // delete all content from db tables (init process)
                        myDb.initDeleteAllContentFromTables();

                        returnMap.put("ClientId", tmpClientId);
                        returnMap.put("ConnectionStatus", "3");
                        returnMap.put("Error", "0");
                        returnMap.put("ErrorText", "");
                        readMoreXml = false;
                    }
                    else if (tmpMainOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Receive_Ok_Send) && tmpServerTime.length() > 0) { // data send and now receive data

                        // save global server time
                        saveGlobalServerTime (tmpServerTime);

                        returnMap.put("ClientId", tmpClientId);
                        returnMap.put("SendSuccessfull", "1");
                        readMoreXml = false;

                    }
                    else if (tmpMainOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Data)) { // receive data

                        readMoreXml = false;

                    }
                    else if (tmpMainOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Error_Init) && tmpErrorText.length() > 0 && tmpServerTime.length() > 0) { // Error in main tag

                        // write last error messages to prefs
                        prefsEditor.putString(ConstansClassSettings.namePrefsLastErrorMessages, tmpErrorText);
                        // set connection status to error
                        prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus, 1); // 0=connect to server; 1=no network available; 2=connection error; 3=connected
                        prefsEditor.apply();

                        // save global server time
                        saveGlobalServerTime (tmpServerTime);

                        returnMap.put("ClientId", "");
                        returnMap.put("ConnectionStatus", "1");
                        returnMap.put("Error", "1");
                        returnMap.put("ErrorText", tmpErrorText);

                        readMoreXml = false;
                    }

                    else if (tmpMainOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Error_Communication) && tmpErrorText.length() > 0 && tmpServerTime.length() > 0) {

                        // save global server time
                        saveGlobalServerTime (tmpServerTime);

                        returnMap.put("ErrorCommunication", "1");
                        returnMap.put("ErrorText", tmpErrorText);
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


    void saveGlobalServerTime (String serverTimeInMills) {

        if (serverTimeInMills.length() > 0) {
            globalServerTime = Long.valueOf(serverTimeInMills) * 1000; // make mills
            // save last contact time with server in prefs
            prefsEditor.putLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, globalServerTime);
            prefsEditor.apply();

            returnMap.put("ServerTimeInMills", serverTimeInMills); // this is a string -> must convert to LONG!
            returnMap.put("AskForTimeSuccessfull", "1");
        }
        else {
            globalServerTime = 0L;
            returnMap.put("ServerTimeInMills", "");
            returnMap.put("AskForTimeSuccessfull","0");
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
                // look for end tag of ourarrangement
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement)) {
                        parseAnymore = false;
                    }
                }
                if (eventType == XmlPullParser.START_TAG) {
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
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_Order:
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
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Now_ArrangementText:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get arrangementText text
                                if (xpp.getText().trim().length() > 0) { // check if arrangementText from xml > 0
                                    tmpArrangementText = xpp.getText().trim();
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
                }

                // look for end tag of our arrangement now
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_Now)) {
                        // check all data for arrangement now correct?
                        if (!error) {
                            // our arrangement order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpArrangementText.length() > 0 && tmpAuthorName.length() > 0 && tmpArrangementTime > 0 && tmpServerId > 0 && tmpBlockId.length() > 0 && tmpChangeTo.length() > 0) {
                                // insert new arrangement in DB
                                myDb.insertRowOurArrangement(tmpArrangementText, tmpAuthorName, tmpArrangementTime, true, tmpSketchCurrent, 0, 4, tmpServerId, tmpBlockId, tmpChangeTo);

                                // write current date of arrangements to pref
                                prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, tmpArrangementTime);
                                // write block id of arrangements to prefs
                                prefsEditor.putString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, tmpBlockId);
                                // reset comment counter
                                prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0);
                                // reset time count comments since
                                prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, System.currentTimeMillis());
                                // signal now arragenemt is updated
                                prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsSignalNowArrangementUpdate, true);

                                prefsEditor.apply();

                                // refresh activity our arrangement and fragement now
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementNow", "1");
                                returnMap.put("OurArrangementNowMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpServerId > 0) { // our arrangement order -> delete entry?

                                // delete arrangement in DB
                                myDb.deleteRowOurArrangement(tmpServerId);

                                // refresh activity ourarrangement and fragement now
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementNow", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpServerId > 0 && tmpArrangementText.length() > 0 && tmpAuthorName.length() > 0 && tmpArrangementTime > 0 && tmpBlockId.length() > 0) { // our arrangement order -> update entry?

                                // update arrangement in DB
                                myDb.updateRowOurArrangement(tmpArrangementText, tmpAuthorName, tmpArrangementTime, true, tmpSketchCurrent, 0, 4, tmpServerId, tmpBlockId);

                                // refresh activity ourarrangement and fragement now
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementNow", "1");
                                returnMap.put("OurArrangementNowMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) {

                                // delete all arrangements now with the blockId
                                myDb.deleteAllRowsOurArrangement(tmpBlockId, false); // false -> all now arrangements; true -> all sketch arrangements

                            }
                        }
                        parseAnymore = false;
                    }
                }
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

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our arrangement now tag
        Boolean error = false;

        // tmp data for database insert
        String tmpCommentText = "";
        String tmpAuthorName = "";
        Long tmpLocalCommentTime = 0L;
        String tmpBlockId = "";
        Long tmpArrangementTime = 0L;
        Long tmpUploadTime = 0L;
        String tmpOrder = "";
        int tmpServerIdArrangement = 0;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
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
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_Comment:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get comment text
                                if (xpp.getText().trim().length() > 0) { // check if commentText from xml > 0
                                    tmpCommentText = xpp.getText().trim();
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
                        case ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_CommentLocaleTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get commentTime text
                                if (xpp.getText().trim().length() > 0) { // check if commentTime from xml > 0
                                    tmpLocalCommentTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
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
                                    tmpArrangementTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
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
                }

                // look for end tag of ourarrangement now
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_NowComment)) {

                        // check all data for arrangement comment correct?
                        if (!error) {

                            // our arrangement now comment order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpLocalCommentTime > 0 && globalServerTime > 0 && tmpServerIdArrangement >= 0 && tmpArrangementTime > 0 && tmpBlockId.length() > 0) {
                                // set upload time on smartphone for commeent
                                tmpUploadTime = System.currentTimeMillis();
                               // insert new comment in DB
                                myDb.insertRowOurArrangementComment(tmpCommentText, tmpAuthorName,  globalServerTime, tmpUploadTime, tmpLocalCommentTime, tmpBlockId, true, tmpArrangementTime, 4, tmpServerIdArrangement, 1);

                                // refresh activity our arrangement and fragment now comment
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementNowComment", "1");
                                returnMap.put("OurArrangementNowCommentMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpLocalCommentTime > 0 && globalServerTime > 0 && tmpServerIdArrangement >= 0 && tmpArrangementTime > 0 && tmpBlockId.length() > 0) {
                                // now comment order -> update

                                // set upload time on smartphone for commeent
                                tmpUploadTime = System.currentTimeMillis();

                                // insert new comment in DB
                                myDb.insertRowOurArrangementComment(tmpCommentText, tmpAuthorName, globalServerTime, tmpUploadTime, tmpLocalCommentTime, tmpBlockId, true, tmpArrangementTime, 4, tmpServerIdArrangement, 1);

                                // refresh activity ourarrangement and fragment now comment
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementNowComment", "1");
                                returnMap.put("OurArrangementNowCommentMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) { // delete all comments; needed by init process

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
                }

                // look for end tag of ourarrangement sketch
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_Sketch)) {
                        // check all data for arrangement sketch correct?
                        if (!error) {
                            // our arrangement sketch order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpArrangementText.length() > 0 && tmpAuthorName.length() > 0 && tmpSketchTime > 0 && tmpServerId > 0 && tmpBlockId.length() > 0 && tmpChangeTo.length() > 0) {

                                // insert new sketch arrangement in DB
                                myDb.insertRowOurArrangement(tmpArrangementText, tmpAuthorName, 0, true, tmpSketchCurrent, tmpSketchTime, 4, tmpServerId, tmpBlockId, tmpChangeTo);

                                // write current date of sketch arrangements to pref
                                prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, tmpSketchTime);
                                // write block id of sketch arrangements to prefs
                                prefsEditor.putString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, tmpBlockId);
                                // reset sketch comment counter
                                prefsEditor.putInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0);
                                // reset time count comments since
                                prefsEditor.putLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, System.currentTimeMillis());
                                // signal sketch arragenemt is updated
                                prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsSignalSketchArrangementUpdate, true);

                                prefsEditor.apply();

                                // refresh activity ourarrangement and fragement sketch
                                returnMap.put("OurArrangement", "1");
                                returnMap.put("OurArrangementSketch", "1");
                                returnMap.put("OurArrangementSketchMainActivityUpdateView", "1");

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
                                returnMap.put("OurArrangementSketchMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) {

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

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our arrangement sketch comment tag
        Boolean error = false;

        // tmp data for database insert
        String tmpCommentText = "";
        int tmpResultQuestionA = 0;
        int tmpResultQuestionB = 0;
        int tmpResultQuestionC = 0;
        String tmpAuthorName = "";
        Long tmpCommentLocalTime = 0L;
        Long tmpUploadTime = 0L;
        Long tmpArrangementTime = 0L;
        String tmpOrder = "";
        String tmpBlockId = "";
        int tmpServerIdArrangement = 0;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New)  && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update)  && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
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
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_CommentLocaleTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get commentTime text
                                if (xpp.getText().trim().length() > 0) { // check if commentLocalTime from xml > 0
                                    tmpCommentLocalTime = Long.valueOf(xpp.getText().trim())* 1000; // make Long from xml-text in milliseconds!!!!!
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
                                    tmpArrangementTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
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
                }

                // look for end tag of ourarrangement sketch comment
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment)) {
                        // check all data for sketch arrangement comment correct?
                        if (!error) {

                            // our arrangement sketch comment order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentLocalTime > 0 && globalServerTime > 0 && tmpArrangementTime > 0 && tmpResultQuestionA >= 0 && tmpResultQuestionB >= 0 && tmpResultQuestionC >= 0 && tmpCommentLocalTime > 0 && tmpServerIdArrangement >= 0 && tmpBlockId.length() > 0) {

                                // set upload time on smartphone for commeent; value from server is not needed
                                tmpUploadTime = System.currentTimeMillis();

                                // insert new comment in DB
                                myDb.insertRowOurArrangementSketchComment(tmpCommentText, tmpResultQuestionA, tmpResultQuestionB, tmpResultQuestionC, tmpAuthorName, globalServerTime, tmpCommentLocalTime, tmpUploadTime, tmpBlockId, true, tmpArrangementTime, 4, tmpServerIdArrangement, 1);

                                // refresh activity ourarrangement and fragment sketch comment
                                returnMap.put("OurArrangement","1");
                                returnMap.put("OurArrangementSketchComment","1");
                                returnMap.put("OurArrangementSketchCommentMainActivityUpdateView", "1");

                            }
                            else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && tmpCommentLocalTime > 0 && globalServerTime > 0 && tmpArrangementTime > 0 && tmpResultQuestionA >= 0 && tmpResultQuestionB >= 0 && tmpResultQuestionC >= 0 && tmpCommentLocalTime > 0 && tmpServerIdArrangement >= 0 && tmpBlockId.length() > 0) {
                                // our arrangement sketch comment order -> update?

                                // set upload time on smartphone for comment; value from server is not needed
                                tmpUploadTime = System.currentTimeMillis();

                                // insert new comment in DB
                                myDb.insertRowOurArrangementSketchComment(tmpCommentText, tmpResultQuestionA, tmpResultQuestionB, tmpResultQuestionC, tmpAuthorName, globalServerTime, tmpCommentLocalTime, tmpUploadTime, tmpBlockId, true, tmpArrangementTime, 4, tmpServerIdArrangement, 1);

                                // refresh activity ourarrangement and fragment sketch comment
                                returnMap.put("OurArrangement","1");
                                returnMap.put("OurArrangementSketchComment","1");
                                returnMap.put("OurArrangementSketchCommentMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) { // delete all comments for sketch arrangements; needed by init process

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

        // TODO: Implematation, when needed!!!!

    }


    private void readOurArrangementTag_Settings() {

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
        Long tmpCommentCountCommentSinceTime = 0L;
        int tmpCommentShare = 0; //0-> not sharing; 1-> sharing

        // arragnement sketch comment settings
        int tmpSketchCommentMaxComment = 0;
        int tmpSketchCommentMaxLetters = 0;
        int tmpSketchCommentDelaytime = 0;
        Long tmpSketchCommentCountCommentSinceTime = 0L;
        int tmpSketchCommentShare = 0; //0-> not sharing; 1-> sharing

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
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
                                    tmpCommentCountCommentSinceTime = Long.valueOf(xpp.getText().trim()) * 1000; // make mills from seconds
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_CommentShare:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get share value; 0-> not sharing; 1-> sharing
                                if (xpp.getText().trim().length() > 0) { // check if share value from xml > 0
                                    tmpCommentShare = Integer.valueOf(xpp.getText().trim());
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
                                    tmpSketchCommentCountCommentSinceTime = Long.valueOf(xpp.getText().trim()) * 1000; // make mills from seconds
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurArrangement_Settings_SketchCommentShare:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get share value; 0-> not sharing; 1-> sharing
                                if (xpp.getText().trim().length() > 0) { // check if share value from xml > 0
                                    tmpSketchCommentShare = Integer.valueOf(xpp.getText().trim());
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
                }

                // look for end tag of ourarrangement settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurArrangement_Settings)) {

                        // check all data for arrangement settings correct?
                        if (!error) {
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) ) { // our arrangement settings order -> delete?

                                // refresh activity ourarrangement because settings have change
                                returnMap.put("OurArrangement","1");
                                returnMap.put("OurArrangementSettings","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // our arrangement settings order -> update?

                                // update main view, only when our arrangement was off and turn to on!
                                if (!prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false) && tmpArrangementOnOff || prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false) && !tmpArrangementOnOff) {
                                    returnMap.put("OurArrangementSettingsMainActivityUpdateView", "1");
                                }

                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, tmpArrangementOnOff); // turn function our arrangement on/off
                                prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, tmpArrangementSketchOnOff); // turn function our arrangement sketch on/off
                                prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowOldArrangement, tmpArrangementOldOnOff); // turn function our arrangement old on/off
                                prefsEditor.apply();

                                // update evaluation of arrangements?
                                if (tmpArrangementEvaluationOnOff && tmpEvaluatePauseTime > 0 && tmpEvaluateActiveTime > 0 && tmpEvaluateStartDate > 0 && tmpEvaluateEndDate > 0) {

                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, tmpArrangementEvaluationOnOff); // turn function on
                                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds,0) != tmpEvaluatePauseTime || prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds,0) != tmpEvaluateActiveTime || prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills,0) != tmpEvaluateStartDate || prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills,0) != tmpEvaluateEndDate) {
                                        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, tmpEvaluatePauseTime);
                                        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, tmpEvaluateActiveTime);
                                        prefsEditor.putLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, tmpEvaluateStartDate);
                                        prefsEditor.putLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, tmpEvaluateEndDate);
                                        prefsEditor.putLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, System.currentTimeMillis()); // evaluation settings have change -> set new start point for evaluation
                                        prefsEditor.apply();
                                    }
                                    // something change in evaluation process
                                    returnMap.put("OurArrangementSettingsEvaluationProcess","1");
                                }
                                else { // turn function arrangement evaluation off
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, tmpArrangementEvaluationOnOff); // turn function off
                                    prefsEditor.apply();
                                    // something change in evaluation process
                                    returnMap.put("OurArrangementSettingsEvaluationProcess","1");

                                }

                                // update comment max/count of arrangements?
                                if (globalServerTime > 0 && tmpArrangementCommentOnOff && tmpCommentMaxComment > 0 && tmpCommentMaxLetters > 0 && tmpCommentDelaytime >= 0 && tmpCommentCountCommentSinceTime > 0 && tmpCommentShare >= 0) {

                                    // set new share value to prefs and set returnMap
                                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, 0) != tmpCommentShare ) {
                                        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, tmpCommentShare); // write new share value to prefs
                                        prefsEditor.putLong(ConstansClassOurArrangement.namePrefsArrangementCommentShareChangeTime, globalServerTime);

                                        if (tmpCommentShare == 1) { // sharing is enable; 1-> sharing comments; 0-> not sharing
                                            returnMap.put("OurArrangementSettingsCommentShareEnable","1");
                                        }
                                        else {
                                            returnMap.put("OurArrangementSettingsCommentShareDisable","1");
                                        }
                                    }

                                    // check if new since time greater then old one, reset count comments and set new since time
                                    if (tmpCommentCountCommentSinceTime > prefs.getLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, 0)) {

                                        prefsEditor.putLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, tmpCommentCountCommentSinceTime); // write new since time to prefs
                                        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0); // reset count comments to 0

                                        returnMap.put("OurArrangementSettingsCommentCountComment", "1");
                                    }

                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, tmpArrangementCommentOnOff); // turn function on
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, tmpCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentMaxLetters, tmpCommentMaxLetters);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, tmpCommentDelaytime);
                                    prefsEditor.apply();

                                    // something change in arrangement comment process
                                    returnMap.put("OurArrangementSettingsCommentProcess","1");
                                }
                                else { // turn function arrangement comment off
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, tmpArrangementCommentOnOff); // turn function on
                                    prefsEditor.apply();

                                    // something change in arrangement comment process
                                    returnMap.put("OurArrangementSettingsCommentProcess","1");
                                }

                                // update sketch comment max/count of sketch arrangements?
                                if (tmpArrangementSketchCommentOnOff && tmpSketchCommentMaxComment > 0 && tmpSketchCommentMaxLetters > 0 && tmpSketchCommentDelaytime >= 0 && tmpSketchCommentCountCommentSinceTime > 0 && tmpSketchCommentShare >= 0) {
                                    // write data to prefs

                                    // set new share value for sketch comment to prefs and set returnMap
                                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShare, 0) != tmpSketchCommentShare ) {
                                        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShare, tmpSketchCommentShare); // write new share value to prefs
                                        prefsEditor.putLong(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShareChangeTime, System.currentTimeMillis());

                                        if (tmpSketchCommentShare == 1) { // sharing is enable; 1-> sharing sketch comments; 0-> not sharing
                                            returnMap.put("OurArrangementSettingsSketchCommentShareEnable","1");
                                        }
                                        else {
                                            returnMap.put("OurArrangementSettingsSketchCommentShareDisable","1");
                                        }
                                    }
                                    // check if new since time greater then old one, reset count comments and set new since time
                                    if (tmpSketchCommentCountCommentSinceTime > prefs.getLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, 0)) {

                                        prefsEditor.putLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, tmpSketchCommentCountCommentSinceTime); // write new since time to prefs
                                        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0); // reset count comments to 0

                                        returnMap.put("OurArrangementSettingsSketchCommentCountComment", "1");
                                    }

                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, tmpArrangementSketchCommentOnOff); // turn function on
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,tmpSketchCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime,tmpSketchCommentDelaytime);
                                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsMaxSketchCommentLetters,tmpSketchCommentMaxLetters);
                                    prefsEditor.apply();

                                    // something change in sketch arrangement comment process
                                    returnMap.put("OurArrangementSettingsSketchCommentProcess","1");
                                }
                                else { // turn function arrangement sketch comment off
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, tmpArrangementSketchCommentOnOff); // turn function on
                                    prefsEditor.apply();
                                    // something change in sketch arragement comment process
                                    returnMap.put("OurArrangementSettingsSketchCommentProcess","1");
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
                // look for end tag of ourgoals
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals)) {
                        parseAnymore = false;
                    }
                }

                if (eventType == XmlPullParser.START_TAG) {
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
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
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
        String tmpChangeTo = "";
        String tmpBlockId = "";
        int tmpServerId = 0;


        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
                     switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
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
                                    tmpGoalTime = Long.valueOf(xpp.getText().trim()) * 1000; // make milliseconds from seconds
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
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if jointlyDebetable from xml > 0
                                    if (xpp.getText().trim().equals("jointly")) { // goal is a jointly goal?
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow_ChangeTo:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get ChangeTo text
                                if (xpp.getText().trim().length() > 0) { // check if ChangeTo from xml > 0
                                    tmpChangeTo = xpp.getText().trim();
                               } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow_BlockId:
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow_ServerId:
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
                }

                // look for end tag of ourgoals jointly now
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals_JointlyNow)) {
                        // check all data for jointly goal now correct?
                        if (!error) {
                            // our goal order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpGoalText.length() > 0 && tmpAuthorName.length() > 0 && tmpGoalTime > 0 && tmpServerId > 0 && tmpBlockId.length() > 0 && tmpChangeTo.length() > 0) {

                                // insert new jointly goals in DB
                                myDb.insertRowOurGoals(tmpGoalText, tmpAuthorName, tmpGoalTime, true, tmpJointlyDebetable, 0, 4, tmpServerId, tmpBlockId, tmpChangeTo);

                                // write current date of jointly goals to pref
                                prefsEditor.putLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, tmpGoalTime);
                                // write block id of jointly goals to prefs
                                prefsEditor.putString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, tmpBlockId);
                                // reset jointly comment counter
                                prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0);
                                // reset time count jointly comments since
                                prefsEditor.putLong(ConstansClassOurGoals.namePrefsJointlyCommentTimeSinceInMills, System.currentTimeMillis());
                                // signal jointly goals are updated
                                prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsSignalJointlyGoalsUpdate, true);

                                prefsEditor.apply();

                                // refresh activity ourgoals and fragement jointly goals
                                returnMap.put("OurGoals", "1");
                                returnMap.put("OurGoalsJointlyNow", "1");
                                returnMap.put("OurGoalsJointlyMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpServerId > 0) { // our goal order -> delete entry?

                                // delete goal in DB
                                myDb.deleteRowOurGoals(tmpServerId);

                                // refresh activity ourgoals and fragement jointly goal now
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsJointlyNow","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpServerId > 0 && tmpGoalText.length() > 0 && tmpAuthorName.length() > 0 && tmpGoalTime > 0 && tmpBlockId.length() > 0) { // our goal order -> update entry?

                                // update arrangement in DB
                                myDb.updateRowOurGoals(tmpGoalText, tmpAuthorName, tmpGoalTime, true, tmpJointlyDebetable, 0, 4, tmpServerId, tmpBlockId);

                                // refresh activity ourgoals and fragement jointly goal now
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsJointlyNow","1");
                                returnMap.put("OurGoalsJointlyMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) {

                                // delete all jointly goals with the blockId
                                myDb.deleteAllRowsOurGoals(tmpBlockId, false); // false -> all jointly goals; true -> all debetable goals

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

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our goals jointly comment tag
        Boolean error = false;

        // tmp data for database insert
        String tmpCommentText = "";
        String tmpAuthorName = "";
        Long tmpCommentLocalTime = 0L;
        String tmpBlockId = "";
        Long tmpGoalTime = 0L;
        Long tmpUploadTime = 0L;
        String tmpOrder = "";
        int tmpServerIdGoal = 0;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_CommentLocaleTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get commentTime text
                                if (xpp.getText().trim().length() > 0) { // check if commentLocaleTime from xml > 0
                                    tmpCommentLocalTime = Long.valueOf(xpp.getText().trim())* 1000; // make Long from xml-text in milliseconds!!!!!
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
                                    tmpGoalTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }

                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_BlockId:
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_ServerGoalId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get server id goal text
                                if (xpp.getText().trim().length() > 0) { // check if server id goal from xml > 0
                                    tmpServerIdGoal = Integer.valueOf(xpp.getText().trim());
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
                }

                // look for end tag of our goals jointly comment
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment)) {

                        // check all data for arrangement now correct?
                        if (!error) {

                            // our goals jointly comment order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && globalServerTime > 0 && tmpCommentLocalTime > 0 && tmpServerIdGoal >= 0 && tmpGoalTime > 0 && tmpBlockId.length() > 0) {

                                // set upload time on smartphone for jointly comment
                                tmpUploadTime = System.currentTimeMillis();

                                // insert new comment in DB
                                myDb.insertRowOurGoalJointlyGoalComment (tmpCommentText, tmpAuthorName, globalServerTime, tmpCommentLocalTime, tmpUploadTime, tmpBlockId, true, tmpGoalTime, 4, tmpServerIdGoal, 1);

                                // refresh activity ourgoals and fragment jointly comment
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsJointlyComment","1");
                                returnMap.put("OurGoalsJointlyCommentMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && globalServerTime > 0 && tmpCommentLocalTime > 0 && tmpServerIdGoal >= 0 && tmpGoalTime > 0 && tmpBlockId.length() > 0) { // our goals jointly comment order -> update entry?

                                // set upload time on smartphone for jointly comment
                                tmpUploadTime = System.currentTimeMillis();

                                // insert new comment in DB
                                myDb.insertRowOurGoalJointlyGoalComment (tmpCommentText, tmpAuthorName, globalServerTime, tmpCommentLocalTime, tmpUploadTime, tmpBlockId, true, tmpGoalTime, 4, tmpServerIdGoal, 1);

                                // refresh activity ourgoals and fragment jointly comment
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsJointlyComment","1");
                                returnMap.put("OurGoalsJointlyCommentMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) { // delete all comments; needed by init process

                                // delete all comments for all current jointly goals with the blockId
                                myDb.deleteAllRowsOurJointlyGoalsComment (tmpBlockId);
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

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our goals debetable comment tag
        Boolean error = false;

        // tmp data for database insert
        String tmpCommentText = "";
        int tmpResultQuestionA = 0;
        int tmpResultQuestionB = 0;
        int tmpResultQuestionC = 0;
        String tmpAuthorName = "";
        Long tmpCommentLocaleTime = 0L;
        int tmpGoalId = 0;
        Long tmpGoalTime = 0L;
        String tmpOrder = "";
        String tmpBlockId = "";
        int tmpServerIdGoal = 0;

        Long tmpUploadTime = 0L;


        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
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
                                    tmpResultQuestionA = Integer.valueOf(xpp.getText().trim()); // make int from xml-text
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
                                    tmpResultQuestionB = Integer.valueOf(xpp.getText().trim()); // make int from xml-text
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
                                    tmpResultQuestionC = Integer.valueOf(xpp.getText().trim()); // make int from xml-text
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_CommentLocaleTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get commentTime text
                                if (xpp.getText().trim().length() > 0) { // check if commentLocaleTime from xml > 0
                                    tmpCommentLocaleTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
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
                                    tmpGoalTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_BlockId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get BlockId text
                                if (xpp.getText().trim().length() > 0) { // check if Block id from xml > 0
                                    tmpBlockId = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ServerIdGoal:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get server id arrangement text
                                if (xpp.getText().trim().length() > 0) { // check if server id arrangement from xml > 0
                                    tmpServerIdGoal = Integer.valueOf(xpp.getText().trim());
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
                }

                // look for end tag of our goals debetable comment
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment)) {

                        // check all data for arrangement debetable comment correct?
                        if (!error) {
                            // our goals debetable comment order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && globalServerTime > 0 && tmpCommentLocaleTime > 0 && tmpGoalTime > 0 && tmpResultQuestionA >= 0 && tmpResultQuestionB >= 0 && tmpResultQuestionC >= 0 && tmpServerIdGoal >= 0 && tmpBlockId.length() > 0) {

                                // set upload time on smartphone for comment; value from server is not needed
                                tmpUploadTime = System.currentTimeMillis();

                                 // insert new comment in DB
                                myDb.insertRowOurGoalsDebetableGoalsComment(tmpCommentText, tmpResultQuestionA, tmpResultQuestionB, tmpResultQuestionC, tmpAuthorName, globalServerTime, tmpCommentLocaleTime, tmpUploadTime, tmpBlockId, true, tmpGoalTime, 4, tmpServerIdGoal, 1);

                                // refresh activity ourgoals and fragment debetable comment
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableComment","1");
                                returnMap.put ("OurGoalsDebetableCommentMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpCommentText.length() > 0 && tmpAuthorName.length() > 0 && globalServerTime > 0 && tmpCommentLocaleTime > 0 && tmpGoalTime > 0 && tmpResultQuestionA >= 0 && tmpResultQuestionB >= 0 && tmpResultQuestionC >= 0 && tmpServerIdGoal >= 0 && tmpBlockId.length() > 0) {

                                // set upload time on smartphone for comment; value from server is not needed
                                tmpUploadTime = System.currentTimeMillis();

                                // insert new comment in DB
                                myDb.insertRowOurGoalsDebetableGoalsComment(tmpCommentText, tmpResultQuestionA, tmpResultQuestionB, tmpResultQuestionC, tmpAuthorName, globalServerTime, tmpCommentLocaleTime, tmpUploadTime, tmpBlockId, true, tmpGoalTime, 4, tmpServerIdGoal, 1);

                                // refresh activity ourgoals and fragment debetable comment
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableComment","1");
                                returnMap.put ("OurGoalsDebetableCommentMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) { // delete all comments for debetable goals; needed by init process

                                // delete all comments for all current sketch arrangements with the blockId
                                myDb.deleteAllRowsOurGoalsDebetableComment (tmpBlockId);

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
        String tmpChangeTo = "";
        String tmpBlockId = "";
        int tmpServerId = 0;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
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
                                    tmpDebetableGoalTime = Long.valueOf(xpp.getText().trim())* 1000; // make Long from xml-text in milliseconds!!!!!
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
                            if (eventType == XmlPullParser.TEXT) {
                                if (xpp.getText().trim().length() > 0) { // check if jointlyDebetable from xml > 0
                                    if (xpp.getText().trim().equals("debetable")) { // goal is a debetable goal?
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_ChangeTo:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get ChangeTo text
                                if (xpp.getText().trim().length() > 0) { // check if ChangeTo from xml > 0
                                    tmpChangeTo = xpp.getText().trim();
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_BlockId:
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
                        case ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow_ServerId:
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
                }

                // look for end tag of ourgoals debetable now
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals_DebetableNow)) {
                        // check all data for debetable goal now correct?
                        if (!error) {

                            // our goal order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpDebetableGoalText.length() > 0 && tmpDebetableAuthorName.length() > 0 && tmpDebetableGoalTime > 0 && tmpServerId > 0 && tmpBlockId.length() > 0 && tmpChangeTo.length() > 0) {

                                // insert new debetable goal in DB
                                myDb.insertRowOurGoals(tmpDebetableGoalText, tmpDebetableAuthorName, 0, true, tmpJointlyDebetable, tmpDebetableGoalTime, 4, tmpServerId, tmpBlockId, tmpChangeTo);

                                // write current date of debetable goals to pref
                                prefsEditor.putLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, tmpDebetableGoalTime);
                                // write block id of debetable goals to prefs
                                prefsEditor.putString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfDebetableGoals, tmpBlockId);
                                // reset debetable comment counter
                                prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0);
                                // reset time count comments since
                                prefsEditor.putLong(ConstansClassOurGoals.namePrefsDebetableCommentTimeSinceInMills, System.currentTimeMillis());
                                // signal debetable goals are updated
                                prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsSignalDebetableGoalsUpdate, true);

                                prefsEditor.apply();

                                // refresh activity ourgoals and fragement debetable goal now
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableNow","1");
                                returnMap.put ("OurGoalsDebetableMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && tmpServerId > 0) { // our goal order -> delete entry?

                                // delete goal in DB
                                myDb.deleteRowOurGoals(tmpServerId);

                                // refresh activity ourgoals and fragement debetable goal now
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableNow","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpServerId > 0 && tmpBlockId.length() > 0 && tmpDebetableGoalText.length() > 0 && tmpDebetableAuthorName.length() > 0 && tmpDebetableGoalTime > 0) { // our goal order -> update entry?

                                // update goal in DB
                                myDb.updateRowOurGoals(tmpDebetableGoalText, tmpDebetableAuthorName, 0, true, tmpJointlyDebetable, tmpDebetableGoalTime, 4,  tmpServerId, tmpBlockId);

                                // refresh activity ourgoals and fragement jointly goal now
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsDebetableNow","1");
                                returnMap.put ("OurGoalsDebetableMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) && tmpBlockId.length() > 0) {

                                // delete all jointly goals with the blockId
                                myDb.deleteAllRowsOurGoals(tmpBlockId, true); // false -> all jointly goals; true -> all debetable goals

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
        Long tmpJointlyCommentCountCommentSinceTime = 0L;
        int tmpCommentShare = 0;
        int tmpCommentDelaytime = 0;

        int tmpDebetableCommentMaxComment = 0;
        int tmpDebetableCommentMaxLetters = 0;
        Long tmpDebetableCommentCountCommentSinceTime = 0L;
        int tmpDebetableCommentShare = 0;
        int tmpDebetableCommentDelaytime = 0;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
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
                                    tmpJointlyEvaluatePauseTime = Integer.valueOf(xpp.getText().trim()) * 3600; // make seconds form hours;
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
                                    tmpJointlyEvaluateActiveTime = Integer.valueOf(xpp.getText().trim())* 3600; // make seconds form hours
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
                                    tmpJointlyEvaluateStartDate = Long.valueOf(xpp.getText().trim())* 1000; // make mills from seconds
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
                                    tmpJointlyEvaluateEndDate = Long.valueOf(xpp.getText().trim())* 1000; // make mills from seconds
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
                                    tmpJointlyCommentCountCommentSinceTime = Long.valueOf(xpp.getText().trim()) * 1000; // make mills from seconds;
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_JointlyCommentDelaytime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get delaytime
                                if (xpp.getText().trim().length() > 0) { // check if delaytime from xml > 0
                                    tmpCommentDelaytime = Integer.valueOf(xpp.getText().trim());
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_JointlyCommentShare:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get share value; 0-> not sharing; 1-> sharing
                                if (xpp.getText().trim().length() > 0) { // check if share value from xml > 0
                                    tmpCommentShare = Integer.valueOf(xpp.getText().trim());
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
                                    tmpDebetableCommentCountCommentSinceTime = Long.valueOf(xpp.getText().trim())* 1000; // make mills from seconds
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_DebetableCommentDelaytime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get delaytime
                                if (xpp.getText().trim().length() > 0) { // check if delaytime from xml > 0
                                    tmpDebetableCommentDelaytime = Integer.valueOf(xpp.getText().trim());
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForOurGoals_Settings_DebetableCommentShare:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get share value; 0-> not sharing; 1-> sharing
                                if (xpp.getText().trim().length() > 0) { // check if share value from xml > 0
                                    tmpDebetableCommentShare = Integer.valueOf(xpp.getText().trim());
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
                }

                // look for end tag of ourarrangement settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForOurGoals_Settings)) {
                        // check all data for goals settings correct?
                        if (!error) {
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) ) { // our goals settings order -> delete?

                                // refresh activity ourgoals because settings have change
                                returnMap.put ("OurGoals","1");
                                returnMap.put ("OurGoalsSettings","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // our goals settings order -> update?

                                // update main view, only when our goals was off and turn to on or vice versa!
                                if (!prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false) && tmpGoalsOnOff || prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false) && !tmpGoalsOnOff) {
                                    returnMap.put("OurGoalsSettingsMainActivityUpdateView", "1");
                                }

                                // write data to prefs
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, tmpGoalsOnOff); // turn function our goals on/off
                                prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkDebetableGoals, tmpGoalsDebetableOnOff); // turn function our goals debetable on/off
                                prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkOldGoals, tmpGoalsOldOnOff); // turn function our goals old on/off
                                prefsEditor.apply();

                                // update evaluation of jointly goals?
                                if (tmpGoalsEvaluationOnOff && tmpJointlyEvaluatePauseTime > 0 && tmpJointlyEvaluateActiveTime > 0 && tmpJointlyEvaluateStartDate > 0 && tmpJointlyEvaluateEndDate > 0) {

                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, tmpGoalsEvaluationOnOff); // turn function on
                                    if (prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds,0) != tmpJointlyEvaluatePauseTime || prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds,0) !=  tmpJointlyEvaluateActiveTime || prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills,0) != tmpJointlyEvaluateStartDate || prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills,0) != tmpJointlyEvaluateEndDate) {
                                        prefsEditor.putInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, tmpJointlyEvaluatePauseTime);
                                        prefsEditor.putInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, tmpJointlyEvaluateActiveTime);
                                        prefsEditor.putLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, tmpJointlyEvaluateStartDate);
                                        prefsEditor.putLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, tmpJointlyEvaluateEndDate);
                                        prefsEditor.putLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, System.currentTimeMillis()); // evaluation settings have change -> set new start point for evaluation
                                        prefsEditor.apply();
                                    }
                                    // something change in evaluation process
                                    returnMap.put ("OurGoalsSettingsEvaluationProcess","1");
                                }
                                else { // turn function our goals evaluation off
                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, tmpGoalsEvaluationOnOff); // turn function off
                                    prefsEditor.apply();
                                    // something change in evaluation process
                                    returnMap.put ("OurGoalsSettingsEvaluationProcess","1");
                                }

                                // update comment max/count of jointly goals?
                                if (tmpGoalsJointlyCommentOnOff && tmpJointlyCommentMaxComment > 0 && tmpJointlyCommentMaxLetters > 0 && tmpCommentDelaytime >= 0 && tmpJointlyCommentCountCommentSinceTime > 0 && tmpCommentShare >= 0) {
                                    // set new share value to prefs and set returnMap
                                    if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentShare, 0) != tmpCommentShare ) {
                                        prefsEditor.putInt(ConstansClassOurGoals.namePrefsJointlyCommentShare, tmpCommentShare); // write new share value to prefs
                                        prefsEditor.putLong(ConstansClassOurGoals.namePrefsJointlyCommentShareChangeTime, System.currentTimeMillis());

                                        if (tmpCommentShare == 1) { // sharing is enable; 1-> sharing comments; 0-> not sharing
                                            returnMap.put("OurGoalsSettingsCommentShareEnable","1");
                                        }
                                        else {
                                            returnMap.put("OurGoalsSettingsCommentShareDisable","1");
                                        }
                                    }

                                    // check if new since time greater then old one, reset count comments and set new since time
                                    if (tmpJointlyCommentCountCommentSinceTime > prefs.getLong(ConstansClassOurGoals.namePrefsJointlyCommentTimeSinceInMills, 0)) {

                                        prefsEditor.putLong(ConstansClassOurGoals.namePrefsJointlyCommentTimeSinceInMills, tmpJointlyCommentCountCommentSinceTime); // write new since time to prefs
                                        prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0); // reset count comments to 0

                                        returnMap.put("OurGoalsSettingsCommentCountComment", "1");
                                    }

                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentJointlyGoals, tmpGoalsJointlyCommentOnOff); // turn function on
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, tmpJointlyCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyLetters, tmpJointlyCommentMaxLetters);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, tmpCommentDelaytime);
                                    prefsEditor.apply();
                                    // something change in jointly goals comment process
                                    returnMap.put ("OurGoalsSettingsCommentProcess","1");
                                }
                                else { // turn function our goals jointly comment off
                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentJointlyGoals, tmpGoalsJointlyCommentOnOff); // turn function off
                                    prefsEditor.apply();
                                    // something change in jointly goals comment process
                                    returnMap.put ("OurGoalsSettingsCommentProcess","1");
                                }

                                // update debetable comment max/count of debetable goals?
                                if (tmpGoalsDebetableCommentOnOff && tmpDebetableCommentMaxComment > 0 && tmpDebetableCommentMaxLetters > 0 && tmpDebetableCommentDelaytime >= 0 && tmpDebetableCommentCountCommentSinceTime > 0 && tmpDebetableCommentShare >= 0) {

                                    // set new share value for debetable comment to prefs and set returnMap
                                    if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentShare, 0) != tmpDebetableCommentShare ) {
                                        prefsEditor.putInt(ConstansClassOurGoals.namePrefsDebetableCommentShare, tmpDebetableCommentShare); // write new share value to prefs
                                        prefsEditor.putLong(ConstansClassOurGoals.namePrefsDebetableCommentShareChangeTime, System.currentTimeMillis());

                                        if (tmpDebetableCommentShare == 1) { // sharing is enable; 1-> sharing debetable comments; 0-> not sharing
                                            returnMap.put("OurGoalsSettingsDebetableCommentShareEnable","1");
                                        }
                                        else {
                                            returnMap.put("OurGoalsSettingsDebetableCommentShareDisable","1");
                                        }
                                    }

                                    // check if new since time greater then old one, reset count debetable comments and set new since time
                                    if (tmpDebetableCommentCountCommentSinceTime > prefs.getLong(ConstansClassOurGoals.namePrefsDebetableCommentTimeSinceInMills, 0)) {

                                        prefsEditor.putLong(ConstansClassOurGoals.namePrefsDebetableCommentTimeSinceInMills, tmpDebetableCommentCountCommentSinceTime); // write new since time to prefs
                                        prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0); // reset count comments to 0

                                        returnMap.put("OurGoalsSettingsDebetableCommentCountComment", "1");
                                    }

                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentDebetableGoals, tmpGoalsDebetableCommentOnOff); // turn function on
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment,tmpDebetableCommentMaxComment);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableLetters,tmpDebetableCommentMaxLetters);
                                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime,tmpDebetableCommentDelaytime);
                                    prefsEditor.apply();
                                    // something change in debetable goals comment process
                                    returnMap.put ("OurGoalsSettingsDebetableCommentProcess","1");
                                }
                                else { // turn function our goals debetable comment off
                                    // write data to prefs
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentDebetableGoals, tmpGoalsDebetableCommentOnOff); // turn function off
                                    prefsEditor.apply();
                                    // something change in debetable goals comment process
                                    returnMap.put ("OurGoalsSettingsDebetableCommentProcess","1");
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

                // look for end tag of meeting
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMeeting)) {
                        parseAnymore = false;
                    }
                }

                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForMeeting_Settings:
                            readMeetingTag_Settings();
                            break;

                        case ConstansClassXmlParser.xmlNameForMeeting_And_Suggestions:
                            readMeetingTag_MeetingAndSuggestions();
                            break;
                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
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

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml meeting settings tag
        Boolean error = false;

        // tmp data for prefs
        Boolean tmpMeetingOnOff = false;
        Boolean tmpMeetingClientMakeSuggestionOnOff = false;
        Boolean tmpMeetingClientCanceleMeetingOnOff = false;
        Boolean tmpMeetingClientCommentSuggestionOnOff = false;

        String tmpOrder = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
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
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_ClientCancelMeeting_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch client cancele meeting turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpMeetingClientCanceleMeetingOnOff = true;}
                                    else {tmpMeetingClientCanceleMeetingOnOff = false;}
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_ClientMakeSuggestion_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch client make suggestion turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpMeetingClientMakeSuggestionOnOff = true;}
                                    else {tmpMeetingClientMakeSuggestionOnOff = false;}
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_ClientMakeSuggestionComment_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch client make suggestion turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {tmpMeetingClientCommentSuggestionOnOff = true;}
                                    else {tmpMeetingClientCommentSuggestionOnOff = false;}
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
                }

                // look for end tag of meeting settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMeeting_Settings)) {
                        // check all data for meeting settings correct?
                        if (!error) {
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) ) { // meting settings order -> delete?

                                // refresh activity meeting because settings have change
                                returnMap.put ("Meeting","1");
                                returnMap.put ("MeetingSettings","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All) ) { // meeting settings order -> delete all?

                                // delete all meeting suggestions in DB
                                myDb.deleteAllRowsMeetingAndSuggestion();

                                // refresh activity meeting because settings have change
                                returnMap.put ("Meeting","1");
                                returnMap.put ("MeetingSettings","1");


                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // meeting settings order -> update?

                                if (!prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Meeting, false) && tmpMeetingOnOff || prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Meeting, false) && !tmpMeetingOnOff) {
                                    returnMap.put("MeetingSettingMainActivityUpdateView", "1");
                                }

                                // write data meeting on off to prefs
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Meeting, tmpMeetingOnOff);

                                // write data meeting client suggestion on off to prefs
                                prefsEditor.putBoolean(ConstansClassMeeting.namePrefsMeeting_ClientSuggestion_OnOff, tmpMeetingClientMakeSuggestionOnOff);

                                // write data meeting client cancele meeting on off to prefs
                                prefsEditor.putBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCanceleMeeting_OnOff, tmpMeetingClientCanceleMeetingOnOff);

                                // write data meeting client comment suggestion on off to prefs
                                prefsEditor.putBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, tmpMeetingClientCommentSuggestionOnOff);

                                prefsEditor.apply();

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


    // read tag meeting or suggestions and push to database
    private void readMeetingTag_MeetingAndSuggestions() {

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml meeting and suggestions tag
        Boolean error = false;

        // tmp data for database insert
        String tmpOrder = "";

        Long tmpMeetingSuggestionDate1 = 0L;
        Long tmpMeetingSuggestionDate2 = 0L;
        Long tmpMeetingSuggestionDate3 = 0L;
        Long tmpMeetingSuggestionDate4 = 0L;
        Long tmpMeetingSuggestionDate5 = 0L;
        Long tmpMeetingSuggestionDate6 = 0L;

        int tmpMeetingPlace1 = 0;
        int tmpMeetingPlace2 = 0;
        int tmpMeetingPlace3 = 0;
        int tmpMeetingPlace4 = 0;
        int tmpMeetingPlace5 = 0;
        int tmpMeetingPlace6 = 0;

        String tmpMeetingSuggestionAuthorName = "";
        Long tmpMeetingSuggestionCreationTime = 0L;
        Long tmpMeetingSuggestionResponseTime = 0L;
        int tmpMeetingSuggestionKategorie = 0;
        String tmpMeetingSuggestionCoachHintText = "";
        Long tmpMeetingSuggestionDataServerId = 0L;
        Long tmpMeetingSuggestionCoachCanceleTime = 0L;
        String tmpMeetingSuggestionCoachCanceleAuthor = "";

        Long tmpClientSuggestionStartDate = 0L;
        Long tmpClientSuggestionEndDate = 0L;

        String tmpClientVoteAuthor = "";
        Long tmpClientVoteLocaleDate = 0L;

        String tmpMeetingFoundFromSuggestionAuthor = "";
        Long tmpMeetingFoundFromSuggestionDate = 0L;

        Long [] array_meetingTime = {0L,0L,0L,0L,0L,0L}; // array store meeting time -> parse to db
        int [] array_meetingPlace = {0,0,0,0,0,0}; // array store meeting place -> parse to db
        int [] array_meetingVote = {0,0,0,0,0,0}; // array store vote results -> parse to db, only needed for db -> comes not from server!!!!!!!!!!

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update)) {
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
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate1:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting/ suggestion date 1 text
                                if (xpp.getText().trim().length() > 0) { // check if meeting/ suggestion date 1 from xml > 0
                                    tmpMeetingSuggestionDate1 = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate2:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting/ suggestion date 2 text
                                if (xpp.getText().trim().length() >= 0) { // check if meeting/ suggestion date 2 from xml > 0
                                    tmpMeetingSuggestionDate2 = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate3:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting/ suggestion date 3 text
                                if (xpp.getText().trim().length() >= 0) { // check if meeting/ suggestion date 3 from xml > 0
                                    tmpMeetingSuggestionDate3 = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                             }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate4:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting/ suggestion date 4 text
                                if (xpp.getText().trim().length() >= 0) { // check if meeting/ suggestion date 4 from xml > 0
                                    tmpMeetingSuggestionDate4 = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate5:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting/ suggestion date 5 text
                                if (xpp.getText().trim().length() >= 0) { // check if meeting/ suggestion date 5 from xml > 0
                                    tmpMeetingSuggestionDate5 = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate6:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting/ suggestion date 6 text
                                if (xpp.getText().trim().length() >= 0) { // check if meeting/ suggestion date 6 from xml > 0
                                    tmpMeetingSuggestionDate6 = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace1:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting place 1
                                if (xpp.getText().trim().length() > 0) { // check if meeting place 1 from xml > 0
                                    tmpMeetingPlace1 = Integer.valueOf(xpp.getText().trim());
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace2:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting place 2
                                if (xpp.getText().trim().length() >= 0) { // check if meeting place 2 from xml > 0
                                    tmpMeetingPlace2 = Integer.valueOf(xpp.getText().trim());
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace3:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting place 3
                                if (xpp.getText().trim().length() >= 0) { // check if meeting place 3 from xml > 0
                                    tmpMeetingPlace3 = Integer.valueOf(xpp.getText().trim());
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace4:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting place 4
                                if (xpp.getText().trim().length() >= 0) { // check if meeting place 4 from xml > 0
                                    tmpMeetingPlace4 = Integer.valueOf(xpp.getText().trim());
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace5:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting place 5
                                if (xpp.getText().trim().length() >= 0) { // check if meeting place 5 from xml > 0
                                    tmpMeetingPlace5 = Integer.valueOf(xpp.getText().trim());
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace6:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting place 6
                                if (xpp.getText().trim().length() >= 0) { // check if meeting place 6 from xml > 0
                                    tmpMeetingPlace6 = Integer.valueOf(xpp.getText().trim());
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_AuthorName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting suggestion author name
                                if (xpp.getText().trim().length() > 0) { // check if meeting suggestion author name from xml > 0
                                    tmpMeetingSuggestionAuthorName = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_CreationTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting suggestion creation time
                                if (xpp.getText().trim().length() > 0) { // check if meeting suggestion creation time from xml > 0
                                    tmpMeetingSuggestionCreationTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ResponseTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting suggestion creation time
                                if (xpp.getText().trim().length() >= 0) { // check if meeting suggestion creation time from xml > 0
                                    tmpMeetingSuggestionResponseTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_Kategorie:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting suggestion kategorie
                                if (xpp.getText().trim().length() > 0) { // check if meeting suggestion kategorie time from xml > 0
                                    tmpMeetingSuggestionKategorie = Integer.valueOf(xpp.getText().trim());
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_CoachHintText:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting suggestion coach hint text
                                if (xpp.getText().trim().length() >= 0) { // check if meeting suggestion coach hint text from xml > 0
                                    tmpMeetingSuggestionCoachHintText = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_DataServerId:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting suggestion coach hint text
                                if (xpp.getText().trim().length() > 0) { // check if meeting suggestion coach hint text from xml > 0
                                    tmpMeetingSuggestionDataServerId = Long.valueOf(xpp.getText().trim());
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_CoachCanceleTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting suggestion coach cancele time
                                if (xpp.getText().trim().length() >= 0) { // check if meeting suggestion coach cancele time from xml > 0
                                    tmpMeetingSuggestionCoachCanceleTime = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Suggestion_CoachCanceleAuthor:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting suggestion coach cancele author text
                                if (xpp.getText().trim().length() >= 0) { // check if meeting suggestion coach cancele author from xml > 0
                                    tmpMeetingSuggestionCoachCanceleAuthor = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Meeting_FoundFromSuggestion_Date:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting found from suggestion date
                                if (xpp.getText().trim().length() >= 0) { // check if meeting found from suggestion date from xml > 0
                                    tmpMeetingFoundFromSuggestionDate = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_Meeting_FoundFromSuggestion_Author:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting found from suggestion author text
                                if (xpp.getText().trim().length() >= 0) { // check if meeting found from suggestion author from xml > 0
                                    tmpMeetingFoundFromSuggestionAuthor = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_SuggestionFromClient_StartDate:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting suggestion start date for client suggestion
                                if (xpp.getText().trim().length() >= 0) { // check if meeting suggestion start date for client suggestion from xml > 0
                                    tmpClientSuggestionStartDate = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMeeting_SuggestionFromClient_EndDate:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get meeting suggestion end date for client suggestion
                                if (xpp.getText().trim().length() >= 0) { // check if meeting suggestion end date for client suggestion from xml > 0
                                    tmpClientSuggestionEndDate = Long.valueOf(xpp.getText().trim()) * 1000; // make Long from xml-text in milliseconds!!!!!
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
                }

                // look for end tag of meeting suggestions
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMeeting_And_Suggestions)) {
                        // check all data for meeting suggestions correct?
                        if (!error) {
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpMeetingSuggestionKategorie > 0) {
                                // meeting or suggestion
                                switch (tmpMeetingSuggestionKategorie) { // check for meeting or suggestion
                                    case 1: // meeting dates
                                        if (tmpMeetingSuggestionDate1 > 0 && tmpMeetingPlace1 > 0 && tmpMeetingSuggestionAuthorName.length() > 0 && tmpMeetingSuggestionCreationTime > 0 && tmpMeetingSuggestionDataServerId > 0) {
                                            // check if meeting data is canceled or new meeting
                                            if (tmpMeetingSuggestionCoachCanceleTime > 0 && tmpMeetingSuggestionCoachCanceleAuthor.length() > 0) {

                                                int meetingStatus = 4; // comes from external
                                                int newMeeting = 1; // 1 = new meeting or suggestion

                                                int timerStatus = 1; // 1 = timer can not run
                                                String updateOrder = ""; // nothing to update

                                                // update row in db table
                                                myDb.updateMeetingCanceledByCoach(tmpMeetingSuggestionDataServerId, tmpMeetingSuggestionCoachCanceleTime, tmpMeetingSuggestionCoachCanceleAuthor, newMeeting, meetingStatus, timerStatus, updateOrder);

                                                returnMap.put("MeetingCanceledMeetingByCoach", "1");
                                                returnMap.put("Meeting", "1");
                                                returnMap.put("MeetingCancelMeetingMainActivityUpdateView", "1");
                                            } else {

                                                // init meeting parameters
                                                Long tmpUploadTime = System.currentTimeMillis();

                                                array_meetingTime[0] = tmpMeetingSuggestionDate1;
                                                array_meetingPlace[0] = tmpMeetingPlace1;

                                                array_meetingTime[1] = 0L;
                                                array_meetingTime[2] = 0L;
                                                array_meetingTime[3] = 0L;
                                                array_meetingTime[4] = 0L;
                                                array_meetingTime[5] = 0L;

                                                array_meetingPlace[1] = 0;
                                                array_meetingPlace[2] = 0;
                                                array_meetingPlace[3] = 0;
                                                array_meetingPlace[4] = 0;
                                                array_meetingPlace[5] = 0;

                                                array_meetingVote[0] = 0;
                                                array_meetingVote[1] = 0;
                                                array_meetingVote[2] = 0;
                                                array_meetingVote[3] = 0;
                                                array_meetingVote[4] = 0;
                                                array_meetingVote[5] = 0;

                                                tmpClientVoteAuthor = "";
                                                Long tmpClientVoteDate = 0L; // value comes from server -> server time
                                                tmpClientVoteLocaleDate = 0L;

                                                tmpMeetingFoundFromSuggestionAuthor = "";
                                                tmpMeetingFoundFromSuggestionDate = 0L;
                                                int tmpMeetingFoundFromSuggestion = 0; // 0=no meeting found from suggestion; 1=meeting found from suggestion
                                                tmpMeetingSuggestionResponseTime = 0L; // not needed -> its a meeting
                                                Long tmpMeetingSuggestionResponseStartTime = 0L; // value comes from server -> server time

                                                tmpMeetingSuggestionCoachCanceleTime = 0L;
                                                tmpMeetingSuggestionCoachCanceleAuthor = "";
                                                int tmpMeetingSuggestionCoachCancele = 0; // 0=not canceled; 1 = canceled
                                                String tmpClientSuggestionText = "";
                                                String tmpClientSuggestionAuthor = "";
                                                Long tmpClientSuggestionTime = 0L; // value comes from server -> server time
                                                Long tmpClientSuggestionLocaleTime = 0L;

                                                Long tmpMeetingSuggestionClientCanceleTime = 0L; // value comes from server -> server time
                                                Long tmpMeetingSuggestionClientCanceleLocaleTime = 0L;
                                                String tmpMeetingSuggestionClientCanceleAuthor = "";
                                                String tmpMeetingSuggestionClientCanceleText = "";
                                                int tmpMeetingSuggestionClientCancele = 0; // 0=not canceled; 1 = canceled
                                                String tmpClientCommentText = "";
                                                String tmpClientCommentAuthor = "";
                                                Long tmpClientCommentTime = 0L; // value comes from server -> server time
                                                Long tmpClientCommentLocaleTime = 0L;

                                                int meetingStatus = 4; // 0=ready to send, 1=meeting/suggestion send, 4=external message
                                                int newMeeting = 1; // 1 = new meeting or suggestion

                                                int timerStatus = 1; // this is a meeting -> no timer needed
                                                String updateOrder = "";

                                                // insert new data into db
                                                myDb.insertNewMeetingOrSuggestionDate(array_meetingTime, array_meetingPlace, array_meetingVote, tmpClientVoteAuthor, tmpClientVoteDate, tmpClientVoteLocaleDate, tmpMeetingSuggestionCreationTime, tmpMeetingSuggestionAuthorName, tmpMeetingSuggestionKategorie, tmpMeetingSuggestionResponseTime, tmpMeetingSuggestionResponseStartTime, tmpMeetingSuggestionCoachHintText, tmpMeetingSuggestionCoachCancele, tmpMeetingSuggestionCoachCanceleTime, tmpMeetingSuggestionCoachCanceleAuthor, tmpMeetingFoundFromSuggestionAuthor, tmpMeetingFoundFromSuggestionDate, tmpMeetingFoundFromSuggestion, tmpMeetingSuggestionDataServerId, tmpClientSuggestionText, tmpClientSuggestionAuthor, tmpClientSuggestionTime, tmpClientSuggestionLocaleTime, tmpClientSuggestionStartDate, tmpClientSuggestionEndDate, tmpClientCommentText, tmpClientCommentAuthor, tmpClientCommentTime, tmpClientCommentLocaleTime, tmpMeetingSuggestionClientCancele, tmpMeetingSuggestionClientCanceleTime, tmpMeetingSuggestionClientCanceleLocaleTime, tmpMeetingSuggestionClientCanceleAuthor, tmpMeetingSuggestionClientCanceleText, meetingStatus, tmpUploadTime, newMeeting, timerStatus, updateOrder);

                                                returnMap.put("MeetingNewMeeting", "1");
                                                returnMap.put("Meeting", "1");
                                                returnMap.put("MeetingNewMeetingMainActivityUpdateView", "1");
                                            }
                                        }
                                        break;
                                    case 2: // meeting suggestions
                                        if (tmpMeetingSuggestionCreationTime > 0 && tmpMeetingSuggestionAuthorName.length() > 0 && tmpMeetingSuggestionDataServerId > 0) {
                                            // check if suggestion data is canceled, new suggestion or suggestion from client
                                            if (tmpMeetingSuggestionCoachCanceleTime > 0 && tmpMeetingSuggestionCoachCanceleAuthor.length() > 0) {

                                                int meetingStatus = 4; // comes from external
                                                int newMeeting = 1; // 1 = new meeting or suggestion

                                                int timerStatus = 1; // 1 = timer can not run
                                                String updateOrder = ""; // nothing to update

                                                // update row in db table
                                                myDb.updateMeetingCanceledByCoach(tmpMeetingSuggestionDataServerId, tmpMeetingSuggestionCoachCanceleTime, tmpMeetingSuggestionCoachCanceleAuthor, newMeeting, meetingStatus, timerStatus, updateOrder);

                                                returnMap.put("MeetingCanceledSuggestionByCoach", "1");
                                                returnMap.put("Meeting", "1");
                                                returnMap.put("MeetingCancelSuggestionMainActivityUpdateView", "1");

                                            } else  if (tmpMeetingFoundFromSuggestionAuthor.length() > 0 && tmpMeetingFoundFromSuggestionDate > 0) {

                                                int meetingStatus = 4; // comes from external
                                                int newMeeting = 1; // 1 = new meeting or suggestion

                                                int timerStatus = 1; // 1 = timer can not run
                                                String updateOrder = ""; // nothing to update

                                                // update row in db table
                                                myDb.updateMeetingFoundFromSuggestion(tmpMeetingSuggestionDataServerId, tmpMeetingFoundFromSuggestionDate, tmpMeetingFoundFromSuggestionAuthor, newMeeting, meetingStatus, timerStatus, updateOrder);

                                                returnMap.put("MeetingFoundFromSuggestion", "1");
                                                returnMap.put("Meeting", "1");
                                                returnMap.put("MeetingFoundSuggestionMainActivityUpdateView", "1");
                                            }
                                            else {

                                                // check suggestion need a response time and a start point (server time)
                                                if (tmpMeetingSuggestionResponseTime > 0 && globalServerTime > 0) {

                                                    // init suggestion parameters
                                                    Long tmpUploadTime = System.currentTimeMillis();

                                                    // set last start point of timer for suggestion to now time
                                                    prefsEditor.putLong(ConstansClassMeeting.namePrefsMeeting_LastStartPointSuggestionTimer, tmpUploadTime);
                                                    prefsEditor.apply();

                                                    if (tmpMeetingSuggestionDate1 > 0) {
                                                        array_meetingTime[0] = tmpMeetingSuggestionDate1;
                                                    }
                                                    if (tmpMeetingSuggestionDate2 > 0) {
                                                        array_meetingTime[1] = tmpMeetingSuggestionDate2;
                                                    }
                                                    if (tmpMeetingSuggestionDate3 > 0) {
                                                        array_meetingTime[2] = tmpMeetingSuggestionDate3;
                                                    }
                                                    if (tmpMeetingSuggestionDate4 > 0) {
                                                        array_meetingTime[3] = tmpMeetingSuggestionDate4;
                                                    }
                                                    if (tmpMeetingSuggestionDate5 > 0) {
                                                        array_meetingTime[4] = tmpMeetingSuggestionDate5;
                                                    }
                                                    if (tmpMeetingSuggestionDate6 > 0) {
                                                        array_meetingTime[5] = tmpMeetingSuggestionDate6;
                                                    }

                                                    if (tmpMeetingPlace1 > 0) {
                                                        array_meetingPlace[0] = tmpMeetingPlace1;
                                                    }
                                                    if (tmpMeetingPlace2 > 0) {
                                                        array_meetingPlace[1] = tmpMeetingPlace2;
                                                    }
                                                    if (tmpMeetingPlace3 > 0) {
                                                        array_meetingPlace[2] = tmpMeetingPlace3;
                                                    }
                                                    if (tmpMeetingPlace4 > 0) {
                                                        array_meetingPlace[3] = tmpMeetingPlace4;
                                                    }
                                                    if (tmpMeetingPlace5 > 0) {
                                                        array_meetingPlace[4] = tmpMeetingPlace5;
                                                    }
                                                    if (tmpMeetingPlace6 > 0) {
                                                        array_meetingPlace[5] = tmpMeetingPlace6;
                                                    }

                                                    array_meetingVote[0] = 0;
                                                    array_meetingVote[1] = 0;
                                                    array_meetingVote[2] = 0;
                                                    array_meetingVote[3] = 0;
                                                    array_meetingVote[4] = 0;
                                                    array_meetingVote[5] = 0;

                                                    tmpClientVoteAuthor = "";
                                                    Long tmpClientVoteDate = 0L; // value comes from server -> server time
                                                    tmpClientVoteLocaleDate = 0L;

                                                    Long tmpMeetingSuggestionResponseStartTime = globalServerTime; // value comes from server -> server time

                                                    tmpMeetingFoundFromSuggestionAuthor = "";
                                                    tmpMeetingFoundFromSuggestionDate = 0L;
                                                    int tmpMeetingFoundFromSuggestion = 0; // 0=no meeting found from suggestion; 1=meeting found from suggestion

                                                    tmpMeetingSuggestionCoachCanceleTime = 0L;
                                                    tmpMeetingSuggestionCoachCanceleAuthor = "";
                                                    int tmpMeetingSuggestionCoachCancele = 0; // 0=not canceled; 1 = canceled

                                                    String tmpClientSuggestionText = "";
                                                    String tmpClientSuggestionAuthor = "";
                                                    Long tmpClientSuggestionTime = 0L; // value comes from server -> server time
                                                    Long tmpClientSuggestionLocaleTime = 0L;

                                                    tmpClientSuggestionStartDate = 0L;
                                                    tmpClientSuggestionEndDate = 0L;

                                                    Long tmpMeetingSuggestionClientCanceleTime = 0L; // value comes from server -> server time
                                                    Long tmpMeetingSuggestionClientCanceleLocaleTime = 0L;
                                                    String tmpMeetingSuggestionClientCanceleAuthor = "";
                                                    String tmpMeetingSuggestionClientCanceleText = "";
                                                    int tmpMeetingSuggestionClientCancele = 0; // 0=not canceled; 1=canceled

                                                    String tmpClientCommentText = "";
                                                    String tmpClientCommentAuthor = "";
                                                    Long tmpClientCommentTime = 0L; // value comes from server -> server time
                                                    Long tmpClientCommentLocaleTime = 0L;

                                                    int meetingStatus = 4; // 0=ready to send, 1=meeting/suggestion send, 4=external message
                                                    int newMeeting = 1; // 1 = new meeting or suggestion

                                                    int timerStatus = 0; // this is a suggestion -> timer needed! =0 timer can run; =1 timer stop!
                                                    String updateOrder = "";

                                                    // insert new data into db
                                                    myDb.insertNewMeetingOrSuggestionDate(array_meetingTime, array_meetingPlace, array_meetingVote, tmpClientVoteAuthor, tmpClientVoteDate, tmpClientVoteLocaleDate, tmpMeetingSuggestionCreationTime, tmpMeetingSuggestionAuthorName, tmpMeetingSuggestionKategorie, tmpMeetingSuggestionResponseTime, tmpMeetingSuggestionResponseStartTime, tmpMeetingSuggestionCoachHintText, tmpMeetingSuggestionCoachCancele, tmpMeetingSuggestionCoachCanceleTime, tmpMeetingSuggestionCoachCanceleAuthor, tmpMeetingFoundFromSuggestionAuthor, tmpMeetingFoundFromSuggestionDate, tmpMeetingFoundFromSuggestion, tmpMeetingSuggestionDataServerId, tmpClientSuggestionText, tmpClientSuggestionAuthor, tmpClientSuggestionTime, tmpClientSuggestionLocaleTime, tmpClientSuggestionStartDate, tmpClientSuggestionEndDate, tmpClientCommentText, tmpClientCommentAuthor, tmpClientCommentTime, tmpClientCommentLocaleTime, tmpMeetingSuggestionClientCancele, tmpMeetingSuggestionClientCanceleTime, tmpMeetingSuggestionClientCanceleLocaleTime, tmpMeetingSuggestionClientCanceleAuthor, tmpMeetingSuggestionClientCanceleText, meetingStatus, tmpUploadTime, newMeeting, timerStatus, updateOrder);

                                                    returnMap.put("MeetingNewSuggestion", "1");
                                                    returnMap.put("Meeting", "1");
                                                    returnMap.put("MeetingNewSuggestionMeetingMainActivityUpdateView", "1");
                                                }
                                            }
                                        }
                                        break;

                                    case 3: // empty, not needed anymore
                                        break;

                                    case 4: // suggestion from client -> invitation from coach
                                        if (tmpClientSuggestionStartDate > 0 && tmpClientSuggestionEndDate > 0 && tmpMeetingSuggestionCreationTime > 0 && tmpMeetingSuggestionAuthorName.length() > 0 && tmpMeetingSuggestionDataServerId > 0) {

                                            // check if invitation for suggestion from client data is canceled
                                            if (tmpMeetingSuggestionCoachCanceleTime > 0 && tmpMeetingSuggestionCoachCanceleAuthor.length() > 0) {

                                                int meetingStatus = 4; // comes from external
                                                int newMeeting = 1; // 1 = new meeting or suggestion

                                                int timerStatus = 1; // 1 = timer can not run
                                                String updateOrder = ""; // nothing to update

                                                // update row in db table
                                                myDb.updateMeetingCanceledByCoach(tmpMeetingSuggestionDataServerId, tmpMeetingSuggestionCoachCanceleTime, tmpMeetingSuggestionCoachCanceleAuthor, newMeeting, meetingStatus, timerStatus, updateOrder);

                                                returnMap.put("MeetingCanceledClientSuggestionByCoach", "1");
                                                returnMap.put("Meeting", "1");
                                                returnMap.put("MeetingInvitationCancelMainActivityUpdateView", "1");

                                            } else  if (tmpMeetingFoundFromSuggestionAuthor.length() > 0 && tmpMeetingFoundFromSuggestionDate > 0) {
                                                // check if meeting found from invitation for suggestion from client

                                                int meetingStatus = 4; // comes from external
                                                int newMeeting = 1; // 1 = new meeting or suggestion

                                                int timerStatus = 1; // 1 = timer can not run
                                                String updateOrder = ""; // nothing to update

                                                // update row in db table
                                                myDb.updateMeetingFoundFromSuggestion(tmpMeetingSuggestionDataServerId, tmpMeetingFoundFromSuggestionDate, tmpMeetingFoundFromSuggestionAuthor, newMeeting, meetingStatus, timerStatus, updateOrder);

                                                returnMap.put("MeetingFoundFromClientSuggestion", "1");
                                                returnMap.put("Meeting", "1");
                                                returnMap.put("MeetingInvitationFoundClientMainActivityUpdateView", "1");
                                            }
                                            else {

                                                Long tmpUploadTime = System.currentTimeMillis();

                                                // set last start point of timer for suggestion from client to now time
                                                prefsEditor.putLong(ConstansClassMeeting.namePrefsMeeting_LastStartPointSuggestionFromClientTimer, tmpUploadTime);
                                                prefsEditor.apply();

                                                array_meetingTime[0] = 0L;
                                                array_meetingTime[1] = 0L;
                                                array_meetingTime[2] = 0L;
                                                array_meetingTime[3] = 0L;
                                                array_meetingTime[4] = 0L;
                                                array_meetingTime[5] = 0L;

                                                array_meetingPlace[0] = 0;
                                                array_meetingPlace[1] = 0;
                                                array_meetingPlace[2] = 0;
                                                array_meetingPlace[3] = 0;
                                                array_meetingPlace[4] = 0;
                                                array_meetingPlace[5] = 0;

                                                array_meetingVote[0] = 0;
                                                array_meetingVote[1] = 0;
                                                array_meetingVote[2] = 0;
                                                array_meetingVote[3] = 0;
                                                array_meetingVote[4] = 0;
                                                array_meetingVote[5] = 0;

                                                tmpClientVoteAuthor = "";
                                                Long tmpClientVoteDate = 0L; // value comes from server -> server time
                                                tmpClientVoteLocaleDate = 0L;

                                                Long tmpMeetingSuggestionResponseStartTime = 0L; // value comes from server -> server time

                                                tmpMeetingFoundFromSuggestionAuthor = "";
                                                tmpMeetingFoundFromSuggestionDate = 0L;
                                                int tmpMeetingFoundFromSuggestion = 0; // 0=no meeting found from suggestion; 1=meeting found from suggestion

                                                tmpMeetingSuggestionCoachCanceleTime = 0L;
                                                tmpMeetingSuggestionCoachCanceleAuthor = "";
                                                int tmpMeetingSuggestionCoachCancele = 0; // 0=not canceled; 1 = canceled

                                                String tmpClientSuggestionText = "";
                                                String tmpClientSuggestionAuthor = "";
                                                Long tmpClientSuggestionTime = 0L;
                                                Long tmpClientSuggestionLocaleTime = 0L; // value comes from server -> server time

                                                Long tmpMeetingSuggestionClientCanceleTime = 0L;
                                                Long tmpMeetingSuggestionClientCanceleLocaleTime = 0L; // value comes from server -> server time
                                                String tmpMeetingSuggestionClientCanceleAuthor = "";
                                                String tmpMeetingSuggestionClientCanceleText = "";
                                                int tmpMeetingSuggestionClientCancele = 0; // 0=not canceled; 1 = canceled

                                                String tmpClientCommentText = "";
                                                String tmpClientCommentAuthor = "";
                                                Long tmpClientCommentTime = 0L;
                                                Long tmpClientCommentLocaleTime = 0L; // value comes from server -> server time

                                                int meetingStatus = 4; // 0=ready to send, 1=meeting/suggestion send, 4=external message
                                                int newMeeting = 1; // 1 = new meeting or suggestion

                                                int timerStatus = 0; // this is a client suggestion -> timer needed!
                                                String updateOrder = "";

                                                // insert new data into db
                                                myDb.insertNewMeetingOrSuggestionDate(array_meetingTime, array_meetingPlace, array_meetingVote, tmpClientVoteAuthor, tmpClientVoteDate, tmpClientVoteLocaleDate, tmpMeetingSuggestionCreationTime, tmpMeetingSuggestionAuthorName, tmpMeetingSuggestionKategorie, tmpMeetingSuggestionResponseTime, tmpMeetingSuggestionResponseStartTime, tmpMeetingSuggestionCoachHintText, tmpMeetingSuggestionCoachCancele, tmpMeetingSuggestionCoachCanceleTime, tmpMeetingSuggestionCoachCanceleAuthor, tmpMeetingFoundFromSuggestionAuthor, tmpMeetingFoundFromSuggestionDate, tmpMeetingFoundFromSuggestion, tmpMeetingSuggestionDataServerId, tmpClientSuggestionText, tmpClientSuggestionAuthor, tmpClientSuggestionTime, tmpClientSuggestionLocaleTime, tmpClientSuggestionStartDate, tmpClientSuggestionEndDate, tmpClientCommentText, tmpClientCommentAuthor, tmpClientCommentTime, tmpClientCommentLocaleTime, tmpMeetingSuggestionClientCancele, tmpMeetingSuggestionClientCanceleTime, tmpMeetingSuggestionClientCanceleLocaleTime, tmpMeetingSuggestionClientCanceleAuthor, tmpMeetingSuggestionClientCanceleText, meetingStatus, tmpUploadTime, newMeeting, timerStatus, updateOrder);

                                                returnMap.put("MeetingNewInvitationSuggestion", "1");
                                                returnMap.put("Meeting", "1");
                                                returnMap.put("MeetingNewInvitationMainActivityUpdateView", "1");
                                            }
                                        }
                                        break;
                                }

                                // start alarm receiver meeting -> check remember meeting or suggestion
                                // new alarm manager service for start all needed alarms
                                EfbSetAlarmManager efbSetAlarmManager = new EfbSetAlarmManager(xmlContext);
                                efbSetAlarmManager.setAlarmManagerForRememberMeeting();
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

                // look for end tag of connect book
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForConnectBook)) {
                        parseAnymore = false;
                    }
                }

                if (eventType == XmlPullParser.START_TAG) {
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
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
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

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml connect book tag
        Boolean error = false;

        // tmp data for database insert
        String tmpMessage = "";
        String tmpAuthorName = "";
        Long tmpMessageLocaleTime = 0L;
        String tmpOrder = "";
        Long tmpUploadTime = 0L;
        int tmpMessageRole = -1;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForConnectBook_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update)  && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New)  && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
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
                        case ConstansClassXmlParser.xmlNameForConnectBook_Message:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get message text text
                                if (xpp.getText().trim().length() > 0) { // check if message text from xml > 0
                                    tmpMessage = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook_AuthorName:
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
                        case ConstansClassXmlParser.xmlNameForConnectBook_MessageLocaleTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get message time text
                                if (xpp.getText().trim().length() > 0) { // check if message time from xml > 0
                                    tmpMessageLocaleTime = Long.valueOf(xpp.getText().trim())* 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook_MessageRole:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get message role text
                                if (xpp.getText().trim().length() >= 0) { // check if message role from xml >= 0
                                    tmpMessageRole = Integer.valueOf(xpp.getText().trim());
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
                }

                // look for end tag of connectbook
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForConnectBook_Messages)) {
                        // check all data for connect book correct?
                        if (!error) {
                            // connect book message order -> new entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) && tmpMessage.length() > 0 && tmpAuthorName.length() > 0 && tmpMessageLocaleTime > 0 && tmpMessageRole >= 0) {

                                // set upload time on smartphone for message; value from server is not needed
                                tmpUploadTime = System.currentTimeMillis();
                                int messageStatus = 4;
                                int timerStatus = 0;
                                Boolean newEntry = true;

                                // put message into db (role: 0= left; 1= right; 2= center)
                                myDb.insertRowChatMessage(tmpAuthorName, tmpMessageLocaleTime, globalServerTime, tmpMessage, tmpMessageRole, messageStatus, newEntry, tmpUploadTime, timerStatus);

                                // refresh activity connect book
                                returnMap.put("ConnectBook","1");
                                returnMap.put("ConnectBookMessageNewOrSend", "1");
                                returnMap.put("ConnectBookMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && tmpMessage.length() > 0 && tmpAuthorName.length() > 0 && tmpMessageLocaleTime > 0 && tmpMessageRole >= 0) {

                                // set upload time on smartphone for message; value from server is not needed
                                tmpUploadTime = System.currentTimeMillis();
                                int messageStatus = 4;
                                int timerStatus = 0;
                                Boolean newEntry = true;

                                // put message into db (role: 0= left; 1= right; 2= center)
                                myDb.insertRowChatMessage(tmpAuthorName, tmpMessageLocaleTime, globalServerTime, tmpMessage, tmpMessageRole, messageStatus, newEntry, tmpUploadTime, timerStatus);

                                // refresh activity connect book
                                returnMap.put("ConnectBook","1");
                                returnMap.put("ConnectBookMessageNewOrSend", "1");
                                returnMap.put("ConnectBookMainActivityUpdateView", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {

                                // delete all messages in connect book
                                myDb.deleteAllChatMessage ();
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


    // read element connect book settings
    private void readConnectBookTag_Settings() {

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml connect book settings tag
        Boolean error = false;

        // tmp data for prefs
        Boolean tmpConnectBookOnOff = false;
        String tmpOrder = "";
        int tmpMaxLetters = -1;
        int tmpMaxMessages = -1;
        int tmpDelayTime = -1;
        int tmpMessageShare = -1;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
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
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch meeting turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {
                                        tmpConnectBookOnOff = true;
                                    } else {
                                        tmpConnectBookOnOff = false;
                                    }
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook_DelayTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get send delay time
                                if (xpp.getText().trim().length() > 0) { // check if delay time from xml > 0
                                    tmpDelayTime = Integer.valueOf(xpp.getText().trim());
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook_MaxMessages:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max messages
                                if (xpp.getText().trim().length() > 0) { // check if max messages from xml > 0
                                    tmpMaxMessages = Integer.valueOf(xpp.getText().trim());
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook_MaxLetters:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max letters
                                if (xpp.getText().trim().length() > 0) { // check if max letters from xml > 0
                                    tmpMaxLetters = Integer.valueOf(xpp.getText().trim());
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForConnectBook_MessageShare:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get share value; 0-> not sharing; 1-> sharing
                                if (xpp.getText().trim().length() > 0) { // check if share value from xml > 0
                                    tmpMessageShare = Integer.valueOf(xpp.getText().trim());
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
                }

                // look for end tag of connect book settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForConnectBook_Settings)) {
                        // check all data for connect book settings correct?
                        if (!error) {
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) { // connect book settings order -> delete?

                                // refresh activity connect book because settings have change
                                returnMap.put("ConnectBook", "1");
                                returnMap.put("ConnectBookSettings", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update)) { // connect book settings order -> update?

                                if (!prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_ConnectBook, false) && tmpConnectBookOnOff || prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_ConnectBook, false) && !tmpConnectBookOnOff) {
                                    returnMap.put("ConnectBookSettingsMainActivityUpdateView", "1");
                                }

                                // in every case -> write data connect book on off to prefs
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_ConnectBook, tmpConnectBookOnOff);
                                prefsEditor.apply();

                                // write send delay time, max letters and max messages to prefs when all set
                                if (tmpConnectBookOnOff && tmpDelayTime >= 0 && tmpMaxLetters >= 0 && tmpMaxMessages >= 0) {
                                    prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectSendDelayTime, tmpDelayTime);
                                    prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectMaxLetters, tmpMaxLetters);
                                    prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectMaxMessages, tmpMaxMessages);

                                    // reset message counter and start time message counter
                                    prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, 0);

                                    // get normal timestamp with day, month and year (hour, minute, seconds and millseconds are zero)
                                    Long startTimestamp = EfbHelperClass.timestampToNormalDayMonthYearDate(System.currentTimeMillis());
                                    prefsEditor.putLong(ConstansClassConnectBook.namePrefsConnectCountMessagesResetTime, startTimestamp);
                                    prefsEditor.putLong(ConstansClassConnectBook.namePrefsConnectCountMessagesLastResetLocaleTime, 0L);
                                    prefsEditor.apply();

                                    // something change in message and delay settings
                                    returnMap.put("ConnectBookSettingsMessageAndDelay", "1");
                                }

                                // set new share value to prefs and set returnMap
                                if (prefs.getInt(ConstansClassConnectBook.namePrefsConnectMessageShare, 0) != tmpMessageShare) {
                                    prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectMessageShare, tmpMessageShare); // write new share value to prefs
                                    prefsEditor.putLong(ConstansClassConnectBook.namePrefsConnectMessageShareChangeTime, System.currentTimeMillis());

                                    if (tmpMessageShare == 1) { // sharing is enable; 1-> sharing messages; 0-> not sharing
                                        returnMap.put("ConnectBookSettingsMessageShareEnable", "1");
                                    } else {
                                        returnMap.put("ConnectBookSettingsMessageShareDisable", "1");
                                    }
                                }

                                // refresh activity connect book because settings have change
                                returnMap.put("ConnectBook", "1");
                                returnMap.put("ConnectBookSettings", "1");
                            }
                        }
                        parseAnymore = false;
                    }
                }
            }
        } catch (XmlPullParserException e) {
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
    // Begin read message -----------------------------------------------------------------------------------
    //


    // read element message
    private void readMessageTag() {

        Boolean parseAnymore = true;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                // look for end tag of message
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMessage)) {
                        parseAnymore = false;
                    }
                }

                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForMessage_Messages:
                            readMessageTag_Messages();
                            break;

                        case ConstansClassXmlParser.xmlNameForMessage_Settings:
                            readMessageTag_Settings();
                            break;
                    }
                }
                eventType = xpp.next();

                // Safety abbort end document
                if (eventType == XmlPullParser.END_DOCUMENT) {
                    parseAnymore = false;
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


    // read element message messages
    private void readMessageTag_Messages() {

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml message tag
        Boolean error = false;

        // tmp data for database insert
        String tmpMessage = "";
        String tmpAuthorName = "";
        Long tmpMessageLocaleTime = 0L;
        String tmpOrder = "";
        Long tmpUploadTime = 0L;
        int tmpMessageRole = -1;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForMessage_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_UpdateAssociatedMessage)  && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_NewAssociatedMessage) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_UpdateNotAssociatedMessage)  && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_NewNotAssociatedMessage)  && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
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
                        case ConstansClassXmlParser.xmlNameForMessage_MessageText:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get message text text
                                if (xpp.getText().trim().length() > 0) { // check if message text from xml > 0
                                    tmpMessage = xpp.getText().trim();
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMessage_AuthorName:
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
                        case ConstansClassXmlParser.xmlNameForMessage_MessageLocaleTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get message time text
                                if (xpp.getText().trim().length() > 0) { // check if message time from xml > 0
                                    tmpMessageLocaleTime = Long.valueOf(xpp.getText().trim())* 1000; // make Long from xml-text in milliseconds!!!!!
                                }
                                else {
                                    error = true;
                                }
                            }
                            else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMessage_MessageRole: // not needed for further operations
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get message role text
                                if (xpp.getText().trim().length() >= 0) { // check if message role from xml >= 0
                                    tmpMessageRole = Integer.valueOf(xpp.getText().trim());
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
                }

                // look for end tag of message
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMessage_Messages)) {
                        // check all data for message correct?
                        if (!error) {
                            // message order -> new associated entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_NewAssociatedMessage) && tmpMessage.length() > 0 && tmpAuthorName.length() > 0 && tmpMessageLocaleTime > 0 && tmpMessageRole >= 0) {

                                // set upload time on smartphone for message; value from server is not needed
                                tmpUploadTime = System.currentTimeMillis();
                                int messageStatus = 4;
                                int anonymous = 1; // not anonymous -> app is connected to server
                                tmpMessageRole = 0; // in ervery case comes from extern -> left side (0= messages on left side; 1= messages on the right side)
                                String source = "message"; // used for future extension
                                Boolean newEntry = true;

                                // put message into db (role: 0= left; 1= right)
                                myDb.insertRowMessage(tmpAuthorName, tmpMessageLocaleTime, globalServerTime, tmpMessage, tmpMessageRole, messageStatus, newEntry, tmpUploadTime, anonymous, source);

                                // refresh activity message
                                returnMap.put("MessagesMessage","1");
                                returnMap.put("MessageMessageNewOrSend", "1");
                                returnMap.put("MessagesMessageMainActivityUpdateView","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_UpdateAssociatedMessage) && tmpMessage.length() > 0 && tmpAuthorName.length() > 0 && tmpMessageLocaleTime > 0 && tmpMessageRole >= 0) {

                                // set upload time on smartphone for message; value from server is not needed
                                tmpUploadTime = System.currentTimeMillis();
                                int messageStatus = 4;
                                int anonymous = 1; // not anonymous -> app is connected to server
                                tmpMessageRole = 0; // in ervery case comes from extern -> left side (0= messages on left side; 1= messages on the right side)
                                String source = "message"; // used for future extension
                                Boolean newEntry = true;

                                // put message into db (role: 0= left; 1= right)
                                myDb.insertRowMessage(tmpAuthorName, tmpMessageLocaleTime, globalServerTime, tmpMessage, tmpMessageRole, messageStatus, newEntry, tmpUploadTime, anonymous, source);

                                // refresh activity message
                                returnMap.put("MessagesMessage","1");
                                returnMap.put("MessageMessageNewOrSend", "1");
                                returnMap.put("MessagesMessageMainActivityUpdateView","1");

                            }
                            // message order -> new not associated entry?
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_NewNotAssociatedMessage) && tmpMessage.length() > 0 && tmpAuthorName.length() > 0 && tmpMessageLocaleTime > 0 && tmpMessageRole >= 0) {

                                // set upload time on smartphone for message; value from server is not needed
                                tmpUploadTime = System.currentTimeMillis();
                                int messageStatus = 4;
                                int anonymous = 0; // anonymous -> app is not connected to server
                                tmpMessageRole = 0; // in ervery case comes from extern -> left side (0= messages on left side; 1= messages on the right side)
                                Boolean newEntry = true;
                                String source = "message"; // used for future extension

                                // put message into db (role: 0= left; 1= right)
                                myDb.insertRowMessage(tmpAuthorName, tmpMessageLocaleTime, globalServerTime, tmpMessage, tmpMessageRole, messageStatus, newEntry, tmpUploadTime, anonymous, source);

                                // refresh activity message
                                returnMap.put("MessagesMessage","1");
                                returnMap.put("MessageMessageNewOrSend", "1");
                                returnMap.put("MessagesMessageMainActivityUpdateView","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_UpdateNotAssociatedMessage) && tmpMessage.length() > 0 && tmpAuthorName.length() > 0 && tmpMessageLocaleTime > 0 && tmpMessageRole >= 0) {

                                // set upload time on smartphone for message; value from server is not needed
                                tmpUploadTime = System.currentTimeMillis();
                                int messageStatus = 4;
                                int anonymous = 0; // anonymous -> app is not connected to server
                                tmpMessageRole = 0; // in ervery case comes from extern -> left side (0= messages on left side; 1= messages on the right side)
                                Boolean newEntry = true;
                                String source = "message"; // used for future extension

                                // put message into db (role: 0= left; 1= right)
                                myDb.insertRowMessage(tmpAuthorName, tmpMessageLocaleTime, globalServerTime, tmpMessage, tmpMessageRole, messageStatus, newEntry, tmpUploadTime, anonymous, source);

                                // refresh activity message
                                returnMap.put("MessagesMessage","1");
                                returnMap.put("MessageMessageNewOrSend", "1");
                                returnMap.put("MessagesMessageMainActivityUpdateView","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {

                                // delete all messages in message
                                myDb.deleteAllMessages ();
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


    // read element message settings
    private void readMessageTag_Settings() {

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml message settings tag
        Boolean error = false;

        // tmp data for prefs
        Boolean tmpMessageOnOff = false;
        String tmpOrder = "";
        int tmpMaxLetters = -1;
        int tmpMaxMessages = -1;
        int tmpMessageStopCommunication = -1;
        Long tmpMessagesCountCommentSinceTime = 0L;

        try {
            int eventType = xpp.next();

            while (parseAnymore) {

                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForMessage_Order:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_UpdateAssociatedMessage) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_UpdateNotAssociatedMessage) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete_All)) {
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
                        case ConstansClassXmlParser.xmlNameForMessage_TurnOnOff:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get switch meeting turn on/off
                                if (xpp.getText().trim().length() > 0) { // check if switch from xml > 0
                                    int tmpSwitchValue = Integer.valueOf(xpp.getText().trim());
                                    if (tmpSwitchValue == 1) {
                                        tmpMessageOnOff = true;
                                    } else {
                                        tmpMessageOnOff = false;
                                    }
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForMessage_MaxMessages:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max messages
                                if (xpp.getText().trim().length() > 0) { // check if max messages from xml > 0
                                    tmpMaxMessages = Integer.valueOf(xpp.getText().trim());
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMessage_MaxLetters:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get max letters
                                if (xpp.getText().trim().length() > 0) { // check if max letters from xml > 0
                                    tmpMaxLetters = Integer.valueOf(xpp.getText().trim());
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMessage_StopCommunication:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get share value; 0-> communication possible; 1-> no communication possible
                                if (xpp.getText().trim().length() >= 0) { // check if value from xml >= 0
                                    tmpMessageStopCommunication = Integer.valueOf(xpp.getText().trim());
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForMessage_MessagesCountSinceTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get count since time;
                                if (xpp.getText().trim().length() >= 0) { // check if value from xml >= 0
                                    tmpMessagesCountCommentSinceTime = Long.valueOf(xpp.getText().trim());
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
                }

                // look for end tag of message settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForMessage_Settings)) {
                        // check all data for message settings correct?
                        if (!error) {
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete)) { // message settings order -> delete?

                                // refresh activity message because settings have change
                                returnMap.put("MessagesMessage", "1");
                                returnMap.put("MessageSettings", "1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_UpdateAssociatedMessage)) { // message settings order -> update?

                                // update main view when messages switch on or off
                                if (!prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Message, false) && tmpMessageOnOff || prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Message, false) && !tmpMessageOnOff) {
                                    returnMap.put("MessagesSettingsMainActivityUpdateView","1");
                                }

                                // in every case -> write data message on off to prefs
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Message, tmpMessageOnOff);
                                prefsEditor.apply();

                                // write max letters and max messages to prefs when all set
                                if (tmpMessageOnOff && tmpMaxLetters >= 0 && tmpMaxMessages >= 0) {
                                    
                                    prefsEditor.putInt(ConstansClassMessage.namePrefsMessageMaxLettersAssociated, tmpMaxLetters);
                                    prefsEditor.putInt(ConstansClassMessage.namePrefsMessageMaxMessageAssociated, tmpMaxMessages);
                                    if (tmpMaxMessages < prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated,0)) {
                                        prefsEditor.putInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated, 0); // reset count associated messages to 0
                                    }

                                    // check stop sending
                                    if (tmpMessageStopCommunication == 0 && prefs.getBoolean(ConstansClassMessage.namePrefsMessageStopCommunication, false)) {
                                        prefsEditor.putBoolean(ConstansClassMessage.namePrefsMessageStopCommunication, false);
                                        returnMap.put("MessageSettingsMessageStopSendingEnable", "1");
                                    }
                                    else if (tmpMessageStopCommunication == 1 && !prefs.getBoolean(ConstansClassMessage.namePrefsMessageStopCommunication, false)) {
                                        prefsEditor.putBoolean(ConstansClassMessage.namePrefsMessageStopCommunication, true);
                                        returnMap.put("MessageSettingsMessageStopSendingDisable", "1");
                                    }

                                    // check if new since time greater then old one, reset count messages associated and set new since time
                                    if (tmpMessagesCountCommentSinceTime > prefs.getLong(ConstansClassMessage.namePrefsMessagesTimeSinceAssociatedInMills, 0)) {
                                        prefsEditor.putLong(ConstansClassMessage.namePrefsMessagesTimeSinceAssociatedInMills, tmpMessagesCountCommentSinceTime); // write new since time to prefs
                                        prefsEditor.putInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated, 0); // reset count associated messages to 0
                                    }

                                    prefsEditor.apply();
                                }

                                // refresh activity message because settings have change
                                returnMap.put("MessagesMessage", "1");
                                returnMap.put("MessageSettings", "1");
                            }

                            else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_UpdateNotAssociatedMessage)) { // message settings order -> update?

                                // write max letters and max messages to prefs when all set
                                if (tmpMaxLetters >= 0 && tmpMaxMessages >= 0) {

                                    prefsEditor.putInt(ConstansClassMessage.namePrefsMessageMaxLettersNotAssociated, tmpMaxLetters);
                                    prefsEditor.putInt(ConstansClassMessage.namePrefsMessageMaxMessageNotAssociated, tmpMaxMessages);
                                    if (tmpMaxMessages < prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentNotAssociated,0)) {
                                        prefsEditor.putInt(ConstansClassMessage.namePrefsMessageCountCurrentNotAssociated, 0); // reset count not associated messages to 0
                                    }

                                    // check stop sending
                                    if (tmpMessageStopCommunication == 0 && prefs.getBoolean(ConstansClassMessage.namePrefsMessageStopCommunication, false)) {
                                        prefsEditor.putBoolean(ConstansClassMessage.namePrefsMessageStopCommunication, false);
                                        returnMap.put("MessageSettingsMessageStopSendingEnable", "1");
                                    }
                                    else if (tmpMessageStopCommunication == 1 && !prefs.getBoolean(ConstansClassMessage.namePrefsMessageStopCommunication, false)) {
                                        prefsEditor.putBoolean(ConstansClassMessage.namePrefsMessageStopCommunication, true);
                                        returnMap.put("MessageSettingsMessageStopSendingDisable", "1");
                                    }

                                    // check if new since time greater then old one, reset count messages not associated and set new since time
                                    if (tmpMessagesCountCommentSinceTime > prefs.getLong(ConstansClassMessage.namePrefsMessagesTimeSinceNotAssociatedInMills, 0)) {
                                        prefsEditor.putLong(ConstansClassMessage.namePrefsMessagesTimeSinceNotAssociatedInMills, tmpMessagesCountCommentSinceTime); // write new since time to prefs
                                        prefsEditor.putInt(ConstansClassMessage.namePrefsMessageCountCurrentNotAssociated, 0); // reset count not associated messages to 0
                                    }

                                    prefsEditor.apply();
                                }

                                // refresh activity message because settings have change
                                returnMap.put("MessagesMessage", "1");
                                returnMap.put("MessageSettings", "1");
                            }
                        }
                        parseAnymore = false;
                    }
                }
            }
        } catch (XmlPullParserException e) {
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
    // End read message -----------------------------------------------------------------------------------
    //


    //
    // Begin read settings -----------------------------------------------------------------------------------
    //

    // read element settings
    private void readSettingTag() {

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our arrangement settings comment tag
        Boolean error = false;

        // tmp data for prefs and database insert
        // switch for functions
        Boolean tmpPreventionOnOff = false;
        Boolean tmpFaqOnOff = false;
        Boolean tmpEmergencyOnOff = false;
        Boolean tmpSettingsOnOff = false;
        Boolean tmpCaseClose = false;
        String tmpClientName = "";

        // settings order
        String tmpOrder = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
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
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForSettings_ClientName:
                             eventType = xpp.next();
                             if (eventType == XmlPullParser.TEXT) { //  get client name
                                 if (xpp.getText().trim().length() > 0) { // check if client name from xml > 0
                                     tmpClientName = xpp.getText().trim();
                                 } else {
                                     error = true;
                                 }
                             } else {
                                 error = true;
                             }
                             break;
                        case ConstansClassXmlParser.xmlNameForSettings_CaseClose:
                             eventType = xpp.next();
                             if (eventType == XmlPullParser.TEXT) { // get case close
                                 if (xpp.getText().trim().length() > 0) { // check if case close from xml > 0
                                     int tmpCaseCloseValue = Integer.valueOf(xpp.getText().trim());
                                     if (tmpCaseCloseValue == 1) {
                                         tmpCaseClose = true;
                                     } else {
                                         tmpCaseClose = false;
                                     }
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
                }

                // look for end tag of settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForSettings)) {
                        // check all data for settings correct?
                        if (!error) {
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Delete) ) { // settings order -> delete?
                                // refresh activity settings because settings have change
                                returnMap.put("Settings","1");

                            } else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // settings order -> update?

                                if (tmpCaseClose) { // case is close!!!

                                    // set case close to true
                                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsCaseClose, true);
                                    // delete client id
                                    prefsEditor.putString(ConstansClassSettings.namePrefsClientId, "");
                                    // delete contact id
                                    prefsEditor.putString(ConstansClassSettings.namePrefsContactId, "");
                                    // set connection status to connect to server
                                    prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus, 0); // 0=connect to server; 1=no network available; 2=connection error; 3=connected

                                    // switch evaluation moduls from arrangement and goals off
                                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false);
                                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false);

                                    // set conection random (Pin) number to 0
                                    prefsEditor.putInt(ConstansClassSettings.namePrefsRandomNumberForConnection,0);

                                    // delete client name
                                    prefsEditor.putString(ConstansClassSettings.namePrefsClientName, "Unbekannt");

                                    // set function message on
                                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Message, true); // turn function message on
                                    prefsEditor.putInt(ConstansClassMessage.namePrefsMessageCountCurrentNotAssociated, 0);
                                    prefsEditor.putInt(ConstansClassMessage.namePrefsMessageMaxLettersNotAssociated, ConstansClassMessage.namePrefsMessageMaxLettersNotAssociatedStandard);
                                    prefsEditor.putInt(ConstansClassMessage.namePrefsMessageMaxMessageNotAssociated, ConstansClassMessage.namePrefsMessageMaxMessageNotAssociatedStandard);
                                    prefsEditor.putBoolean(ConstansClassMessage.namePrefsMessageWelcomeMessageWithoutConnection, false);

                                    // write last error messages to prefs
                                    prefsEditor.putString(ConstansClassSettings.namePrefsLastErrorMessages, "");
                                    prefsEditor.apply();

                                    // refresh app beause case is close
                                    returnMap.put("Case_close","1");
                                }
                                else {
                                    // update client name?
                                    if (tmpClientName.length() > 0 && !prefs.getString(ConstansClassSettings.namePrefsClientName, "x").equals(tmpClientName)) {
                                        // write data to prefs
                                        prefsEditor.putString(ConstansClassSettings.namePrefsClientName, tmpClientName);
                                    }

                                     // update main view when prevention is switched on or off
                                    if (!prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Prevention, false) && tmpPreventionOnOff || prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Prevention, false) && !tmpPreventionOnOff) {
                                        returnMap.put("PreventionPrevention","1");
                                        returnMap.put("PreventionSettingMainActivityUpdateView", "1");
                                    }

                                    // update main view when faq is switched on or off
                                    if (!prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Faq, false) && tmpFaqOnOff || prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Faq, false) && !tmpFaqOnOff) {
                                        returnMap.put("FaqFaq","1");
                                        returnMap.put("FaqSettingMainActivityUpdateView", "1");
                                    }

                                    // update main view when emergency is switched on or off
                                    if (!prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_EmergencyHelp, false) && tmpEmergencyOnOff || prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_EmergencyHelp, false) && !tmpEmergencyOnOff) {
                                        returnMap.put("EmergencyEmergency","0");
                                        returnMap.put("EmergencySettingMainActivityUpdateView", "0");
                                    }

                                    // update main view when setting is switched on or off
                                    if (!prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Settings, false) && tmpSettingsOnOff || prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Settings, false) && !tmpSettingsOnOff) {
                                        returnMap.put("SettingSetting","0");
                                        returnMap.put("SettingSettingMainActivityUpdateView", "0");
                                    }

                                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Prevention, tmpPreventionOnOff); // turn function prevention on/off
                                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Faq, tmpFaqOnOff); // turn function faq on/off
                                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_EmergencyHelp, tmpEmergencyOnOff); // turn function emergency help on/off
                                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Settings, tmpSettingsOnOff); // turn function settings on/off
                                    prefsEditor.apply();
                                }

                                // refresh activity settings because settings have change
                                returnMap.put("Settings","1");
                                returnMap.put("SettingsOtherMenueItems","1");

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


    // read element involved person
    private void readSettingInvolvedPersonTag() {

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml our arrangement settings comment tag
        Boolean error = false;

        // tmp data for prefs and database insert
        String tmpInvolvedPersonName = "";
        String tmpInvolvedPersonFunction = "";
        String tmpInvolvedPersonPrecenseTextOne = "";
        String tmpInvolvedPersonPrecenseTextTwo = "";
        Long tmpInvolvedPersonPrecenseTwoStart = 0L;
        Long tmpInvolvedPersonPrecenseTwoEnd = 0L;
        Long tmpInvolvedPersonModifiedTime = 0L;

        // involved person order
        String tmpOrder = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().trim()) {
                        case ConstansClassXmlParser.xmlNameForSettings_CaseInvolvedPersonOrder:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get order text
                                if (xpp.getText().trim().length() > 0) { // check if order from xml > 0
                                    tmpOrder = xpp.getText().trim();
                                    if (!tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) && !tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New)) {
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
                        case ConstansClassXmlParser.xmlNameForSettings_CaseInvolvedPersonName:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get name for involved person
                                if (xpp.getText().trim().length() > 0) { // check name from xml > 0
                                    tmpInvolvedPersonName = xpp.getText().trim();
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForSettings_CaseInvolvedPersonFunction:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get function for involved person
                                if (xpp.getText().trim().length() > 0) { // check function from xml > 0
                                    tmpInvolvedPersonFunction = xpp.getText().trim();
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForSettings_CaseInvolvedPersonPrecenseOne:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get precense text one for involved person
                                if (xpp.getText().trim().length() > 0) { // check text from xml > 0
                                    tmpInvolvedPersonPrecenseTextOne = xpp.getText().trim();
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForSettings_CaseInvolvedPersonPrecenseTwo:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get precense text two for involved person
                                if (xpp.getText().trim().length() > 0) { // check text from xml > 0
                                    tmpInvolvedPersonPrecenseTextTwo = xpp.getText().trim();
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForSettings_CaseInvolvedPersonPrecenseStart:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get start time precense text two
                                if (xpp.getText().trim().length() > 0) { // check if start time precense text two from xml > 0
                                    tmpInvolvedPersonPrecenseTwoStart = Long.valueOf(xpp.getText().trim())* 1000; // make Long from xml-text in milliseconds!!!!!
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForSettings_CaseInvolvedPersonPrecenseEnd:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get end time precense text two
                                if (xpp.getText().trim().length() > 0) { // check if end time precense text two from xml > 0
                                    tmpInvolvedPersonPrecenseTwoEnd = Long.valueOf(xpp.getText().trim())* 1000; // make Long from xml-text in milliseconds!!!!!
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;

                        case ConstansClassXmlParser.xmlNameForSettings_CaseInvolvedPersonModifiedTime:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get modified time precense text two
                                if (xpp.getText().trim().length() > 0) { // check if modified time precense text two from xml > 0
                                    tmpInvolvedPersonModifiedTime = Long.valueOf(xpp.getText().trim())* 1000; // make Long from xml-text in milliseconds!!!!!
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
                }

                // look for end tag of involved person
                if (eventType == XmlPullParser.END_TAG) {

                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForSettings_CaseInvolvedPerson)) {
                        // check all data for settings correct?
                        if (!error) {
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_New) ) { // involved person order -> new?

                                if (tmpInvolvedPersonName.length() > 0 && tmpInvolvedPersonFunction.length() > 0 && tmpInvolvedPersonModifiedTime > 0) {
                                    // delete all content from db table
                                    myDb.deleteTableInvolvedPerson();

                                    // and insert first person
                                    int newEntry = 1; // entry is new!
                                    myDb.insertNewInvolvedPerson(tmpInvolvedPersonName, tmpInvolvedPersonFunction, tmpInvolvedPersonPrecenseTextOne, tmpInvolvedPersonPrecenseTextTwo, tmpInvolvedPersonPrecenseTwoStart, tmpInvolvedPersonPrecenseTwoEnd, tmpInvolvedPersonModifiedTime, newEntry);

                                    // refresh activity settings because settings have change
                                    returnMap.put("Settings","1");
                                    returnMap.put("InvolvedPerson","1");
                                }
                            }
                            else if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // involved person order -> update?

                                if (tmpInvolvedPersonName.length() > 0 && tmpInvolvedPersonFunction.length() > 0 && tmpInvolvedPersonModifiedTime > 0) {

                                    // insert follow person
                                    int newEntry = 1; // entry is new!
                                    myDb.insertNewInvolvedPerson(tmpInvolvedPersonName, tmpInvolvedPersonFunction, tmpInvolvedPersonPrecenseTextOne, tmpInvolvedPersonPrecenseTextTwo, tmpInvolvedPersonPrecenseTwoStart, tmpInvolvedPersonPrecenseTwoEnd, tmpInvolvedPersonModifiedTime, newEntry);

                                    // refresh activity settings because settings have change
                                    returnMap.put("Settings","1");
                                    returnMap.put("InvolvedPerson","1");
                                }
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

        Boolean parseAnymore = true;

        // true -> error occuret while parsing xml time table tag
        Boolean error = false;

        // tmp data for prefs and database insert
        String tmpOrder = "";
        Boolean tmpTimeTableOnOff = false; // switch for functions
        int tmpTimeTableValue = -1;
        Long tmpChangeTime = 0L;
        String tmpAuthorName = "";

        try {
            int eventType = xpp.next();

            while (parseAnymore) {
                if (eventType == XmlPullParser.START_TAG) {
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
                                } else {
                                    error = true;
                                }
                            } else {
                                error = true;
                            }
                            break;
                        case ConstansClassXmlParser.xmlNameForTimeTable_Modified_Author:
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
                        case ConstansClassXmlParser.xmlNameForTimeTable_Modified_Date:
                            eventType = xpp.next();
                            if (eventType == XmlPullParser.TEXT) { // get change time text
                                if (xpp.getText().trim().length() > 0) { // check if change time from xml > 0
                                    tmpChangeTime = Long.valueOf(xpp.getText().trim())* 1000; // make Long from xml-text in milliseconds!!!!!
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
                }

                // look for end tag of settings
                if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().trim().equals(ConstansClassXmlParser.xmlNameForTimeTable)) {
                        // check all data for time table correct?
                        if (!error) {
                            if (tmpOrder.equals(ConstansClassXmlParser.xmlNameForOrder_Update) ) { // settings order -> update?

                                if (!prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_TimeTable, false) && tmpTimeTableOnOff || prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_TimeTable, false) && !tmpTimeTableOnOff) {
                                    returnMap.put("TimeTableSettingMainActivityUpdateView","1");
                                }

                                // set time table on/off
                                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_TimeTable, tmpTimeTableOnOff); // turn function time table on/off

                                // get old time table value for nothing change check
                                int tmpOldTimeTableValue = prefs.getInt(ConstansClassTimeTable.namePrefsTimeTableValue, 0); // get old value for time table

                                if (tmpTimeTableOnOff && tmpTimeTableValue >= 0 && tmpAuthorName.length() > 0 && tmpChangeTime > 0) { // change time table value?
                                    // check for value change!
                                    if (tmpOldTimeTableValue != tmpTimeTableValue) {
                                        prefsEditor.putInt(ConstansClassTimeTable.namePrefsTimeTableValue, tmpTimeTableValue); // set value for time table
                                        prefsEditor.putString(ConstansClassTimeTable.namePrefsTimeTableModifiedAuthor, tmpAuthorName); // set author name for time table
                                        prefsEditor.putLong(ConstansClassTimeTable.namePrefsTimeTableModifiedDate, tmpChangeTime); // set change date for time table
                                        prefsEditor.putBoolean(ConstansClassTimeTable.namePrefsTimeTableNewValue, true); // set new value for time table to true
                                        returnMap.put("TimeTableNewValue", "1");
                                        returnMap.put("TimeTableNewValueMainActivityUpdateView", "1");
                                    }
                                }
                                else if (!tmpTimeTableOnOff) { // time table is off -> set value to default
                                    prefsEditor.putInt(ConstansClassTimeTable.namePrefsTimeTableValue, 0); // set value for time table
                                    prefsEditor.putString(ConstansClassTimeTable.namePrefsTimeTableModifiedAuthor, ""); // set author name for time table
                                    prefsEditor.putLong(ConstansClassTimeTable.namePrefsTimeTableModifiedDate, 0); // set change date for time table
                                }

                                prefsEditor.apply();

                                // refresh activity time table because settings have change
                                returnMap.put("TimeTable","1");
                                returnMap.put("TimeTableSettings","1");
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

        returnMap.put("SendNotSuccessfull", "1");

        // write last error messages to prefs
        prefsEditor.putString(ConstansClassSettings.namePrefsLastErrorMessages, tmpErrorText);
        
        // set connection status to error
        //prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus, 1);
        
        prefsEditor.apply();
        
    }

}
