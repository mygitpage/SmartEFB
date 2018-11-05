package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 14.02.2017.
 */
class ConstansClassXmlParser {


    // xml name for master element smartefb
    static final String xmlNameForMasterElement = "smartEfb";

    // xml name for element main
    static final String xmlNameForMain = "main";

    // xml name for element main order
    static final String xmlNameForMain_Order = "main_order";

    // xml name for element main_ClientID
    static final String xmlNameForMain_ClientID = "clientid";

    // xml name for element error text
    static final String xmlNameForMain_ErrorText = "errortext";

    // xml name for element main client pin
    static final String xmlNameForMainClientPin = "clientpin";

    // xml name for element main actual app version (versus local app version of the installed app) -> comes from server
    static final String xmlNameForMain_ActualAppVersion = "actual_app_version";

    // xml name for element main this app version (versus server app version of the installed server) -> comes from app and described the actual installed app version
    static final String xmlNameForMain_ThisAppVersion = "this_app_version";

    // xml name for element main contact id
    static final String xmlNameForMain_ContactId = "contactid";

    // xml name for element main contact id
    static final String xmlNameForMain_ServerTime = "server_time";





    //
    // ++++++++++++++++++++++ Connect Book ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    // xml name for element connect book
    static final String xmlNameForConnectBook = "connectbook";

    // xml name for element connect book messages
    static final String xmlNameForConnectBook_Messages = "connectbook_messages";

    // xml name for element connect book settings
    static final String xmlNameForConnectBook_Settings = "connectbook_settings";

    // xml name for element connect book turn on/off
    static final String xmlNameForConnectBook_TurnOnOff = "connectbook_turnonoff";

    // xml name for element connect book order
    static final String xmlNameForConnectBook_Order = "connectbook_order";

    // xml name for element connect book send delay time
    static final String xmlNameForConnectBook_DelayTime = "connectbook_settings_delaytime";

    // xml name for element connect book max messages
    static final String xmlNameForConnectBook_MaxMessages = "connectbook_settings_maxmessages";

    // xml name for element connect book max letters
    static final String xmlNameForConnectBook_MaxLetters = "connectbook_settings_maxletters";

    // xml name for element connect book message sharing
    static final String xmlNameForConnectBook_MessageShare = "connectbook_settings_messageshare";

    // xml name for element connect book message
    static final String xmlNameForConnectBook_Message = "connectbook_message";

    // xml name for element connect book message author name
    static final String xmlNameForConnectBook_AuthorName = "connectbook_authorname";

    // xml name for element connect book message locale time
    static final String xmlNameForConnectBook_MessageLocaleTime = "connectbook_messagelocaletime";

    // xml name for element connect book message role
    static final String xmlNameForConnectBook_MessageRole = "connectbook_messagerole";

    //
    // ++++++++++++++++++++++ End Connect Book ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //



    //
    // ++++++++++++++++++++++ Our Arrangements ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    // xml name for element our arrangement
    static final String xmlNameForOurArrangement = "ourarrangement";

        // xml name for element our arrangement turn on/off
        static final String xmlNameForOurArrangement_TurnOnOff = "ourarrangement_turnonoff";

        // xml name for element our arrangement now
        static final String xmlNameForOurArrangement_Now = "ourarrangement_now";

            // xml name for element our arrangement order -> the order can be new, delete or update (see definitions below)
            static final String xmlNameForOurArrangement_Now_Order = "ourarrangement_now_order";

            // xml name for element our arrangement now arrangement text
            static final String xmlNameForOurArrangement_Now_ArrangementText = "ourarrangement_now_arrangementtext";

            // xml name for element our arrangement now author name
            static final String xmlNameForOurArrangement_Now_AuthorName = "ourarrangement_now_authorname";

            // xml name for element our arrangement now arrangement time
            static final String xmlNameForOurArrangement_Now_ArrangementTime = "ourarrangement_now_arrangementtime";

            // xml name for element our arrangement now sketch current
            static final String xmlNameForOurArrangement_Now_SketchCurrent = "ourarrangement_now_sketchcurrent";

            // xml name for element our arrangement block id
            static final String xmlNameForOurArrangement_Now_BlockId = "ourarrangement_now_blockid";

            // xml name for element our arrangement block id
            static final String xmlNameForOurArrangement_Now_ServerId = "ourarrangement_now_serverid";

            // xml name for element our arrangement change to
            static final String xmlNameForOurArrangement_Now_ChangeTo = "ourarrangement_now_changeto";

        // xml name for element our arrangement sketch
        static final String xmlNameForOurArrangement_Sketch = "ourarrangement_sketch";

        // xml name for element our arrangement turn on/off
        static final String xmlNameForOurArrangement_Sketch_TurnOnOff = "ourarrangement_sketch_turnonoff";

            // xml name for element our arrangement sketch order -> the order can be new, delete or update (see definitions below)
            static final String xmlNameForOurArrangement_Sketch_Order = "ourarrangement_sketch_order";

            // xml name for element our arrangement sketch arrangement text
            static final String xmlNameForOurArrangement_Sketch_ArrangementText = "ourarrangement_sketch_arrangementtext";

            // xml name for element our arrangement sketch author name
            static final String xmlNameForOurArrangement_Sketch_AuthorName = "ourarrangement_sketch_authorname";

