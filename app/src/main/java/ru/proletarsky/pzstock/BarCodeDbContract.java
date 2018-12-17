package ru.proletarsky.pzstock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by alexey on 30/11/2018.
 */

public final class BarCodeDbContract {
    private BarCodeDbContract() {}
    //constants
    public static class BarCodeDb implements BaseColumns {
        public static final String ITEM_TABLE = "Item";
        public static final String F_ID = "id";
        public static final String F_BARCODE = "Barcode";
        public static final String F_NOMEN = "Nomenclature";
        public static final String F_SIZE = "Size";
        public static final String F_UNIT = "Unit";
        public static final String F_STOCK = "Stock";
        public static final String F_ADDITIONAL = "Additional";
        public static final String F_NUMBER = "Number";
        public static final String F_DATE = "Date";
        public static final String F_AMOUNT = "Amount";
        public static final String F_SUM = "Sum";
        public static final String F_SUPPLIER = "Supplier";
    }
}
