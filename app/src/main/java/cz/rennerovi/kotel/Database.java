package cz.rennerovi.kotel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 9.12.2015.
 */
public class Database {

    protected static final String DATABASE_NAME = "kotel";
    protected static final int DATABASE_VERSION = 3;

    protected static final String TB_NAME = "data";
    protected static final String COLUMN_ID = "_id";
    protected static final String COLUMN_ID2 = "id";
    protected static final String COLUMN_NAME = "name";
    //protected static final String COLUMN_SHOW = "show";

    public static final String[] columns = {COLUMN_ID, COLUMN_ID2, COLUMN_NAME};//, COLUMN_SHOW};

    private SQLiteOpenHelper openHelper;

    public Database(Context ctx) {
        openHelper = new DatabaseHelper(ctx);
    }

    static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper (Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TB_NAME + " (" + COLUMN_ID
                    + " INTEGER PRIMARY KEY," + COLUMN_ID2
                    + " TEXT NOT NULL," + COLUMN_NAME + " TEXT NOT NULL"//+ COLUMN_SHOW + " TEXT NOT NULL"
                     + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
            onCreate(db);
            //Toast.makeText(.this, "Databse dropped due to database upgrade, sorry..."),
        }
    }

    public long addRecord (int id2, String name)//, String show)
    {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID2, id2);
        cv.put(COLUMN_NAME, name);
        //cv.put(COLUMN_SHOW, show);

        long id = db.insert(TB_NAME, null, cv);
        db.close();
        return id;
    }

    public Cursor getRecords(String orderBy, boolean desc) {
        SQLiteDatabase db = openHelper.getReadableDatabase();

            return db.query(TB_NAME, columns, null, null, null, null, orderBy + (desc ? "DESC" : "ASC"));
    }

    public void close() {
        openHelper.close();
    }
}