            // xml name for element our arrangement sketch sketch current
            static final String xmlNameForOurArrangement_Sketch_SketchCurrent = "ourarrangement_sketch_sketchcurrent";

            // xml name for element our arrangement sketch sketch time
            static final String xmlNameForOurArrangement_Sketch_SketchTime = "ourarrangement_sketch_sketchtime";

            // xml name for element our arrangement block id
            static final String xmlNameForOurArrangement_Sketch_BlockId = "ourarrangement_sketch_blockid";

            // xml name for element our arrangement block id
            static final String xmlNameForOurArrangement_Sketch_ServerId = "ourarrangement_sketch_serverid";

            // xml name for element our arrangement change to
            static final String xmlNameForOurArrangement_Sketch_ChangeTo = "ourarrangement_sketch_changeto";

        // xml name for element our arrangement now comment
        static final String xmlNameForOurArrangement_NowComment = "ourarrangement_nowcomment";

        // xml name for element our arrangement now comment turn on/off
        static final String xmlNameForOurArrangement_NowComment_TurnOnOff = "ourarrangement_nowcomment_turnonoff";

            // xml name for element our arrangement now comment order
            static final String xmlNameForOurArrangement_NowComment_Order = "ourarrangement_nowcomment_order";

            // xml name for element our arrangement now comment comment
            static final String xmlNameForOurArrangement_NowComment_Comment = "ourarrangement_nowcomment_comment";

            // xml name for element our arrangement now comment author name
            static final String xmlNameForOurArrangement_NowComment_AuthorName = "ourarrangement_nowcomment_authorname";

            // xml name for element our arrangement now comment locale time
            static final String xmlNameForOurArrangement_NowComment_CommentLocaleTime = "ourarrangement_nowcomment_commentlocaletime";

            // xml name for element our arrangement now comment date of arrangement to comment
            static final String xmlNameForOurArrangement_NowComment_DateOfArrangement = "ourarrangement_nowcomment_dateofarrangement";

            // xml name for element our arrangement server id arrangement
            static final String xmlNameForOurArrangement_NowComment_ServerIdArrangement = "ourarrangement_nowcomment_serverid";

            // xml name for element our arrangement now comment block number for arrangements
            static final String xmlNameForOurArrangement_NowComment_BlockId = "ourarrangement_nowcomment_blockid";


        // xml name for element our arrangement sketch comment
        static final String xmlNameForOurArrangement_SketchComment = "ourarrangement_sketchcomment";

        // xml name for element our arrangement sketch comment turn on/off
        static final String xmlNameForOurArrangement_SketchComment_TurnOnOff = "ourarrangement_sketchcomment_turnonoff";

            // xml name for element our arrangement sketch comment order
            static final String xmlNameForOurArrangement_SketchComment_Order = "ourarrangement_sketchcomment_order";

            // xml name for element our arrangement sketch comment comment
            static final String xmlNameForOurArrangement_SketchComment_Comment = "ourarrangement_sketchcomment_comment";

            // xml name for element our arrangement sketch comment result question A
            static final String xmlNameForOurArrangement_SketchComment_ResultQuestionA = "ourarrangement_sketchcomment_resultquestion_a";

            // xml name for element our arrangement sketch comment result question B
            static final String xmlNameForOurArrangement_SketchComment_ResultQuestionB = "ourarrangement_sketchcomment_resultquestion_b";

            // xml name for element our arrangement sketch comment result question C
            static final String xmlNameForOurArrangement_SketchComment_ResultQuestionC = "ourarrangement_sketchcomment_resultquestion_c";

            // xml name for element our arrangement sketch comment author name
            static final String xmlNameForOurArrangement_SketchComment_AuthorName = "ourarrangement_sketchcomment_authorname";

            // xml name for element our arrangement sketch comment locale time
            static final String xmlNameForOurArrangement_SketchComment_CommentLocaleTime = "ourarrangement_sketchcomment_commentlocaletime";

            // xml name for element our arrangement sketch comment date of arrangement to comment
            static final String xmlNameForOurArrangement_SketchComment_DateOfArrangement = "ourarrangement_sketchcomment_dateofarrangement";

            // xml name for element our arrangement sketch comment server id arrangement
            static final String xmlNameForOurArrangement_SketchComment_ServerIdArrangement = "ourarrangement_sketchcomment_serverid";

            // xml name for element our arrangement sketch comment block number for sketch arrangements
            static final String xmlNameForOurArrangement_SketchComment_BlockId = "ourarrangement_sketchcomment_blockid";

        // xml name for element our arrangement evaluate
        static final String xmlNameForOurArrangement_Evaluate = "ourarrangement_evaluate";

        // xml name for element our arrangement evaluate turn on/off
        static final String xmlNameForOurArrangement_Evaluate_TurnOnOff = "ourarrangement_evaluate_turnonoff";

            // xml name for element our arrangement evaluate order
            static final String xmlNameForOurArrangement_Evaluate_Order = "ourarrangement_evaluate_order";

            // xml name for element our arrangement evaluate remarks
            static final String xmlNameForOurArrangement_Evaluate_Remarks = "ourarrangement_evaluate_remarks";

            // xml name for element our arrangement evaluate result question A
            static final String xmlNameForOurArrangement_Evaluate_ResultQuestionA = "ourarrangement_evaluate_resultquestion_a";

