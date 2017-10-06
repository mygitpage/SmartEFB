package de.smart_efb.efbapp.smartefb;


/**
 * Created by ich on 20.03.16.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.sql.Struct;
import java.sql.Timestamp;


public class DBAdapter extends SQLiteOpenHelper {

    Context dbContext;

    // DB name
    public static final String DATABASE_NAME = "efbDb";

    // Tables name
    public static final String DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT = "ourArrangementCommentTable";
    public static final String DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT = "ourArrangementSketchCommentTable";
    public static final String DATABASE_TABLE_OUR_ARRANGEMENT = "ourArrangementTable";
    public static final String DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE = "ourArrangementEvaluateTable";
    public static final String DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW = "ourGoalsDebetableJointlyGoalsNow";
    public static final String DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT = "ourGoalsJointlyGoalsComment";
    public static final String DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE = "ourGoalsJointlyGoalsEvaluate";
    public static final String DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT = "ourGoalsDebetableGoalsComment";
    public static final String DATABASE_TABLE_MEETING_FIND_MEETING = "meetingFindMeeting";
    public static final String DATABASE_TABLE_CHAT_MESSAGE = "chatMessageTable";

    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 39;

    // Common column names
    public static final String KEY_ROWID = "_id";

    /**********************************************************************************************/
    /************************ Begin of table definitions **********************************************************************/

    // Our Arrangement - column names and numbers
    public static final String OUR_ARRANGEMENT_KEY_ARRANGEMENT = "arrangement";
    public static final String OUR_ARRANGEMENT_KEY_AUTHOR_NAME = "author_name";
    public static final String OUR_ARRANGEMENT_KEY_WRITE_TIME = "arrangement_time";
    public static final String OUR_ARRANGEMENT_KEY_NEW_ENTRY = "new_entry";
    public static final String OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE = "eval_possible";
    public static final String OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT = "sketch";
    public static final String OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME = "sketch_time";
    public static final String OUR_ARRANGEMENT_KEY_SERVER_ID = "serverid";
    public static final String OUR_ARRANGEMENT_KEY_BLOCK_ID = "blockid";
    public static final String OUR_ARRANGEMENT_KEY_CHANGE_TO = "change_to";
    public static final String OUR_ARRANGEMENT_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message
    public static final String OUR_ARRANGEMENT_KEY_LAST_EVAL_TIME = "last_eval_time";

    // All keys from table app settings in a String
    public static final String[] OUR_ARRANGEMENT_ALL_KEYS = new String[] {KEY_ROWID, OUR_ARRANGEMENT_KEY_ARRANGEMENT, OUR_ARRANGEMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_KEY_NEW_ENTRY, OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE, OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT, OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME, OUR_ARRANGEMENT_KEY_SERVER_ID, OUR_ARRANGEMENT_KEY_BLOCK_ID, OUR_ARRANGEMENT_KEY_STATUS, OUR_ARRANGEMENT_KEY_CHANGE_TO, OUR_ARRANGEMENT_KEY_LAST_EVAL_TIME };

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
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_COMMENT = "comment";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME = "author_name";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME = "comment_time";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_UPLOAD_TIME = "upload_time";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID = "blockid";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY = "new_entry";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME = "arrangement_time";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT = "server_id";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message

    // All keys from table app settings in a String
    public static final String[] OUR_ARRANGEMENT_COMMENT_ALL_KEYS = new String[] {KEY_ROWID, OUR_ARRANGEMENT_COMMENT_KEY_COMMENT, OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_COMMENT_KEY_UPLOAD_TIME, OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID, OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY, OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME, OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT, OUR_ARRANGEMENT_COMMENT_KEY_STATUS };

    // SQL String to create our arrangement comment table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT_COMMENT =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_COMMENT + " TEXT not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_UPLOAD_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID + " TEXT not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Our Arrangement Sketch Comment- column names and numbers

    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT = "comment";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1 = "result_q_a";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2 = "result_q_b";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3 = "result_q_c";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME = "author_name";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME = "comment_time";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_UPLOAD_TIME = "upload_time";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID = "blockid";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT = "server_id";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY = "new_entry";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME = "arrangement_time";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message

    // All keys from table sketch comment
    public static final String[] OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS = new String[] {KEY_ROWID, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_UPLOAD_TIME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS };

    // SQL String to create our arrangement sketch comment table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT_SKETCH_COMMENT =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT + " TEXT not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_UPLOAD_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID + " TEXT not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Our Arrangement Evaluate- column names and numbers
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME = "arrangement_time";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_SERVER_ID_ARRANGEMENT = "server_id";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_BLOCKID = "block_id";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1 = "result_q_a";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2 = "result_q_b";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3 = "result_q_c";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4 = "result_q_d";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS = "result_remarks";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME = "result_time";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME = "start_block_time";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME = "end_block_time";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME = "username";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message

    // All keys from table app settings in a String
    public static final String[] OUR_ARRANGEMENT_EVALUATE_ALL_KEYS = new String[] {KEY_ROWID, OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_SERVER_ID_ARRANGEMENT, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME, OUR_ARRANGEMENT_EVALUATE_KEY_STATUS, OUR_ARRANGEMENT_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_BLOCKID};

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
                    + OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME + " STRING not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Chat Messages - column names and numbers
    public static final String CHAT_MESSAGE_KEY_WRITE_TIME = "write_time";
    public static final String CHAT_MESSAGE_KEY_AUTHOR_NAME = "author_name";
    public static final String CHAT_MESSAGE_KEY_MESSAGE = "message";
    public static final String CHAT_MESSAGE_KEY_ROLE = "role";
    public static final String CHAT_MESSAGE_KEY_TX_TIME = "tx_time";
    public static final String CHAT_MESSAGE_KEY_STATUS = "status";
    public static final String CHAT_MESSAGE_KEY_NEW_ENTRY = "new_entry";
    public static final String CHAT_MESSAGE_KEY_MD5_HASH = "md5_hash";

    // All keys from table chat messages in a String
    public static final String[] CHAT_MESSAGE_ALL_KEYS = new String[] {KEY_ROWID, CHAT_MESSAGE_KEY_WRITE_TIME, CHAT_MESSAGE_KEY_AUTHOR_NAME, CHAT_MESSAGE_KEY_MESSAGE, CHAT_MESSAGE_KEY_ROLE, CHAT_MESSAGE_KEY_TX_TIME, CHAT_MESSAGE_KEY_STATUS, CHAT_MESSAGE_KEY_NEW_ENTRY, CHAT_MESSAGE_KEY_MD5_HASH };

    // SQL String to create chat-message-table
    private static final String DATABASE_CREATE_SQL_CHAT_MESSAGE =
            "create table " + DATABASE_TABLE_CHAT_MESSAGE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + CHAT_MESSAGE_KEY_WRITE_TIME + " INTEGER not null, "
                    + CHAT_MESSAGE_KEY_AUTHOR_NAME + " STRING not null, "
                    + CHAT_MESSAGE_KEY_MESSAGE + " TEXT not null, "
                    + CHAT_MESSAGE_KEY_ROLE + " INTEGER not null, "
                    + CHAT_MESSAGE_KEY_TX_TIME + " INTEGER, "
                    + CHAT_MESSAGE_KEY_STATUS + " INTEGER not null, "
                    + CHAT_MESSAGE_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + CHAT_MESSAGE_KEY_MD5_HASH + " TEXT not null"
                    + ");";

    /*************************************************************************************************************************/
    /************************ Our Goals Definitions **************************************************************************/

    // Debetable/Jointly Goals Now - column names and numbers
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL = "goal";
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME = "author_name";
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME = "goal_time";
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY = "new_entry";
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE = "eval_possible";
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE = "jointlyDebetable";
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DEBETABLE_TIME = "debetable_time";
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID = "serverid";
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID = "blockid";
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_CHANGE_TO = "change_to";
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message
    public static final String OUR_GOALS_JOINTLY_DEBETABLE_GOALS_LAST_EVAL_TIME = "last_eval_time";

    // All keys in a String
    public static final String[] OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS = new String[] {KEY_ROWID, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DEBETABLE_TIME, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_CHANGE_TO, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_STATUS, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_LAST_EVAL_TIME};

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
    public static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT = "comment";
    public static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME = "author_name";
    public static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME = "comment_time";
    public static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_UPLOAD_TIME = "upload_time";
    public static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID = "blockid";
    public static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY = "new_entry";
    public static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME = "goal_time";
    public static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL = "server_id";
    public static final String OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message

    // All keys from table app settings in a String
    public static final String[] OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS = new String[] {KEY_ROWID, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_UPLOAD_TIME, OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS };

    // SQL String to create our goals jointly comment table
    private static final String DATABASE_CREATE_SQL_OUR_GOALS_JOINTLY_GOALS_COMMENT =
            "create table " + DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT + " TEXT not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_UPLOAD_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID + " TEXT not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Our Goals Jointly Goals Evaluate- column names and numbers
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_GOAL_TIME = "goal_time";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_SERVER_ID = "server_id";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_BLOCKID = "block_id";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION1 = "result_q_a";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION2 = "result_q_b";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION3 = "result_q_c";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION4 = "result_q_d";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_REMARKS = "result_remarks";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME = "start_block_time";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME = "end_block_time";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_TIME = "result_time";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_USERNAME = "username";
    public static final String OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message

    // All keys from table app settings in a String
    public static final String[] OUR_GOALS_JOINTLY_GOALS_EVALUATE_ALL_KEYS = new String[] {KEY_ROWID, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_GOAL_TIME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION1, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION2, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION3, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION4, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_REMARKS, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_TIME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_USERNAME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_STATUS, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_SERVER_ID, OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_BLOCKID};

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
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME + " INTEGER not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_USERNAME + " STRING not null, "
                    + OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_STATUS + " INTEGER DEFAULT 0"
                    + ");";

    /**********************************************************************************************/
    /**********************************************************************************************/
    // Our Goals Debetable Goals Comment- column names and numbers
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT = "comment";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1 = "result_q_a";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION2 = "result_q_b";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION3 = "result_q_c";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME = "author_name";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME = "comment_time";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_UPLOAD_TIME = "upload_time";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID = "blockid";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID = "server_id";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY = "new_entry";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME = "goal_time";
    public static final String OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message

    // All keys from table app settings in a String
    public static final String[] OUR_GOALS_DEBETABLE_GOALS_COMMENT_ALL_KEYS = new String[] {KEY_ROWID, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION2, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION3, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_UPLOAD_TIME, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID, OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS };

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
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID + " TEXT not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID + " INTEGER not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME + " INTEGER not null, "
                    + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS + " INTEGER DEFAULT 0"
                    + ");";



    /**********************************************************************************************/
    /**********************************************************************************************/
    // Meeting - column names and numbers
    public static final String MEETING_FIND_MEETING_KEY_MEETING_PLACE = "place";
    public static final String MEETING_FIND_MEETING_KEY_NEW_ENTRY = "new_entry";
    public static final String MEETING_FIND_MEETING_KEY_DATE_TIME = "date_time";
    public static final String MEETING_FIND_MEETING_KEY_APPROVAL = "approval";
    public static final String MEETING_FIND_MEETING_KEY_STATUS = "status"; // 0=ready to send, 1=message send, 4=external message

    // All keys from table in a String
    public static final String[] MEETING_FIND_MEETING_ALL_KEYS = new String[] {KEY_ROWID, MEETING_FIND_MEETING_KEY_MEETING_PLACE, MEETING_FIND_MEETING_KEY_NEW_ENTRY, MEETING_FIND_MEETING_KEY_APPROVAL, MEETING_FIND_MEETING_KEY_DATE_TIME, MEETING_FIND_MEETING_KEY_STATUS};

    // SQL String to create find meeting table
    private static final String DATABASE_CREATE_SQL_MEETING_FIND_MEETING =
            "create table " + DATABASE_TABLE_MEETING_FIND_MEETING + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + MEETING_FIND_MEETING_KEY_MEETING_PLACE + " STRING not null, "
                    + MEETING_FIND_MEETING_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + MEETING_FIND_MEETING_KEY_APPROVAL + " INTEGER DEFAULT 0, "
                    + MEETING_FIND_MEETING_KEY_DATE_TIME + " INTEGER not null, "
                    + MEETING_FIND_MEETING_KEY_STATUS + " INTEGER DEFAULT 0"
                    + ");";



    /**********************************************************************************************/
    /************************ End of table definitions **********************************************************************/


    //construtor of DBAdapter
    DBAdapter (Context context) {

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

        // Create table Meeting Find Meeting
        _db.execSQL(DATABASE_CREATE_SQL_MEETING_FIND_MEETING);


    }


    @Override
    // on upgrade -> delete the tables when exits
    public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {

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

        // Destroy table Meeting Find Meeting
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MEETING_FIND_MEETING);



        // Recreate new database:
        onCreate(_db);
    }



    /********************************* TABLES FOR FUNCTION: Chat Message  ******************************************/

    // Add a new set of values to the database.
    public long insertRowChatMessage(String author_name, long writeTime, String message, int role, int status, Boolean newEntry) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(CHAT_MESSAGE_KEY_AUTHOR_NAME, author_name);
        initialValues.put(CHAT_MESSAGE_KEY_WRITE_TIME, writeTime);
        initialValues.put(CHAT_MESSAGE_KEY_MESSAGE, message);
        initialValues.put(CHAT_MESSAGE_KEY_ROLE, role);
        initialValues.put(CHAT_MESSAGE_KEY_STATUS, status);
        initialValues.put(CHAT_MESSAGE_KEY_MD5_HASH, EfbHelperClass.md5(message)); // generate md5 hash from message

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
    public boolean deleteRowChatMessage(long rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE_CHAT_MESSAGE, where, null) != 0;
    }


    public void deleteAllChatMessage() {

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
    public Cursor getAllRowsChatMessage() {

        SQLiteDatabase db = this.getWritableDatabase();

        // sort string
        String sort = CHAT_MESSAGE_KEY_WRITE_TIME + " ASC";

        Cursor c = 	db.query(true, DATABASE_TABLE_CHAT_MESSAGE, CHAT_MESSAGE_ALL_KEYS,
                null, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRowChatMessage(long rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE_CHAT_MESSAGE, CHAT_MESSAGE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateRowChatMessage(long rowId, int write_time, String author_name, String message, int role, int tx_time, int status, Boolean newEntry) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

		// Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(CHAT_MESSAGE_KEY_WRITE_TIME, write_time);
        newValues.put(CHAT_MESSAGE_KEY_AUTHOR_NAME, author_name);
        newValues.put(CHAT_MESSAGE_KEY_MESSAGE, message);
        newValues.put(CHAT_MESSAGE_KEY_ROLE, role);
        newValues.put(CHAT_MESSAGE_KEY_TX_TIME, tx_time);
        newValues.put(CHAT_MESSAGE_KEY_STATUS, tx_time);

        // is it a new entry?
        if (newEntry) {
            newValues.put(CHAT_MESSAGE_KEY_NEW_ENTRY, 1);
        } else {
            newValues.put(CHAT_MESSAGE_KEY_NEW_ENTRY, 0);
        }

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

    public long insertRowOurArrangement(String arrangement, String authorName, long arrangementTime, Boolean newEntry, Boolean sketchCurrent, long sketchTime, int status, int serverId, String blockId, String changeTo) {

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
    public boolean updateRowOurArrangement(String arrangement, String authorName, long arrangementTime, Boolean newEntry, Boolean sketchCurrent, long sketchTime, int status, int serverId, String blockId) {

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
    public boolean deleteRowOurArrangement(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SERVER_ID + "=" + serverId;
        return db.delete(DATABASE_TABLE_OUR_ARRANGEMENT, where, null) != 0;

    }


    // Delete all arrangements with the same blockId
    public boolean deleteAllRowsOurArrangement(String blockId, Boolean sketchCurrent) {

        int tmpStatus;

        SQLiteDatabase db = this.getWritableDatabase();

        if (sketchCurrent) {
            tmpStatus = 1;
        } else {
            tmpStatus = 0;
        }

        String where = OUR_ARRANGEMENT_KEY_BLOCK_ID + "='" + blockId + "' AND " + OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=" + tmpStatus ;

        return db.delete(DATABASE_TABLE_OUR_ARRANGEMENT, where, null) != 0;

    }



    // Return all data from the database (table ourArrangement) -> see order!
    // the result is sorted by DESC
    public Cursor getAllRowsCurrentOurArrangement(String blockID, String order) {

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

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Return sketch arrangmenet from the database (table ourArrangement)
    // the result is sorted by DESC
    public Cursor getAllRowsSketchOurArrangement(String blockID) {

        String where = "";

        SQLiteDatabase db = this.getWritableDatabase();

        // get only arrangments and no sketches
        where = OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=1 AND " + OUR_ARRANGEMENT_KEY_BLOCK_ID + "=" + blockID;

        // sort string
        String sort = KEY_ROWID + " ASC";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }


        return c;
    }


    // Get a specific row from the arrangement (by serverId)
    public Cursor getRowOurArrangement(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=0 AND " + OUR_ARRANGEMENT_KEY_SERVER_ID + "=" + serverId;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get a specific row from the sketch arrangement (by rowId)
    public Cursor getRowSketchOurArrangement(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=1 AND " + OUR_ARRANGEMENT_KEY_SERVER_ID + "=" + serverId;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }



    // Get the number of new rows in arrangement (new entrys, current and sketch) where date is current arrangement date or sketch write time -> no older one!
    public int getCountNewEntryOurArrangement(long currentDateOfArrangement, String currentSketch) {

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

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // delete status new entry in table ourArrangement for arrangment rowId.
    public boolean deleteStatusNewEntryOurArrangement (int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_ARRANGEMENT_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT, newValues, where, null) != 0;
    }




    // change (delete/set) status evaluation possible in table ourArrangement
    public boolean changeStatusEvaluationPossibleOurArrangement (int serverId, String state) {

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
    public boolean changeStatusEvaluationPossibleAllOurArrangement (String blockId, String state) {

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
    public long insertRowOurArrangementComment(String comment, String authorName, long commentTime, long upload_time, String blockid, Boolean newEntry, long currentDateOfArrangement, int status, int serverId ) {

       SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_UPLOAD_TIME, upload_time);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID, blockid);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME, currentDateOfArrangement);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT, serverId);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_STATUS, status);

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
    public Cursor getAllRowsOurArrangementComment(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT + "=" + serverId;

        // sort string
        String sort = OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME + " DESC";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in all comment for all arrangement (new entrys) where block id are current arrangement block
    public int getCountAllNewEntryOurArrangementComment(String blockIdOfArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfArrangement;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        db.close(); // close db connection

        // return how many
        return c.getCount();
    }




    // delete status new entry in table ourArrangementComment.
    public boolean deleteStatusNewEntryOurArrangementComment (int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, newValues, where, null) != 0;
    }


    // delete all comments with the same block id
    public Boolean deleteAllRowsOurArrangementComment (String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID + "='" + blockId + "'";

        return db.delete(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, where, null) != 0;

    }


    // Return one comment from the database for arrangement with row id = dbid (table ourArrangementComment)
    public Cursor getOneRowOurArrangementComment(Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }




    // update status comment in table ourArrangementComment
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Comment
    public boolean updateStatusOurArrangementComment (Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_COMMENT_KEY_STATUS, status);

        Log.d("DB Change Stat Comment", "ROW ID:"+rowId);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, newValues, where, null) != 0;
    }




    // Get all comments with status = 0 (Ready to send) and block id of current arrangement
    public Cursor getAllReadyToSendComments (String blockIdOfArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        // status = 0 and block id = blockIdOfArrangement
        String where = OUR_ARRANGEMENT_COMMENT_KEY_STATUS + "=0 AND " + OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfArrangement;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        // return cursor
        return c;
    }




    /********************************* End!! TABLES FOR FUNCTION: Our Arrangement Comment ***************************************/
    /****************************************************************************************************************************/





    /********************************* TABLES FOR FUNCTION: Our Arrangement Sketch Comment ******************************************/

    // Add a new set of values to ourArrangementSketchComment .
    public long insertRowOurArrangementSketchComment(String comment, int question_a, int question_b, int question_c, String authorName, long commentTime, long upload_time, String blockid, Boolean newEntry, long currentDateOfArrangement, int status, int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1, question_a);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2, question_b);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3, question_c);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_UPLOAD_TIME, upload_time);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID, blockid);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME, currentDateOfArrangement);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS, status);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT, serverId);

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
    public Cursor getAllRowsOurArrangementSketchComment(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT + "=" + serverId;

        // sort string
        String sort = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME + " DESC";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in all comment for all sketch arrangement (new entrys) where date is current sketch arrangement date -> no older one!
    public int getCountAllNewEntryOurArrangementSketchComment(String blockIdOfSketchArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfSketchArrangement;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // delete status new entry in table ourArrangementSketchComment.
    public boolean deleteStatusNewEntryOurArrangementSketchComment (int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, newValues, where, null) != 0;
    }


    // delete all comments for sketch arrangements with the same block id
    public Boolean deleteAllRowsOurArrangementSketchComment (String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID + "='" + blockId + "'";

        return db.delete(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, where, null) != 0;

    }


    // Return one comment from the database for sketch arrangement with row id = dbid (table ourArrangementSketchComment)
    public Cursor getOneRowOurArrangementSketchComment(Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }




    // update status comment in table ourArrangementSketchComment
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Comment
    public boolean updateStatusOurArrangementSketchComment (Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, newValues, where, null) != 0;
    }



    // Get all sketch comments with status = 0 (Ready to send) and block id of sketch arrangement
    public Cursor getAllReadyToSendSketchComments (String blockIdOfSketchArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        // status = 0 and block id = blockIdOfSketchArrangement
        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS + "=0 AND " + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfSketchArrangement;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        // return cursor
        return c;
    }


    /********************************* End!! TABLES FOR FUNCTION: Our Arrangement Sketch Comment ***************************************/
    /****************************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Our Arrangement Evaluate ******************************************/

    // Add a new set of values to ourArrangementEvaluate .
    public long insertRowOurArrangementEvaluate(int serverId, long currentDateOfArrangement, int resultQuestion1, int resultQuestion2, int resultQuestion3, int resultQuestion4, String resultRemarks, long resultTime, String userName, int status, long startEvaluationTime, long endEvaluationTime, String blockId) {

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
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME, startEvaluationTime);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME, endEvaluationTime);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME, userName);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_STATUS, status);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE, null, initialValues);

    }



    public Cursor getOneRowEvaluationResultArrangement (Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE, OUR_ARRANGEMENT_EVALUATE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;

    }




    // update status evaluation in table ourArrangementEvaluate
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Evaluation
    public boolean updateStatusOurArrangementEvaluation (Long rowId, int status) {

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
    public Cursor getAllReadyToSendArrangementEvaluationResults () {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_EVALUATE_KEY_STATUS + "=0";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE, OUR_ARRANGEMENT_EVALUATE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // set last evaluation time in choosen (serverId) arrangement in table our arrangement
    public boolean setEvaluationTimePointForArrangement (int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SERVER_ID + "=" + serverId;

        // Create row's data:
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_ARRANGEMENT_KEY_LAST_EVAL_TIME, System.currentTimeMillis());


        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_ARRANGEMENT, newValues, where, null) != 0;

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

    public long insertRowOurGoals(String goal, String authorName, long goalTime, Boolean newEntry, Boolean jointlyDebetable, long debetableTime, int status, int serverId, String blockId, String changeTo) {

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
    public boolean deleteRowOurGoals(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + "=" + serverId;
        return db.delete(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, where, null) != 0;
    }




    // Change an existing row to be equal to oldMd5.
    public boolean updateRowOurGoals(String goal, String authorName, long goalTime, Boolean newEntry, Boolean jointlyDebetable, long debetableTime, int status, int serverId, String blockId) {

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
    public Cursor getAllJointlyRowsOurGoals (String blockID, String order) {

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

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;

    }


    // Return debetable goals from the database (table ourGoals)
    // the result is sorted by DESC
    public Cursor getAllDebetableRowsOurGoals(String blockId) {

        String where = "";

        SQLiteDatabase db = this.getWritableDatabase();

        // get only debetable goals
        where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE + "=1 AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID + "=" + blockId;;

        // sort string
        String sort = KEY_ROWID + " ASC";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get a specific jointly row from the goals (by serverId)
    public Cursor getJointlyRowOurGoals(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE + "=0 AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + "=" + serverId;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get a specific debetable row from the goals (by rowId)
    public Cursor getDebetableRowOurGoals(int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE + "=1 AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + "=" + serverId;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }



    // Get the number of new rows in goals (new entrys, jointly and debetable) where date is write time -> no older one!
    public int getCountNewEntryOurGoals(long currentDateOfGoals) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where;

        where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY + "=1 AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME + "=" + currentDateOfGoals;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, OUR_GOALS_JOINTLY_DEBETABLE_GOALS_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // delete status new entry in table ourGoals for goal rowId.
    public boolean deleteStatusNewEntryOurGoals (int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, newValues, where, null) != 0;
    }




    // change (delete/set) status evaluation poosible in table ourGoals for one goal (rowId)
    public boolean changeStatusEvaluationPossibleOurGoals (int serverId, String state) {


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
    public boolean changeStatusEvaluationPossibleAllOurGoals (String blockId, String state) {

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
    public boolean deleteAllRowsOurGoals(String blockId, Boolean jointlyDebetable) {

        int tmpStatus;

        SQLiteDatabase db = this.getWritableDatabase();

        if (jointlyDebetable) {
            tmpStatus = 1;
        } else {
            tmpStatus = 0;
        }

        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID + "='" + blockId + "' AND " + OUR_GOALS_JOINTLY_DEBETABLE_GOALS_DIFFERENCE + "=" + tmpStatus ;

        return db.delete(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, where, null) != 0;

    }






    /********************************* End!! TABLES FOR FUNCTION: Our Debetable/Jointly Goals ******************************************/
    /***********************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Our Goals Jointly Goals Comment ******************************************/
    // Add a new set of values to ourGoalsJointlyGoalsComment .
    public long insertRowOurGoalJointlyGoalComment(String comment, String authorName, long commentTime, long upload_time, String blockid, Boolean newEntry, long currentDateOfGoal, int status, int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_UPLOAD_TIME, upload_time);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID, blockid);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME, currentDateOfGoal);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL, serverId);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS, status);

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
    public Cursor getAllRowsOurGoalsJointlyGoalsComment(int serverGoalId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL + "=" + serverGoalId;

        // sort string
        String sort = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME + " DESC";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in all comment for all jointly goals (new entrys) where date is current goal date -> no older one!
    public int getCountAllNewEntryOurGoalsJointlyGoalsComment(long currentDateOfJointlyGoal) {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME + "=" + currentDateOfJointlyGoal;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }



    // Get the number of new rows in comment for choosen jointly goal, look jointlyGoalRowId (new entrys)
    public int getCountNewEntryOurGoalsJointlyGoalComment(int  jointlyGoalServerId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL + "=" + jointlyGoalServerId;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }


    // delete status new entry in table ourArrangementComment.
    public boolean deleteStatusNewEntryOurGoalsJointlyGoalComment (int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, newValues, where, null) != 0;
    }


    // delete all comments with the same block id
    public Boolean deleteAllRowsOurJointlyGoalsComment (String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID + "='" + blockId + "'";

        return db.delete(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, where, null) != 0;

    }



    // Return one jointly comment from the database for goals with row id = dbid (table ourGoalsJointlyGoalsComment)
    public Cursor getOneRowOurGoalsJointlyComment(Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }




    // update status comment in table ourGoalsJointlyGoalsComment
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Comment
    public boolean updateStatusOurGoalsJointlyGoalsComment (Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS, status);

        Log.d("DB Change Stat Comment", "ROW ID:"+rowId);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, newValues, where, null) != 0;
    }




    // Get all comments with status = 0 (Ready to send) and block id of current jointly goals
    public Cursor getAllReadyToSendJointlyGoalsComments (String blockIdOfGoals) {

        SQLiteDatabase db = this.getWritableDatabase();

        // status = 0 and block id = blockIdOfArrangement
        String where = OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS + "=0 AND " + OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfGoals;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_COMMENT, OUR_GOALS_JOINTLY_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        // return cursor
        return c;
    }






    /********************************* End!! TABLES FOR FUNCTION: Our Goals Jointly Goals Comment ***************************************/
    /****************************************************************************************************************************/


    /********************************* TABLES FOR FUNCTION: Our Goals Jointly Goals Evaluate ******************************************/

    // Add a new set of values to ourGoalsJointlyGoalsEvaluate .
    public long insertRowOurGoalsJointlyGoalEvaluate(int serverGoalId, long currentDateOfGoal, int resultQuestion1, int resultQuestion2, int resultQuestion3, int resultQuestion4, String resultRemarks, long resultTime, String userName, int status, long startEvaluationTime, long endEvaluationTime, String blockId) {

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
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME, startEvaluationTime);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME, endEvaluationTime);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_USERNAME, userName);
        initialValues.put(OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_STATUS, status);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE, null, initialValues);
    }



    public Cursor getOneRowEvaluationResultGoals (Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE, OUR_GOALS_JOINTLY_GOALS_EVALUATE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;

    }


    // get all evaluation results in table ourGoalsEvaluate with status = 0
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Evaluation
    public Cursor getAllReadyToSendGoalsEvaluationResults () {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_STATUS + "=0";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE, OUR_GOALS_JOINTLY_GOALS_EVALUATE_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }



    // update status evaluation in table jointly goals evaluation
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Evaluation
    public boolean updateStatusOurGoalsEvaluation (Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_GOALS_EVALUATE, newValues, where, null) != 0;
    }


    // set last evaluation time in choosen (serverId) goal in table jointly debetable goal
    public boolean setEvaluationTimePointForGoal (int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID + "=" + serverId;

        // Create row's data:
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_GOALS_JOINTLY_DEBETABLE_GOALS_LAST_EVAL_TIME, System.currentTimeMillis());

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NOW, newValues, where, null) != 0;
    }


    /********************************* End!! TABLES FOR FUNCTION: Our Goals Jointly Goals Evaluate ***************************************/





    /********************************* TABLES FOR FUNCTION: Our Goals Debetable Goals Comment ******************************************/

    // Add a new set of values to ourGoalsDebetableGoalsComment .
    public long insertRowOurGoalsDebetableGoalsComment(String comment, int question_a, int question_b, int question_c, String authorName, long commentTime, long upload_time, String blockid, Boolean newEntry, long currentDateOfDebetableGoal, int status, int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1, question_a);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION2, question_b);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION3, question_c);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_UPLOAD_TIME, upload_time);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID, blockid);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME, currentDateOfDebetableGoal);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS, status);
        initialValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID, serverId);

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
    public Cursor getAllRowsOurGoalsDebetableGoalsComment(int serverGoalId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID + "=" + serverGoalId;

        // sort string
        String sort = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME + " DESC";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, OUR_GOALS_DEBETABLE_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;

    }


    // Get the number of new rows in all comment for all debetable goals (new entrys) where date is current debetable goal date -> no older one!
    public int getCountAllNewEntryOurGoalsDebetableGoalsComment(long currentDateOfGoal) {

        SQLiteDatabase db = this.getWritableDatabase();



        // new_entry = 1 (true)?
        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME + "=" + currentDateOfGoal;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, OUR_GOALS_DEBETABLE_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();

    }




    // Return one comment from the database for debetable goals with row id = dbid (table DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT)
    public Cursor getOneRowOurGoalsDebetableComment (Long dbId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // data filter
        String where = KEY_ROWID + "=" + dbId;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, OUR_GOALS_DEBETABLE_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }



    // update status comment in table DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT
    // status = 0 -> ready to send, = 1 -> sucsessfull send, = 4 -> external Comment
    public boolean updateStatusOurGoalsDebetableComment (Long rowId, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row status with status
        ContentValues newValues = new ContentValues();
        newValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS, status);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, newValues, where, null) != 0;
    }



    // delete status new entry in table ourGoalsDebetableGoalsComment.
    public boolean deleteStatusNewEntryOurGoalsDebetableGoalsComment (int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, newValues, where, null) != 0;
    }



    // delete all comments for debetable goals with the same block id
    public Boolean deleteAllRowsOurGoalsDebetableComment (String blockId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID + "='" + blockId + "'";

        return db.delete(DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, where, null) != 0;

    }




    // Get all debetable comments with status = 0 (Ready to send) and block id of current debetable goals
    public Cursor getAllReadyToSendDebetableComments (String blockIdOfGoals) {

        SQLiteDatabase db = this.getWritableDatabase();

        // status = 0 and block id = blockIdOfDebetableGoals
        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS + "=0 AND " + OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID + "=" + blockIdOfGoals;

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_GOALS_DEBETABLE_GOALS_COMMENT, OUR_GOALS_DEBETABLE_GOALS_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        // return cursor
        return c;
    }






    /********************************* End!! TABLES FOR FUNCTION: Our Goals Debetable Goals Comment ***************************************/
    /****************************************************************************************************************************/





    /********************************* TABLES FOR FUNCTION: Meeting ******************************************/

    // Add a new meeting date and time suggestion
    public long insertNewMeetingDateAndTime (long meetingDateAndTime, String meetingPlace, Boolean newEntry, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(MEETING_FIND_MEETING_KEY_MEETING_PLACE, meetingPlace);
        initialValues.put(MEETING_FIND_MEETING_KEY_DATE_TIME, meetingDateAndTime);
        initialValues.put(MEETING_FIND_MEETING_KEY_APPROVAL,0); // --> no approval so far
        initialValues.put(MEETING_FIND_MEETING_KEY_STATUS, status);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(MEETING_FIND_MEETING_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(MEETING_FIND_MEETING_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_MEETING_FIND_MEETING, null, initialValues);

    }


    // Change an existing row to be equal to oldMeetingTime.
    public boolean updateMeetingDateAndTime(long meetingDateAndTime, String meetingPlace, long oldMeetingTime, Boolean newEntry, int status) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = MEETING_FIND_MEETING_KEY_DATE_TIME + "=" + oldMeetingTime;

        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(MEETING_FIND_MEETING_KEY_MEETING_PLACE, meetingPlace);
        newValues.put(MEETING_FIND_MEETING_KEY_DATE_TIME, meetingDateAndTime);
        newValues.put(MEETING_FIND_MEETING_KEY_APPROVAL,0); // --> no approval so far
        newValues.put(MEETING_FIND_MEETING_KEY_STATUS, status);

        // is it a new entry?
        if (newEntry) {
            newValues.put(MEETING_FIND_MEETING_KEY_NEW_ENTRY, 1);
        } else {
            newValues.put(MEETING_FIND_MEETING_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_FIND_MEETING, newValues, where, null) != 0;
    }


    // Delete a row from the database, by oldMeetingTime
    public boolean deleteRowMeetingDateAndTime(Long oldMeetingTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = MEETING_FIND_MEETING_KEY_DATE_TIME + "=" + oldMeetingTime;
        return db.delete(DATABASE_TABLE_MEETING_FIND_MEETING, where, null) != 0;
    }


    // Delete all rows (suggestions) from the database
    public boolean deleteAllRowsMeetingDateAndTime() {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(DATABASE_TABLE_MEETING_FIND_MEETING, null, null) != 0;
    }



    // Return all meetings from the database
    // the result is sorted by DESC
    public Cursor getAllRowsSuggesteMeetings() {

        SQLiteDatabase db = this.getWritableDatabase();

        // sort string
        String sort = MEETING_FIND_MEETING_KEY_DATE_TIME + " DESC";

        Cursor c = 	db.query(true, DATABASE_TABLE_MEETING_FIND_MEETING, MEETING_FIND_MEETING_ALL_KEYS,
                null, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;

    }


    // Return all choosen meetings from the database
    // the result is sorted by DESC
    public Cursor getRowsChoosenSuggesteMeetings() {

        SQLiteDatabase db = this.getWritableDatabase();

        // search string
        String where = MEETING_FIND_MEETING_KEY_APPROVAL + "=1"; // approval = 1 is set

        // sort string
        String sort = MEETING_FIND_MEETING_KEY_DATE_TIME + " DESC";

        Cursor c = 	db.query(true, DATABASE_TABLE_MEETING_FIND_MEETING, MEETING_FIND_MEETING_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;

    }


    // Get the number of new rows in meeting
    public int getCountNewEntryMeetingFindMeeting () {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY + "=1";
        Cursor c = 	db.query(true, DATABASE_TABLE_MEETING_FIND_MEETING, MEETING_FIND_MEETING_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();

    }


    // delete status new entry for meeting with rowId
    public boolean deleteStatusNewEntryMeetingFindMeeting (int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(MEETING_FIND_MEETING_KEY_NEW_ENTRY, 0);

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_FIND_MEETING, newValues, where, null) != 0;
    }


    // set/unset approval for meeting with rowId
    public boolean setUnsetStatusApprovalMeetingFindMeeting (int rowId, Boolean setUnset) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        // set / unset approval for meeting
        if (setUnset) {
            newValues.put(MEETING_FIND_MEETING_KEY_APPROVAL, 1);
        } else {
            newValues.put(MEETING_FIND_MEETING_KEY_APPROVAL, 0);
        }

        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_FIND_MEETING, newValues, where, null) != 0;
    }

    // unset all approval meeting in table
    public boolean unsetAllStatusApprovalMeetingFindMeeting () {

        SQLiteDatabase db = this.getWritableDatabase();

        // Create row new_entry = 0 (not new!)
        ContentValues newValues = new ContentValues();

        newValues.put(MEETING_FIND_MEETING_KEY_APPROVAL, 0);


        // Insert it into the database.
        return db.update(DATABASE_TABLE_MEETING_FIND_MEETING, newValues, null, null) != 0;
    }

    /********************************* End!! TABLES FOR FUNCTION: Meeting ***************************************/
    /****************************************************************************************************************************/



}





