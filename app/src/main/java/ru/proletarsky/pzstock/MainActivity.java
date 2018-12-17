package ru.proletarsky.pzstock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    //private TextView textView;
    private BarCodeDbHelper dbHelper;
    private EditText barCode;
    private Button askButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Timer mTimer;

    private List<DataMember> mDataList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useScanner = preferences.getBoolean("use_barcode_scanner", true);

        //textView = (TextView) findViewById(R.id.text_view);
        barCode = (EditText) findViewById(R.id.barcode);
        if (useScanner) {
            barCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 0) {
                        if (mTimer != null) {
                            mTimer.cancel();
                        }
                        mTimer = new Timer();
                        try {
                            mTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mTimer.cancel();
                                            mTimer = null;
                                            if (barCode.getText().length() > 4) {
                                                btn_requestDatabase(barCode);
                                            }
                                            barCode.getText().clear();
                                        }
                                    });
                                }
                            }, 100);
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        askButton = (Button) findViewById(R.id.btn_request);
        askButton.setVisibility(useScanner ?  View.GONE : View.VISIBLE);
        dbHelper = new BarCodeDbHelper(this);
        dbHelper.open();
        int recCount = dbHelper.getRecordsCount();
        //textView.setText(String.format("Число записей: %d", recCount));
        //setting up RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        if (mDataList == null) {
            mDataList = new ArrayList<DataMember>();
        }
        /*
        DataMember dataMember = dbHelper.requestDatabase("2000000007120");
        if (dataMember != null) {
            mDataList.add(dataMember);
        }*/
        mAdapter = new DataAdapter(mDataList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mDataList.remove(position);
                ((DataAdapter)mAdapter).setDataList(mDataList);
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.load_csv) {
            //Toast.makeText(this, "Load file pressed", Toast.LENGTH_LONG).show();
            readFile();
            return true;
        } else if (id == R.id.settings) {
            //Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ActivitySettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void readFile() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String msg;
                try {
                    InputStream inputStream = getResources().openRawResource(R.raw.sample);
                    byte[] b = new byte[inputStream.available()];
                    inputStream.read(b);
                    String[] lines = new String(b).split("\r\n");
                    long maxID = dbHelper.LoadData(lines);
                    msg = String.format("Строк в файле: %d, Записей: %d, max id %d", lines.length, dbHelper.getRecordsCount(), maxID);
                } catch (IOException e) {
                    msg = e.getMessage();
                    //textView.setText( "Ошибка загрузки");
                }
                final String toastMsg = msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public  void btn_requestDatabase(View v) {
        final String codeToFind = barCode.getText().toString();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final DataMember dataMember = dbHelper.requestDatabase(codeToFind);
                if (dataMember != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDataList.add(0, dataMember);
                            ((DataAdapter)mAdapter).setDataList(mDataList);
                            barCode.setText("");
                        }
                    });
                }
            }
        });
    }
}
