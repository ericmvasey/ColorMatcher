package apotheosis.colormatcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import apotheosis.Environment;
import apotheosis.adapters.ColorHarmoniesAdapter;
import apotheosis.views.ColorCardView;

public class ColorAnalyzer extends AsyncTask<Bitmap, Integer, Integer>
{
    private View parent;
    private Context c;
    public ColorAnalyzer(Context c, View parent)
    {
        this.c = c;
        this.parent = parent;
    }

    @Override
    protected Integer doInBackground(Bitmap... bitmaps)
    {
        Bitmap bitmap = bitmaps[0];
        /*int[] pixels = new int[ (bitmap.getHeight() * bitmap.getWidth())];

        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int pixelCount = pixels.length;

        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 1, 1, bitmap.getWidth()-1, bitmap.getHeight()-1);

        for(int i: pixels)
        {
            redBucket += Color.red(i);
            blueBucket += Color.blue(i);
            greenBucket += Color.green(i);
        }


        return Color.rgb( redBucket/pixelCount,
                greenBucket/pixelCount,
                blueBucket/pixelCount);*/

        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = scaled.getPixel(0,0);
        scaled.recycle();
        return color;
    }

    @Override
    protected void onPostExecute(Integer result)
    {
        HSLColor detectedColor = new HSLColor(result);

        ((CardView) parent.findViewById(R.id.avgColor)).setCardBackgroundColor(detectedColor.getRGB());

        ColorCardView firstSelection = new ColorCardView(c);

        final float scale = c.getResources().getDisplayMetrics().density;
        int dpWHInPx  = Math.round(Environment.CARD_SIZE_DP * scale);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpWHInPx, dpWHInPx);
        lp.setMargins(0,0,(int)(5*scale),0);
        firstSelection.setLayoutParams(lp);
        firstSelection.setCardBackgroundColor(detectedColor.getRGB());
        firstSelection.setRadius(2 * scale);

        ((LinearLayout) parent.findViewById(R.id.selections)).addView(firstSelection);

        GridView complements = (GridView) parent.findViewById(R.id.matches);

        ColorHarmoniesAdapter adapter;
        if(detectedColor.getLuminance() < 20f)
        {
            adapter = new ColorHarmoniesAdapter(HSLColor.getBComplements(), c);
        }
        else if(detectedColor.getLuminance() > 75f)
        {
            adapter = new ColorHarmoniesAdapter(HSLColor.getWComplements(), c);
        }
        else if(detectedColor.getSaturation() < 5f)
        {
            adapter = new ColorHarmoniesAdapter(HSLColor.getGComplements(), c);
        }
        else
        {
            SharedPreferences preferences = c.getSharedPreferences(Environment.PREFERENCES_ID, Context.MODE_PRIVATE);

            switch (preferences.getInt(Environment.MATCH_FILTER_PREFERENCE, Environment.MATCH_FILTER_PREFERENCE_ALL))
            {
                case Environment.MATCH_FILTER_PREFERENCE_ALL:
                    adapter = new ColorHarmoniesAdapter(detectedColor.getAllHarmonies(), c);
                    break;

                case Environment.MATCH_FILTER_PREFERENCE_CLOTHING:
                    adapter = new ColorHarmoniesAdapter(detectedColor.getClothingHarmonies(), c);
                    break;

                default:
                    adapter = new ColorHarmoniesAdapter(detectedColor.getAllHarmonies(), c);
                    break;
            }
        }
        complements.setAdapter(adapter);
        parent.setVisibility(View.VISIBLE);

        super.onPostExecute(result);
    }
}
