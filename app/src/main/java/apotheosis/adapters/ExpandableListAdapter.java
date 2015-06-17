package apotheosis.adapters;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import apotheosis.colormatcher.HSLColor;
import apotheosis.colormatcher.R;
import apotheosis.views.ColorCardView;

public class ExpandableListAdapter extends BaseExpandableListAdapter
{
    private List<Pair<HSLColor,String[]>> details;

    public ExpandableListAdapter(List<Pair<HSLColor,String[]>> details)
    {
        this.details = details;
    }

    @Override
    public int getGroupCount()
    {
        return details.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 4;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return details.get(groupPosition).first;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return details.get(groupPosition).second[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        HSLColor color = details.get(groupPosition).first;

        Context c = parent.getContext();

        final float scale = c.getResources().getDisplayMetrics().density;
        int dpHeight = Math.round(75 * scale);

        ColorCardView cv = new ColorCardView(c);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpHeight);
        cv.setCardBackgroundColor(color.getRGB());
        cv.setLayoutParams(lp);

        return cv;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        final Context c = parent.getContext();
        final String item = details.get(groupPosition).second[childPosition];

        if(convertView == null)
        {
            convertView = LayoutInflater.from(c).inflate(R.layout.list_item_outfit_group_details, parent, false);
        }

        TextView detail = (TextView) convertView.findViewById(R.id.detail);
        detail.setText(item);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}