            // xml name for element our arrangement evaluate result question B
            static final String xmlNameForOurArrangement_Evaluate_ResultQuestionB = "ourarrangement_evaluate_resultquestion_b";

            // xml name for element our arrangement evaluate result question C
            static final String xmlNameForOurArrangement_Evaluate_ResultQuestionC = "ourarrangement_evaluate_resultquestion_c";

            // xml name for element our arrangement evaluate result question D
            static final String xmlNameForOurArrangement_Evaluate_ResultQuestionD = "ourarrangement_evaluate_resultquestion_d";

            // xml name for element our arrangement evalute result time
            static final String xmlNameForOurArrangement_Evaluate_LocaleTime = "ourarrangement_evaluate_localetime";

            // xml name for element our arrangement evalute start time
            static final String xmlNameForOurArrangement_Evaluate_StartTime = "ourarrangement_evaluate_starttime";

            // xml name for element our arrangement evalute end time
            static final String xmlNameForOurArrangement_Evaluate_EndTime = "ourarrangement_evaluate_endtime";

            // xml name for element our arrangement evaluate author name
            static final String xmlNameForOurArrangement_Evaluate_AuthorName = "ourarrangement_evaluate_authorname";

            // xml name for element our arrangement evaluate date of arrangement to evaluate
            static final String xmlNameForOurArrangement_Evaluate_DateOfArrangement = "ourarrangement_evaluate_dateofarrangement";

            // xml name for element our arrangement evaluate server id arrangement
            static final String xmlNameForOurArrangement_Evaluate_ServerIdArrangement = "ourarrangement_evaluate_serverid";

            // xml name for element our arrangement evaluate block number for arrangements
            static final String xmlNameForOurArrangement_Evaluate_BlockId = "ourarrangement_evaluate_blockid";

        // xml name for element our arrangement settings
        static final String xmlNameForOurArrangement_Settings = "ourarrangement_settings";

            // xml name for element our arrangement settings order
            static final String xmlNameForOurArrangement_Settings_Order = "ourarrangement_settings_order";

            // xml name for element our arrangement old turn on/off
            static final String xmlNameForOurArrangementOld_TurnOnOff = "ourarrangement_old_turnonoff";

            // xml name for element our arrangement settings evaluate pause time
            static final String xmlNameForOurArrangement_Settings_EvaluatePauseTime = "ourarrangement_settings_evaluatepausetime";

            // xml name for element our arrangement settings evaluate active time
            static final String xmlNameForOurArrangement_Settings_EvaluateActiveTime = "ourarrangement_settings_evaluateactivetime";

            // xml name for element our arrangement settings evaluate start date
            static final String xmlNameForOurArrangement_Settings_EvaluateStartDate = "ourarrangement_settings_evaluatestartdate";

            // xml name for element our arrangement settings evaluate end date
            static final String xmlNameForOurArrangement_Settings_EvaluateEndDate = "ourarrangement_settings_evaluateenddate";

            // xml name for element our arrangement settings comment max comment
            static final String xmlNameForOurArrangement_Settings_CommentMaxComment = "ourarrangement_settings_commentmaxcomment"; //value > 1000 -> no limitations with comments

            // xml name for element our arrangement settings comment max letters
            static final String xmlNameForOurArrangement_Settings_CommentMaxLetters = "ourarrangement_settings_commentmaxletters";

            // xml name for element our arrangement settings comment delaytime
            static final String xmlNameForOurArrangement_Settings_CommentDelaytime = "ourarrangement_settings_commentdelaytime";

            // xml name for element our arrangement settings comment count comment since time
            static final String xmlNameForOurArrangement_Settings_CommentCountCommentSinceTime = "ourarrangement_settings_commentcountcommentsincetime";

            // xml name for element our arrangement settings comment share
            static final String xmlNameForOurArrangement_Settings_CommentShare = "ourarrangement_settings_commentshare";

            // xml name for element our arrangement settings sketch comment max comment
            static final String xmlNameForOurArrangement_Settings_SketchCommentMaxComment = "ourarrangement_settings_sketchcommentmaxcomment"; //value > 1000 -> no limitations with comments

            // xml name for element our arrangement settings sketch comment max letters
            static final String xmlNameForOurArrangement_Settings_SketchCommentMaxLetters = "ourarrangement_settings_sketchcommentmaxletters";

            // xml name for element our arrangement settings sketch comment delaytime
            static final String xmlNameForOurArrangement_Settings_SketchCommentDelaytime = "ourarrangement_settings_sketchcommentdelaytime";

            // xml name for element our arrangement settings sketch comment count comment since time
            static final String xmlNameForOurArrangement_Settings_SketchCommentCountCommentSinceTime = "ourarrangement_settings_sketchcommentcountcommentsincetime";

            // xml name for element our arrangement settings comment share
            static final String xmlNameForOurArrangement_Settings_SketchCommentShare = "ourarrangement_settings_sketchcommentshare";


    //
    // ++++++++++++++++++++++ End Our Arrangements ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //

    //
    // ++++++++++++++++++++++ Our Goals ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    // xml name for element our goals
    static final String xmlNameForOurGoals = "ourgoals";

