package apotheosis.colormatcher;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ColorDBHelper extends SQLiteOpenHelper
{
    private static String DB_PATH = "/data/data/apotheosis.colormatcher/databases/";
    private static String DB_NAME= "color.db";
    private Context c;
    private SQLiteDatabase db;

    public ColorDBHelper(Context c)
    {
        super(c, DB_NAME, null, 1);
        this.c = c;
    }

    public void createDB() throws IOException
    {
        if(!checkDB())
        {
            this.getReadableDatabase();
            try
            {
                copyDB();
            }
            catch(IOException e)
            {
                e.printStackTrace();
                throw new IOException("Error copying DB");
            }
        }
    }

    private boolean checkDB()
    {
        SQLiteDatabase db = null;

        try
        {
            String path = DB_PATH + DB_NAME;
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch(SQLiteException e)
        {
            //ignore
        }

        if(db != null)
            db.close();

        return (db != null);
    }

    private void copyDB() throws IOException
    {
        InputStream input = c.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream output = new FileOutputStream(outFileName);

        byte[] buff = new byte[1024];
        int len;

        while((len = input.read(buff)) > 0)
        {
            output.write(buff,0,len);
        }

        output.flush();
        output.close();
        input.close();
    }

    public String lookupColorName(HSLColor color)
    {
        db = SQLiteDatabase.openDatabase(DB_PATH+DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        String red = String.valueOf(Color.red(color.getRGB())),
               green = String.valueOf(Color.green(color.getRGB())),
               blue =  String.valueOf(Color.blue(color.getRGB()));

        Cursor c = db.rawQuery("select name from colors where abs((?-red)/red) < 0.1 and abs((?-green)/green) < 0.1 and abs((?-blue)/blue) < 0.1" +
                        " order by abs((?-red)/red), abs((?-green)/green) , abs((?-blue)/blue) desc",
                new String[]{red, green, blue, red, green, blue});

        String name = null;

        if(c.getCount() > 0)
        {
            c.moveToFirst();
            name = c.getString(0);
        }

        c.close();
        db.close();
        return name;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
