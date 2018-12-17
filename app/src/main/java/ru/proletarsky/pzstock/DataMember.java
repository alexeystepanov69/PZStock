package ru.proletarsky.pzstock;

import android.database.Cursor;

import java.text.DecimalFormat;

/**
 * Created by alexey on 08/12/2018.
 */

//Setup data member
public class DataMember {
    public String nomenclature;
    public String stock;
    public String supplier;
    public String orderInfo;
    public String unit;
    public float sum;
    public float amount;

    public DataMember() {}
    public DataMember(String nom, String stck, String supp, String order, String unt,
                      float fs, float fam) {
        nomenclature = nom;
        stock = stck;
        supplier = supp;
        orderInfo = order;
        unit = unt;
        sum = fs;
        amount = fam;
    }
    public void readDatabaseRecord(Cursor cursor) {
        try {
            nomenclature = cursor.getString(cursor.getColumnIndex(BarCodeDbContract.BarCodeDb.F_NOMEN));
            stock = cursor.getString(cursor.getColumnIndex(BarCodeDbContract.BarCodeDb.F_STOCK));
            supplier = cursor.getString(cursor.getColumnIndex(BarCodeDbContract.BarCodeDb.F_SUPPLIER));
            orderInfo = cursor.getString(cursor.getColumnIndex(BarCodeDbContract.BarCodeDb.F_ADDITIONAL));
            unit = cursor.getString(cursor.getColumnIndex(BarCodeDbContract.BarCodeDb.F_UNIT));
            sum = cursor.getFloat(cursor.getColumnIndex(BarCodeDbContract.BarCodeDb.F_SUM));
            amount = cursor.getFloat(cursor.getColumnIndex(BarCodeDbContract.BarCodeDb.F_AMOUNT));
        } catch (Exception e) {
            nomenclature = e.getMessage();
        }
    }

    public String getAmount() {
        DecimalFormat df = new DecimalFormat();
        return String.format("Кол-во: %s %s", df.format(amount), unit);
    }
    public String getSum() {
        DecimalFormat df = new DecimalFormat();
        return String.format("Сумма: %s руб", df.format(sum));
    }
}