        // xml name for element our goals turn on/off
        static final String xmlNameForOurGoals_TurnOnOff = "ourgoals_turnonoff";

        // xml name for element our goals jointly now
        static final String xmlNameForOurGoals_JointlyNow = "ourgoals_jointly";

            // xml name for element our goals order -> the order can be new, delete or update (see definitions below)
            static final String xmlNameForOurGoals_JointlyNow_Order = "ourgoals_jointly_order";

            // xml name for element our goal jointly now goal text
            static final String xmlNameForOurGoals_JointlyNow_GoalText = "ourgoals_jointly_goaltext";

            // xml name for element our goal jointly now author name
            static final String xmlNameForOurGoals_JointlyNow_AuthorName = "ourgoals_jointly_authorname";

            // xml name for element our goal jointly now goal time
            static final String xmlNameForOurGoals_JointlyNow_GoalTime = "ourgoals_jointly_goaltime";

            // xml name for element our goal jointly now jointly/debetable
            static final String xmlNameForOurGoals_JointlyNow_JointlyDebetable = "ourgoals_jointly_debetablejointly";

            // xml name for element our goal jointly block id
            static final String xmlNameForOurGoals_JointlyNow_BlockId = "ourgoals_jointly_blockid";

            // xml name for element our goal jointly server id
            static final String xmlNameForOurGoals_JointlyNow_ServerId = "ourgoals_jointly_serverid";

            // xml name for element our goal jointly server id
            static final String xmlNameForOurGoals_JointlyNow_ChangeTo = "ourgoals_jointly_changeto";

        // xml name for element our goals debetable now
        static final String xmlNameForOurGoals_DebetableNow = "ourgoals_debetable";

        // xml name for element our goals debetable turn on/off
        static final String xmlNameForOurGoals_DebetableNow_TurnOnOff = "ourgoals_debetable_turnonoff";

            // xml name for element our goals order -> the order can be new, delete or update (see definitions below)
            static final String xmlNameForOurGoals_DebetableNow_Order = "ourgoals_debetable_order";

            // xml name for element our goal debetable now goal text
            static final String xmlNameForOurGoals_DebetableNow_GoalText = "ourgoals_debetable_goaltext";

            // xml name for element our goal debetable now author name
            static final String xmlNameForOurGoals_DebetableNow_AuthorName = "ourgoals_debetable_authorname";

            // xml name for element our goal debetable now goal time
            static final String xmlNameForOurGoals_DebetableNow_GoalTime = "ourgoals_debetable_goaltime";

            // xml name for element our goal debetable now jointly/debetable
            static final String xmlNameForOurGoals_DebetableNow_JointlyDebetable = "ourgoals_debetable_debetablejointly";

            // xml name for element our goal debetable now block id
            static final String xmlNameForOurGoals_DebetableNow_BlockId = "ourgoals_debetable_blockid";
        
            // xml name for element our goal debetable now server id
            static final String xmlNameForOurGoals_DebetableNow_ServerId = "ourgoals_debetable_serverid";
        
            // xml name for element our goal debetable now server id
            static final String xmlNameForOurGoals_DebetableNow_ChangeTo = "ourgoals_debetable_changeto";


        // xml name for element our goals evaluate
        static final String xmlNameForOurGoals_JointlyEvaluate = "ourgoals_evaluate";

        // xml name for element our goals evaluate turn on/off
        static final String xmlNameForOurGoals_JointlyEvaluate_TurnOnOff = "ourgoals_evaluate_turnonoff";

            // xml name for element our goals evaluate order
            static final String xmlNameForOurGoals_JointlyEvaluate_Order = "ourgoals_evaluate_order";

            // xml name for element our goals evaluate remarks
            static final String xmlNameForOurGoals_JointlyEvaluate_Remarks = "ourgoals_evaluate_remarks";

            // xml name for element our goals evaluate result question A
            static final String xmlNameForOurGoals_JointlyEvaluate_ResultQuestionA = "ourgoals_evaluate_resultquestion_a";

            // xml name for element our goals evaluate result question B
            static final String xmlNameForOurGoals_JointlyEvaluate_ResultQuestionB = "ourgoals_evaluate_resultquestion_b";

            // xml name for element our goals evaluate result question C
            static final String xmlNameForOurGoals_JointlyEvaluate_ResultQuestionC = "ourgoals_evaluate_resultquestion_c";

            // xml name for element our goals evaluate result question D
            static final String xmlNameForOurGoals_JointlyEvaluate_ResultQuestionD = "ourgoals_evaluate_resultquestion_d";

            // xml name for element our goals evalute result time
            static final String xmlNameForOurGoals_JointlyEvaluate_LocaleTime = "ourgoals_evaluate_localetime";

            // xml name for element our goals evalute start time
            static final String xmlNameForOurGoals_JointlyEvaluate_StartTime = "ourgoals_evaluate_starttime";

            // xml name for element our goals evalute end time
            static final String xmlNameForOurGoals_JointlyEvaluate_EndTime = "ourgoals_evaluate_endtime";

            // xml name for element our goals evaluate author name
            static final String xmlNameForOurGoals_JointlyEvaluate_AuthorName = "ourgoals_evaluate_authorname";

            // xml name for element our goals evaluate date of goals to evaluate
            static final String xmlNameForOurGoals_JointlyEvaluate_DateOfGoal = "ourgoals_evaluate_dateofgoal";

