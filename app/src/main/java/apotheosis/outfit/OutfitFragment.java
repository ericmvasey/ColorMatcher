package apotheosis.outfit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.LinkedList;

import apotheosis.adapters.ExpandableListAdapter;
import apotheosis.colormatcher.ColorDBHelper;
import apotheosis.colormatcher.HSLColor;
import apotheosis.colormatcher.R;

public class OutfitFragment extends Fragment
{
    public OutfitFragment()
    {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_outfit, container, false);

        ExpandableListView lv = (ExpandableListView) root.findViewById(R.id.outfitList);

        LinkedList<Pair<HSLColor,String[]>> items = new LinkedList<>();
        int[] colors = getArguments().getIntArray("colors");

        for(int intColor: colors)
        {
            HSLColor color = new HSLColor(intColor);
            int colorRgb = color.getRGB();
            int r = (colorRgb >> 16) & 0xFF;
            int g = (colorRgb >> 8) & 0xFF;
            int b = (colorRgb) & 0xFF;

            String hasName = new ColorDBHelper(getActivity()).lookupColorName(color);
            String colorName;

            if(hasName != null)
                colorName = hasName;
            else
                colorName="No name found.";

            String rgbText = "RGB: (" + r + ", " + g + ", " + b + ")";
            String hexText = String.format("Hex: #%02X%02X%02X", r, g, b);
            String hslText = String.format("HSL: (%.2f, %.2f, %.2f)", color.getHue(), color.getSaturation(), color.getLuminance());

            String rgbcpy = "(" + r + "," + g + "," + b + ")";
            String hexcpy = String.format("#%02X%02X%02X", r, g, b);
            String hslcpy = String.format("(%.2f, %.2f, %.2f)", color.getHue(), color.getSaturation(), color.getLuminance());

            items.add(new Pair<>(color, new String[]{colorName,rgbText,hexText,hslText, colorName, rgbcpy, hexcpy, hslcpy}));
        }

        final ExpandableListAdapter adapter = new ExpandableListAdapter(items);
        lv.setAdapter(adapter);

        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText((String) adapter.getChild(groupPosition,childPosition+4));
                Toast.makeText(getActivity(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return root;
    }
}