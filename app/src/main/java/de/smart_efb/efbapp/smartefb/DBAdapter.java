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

import java.sql.Timestamp;


public class DBAdapter extends SQLiteOpenHelper {


    // DB name
    public static final String DATABASE_NAME = "efbDb";

    // Tables name
    public static final String DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT = "ourArrangementCommentTable";
    public static final String DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT = "ourArrangementSketchCommentTable";
    public static final String DATABASE_TABLE_OUR_ARRANGEMENT = "ourArrangementTable";
    public static final String DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE = "ourArrangementEvaluateTable";
    public static final String DATABASE_TABLE_CHAT_MESSAGE = "chatMessageTable";

    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 23;


    // Common column names
    public static final String KEY_ROWID = "_id";


    /************************ Begin of table definitions **********************************************************************/

    // Our Arrangement - column names and numbers
    public static final String OUR_ARRANGEMENT_KEY_ARRANGEMENT = "arrangement";
    public static final String OUR_ARRANGEMENT_KEY_AUTHOR_NAME = "author_name";
    public static final String OUR_ARRANGEMENT_KEY_WRITE_TIME = "arrangement_time";
    public static final String OUR_ARRANGEMENT_KEY_NEW_ENTRY = "new_entry";
    public static final String OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE = "eval_possible";
    public static final String OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT = "sketch";
    public static final String OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME = "sketch_time";


    // All keys from table app settings in a String
    public static final String[] OUR_ARRANGEMENT_ALL_KEYS = new String[] {KEY_ROWID, OUR_ARRANGEMENT_KEY_ARRANGEMENT, OUR_ARRANGEMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_KEY_NEW_ENTRY, OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE, OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT, OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME };

    // SQL String to create our arrangement table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_KEY_ARRANGEMENT + " TEXT not null, "
                    + OUR_ARRANGEMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_ARRANGEMENT_KEY_WRITE_TIME + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME + " INTEGER DEFAULT 0"
                    + ");";



    /**********************************************************************************************/
    // Our Arrangement Comment- column names and numbers
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_COMMENT = "comment";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME = "author_name";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME = "comment_time";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT = "id_arrangement";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY = "new_entry";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME = "arrangement_time";


    // All keys from table app settings in a String
    public static final String[] OUR_ARRANGEMENT_COMMENT_ALL_KEYS = new String[] {KEY_ROWID, OUR_ARRANGEMENT_COMMENT_KEY_COMMENT, OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT, OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY };

    // SQL String to create our arrangement comment table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT_COMMENT =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_COMMENT + " TEXT not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME + " INTEGER not null"
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
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ID_ARRANGEMENT = "id_arrangement";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY = "new_entry";
    public static final String OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME = "arrangement_time";


    // All keys from table app settings in a String
    public static final String[] OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS = new String[] {KEY_ROWID, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ID_ARRANGEMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY };

    // SQL String to create our arrangement comment table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT_SKETCH_COMMENT =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT + " TEXT not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ID_ARRANGEMENT + " INTEGER not null, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0, "
                    + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME + " INTEGER not null"
                    + ");";



    /**********************************************************************************************/






    /**********************************************************************************************/
    // Our Arrangement Evaluate- column names and numbers
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME = "arrangement_time";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_ID = "arrangement_id";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1 = "result_q_a";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2 = "result_q_b";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3 = "result_q_c";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4 = "result_q_d";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION5 = "result_q_e"; // this colum could be delete by next database update
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS = "result_remarks";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME = "result_time";
    public static final String OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME = "username";

    // All keys from table app settings in a String
    public static final String[] OUR_ARRANGEMENT_EVALUATE_ALL_KEYS = new String[] {KEY_ROWID, OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_ID, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION5, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS, OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME, OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME};

    // SQL String to create our arrangement comment table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT_EVALUATE =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_ID + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION5 + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS + " TEXT not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME + " STRING not null"
                    + ");";


    /**********************************************************************************************/
    // Chat Messages - column names and numbers
    public static final String CHAT_MESSAGE_KEY_WRITE_TIME = "write_time";
    public static final String CHAT_MESSAGE_KEY_AUTHOR_NAME = "author_name";
    public static final String CHAT_MESSAGE_KEY_MESSAGE = "message";
    public static final String CHAT_MESSAGE_KEY_ROLE = "role";
    public static final String CHAT_MESSAGE_KEY_TX_TIME = "tx_time";
    public static final String CHAT_MESSAGE_KEY_STATUS = "status";
    public static final String CHAT_MESSAGE_KEY_NEW_ENTRY = "new_entry";


