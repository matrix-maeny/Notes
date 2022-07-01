package com.matrix_maeny.mynotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.viewHolder> {

    ArrayList<NotesModel> list;
    Context context;
    boolean clicked = true;
    ProcessHelper processHelper;
    TextToSpeech textToSpeech = null;
    Set<String> a = new HashSet<String>();          // to store voice

    viewHolder buttonHolder = null;


    RefreshTheLayout refresh;
    boolean initializeStatus = false;


    public NotesAdapter(ArrayList<NotesModel> list, Context context) {
        this.list = list;
        this.context = context;
        refresh = (RefreshTheLayout) context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notes_view_model, parent, false);
        processHelper = new ProcessHelper(context.getApplicationContext());

        initializeSpeechEngine();

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {


        NotesModel model = list.get(position);
        holder.noteHeading.setText(model.getNoteHeading());
        holder.noteContent.setText(model.getNoteContent());
        holder.playPause.setImageResource(model.getPlayPause());

        clicked = true;

        buttonHolder = holder;

       // startSpeaking("\t\t\t\t",true,holder);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intnt = new Intent(v.getContext(), NotesEditorActivity.class);
                intnt.putExtra("heading", holder.noteHeading.getText().toString());
                intnt.putExtra("content", holder.noteContent.getText().toString());
                intnt.putExtra("check", true);
                intnt.putExtra("position", holder.getAdapterPosition());


                v.getContext().startActivity(intnt);

            }
        });

        holder.playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context.getApplicationContext(), "play click", Toast.LENGTH_SHORT).show();

                if (clicked) {
                    holder.playPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24); // changing the icon

                    String h = holder.noteHeading.getText().toString();
                    String c = holder.noteContent.getText().toString();

                    String words = "Note heading, " + h + ".\n" + c + ".";
                    clicked = false;
                    Toast.makeText(context.getApplicationContext(), "Speaking started...", Toast.LENGTH_SHORT).show();
                    startSpeaking(words, true,holder);


                } else {
                    stopSpeaking();
                    holder.playPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24); // changing the icon
                    clicked = true;
                }

            }
        });


        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.cardView);
                popupMenu.getMenuInflater().inflate(R.menu.notes_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.shareNote:
                                Toast.makeText(context.getApplicationContext(), "sharing", Toast.LENGTH_LONG).show();
                                startShare(holder.noteHeading.getText().toString(), holder.noteContent.getText().toString());
                                break;
                            case R.id.deleteNote:
                                startDeleteNote(holder.noteHeading.getText().toString());
                                refresh.refreshTheLayout();
//                                Toast.makeText(context.getApplicationContext(), "Note deleted", Toast.LENGTH_LONG).show();

                                break;
                            case R.id.deleteAllNote:
                                deleteAllNotes();
                                refresh.refreshTheLayout();


//                                Toast.makeText(context.getApplicationContext(), "refresh to load", Toast.LENGTH_LONG).show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });


    }

    final void startShare(String heading, String content) { // for sharing the data


        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");

        String shareText = "**** "+heading+ " ****\n\n" + content + "\n";


        intent.putExtra(Intent.EXTRA_TEXT, shareText);

        context.startActivity(intent);
    }

    final void startDeleteNote(String heading) {
        MyWorkDBHelper dbHelper = new MyWorkDBHelper(context.getApplicationContext(), "NotesData.db", 2);
        dbHelper.deleteNotesData(heading);

        dbHelper.close();
    }

    final void startSpeaking(String words, boolean speak,viewHolder holder) {


        if (!speak) {
           textToSpeech.stop();
            return;
        }
        MyWorkDBHelper dbHelper = new MyWorkDBHelper(context.getApplicationContext(), "SpeechSettings.db", 1);


        String speechVoice = "en-GB-language";
        String speechCode = "en";
        String speechCountry = "GB";
        int pitchOfProgressBar = 1;
        int speedOfProgressBar = 1;

        float pitch = 1;
        float speed = 0;


        Cursor cursor = dbHelper.getData();

        if (cursor.getCount() == 0) {
            Toast.makeText(context.getApplicationContext(), "Check speech settings before you begin", Toast.LENGTH_LONG).show();

            context.startActivity(new Intent(context.getApplicationContext(), SettingsActivity.class));
        } else {

            if (cursor.moveToNext()) {

                speedOfProgressBar = cursor.getInt(1);
                speed = (float) speedOfProgressBar / 50;

            }

            if (cursor.moveToNext()) {
                pitchOfProgressBar = cursor.getInt(1);
                pitch = (float) pitchOfProgressBar / 50;
            }

            if (cursor.moveToNext()) {
                speechVoice = cursor.getString(2);
                speechCode = cursor.getString(3);
                speechCountry = cursor.getString(4);
            }

            speakTheWords(speechVoice, speechCode, speechCountry, words, pitch, speed,holder);


            //Toast.makeText(context, "speaking started......", Toast.LENGTH_LONG).show();


        }

        dbHelper.close();

        // getting the values from the data base
    }

    public void speakTheWords(String speechVoice, String speechCode, String speechCountry, String words, float pitch, float speed,viewHolder holder) {

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {


                    int checkLanguageStatus = textToSpeech.setLanguage(Locale.US);

                    Voice voice = new Voice(speechVoice, new Locale(speechCode, speechCountry), 400, 200, false, a);
                    if (textToSpeech.setVoice(voice) == TextToSpeech.SUCCESS) {

                    }

                } else {
                    Toast.makeText(context.getApplicationContext(), "Text To Speed Engine Initialization Failed: processHelper", Toast.LENGTH_SHORT).show();
                    return;
                }

                textToSpeech.setPitch(pitch);
                textToSpeech.setSpeechRate(speed);

//                Toast.makeText(context, "wait few seconds to initialize speech engine", Toast.LENGTH_LONG).show();

                textToSpeech.stop();
                textToSpeech.speak(words, TextToSpeech.QUEUE_FLUSH, null, "matrix");

            }
        });



