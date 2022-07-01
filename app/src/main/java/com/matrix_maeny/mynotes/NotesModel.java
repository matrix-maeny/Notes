package com.matrix_maeny.mynotes;

public class NotesModel {

// ========================= Notes variables ==============================

    String noteHeading;
    String noteContent;
    int playPause;

    public NotesModel(String noteHeading, String noteContent, int playPause) {
        this.noteHeading = noteHeading;
        this.noteContent = noteContent;
        this.playPause = playPause;
    }

    public String getNoteHeading() {
        return noteHeading;
    }

    public void setNoteHeading(String noteHeading) {
        this.noteHeading = noteHeading;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public int getPlayPause() {
        return playPause;
    }

    public void setPlayPause(int playPause) {
        this.playPause = playPause;
    }

    // complete
}
