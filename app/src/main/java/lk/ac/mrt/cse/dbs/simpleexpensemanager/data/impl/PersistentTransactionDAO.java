package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;

        import java.text.DateFormat;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;

        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

        import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteDatabaseHelper.ACCOUNT_NO;
        import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteDatabaseHelper.AMOUNT;
        import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteDatabaseHelper.DATE;
        import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteDatabaseHelper.EXPENSE_TYPE;
        import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteDatabaseHelper.TABLE_TRANSACTION;

public class PersistentTransactionDAO implements TransactionDAO {
    private final SQLiteDatabaseHelper helper;
    private SQLiteDatabase db;

    public PersistentTransactionDAO(Context context) {
        helper = new SQLiteDatabaseHelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        db = helper.getWritableDatabase();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues cv = new ContentValues();
        cv.put(DATE, df.format(date));
        cv.put(ACCOUNT_NO, accountNo);
        cv.put(EXPENSE_TYPE, String.valueOf(expenseType));
        cv.put(AMOUNT, amount);

        // Updating row values
        db.insert(TABLE_TRANSACTION, null, cv);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactions = new ArrayList<Transaction>();

        db = helper.getReadableDatabase();
        String[] projection = { DATE, ACCOUNT_NO, EXPENSE_TYPE, AMOUNT };

        Cursor cursor = db.query(TABLE_TRANSACTION, projection, null, null, null, null, null);

        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(DATE));
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            String accountNumber = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String type = cursor.getString(cursor.getColumnIndex(EXPENSE_TYPE));
            ExpenseType expenseType = ExpenseType.valueOf(type);
            double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));
            Transaction transaction = new Transaction(date1,accountNumber,expenseType,amount);
            transactions.add(transaction);
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {

        List<Transaction> transactions = new ArrayList<Transaction>();

        db = helper.getReadableDatabase();

        String[] projection = { DATE, ACCOUNT_NO, EXPENSE_TYPE, AMOUNT };
        Cursor cursor = db.query(TABLE_TRANSACTION, projection, null, null, null, null,null);

        int rowsNum = cursor.getCount();

        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(DATE));
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            String accountNumber = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String type = cursor.getString(cursor.getColumnIndex(EXPENSE_TYPE));
            ExpenseType expenseType = ExpenseType.valueOf(type);
            double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));
            Transaction transaction = new Transaction(date1,accountNumber,expenseType,amount);
            transactions.add(transaction);
        }

        if (rowsNum <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(rowsNum - limit, rowsNum);
    }
}