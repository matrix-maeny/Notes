package com.matrix_maeny.mynotes;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NotesAdapter.RefreshTheLayout {


    //    private int STORAGE_READ_PERMISSION_CODE = 1;
    private final int STORAGE_WRITE_PERMISSION_CODE = 1;

    RecyclerView recyclerView;                          // this is for storing the id of recyclerView
    FloatingActionButton addBtn;                   // this is float button to add files
    View rootView;                                      // for the layout file
    ArrayList<NotesModel> list;                            // for the storing the list
    MyWorkDBHelper dbHelper;
    NotesAdapter adapter;

    TextView noteFragmentText;
    ConstraintLayout constraintLayout;

    Cursor cursor;
    private float dX, dY;
    int lastAction;
    //boolean moved = false;

    public MainActivity() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStorageWritePermission();
        }

        recyclerView = findViewById(R.id.recyclerView);        // this is for the recognition of the code....
        addBtn = findViewById(R.id.addBtn);
        constraintLayout = findViewById(R.id.constraintLayout);

        noteFragmentText = findViewById(R.id.emptyView);
        list = new ArrayList<>();                                 // for storing the notes...


        adapter = new NotesAdapter(list, MainActivity.this);               // creating an adapter object
        recyclerView.setAdapter(adapter);                                               // setting the adapter to our recycler view
        setLayout();
        setTheChanges();




        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(MainActivity.this, NotesEditorActivity.class));
                } else {
                    requestStorageWritePermission();
                }
            }
        });


        // adding onclick listner to the refresh layout


    }


    final void requestStorageWritePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("Storage permission is needed to WRITE notes")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_PERMISSION_CODE);
        }
    }

    // request permission result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_WRITE_PERMISSION_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission DENIED... please ENABLE manually", Toast.LENGTH_SHORT).show();

            }
        }


    }

    final void setLayout() {             // for setting the layout of the note for every note
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
    }

    final void setTheChanges() {
        dbHelper = new MyWorkDBHelper(MainActivity.this, "NotesData.db", 2);
        cursor = dbHelper.getNotesData();
        if (cursor.getCount() > 0) {
            list.clear();
            while (cursor.moveToNext()) {
                String tempHeading = cursor.getString(0);
                String tempContent = cursor.getString(1);
                list.add(new NotesModel(tempHeading, tempContent, R.drawable.ic_baseline_play_circle_outline_24));

            }

        } else {
            list.clear();
        }

        if (list.size() == 0) {
            noteFragmentText.setVisibility(View.VISIBLE);
        } else {
            noteFragmentText.setVisibility(View.GONE);
        }


        setLayout();
        dbHelper.close();


    } // for getting the notes and showing

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        setTheChanges();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    public void refreshTheLayout() {
        setTheChanges();
    }
}