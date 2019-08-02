package apotheosis.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.util.Pair;

import java.util.List;

import apotheosis.Environment;
import apotheosis.colormatcher.HSLColor;
import apotheosis.colormatcher.R;
import apotheosis.views.ColorCardView;

public class OutfitListAdapter extends BaseAdapter
{
    private List<Pair<String,HSLColor[]>> outfits;

    public OutfitListAdapter(List<Pair<String,HSLColor[]>> items)
    {
        outfits = items;
    }

    @Override
    public int getCount()
    {
        return outfits.size();
    }

    public String getOutfitName(int position)
    {
        return outfits.get(position).first;
    }

    @Override
    public HSLColor[] getItem(int position)
    {
        return outfits.get(position).second;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void addItem(String title, HSLColor[] outfit)
    {
        outfits.add(new Pair<>(title,outfit));
        notifyDataSetChanged();
    }

    public void removeItem(int position)
    {
        outfits.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_outfit, parent, false);

        final Context c = linearLayout.getContext();

        final int scale = (int) c.getResources().getDisplayMetrics().density;
        int dpWHInPx = Math.round(Environment.CARD_SIZE_DP * scale);

        TextView name = new TextView(parent.getContext());
        name.setPadding(10*scale, 0, 0, 5*scale);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        name.setText(outfits.get(position).first);
        linearLayout.addView(name);

        LinearLayout colorContainer = new LinearLayout(linearLayout.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpWHInPx);
        colorContainer.setLayoutParams(params);
        colorContainer.setOrientation(LinearLayout.HORIZONTAL);

        for (HSLColor color : getItem(position))
        {
            ColorCardView card = new ColorCardView(c);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(dpWHInPx, dpWHInPx);
            cardParams.weight = 1;

            card.setLayoutParams(cardParams);
            card.setRadius(0);
            card.setCardBackgroundColor(color.getRGB());

            colorContainer.addView(card);
        }

        colorContainer.setPadding(0, 0, 0, 3*scale);
        linearLayout.addView(colorContainer);

        return linearLayout;
    }
}