            // xml name for element our goals evaluate server id goals
            static final String xmlNameForOurGoals_JointlyEvaluate_ServerIdGoal = "ourgoals_evaluate_serverid";

            // xml name for element our goals evaluate block number for goals
            static final String xmlNameForOurGoals_JointlyEvaluate_BlockId = "ourgoals_evaluate_blockid";

    // xml name for element our goals jointly comment
        static final String xmlNameForOurGoals_JointlyComment = "ourgoals_jointlycomment";

        // xml name for element our goals jointly comment turn on/off
        static final String xmlNameForOurGoals_JointlyComment_TurnOnOff = "ourgoals_jointlycomment_turnonoff";

            // xml name for element our goals jointly comment order
            static final String xmlNameForOurGoals_JointlyComment_Order = "ourgoals_jointlycomment_order";

            // xml name for element our goals jointly comment comment
            static final String xmlNameForOurGoals_JointlyComment_Comment = "ourgoals_jointlycomment_comment";

            // xml name for element our goals jointly comment author name
            static final String xmlNameForOurGoals_JointlyComment_AuthorName = "ourgoals_jointlycomment_authorname";

            // xml name for element our goals jointly comment locale time
            static final String xmlNameForOurGoals_JointlyComment_CommentLocaleTime = "ourgoals_jointlycomment_commentlocaletime";

            // xml name for element our goals jointly comment id goal (on all smartphones must be the same!)
            static final String xmlNameForOurGoals_JointlyComment_ServerGoalId = "ourgoals_jointlycomment_serverid";

            // xml name for element our goals jointly comment date of jointly goal to comment
            static final String xmlNameForOurGoals_JointlyComment_DateOfJointlyGoal = "ourgoals_jointlycomment_dateofgoal";

            // xml name for element our goals jointly comment block number for goals
            static final String xmlNameForOurGoals_JointlyComment_BlockId = "ourgoals_jointlycomment_blockid";


        // xml name for element our goals debetable comment
        static final String xmlNameForOurGoals_DebetableComment = "ourgoals_debetablecomment";

        // xml name for element our goals debetable comment turn on/off
        static final String xmlNameForOurGoals_DebetableComment_TurnOnOff = "ourgoals_debetablecomment_turnonoff";

            // xml name for element our goals debetable comment order
            static final String xmlNameForOurGoals_DebetableComment_Order = "ourgoals_debetablecomment_order";

            // xml name for element our goals debetable comment comment
            static final String xmlNameForOurGoals_DebetableComment_Comment = "ourgoals_debetablecomment_comment";

            // xml name for element our goals debetable comment result question A
            static final String xmlNameForOurGoals_DebetableComment_ResultQuestionA = "ourgoals_debetablecomment_resultquestion_a";

            // xml name for element our goals debetable comment result question B
            static final String xmlNameForOurGoals_DebetableComment_ResultQuestionB = "ourgoals_debetablecomment_resultquestion_b";

            // xml name for element our goals debetable comment result question C
            static final String xmlNameForOurGoals_DebetableComment_ResultQuestionC = "ourgoals_debetablecomment_resultquestion_c";

            // xml name for element our goals debetable comment author name
            static final String xmlNameForOurGoals_DebetableComment_AuthorName = "ourgoals_debetablecomment_authorname";

            // xml name for element our goals debetable comment time
            static final String xmlNameForOurGoals_DebetableComment_CommentLocaleTime = "ourgoals_debetablecomment_commentlocaletime";

            // xml name for element our goals debetable comment date of debetable goal to comment
            static final String xmlNameForOurGoals_DebetableComment_DateOfDebetableGoal = "ourgoals_debetablecomment_dateofgoal";

            // xml name for element our goals debetable comment server id goal
            static final String xmlNameForOurGoals_DebetableComment_ServerIdGoal = "ourgoals_debetablecomment_serverid";

            // xml name for element our goals debetable comment block number for debetable goals
            static final String xmlNameForOurGoals_DebetableComment_BlockId = "ourgoals_debetablecomment_blockid";

    // xml name for element our goals settings
        static final String xmlNameForOurGoals_Settings = "ourgoals_settings";

            // xml name for element our goals settings order
            static final String xmlNameForOurGoals_Settings_Order = "ourgoals_settings_order";

            // xml name for element our goals old turn on/off
            static final String xmlNameForOurGoalsOld_TurnOnOff = "ourgoals_old_turnonoff";

            // xml name for element our goals settings evaluate pause time
            static final String xmlNameForOurGoals_Settings_EvaluatePauseTime = "ourgoals_settings_jointlyevaluatepausetime";

            // xml name for element our goals settings evaluate active time
            static final String xmlNameForOurGoals_Settings_EvaluateActiveTime = "ourgoals_settings_joontlyevaluateactivetime";

            // xml name for element our goals settings evaluate start date
            static final String xmlNameForOurGoals_Settings_EvaluateStartDate = "ourgoals_settings_jointlyevaluatestartdate";

            // xml name for element our goals settings evaluate end date
            static final String xmlNameForOurGoals_Settings_EvaluateEndDate = "ourgoals_settings_jointlyevaluateenddate";