    // All keys from table chat messages in a String
    public static final String[] CHAT_MESSAGE_ALL_KEYS = new String[] {KEY_ROWID, CHAT_MESSAGE_KEY_WRITE_TIME, CHAT_MESSAGE_KEY_AUTHOR_NAME, CHAT_MESSAGE_KEY_MESSAGE, CHAT_MESSAGE_KEY_ROLE, CHAT_MESSAGE_KEY_TX_TIME, CHAT_MESSAGE_KEY_STATUS, CHAT_MESSAGE_KEY_NEW_ENTRY };

    // SQL String to create chat-message-table
    private static final String DATABASE_CREATE_SQL_CHAT_MESSAGE =
            "create table " + DATABASE_TABLE_CHAT_MESSAGE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + CHAT_MESSAGE_KEY_WRITE_TIME + " INTEGER not null, "
                    + CHAT_MESSAGE_KEY_AUTHOR_NAME + " STRING not null, "
                    + CHAT_MESSAGE_KEY_MESSAGE + " TEXT not null, "
                    + CHAT_MESSAGE_KEY_ROLE + " INTEGER not null, "
                    + CHAT_MESSAGE_KEY_TX_TIME + " INTEGER, "
                    + CHAT_MESSAGE_KEY_STATUS + " INTEGER not null, "
                    + CHAT_MESSAGE_KEY_NEW_ENTRY + " INTEGER DEFAULT 0"
                    + ");";

    /************************ End of table definitions **********************************************************************/

