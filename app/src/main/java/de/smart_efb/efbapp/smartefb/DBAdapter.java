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


    // Look at:
    // http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/


    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////

    // DB name
    public static final String DATABASE_NAME = "efbDb";

    // Tables name
    public static final String DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT = "ourArrangementCommentTable";
    public static final String DATABASE_TABLE_OUR_ARRANGEMENT = "ourArrangementTable";
    public static final String DATABASE_TABLE_CHAT_MESSAGE = "chatMessageTable";

    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 15;


    // Context of application who uses us.
    private final Context context;


    // For logging:
    private static final String TAG = "smartefb.DBAdapter";

    // Common column names
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;


    /************************ Begin of table definitions **********************************************************************/

    // Our Arrangement - column names and numbers
    public static final String OUR_ARRANGEMENT_KEY_ARRANGEMENT = "arrangement";
    public static final String OUR_ARRANGEMENT_KEY_AUTHOR_NAME = "author_name";
    public static final String OUR_ARRANGEMENT_KEY_WRITE_TIME = "arrangement_time";
    public static final String OUR_ARRANGEMENT_KEY_NEW_ENTRY = "new_entry";

    public static final int OUR_ARRANGEMENT_COL_ARRANGEMENT = 1;
    public static final int OUR_ARRANGEMENT_COL_AUTHOR_NAME = 2;
    public static final int OUR_ARRANGEMENT_COL_WRITE_TIME = 3;
    public static final int OUR_ARRANGEMENT_COL_NEW_ENTRY = 4;


    // All keys from table app settings in a String
    public static final String[] OUR_ARRANGEMENT_ALL_KEYS = new String[] {KEY_ROWID, OUR_ARRANGEMENT_KEY_ARRANGEMENT, OUR_ARRANGEMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_KEY_NEW_ENTRY };

    // SQL String to create our arrangement table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_KEY_ARRANGEMENT + " TEXT not null, "
                    + OUR_ARRANGEMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_ARRANGEMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0"
                    + ");";



    /**********************************************************************************************/
    // Our Arrangement Comment- column names and numbers
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_COMMENT = "comment";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME = "author_name";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME = "comment_time";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT = "id_arrangement";
    public static final String OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY = "new_entry";

    public static final int OUR_ARRANGEMENT_COMMENT_COL_COMMENT = 1;
    public static final int OUR_ARRANGEMENT_COMMENT_COL_AUTHOR_NAME = 2;
    public static final int OUR_ARRANGEMENT_COMMENT_COL_WRITE_TIME = 3;
    public static final int OUR_ARRANGEMENT_COMMENT_COL_ID_ARRANGEMENT = 4;
    public static final int OUR_ARRANGEMENT_COMMENT_COL_NEW_ENTRY = 5;


    // All keys from table app settings in a String
    public static final String[] OUR_ARRANGEMENT_COMMENT_ALL_KEYS = new String[] {KEY_ROWID, OUR_ARRANGEMENT_COMMENT_KEY_COMMENT, OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME, OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME, OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT, OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY };

    // SQL String to create our arrangement comment table
    private static final String DATABASE_CREATE_SQL_OUR_ARRANGEMENT_COMMENT =
            "create table " + DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_COMMENT + " TEXT not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME + " STRING not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT + " INTEGER not null, "
                    + OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY + " INTEGER DEFAULT 0"
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

    public static final int CHAT_MESSAGE_COL_WRITE_TIME = 1;
    public static final int CHAT_MESSAGE_COL_AUTHOR_NAME = 2;
    public static final int CHAT_MESSAGE_COL_MESSAGE = 3;
    public static final int CHAT_MESSAGE_COL_ROLE = 4;
    public static final int CHAT_MESSAGE_COL_TX_TIME = 5;
    public static final int CHAT_MESSAGE_COL_STATUS = 6;
    public static final int CHAT_MESSAGE_COL_NEW_ENTRY = 7;

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


    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////


    DBAdapter (Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase _db) {

        // Create table OurArrangementComment
        _db.execSQL(DATABASE_CREATE_SQL_OUR_ARRANGEMENT_COMMENT);

        // Create table OurArrangement
        _db.execSQL(DATABASE_CREATE_SQL_OUR_ARRANGEMENT);

        // Create table ChatMessage
        _db.execSQL(DATABASE_CREATE_SQL_CHAT_MESSAGE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {

        Log.w(TAG, "Upgrading to " + newVersion);

        // Destroy table OurArrangementComment
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT);

        // Destroy table OurArrangement
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OUR_ARRANGEMENT);

        // Destroy table ChatMessage
        _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CHAT_MESSAGE);

        // Recreate new database:
        onCreate(_db);
    }



    /********************************* CROD for Chat Message ******************************************/

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


    /********************************* End!! CROD for Chat Message ******************************************/





    /********************************* CROD for Our Arrangement ******************************************/

    // Add a new set of values to ourArrangement .
    public long insertRowOurArrangement(String arrangement, String authorName, long arrangementTime, Boolean newEntry) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_KEY_ARRANGEMENT, arrangement);
        initialValues.put(OUR_ARRANGEMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_ARRANGEMENT_KEY_WRITE_TIME, arrangementTime);

        // is it a new entry?
        if (newEntry) {
            initialValues.put(OUR_ARRANGEMENT_KEY_NEW_ENTRY, 1);
        } else {
            initialValues.put(OUR_ARRANGEMENT_KEY_NEW_ENTRY, 0);
        }

        // Insert it into the database.
        return db.insert(DATABASE_TABLE_OUR_ARRANGEMENT, null, initialValues);
    }



    // Return all data from the database (table ourArrangement) (equal: write_time = currentDateOfArrangement, smaller: write_time < currentDateOfArrangement)
    // the result is sorted by DESC
    public Cursor getAllRowsCurrentOurArrangement(long currentDateOfArrangement, String equalGreater) {

        String where = "";

        SQLiteDatabase db = this.getWritableDatabase();


        switch (equalGreater) {

            case "equal":
                where = OUR_ARRANGEMENT_KEY_WRITE_TIME + " = " + currentDateOfArrangement;
                break;
            case "smaller":
                where = OUR_ARRANGEMENT_KEY_WRITE_TIME + " < " + currentDateOfArrangement;
                break;
            default:
                where = OUR_ARRANGEMENT_KEY_WRITE_TIME + " = " + currentDateOfArrangement;
                break;
        }

        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, OUR_ARRANGEMENT_KEY_WRITE_TIME + " DESC", null);

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



    // Get the number of new rows in arrangement (new entrys)
    public int getCountNewEntryOurArrangement() {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_ARRANGEMENT_KEY_NEW_ENTRY + "=1";
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT, OUR_ARRANGEMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

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


    /********************************* End!! CROD for Our Arrangement ******************************************/



    /********************************* CROD for Our Arrangement Comment ******************************************/

    // Add a new set of values to ourArrangementComment .
    public long insertRowOurArrangementComment(String comment, String authorName, long commentTime, int idArrangement, Boolean newEntry) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues initialValues = new ContentValues();

        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_COMMENT, comment);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME, authorName);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME, commentTime);
        initialValues.put(OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT, idArrangement);

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

        String where = OUR_ARRANGEMENT_COMMENT_KEY_ID_ARRANGEMENT + "=" + arrangementId;
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME + " DESC", null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Get the number of new rows in all comment for all arrangement (new entrys)
    public int getCountAllNewEntryOurArrangementComment() {

        SQLiteDatabase db = this.getWritableDatabase();

        // new_entry = 1 (true)?
        String where = OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY + "=1";
        Cursor c = 	db.query(true, DATABASE_TABLE_OUR_ARRANGEMENT_COMMENT, OUR_ARRANGEMENT_COMMENT_ALL_KEYS,
                where, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

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


    /********************************* End!! CROD for Our Arrangement ******************************************/





}





