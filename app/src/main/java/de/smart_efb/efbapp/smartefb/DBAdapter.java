package de.smart_efb.efbapp.smartefb;


/**
 * Created by ich on 20.03.16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBAdapter extends SQLiteOpenHelper {

    Context dbContext;

    // DB name
    private static final String DATABASE_NAME = "efbDb";

    // Tables name
    private static final String DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT = "ourArrangementCommentTable";
    private static final String DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT = "ourArrangementSketchCommentTable";
    private static final String DATABASE_TABLE_OUR_ARRANGEMENT = "ourArrangementTable";
    private static final String DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE = "ourArrangementEvaluateTable";
    private static final String DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW = "ourGoalsDebetableJointlyGoalsNow";
    private static final String DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT = "ourGoalsJointlyGoalsComment";
    private static final String DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE = "ourGoalsJointlyGoalsEvaluate";
    private static final String DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT = "ourGoalsDebetableGoalsComment";
    private static final String DATABASE_TABLE_MEETING_SUGGESTION = "meetingSuggestion";
    private static final String DATABASE_TABLE_CHAT_MESSAGE = "chatMessageTable";
    private static final String DATABASE_TABLE_INVOLVED_PERSON = "involvedPersonTable";
    private static final String DATABASE_TABLE_MESSAGE = "messageTable";

    // Track DB version if a new version of your app changes the format.
    private static final int DATABASE_VERSION = 52;

    // Common column names
    public static final String KEY_ROWID = "_id";

    /**********************************************************************************************/
    /************************ Begin of table definitions **********************************************************************/

    // Our Arrangement - column names and numbers
    static final String OUR_ARRANGEMENT_KEY_ARRANGEMENT = "arrangement";
    static final String OUR_ARRANGEMENT_KEY_AUTHOR_NAME = "author_name";
    static final String OUR_ARRANGEMENT_KEY_WRITE_TIME = "arrangement_time";
    static final String OUR_ARRANGEMENT_KEY_NEW_ENTRY = "new_entry";
    static final String OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE = "eval_possible";
    static final String OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT = "sketch";
    static final String OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME = "sketch_time";
    static final String OUR_ARRANGEMENT_KEY_SERVER_ID = "serverid";
    static final String OUR_ARRANGEMENT_KEY_BLOCK_ID = "blockid";
    static final String OUR_ARRANGEMENT_KEY_CHANGE_TO = "change_to";
    static final String OUR_ARRANGEMENT_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message
    static final String OUR_ARRANGEMENT_KEY_LAST_EVAL_TIME = "last_eval_time";

    // All keys from table app settings in a String
    static final String[] OUR_ARRANGEMENT_ALL_KEYS = new String[]{KEY_ROWID, OUR_ARRANGEMENT_KEY_ARRANGEMENT, OUR_ARRANGEMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_KEY_NEW_ENTRY, OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE, OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT, OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME, OUR_ARRANGEMENT_KEY_SERVER_ID, OUR_ARRANGEMENT_KEY_BLOCK_ID, OUR_ARRANGEMENT_KEY_STATUS, OUR_ARRANGEMENT_KEY_CHANGE_TO, OUR_ARRANGEMENT_KEY_LAST_EVAL_TIME};

    // SQL String to create our arrangement table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_KEY_ARRANGEMENT + " TEXT not null, "
                    + OUR_ARRANGEMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_ARRANGEMENT_KEY_WRITE_TIME + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_SERVER_ID + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_BLOCK_ID + " TEXT not null, "
                    + OUR_ARRANGEMENT_KEY_STATUS + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_CHANGE_TO + " STRING not null, "
                    + OUR_ARRANGEMENT_KEY_LAST_EVAL_TIME + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Our Arrangement Comment- column names and numbers
    static final String OUR_ARRANGEMENT_COMMENT_KEY_COMMENT = "comment";
    static final String OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME = "author_name";
    static final String OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME = "comment_time";
    static final String OUR_ARRANGEMENT_COMMENT_KEY_UPLOAD_TIME = "upload_time";
    static final String OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME = "local_time";
    static final String OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID = "blockid";
    static final String OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY = "new_entry";
    static final String OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME = "arrangement_time";
    static final String OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT = "server_id";
    static final String OUR_ARRANGEMENT_COMMENT_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message
    static final String OUR_ARRANGEMENT_COMMENT_KEY_TIMER_STATUS = "timer_status";

    // All keys from table app settings in a String
    static final String[] OUR_ARRANGEMENT_COMMENT_ALL_KEYS = new String[]{KEY_ROWID, OUR_ARRANGEMENT_COMMENT_KEY_COMMENT, OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_COMMENT_KEY_UPLOAD_TIME, OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME, OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID, OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY, OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME, OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT, OUR_ARRANGEMENT_COMMENT_KEY_STATUS, OUR_ARRANGEMENT_COMMENT_KEY_TIMER_STATUS};

    // SQL String to create our arrangement comment table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT_COMMENT =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_COMMENT + " TEXT not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_UPLOAD_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID + " TEXT not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_STATUS + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_TIMER_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Our Arrangement Sketch Comment- column names and numbers

    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT = "comment";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1 = "result_q_a";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2 = "result_q_b";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3 = "result_q_c";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME = "author_name";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME = "comment_time";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_UPLOAD_TIME = "upload_time";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME = "local_time";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID = "blockid";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT = "server_id";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY = "new_entry";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME = "arrangement_time";
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message
    static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_TIMER_STATUS = "timer_status";

    // All keys from table sketch comment
    static final String[] OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS = new String[]{KEY_ROWID, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_UPLOAD_TIME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_TIMER_STATUS};

    // SQL String to create our arrangement sketch comment table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT_SKETCH_COMMENT =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT + " TEXT not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_UPLOAD_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID + " TEXT not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_TIMER_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Our Arrangement Evaluate- column names and numbers
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME = "arrangement_time";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_SERVER_ID_ARRANGEMENT = "server_id";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_BLOCKID = "block_id";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1 = "result_q_a";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2 = "result_q_b";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3 = "result_q_c";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4 = "result_q_d";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS = "result_remarks";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME = "result_time";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_LOCAL_TIME = "local_time";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME = "start_block_time";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME = "end_block_time";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME = "username";
    static final String OUR_ARRANGEMENT_EVALUATE_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message

    // All keys from table app settings in a String
    static final String[] OUR_ARRANGEMENT_EVALUATE_ALL_KEYS = new String[]{KEY_ROWID, OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_SERVER_ID_ARRANGEMENT, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_LOCAL_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME, OUR_ARRANGEMENT_EVALUATE_KEY_STATUS, OUR_ARRANGEMENT_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_BLOCKID};

    // SQL String to create our arrangement evaluate table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT_EVALUATE =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_SERVER_ID_ARRANGEMENT + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_BLOCKID + " STRING not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS + " TEXT not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_LOCAL_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME + " STRING not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Chat Messages - column names and numbers
    static final String CHAT_MESSAGE_KEY_WRITE_TIME = "message_time";
    static final String CHAT_MESSAGE_KEY_LOCAL_TIME = "local_time";
    static final String CHAT_MESSAGE_KEY_AUTHOR_NAME = "author_name";
    static final String CHAT_MESSAGE_KEY_MESSAGE = "message";
    static final String CHAT_MESSAGE_KEY_ROLE = "role_status";
    static final String CHAT_MESSAGE_KEY_UPLOAD_TIME = "upload_time";
    static final String CHAT_MESSAGE_KEY_STATUS = "status";
    static final String CHAT_MESSAGE_KEY_NEW_ENTRY = "new_entry";
    static final String CHAT_MESSAGE_KEY_TIMER_STATUS = "timer_status";

    // All keys from table chat messages in a String
    static final String[] CHAT_MESSAGE_ALL_KEYS = new String[]{KEY_ROWID, CHAT_MESSAGE_KEY_WRITE_TIME, CHAT_MESSAGE_KEY_LOCAL_TIME, CHAT_MESSAGE_KEY_AUTHOR_NAME, CHAT_MESSAGE_KEY_MESSAGE, CHAT_MESSAGE_KEY_ROLE, CHAT_MESSAGE_KEY_UPLOAD_TIME, CHAT_MESSAGE_KEY_STATUS, CHAT_MESSAGE_KEY_NEW_ENTRY, CHAT_MESSAGE_KEY_TIMER_STATUS};

    // SQL String to create chat-message-table
    private static final String DATABASE_CREATE_SQL_CHAT_MESSAGE =
            "create table " + DATABASE_TABLE_CHAT_MESSAGE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + CHAT_MESSAGE_KEY_WRITE_TIME + " INTEGER not null, "
                    + CHAT_MESSAGE_KEY_LOCAL_TIME + " INTEGER not null, "
                    + CHAT_MESSAGE_KEY_AUTHOR_NAME + " STRING not null, "
                    + CHAT_MESSAGE_KEY_MESSAGE + " TEXT not null, "
                    + CHAT_MESSAGE_KEY_ROLE + " INTEGER not null, "
                    + CHAT_MESSAGE_KEY_UPLOAD_TIME + " INTEGER, "
                    + CHAT_MESSAGE_KEY_STATUS + " INTEGER not null, "
                    + CHAT_MESSAGE_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + CHAT_MESSAGE_KEY_TIMER_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /*************************************************************************************************************************/
    /************************ Our Goals Definitions **************************************************************************/

    // Debetable/Jointly Goals Now - column names and numbers
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL = "goal";
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME = "author_name";
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME = "goal_time";
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY = "new_entry";
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE = "eval_possible";
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE = "jointlyDebetable";
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DEBETABLE_TIME = "debetable_time";
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID = "serverid";
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID = "blockid";
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_CHANGE_TO = "change_to";
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message
    static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_LAST_EVAL_TIME = "last_eval_time";

    // All keys in a String
    public static final String[] OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS = new String[]{KEY_ROWID, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DEBETABLE_TIME, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_CHANGE_TO, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_STATUS, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_LAST_EVAL_TIME};

    // SQL String to create our goals jointly goals now table
    private static final String DATABASE_CREATE_SQL_OUR_GOALS_DEBETABLE_JOINTLY_GOALS_NOW =
            "create table " + DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL + " TEXT not null, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME + " STRING not null, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DEBETABLE_TIME + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID + " TEXT not null, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_CHANGE_TO + " STRING not null, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_STATUS + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_LAST_EVAL_TIME + " INTEGER DEFAULT 0"
                    + ");";

    /************************ End Definitions Our Goals *********************************************************************/
    /**********************************************************************************************/
    // Our Goals Jointly Goals Comment- column names and numbers
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT = "comment";
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME = "author_name";
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME = "comment_time";
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_UPLOAD_TIME = "upload_time";
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_LOCAL_TIME = "local_time";
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID = "blockid";
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY = "new_entry";
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME = "goal_time";
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL = "server_id";
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message
    static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_TIMER_STATUS = "timer_status";

    // All keys from table app settings in a String
    static final String[] OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS = new String[]{KEY_ROWID, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_LOCAL_TIME, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_UPLOAD_TIME, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_TIMER_STATUS};

    // SQL String to create our goals jointly comment table
    private static final String DATABASE_CREATE_SQL_OUR_GOALS_JOINTLY_GOALS_COMMENT =
            "create table " + DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT + " TEXT not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_UPLOAD_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_LOCAL_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID + " TEXT not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_TIMER_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Our Goals Jointly Goals Evaluate- column names and numbers
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_GOAL_TIME = "goal_time";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_SERVER_ID = "server_id";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_BLOCKID = "block_id";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION1 = "result_q_a";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION2 = "result_q_b";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION3 = "result_q_c";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION4 = "result_q_d";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_REMARKS = "result_remarks";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME = "start_block_time";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME = "end_block_time";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_TIME = "result_time";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_LOCAL_TIME = "local_time";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_USERNAME = "username";
    static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message

    // All keys from table app settings in a String
    static final String[] OUR_GOALS_JOINTLY_GOALS_EVALUATE_ALL_KEYS = new String[]{KEY_ROWID, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_GOAL_TIME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION1, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION2, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION3, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION4, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_REMARKS, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_TIME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_LOCAL_TIME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_USERNAME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_STATUS, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_SERVER_ID, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_BLOCKID};

    // SQL String to create our jointly goals evaluate table
    private static final String DATABASE_CREATE_SQL_OUR_GOALS_JOINTLY_GOALS_EVALUATE =
            "create table " + DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_GOAL_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_SERVER_ID + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_BLOCKID + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION1 + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION2 + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION3 + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION4 + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_REMARKS + " TEXT not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_LOCAL_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_USERNAME + " STRING not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Our Goals Debetable Goals Comment- column names and numbers
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT = "comment";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1 = "result_q_a";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION2 = "result_q_b";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION3 = "result_q_c";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME = "author_name";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME = "comment_time";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_UPLOAD_TIME = "upload_time";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME = "local_time";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID = "blockid";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID = "server_id";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY = "new_entry";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME = "goal_time";
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message
    static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_TIMER_STATUS = "timer_status";

    // All keys from table app settings in a String
    static final String[] OUR_GOALS_DEBETABLE_GOALS_COMMENT_ALL_KEYS = new String[]{KEY_ROWID, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION2, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION3, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_UPLOAD_TIME, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_TIMER_STATUS};

    // SQL String to create our goals debetable comment table
    private static final String DATABASE_CREATE_SQL_OUR_GOALS_DEBETABLE_GOALS_COMMENT =
            "create table " + DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1 + " INTEGER not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION2 + " INTEGER not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION3 + " INTEGER not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT + " TEXT not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_UPLOAD_TIME + " INTEGER not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME + " INTEGER not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID + " TEXT not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID + " INTEGER not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME + " INTEGER not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_TIMER_STATUS + " INTEGER DEFAULT 0"
                    + ");";


    /**********************************************************************************************/
    /**********************************************************************************************/
    // Meeting And Suggestion - column names and numbers

    static final String MEETING_SUGGESTION_KEY_DATE1 = "date1";
    static final String MEETING_SUGGESTION_KEY_DATE2 = "date2";
    static final String MEETING_SUGGESTION_KEY_DATE3 = "date3";
    static final String MEETING_SUGGESTION_KEY_DATE4 = "date4";
    static final String MEETING_SUGGESTION_KEY_DATE5 = "date5";
    static final String MEETING_SUGGESTION_KEY_DATE6 = "date6";
    static final String MEETING_SUGGESTION_KEY_PLACE1 = "place1";
    static final String MEETING_SUGGESTION_KEY_PLACE2 = "place2";
    static final String MEETING_SUGGESTION_KEY_PLACE3 = "place3";
    static final String MEETING_SUGGESTION_KEY_PLACE4 = "place4";
    static final String MEETING_SUGGESTION_KEY_PLACE5 = "place5";
    static final String MEETING_SUGGESTION_KEY_PLACE6 = "place6";
    static final String MEETING_SUGGESTION_KEY_VOTE1 = "vote1";
    static final String MEETING_SUGGESTION_KEY_VOTE2 = "vote2";
    static final String MEETING_SUGGESTION_KEY_VOTE3 = "vote3";
    static final String MEETING_SUGGESTION_KEY_VOTE4 = "vote4";
    static final String MEETING_SUGGESTION_KEY_VOTE5 = "vote5";
    static final String MEETING_SUGGESTION_KEY_VOTE6 = "vote6";
    static final String MEETING_SUGGESTION_KEY_VOTEDATE = "vote_date";
    static final String MEETING_SUGGESTION_KEY_VOTELOCALEDATE = "vote_locale_date";
    static final String MEETING_SUGGESTION_KEY_VOTEAUTHOR = "vote_author";
    static final String MEETING_SUGGESTION_KEY_MEETING_CANCELED = "meeting_canceled"; // 0=not canceled; 1= meeting canceled
    static final String MEETING_SUGGESTION_KEY_MEETING_CANCELED_AUTHOR = "meeting_canceled_author";
    static final String MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME = "meeting_canceled_time";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED = "meeting_client_canceled"; // 0=not canceled; 1= meeting canceled by client
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_AUTHOR = "meeting_client_canceled_author";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME = "meeting_client_canceled_time";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_LOCALE_TIME = "meeting_client_canceled_locale_time";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TEXT = "meeting_client_canceled_text";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT = "meeting_client_suggestion";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_AUTHOR = "meeting_client_suggestion_author";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TIME = "meeting_client_suggestion_time";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_LOCALE_TIME = "meeting_client_suggestion_locale_time";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE = "meeting_client_suggestion_startdate";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE = "meeting_client_suggestion_enddate";
    static final String MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME = "meeting_response_time";
    static final String MEETING_SUGGESTION_KEY_MEETING_RESPONSE_START_TIME = "meeting_response_start_time";
    static final String MEETING_SUGGESTION_KEY_SUGGESTION_FOUND = "suggestion_found";
    static final String MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_AUTHOR = "suggestion_found_author";
    static final String MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_DATE = "suggestion_found_date";
    static final String MEETING_SUGGESTION_KEY_MEETING_COACH_HINT_TEXT = "meeting_coach_hint";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_AUTHOR = "meeting_client_comment_author";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_DATE = "meeting_client_comment_date";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_LOCALE_DATE = "meeting_client_comment_locale_date";
    static final String MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_TEXT = "meeting_client_comment_text";
    static final String MEETING_SUGGESTION_KEY_MEETING_SERVER_ID = "meeting_server_id";
    static final String MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME = "meeting_upload_time";
    static final String MEETING_SUGGESTION_KEY_MEETING_KATEGORIE = "meeting_kategorie"; // 0 = nothing; 1 = dates are meeting dates; 2 = dates are meeting suggestions; 3 = responses from client for coach suggestion; 4 = suggestion from client; 5 = client comment suggestion
    static final String MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME = "meeting_creation_time";
    static final String MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR = "meeting_creation_author";
    static final String MEETING_SUGGESTION_MEETING_KEY_STATUS = "status"; // 0=ready to send, 1=meeting/suggestion send, 4=external message
    static final String MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST = "new_mett_suggest"; // 1=new meeting/ suggestion
    static final String MEETING_SUGGESTION_KEY_REMEMBER_POINT = "remember_point"; //
    static final String MEETING_SUGGESTION_KEY_TIMER_STATUS = "timer_status";
    static final String MEETING_SUGGESTION_KEY_UPDATE_ORDER = "update_order"; // after send to server some values (wrtite_time, etc.) must be corrected

    // All keys from table in a String
    static final String[] MEETING_SUGGESTION_MEETING_ALL_KEYS = new String[]{KEY_ROWID, MEETING_SUGGESTION_KEY_DATE1, MEETING_SUGGESTION_KEY_DATE2, MEETING_SUGGESTION_KEY_DATE3, MEETING_SUGGESTION_KEY_DATE4, MEETING_SUGGESTION_KEY_DATE5, MEETING_SUGGESTION_KEY_DATE6,
            MEETING_SUGGESTION_KEY_PLACE1, MEETING_SUGGESTION_KEY_PLACE2, MEETING_SUGGESTION_KEY_PLACE3, MEETING_SUGGESTION_KEY_PLACE4, MEETING_SUGGESTION_KEY_PLACE5, MEETING_SUGGESTION_KEY_PLACE6,
            MEETING_SUGGESTION_KEY_VOTE1, MEETING_SUGGESTION_KEY_VOTE2, MEETING_SUGGESTION_KEY_VOTE3, MEETING_SUGGESTION_KEY_VOTE4, MEETING_SUGGESTION_KEY_VOTE5, MEETING_SUGGESTION_KEY_VOTE6, MEETING_SUGGESTION_KEY_VOTEDATE, MEETING_SUGGESTION_KEY_VOTELOCALEDATE, MEETING_SUGGESTION_KEY_VOTEAUTHOR,
            MEETING_SUGGESTION_KEY_SUGGESTION_FOUND, MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_AUTHOR, MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_DATE, MEETING_SUGGESTION_KEY_MEETING_CANCELED, MEETING_SUGGESTION_KEY_MEETING_CANCELED_AUTHOR, MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME, MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED,
            MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_AUTHOR, MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME, MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_LOCALE_TIME, MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TEXT, MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT, MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_AUTHOR,
            MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TIME, MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_LOCALE_TIME, MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE, MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE, MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME, MEETING_SUGGESTION_KEY_MEETING_RESPONSE_START_TIME, MEETING_SUGGESTION_KEY_MEETING_COACH_HINT_TEXT, MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_AUTHOR, MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_DATE,
            MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_LOCALE_DATE, MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_TEXT, MEETING_SUGGESTION_KEY_MEETING_SERVER_ID, MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME, MEETING_SUGGESTION_KEY_MEETING_KATEGORIE, MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME,
            MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR, MEETING_SUGGESTION_MEETING_KEY_STATUS, MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST, MEETING_SUGGESTION_KEY_REMEMBER_POINT, MEETING_SUGGESTION_KEY_TIMER_STATUS, MEETING_SUGGESTION_KEY_UPDATE_ORDER};

    // SQL String to create find meeting table
    private static final String DATABASE_CREATE_SQL_MEETING_SUGGESTION =
            "create table " + DATABASE_TABLE_MEETING_SUGGESTION + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + MEETING_SUGGESTION_KEY_DATE1 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_DATE2 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_DATE3 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_DATE4 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_DATE5 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_DATE6 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_PLACE1 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_PLACE2 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_PLACE3 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_PLACE4 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_PLACE5 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_PLACE6 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_VOTE1 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_VOTE2 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_VOTE3 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_VOTE4 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_VOTE5 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_VOTE6 + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_VOTEAUTHOR + " STRING not null, "
                    + MEETING_SUGGESTION_KEY_VOTEDATE + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_VOTELOCALEDATE + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_AUTHOR + " STRING not null, "
                    + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_DATE + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CANCELED + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CANCELED_AUTHOR + " STRING not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_AUTHOR + " STRING not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_LOCALE_TIME + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TEXT + " STRING not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT + " STRING not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_AUTHOR + " STRING not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TIME + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_LOCALE_TIME + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_START_TIME + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_COACH_HINT_TEXT + " STRING not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_AUTHOR + " STRING not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_DATE + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_LOCALE_DATE + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_TEXT + " STRING not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_SERVER_ID + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME + " INTEGER not null, "
                    + MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR + " STRING not null, "
                    + MEETING_SUGGESTION_MEETING_KEY_STATUS + " INTEGER DEFAULT 0, "
                    + MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST + " INTEGER DEFAULT 0, "
                    + MEETING_SUGGESTION_KEY_REMEMBER_POINT + " INTEGER DEFAULT 0, "
                    + MEETING_SUGGESTION_KEY_TIMER_STATUS + " INTEGER DEFAULT 0, "
                    + MEETING_SUGGESTION_KEY_UPDATE_ORDER + " STRING not null "
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // involved person- column names and numbers
    static final String INVOLVED_PERSON_KEY_NAME = "name";
    static final String INVOLVED_PERSON_KEY_FUNCTION = "function";
    static final String INVOLVED_PERSON_KEY_PRESENCE_TEXT_ONE = "precense_one";
    static final String INVOLVED_PERSON_KEY_PRESENCE_TEXT_TWO = "precense_two";
    static final String INVOLVED_PERSON_KEY_PRESENCE_TWO_START = "precense_two_start";
    static final String INVOLVED_PERSON_KEY_PRESENCE_TWO_END = "precense_two_end";
    static final String INVOLVED_PERSON_KEY_MODIFIED_TIME = "modified_time";
    static final String INVOLVED_PERSON_KEY_NEW_ENTRY = "new_entry";

    // All keys from table involved person in a String
    static final String[] INVOLVED_PERSON_ALL_KEYS = new String[]{KEY_ROWID, INVOLVED_PERSON_KEY_NAME, INVOLVED_PERSON_KEY_FUNCTION, INVOLVED_PERSON_KEY_PRESENCE_TEXT_ONE, INVOLVED_PERSON_KEY_PRESENCE_TEXT_TWO, INVOLVED_PERSON_KEY_PRESENCE_TWO_START, INVOLVED_PERSON_KEY_PRESENCE_TWO_END, INVOLVED_PERSON_KEY_MODIFIED_TIME, INVOLVED_PERSON_KEY_NEW_ENTRY};

    // SQL String to create involved person table
    private static final String DATABASE_CREATE_SQL_INVOLVED_PERSON =
            "create table " + DATABASE_TABLE_INVOLVED_PERSON + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + INVOLVED_PERSON_KEY_NAME + " STRING not null, "
                    + INVOLVED_PERSON_KEY_FUNCTION + " STRING not null, "
                    + INVOLVED_PERSON_KEY_PRESENCE_TEXT_ONE + " TEXT not null, "
                    + INVOLVED_PERSON_KEY_PRESENCE_TEXT_TWO + " TEXT not null, "
                    + INVOLVED_PERSON_KEY_PRESENCE_TWO_START + " INTEGER not null, "
                    + INVOLVED_PERSON_KEY_PRESENCE_TWO_END + " INTEGER not null, "
                    + INVOLVED_PERSON_KEY_MODIFIED_TIME + " INTEGER not null, "
                    + INVOLVED_PERSON_KEY_NEW_ENTRY + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Message - column names and numbers
    static final String MESSAGE_KEY_WRITE_TIME = "message_time";
    static final String MESSAGE_KEY_LOCAL_TIME = "local_time";
    static final String MESSAGE_KEY_AUTHOR_NAME = "author_name";
    static final String MESSAGE_KEY_MESSAGE = "message";
    static final String MESSAGE_KEY_ROLE = "role_status";
    static final String MESSAGE_KEY_UPLOAD_TIME = "upload_time";
    static final String MESSAGE_KEY_STATUS = "status";
    static final String MESSAGE_KEY_NEW_ENTRY = "new_entry";
    static final String MESSAGE_KEY_ANONYMOUS = "anonymous"; //(0= anonymous; 1= not anonymous)
    static final String MESSAGE_KEY_SOURCE = "source"; //(the source of the message, like arrangement comment, goal comment, etc)

    // All keys from table message in a String
    static final String[] MESSAGE_ALL_KEYS = new String[]{KEY_ROWID, MESSAGE_KEY_WRITE_TIME, MESSAGE_KEY_LOCAL_TIME, MESSAGE_KEY_AUTHOR_NAME, MESSAGE_KEY_MESSAGE, MESSAGE_KEY_ROLE, MESSAGE_KEY_UPLOAD_TIME, MESSAGE_KEY_STATUS, MESSAGE_KEY_NEW_ENTRY, MESSAGE_KEY_ANONYMOUS, MESSAGE_KEY_SOURCE};

    // SQL String to create message table
    private static final String DATABASE_CREATE_SQL_MESSAGE =
            "create table " + DATABASE_TABLE_MESSAGE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + MESSAGE_KEY_WRITE_TIME + " INTEGER not null, "
                    + MESSAGE_KEY_LOCAL_TIME + " INTEGER not null, "
                    + MESSAGE_KEY_AUTHOR_NAME + " STRING not null, "
                    + MESSAGE_KEY_MESSAGE + " TEXT not null, "
                    + MESSAGE_KEY_ROLE + " INTEGER not null, "
                    + MESSAGE_KEY_UPLOAD_TIME + " INTEGER, "
                    + MESSAGE_KEY_STATUS + " INTEGER not null, "
                    + MESSAGE_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + MESSAGE_KEY_ANONYMOUS + " INTEGER DEFAULT 0, "
                    + MESSAGE_KEY_SOURCE + " TEXT not null"
                    + ");";

    /*************************************************************************************************************************/
    /************************ End of table definitions **********************************************************************/


    //construtor of DBAdapter
    DBAdapter(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        dbContext = context;
    }


    @Override
    // create the tables
    public void onCreate(SQLiteDatabase _db) {

        // Create table OurArrangementComment
        _db.execSQL(DATABASE_CREATE_SQL_OUR_ARRANGEMENT_COMMENT);

        // Create table OurArrangementSketchComment
        _db.execSQL(DATABASE_CREATE_SQL_OUR_ARRANGEMENT_SKETCH_COMMENT);

        // Create table OurArrangement
        _db.execSQL(DATABASE_CREATE_SQL_OUR_ARRANGEMENT);

        // Create table OurArrangementEvaluate
        _db.execSQL(DATABASE_CREATE_SQL_OUR_ARRANGEMENT_EVALUATE);

        // Create table ChatMessage
        _db.execSQL(DATABASE_CREATE_SQL_CHAT_MESSAGE);

        // Create table Our Goals Debetable/Jointly Goals Now
        _db.execSQL(DATABASE_CREATE_SQL_OUR_GOALS_DEBETABLE_JOINTLY_GOALS_NOW);

        // Create table Our Goals Jointly Goals Comment
        _db.execSQL(DATABASE_CREATE_SQL_OUR_GOALS_JOINTLY_GOALS_COMMENT);

        // Create table Our Goals Jointly Goals Evaluate
        _db.execSQL(DATABASE_CREATE_SQL_OUR_GOALS_JOINTLY_GOALS_EVALUATE);

        // Create table Our Goals Debetable Goals Comment
        _db.execSQL(DATABASE_CREATE_SQL_OUR_GOALS_DEBETABLE_GOALS_COMMENT);

        // Create table Meeting
        _db.execSQL(DATABASE_CREATE_SQL_MEETING_SUGGESTION);

        // Create table involved person
        _db.execSQL(DATABASE_CREATE_SQL_INVOLVED_PERSON);

        // Create table message
        _db.execSQL(DATABASE_CREATE_SQL_MESSAGE);
    }


    @Override
    // on upgrade -> delete the tables when exits
    public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {

        // do update work here



        // only for debbuging
        /*
        // Destroy table OurArrangementComment
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT);

        // Destroy table OurArrangementSketchComment
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT);

        // Destroy table OurArrangement
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OUR_ARRANGEMENT);

        // Destroy table OurArrangementEvaluate
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE);

        // Destroy table ChatMessage
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CHAT_MESSAGE);

        // Destroy table Our Goals Debetable/Jointly Goals Now
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW);

        // Destroy table Our Goals Jointly Goals Comment
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT);

        // Destroy table Our Goals Jointly Goals Evaluate
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE);

        // Destroy table Our Goals Jointly Goals Evaluate
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT);

        // Destroy table Meeting and Suggestion
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MEETING_SUGGESTION);

        // Destroy table Meeting and Suggestion
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_INVOLVED_PERSON);

        // Destroy table Message
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MESSAGE);

        // Recreate new database:
        onCreate(_db);
        */
    }


    /********************************* TABLES FOR FUNCTION: Chat Message  ******************************************/

    // Add a new set of values to the database.
    long insertRowChatMessage(String author_name, long localeTime, long writeTime, String message, int role, int status, Boolean newEntry, long upload_time, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(CHAT_MESSAGE_KEY_AUTHOR_NAME, author_name);
        initialValues.put(CHAT_MESSAGE_KEY_WRITE_TIME, writeTime);
        initialValues.put(CHAT_MESSAGE_KEY_LOCAL_TIME, localeTime);
        initialValues.put(CHAT_MESSAGE_KEY_MESSAGE, message);
        initialValues.put(CHAT_MESSAGE_KEY_ROLE, role); //(role: 0= left; 1= right; 2= center;)
        initialValues.put(CHAT_MESSAGE_KEY_STATUS, status);
        initialValues.put(CHAT_MESSAGE_KEY_UPLOAD_TIME, upload_time);
        initialValues.put(CHAT_MESSAGE_KEY_TIMER_STATUS, timerStatus);


        // is it a new entry?
        if (newEntry) {
            initialValues.put(CHAT_MESSAGE_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(CHAT_MESSAGE_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_CHAT_MESSAGE, null, initialValues);

    }


    // Delete a row from the database, by rowId (primary key)
    boolean deleteRowChatMessage(long rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE_CHAT_MESSAGE, where, null) != 0;
    }


    void deleteAllChatMessage() {

        Cursor c = getAllRowsChatMessage();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);

        if (c.moveToFirst()) {
            do {
                deleteRowChatMessage(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }


    // Return all data in the database. chat messages sorted by write time ASC
    Cursor getAllRowsChatMessage() {

        SQLiteDatabase db = this.getWritableDatabase();

        // sort string
        String sort = CHAT_MESSAGE_KEY_WRITE_TIME + " ASC";

        Cursor c = db.query(true, DATABASE_TABLE_CHAT_MESSAGE, CHAT_MESSAGE_ALL_KEYS,
                null, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    // Get a specific row (by rowId)
    Cursor getOneRowChatMessage(long rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        Cursor c = db.query(true, DATABASE_TABLE_CHAT_MESSAGE, CHAT_MESSAGE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // update status message in table connectBookMessage
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external message
    boolean updateStatusConnectBookMessage(Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(CHAT_MESSAGE_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_CHAT_MESSAGE, newValues, where, null) != 0;
    }


    // Get all connect book messages with status = 0 (Ready to send) and role = 1 (own messages)
    Cursor getAllReadyToSendConnectBookMessages() {

        SQLiteDatabase db = this.getWritableDatabase();

        // status = 0 and role = 1
        String where = CHAT_MESSAGE_KEY_STATUS + "=0 AND " + CHAT_MESSAGE_KEY_ROLE + "=1";

        Cursor c = db.query(true, DATABASE_TABLE_CHAT_MESSAGE, CHAT_MESSAGE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return cursor
        return c;
    }


    // Get the number of new rows in connect book message
    int getCountNewEntryConnectBookMessage() {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = CHAT_MESSAGE_KEY_NEW_ENTRY + "=1";

        Cursor c = db.query(true, DATABASE_TABLE_CHAT_MESSAGE, CHAT_MESSAGE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // delete status new entry in table connect book for message rowId.
    boolean deleteStatusNewEntryConnectBookMessage(int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(CHAT_MESSAGE_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_CHAT_MESSAGE, newValues, where, null) != 0;
    }


    // update status of timer for message
    // 0= timer can run; 1= timer finish!
    boolean updateTimerStatusConnectBookMessage(Long rowId, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(CHAT_MESSAGE_KEY_TIMER_STATUS, timerStatus);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_CHAT_MESSAGE, newValues, where, null) != 0;
    }


    // update write time for message from locale time to server time in table connectBookMessage
    boolean updateWriteTimeConnectBookMessage (Long rowId, Long writeTimeFromServer) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row write time from server
        ContentValues newValues = new ContentValues();
        newValues.put(CHAT_MESSAGE_KEY_WRITE_TIME, writeTimeFromServer);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_CHAT_MESSAGE, newValues, where, null) != 0;
    }

    /********************************* End!! TABLES FOR FUNCTION: Chat Message ******************************************/
    /********************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Our Arrangement ******************************************/

    /* Add a new set of values (arrangement) to ourArrangement
        arrangement -> text of arrangement
        authorName -> name of author
        arrangementTime -> date and time of actual arrangement (not sketch arrangement)
        newEntry -> true, arrangement is new in database; false, it is old!
        sketchCurrent -> true, arrangement is a sketch; false, arrangement is an actual arrangement
        sketchTime -> date and time of sketch arrangement (not actual arrangement)
        serverId -> the id of arrangement on the server
        blockId -> the id of a block of arrangement
        changeTo -> needed when a arrangement is switch from now arrangement to sketch arrangement or visversa
        status -> the arragement status 0=ready to send, 1=message send, 4=external message
     */
    long insertRowOurArrangement(String arrangement, String authorName, long arrangementTime, Boolean newEntry, Boolean sketchCurrent, long sketchTime, int status, int serverId, String blockId, String changeTo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_KEY_ARRANGEMENT, arrangement);
        initialValues.put(OUR_ARRANGEMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_ARRANGEMENT_KEY_SERVER_ID, serverId); // id of arrangement on the server -> unique identifier for arrangement
        initialValues.put(OUR_ARRANGEMENT_KEY_BLOCK_ID, blockId); // id of the block of arrangement -> current block number is safe in prefs to find the block in db
        initialValues.put(OUR_ARRANGEMENT_KEY_CHANGE_TO, changeTo); // possible values are "nothing", "normal" and "sketch" (look for definitions in ConstansClassOurArrangement
        initialValues.put(OUR_ARRANGEMENT_KEY_STATUS, status);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(OUR_ARRANGEMENT_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(OUR_ARRANGEMENT_KEY_NEW_ENTRY, 0);
        }

        // is it a sketch? sketchCurrent-> true!
        if (sketchCurrent) {
            initialValues.put(OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT, 1);
            initialValues.put(OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME, sketchTime);
            initialValues.put(OUR_ARRANGEMENT_KEY_WRITE_TIME, 0);
        } else {
            initialValues.put(OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT, 0);
            initialValues.put(OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME, 0);
            initialValues.put(OUR_ARRANGEMENT_KEY_WRITE_TIME, arrangementTime);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_ARRANGEMENT, null, initialValues);
    }


    // Change an existing row of arrangement to be equal to serverId.
    boolean updateRowOurArrangement(String arrangement, String authorName, long arrangementTime, Boolean newEntry, Boolean sketchCurrent, long sketchTime, int status, int serverId, String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SERVER_ID + "=" + serverId;

        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_KEY_ARRANGEMENT, arrangement);
        newValues.put(OUR_ARRANGEMENT_KEY_AUTHOR_NAME, authorName);
        newValues.put(OUR_ARRANGEMENT_KEY_SERVER_ID, serverId); // id of arrangement on the server -> unique identifier for arrangement
        newValues.put(OUR_ARRANGEMENT_KEY_BLOCK_ID, blockId); // id of the block of arrangement -> current block number is safe in prefs to find the block in db
        newValues.put(OUR_ARRANGEMENT_KEY_STATUS, status);

        // is it a new entry?
        if (newEntry) {
            newValues.put(OUR_ARRANGEMENT_KEY_NEW_ENTRY, 1);
        } else {
            newValues.put(OUR_ARRANGEMENT_KEY_NEW_ENTRY, 0);
        }

        // is it a sketch? sketchCurrent-> true!
        if (sketchCurrent) {
            newValues.put(OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT, 1);
            newValues.put(OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME, sketchTime);
            newValues.put(OUR_ARRANGEMENT_KEY_WRITE_TIME, 0);
        } else {
            newValues.put(OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT, 0);
            newValues.put(OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME, 0);
            newValues.put(OUR_ARRANGEMENT_KEY_WRITE_TIME, arrangementTime);
        }

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT, newValues, where, null) != 0;
    }


    // Delete a row from the database, by serverId
    boolean deleteRowOurArrangement(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SERVER_ID + "=" + serverId;
        return db.delete(DATABASE_TABLE_OUR_ARRANGEMENT, where, null) != 0;
    }


    // Delete all arrangements with the same blockId
    boolean deleteAllRowsOurArrangement(String blockId, Boolean sketchCurrent) {

        int tmpStatus;

        SQLiteDatabase db = this.getWritableDatabase();

        if (sketchCurrent) {
            tmpStatus = 1;
        } else {
            tmpStatus = 0;
        }

        String where = OUR_ARRANGEMENT_KEY_BLOCK_ID + "='" + blockId + "' AND " + OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=" + tmpStatus;

        return db.delete(DATABASE_TABLE_OUR_ARRANGEMENT, where, null) != 0;
    }


    // Return all data from the database (table ourArrangement) -> see order!
    // the result is sorted by DESC
    Cursor getAllRowsCurrentOurArrangement(String blockID, String order) {

        String where = "";

        SQLiteDatabase db = this.getWritableDatabase();

        // get only arrangments and no sketches
        where = OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=0 AND ";

        switch (order) {


            case "equalBlockId":
                where += OUR_ARRANGEMENT_KEY_BLOCK_ID + "='" + blockID + "'";
                break;

            case "notEqualBlockId":
                where += OUR_ARRANGEMENT_KEY_BLOCK_ID + "!='" + blockID + "'";
                break;
            default:
                where += OUR_ARRANGEMENT_KEY_BLOCK_ID + "='" + blockID + "'";
                break;
        }

        // sort string
        String sort = OUR_ARRANGEMENT_KEY_WRITE_TIME + " DESC," + KEY_ROWID + " ASC";

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Return sketch arrangmenet from the database (table ourArrangement)
    // the result is sorted by DESC
    Cursor getAllRowsSketchOurArrangement(String blockID) {

        String where = "";

        SQLiteDatabase db = this.getWritableDatabase();

        // get only arrangments and no sketches
        where = OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=1 AND " + OUR_ARRANGEMENT_KEY_BLOCK_ID + "=" + blockID;

        // sort string
        String sort = KEY_ROWID + " ASC";

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }


        return c;
    }


    // Get a specific row from the arrangement (by serverId)
    Cursor getRowOurArrangement(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=0 AND " + OUR_ARRANGEMENT_KEY_SERVER_ID + "=" + serverId;
        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get a specific row from the sketch arrangement (by rowId)
    Cursor getRowSketchOurArrangement(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=1 AND " + OUR_ARRANGEMENT_KEY_SERVER_ID + "=" + serverId;
        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in arrangement (new entrys, current and sketch) where date is current arrangement date or sketch write time -> no older one!
    int getCountNewEntryOurArrangement(long currentDateOfArrangement, String currentSketch) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where;

        switch (currentSketch) {
            case "current": // new entry and arrangement time
                where = OUR_ARRANGEMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_KEY_WRITE_TIME + "=" + currentDateOfArrangement;
                break;
            case "sketch": // new entry and sketch arrangement time
                where = OUR_ARRANGEMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME + "=" + currentDateOfArrangement;
                break;
            default:
                where = OUR_ARRANGEMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_KEY_WRITE_TIME + "=" + currentDateOfArrangement;
                break;

        }

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // delete status new entry in table ourArrangement for arrangment rowId.
    boolean deleteStatusNewEntryOurArrangement(int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_ARRANGEMENT_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT, newValues, where, null) != 0;
    }


    // change (delete/set) status evaluation possible in table ourArrangement
    boolean changeStatusEvaluationPossibleOurArrangement(int serverId, String state) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SERVER_ID + "=" + serverId;

        // Create row
        ContentValues newValues = new ContentValues();

        switch (state) {

            case "set":
                newValues.put(OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE, 1);
                break;
            case "delete":
                newValues.put(OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE, 0);
                break;
            default:
                newValues.put(OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE, 0);
                break;

        }

        // update table our arrangement
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT, newValues, where, null) != 0;
    }


    // change (delete/set) status evaluation poosible in table ourArrangement for all arrangement with current block id
    boolean changeStatusEvaluationPossibleAllOurArrangement(String blockId, String state) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_BLOCK_ID + "=" + blockId;

        // Create row
        ContentValues newValues = new ContentValues();

        switch (state) {

            case "set":
                newValues.put(OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE, 1);
                break;
            case "delete":
                newValues.put(OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE, 0);
                break;
            default:
                newValues.put(OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE, 0);
                break;

        }

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT, newValues, where, null) != 0;
    }


    /********************************* End!! TABLES FOR FUNCTION: Our Arrangement ******************************************/
    /***********************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Our Arrangement Comment ******************************************/

    // Add a new set of values to ourArrangementComment .
    long insertRowOurArrangementComment(String comment, String authorName, long commentTime, long upload_time, long localeTime, String blockid, Boolean newEntry, long currentDateOfArrangement, int status, int serverId, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_UPLOAD_TIME, upload_time);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME, localeTime);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID, blockid);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME, currentDateOfArrangement);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT, serverId);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_STATUS, status);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_TIMER_STATUS, timerStatus);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, null, initialValues);
    }


    // Return all comments from the database for arrangement with  server id = id (table ourArrangementComment)
    // the result is sorted by DESC
    Cursor getAllRowsOurArrangementComment(int serverId, String sortSequence) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT + "=" + serverId;

        // get sort string
        String sort = "";
        switch (sortSequence) {
            case "ascending":
                sort = OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME + " ASC";
                break;
            case "descending":
                sort = OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME + " DESC";
                break;
            default:
                sort = OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME + " DESC";
                break;
        }

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in all comment for all arrangement (new entrys) where block id are current arrangement block
    int getCountAllNewEntryOurArrangementComment(String blockIdOfArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfArrangement;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        db.close(); // close db connection

        // return how many
        return c.getCount();
    }


    // delete status new entry in table ourArrangementComment.
    boolean deleteStatusNewEntryOurArrangementComment(int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, newValues, where, null) != 0;
    }


    // delete all comments with the same block id
    Boolean deleteAllRowsOurArrangementComment(String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID + "='" + blockId + "'";

        return db.delete(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, where, null) != 0;
    }


    // Return one comment from the database for arrangement with row id = dbid (table ourArrangementComment)
    Cursor getOneRowOurArrangementComment(Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // update status comment and write of comment (with server time) in table ourArrangementComment
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Comment
    boolean updateStatusOurArrangementComment(Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_COMMENT_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, newValues, where, null) != 0;
    }


    // Get all comments with status = 0 (Ready to send) and block id of current arrangement
    Cursor getAllReadyToSendComments(String blockIdOfArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        // status = 0 and block id = blockIdOfArrangement
        String where = OUR_ARRANGEMENT_COMMENT_KEY_STATUS + "=0 AND " + OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfArrangement;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return cursor
        return c;
    }


    // update status of timer for comment
    // 0= timer can run; 1= timer finish!
    boolean updateTimerStatusOurArrangementComment(Long rowId, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_COMMENT_KEY_TIMER_STATUS, timerStatus);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, newValues, where, null) != 0;
    }


    // update write time for comment from locale time to server time in table ourArrangementComment
    boolean updateWriteTimeOurArrangementComment(Long rowId, Long writeTimeFromServer) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row write time from server
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME, writeTimeFromServer);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, newValues, where, null) != 0;
    }

    /********************************* End!! TABLES FOR FUNCTION: Our Arrangement Comment ***************************************/
    /****************************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Our Arrangement Sketch Comment ******************************************/

    // Add a new set of values to ourArrangementSketchComment .
    long insertRowOurArrangementSketchComment(String comment, int question_a, int question_b, int question_c, String authorName, long localeTime, long commentTime, long upload_time, String blockid, Boolean newEntry, long currentDateOfArrangement, int status, int serverId, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1, question_a);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2, question_b);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3, question_c);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME, localeTime);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_UPLOAD_TIME, upload_time);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID, blockid);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME, currentDateOfArrangement);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS, status);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT, serverId);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_TIMER_STATUS, timerStatus);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, null, initialValues);
    }


    // Return all comments from the database for sketch arrangement with server arrangement id = id (table ourArrangementSketchComment)
    // the result is sorted by DESC
    Cursor getAllRowsOurArrangementSketchComment(int serverId, String sortSequence) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT + "=" + serverId;

        // get sort string
        String sort = "";
        switch (sortSequence) {
            case "ascending":
                sort = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME + " ASC";
                break;
            case "descending":
                sort = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME + " DESC";
                break;
            default:
                sort = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME + " DESC";
                break;
        }

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in all comment for all sketch arrangement (new entrys) where date is current sketch arrangement date -> no older one!
    int getCountAllNewEntryOurArrangementSketchComment(String blockIdOfSketchArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfSketchArrangement;
        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // delete status new entry in table ourArrangementSketchComment.
    boolean deleteStatusNewEntryOurArrangementSketchComment(int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, newValues, where, null) != 0;
    }


    // delete all comments for sketch arrangements with the same block id
    Boolean deleteAllRowsOurArrangementSketchComment(String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID + "='" + blockId + "'";

        return db.delete(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, where, null) != 0;

    }


    // Return one comment from the database for sketch arrangement with row id = dbid (table ourArrangementSketchComment)
    Cursor getOneRowOurArrangementSketchComment(Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // update status comment in table ourArrangementSketchComment
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Comment
    boolean updateStatusOurArrangementSketchComment(Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, newValues, where, null) != 0;
    }


    // Get all sketch comments with status = 0 (Ready to send) and block id of sketch arrangement
    Cursor getAllReadyToSendSketchComments(String blockIdOfSketchArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        // status = 0 and block id = blockIdOfSketchArrangement
        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS + "=0 AND " + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfSketchArrangement;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return cursor
        return c;
    }


    // update status of timer for sketch comment
    // 0= timer can run; 1= timer finish!
    boolean updateTimerStatusOurArrangementSketchComment(Long rowId, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row timer status with timerStatus
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_TIMER_STATUS, timerStatus);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, newValues, where, null) != 0;
    }


    // update write time for sketch comment from locale time to server time in table ourArrangementSketchComment
    boolean updateWriteTimeOurArrangementSketchComment(Long rowId, Long writeTimeFromServer) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row write time from server
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME, writeTimeFromServer);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, newValues, where, null) != 0;
    }

    /********************************* End!! TABLES FOR FUNCTION: Our Arrangement Sketch Comment ***************************************/
    /****************************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Our Arrangement Evaluate ******************************************/

    // Add a new set of values to ourArrangementEvaluate .
    long insertRowOurArrangementEvaluate(int serverId, long currentDateOfArrangement, int resultQuestion1, int resultQuestion2, int resultQuestion3, int resultQuestion4, String resultRemarks, long localeTime, long resultTime, String userName, int status, long startEvaluationTime, long endEvaluationTime, String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_SERVER_ID_ARRANGEMENT, serverId);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_BLOCKID, blockId);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME, currentDateOfArrangement);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1, resultQuestion1);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2, resultQuestion2);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3, resultQuestion3);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4, resultQuestion4);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS, resultRemarks);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME, resultTime);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_LOCAL_TIME, localeTime);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME, startEvaluationTime);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME, endEvaluationTime);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME, userName);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_STATUS, status);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE, null, initialValues);
    }


    Cursor getOneRowEvaluationResultArrangement(Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE, OUR_ARRANGEMENT_EVALUATE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;

    }


    // update status evaluation in table ourArrangementEvaluate
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Evaluation
    boolean updateStatusOurArrangementEvaluation(Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE, newValues, where, null) != 0;
    }


    // get all evaluation results in table ourArrangementEvaluate with status = 0
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Evaluation
    Cursor getAllReadyToSendArrangementEvaluationResults() {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_EVALUATE_KEY_STATUS + "=0";

        Cursor c = db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE, OUR_ARRANGEMENT_EVALUATE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // set last evaluation time in choosen (serverId) arrangement in table our arrangement
    boolean setEvaluationTimePointForArrangement(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SERVER_ID + "=" + serverId;

        // Create row's data:
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_ARRANGEMENT_KEY_LAST_EVAL_TIME, System.currentTimeMillis());

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT, newValues, where, null) != 0;
    }


    // update write time for evaluation result from locale time to server time in table ourArrangementEvaluationResult
    boolean updateWriteTimeOurArrangementEvaluationResult(Long rowId, Long writeTimeFromServer) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row evaluationresult time from server
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME, writeTimeFromServer);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE, newValues, where, null) != 0;
    }


    /********************************* End!! TABLES FOR FUNCTION: Our Arrangement Evaluate ***************************************/
    /****************************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Our Goals Jointly/Debetable ******************************************/

    /* Add a new set of values (goal) to ourgoals
        goals -> text of arrangement
        authorName -> name of author
        goalTime -> date and time of actual goal
        newEntry -> true, goal is new in database; false, it is old!
        eval_possible -> true, evaluation is possible; false -> not
        jointlyDebetable -> true, goal is a debetable; false, goal is an jointly goal
        debetableTime -> date and time of debetable goal
        serverId -> the id of goal on the server
        blockId -> the id of a block of goal
        changeTo -> needed when a goal is switch from jointly goal to debetable goal or visversa
        status -> the status of goal -> 0=ready to send, 1=message send, 4=external message
     */
    long insertRowOurGoals(String goal, String authorName, long goalTime, Boolean newEntry, Boolean jointlyDebetable, long debetableTime, int status, int serverId, String blockId, String changeTo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL, goal);
        initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME, authorName);
        initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID, serverId); // id of goal on the server -> unique identifier for goal
        initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID, blockId); // id of the block of goals -> current block number is safe in prefs to find the block in db
        initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_CHANGE_TO, changeTo); // possible values are "nothing", "jointly" and "debetable" (look for definitions in ConstansClassOurGoals)
        initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_STATUS, status);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY, 1);
        } else {
            initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY, 0);
        }

        // is it a debetable goal? jointlyDebetable -> true!
        if (jointlyDebetable) {
            initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE, 1);
            initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DEBETABLE_TIME, debetableTime);
            initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME, 0);
        } else {
            initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE, 0);
            initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DEBETABLE_TIME, 0);
            initialValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME, goalTime);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, null, initialValues);
    }


    // Delete a row from the database, by serverId
    boolean deleteRowOurGoals(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + "=" + serverId;
        return db.delete(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, where, null) != 0;
    }


    // Change an existing row to be equal to oldMd5.
    boolean updateRowOurGoals(String goal, String authorName, long goalTime, Boolean newEntry, Boolean jointlyDebetable, long debetableTime, int status, int serverId, String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + "=" + serverId;

        // Create row's data:
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL, goal);
        newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME, authorName);
        newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME, goalTime);
        newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID, serverId); // id of goals on the server -> unique identifier for goal
        newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID, blockId); // id of the block of goals -> current block number is safe in prefs to find the block in db
        newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_STATUS, status);

        // is it a new entry?
        if (newEntry) {
            newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY, 1);
        } else {
            newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY, 0);
        }

        // is it a debetable goal? jointlyDebetable -> true!
        if (jointlyDebetable) {
            newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE, 1);
            newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DEBETABLE_TIME, debetableTime);
            newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME, 0);
        } else {
            newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE, 0);
            newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DEBETABLE_TIME, 0);
            newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME, goalTime);
        }


        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, newValues, where, null) != 0;
    }


    // Return all data from the database (table ourGoals) where blockid is equal or not equal!
    // the result is sorted by DESC from wirte_time
    Cursor getAllJointlyRowsOurGoals(String blockID, String order) {

        String where = "";

        SQLiteDatabase db = this.getWritableDatabase();

        // get only arrangments and no sketches
        where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE + "=0 AND ";

        switch (order) {

            case "equalBlockId":
                where += OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID + "='" + blockID + "'";
                break;

            case "notEqualBlockId":
                where += OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID + "!='" + blockID + "'";
                break;
            default:
                where += OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID + "='" + blockID + "'";
                break;
        }

        // sort string
        String sort = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME + " DESC, " + KEY_ROWID + " ASC";

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;

    }


    // Return debetable goals from the database (table ourGoals)
    // the result is sorted by DESC
    Cursor getAllDebetableRowsOurGoals(String blockId) {

        String where = "";

        SQLiteDatabase db = this.getWritableDatabase();

        // get only debetable goals
        where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE + "=1 AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID + "=" + blockId;
        ;

        // sort string
        String sort = KEY_ROWID + " ASC";

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get a specific jointly row from the goals (by serverId)
    Cursor getJointlyRowOurGoals(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE + "=0 AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + "=" + serverId;
        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get a specific debetable row from the goals (by rowId)
    Cursor getDebetableRowOurGoals(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE + "=1 AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + "=" + serverId;
        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in goals (new entrys, jointly and debetable) where date is write time -> no older one!
    int getCountNewEntryOurGoals(long currentDateOfGoals, String jointlyDebetable) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where;

        switch (jointlyDebetable) {
            case "jointly": // new entry and jointly goal time
                where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY + "=1 AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME + "=" + currentDateOfGoals;
                break;
            case "debetable": // new entry and debetable goal time
                where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY + "=1 AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DEBETABLE_TIME + "=" + currentDateOfGoals;
                break;
            default:
                where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY + "=1 AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME + "=" + currentDateOfGoals;
                break;

        }

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // delete status new entry in table ourGoals for goal rowId.
    boolean deleteStatusNewEntryOurGoals(int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, newValues, where, null) != 0;
    }


    // change (delete/set) status evaluation poosible in table ourGoals for one goal (rowId)
    boolean changeStatusEvaluationPossibleOurGoals(int serverId, String state) {


        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + "=" + serverId;

        // Create row
        ContentValues newValues = new ContentValues();

        switch (state) {

            case "set":
                newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE, 1);
                break;
            case "delete":
                newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE, 0);
                break;
            default:
                newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE, 0);
                break;

        }

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, newValues, where, null) != 0;
    }


    // change (delete/set) status evaluation poosible in table ourGoals for all goals with current goal time
    boolean changeStatusEvaluationPossibleAllOurGoals(String blockId, String state) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID + "=" + blockId;

        // Create row
        ContentValues newValues = new ContentValues();

        switch (state) {

            case "set":
                newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE, 1);
                break;
            case "delete":
                newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE, 0);
                break;
            default:
                newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE, 0);
                break;

        }

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, newValues, where, null) != 0;
    }


    // Delete all goals with the same blockId
    boolean deleteAllRowsOurGoals(String blockId, Boolean jointlyDebetable) {

        int tmpStatus;

        SQLiteDatabase db = this.getWritableDatabase();

        if (jointlyDebetable) {
            tmpStatus = 1;
        } else {
            tmpStatus = 0;
        }

        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID + "='" + blockId + "' AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE + "=" + tmpStatus;

        return db.delete(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, where, null) != 0;

    }


    /********************************* End!! TABLES FOR FUNCTION: Our Debetable/Jointly Goals ******************************************/
    /***********************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Our Goals Jointly Goals Comment ******************************************/
    // Add a new set of values to ourGoalsJointlyGoalsComment .
    long insertRowOurGoalJointlyGoalComment(String comment, String authorName, long commentTime, long commentLocaleTime, long upload_time, String blockid, Boolean newEntry, long currentDateOfGoal, int status, int serverId, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_LOCAL_TIME, commentLocaleTime);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_UPLOAD_TIME, upload_time);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID, blockid);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME, currentDateOfGoal);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL, serverId);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS, status);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_TIMER_STATUS, timerStatus);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, null, initialValues);
    }


    // Return all comments from the database for jointly goals with serverGoalId = id (table ourGoalsJointlyGoalsComment)
    // the result is sorted by DESC
    Cursor getAllRowsOurGoalsJointlyGoalsComment(int serverGoalId, String sortSequence) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL + "=" + serverGoalId;

        // get sort string
        String sort = "";
        switch (sortSequence) {
            case "ascending":
                sort = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME + " ASC";
                break;
            case "descending":
                sort = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME + " DESC";
                break;
            default:
                sort = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME + " DESC";
                break;
        }

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in all comment for all jointly goals (new entrys) where date is current goal date -> no older one!
    int getCountAllNewEntryOurGoalsJointlyGoalsComment(long currentDateOfJointlyGoal) {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME + "=" + currentDateOfJointlyGoal;
        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }



    // delete status new entry in table ourArrangementComment.
    boolean deleteStatusNewEntryOurGoalsJointlyGoalComment(int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, newValues, where, null) != 0;
    }


    // delete all comments with the same block id
    Boolean deleteAllRowsOurJointlyGoalsComment(String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID + "='" + blockId + "'";

        return db.delete(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, where, null) != 0;

    }


    // Return one jointly comment from the database for goals with row id = dbid (table ourGoalsJointlyGoalsComment)
    Cursor getOneRowOurGoalsJointlyComment(Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // update status comment in table ourGoalsJointlyGoalsComment
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Comment
    boolean updateStatusOurGoalsJointlyGoalsComment(Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, newValues, where, null) != 0;
    }


    // Get all comments with status = 0 (Ready to send) and block id of current jointly goals
    Cursor getAllReadyToSendJointlyGoalsComments(String blockIdOfGoals) {

        SQLiteDatabase db = this.getWritableDatabase();

        // status = 0 and block id = blockIdOfArrangement
        String where = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS + "=0 AND " + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfGoals;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return cursor
        return c;
    }


    // update status of timer for comment
    // 0= timer can run; 1= timer finish!
    boolean updateTimerStatusOurGoalsJointlyComment(Long rowId, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_TIMER_STATUS, timerStatus);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, newValues, where, null) != 0;
    }


    // update write time for jointly comment from locale time to server time in table ourGoalsJointlyComment
    boolean updateWriteTimeOurGoalsJointlyComment(Long rowId, Long writeTimeFromServer) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row write time from server
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME, writeTimeFromServer);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, newValues, where, null) != 0;
    }


    /********************************* End!! TABLES FOR FUNCTION: Our Goals Jointly Goals Comment ***************************************/
    /****************************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Our Goals Jointly Goals Evaluate ******************************************/

    // Add a new set of values to ourGoalsJointlyGoalsEvaluate .
    long insertRowOurGoalsJointlyGoalEvaluate(int serverGoalId, long currentDateOfGoal, int resultQuestion1, int resultQuestion2, int resultQuestion3, int resultQuestion4, String resultRemarks, long localeTime, long resultTime, String userName, int status, long startEvaluationTime, long endEvaluationTime, String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_SERVER_ID, serverGoalId);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_GOAL_TIME, currentDateOfGoal);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_BLOCKID, blockId);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION1, resultQuestion1);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION2, resultQuestion2);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION3, resultQuestion3);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION4, resultQuestion4);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_REMARKS, resultRemarks);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_TIME, resultTime);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_LOCAL_TIME, localeTime);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME, startEvaluationTime);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME, endEvaluationTime);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_USERNAME, userName);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_STATUS, status);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE, null, initialValues);
    }


    Cursor getOneRowEvaluationResultGoals(Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE, OUR_GOALS_JOINTLY_GOALS_EVALUATE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // get all evaluation results in table ourGoalsEvaluate with status = 0
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Evaluation
    Cursor getAllReadyToSendGoalsEvaluationResults() {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_STATUS + "=0";

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE, OUR_GOALS_JOINTLY_GOALS_EVALUATE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // update status evaluation in table jointly goals evaluation
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Evaluation
    boolean updateStatusOurGoalsEvaluation(Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE, newValues, where, null) != 0;
    }


    // set last evaluation time in choosen (serverId) goal in table jointly debetable goal
    boolean setEvaluationTimePointForGoal(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + "=" + serverId;

        // Create row's data:
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_LAST_EVAL_TIME, System.currentTimeMillis());

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, newValues, where, null) != 0;
    }


    // update write time for evaluation result from locale time to server time in table ourGoalsJointlyGoalsEvaluationResult
    boolean updateWriteTimeOurGoalsEvaluationResult(Long rowId, Long writeTimeFromServer) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row evaluationresult time from server
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_TIME, writeTimeFromServer);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE, newValues, where, null) != 0;
    }


    /********************************* End!! TABLES FOR FUNCTION: Our Goals Jointly Goals Evaluate ***************************************/


    /********************************* TABLES FOR FUNCTION: Our Goals Debetable Goals Comment ******************************************/

    // Add a new set of values to ourGoalsDebetableGoalsComment .
    long insertRowOurGoalsDebetableGoalsComment(String comment, int question_a, int question_b, int question_c, String authorName, long commentTime, long commentLocaleTime, long upload_time, String blockid, Boolean newEntry, long currentDateOfDebetableGoal, int status, int serverId, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1, question_a);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION2, question_b);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION3, question_c);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME, commentLocaleTime);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_UPLOAD_TIME, upload_time);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID, blockid);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME, currentDateOfDebetableGoal);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS, status);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID, serverId);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_TIMER_STATUS, timerStatus);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, null, initialValues);
    }


    // Return all comments from the database for debetable goal with serverGoalId = id (table ourGoalsDebetableGoalsComment)
    // the result is sorted by DESC
    Cursor getAllRowsOurGoalsDebetableGoalsComment(int serverGoalId, String sortSequence) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID + "=" + serverGoalId;

        // get sort string
        String sort = "";
        switch (sortSequence) {
            case "ascending":
                sort = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME + " ASC";
                break;
            case "descending":
                sort = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME + " DESC";
                break;
            default:
                sort = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME + " DESC";
                break;
        }

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, OUR_GOALS_DEBETABLE_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in all comment for all debetable goals (new entrys) where date is current debetable goal date -> no older one!
    int getCountAllNewEntryOurGoalsDebetableGoalsComment(long currentDateOfGoal) {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME + "=" + currentDateOfGoal;
        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, OUR_GOALS_DEBETABLE_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // Return one comment from the database for debetable goals with row id = dbid (table DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT)
    Cursor getOneRowOurGoalsDebetableComment(Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, OUR_GOALS_DEBETABLE_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // update status comment in table DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Comment
    boolean updateStatusOurGoalsDebetableComment(Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, newValues, where, null) != 0;
    }


    // delete status new entry in table ourGoalsDebetableGoalsComment.
    boolean deleteStatusNewEntryOurGoalsDebetableGoalsComment(int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, newValues, where, null) != 0;
    }


    // delete all comments for debetable goals with the same block id
    Boolean deleteAllRowsOurGoalsDebetableComment(String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID + "='" + blockId + "'";

        return db.delete(DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, where, null) != 0;
    }


    // Get all debetable comments with status = 0 (Ready to send) and block id of current debetable goals
    Cursor getAllReadyToSendDebetableComments(String blockIdOfGoals) {

        SQLiteDatabase db = this.getWritableDatabase();

        // status = 0 and block id = blockIdOfDebetableGoals
        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS + "=0 AND " + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfGoals;

        Cursor c = db.query(true, DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, OUR_GOALS_DEBETABLE_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return cursor
        return c;
    }


    // update status of timer for comment
    // 0= timer can run; 1= timer finish!
    boolean updateTimerStatusOurGoalsDebetableComment(Long rowId, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_TIMER_STATUS, timerStatus);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, newValues, where, null) != 0;
    }


    // update write time for debetable comment from locale time to server time in table ourGoalsJointlyComment
    boolean updateWriteTimeOurGoalsDebetableComment(Long rowId, Long writeTimeFromServer) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row write time from server
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME, writeTimeFromServer);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, newValues, where, null) != 0;
    }

    /********************************* End!! TABLES FOR FUNCTION: Our Goals Debetable Goals Comment ***************************************/
    /****************************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Meeting ******************************************/

    // Add a new meeting or suggestion date in db
    Long insertNewMeetingOrSuggestionDate(Long[] array_meetingTime, int[] array_meetingPlace, int[] array_meetingVote, String tmpClientVoteAuthor, Long tmpClientVoteDate, Long tmpClientVoteLocaleDate, Long tmpMeetingSuggestionCreationTime, String tmpMeetingSuggestionAuthorName, int tmpMeetingSuggestionKategorie, Long tmpMeetingSuggestionResponseTime, Long tmpMeetingSuggestionResponseStartTime, String tmpMeetingSuggestionCoachHintText, int tmpMeetingSuggestionCoachCancele, Long tmpMeetingSuggestionCoachCanceleTime, String tmpMeetingSuggestionCoachCanceleAuthor, String tmpMeetingFoundFromSuggestionAuthor, Long tmpMeetingFoundFromSuggestionDate, int tmpMeetingFoundFromSuggestion, Long tmpMeetingSuggestionDataServerId, String tmpClientSuggestionText, String tmpClientSuggestionAuthor, Long tmpClientSuggestionTime, Long tmpClientSuggestionLocaleTime, Long tmpClientSuggestionStartDate, Long tmpClientSuggestionEndDate, String tmpClientCommentText, String tmpClientCommentAuthor, Long tmpClientCommentTime, Long tmpClientCommentLocaleTime, int tmpMeetingSuggestionClientCancele, Long tmpMeetingSuggestionClientCanceleTime, Long tmpMeetingSuggestionClientCanceleLocaleTime, String tmpMeetingSuggestionClientCanceleAuthor, String tmpMeetingSuggestionClientCanceleText, int meetingStatus, Long tmpUploadTime, int newMeeting, int timerStatus, String updateOrder) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(MEETING_SUGGESTION_KEY_DATE1, array_meetingTime[0]);
        initialValues.put(MEETING_SUGGESTION_KEY_DATE2, array_meetingTime[1]);
        initialValues.put(MEETING_SUGGESTION_KEY_DATE3, array_meetingTime[2]);
        initialValues.put(MEETING_SUGGESTION_KEY_DATE4, array_meetingTime[3]);
        initialValues.put(MEETING_SUGGESTION_KEY_DATE5, array_meetingTime[4]);
        initialValues.put(MEETING_SUGGESTION_KEY_DATE6, array_meetingTime[5]);
        initialValues.put(MEETING_SUGGESTION_KEY_PLACE1, array_meetingPlace[0]);
        initialValues.put(MEETING_SUGGESTION_KEY_PLACE2, array_meetingPlace[1]);
        initialValues.put(MEETING_SUGGESTION_KEY_PLACE3, array_meetingPlace[2]);
        initialValues.put(MEETING_SUGGESTION_KEY_PLACE4, array_meetingPlace[3]);
        initialValues.put(MEETING_SUGGESTION_KEY_PLACE5, array_meetingPlace[4]);
        initialValues.put(MEETING_SUGGESTION_KEY_PLACE6, array_meetingPlace[5]);
        initialValues.put(MEETING_SUGGESTION_KEY_VOTE1, array_meetingVote[0]);
        initialValues.put(MEETING_SUGGESTION_KEY_VOTE2, array_meetingVote[1]);
        initialValues.put(MEETING_SUGGESTION_KEY_VOTE3, array_meetingVote[2]);
        initialValues.put(MEETING_SUGGESTION_KEY_VOTE4, array_meetingVote[3]);
        initialValues.put(MEETING_SUGGESTION_KEY_VOTE5, array_meetingVote[4]);
        initialValues.put(MEETING_SUGGESTION_KEY_VOTE6, array_meetingVote[5]);
        initialValues.put(MEETING_SUGGESTION_KEY_VOTEAUTHOR, tmpClientVoteAuthor);
        initialValues.put(MEETING_SUGGESTION_KEY_VOTEDATE, tmpClientVoteDate);
        initialValues.put(MEETING_SUGGESTION_KEY_VOTELOCALEDATE, tmpClientVoteLocaleDate);
        initialValues.put(MEETING_SUGGESTION_KEY_SUGGESTION_FOUND, tmpMeetingFoundFromSuggestion);
        initialValues.put(MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_AUTHOR, tmpMeetingFoundFromSuggestionAuthor);
        initialValues.put(MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_DATE, tmpMeetingFoundFromSuggestionDate);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME, tmpMeetingSuggestionCreationTime);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR, tmpMeetingSuggestionAuthorName);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_KATEGORIE, tmpMeetingSuggestionKategorie);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME, tmpMeetingSuggestionResponseTime);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_RESPONSE_START_TIME, tmpMeetingSuggestionResponseStartTime);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_COACH_HINT_TEXT, tmpMeetingSuggestionCoachHintText);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME, tmpMeetingSuggestionCoachCanceleTime);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CANCELED_AUTHOR, tmpMeetingSuggestionCoachCanceleAuthor);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CANCELED, tmpMeetingSuggestionCoachCancele);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT, tmpClientSuggestionText);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_AUTHOR, tmpClientSuggestionAuthor);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TIME, tmpClientSuggestionTime);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_LOCALE_TIME, tmpClientSuggestionLocaleTime);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE, tmpClientSuggestionStartDate);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE, tmpClientSuggestionEndDate);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_TEXT, tmpClientCommentText);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_AUTHOR, tmpClientCommentAuthor);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_DATE, tmpClientCommentTime);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_LOCALE_DATE, tmpClientCommentLocaleTime);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED, tmpMeetingSuggestionClientCancele);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_AUTHOR, tmpMeetingSuggestionClientCanceleAuthor);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME, tmpMeetingSuggestionClientCanceleTime);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_LOCALE_TIME, tmpMeetingSuggestionClientCanceleLocaleTime);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TEXT, tmpMeetingSuggestionClientCanceleText);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_SERVER_ID, tmpMeetingSuggestionDataServerId);
        initialValues.put(MEETING_SUGGESTION_MEETING_KEY_STATUS, meetingStatus);
        initialValues.put(MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME, tmpUploadTime);
        initialValues.put(MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST, newMeeting);
        initialValues.put(MEETING_SUGGESTION_KEY_TIMER_STATUS, timerStatus);
        initialValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, updateOrder);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_MEETING_SUGGESTION, null, initialValues);
    }


    // Change an existing meeting to canceled by coach
    boolean updateMeetingCanceledByCoach(Long meeting_server_id, Long canceledTime, String canceledAuthor, int newMeeting, int status, int timerStatus, String updateOrder) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = "";
        where = MEETING_SUGGESTION_KEY_MEETING_SERVER_ID + "=" + meeting_server_id;

        // Create rows data:
        ContentValues newValues = new ContentValues();
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME, canceledTime);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CANCELED, 1);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CANCELED_AUTHOR, canceledAuthor);
        newValues.put(MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST, newMeeting);
        newValues.put(MEETING_SUGGESTION_MEETING_KEY_STATUS, status);
        newValues.put(MEETING_SUGGESTION_KEY_TIMER_STATUS, timerStatus);
        newValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, updateOrder);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }


    // Change an existing meeting to canceled by client
    boolean updateMeetingCanceledByClient(Long meeting_id, long canceledLocaleTime, long canceledTime, String canceledAuthor, String canceledReasonText, int status,String updateOrder) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + meeting_id;

        // Create rows data:
        ContentValues newValues = new ContentValues();
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME, canceledTime);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_LOCALE_TIME, canceledLocaleTime);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED, 1);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_AUTHOR, canceledAuthor);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TEXT, canceledReasonText);
        newValues.put(MEETING_SUGGESTION_MEETING_KEY_STATUS, status);
        newValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, updateOrder);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }


    // Change an existing suggestion that a meeting was found by coach
    boolean updateMeetingFoundFromSuggestion(Long meeting_id, long foundTime, String foundAuthor, int newMeeting, int status, int timerStatus, String updateOrder) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = "";
        where = MEETING_SUGGESTION_KEY_MEETING_SERVER_ID + "=" + meeting_id;

        // Create rows data:
        ContentValues newValues = new ContentValues();
        newValues.put(MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_DATE, foundTime);
        newValues.put(MEETING_SUGGESTION_KEY_SUGGESTION_FOUND, 1);
        newValues.put(MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_AUTHOR, foundAuthor);
        newValues.put(MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST, newMeeting);
        newValues.put(MEETING_SUGGESTION_MEETING_KEY_STATUS, status);
        newValues.put(MEETING_SUGGESTION_KEY_TIMER_STATUS, timerStatus);
        newValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, updateOrder);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }


    // Change an existing suggestion from client with client suggestion
    boolean updateSuggestionFromClient(Long meeting_id, long suggestionTime, long suggestionLocaleTime, String suggestionAuthor, String suggestionText, int status, int timerStatus, String updateOrder) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + meeting_id;

        // Create rows data:
        ContentValues newValues = new ContentValues();
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TIME, suggestionTime);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_LOCALE_TIME, suggestionLocaleTime);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_AUTHOR, suggestionAuthor);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT, suggestionText);
        newValues.put(MEETING_SUGGESTION_MEETING_KEY_STATUS, status);
        newValues.put(MEETING_SUGGESTION_KEY_TIMER_STATUS, timerStatus);
        newValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, updateOrder);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }


    // Delete all rows meeting and suggestions from the database
    boolean deleteAllRowsMeetingAndSuggestion() {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(DATABASE_TABLE_MEETING_SUGGESTION, null, null) != 0;
    }


    // Delete selected meeting or suggestion from db
    boolean deleteSelectedMeetingOrSuggestionFromDb(Long meeting_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + meeting_id;

        return db.delete(DATABASE_TABLE_MEETING_SUGGESTION, where, null) != 0;
    }


    // Get the number of new rows in meeting/ suggestion
    int getCountNewEntryMeetingAndSuggestion(String meetingOrSuggestion) {

        String where = ""; //new_entry = 1 (true)?

        switch (meetingOrSuggestion) {
            case "meeting":
                where = MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST + "=1 AND " + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1"; // kategorie =1 -> normal meeting
                break;
            case "suggestion":
                where = MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST + "=1 AND " + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=2"; // kategorie =2 -> suggestion
                break;
            case "suggestion_from_client":
                where = MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST + "=1 AND " + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4"; // kategorie =4 -> suggestion from client
                break;
            case "all":
                where = MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST + "=1";
                break;
            default:
                where = MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST + "=1 AND " + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1"; // kategorie =1 -> normal meeting
                break;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.query(true, DATABASE_TABLE_MEETING_SUGGESTION, MEETING_SUGGESTION_MEETING_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // delete status new entry for meeting/ suggestion with rowId
    boolean deleteStatusNewEntryMeetingAndSuggestion(Long rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();
        newValues.put(MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }


    // delete status new entry for old meetings/ suggestions -> these are meeting/suggestion with time border
    boolean deleteStatusNewEntryAllOldMeetingAndSuggestion(Long nowTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = "((" + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1 AND " + MEETING_SUGGESTION_KEY_DATE1 + "<" + nowTime + ") OR (" + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=2 AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + "<" + nowTime + ") OR (" + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + "<" + nowTime + "))";

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();
        newValues.put(MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }


    // Return all meeting/ suggestion from the database
    Cursor getAllRowsMeetingsAndSuggestion(String suggestionOrMeetingData, Long nowTime) {

        String where = "";
        String sort = "";

        SQLiteDatabase db = this.getWritableDatabase();

        switch (suggestionOrMeetingData) {

            case "future_meeting":
                Long canceledMeetingTimeBorder = nowTime - ConstansClassMeeting.showDifferentTimeForCanceledMeeting;
                where = "(" + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1 AND " + MEETING_SUGGESTION_KEY_DATE1 + ">=" + nowTime + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0) OR (" + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1 AND " + MEETING_SUGGESTION_KEY_DATE1 + ">=" + nowTime + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME + ">" + canceledMeetingTimeBorder + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=1)";
                sort = MEETING_SUGGESTION_KEY_MEETING_CANCELED + " ASC, " + MEETING_SUGGESTION_KEY_DATE1 + " ASC";
                break;
            case "future_suggestion":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=2 AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + ">=" + (nowTime-ConstansClassMeeting.namePrefsSuggestionDeltaTimeView) + " AND " + MEETING_SUGGESTION_KEY_TIMER_STATUS + "=0";
                sort = MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME + " DESC, " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + " DESC, " + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND + " DESC";
                break;

            case "future_suggestion_without_timeborder":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=2";
                sort = MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME + " DESC, " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + " DESC, " + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND + " DESC";
                break;

            case "future_suggestion_from_client":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + ">" + nowTime;
                sort = MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME + " DESC, " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + " DESC, " + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND + " DESC";
                break;

            case "future_suggestion_from_client_without_timeborder":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4";
                sort = MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME + " DESC, " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + " DESC, " + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND + " DESC";
                break;





            case "old_meeting":
                where = "(" + MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1 AND " + MEETING_SUGGESTION_KEY_DATE1 + "<" + nowTime + ")";
                sort = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + " DESC";
                break;
            case "ready_to_send":
                where = MEETING_SUGGESTION_MEETING_KEY_STATUS + "=0";
                sort = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + " DESC";
                break;
            case "suggestion_for_show_attention":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=2 AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + ">=" + nowTime + " AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_START_TIME + "<=" + nowTime + " AND " + MEETING_SUGGESTION_KEY_TIMER_STATUS + "=0  AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND + "=0 AND " + MEETING_SUGGESTION_KEY_VOTEAUTHOR + "='' AND " + MEETING_SUGGESTION_KEY_VOTEDATE + "=0 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_AUTHOR + "='' AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_DATE + "=0 AND (" + MEETING_SUGGESTION_MEETING_KEY_STATUS + "=0 OR " + MEETING_SUGGESTION_MEETING_KEY_STATUS + "=4)";
                sort = MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME + " DESC, " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + " DESC, " + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND + " DESC";
                break;

            case "client_suggestion_for_show_attention":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + "<" + nowTime + " AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + ">" + nowTime + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_TIMER_STATUS + "=0 AND " + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND + "=0 AND (" + MEETING_SUGGESTION_MEETING_KEY_STATUS + "=0 OR " + MEETING_SUGGESTION_MEETING_KEY_STATUS + "=4)";
                sort = MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME + " DESC, " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + " DESC, " + MEETING_SUGGESTION_KEY_SUGGESTION_FOUND + " DESC";
                break;

            default:
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1 AND " + MEETING_SUGGESTION_KEY_DATE1 + ">=" + nowTime;
                sort = MEETING_SUGGESTION_KEY_MEETING_CANCELED + " DESC, " + MEETING_SUGGESTION_KEY_DATE1 + " DESC";
                break;
        }

        Cursor c = db.query(true, DATABASE_TABLE_MEETING_SUGGESTION, MEETING_SUGGESTION_MEETING_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Return one row of meeting or suggestion from the database
    Cursor getOneRowMeetingsOrSuggestion(Long dbId) {

        String where = "";

        SQLiteDatabase db = this.getWritableDatabase();

        // get with id of db row
        where = KEY_ROWID + "=" + dbId;

        Cursor c = db.query(true, DATABASE_TABLE_MEETING_SUGGESTION, MEETING_SUGGESTION_MEETING_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Return all meeting/ suggestion from the database to remember
    // values for MEETING_SUGGESTION_KEY_REMEMBER_POINT : 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes (for meeting, coach suggestion and start client suggestion)
    // values for MEETING_SUGGESTION_KEY_REMEMBER_POINT : 0 = no remember so far; 20 = remember 24 h; 25 = remember 2 hours; 30 = remember 15 minutes (for end client suggestion)
    Cursor getAllRowsRememberMeetingsAndSuggestion(String function, Long nowTime) {

        String where = "";
        String sort = "";

        Long delta_15min_lowerLimit = 13L * 60L * 1000L; // 13 min in mills
        Long delta_15min_upperLimit = 17L * 60L * 1000L; // 17 min in mills

        Long delta_120min_lowerLimit = 118L * 60L * 1000L; // 118 min in mills
        Long delta_120min_upperLimit = 122L * 60L * 1000L; // 122 min in mills

        Long delta_1440min_lowerLimit = (24L * 60L * 60L * 1000L) - (2L * 60L * 1000L); // 1438 (24h) min in mills
        Long delta_1440min_upperLimit = 24L * 60L * 60L * 1000L + (2L * 60L * 1000L); // 1442 (24h) min in mills

        SQLiteDatabase db = this.getWritableDatabase();

        switch (function) {

            case "remember_meeting_15min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1 AND " + MEETING_SUGGESTION_KEY_DATE1 + ">" + (nowTime + delta_15min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_DATE1 + "<" + (nowTime + delta_15min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=15";
                sort = MEETING_SUGGESTION_KEY_DATE1 + " ASC";
                break;

            case "remember_meeting_120min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1 AND " + MEETING_SUGGESTION_KEY_DATE1 + ">" + (nowTime + delta_120min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_DATE1 + "<" + (nowTime + delta_120min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=10";
                sort = MEETING_SUGGESTION_KEY_DATE1 + " ASC";
                break;

            case "remember_meeting_1440min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1 AND " + MEETING_SUGGESTION_KEY_DATE1 + ">" + (nowTime + delta_1440min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_DATE1 + "<" + (nowTime + delta_1440min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=5";
                sort = MEETING_SUGGESTION_KEY_DATE1 + " ASC";
                break;

            case "remember_meeting_next_wakeup":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1 AND " + MEETING_SUGGESTION_KEY_DATE1 + ">=" + (nowTime + delta_15min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=15";
                sort = MEETING_SUGGESTION_KEY_DATE1 + " ASC";
                break;

            case "remember_suggestion_end_15min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=2 AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + ">" + (nowTime + delta_15min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + "<" + (nowTime + delta_15min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_VOTEDATE + "=0 AND " + MEETING_SUGGESTION_KEY_VOTEAUTHOR + "='' AND "+ MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=15";
                sort =  MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + " ASC";
                break;

            case "remember_suggestion_end_120min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=2 AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + ">" + (nowTime + delta_120min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + "<" + (nowTime + delta_120min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_VOTEDATE + "=0 AND " + MEETING_SUGGESTION_KEY_VOTEAUTHOR + "='' AND "+ MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=10";
                sort =  MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + " ASC";
                break;

            case "remember_suggestion_end_1440min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=2 AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + ">" + (nowTime + delta_1440min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + "<" + (nowTime + delta_1440min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_VOTEDATE + "=0 AND " + MEETING_SUGGESTION_KEY_VOTEAUTHOR + "='' AND "+ MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=5";
                sort =  MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + " ASC";
                break;

            case "remember_suggestion_next_wakeup":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=2 AND " + MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + ">" + (nowTime + delta_15min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_VOTEDATE + "=0 AND " + MEETING_SUGGESTION_KEY_VOTEAUTHOR + "='' AND "+ MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=15";
                sort =  MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME + " ASC";
                break;

            case "remember_client_suggestion_start_15min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + ">" + (nowTime + delta_15min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + "<" + (nowTime + delta_15min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT + "='' AND " + MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=15";
                sort =  MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + " ASC";
                break;

            case "remember_client_suggestion_start_120min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + ">" + (nowTime + delta_120min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + "<" + (nowTime + delta_120min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT + "='' AND "+ MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=10";
                sort =  MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + " ASC";
                break;

            case "remember_client_suggestion_start_1440min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + ">" + (nowTime + delta_1440min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + "<" + (nowTime + delta_1440min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT + "='' AND "+ MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=5";
                sort =  MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + " ASC";
                break;

            case "remember_client_suggestion_next_wakeup":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + ">" + (nowTime + delta_15min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT + "='' AND " + MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=15";
                sort =  MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + " ASC";
                break;

            case "remember_client_suggestion_end_15min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + ">" + (nowTime + delta_15min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + "<" + (nowTime + delta_15min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT + "='' AND " + MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=30";
                sort =  MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + " ASC";
                break;

            case "remember_client_suggestion_end_120min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + ">" + (nowTime + delta_120min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + "<" + (nowTime + delta_120min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT + "='' AND "+ MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=25";
                sort =  MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + " ASC";
                break;

            case "remember_client_suggestion_end_1440min":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + ">" + (nowTime + delta_1440min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + "<" + (nowTime + delta_1440min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT + "='' AND "+ MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=20";
                sort =  MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + " ASC";
                break;

            case "remember_client_suggestion_end_wakeup":
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=4 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE + ">" + (nowTime + delta_15min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0 AND " + MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT + "='' AND " + MEETING_SUGGESTION_KEY_REMEMBER_POINT + "!=30";
                sort =  MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE + " ASC";
                break;

            default:
                where = MEETING_SUGGESTION_KEY_MEETING_KATEGORIE + "=1 AND " + MEETING_SUGGESTION_KEY_DATE1 + ">" + (nowTime - delta_15min_lowerLimit) + " AND " + MEETING_SUGGESTION_KEY_DATE1 + "<" + (nowTime + delta_15min_upperLimit) + " AND " + MEETING_SUGGESTION_KEY_MEETING_CANCELED + "=0";
                sort = MEETING_SUGGESTION_KEY_DATE1 + " ASC";
                break;
        }

        Cursor c = db.query(true, DATABASE_TABLE_MEETING_SUGGESTION, MEETING_SUGGESTION_MEETING_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // update remember status of meeting/ suggestion with rowId
    // remember points are: 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
    boolean updateStatusRememberMeetingAndSuggestion(Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId ;

        // Create row with new status
        ContentValues newValues = new ContentValues();
        newValues.put(MEETING_SUGGESTION_KEY_REMEMBER_POINT, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }


    // update status of meeting/ suggestion with rowId
    boolean updateStatusMeetingAndSuggestion(Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row with new status
        ContentValues newValues = new ContentValues();
        newValues.put(MEETING_SUGGESTION_MEETING_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }


    boolean updateSuggestionVoteAndCommentByClient(int tmpResultVote1, int tmpResultVote2, int tmpResultVote3, int tmpResultVote4, int tmpResultVote5, int tmpResultVote6, Long tmpVoteDate, Long tmpVoteLocaleDate, String tmpVoteAuthor, String tmpClientCommentAuthor, Long tmpClientCommentDate, Long tmpClientCommentLocaleDate, String tmpClientCommentText, Long clientVoteDbId, int tmpStatus, int timerStatus, String updateOrder) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + clientVoteDbId;

        // Create rows data:
        ContentValues newValues = new ContentValues();

        // set vote data
        newValues.put(MEETING_SUGGESTION_KEY_VOTE1, tmpResultVote1);
        newValues.put(MEETING_SUGGESTION_KEY_VOTE2, tmpResultVote2);
        newValues.put(MEETING_SUGGESTION_KEY_VOTE3, tmpResultVote3);
        newValues.put(MEETING_SUGGESTION_KEY_VOTE4, tmpResultVote4);
        newValues.put(MEETING_SUGGESTION_KEY_VOTE5, tmpResultVote5);
        newValues.put(MEETING_SUGGESTION_KEY_VOTE6, tmpResultVote6);
        newValues.put(MEETING_SUGGESTION_KEY_VOTEAUTHOR, tmpVoteAuthor);
        newValues.put(MEETING_SUGGESTION_KEY_VOTEDATE, tmpVoteDate);
        newValues.put(MEETING_SUGGESTION_KEY_VOTELOCALEDATE, tmpVoteLocaleDate);
        newValues.put(MEETING_SUGGESTION_MEETING_KEY_STATUS, tmpStatus);

        // set client comment suggestion data
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_DATE, tmpClientCommentDate);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_LOCALE_DATE, tmpClientCommentLocaleDate);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_AUTHOR, tmpClientCommentAuthor);
        newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_TEXT, tmpClientCommentText);

        // set timer status
        newValues.put(MEETING_SUGGESTION_KEY_TIMER_STATUS, timerStatus);

        // set timer status
        newValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, updateOrder);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }


    boolean postUpdateWriteTimeMeetingSuggestion(Long dbId, String updateOrder, Long globalServerTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + dbId ;

        // Create row with new status
        ContentValues newValues = new ContentValues();

        switch (updateOrder) {

            case "update_client_canceled_server_time":
                newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME, globalServerTime);
                newValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, "");
                break;
            case "update_client_vote_comment_time_voteandcomment":
                newValues.put(MEETING_SUGGESTION_KEY_VOTEDATE, globalServerTime);
                newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_DATE, globalServerTime);
                newValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, "");
                break;
            case "update_client_vote_comment_time_onlyvote":
                newValues.put(MEETING_SUGGESTION_KEY_VOTEDATE, globalServerTime);
                newValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, "");
                break;
            case "update_client_vote_comment_time_onlycomment":
                newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_DATE, globalServerTime);
                newValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, "");
                break;

            case "update_suggestion_from_client_server_time":
                newValues.put(MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TIME, globalServerTime);
                newValues.put(MEETING_SUGGESTION_KEY_UPDATE_ORDER, "");
                break;
        }

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }



    // update status of timer for meeting/suggestion/suggestion from client
    // 0= timer can run; 1= timer finish!
    boolean updateTimerStatusMeetingSuggestion (Long rowId, int timerStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create timer status with status
        ContentValues newValues = new ContentValues();
        newValues.put(MEETING_SUGGESTION_KEY_TIMER_STATUS, timerStatus);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_SUGGESTION, newValues, where, null) != 0;
    }






    /********************************* End!! TABLES FOR FUNCTION: Meeting ***************************************/
    /****************************************************************************************************************************/




    /********************************* TABLES FOR FUNCTION: Involved Person ******************************************/

    // Add a new case involved person in db
    void insertNewInvolvedPerson(String tmpName, String tmpFunction, String tmpPrecenseTextOne, String tmpPrecenseTextTwo, Long tmpPrecenseTwoStart, Long tmpPrecenseTwoEnd, Long tmpModifiedTime, int tmpNewEntry) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(INVOLVED_PERSON_KEY_NAME, tmpName);
        initialValues.put(INVOLVED_PERSON_KEY_FUNCTION, tmpFunction);
        initialValues.put(INVOLVED_PERSON_KEY_PRESENCE_TEXT_ONE, tmpPrecenseTextOne);
        initialValues.put(INVOLVED_PERSON_KEY_PRESENCE_TEXT_TWO, tmpPrecenseTextTwo);
        initialValues.put(INVOLVED_PERSON_KEY_PRESENCE_TWO_START, tmpPrecenseTwoStart);
        initialValues.put(INVOLVED_PERSON_KEY_PRESENCE_TWO_END, tmpPrecenseTwoEnd);
        initialValues.put(INVOLVED_PERSON_KEY_MODIFIED_TIME, tmpModifiedTime);
        initialValues.put(INVOLVED_PERSON_KEY_NEW_ENTRY, tmpNewEntry);

        // Insert it into the database.
        db.insert(DATABASE_TABLE_INVOLVED_PERSON, null, initialValues);
    }


    // Delete a row from the database, by rowId (primary key)
    boolean deleteRowInvolvedPerson(long rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE_INVOLVED_PERSON, where, null) != 0;
    }


    // delete all content from table involved person
    void deleteTableInvolvedPerson () {

        Cursor c = getInvolvedPerson("all");
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);

        if (c != null && c.moveToFirst()) {
            do {
                deleteRowInvolvedPerson(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }


    // Return selected persons with function like coach or client or both
    Cursor getInvolvedPerson (String functionOfPerson) {

        String where = "";
        String sort = "";

        SQLiteDatabase db = this.getWritableDatabase();

        switch (functionOfPerson) {

            case "coach":
                where = INVOLVED_PERSON_KEY_FUNCTION + "='coach'";
                sort = INVOLVED_PERSON_KEY_NAME + " DESC";
                break;
            case "client":
                where = INVOLVED_PERSON_KEY_FUNCTION + "='client'";
                sort = INVOLVED_PERSON_KEY_NAME + " DESC";
                break;
            case "all":
                where = INVOLVED_PERSON_KEY_FUNCTION + "='client' OR " + INVOLVED_PERSON_KEY_FUNCTION + "='coach'";
                sort = INVOLVED_PERSON_KEY_NAME + " DESC";
                break;
            default:
                where = INVOLVED_PERSON_KEY_FUNCTION + "='coach'";
                sort = INVOLVED_PERSON_KEY_NAME + " DESC";
                break;
        }

        Cursor c = db.query(true, DATABASE_TABLE_INVOLVED_PERSON, INVOLVED_PERSON_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;

    }
    /********************************* End!! TABLES FOR FUNCTION: Involved Person ***************************************/





    /********************************* TABLES FOR FUNCTION: Message  ******************************************/

    // Add a new set of values to the database.
    long insertRowMessage(String authorName, Long localeTime, Long writeTime, String message, int role, int status, Boolean newEntry, long upload_time, int anonymous, String source) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(MESSAGE_KEY_AUTHOR_NAME, authorName);
        initialValues.put(MESSAGE_KEY_WRITE_TIME, writeTime);
        initialValues.put(MESSAGE_KEY_LOCAL_TIME, localeTime);
        initialValues.put(MESSAGE_KEY_MESSAGE, message);
        initialValues.put(MESSAGE_KEY_ROLE, role); //(role: 0= left; 1= right;)
        initialValues.put(MESSAGE_KEY_STATUS, status);
        initialValues.put(MESSAGE_KEY_UPLOAD_TIME, upload_time);
        initialValues.put(MESSAGE_KEY_ANONYMOUS, anonymous);
        initialValues.put(MESSAGE_KEY_SOURCE, source);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(MESSAGE_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(MESSAGE_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_MESSAGE, null, initialValues);

    }


    // Delete a row from the database, by rowId (primary key)
    boolean deleteRowMessage(long rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE_MESSAGE, where, null) != 0;
    }


    void deleteAllMessages() {

        Cursor c = getAllRowsMessages();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);

        if (c.moveToFirst()) {
            do {
                deleteRowMessage(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }


    // Return all data in the database. messages sorted by write time ASC
    Cursor getAllRowsMessages() {

        SQLiteDatabase db = this.getWritableDatabase();

        // sort string
        String sort = MESSAGE_KEY_WRITE_TIME + " ASC";

        Cursor c = db.query(true, DATABASE_TABLE_MESSAGE, MESSAGE_ALL_KEYS,
                null, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    // Get a specific row (by rowId)
    Cursor getOneRowMessage(long rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        Cursor c = db.query(true, DATABASE_TABLE_MESSAGE, MESSAGE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // update status message in table message
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external message
    boolean updateStatusMessage(Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(MESSAGE_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MESSAGE, newValues, where, null) != 0;
    }


    // Get all connect book messages with status = 0 (Ready to send) and role = 1 (own messages)
    Cursor getAllReadyToSendMessages() {

        SQLiteDatabase db = this.getWritableDatabase();

        // status = 0 and role = 1
        String where = MESSAGE_KEY_STATUS + "=0 AND " + MESSAGE_KEY_ROLE + "=1";

        Cursor c = db.query(true, DATABASE_TABLE_MESSAGE, MESSAGE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return cursor
        return c;
    }


    // Get the number of new rows in message
    int getCountNewEntryMessage() {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = MESSAGE_KEY_NEW_ENTRY + "=1";

        Cursor c = db.query(true, DATABASE_TABLE_MESSAGE, MESSAGE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // delete status new entry in table message for rowId.
    boolean deleteStatusNewEntryMessage(int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(MESSAGE_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MESSAGE, newValues, where, null) != 0;
    }




    // update write time for message from locale time to server time in table message
    boolean updateWriteTimeMessage (Long rowId, Long writeTimeFromServer) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row write time from server
        ContentValues newValues = new ContentValues();
        newValues.put(MESSAGE_KEY_WRITE_TIME, writeTimeFromServer);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MESSAGE, newValues, where, null) != 0;
    }

    /********************************* END TABLES FOR FUNCTION: Message  ******************************************/
    /****************************************************************************************************************************/

    // delete all content from all tables, call by init process
    void initDeleteAllContentFromTables() {

        SQLiteDatabase db = this.getWritableDatabase();

        // delete content from chat messages table
        db.delete(DATABASE_TABLE_CHAT_MESSAGE, null, null);

        // delete content from meeting/suggestion table
        db.delete(DATABASE_TABLE_MEETING_SUGGESTION, null, null);

        // delete content from debetable goals comment table
        db.delete(DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, null, null);

        // delete content from jointly goals evaluation table
        db.delete(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE, null, null);

        // delete content from jointly goals comment table
        db.delete(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, null, null);

        // delete content from jointly goals table
        db.delete(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, null, null);

        // delete content from arrangement evaluate table
        db.delete(DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE, null, null);

        // delete content from arrangement table
        db.delete(DATABASE_TABLE_OUR_ARRANGEMENT, null, null);

        // delete content from arrangement sketch comment table
        db.delete(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, null, null);

        // delete content from arrangement comment table
        db.delete(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, null, null);

        // delete content from involved person table
        db.delete(DATABASE_TABLE_INVOLVED_PERSON, null, null);

    }




}