            // xml name for element our goals settings comment max comment
            static final String xmlNameForOurGoals_Settings_CommentMaxComment = "ourgoals_settings_jointlycommentmaxcomment"; //value > 1000 -> no limitations with comments

            // xml name for element our goals settings comment max letters
            static final String xmlNameForOurGoals_Settings_CommentMaxLetters = "ourgoals_settings_jointlycommentmaxletters";

            // xml name for element our goals settings comment count comment since time
            static final String xmlNameForOurGoals_Settings_CommentCountCommentSinceTime = "ourgoals_settings_jointlycommentcountcommentsincetime";

            // xml name for element our goals settings debetable comment max comment
            static final String xmlNameForOurGoals_Settings_DebetableCommentMaxComment = "ourgoals_settings_debetablecommentmaxcomment"; //value > 1000 -> no limitations with comments

            // xml name for element our goals settings debetable comment max letters
            static final String xmlNameForOurGoals_Settings_DebetableCommentMaxLetters = "ourgoals_settings_debetablecommentmaxletters";

            // xml name for element our goals settings debetable comment count comment since time
            static final String xmlNameForOurGoals_Settings_DebetableCommentCountCommentSinceTime = "ourgoals_settings_debetablecommentcountcommentsincetime";

            // xml name for element our arrangement settings comment share
            static final String xmlNameForOurGoals_Settings_DebetableCommentShare = "ourgoals_settings_debetablecommentshare";

            // xml name for element our arrangement settings comment share
            static final String xmlNameForOurGoals_Settings_JointlyCommentShare = "ourgoals_settings_commentshare";

            // xml name for element our arrangement settings sketch comment delaytime
            static final String xmlNameForOurGoals_Settings_JointlyCommentDelaytime = "ourgoals_settings_commentdelaytime";

            // xml name for element our arrangement settings comment delaytime
            static final String xmlNameForOurGoals_Settings_DebetableCommentDelaytime = "ourgoals_settings_debetablecommentdelaytime";


    //
    // ++++++++++++++++++++++ End Our Goals ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //


    //
    // ++++++++++++++++++++++ Meeting ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    // xml name for element meeting

    static final String xmlNameForMeeting = "meeting";
    // xml name for element meeting settings
    static final String xmlNameForMeeting_Settings = "meeting_settings";

    // xml name for element meeting and suggestions
    static final String xmlNameForMeeting_And_Suggestions = "meeting_suggestions";

    // xml name for element meeting turn on/off
    static final String xmlNameForMeeting_TurnOnOff = "meeting_turnonoff";

    // xml name for element meeting client cancele meeting turn on/off
    static final String xmlNameForMeeting_ClientCancelMeeting_TurnOnOff = "client_cancele_meeting_turnonoff";

    // xml name for element meeting client make suggestions turn on/off
    static final String xmlNameForMeeting_ClientMakeSuggestion_TurnOnOff = "client_suggestion_possible_turnonoff";

    // xml name for element meeting client make meeting comment turn on/off
    static final String xmlNameForMeeting_ClientMakeSuggestionComment_TurnOnOff = "client_comment_suggestion_turnonoff";

    // xml name for element meeting settings order
    static final String xmlNameForMeeting_Settings_Order = "meeting_settings_order";

    // xml name for element meeting suggestion order
    static final String xmlNameForMeeting_Suggestion_Order = "meeting_suggestions_order";

    // xml name for element meeting suggestion date 1-6
    static final String xmlNameForMeeting_Suggestion_MettingDate1 = "meeting_date_1";
    static final String xmlNameForMeeting_Suggestion_MettingDate2 = "meeting_date_2";
    static final String xmlNameForMeeting_Suggestion_MettingDate3 = "meeting_date_3";
    static final String xmlNameForMeeting_Suggestion_MettingDate4 = "meeting_date_4";
    static final String xmlNameForMeeting_Suggestion_MettingDate5 = "meeting_date_5";
    static final String xmlNameForMeeting_Suggestion_MettingDate6 = "meeting_date_6";

    // xml name for element meeting suggestion place 1-6
    static final String xmlNameForMeeting_Suggestion_MettingPlace1 = "meeting_place_1";
    static final String xmlNameForMeeting_Suggestion_MettingPlace2 = "meeting_place_2";
    static final String xmlNameForMeeting_Suggestion_MettingPlace3 = "meeting_place_3";
    static final String xmlNameForMeeting_Suggestion_MettingPlace4 = "meeting_place_4";
    static final String xmlNameForMeeting_Suggestion_MettingPlace5 = "meeting_place_5";
    static final String xmlNameForMeeting_Suggestion_MettingPlace6 = "meeting_place_6";

