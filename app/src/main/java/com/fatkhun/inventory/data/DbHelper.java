package com.fatkhun.inventory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fatkhun.inventory.data.model.Product;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.fatkhun.inventory.data.ProductContract.ProductEntry.COLUMN_CODE;
import static com.fatkhun.inventory.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.fatkhun.inventory.data.ProductContract.ProductEntry.COLUMN_PHOTO_PATH;
import static com.fatkhun.inventory.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.fatkhun.inventory.data.ProductContract.ProductEntry.SQL_CREATE_ENTRY;
import static com.fatkhun.inventory.data.ProductContract.ProductEntry.SQL_DELETE_ENTRY;
import static com.fatkhun.inventory.data.ProductContract.ProductEntry.TABLE_NAME;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "InventoryApp.db";
    private static final int DATABASE_VERSION = 1;

    private static DbHelper sDbHelperInstance;

    public static synchronized DbHelper getInstance(Context context) {
        if (sDbHelperInstance == null) {
            sDbHelperInstance = new DbHelper(context);
        }
        return sDbHelperInstance;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRY);
        onCreate(sqLiteDatabase);
    }

    public void insertProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = generateContentValues(product);
            long insertId = db.insertOrThrow(TABLE_NAME, null, cv);
            Log.i(TAG, "Inserted new Product with ID: " + insertId);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
    }

    public List<Product> queryProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {_ID, COLUMN_NAME, COLUMN_CODE, COLUMN_PHOTO_PATH, COLUMN_QUANTITY};
        String orderBy = COLUMN_NAME + " ASC";
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, orderBy);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Product product = createProductFromCursor(cursor);
                    products.add(product);
                } while (cursor.moveToNext());
                Log.i(TAG, "Queried Products with size: " + products.size());
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return products;
    }

    public Product queryProductDetails(Product product) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {COLUMN_CODE};
        String selection = _ID + " = ?";
        String[] selectionArgs = {String.valueOf(product.getId())};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                product.setCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)));
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return product;
    }

    public void updateProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = generateContentValues(product);
            String whereClause = _ID + " = ?";
            String[] whereArgs = {String.valueOf(product.getId())};
            int noOfRowsAffected = db.update(TABLE_NAME, cv, whereClause, whereArgs);
            Log.i(TAG, "Number of rows affected: " + noOfRowsAffected);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateProductQuantity(long id, int newQuantity) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_QUANTITY, newQuantity);
            String whereClause = _ID + " = ?";
            String[] whereArgs = {String.valueOf(id)};
            int noOfRowsAffected = db.update(TABLE_NAME, cv, whereClause, whereArgs);
            Log.i(TAG, "Number of rows affected: " + noOfRowsAffected);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteProduct(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            String whereClause = _ID + " = ?";
            String[] whereArgs = {String.valueOf(id)};
            db.delete(TABLE_NAME, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAllProducts() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @SuppressWarnings("unused")
    public void deleteDatabase(Context context) {
        context.deleteDatabase(getDatabaseName());
    }

    private ContentValues generateContentValues(Product product) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, product.getName());
        cv.put(COLUMN_CODE, product.getCode());
        cv.put(COLUMN_PHOTO_PATH, product.getPhotoPath());
        cv.put(COLUMN_QUANTITY, product.getQuantity());
        return cv;
    }

    private Product createProductFromCursor(Cursor cursor) throws IllegalArgumentException {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        String skuCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE));
        String photoPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_PATH));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
        return new Product(id, name, skuCode, photoPath, quantity);
    }
}
