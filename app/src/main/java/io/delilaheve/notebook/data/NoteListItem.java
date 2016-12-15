package io.delilaheve.notebook.data;

import java.io.Serializable;

public class NoteListItem implements Serializable {

    private String text;
    private Boolean isChecked;

    public NoteListItem(String text, Boolean isChecked) {
        this.text = text;
        this.isChecked = isChecked;
    }

    public String getText() {
        return text;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    @Override
    public boolean equals(Object item) {
        boolean equal = false;

        if(item instanceof NoteListItem) {
            NoteListItem i = (NoteListItem) item;
            equal = text == i.text && isChecked == i.isChecked;
        }

        return equal;
    }
}
