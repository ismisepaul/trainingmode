package ismisepaul.trainingmode;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrainingModeDb {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "trainingMode";
    private static final String TRAINING_MODE_TABLE_NAME = "app_preference";
    private static final String KEY_ID = "package_name";
    private static final String KEY_NAME = "app_name";

    private SqLiteDbHelper mHelper;
    private final Context mContext;
    private SQLiteDatabase mDatabase;

    private static class SqLiteDbHelper extends SQLiteOpenHelper {

        //
        SqLiteDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //set up the database with this create command and use in onCreate
        private static final String TRAINING_MODE_TABLE_CREATE =
                "CREATE TABLE" + TRAINING_MODE_TABLE_NAME + " (" +
                        "PACKAGE_NAME" + " TEXT PRIMARY KEY, " +
                        "APP_NAME" + " TEXT);";

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TRAINING_MODE_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            //if exists delete...

        }
    }

    public TrainingModeDb(Context context){
        mContext = context;
    }

    public TrainingModeDb open(){
        mHelper = new SqLiteDbHelper(mContext);
        mDatabase = mHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        mHelper.close();
    }

    public void create(String packageName, String appName){
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, packageName);
        cv.put(KEY_NAME, appName);

        mDatabase.insert(TRAINING_MODE_TABLE_NAME, null, cv);
    }
}
