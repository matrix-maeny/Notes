package com.matrix_maeny.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.matrix_maeny.mynotes.R;
import com.matrix_maeny.mynotes.NotesAdapter;
import com.matrix_maeny.mynotes.NotesModel;

import java.util.ArrayList;
import java.util.Objects;

public class NotesEditorActivity extends AppCompatActivity {

    EditText enterHeading;
    EditText enterContent;
    TextView setNoteHeading;

    MyWorkDBHelper dbHelper;
    boolean backCheck = false;
    boolean isUpdating = false;
    String heading;
    String content;
    int position = 0;


    View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_editor);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        enterHeading = findViewById(R.id.enterHeading);
        enterContent = findViewById(R.id.enterContent);
        setNoteHeading = findViewById(R.id.setNoteHeading);
        view = findViewById(R.id.myView);

        Intent intent = getIntent();
        heading = intent.getStringExtra("heading");
        content = intent.getStringExtra("content");
        position = intent.getIntExtra("position", 0);
        isUpdating = intent.getBooleanExtra("check", false);


        if (isUpdating) {
            String note = "Note: "+heading;
            setNoteHeading.setText(note);
            setNoteHeading.setVisibility(View.VISIBLE);
            enterHeading.setVisibility(View.GONE);

        }else{
            setNoteHeading.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }


        enterHeading.setText(heading);
        enterContent.setText(content);


    }

    @Override
    public void onBackPressed() {
        dbHelper = new MyWorkDBHelper(NotesEditorActivity.this, "NotesData.db", 2);

        heading = enterHeading.getText().toString(); //getting the heading
        content = enterContent.getText().toString(); // getting the content
        content = content.trim();
        heading = heading.trim();

        if(heading.length() >46){
            Toast.makeText(NotesEditorActivity.this,"Heading must be less than 45 characters", Toast.LENGTH_LONG).show();
           return;
        }
        if(content.equals("")) content = "Blank";

        Cursor cursor = dbHelper.getNotesData(); // getting the cursor for accessing


        if (!heading.equals("")) {
            if (!isUpdating && dbHelper.insertNoteData(heading, content)) {

                tempToast("file saved successfully");
                dbHelper.close();
                super.onBackPressed();
            } else {
                for (int i = 0; i <= position; i++) cursor.moveToNext();
                Log.i("position", String.valueOf(position));

                if (isUpdating && dbHelper.updateNoteDate(cursor.getString(0), content)) {
                    tempToast("file updated successfully");
                    dbHelper.close();
                    super.onBackPressed();
                } else {
                    tempToast("The heading is already taken");
                }
            }
        } else {
            if (backCheck) {
                dbHelper.close();
                super.onBackPressed();
            } else {
                Toast.makeText(NotesEditorActivity.this, "Please enter heading or press again to exit", Toast.LENGTH_LONG).show();;
                backCheck = true;
            }
        }


    }

    final void tempToast(String m) {
        Toast.makeText(NotesEditorActivity.this, m, Toast.LENGTH_SHORT).show();

    }


}