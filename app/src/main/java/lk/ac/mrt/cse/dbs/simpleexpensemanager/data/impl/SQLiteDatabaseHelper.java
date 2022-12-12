package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "200530D";

    public static final String TABLE_ACCOUNT = "accountsDetails";
    public static final String TABLE_TRANSACTION = "transactions";

    //Column names of the tables
    public static final String ACCOUNT_NO = "accountNo";

    //TABLE_ACCOUNT
    public static final String BANK_NAME = "bankName";
    public static final String HOLDER_NAME = "accountHolderName";
    public static final String BALANCE = "balance";

    //TABLE_TRANSACTION
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String AMOUNT = "amount";
    public static final String DATE = "date";

    public SQLiteDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_ACCOUNT + "(" +
                ACCOUNT_NO + " TEXT PRIMARY KEY, " +
                BANK_NAME + " TEXT NOT NULL, " +
                HOLDER_NAME + " TEXT NOT NULL, " +
                BALANCE + " REAL NOT NULL)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_TRANSACTION + "(" +
                DATE + " TEXT NOT NULL, " +
                EXPENSE_TYPE + " TEXT NOT NULL, " +
                AMOUNT + " REAL NOT NULL, " +
                ACCOUNT_NO + " TEXT," +
                "FOREIGN KEY (" + ACCOUNT_NO + ") REFERENCES " + TABLE_ACCOUNT + "(" + ACCOUNT_NO + "))");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);

        // Create tables again
        onCreate(sqLiteDatabase);
    }
}