    // xml name for element meeting suggestion votes 1-6
    static final String xmlNameForMeeting_Suggestion_MettingVote1 = "meeting_vote_1";
    static final String xmlNameForMeeting_Suggestion_MettingVote2 = "meeting_vote_2";
    static final String xmlNameForMeeting_Suggestion_MettingVote3 = "meeting_vote_3";
    static final String xmlNameForMeeting_Suggestion_MettingVote4 = "meeting_vote_4";
    static final String xmlNameForMeeting_Suggestion_MettingVote5 = "meeting_vote_5";
    static final String xmlNameForMeeting_Suggestion_MettingVote6 = "meeting_vote_6";
    static final String xmlNameForMeeting_Suggestion_VoteAuthor = "meeting_vote_author";
    static final String xmlNameForMeeting_Suggestion_VoteLocaleDate = "meeting_vote_locale_date";
    static final String xmlNameForMeeting_Meeting_FoundFromSuggestion_Author = "meeting_found_author";
    static final String xmlNameForMeeting_Meeting_FoundFromSuggestion_Date = "meeting_found_date";
    static final String xmlNameForMeeting_Suggestion_AuthorName = "meeting_authorname";
    static final String xmlNameForMeeting_Suggestion_CreationTime = "meeting_creation_time";
    static final String xmlNameForMeeting_Suggestion_Kategorie = "meeting_kategorie";
    static final String xmlNameForMeeting_Suggestion_ResponseTime = "meeting_responsetime";
    static final String xmlNameForMeeting_Suggestion_CoachHintText = "meeting_coach_hint_text";
    static final String xmlNameForMeeting_Suggestion_DataServerId = "meeting_client_server_id";
    static final String xmlNameForMeeting_Suggestion_CoachCanceleTime = "meeting_coach_canceled_time";
    static final String xmlNameForMeeting_Suggestion_CoachCanceleAuthor = "meeting_coach_canceled_author";
    static final String xmlNameForMeeting_Suggestion_ClientCommentAuthorName = "meeting_client_comment_authorname";
    static final String xmlNameForMeeting_Suggestion_ClientCommentLocaleTime = "meeting_client_comment_locale_time";
    static final String xmlNameForMeeting_Suggestion_ClientCommentText = "meeting_client_comment_text";
    static final String xmlNameForMeeting_SuggestionFromClient_StartDate = "suggestion_from_client_startdate";
    static final String xmlNameForMeeting_SuggestionFromClient_EndDate = "suggestion_from_client_enddate";
    static final String xmlNameForMeeting_SuggestionFromClient_Author = "suggestion_from_client_authorname";
    static final String xmlNameForMeeting_SuggestionFromClient_LocaleTime = "suggestion_from_client_locale_time";
    static final String xmlNameForMeeting_SuggestionFromClient_Text = "suggestion_from_client_text";
    static final String xmlNameForMeeting_Suggestion_ClientCanceledAuthorName = "meeting_client_canceled_authorrname";
    static final String xmlNameForMeeting_Suggestion_ClientCanceledLocaleTime = "meeting_client_canceled_locale_time";
    static final String xmlNameForMeeting_Suggestion_ClientCanceledText = "meeting_client_canceled_text";

    //
    // ++++++++++++++++++++++ End Meeting ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //

    //
    // ++++++++++++++++++++++ Main Settings ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    // xml name for element settings
    static final String xmlNameForSettings = "settings";

        // xml name for element settings order
        static final String xmlNameForSettings_Order = "settings_order";

        // xml name for element settings prevention turn on/off
        static final String xmlNameForSettings_Prevention_TurnOnOff = "settings_prevention_turnonoff";

        // xml name for element settings faq turn on/off
        static final String xmlNameForSettings_Faq_TurnOnOff = "settings_faq_turnonoff";

        // xml name for element settings emergency turn on/off
        static final String xmlNameForSettings_Emergency_TurnOnOff = "settings_emergency_turnonoff";

        // xml name for element settings settings turn on/off
        static final String xmlNameForSettings_Settings_TurnOnOff = "settings_settings_turnonoff";

        // xml name for element client name
        static final String xmlNameForSettings_ClientName = "settings_clientname";

        // xml name for element settings case close
        static final String xmlNameForSettings_CaseClose ="settings_caseclose";

    // xml name for element case involved person
    static final String xmlNameForSettings_CaseInvolvedPerson ="case_involved_person";

        // xml name for element case involved person order
        static final String xmlNameForSettings_CaseInvolvedPersonOrder ="case_involved_person_order";

        // xml name for element case involved person name
        static final String xmlNameForSettings_CaseInvolvedPersonName ="case_involved_person_name";

        // xml name for element case involved person function
        static final String xmlNameForSettings_CaseInvolvedPersonFunction ="case_involved_person_function";

        // xml name for element case involved person precense text one
        static final String xmlNameForSettings_CaseInvolvedPersonPrecenseOne ="case_involved_person_precense_one";

        // xml name for element case involved person precense text two
        static final String xmlNameForSettings_CaseInvolvedPersonPrecenseTwo ="case_involved_person_precense_two";

        // xml name for element case involved person precense text two start time
        static final String xmlNameForSettings_CaseInvolvedPersonPrecenseStart ="case_involved_person_precense_start";

        // xml name for element case involved person precense text two end time
        static final String xmlNameForSettings_CaseInvolvedPersonPrecenseEnd ="case_involved_person_precense_end";

        // xml name for element case involved person precense text two modified time
        static final String xmlNameForSettings_CaseInvolvedPersonModifiedTime ="case_involved_person_precense_modified";

    //
    // ++++++++++++++++++++++ End Main Settings ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //


    //
    // ++++++++++++++++++++++ Time Table ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    // xml name for element time table
    static final String xmlNameForTimeTable = "timetable";

    // xml name for element time table order
    static final String xmlNameForTimeTable_Order = "timetable_order";

    // xml name for element time table turn on/off
    static final String xmlNameForTimeTable_TurnOnOff = "timetable_turnonoff";

