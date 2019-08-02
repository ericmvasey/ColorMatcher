package apotheosis.outfit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import androidx.core.util.Pair;

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

        HSLColor[] outfit = new HSLColor[]{new HSLColor(HSLColor.fromRGB(Color.BLUE)), new HSLColor(Color.GRAY)};
        db.execSQL("INSERT INTO outfits (type,colors, name) VALUES (0," + hslToString(outfit) + ", '" + "Blue and Gray" + "');");

        outfit = new HSLColor[]{new HSLColor(HSLColor.fromRGB(Color.BLUE)), new HSLColor(Color.WHITE)};
        db.execSQL("INSERT INTO outfits (type,colors, name) VALUES (0," + hslToString(outfit) + ", '" + "Blue and White" + "');");

        outfit = new HSLColor[]{new HSLColor(Color.rgb(210,180,140)), new HSLColor(Color.rgb(128,0,0))};
        db.execSQL("INSERT INTO outfits (type,colors, name) VALUES (0," + hslToString(outfit) + ", '" + "Tan and Maroon" + "');");

        outfit = new HSLColor[]{new HSLColor(Color.rgb(255,105,180)), new HSLColor(Color.BLACK)};
        db.execSQL("INSERT INTO outfits (type,colors, name) VALUES (0," + hslToString(outfit) + ", '" + "Pink and Black" + "');");

        outfit = new HSLColor[]{new HSLColor(Color.rgb(160,32,240)), new HSLColor(Color.rgb(245,245,220))};
        db.execSQL("INSERT INTO outfits (type,colors, name) VALUES (0," + hslToString(outfit) + ", '" + "Purple and Beige" + "');");
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
        getWritableDatabase().execSQL("INSERT INTO outfits (type,colors, name) VALUES (1," + hslToString(outfit) + ", '" + title + "');");
    }

    private String hslToString(HSLColor[] outfit)
    {
        StringBuilder colors = new StringBuilder("'");

        for(HSLColor color: outfit)
        {
            colors.append(color.getRGB());
            colors.append(",");
        }

        colors.replace(colors.length()-1,colors.length(),"'");
        return colors.toString();
    }
}
