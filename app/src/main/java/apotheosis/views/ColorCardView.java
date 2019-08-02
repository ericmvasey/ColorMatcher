package apotheosis.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;

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