    // xml name for element time table value
    static final String xmlNameForTimeTable_Value = "timetable_value";

    // xml name for element time table modified date
    static final String xmlNameForTimeTable_Modified_Date = "timetable_modified_date";

    // xml name for element time table modified author
    static final String xmlNameForTimeTable_Modified_Author = "timetable_modified_author";

    //
    // ++++++++++++++++++++++ End Time Table ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //

    //
    // ++++++++++++++++++++++ Message ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    // xml name for element message
    static final String xmlNameForMessage = "message";

    // xml name for element message messages
    static final String xmlNameForMessage_Messages = "message_messages";

    // xml name for element message settings
    static final String xmlNameForMessage_Settings = "message_settings";

    // xml name for element message turn on/off
    static final String xmlNameForMessage_TurnOnOff = "message_turnonoff";

    // xml name for element message order
    static final String xmlNameForMessage_Order = "message_order";
    
    // xml name for element message max messages
    static final String xmlNameForMessage_MaxMessages = "message_settings_maxmessages";

    // xml name for element message count comment since time
    static final String xmlNameForMessage_MessagesCountSinceTime = "message_settings_countsincetime";

    // xml name for element message max letters
    static final String xmlNameForMessage_MaxLetters = "message_settings_maxletters";

    // xml name for element message stop communication
    static final String xmlNameForMessage_StopCommunication = "message_settings_stop";

    // xml name for element message text
    static final String xmlNameForMessage_MessageText = "message_text";

    // xml name for element message message author name
    static final String xmlNameForMessage_AuthorName = "message_authorname";

    // xml name for element message message locale time
    static final String xmlNameForMessage_MessageLocaleTime = "message_localetime";

    // xml name for element message message role
    static final String xmlNameForMessage_MessageRole = "message_role";

    //
    // ++++++++++++++++++++++ End Message ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //


    //
    // ++++++++++++++++++++++ Order Definitions ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    // Order definitions

    // xml name for element order "new"
    static final String xmlNameForOrder_New = "new";


    // xml name for element order "newAssocMessage" -> message with connect to server -> associated
    static final String xmlNameForOrder_NewAssociatedMessage = "newAssocMessage";

    // xml name for element order "updateAssocMessage" -> message with connect to server -> associated
    static final String xmlNameForOrder_UpdateAssociatedMessage = "updateAssocMessage";

    // xml name for element order "newNotAssocMessage" -> message without connection to server -> not associated
    static final String xmlNameForOrder_NewNotAssociatedMessage = "newNotAssocMessage";

    // xml name for element order "updateNotAssocMessage" -> message without connection to server -> not tassociated
    static final String xmlNameForOrder_UpdateNotAssociatedMessage = "updateNotAssocMessage";
    
    
    

    // xml name for element order "delete"
    static final String xmlNameForOrder_Delete = "delete";

    // xml name for element order "delete_all"
    static final String xmlNameForOrder_Delete_All = "delete_all";

    // xml name for element order "update"
    static final String xmlNameForOrder_Update = "update";

    // xml name for element order "init"
    static final String xmlNameForOrder_Init = "init";

    // xml name for element order "init error"
    static final String xmlNameForOrder_Error_Init = "error_init";

    // xml name for element order "init error"
    static final String xmlNameForOrder_Error_Communication = "error_communication";

    // xml name for element order "data"
    static final String xmlNameForOrder_Data = "data";

    // xml name for element order "receive ok and send"
    static final String xmlNameForOrder_Receive_Ok_Send = "receive_ok_send";

    // xml name for send ask new data from server
    static final String xmlNameForSendToServer_AskNewData = "asknewdata";

    // xml name for send now comment of arrangement to server
    static final String xmlNameForSendToServer_CommentArrangement = "sendarrangementcomment";

    // xml name for send comment of sketch arrangement to server
    static final String xmlNameForSendToServer_CommentSketchArrangement = "sendsketcharrangementcomment";

    // xml name for send evaluationresult arrangement to server
    static final String xmlNameForSendToServer_EvaluationResultArrangement = "sendevaluationresultarrangement";

    // xml name for send comment of jointly goal to server
    static final String xmlNameForSendToServer_JointlyGoalsComment = "sendjointlygoalscomment";

    // xml name for send comment of jointly goal to server
    static final String xmlNameForSendToServer_JointlyGoalsEvaluationResult = "sendevaluationresultgoals";

    // xml name for send comment of debetable goal to server
    static final String xmlNameForSendToServer_DebetableGoalsComment = "senddebetablegoalscomment";

    // xml name for send connect book message to server
    static final String xmlNameForSendToServer_ConnectBookMessage = "sendconnectbookmessage";

    // xml name for send message to server (associated)
    static final String xmlNameForSendToServer_MessageAssociated = "sendmessageassociated";

    // xml name for send message to server (not associated)
    static final String xmlNameForSendToServer_MessageNotAssociated = "sendmessagenotassociated";

    // xml name for send meeting data to server
    static final String xmlNameForSendToServer_MeetingData = "sendmeetingdata";

    // xml feature link for init xml serializer
    static final String xmlFeatureLink = "http://xmlpull.org/v1/doc/features.html#indent-output";

    //
    // ++++++++++++++++++++++ End Order Definitions ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //

}
