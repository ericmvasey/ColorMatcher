package apotheosis.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import apotheosis.colormatcher.HSLColor;

public class ColorCardView extends CardView
{
    private HSLColor color;
    public ColorCardView(Context context)
    {
        super(context);
    }

    public ColorCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void setCardBackgroundColor(int color)
    {
        super.setCardBackgroundColor(color);
        this.color = new HSLColor(color);
    }

    public HSLColor getColor()
    {
        return color;
    }
}
