package apotheosis.outfit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

import apotheosis.colormatcher.HSLColor;

public class OutfitDBHelper extends SQLiteOpenHelper
{
    public OutfitDBHelper(Context context)
    {
        super(context, "outfits", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE outfits (type integer, colors TEXT, name TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public List<Pair<String,HSLColor[]>> getAllUserOutfits()
    {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * from outfits where type=1;", null);
        cursor.moveToFirst();

        ArrayList<Pair<String,HSLColor[]>> outfits = new ArrayList<>();

        if(cursor.getCount() > 0)
        {
            do
            {
                ArrayList<HSLColor> outfit = new ArrayList<>();
                String[] colors = cursor.getString(1).split(",");

                for(String color: colors)
                    outfit.add(new HSLColor(Integer.parseInt(color)));

                outfits.add(new Pair<>(cursor.getString(2),outfit.toArray(new HSLColor[outfit.size()])));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return outfits;
    }

    public List<Pair<String,HSLColor[]>> getAllPreDefinedOutfits()
    {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * from outfits where type=0;", null);
        cursor.moveToFirst();

        ArrayList<Pair<String,HSLColor[]>> outfits = new ArrayList<>();

        if(cursor.getCount() > 0)
        {
            do
            {
                ArrayList<HSLColor> outfit = new ArrayList<>();
                String[] colors = cursor.getString(1).split(",");

                for(String color: colors)
                    outfit.add(new HSLColor(Integer.parseInt(color)));

                outfits.add(new Pair<>(cursor.getString(2),outfit.toArray(new HSLColor[outfit.size()])));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return outfits;
    }

    public void removeOutfit(HSLColor[] outfit)
    {
        StringBuilder colors = new StringBuilder("'");

        for(HSLColor color: outfit)
        {
            colors.append(color.getRGB());
            colors.append(",");
        }

        colors.replace(colors.length()-1,colors.length(),"'");

        getWritableDatabase().execSQL("DELETE FROM outfits WHERE colors=" + colors + " and type=1;");
    }

    public void addOutfit(String title, HSLColor[] outfit)
    {
        StringBuilder colors = new StringBuilder("'");

        for(HSLColor color: outfit)
        {
            colors.append(color.getRGB());
            colors.append(",");
        }

        colors.replace(colors.length()-1,colors.length(),"'");

        getWritableDatabase().execSQL("INSERT INTO outfits (type,colors, name) VALUES (1," + colors.toString() + ", '" + title + "');");
    }
}