    //construtor of DBAdapter
    DBAdapter (Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

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

    // Return all data in the database.
    public Cursor getAllRowsChatMessage() {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE_CHAT_MESSAGE, CHAT_MESSAGE_ALL_KEYS,
                where, null, null, null, null, null);

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
     */

    public long insertRowOurArrangement(String arrangement, String authorName, long arrangementTime, Boolean newEntry, Boolean sketchCurrent, long sketchTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_KEY_ARRANGEMENT, arrangement);
        initialValues.put(OUR_ARRANGEMENT_KEY_AUTHOR_NAME, authorName);


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



    // Return all data from the database (table ourArrangement) (equal: write_time = currentDateOfArrangement, smaller: write_time < currentDateOfArrangement)
    // the result is sorted by DESC
    public Cursor getAllRowsCurrentOurArrangement(long currentDateOfArrangement, String equalGreater) {

        String where = "";

        SQLiteDatabase db = this.getWritableDatabase();

        // get only arrangments and no sketches
        where = OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=0 AND ";

        switch (equalGreater) {

            case "equal":
                where += OUR_ARRANGEMENT_KEY_WRITE_TIME + "=" + currentDateOfArrangement;
                break;
            case "smaller":
                where += OUR_ARRANGEMENT_KEY_WRITE_TIME + "<" + currentDateOfArrangement;
                break;
            default:
                where += OUR_ARRANGEMENT_KEY_WRITE_TIME + "=" + currentDateOfArrangement;
                break;
        }

        // sort string
        String sort = OUR_ARRANGEMENT_KEY_WRITE_TIME + " DESC, " + KEY_ROWID + " DESC";


        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Return sketch arrangmenet from the database (table ourArrangement)
    // the result is sorted by DESC
    public Cursor getAllRowsSketchOurArrangement(long currentDateOfArrangement) {

        String where = "";

        SQLiteDatabase db = this.getWritableDatabase();

        // get only arrangments and no sketches
        where = OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=1 AND " + OUR_ARRANGEMENT_KEY_SKETCH_WRITE_TIME + "=" + currentDateOfArrangement;;

        // sort string
        String sort = KEY_ROWID + " DESC";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get a specific row from the arrangement (by rowId)
    public Cursor getRowOurArrangement(int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get a specific row from the sketch arrangement (by rowId)
    public Cursor getRowSketchOurArrangement(int rowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_SKETCH_ARRANGEMENT + "=1 AND " + KEY_ROWID + "=" + rowId;
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




    // change (delete/set) status evaluation poosible in table ourArrangement
    public boolean changeStatusEvaluationPossibleOurArrangement (int rowId, String state) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = KEY_ROWID + "=" + rowId;

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


    // change (delete/set) status evaluation poosible in table ourArrangement for all arrangement with current arrangement time
    public boolean changeStatusEvaluationPossibleAllOurArrangement (long arrangementTime, String state) {

        SQLiteDatabase db = this.getWritableDatabase();

        String where = OUR_ARRANGEMENT_KEY_WRITE_TIME + "=" + arrangementTime;

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
    public long insertRowOurArrangementComment(String comment, String authorName, long commentTime, int idArrangement, Boolean newEntry, long currentDateOfArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT, idArrangement);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME, currentDateOfArrangement);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, null, initialValues);
    }




    // Return all commens from the database for arrangement with arrangement_id = id (table ourArrangementComment)
    // the result is sorted by DESC
    public Cursor getAllRowsOurArrangementComment(int arrangementId) {

        SQLiteDatabase db = this.getWritableDatabase();


        // data filter
        String where = OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT + "=" + arrangementId;

        // sort string
        String sort = OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME + " DESC";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in all comment for all arrangement (new entrys) where date is current arrangement date -> no older one!
    public int getCountAllNewEntryOurArrangementComment(long currentDateOfArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME + "=" + currentDateOfArrangement;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        // return how many
        return c.getCount();
    }



    // Get the number of new rows in comment for choosen arrangement, look arrangementRowId (new entrys)
    public int getCountNewEntryOurArrangementComment(int arrangementRowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true) and choosen arrangement like arrangementRowId?
        String where = OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT + "=" + arrangementRowId;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

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




    /********************************* End!! TABLES FOR FUNCTION: Our Arrangement Comment ***************************************/
    /****************************************************************************************************************************/





    /********************************* TABLES FOR FUNCTION: Our Arrangement Sketch Comment ******************************************/

    // Add a new set of values to ourArrangementSketchComment .
    public long insertRowOurArrangementSketchComment(String comment, int question_a, int question_b, int question_c, String authorName, long commentTime, int idArrangement, Boolean newEntry, long currentDateOfArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1, question_a);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2, question_b);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3, question_c);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ID_ARRANGEMENT, idArrangement);
        initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME, currentDateOfArrangement);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, null, initialValues);
    }




    // Return all comments from the database for sketch arrangement with arrangement_id = id (table ourArrangementSketchComment)
    // the result is sorted by DESC
    public Cursor getAllRowsOurArrangementSketchComment(int arrangementId) {

        SQLiteDatabase db = this.getWritableDatabase();


        // data filter
        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ID_ARRANGEMENT + "=" + arrangementId;

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
    public int getCountAllNewEntryOurArrangementSketchComment(long currentDateOfArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();



        // new_entry = 1 (true)?
        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME + "=" + currentDateOfArrangement;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_SKETCH_COMMENT, OUR_ARRANGEMENT_SKETCH_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        Log.d ("AllNewEntrySketch", "DRIN!!!!!! -> "+c.getCount());


        // return how many
        return c.getCount();
    }



    // Get the number of new rows in comment for choosen sketch arrangement, look arrangementRowId (new entrys)
    public int getCountNewEntryOurArrangementSketchComment(int arrangementRowId) {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true) and choosen arrangement like arrangementRowId?
        String where = OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY + "=1 AND " + OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ID_ARRANGEMENT + "=" + arrangementRowId;
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




    /********************************* End!! TABLES FOR FUNCTION: Our Arrangement Sketch Comment ***************************************/
    /****************************************************************************************************************************/










    /********************************* TABLES FOR FUNCTION: Our Arrangement Evaluate ******************************************/

    // Add a new set of values to ourArrangementEvaluate .
    public long insertRowOurArrangementEvaluate(int arrangementId, long currentDateOfArrangement, int resultQuestion1, int resultQuestion2, int resultQuestion3, int resultQuestion4, String resultRemarks, long resultTime, String userName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_ID, arrangementId);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME, currentDateOfArrangement);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1, resultQuestion1);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2, resultQuestion2);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3, resultQuestion3);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4, resultQuestion4);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION5, 0); // this colum could be delete by next database update
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS, resultRemarks);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME, resultTime);
        initialValues.put(OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME, userName);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_ARRANGEMENT_EVALUATE, null, initialValues);
    }





    // get next arrangement id to evaluate
    // when there is no arrangement anymore the result ID is Zero
    /*
    public int getNextArrangementIdToEvaluate(Long currentDateOfArrangement) {

        SQLiteDatabase db = this.getWritableDatabase();


        // data filter
        String where = OUR_ARRANGEMENT_KEY_WRITE_TIME + "=" + currentDateOfArrangement + " AND " + OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE + "=1";

        // sort string
        String sort = KEY_ROWID + " DESC";

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, sort, null);

        if (c != null) { // is there an arrangement?
            c.moveToFirst();
            return c.getInt(c.getColumnIndex(DBAdapter.KEY_ROWID)); // return arrangement id
        }

        return 0; // no -> return Zero


    }
    */



    /********************************* End!! TABLES FOR FUNCTION: Our Arrangement Comment ***************************************/
    /****************************************************************************************************************************/



}





