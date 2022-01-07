package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 07.02.2017.
 */
class ConstansClassOurArrangement {

    // Max number of comments (current and sketch) <-> Over this number you can write infinitely comments
    static final int commentLimitationBorder = 1000;

    // Number of different subtitles
    static final int numberOfDifferentSubtitle = 8;

    // prefs name for current date of arrangement
    public static final String namePrefsCurrentDateOfArrangement = "currentDateOfArrangement";

    // prefs name for current block id of arrangement
    static final String namePrefsCurrentBlockIdOfArrangement = "currentBlockIdOfArrangement";

    // prefs name for current date of sketch arrangement
    static final String namePrefsCurrentDateOfSketchArrangement = "currentDateOfSketchArrangement";

    // prefs name for current block id of sketch arrangement
    static final String namePrefsCurrentBlockIdOfSketchArrangement = "currentBlockIdOfSketchArrangement";

    // prefs name for show evaluate arrangement
    static final String namePrefsShowEvaluateArrangement = "showArrangementEvaluate";

    // prefs name for show arrangement comment
    static final String namePrefsShowArrangementComment = "showArrangementComment";

    // prefs name for show old arrangement
    static final String namePrefsShowOldArrangement = "showOldArrangements";

    // prefs name for show sketch arrangement
    static final String namePrefsShowSketchArrangement = "showSketchArrangements";

    // prefs name for show link to comment sketch arrangement
    static final String namePrefsShowLinkCommentSketchArrangement = "showCommentLinkSketchArrangements";

    // prefs name for start Date Evaluation in mills
    static final String namePrefsStartDateEvaluationInMills = "startDataEvaluationInMills";

    // prefs name for start Date Evaluation in mills
    static final String namePrefsEndDateEvaluationInMills = "endDataEvaluationInMills";

    // prefs name for evaluation pause time in seconds
    static final String namePrefsEvaluatePauseTimeInSeconds = "evaluatePauseTimeInSeconds";

    // prefs name for evaluation active time in seconds
    static final String namePrefsEvaluateActiveTimeInSeconds = "evaluateActivTimeInSeconds";

    // prefs name for start point now arrangement evaluation in mills
    static final String namePrefsStartPointEvaluationPeriodInMills = "startPointEvaluationPeriodInMills";

    // prefs name for notification check if evaluation period change
    static final String namePrefsEvalautionPeriodChangeNotification = "evaluationPeriodChangeNotification";

    // prefs name for count max comment
    static final String namePrefsCommentMaxComment = "commentOurArrangementMaxComment";

    // prefs name for count max letters
    static final String namePrefsCommentMaxLetters = "commentOurArrangementMaxLetters";

    // prefs name for count delaytime
    static final String namePrefsCommentDelaytime = "commentOurArrangementDelaytime";

    // prefs name for count comment
    static final String namePrefsCommentCountComment = "commentOurArrangementCountComment";

    // prefs name for number of comment in list for show comment in our arrangement now
    static final String namePrefsNumberOfCommentForOurArrangementNowShowComment = "numberOfCommentForOurArrangementNow";

    // prefs name for time since comment start in mills
    static final String namePrefsCommentTimeSinceCommentStartInMills = "commentOurArrangementTimeSinceInMills";

    // prefs name for sharing comments
    static final String namePrefsArrangementCommentShare = "commentOurArrangementShare";

    // prefs name for store change time of arrangement sharing switch
    static final String namePrefsArrangementCommentShareChangeTime  = "commentOurArrangementShareChangeTime";

    // prefs name for count max sketch comment
    static final String namePrefsMaxSketchComment = "commentSketchOurArrangementMaxComment";

    // prefs name for count max sketch comment letters
    static final String namePrefsMaxSketchCommentLetters = "commentSketchOurArrangementMaxCommentLetters";

    // prefs name for count sketch comment delaytime
    static final String namePrefsSketchCommentDelaytime = "commentSketchOurArrangementDelaytime";

    // prefs name for count sketch comment
    static final String namePrefsSketchCommentCountComment = "commentSketchOurArrangementCountComment";

    // prefs name for time since sketch comment start in mills
    static final String namePrefsSketchCommentTimeSinceSketchCommentStartInMills = "sketchCommentOurArrangementTimeSinceInMills";

    // prefs name for sharing sketch comment
    static final String namePrefsArrangementSketchCommentShare = "sketchCommentOurArrangementShare";

    // prefs name for store change time of arrangement sketch comment counter
    static final String namePrefsArrangementSketchCommentShareChangeTime  = "sketchCommentOurArrangementShareChangeTime";

    // true -> Sketch arragement are updated; false -> no update
    static final String namePrefsSignalSketchArrangementUpdate = "signalSketchArrangementUpdated";

    // true -> Now arragement are updated; false -> no update
    static final String namePrefsSignalNowArrangementUpdate = "signalNowArrangementUpdated";

    // sort sequence of arrangement comment list-> "descending" or "ascending"
    static final String namePrefsSortSequenceOfArrangementCommentList = "arrangementCommentSortSequence";

    // sort sequence of now arrangement list-> "descending" or "ascending"
    static final String namePrefsSortSequenceOfArrangementNowList = "arrangementNowSortSequence";

    // sort sequence of now arrangement list-> "descending" or "ascending"
    static final String namePrefsSortSequenceOfArrangementOldList = "arrangementOldSortSequence";

    // sort sequence of arrangement sketch comment list-> "descending" or "ascending"
    static final String namePrefsSortSequenceOfArrangementSketchCommentList = "arrangementSketchCommentSortSequence";

    // sort sequence of sketch arrangement list-> "descending" or "ascending"
    static final String namePrefsSortSequenceOfArrangementSketchList = "arrangementSketchSortSequence";

    // prefs name for number of comment in list for show comment in our arrangement sketch
    static final String namePrefsNumberOfCommentForOurArrangementSketchShowComment = "numberOfCommentForOurArrangementSketch";

    // default time for active and pause time in evaluation arrangement (time in seconds)
    static final int defaultTimeForActiveAndPauseEvaluationArrangement = 43200; // this is 12 hours


    // constans for normal arrangement in change to
    static final String arrangementStatusNormal = "normal";

    // constans for sketch arrangement in change to
    static final String arrangementStatusSketch = "sketch";

    // constans for nothing in change to
    static final String arrangementStatusNothing = "nothing";













}
