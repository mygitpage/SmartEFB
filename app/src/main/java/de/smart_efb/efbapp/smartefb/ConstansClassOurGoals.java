package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 08.02.2017.
 */
class ConstansClassOurGoals {

    // Max number of comments (jointly and debetable goals) <-> Over this number you can write infinitely comments
    static final int commentLimitationBorder = 1000;

    // Number of different subtitles
    static final int numberOfDifferentSubtitle = 8;

    // prefs name for current date of jointly goals
    public static final String namePrefsCurrentDateOfJointlyGoals = "currentDateOfJointlyGoals";

    // prefs name for current date of debetable goals
    static final String namePrefsCurrentDateOfDebetableGoals = "currentDateOfDebetableGoals";

    // prefs name for current block id of jointly goals
    static final String namePrefsCurrentBlockIdOfJointlyGoals = "currentBlockIdOfJointlyGoals";

    // prefs name for current block id of debetable goals
    static final String namePrefsCurrentBlockIdOfDebetableGoals = "currentBlockIdOfDebetableGoals";

    // prefs name for show link for evaluate jointly goal
    static final String namePrefsShowLinkEvaluateJointlyGoals = "showEvaluateLinkJointlyGoals";

    // prefs name for show link for comment jointly goal
    static final String namePrefsShowLinkCommentJointlyGoals = "showCommentLinkJointlyGoals";

    // prefs name for show link for old goals
    static final String namePrefsShowLinkOldGoals = "showOldGoals";


    // prefs name for show link for debetable goals
    static final String namePrefsShowLinkDebetableGoals = "showDebetableGoals";

    // prefs name for show link for comment debetable goals
    static final String namePrefsShowLinkCommentDebetableGoals = "showCommentLinkDebetableGoals";

    // prefs name for jointly comment max count comments
    static final String namePrefsCommentMaxCountJointlyComment = "commentJointlyGoalMaxCountComment";

    // prefs name for jointly comment max letters
    static final String namePrefsCommentMaxCountJointlyLetters = "commentJointlyGoalMaxCountLetters";

    // prefs name for jointly comment count comments
    static final String namePrefsCommentCountJointlyComment = "commentJointlyGoalCountComment";

    // prefs name for jointly comment since date in mills
    static final String namePrefsJointlyCommentTimeSinceInMills = "jointlyGoalsCommentOurGoalsTimeSinceInMills";

    // prefs name for jointly comment delaytime
    static final String namePrefsJointlyCommentDelaytime = "jointlyGoalsCommentDelaytime";

    // prefs name for sharing jointly comments
    static final String namePrefsJointlyCommentShare = "jointlyCommentOurGoalsShare";

    // prefs name for store change time of goals sharing switch
    static final String namePrefsJointlyCommentShareChangeTime  = "jointlyCommentOurGoalsShareChangeTime";

    // prefs name for debetable comment max count comments
    static final String namePrefsCommentMaxCountDebetableComment = "commentDebetableGoalsOurGoalsMaxComment";

    // prefs name for debetable comment max count comments
    static final String namePrefsCommentMaxCountDebetableLetters = "commentDebetableGoalsOurGoalsMaxLetters";

    // prefs name for debetable comment count comments
    static final String namePrefsCommentCountDebetableComment = "commentDebetableGoalsOurGoalsCountComment";

    // prefs name for debetable comment since date in mills
    static final String namePrefsDebetableCommentTimeSinceInMills = "debetableGoalsCommentOurGoalsTimeSinceInMills";

    // prefs name for jointly comment delaytime
    static final String namePrefsDebetableCommentDelaytime = "debetableGoalsCommentDelaytime";

    // prefs name for sharing debetable comments
    static final String namePrefsDebetableCommentShare = "debetableCommentOurGoalsShare";

    // prefs name for store change time of goals sharing switch
    static final String namePrefsDebetableCommentShareChangeTime  = "debetableCommentOurGoalsShareChangeTime";

    // true -> debetable goals are updated; false -> no update
    static final String namePrefsSignalDebetableGoalsUpdate = "signalDebetableGoalsUpdated";

    // true -> jointly goals are updated; false -> no update
    static final String namePrefsSignalJointlyGoalsUpdate = "signalJointlyGoalsUpdated";

    // prefs name for start Date Evaluation in mills
    static final String namePrefsStartDateJointlyGoalsEvaluationInMills = "startDataJointlyGoalsEvaluationInMills";

    // prefs name for start Date Evaluation in mills
    static final String namePrefsEndDateJointlyGoalsEvaluationInMills = "endDataJointlyGoalsEvaluationInMills";

    // prefs name for evaluation jointly goal pause time in seconds
    static final String namePrefsEvaluateJointlyGoalsPauseTimeInSeconds = "evaluateJointlyGoalsPauseTimeInSeconds";

    // prefs name for evaluation jointly goal active time in seconds
    static final String namePrefsEvaluateJointlyGoalsActiveTimeInSeconds = "evaluateJointlyGoalsActivTimeInSeconds";

    // prefs name for jointly goals time start point evaluation in millis
    static final String namePrefsStartPointJointlyGoalsEvaluationPeriodInMills = "startPointJointlygoalsEvaluationPeriodInMills";

    // sort sequence of goals jointly comment list-> "decending" or "ascending"
    static final String namePrefsSortSequenceOfGoalsJointlyCommentList = "goalsJointlyCommentSortSequence";

    // sort sequence of goals debetable comment list-> "decending" or "ascending"
    static final String namePrefsSortSequenceOfGoalsDebetableCommentList = "goalsDebetableCommentSortSequence";

    // default time for active and pause time in evaluation jointly goal (time in seconds)
    static final int defaultTimeForActiveAndPauseEvaluationJointlyGoals = 43200; // this is 12 hours

    // prefs name for author of debetable goals
    static final String namePrefsAuthorOfDebetableGoals = "authorOfDebetableGoals";

    // constans for normal goal in change to
    static final String jointlyGoalStatusJointly = "jointly";

    // constans for debetable goal in change to
    static final String jointlyGoalStatusDebetable = "debetable";

    // constans for nothing in change to
    static final String jointlyGoalStatusNothing = "nothing";



}
