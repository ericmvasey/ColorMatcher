package apotheosis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class Environment
{
    public static final String PREFERENCES_ID = "colormatcher",
        FIRST_LAUNCH_PREFERENCE = "firstLaunch",
        MATCH_FILTER_PREFERENCE = "matchFilter";
     public static final int MATCH_FILTER_PREFERENCE_ALL = 0,
        MATCH_FILTER_PREFERENCE_CLOTHING = 1,
        MATCH_FILTER_PREFERENCE_PAINT = 2;

    public static final int CARD_SIZE_DP = 60;

    public static Bitmap drawableToBitmap (Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static String createImageFromBitmap(Bitmap bitmap, Context c)
    {
        String fileName = "myImage";//no .png or .jpg needed
        try
        {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = c.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fileName = null;
        }

        return fileName;
    }
}
