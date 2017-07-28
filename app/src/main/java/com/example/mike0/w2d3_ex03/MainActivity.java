package com.example.mike0.w2d3_ex03;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mike0.w2d3_ex03.FeedReaderContract.FeedEntry;

import static java.lang.String.format;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName() + "_TAG";

    private DBHelper helper;
    private SQLiteDatabase database;

    EditText titleET, subtitleET, updateTitleET;

    Button saveBtn, readBtn, updateBtn, deleteBtn;

    TextView resultTV, resultUpdateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new DBHelper(this);
        database = helper.getWritableDatabase();

        titleET = (EditText) findViewById(R.id.et_title);
        subtitleET = (EditText) findViewById(R.id.et_subtitle);
        updateTitleET = (EditText) findViewById(R.id.et_update_title);

        saveBtn = (Button) findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(this);
        readBtn = (Button) findViewById(R.id.btn_read);
        readBtn.setOnClickListener(this);
        updateBtn = (Button) findViewById(R.id.btn_update);
        updateBtn.setOnClickListener(this);
        deleteBtn = (Button) findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(this);

        resultTV = (TextView) findViewById(R.id.tv_result);
        resultUpdateTV = (TextView) findViewById(R.id.tv_result_update);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    private void saveRecord() {

        String title = titleET.getText().toString();
        String subtitle = subtitleET.getText().toString();


        ContentValues values = new ContentValues(); // Prevents SQL injection
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        long recordId = database.insert(
                FeedEntry.TABLE_NAME,
                null,
                values
        );

        if (recordId > 0) {
            Log.d(TAG, "saveRecord: Record saved.");
            Toast.makeText(this, "Data saved.", Toast.LENGTH_SHORT).show();
        } else  {
            Log.d(TAG, "saveRecord: Record not saved.");
            Toast.makeText(this, "Data not saved.", Toast.LENGTH_SHORT).show();
        }

        titleET.setText("");
        subtitleET.setText("");
    }

    private void readRecord() {
        resultUpdateTV.setText("");

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };
        /*
        String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";


        String[] selectionArg = {
                "Record title"
        };
        String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + "DESC";
        */

        Cursor cursor = database.query(
                FeedEntry.TABLE_NAME,        // TABLE
                projection,                  // Projection
                null,                        // Selection (WHERE)
                null,                        // Values for selection
                null,                        // Group by
                null,                        // Filters
                null                         // Sort order
        );
        while(cursor.moveToNext()) {
            StringBuilder dataResult = new StringBuilder(String.valueOf(resultTV.getText().toString()));
            long entryId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
            String entryTitle = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            String entrySubtitle = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE));
            Log.d(TAG, "readRecord: id: " +  entryId + " title: " +  entryTitle + " subtitle: " + entrySubtitle);
            resultTV.setText(dataResult.append(String.format(getString(R.string.lbl_result), entryId, entryTitle, entrySubtitle)));
        }
    }

    private void updateRecord() {
        String title = titleET.getText().toString();
        String titleUpdate = updateTitleET.getText().toString();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, titleUpdate);

        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = {
                title
        };

        int count = database.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        resultTV.setText("");
        resultUpdateTV.setText(String.format(getString(R.string.lbl_result_update), title, titleUpdate));


        if (count > 0) {
            Log.d(TAG, "updateRecord: Updated records. " +  "(" + count + ")");
            Toast.makeText(this, "updateRecord: Updated records.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "updateRecord: Records not updated.");
            Toast.makeText(this, "updateRecord: Records not updated.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRecord() {
        String titleUpdate = updateTitleET.getText().toString();

        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = {
                titleUpdate
        };
        int deleted = database.delete(
                FeedEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
        if (deleted > 0) {
            Log.d(TAG, "deleteRecord: Record deleted.");
            Toast.makeText(this, "deleteRecord: Record deleted.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "deleteRecord: Record not deleted.");
            Toast.makeText(this, "deleteRecord: Record not deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveRecord();
                break;
            case R.id.btn_read:
                readRecord();
                break;
            case R.id.btn_update:
                updateRecord();
                break;
            case R.id.btn_delete:
                deleteRecord();
                break;
        }
    }
}
