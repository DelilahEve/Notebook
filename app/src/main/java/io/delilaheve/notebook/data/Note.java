package io.delilaheve.notebook.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.util.Tools;

public class Note implements Comparable, Serializable{

    private int id = -1;
    private int notebookId = -1;
    private int type;

    private String title = "";
    private String text = "";

    private ArrayList<NoteListItem> items;

    private Boolean preview;
    private Boolean temp;

    private Boolean haveChanges = false;

    private Boolean adShell = false;

    private long created;
    private long modified;

    public Note(Boolean isAdShell) {
        adShell = isAdShell;
    }

    public Note() {
        created = Calendar.getInstance().get(Calendar.MILLISECOND);
        modified = created;

        preview = true;
        temp = false;
        type = Keys.ITEM_TYPE_TEXT;
    }

    public Note(int id, int notebookId, String title, String text, Boolean preview, Boolean temp, long created, long modified) {
        this.id = id;
        this.notebookId = notebookId;
        this.title = title;
        this.text = text;
        this.preview = preview;
        this.temp = temp;
        this.created = created;
        this.modified = modified;
        type = Keys.ITEM_TYPE_TEXT;
    }

    public Note(int id, int notebookId, int type, String title, String rawItems, Boolean preview, Boolean temp, long created, long modified) {
        this.id = id;
        this.notebookId = notebookId;
        this.type = type;
        this.title = title;
        this.preview = preview;
        this.temp = temp;
        this.created = created;
        this.modified = modified;
        items = Tools.parseList(rawItems, type);
    }

    public Boolean isAdShell() {
        return adShell;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        modified = Calendar.getInstance().getTimeInMillis();
        haveChanges = true;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        modified = Calendar.getInstance().get(Calendar.MILLISECOND);
        haveChanges = true;
    }

    public NoteListItem[] getItems() {
        return items.toArray(new NoteListItem[items.size()]);
    }

    public void addItem(NoteListItem item) {
        items.add(item);
        modified = Calendar.getInstance().get(Calendar.MILLISECOND);
        haveChanges = true;
    }

    public void removeItem(NoteListItem item) {
        items.remove(item);
        modified = Calendar.getInstance().get(Calendar.MILLISECOND);
        haveChanges = true;
    }

    public int getType() {
        return type;
    }

    public void setPreview(Boolean preview) {
        this.preview = preview;
        modified = Calendar.getInstance().get(Calendar.MILLISECOND);
        haveChanges = true;
    }

    public Boolean shouldPreview() {
        return preview;
    }

    public void setTemp(Boolean temp) {
        this.temp = temp;
        modified = Calendar.getInstance().get(Calendar.MILLISECOND);
        haveChanges = true;
    }

    public Boolean isTemp() {
        return temp;
    }

    public long getCreated() {
        return created;
    }

    public long getModified() {
        return modified;
    }

    public String getTextForStorage() {
        String result = "";

        switch (type) {
            case Keys.ITEM_TYPE_TEXT:
                result = text;
                break;
            case Keys.ITEM_TYPE_BULLET:
            case Keys.ITEM_TYPE_CHECK:
                result = Tools.compressList(items, type);
                break;
        }

        return result;
    }

    @Override
    public int compareTo(Object another) {
        if(!(another instanceof Note))
            return 0;

        Note other = (Note) another;
        int result;

        switch (Settings.getSortMode()) {
            case Keys.SORT_MODE_MODIFIED_ASC:
            case Keys.SORT_MODE_MODIFIED_DESC:
                result = compareModified(other);
                break;
            case Keys.SORT_MODE_CREATED_ASC:
            case Keys.SORT_MODE_CREATED_DESC:
                result = compareCreated(other);
                break;
            case Keys.SORT_MODE_AZ:
            case Keys.SORT_MODE_ZA:
                result = compareTitle(other);
                break;
            default:
                result = compareModified(other);
                break;
        }

        return result;
    }

    private int compareModified(Note other) {
        if(modified < other.getModified())
            return 1;
        else if (modified > other.getModified())
            return -1;
        else
            return 0;
    }

    private int compareCreated(Note other) {
        if(created < other.getModified())
            return 1;
        else if (created > other.getModified())
            return -1;
        else
            return 0;
    }

    private int compareTitle(Note other) {
        return title.compareTo(other.getTitle());
    }

    public Boolean hasChanges() {
        return haveChanges;
    }

    public void save() {
        Notebook notebook;

        modified = Calendar.getInstance().getTimeInMillis();

        if(notebookId == -1) {
            notebook = Common.getCurrentNotebook();
            notebookId = notebook.getId();
        }
        else {
            notebook = Common.helper.getNotebooks(notebookId)[0];
        }

        if(id == -1)
            id = Common.helper.addNote(this, notebook);
        else
            Common.helper.updateNote(this, notebook);

        haveChanges = false;
    }
}
