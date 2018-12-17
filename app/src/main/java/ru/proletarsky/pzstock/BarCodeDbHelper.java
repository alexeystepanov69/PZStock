package ru.proletarsky.pzstock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.InvalidPropertiesFormatException;

/**
 * Created by alexey on 30/11/2018.
 */

public class BarCodeDbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ITEM =
            "CREATE TABLE " + BarCodeDbContract.BarCodeDb.ITEM_TABLE
                    + "(" + BarCodeDbContract.BarCodeDb.F_ID + " INTEGER PRIMARY KEY, "
                    + BarCodeDbContract.BarCodeDb.F_BARCODE + " TEXT NULL, "
                    + BarCodeDbContract.BarCodeDb.F_NOMEN + " TEXT NULL, "
                    + BarCodeDbContract.BarCodeDb.F_SIZE + "TEXT NULL, "
                    + BarCodeDbContract.BarCodeDb.F_UNIT + " TEXT NULL, "
                    + BarCodeDbContract.BarCodeDb.F_STOCK + " TEXT NULL, "
                    + BarCodeDbContract.BarCodeDb.F_ADDITIONAL + " TEXT NULL, "
                    + BarCodeDbContract.BarCodeDb.F_NUMBER + " TEXT NULL, "
                    + BarCodeDbContract.BarCodeDb.F_DATE + " TEXT NULL, "
                    + BarCodeDbContract.BarCodeDb.F_AMOUNT + " FLOAT NULL, "
                    + BarCodeDbContract.BarCodeDb.F_SUM + " FLOAT NULL, "
                    + BarCodeDbContract.BarCodeDb.F_SUPPLIER + " TEXT NULL);";
    private static final String SQL_DROP_STOCK =
            "drop table if exists Stock;";
    private static final String SQL_DROP_NOMEN =
            "drop tabel if exists Nomenclature;";
    private static final String SQL_DROP_ORDER =
            "drop table if exists Order;";
    private static final String SQL_DROP_ITEM =
            "drop table if exists " + BarCodeDbContract.BarCodeDb.ITEM_TABLE + ";";
    private  static final String SQL_CREATE_ALL =
            SQL_CREATE_ITEM;
    private static final String SQL_DROP_ALL =
            SQL_DROP_ITEM + SQL_DROP_ORDER + SQL_DROP_ORDER + SQL_DROP_STOCK;

    public static final int DATABASE_VERSION = 5;
    public static  final String DATABASE_NAME = "barcode";
    public static final String TAG = "Database";
    //private final Context contextLocal;
    public SQLiteDatabase database;

    public BarCodeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //contextLocal = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ALL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DROP_ALL);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void open() {
        database = this.getWritableDatabase();
    }

    public void close() {
        this.database.close();
    }

    public int getRecordsCount() {
        String cmd = "select count(*) from " + BarCodeDbContract.BarCodeDb.ITEM_TABLE;
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(cmd, null);
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count;
            } else {
                return -1;
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            cursor.close();
        }
        return -2;
    }

    public DataMember requestDatabase(String barCode) {
        if (barCode.length() <= 4) {
            return null;
        }
        String cmd = "select * from " + BarCodeDbContract.BarCodeDb.ITEM_TABLE
                + " where " + BarCodeDbContract.BarCodeDb.F_BARCODE + " like ?";
        String forSearch = "%" + barCode;
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(cmd, new String[] {forSearch});
            if (cursor.moveToFirst()) {
                DataMember dm = new DataMember();
                dm.readDatabaseRecord(cursor);
                return dm;
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            return null;
        } finally {
            cursor.close();
        }
    }

    //Загружает данные в БД
    public long LoadData(String [] data) {
        if (data.length > 1) {
            long id = 0;
            //db.beginTransaction();
            database.execSQL("delete from " + BarCodeDbContract.BarCodeDb.ITEM_TABLE);
            //Заголовок
            String [] header = data[0].toUpperCase().split(";");
            for (int j = 1; j < data.length; j++) {
                try {
                    String[] rawData = data[j].split(";");
                    //BarCodeRecord barCodeRecord = new BarCodeRecord(this);
                    //barCodeRecord.parseCsvString(header, rawData);
                    if (rawData.length != header.length)
                        continue;   //Skip row
                    ContentValues contentValues = new ContentValues();
                    for (int i = 0; i < rawData.length; i++) {
                        switch (header[i]) {
                           case "\uFEFFШТРИХКОД":
                                contentValues.put(BarCodeDbContract.BarCodeDb.F_BARCODE, rawData[i]);
                                break;
                            case "НОМЕНКЛАТУРА":
                                contentValues.put(BarCodeDbContract.BarCodeDb.F_NOMEN, rawData[i]);
                                break;
                            /*case "ГАБАРИТ":
                                contentValues.put(BarCodeDbContract.BarCodeDb.F_SIZE, rawData[i]);
                                break;*/
                            case "ЕД ИЗМ":
                                contentValues.put(BarCodeDbContract.BarCodeDb.F_UNIT, rawData[i]);
                                break;
                            case "СКЛАД":
                                contentValues.put(BarCodeDbContract.BarCodeDb.F_STOCK, rawData[i]);
                                break;
                            case "ДОКУМЕНТ ПОСТУПЛЕНИЯ":
                                contentValues.put(BarCodeDbContract.BarCodeDb.F_ADDITIONAL, rawData[i]);
                                break;
                            case "НОМЕР":
                                contentValues.put(BarCodeDbContract.BarCodeDb.F_NUMBER, rawData[i]);
                                break;
                            case "ДАТА":
                                contentValues.put(BarCodeDbContract.BarCodeDb.F_DATE, rawData[i]);
                                break;
                            case "КОЛИЧЕСТВО":
                                try {
                                    String strNumber = rawData[i].replace(" ", "")
                                            .replace(",", ".");
                                    float amount = Float.parseFloat(strNumber);
                                    contentValues.put(BarCodeDbContract.BarCodeDb.F_AMOUNT, amount);
                                } catch (Exception e) {
                                    throw new InvalidPropertiesFormatException(
                                            String.format("%s не распознается как число", rawData[i]));
                                }
                                break;
                            case "СТОИМОСТЬ":
                                try {
                                    String strNumber = rawData[i].replace(" ", "")
                                            .replace(",", ".");
                                    float sum = Float.parseFloat(strNumber);
                                    contentValues.put(BarCodeDbContract.BarCodeDb.F_SUM, rawData[i]);
                                } catch (Exception e) {
                                    throw new InvalidPropertiesFormatException(
                                            String.format("%s не распознается как число", rawData[i]));
                                }
                                break;
                            case "ПОСТАВЩИК":
                                contentValues.put(BarCodeDbContract.BarCodeDb.F_SUPPLIER, rawData[i]);
                                break;
                        }
                    }
                    id += database.insert(BarCodeDbContract.BarCodeDb.ITEM_TABLE,
                            null, contentValues);
                } catch (Exception e) {
                    Log.d("TAG", String.format("Error: %s at string %d", e.getLocalizedMessage(), j));
                }
            }
            return id;
        } else {
            return -1;
        }
    }
}
