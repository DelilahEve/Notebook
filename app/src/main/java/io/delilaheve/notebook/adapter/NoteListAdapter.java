package io.delilaheve.notebook.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.data.Note;
import io.delilaheve.notebook.data.Notebook;
import io.delilaheve.notebook.util.IUpdatable;

public class NoteListAdapter extends Adapter {

    private String filter = "";

    public NoteListAdapter(Context context, Notebook notebook) {
        super(context, notebook);

        Settings.addListener(this);
    }

    @Override
    public Note[] fetchItems() {
        Note[] notes;

        if(Common.searchActive && Settings.getSearchMode() == Keys.SEARCH_MODE_ALL)
            notes = Common.helper.getNotes(null, filter);
        else
            notes = Common.helper.getNotes(getNotebook(), filter);

        int mode = Settings.getSortMode();
        Arrays.sort(notes);

        if(mode == Keys.SORT_MODE_MODIFIED_DESC || mode == Keys.SORT_MODE_CREATED_DESC || mode == Keys.SORT_MODE_ZA) {
            Note[] desc = new Note[notes.length];
            for(int i = 0, j = notes.length-1; i < notes.length; i++, j--)
                desc[i] = notes[j];

            notes = desc;
        }

        if (Common.freeMode) {
            return injectAds(notes);
        }
        else {
            return notes;
        }
    }

    private Note[] injectAds(Note[] notes) {
        int adCount = notes.length / 4;

        if (adCount < 1)
            adCount = 1;

        Note[] injected = new Note[notes.length + adCount];

        int sourcePos = 0;
        int i = 0;
        while (i < injected.length) {

            if (adCount > 0 && (sourcePos == notes.length || (sourcePos != 0 && sourcePos % 4 == 0))) {
                injected[i] = new Note(true);
                adCount--;
            }
            else {
                injected[i] = notes[sourcePos];
                sourcePos++;
            }

            i++;
        }

        return injected;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = getInflater().inflate(R.layout.card_note, parent, false);

        Note note;
        TextView title, preview, date;

        note = (Note) getItem(position);
        title = (TextView) view.findViewById(R.id.title_text);
        preview = (TextView) view.findViewById(R.id.preview_text);
        date = (TextView) view.findViewById(R.id.modify_date);



        if(note != null) {
            if(note.isAdShell()) {
                view = getInflater().inflate(R.layout.card_ad, parent, false);
                NativeExpressAdView adView = (NativeExpressAdView) view.findViewById(R.id.ad_view);
                AdRequest request = new AdRequest.Builder()
                        //.addTestDevice(testId)
                        .build();
                if (Common.freeMode && !Settings.getPurchasedUpgrade()) {
                    adView.loadAd(request);
                }
                else {
                    adView.setVisibility(View.GONE);
                }
            }
            else {
                title.setText(note.getTitle());

                if (note.shouldPreview()) {
                    String previewText = "";

                    switch (note.getType()) {
                        case Keys.ITEM_TYPE_TEXT:
                            previewText = note.getText();
                            break;
                        case Keys.ITEM_TYPE_BULLET:
                        case Keys.ITEM_TYPE_CHECK:
                            for(int i = 0; i < 3 && i < note.getItems().length; i++)
                                previewText += note.getItems()[i];
                            break;
                    }

                    preview.setText(previewText);
                }

                String formattedDate;
                Date time = new Date(note.getModified());
                SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);
                formattedDate = format.format(time);

                date.setText(formattedDate);
            }
        }

        return view;
    }

    public void applyFilter(String filter) {
        this.filter = filter;
        onUpdateOccurred();
    }
}
