package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 06.02.2017.
 */
public class ConstansClassMeeting {



    // Number of different subtitles
    public static final int numberOfDifferentSubtitle = 8;





    // number of checkboxes for choosing timezones
    public static final int countNumberTimezones = 15;

    // number of simultaneous meetings
    public static final int numberSimultaneousMeetings = 2;






    // meeting client suggestion on off to prefs
    public static final String namePrefsMeeting_ClientSuggestion_OnOff ="meetingClientSuggestionOnOff";

    // meeting client cancele meeting on off to prefs
    public static final String namePrefsMeeting_ClientCanceleMeeting_OnOff ="meetingClientCanceleMeetingOnOff";

    // meeting client comment suggestion on off to prefs
    public static final String namePrefsMeeting_ClientCommentSuggestion_OnOff ="meetingClientCommentSuggestionOnOff";


    // meeting number of letters for canceled meeting reason
    public static final int namePrefsMaxLettersCanceledMeetingReason = 600;

















    // prefs name for meeting status
    public static final String namePrefsMeetingStatus = "meetingStatus";

    // prefs name for meeting place
    public static final String namePrefsMeetingPlace = "meetingPlace";

    // prefs name for timezone array
    public static final String namePrefsArrayMeetingTimezoneArray = "meetingTimezone_";

    // prefs name for meeting problem
    public static final String namePrefsMeetingProblem = "meetingProblem";

    // prefs name for meeting time and date
    public static final String namePrefsMeetingTimeAndDate = "meetingDateAndTime";

    // prefs name for author meeting suggestion
    public static final String namePrefsAuthorMeetingSuggestion = "authorMeetingSuggestions";

    // prefs name for info new meeting date and time
    public static final String namePrefsNewMeetingDateAndTime = "meetingNewDateAndTime";

    // prefs name for deadline for response of meeting suggestions
    public static final String namePrefsMeetingSuggestionsResponseDeadline = "meetingSuggestionsResponseDeadline";

    // prefs praefix for  meeting _A and _B in the name of pref
    public static String [] prefsPraefixMeetings = {"_A","_B"};

    // prefs name for meeting Id
    public static final String namePrefsMeetingId = "meetingId";

}
