package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteDatabaseHelper.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteDatabaseHelper.BALANCE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteDatabaseHelper.BANK_NAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteDatabaseHelper.HOLDER_NAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteDatabaseHelper.TABLE_ACCOUNT;

public class PersistentAccountDAO implements AccountDAO {
    private final SQLiteDatabaseHelper helper;
    private SQLiteDatabase db;

    public PersistentAccountDAO(Context context) {
        helper = new SQLiteDatabaseHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        db = helper.getReadableDatabase();
        String[] projection = {ACCOUNT_NO};

        Cursor cursor = db.query(TABLE_ACCOUNT, projection, null, null, null, null, null);
        List<String> accountNumbers = new ArrayList<String>();

        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow(ACCOUNT_NO));
            accountNumbers.add(accountNumber);
        }
        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();

        db = helper.getReadableDatabase();

        String[] projection = {ACCOUNT_NO, BANK_NAME, HOLDER_NAME, BALANCE};

        Cursor cursor = db.query(TABLE_ACCOUNT, projection, null, null, null, null, null);

        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String bankName = cursor.getString(cursor.getColumnIndex(BANK_NAME));
            String accountHolderName = cursor.getString(cursor.getColumnIndex(HOLDER_NAME));
            double balance = cursor.getDouble(cursor.getColumnIndex(BALANCE));
            Account account = new Account(accountNumber,bankName,accountHolderName,balance);
            accounts.add(account);
        }
        cursor.close();
        return accounts;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        db = helper.getReadableDatabase();
        String[] projection = {ACCOUNT_NO, BANK_NAME, HOLDER_NAME, BALANCE };
        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor c = db.query(TABLE_ACCOUNT, projection, selection, selectionArgs, null, null, null);

        if (c == null){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else {
            c.moveToFirst();

            Account account = new Account(accountNo, c.getString(c.getColumnIndex(BANK_NAME)),
                    c.getString(c.getColumnIndex(HOLDER_NAME)), c.getDouble(c.getColumnIndex(BALANCE)));
            return account;
        }
    }

    @Override
    public void addAccount(Account account) {

        db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ACCOUNT_NO, account.getAccountNo());
        cv.put(BANK_NAME, account.getBankName());
        cv.put(HOLDER_NAME, account.getAccountHolderName());
        cv.put(BALANCE,account.getBalance());

        // Updating row values
        db.insert(TABLE_ACCOUNT, null, cv);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        db = helper.getWritableDatabase();
        db.delete(TABLE_ACCOUNT, ACCOUNT_NO + " = ?",
                new String[] { accountNo });
        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        db = helper.getWritableDatabase();
        String[] projection = { BALANCE };
        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cursor = db.query(TABLE_ACCOUNT, projection, selection, selectionArgs, null, null, null);

        double balance;
        if(cursor.moveToFirst())
            balance = cursor.getDouble(0);
        else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        ContentValues cv = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                cv.put(BALANCE, balance - amount);
                break;
            case INCOME:
                cv.put(BALANCE, balance + amount);
                break;
        }

        // Updating row values
        db.update(TABLE_ACCOUNT, cv, ACCOUNT_NO + " = ?",
                new String[] { accountNo });

        cursor.close();
        db.close();
    }
}
