package apotheosis.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.List;

import apotheosis.Environment;
import apotheosis.colormatcher.HSLColor;
import apotheosis.views.ColorCardView;

public class ColorHarmoniesAdapter extends BaseAdapter
{
    private List<HSLColor> items;
    private Context c;

    public ColorHarmoniesAdapter(List<HSLColor> items, Context c)
    {
        this.items = items;
        this.c = c;
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ColorCardView card;

        if(convertView == null)
        {
            final float scale = c.getResources().getDisplayMetrics().density;
            int dpWHInPx  = Math.round(Environment.CARD_SIZE_DP * scale);

            HSLColor hslColor = items.get(position);


            card = new ColorCardView(c);
            card.setLayoutParams(new GridView.LayoutParams(dpWHInPx, dpWHInPx));
            card.setCardBackgroundColor(hslColor.getRGB());
/*
            TextView tv = new TextView(c);
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if(hslColor.isPrimaryColor())
                tv.setText("P");
            else if(hslColor.isSecondaryColor())
                tv.setText("S");
            else if(hslColor.isIntermediateColor())
                tv.setText("I");

            tv.setGravity(Gravity.CENTER);
            Palette p = Palette.generate(Bitmap.createBitmap(new int[]{hslColor.getRGB()}, 1, 1, Bitmap.Config.ARGB_8888));

            try
            {
                tv.setTextColor(p.getDarkVibrantSwatch().getBodyTextColor());
            }
            catch(Exception e)
            {
                tv.setTextColor(Color.WHITE);
            }

            card.addView(tv);
*/
            card.setCardElevation(0);
            card.setRadius(2 * scale);
        }
        else
        {
            card = (ColorCardView) convertView;
        }

        return card;
    }
}