//        Voice voice = new Voice(speechVoice, new Locale(speechCode, speechCountry), 400, 200, false, a);
//
//        if (textToSpeech.setVoice(voice) != TextToSpeech.SUCCESS) {
//            Toast.makeText(context.getApplicationContext(), "Language not supported", Toast.LENGTH_SHORT).show();
//        }
//
//        textToSpeech.setPitch(pitch);
//        textToSpeech.setSpeechRate(speed);
//
////                Toast.makeText(context, "wait few seconds to initialize speech engine", Toast.LENGTH_LONG).show();
//
//        textToSpeech.speak(words, TextToSpeech.QUEUE_FLUSH, null, "matrix");


        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!textToSpeech.isSpeaking()){
                    textToSpeech.stop();
                    textToSpeech.speak(words, TextToSpeech.QUEUE_FLUSH, null, "matrix");
                    //Toast.makeText(context.getApplicationContext(), "Wait few seconds...", Toast.LENGTH_SHORT).show();
                    handler1.postDelayed(this,5);

                }else{
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!textToSpeech.isSpeaking()){
                                //Toast.makeText(context.getApplicationContext(), "speech stopped", Toast.LENGTH_SHORT).show();
                                holder.playPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                                clicked = true;
                            }else {
                                handler.postDelayed(this,5);
                            }
                        }
                    },50);
                }
            }
        },1);


//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(!textToSpeech.isSpeaking()){
//                    //Toast.makeText(context.getApplicationContext(), "speech stopped", Toast.LENGTH_SHORT).show();
//                    holder.playPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
//                    clicked = true;
//                }else {
//                    handler.postDelayed(this,5);
//                }
//            }
//        },50);






    }

    private void initializeSpeechEngine() {

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    initializeStatus = true;

                    Toast.makeText(context.getApplicationContext(), "Wait few seconds to initialize speech engine", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context.getApplicationContext(), "Text To Speed Engine Initialization Failed: processHelper", Toast.LENGTH_SHORT).show();

                    initializeStatus = false;
                }
            }
        });


    }

//    private void changeButtonState(boolean status) {
//
//        if (!status) {
//        }
//    }

    final void stopSpeaking() {

        textToSpeech.stop();


    }


    final void deleteAllNotes() {
        MyWorkDBHelper dbHelper = new MyWorkDBHelper(context.getApplicationContext(), "NotesData.db", 2);

        dbHelper.deleteAllNotes();
        dbHelper.close();
    }

    public interface RefreshTheLayout {
        void refreshTheLayout();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {

        TextView noteHeading, noteContent;
        ImageView playPause;
        CardView cardView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            noteContent = itemView.findViewById(R.id.noteContent);
            noteHeading = itemView.findViewById(R.id.alarmTime);
            playPause = itemView.findViewById(R.id.playPause);
            cardView = itemView.findViewById(R.id.remCardView);


        }


    }


}
