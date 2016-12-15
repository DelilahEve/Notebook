package io.delilaheve.notebook.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.res.ResourcesCompat;

import com.android.vending.billing.IInAppBillingService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.data.ColourPack;
import io.delilaheve.notebook.data.Note;
import io.delilaheve.notebook.data.NoteListItem;

public class Tools {

    private static IInAppBillingService service;
    private static ServiceConnection connection;

    public static void clearTemp() {
        Note[] all = Common.helper.getNotes(null);

        long timestamp = Calendar.getInstance().getTimeInMillis();
        long timer = Settings.getTempNoteTimer();

        for (Note note : all) {
            if(note.isTemp() && (note.getCreated() + timer > timestamp))
                Common.helper.deleteNote(note);
        }
    }

    public static ArrayList<NoteListItem> parseList(String rawItems, int type) {
        String[] split = rawItems.split(Keys.KEY_LIST_ITEM_SPLIT);

        ArrayList<NoteListItem> items = new ArrayList<>();

        for(String item : split) {
            if(item.trim().equals(""))
                continue;

            String text;
            Boolean isChecked;

            if(type == Keys.ITEM_TYPE_CHECK) {
                String[] parts = item.split(Keys.KEY_SUB_ITEM_SPLIT);
                text = parts[0];
                isChecked = parts[1].equals(Keys.KEY_BOOL_TRUE);
            }
            else {
                text = item;
                isChecked = false;
            }

            items.add(new NoteListItem(text, isChecked));
        }

        return items;
    }

    public static String compressList(ArrayList<NoteListItem> items, int type)
    {
        String compressed = "";

        for(NoteListItem item : items)
        {
            compressed += item.getText();

            if(type == Keys.ITEM_TYPE_CHECK) {
                compressed += Keys.KEY_SUB_ITEM_SPLIT;
                compressed += item.getIsChecked() ? Keys.KEY_BOOL_TRUE : Keys.KEY_BOOL_FALSE;
            }

            compressed += Keys.KEY_LIST_ITEM_SPLIT;
        }

        return compressed;
    }

    public static Boolean isValidHex(String hex) {
        Pattern p = Pattern.compile("^#(([0-9|A-F]){6}|([0-9|A-F]){8})$");
        Matcher m = p.matcher(hex);

        return m.matches();
    }

    public static Drawable getSamplePreview(int colour, Context context) {
        if(context == null)
            return null;

        Drawable d;

        if(Build.VERSION.SDK_INT >= 21)
            d = context.getDrawable(R.drawable.colour_pallete_sample);
        else
            d = ResourcesCompat.getDrawable(context.getResources(), R.drawable.colour_pallete_sample, null);

        d.setColorFilter(colour, PorterDuff.Mode.MULTIPLY);

        return d;
    }

    public static Bitmap colouriseNotebook(int colour, Context context) {
        Bitmap notebookIcon;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        notebookIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notebook, options);

        int[] pixels = new int[notebookIcon.getWidth() * notebookIcon.getHeight()];
        notebookIcon.getPixels(pixels, 0, notebookIcon.getWidth(), 0, 0, notebookIcon.getWidth(), notebookIcon.getHeight());

        int white = Color.WHITE;

        int[] newPixels = new int[pixels.length];

        for(int i = 0; i < pixels.length; i++)
                if(pixels[i] == white)
                    newPixels[i] = colour;
                else
                    newPixels[i] = pixels[i];

        notebookIcon.setPixels(newPixels, 0, notebookIcon.getWidth(), 0, 0, notebookIcon.getWidth(), notebookIcon.getHeight());

        return notebookIcon;
    }

    public static void calculateContrastMode() {
        ColourPack pack = new ColourPack(Settings.getPrimary());
        double y = (299 * pack.getR() + 587 * pack.getG() + 114 * pack.getB()) / 1000;
        Common.contrastMode = y >= 128 ? Keys.CONTRAST_DARK : Keys.CONTRAST_LIGHT;
    }

    public static long getMilliseconds(int amount, Common.TimeUnit unit) {
        return  (amount * unit.getMilliseconds());
    }

    public static String getNormalizedTime(long milliseconds) {
        String unit, time;
        int amount;

        time = "%d %s";

        Common.TimeUnit u = getUnit(milliseconds);
        if(u == null)
            return "error";

        unit = u.getName();
        amount = getAmount(milliseconds, u);

        time = String.format(Locale.CANADA, time, amount, unit);
        if (amount > 1)
            time += "s";

        return time;
    }

    public static int getAmount(long milliseconds, Common.TimeUnit unit) {
        for (int i = Common.maxTimeAmount; i >= Common.minTimeAmount; i--)
            if((milliseconds / i) == unit.getMilliseconds())
                return i;

        return Common.minTimeAmount;
    }

    public static Common.TimeUnit getUnit(long milliseconds) {
        Common.TimeUnit[] units = {
                Common.TimeUnit.MINUTE,
                Common.TimeUnit.HOUR,
                Common.TimeUnit.DAY,
                Common.TimeUnit.WEEK
        };

        for (int i = 0; i < units.length-1; i++) {
            Common.TimeUnit curr = units[i];
            Common.TimeUnit up = units[i+1];

            if (milliseconds >= curr.getMilliseconds() && milliseconds < up.getMilliseconds())
                return curr;
        }

        return Common.TimeUnit.WEEK;
    }

    public static IInAppBillingService getBilling(Context context) {
        connection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                service = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                service = IInAppBillingService.Stub.asInterface(binder);
            }
        };
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        return service;
    }

    public static void finishedBilling(Context context) {
        if (service != null)
            context.unbindService(connection);
    }

}
