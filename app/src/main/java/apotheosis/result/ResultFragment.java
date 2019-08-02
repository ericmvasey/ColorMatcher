package apotheosis.result;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;

import apotheosis.Environment;
import apotheosis.adapters.ColorHarmoniesAdapter;
import apotheosis.colormatcher.ColorAnalyzer;
import apotheosis.colormatcher.HSLColor;
import apotheosis.colormatcher.R;
import apotheosis.views.ColorCardView;


public class ResultFragment extends Fragment
{
    public ResultFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View root = inflater.inflate(R.layout.fragment_result, container, false);

        ColorAnalyzer colorAnalyzer = new ColorAnalyzer(getActivity(), root);

        if(!colorAnalyzer.getStatus().equals(AsyncTask.Status.RUNNING))
        {
            try
            {
                Bitmap b = BitmapFactory.decodeStream(getActivity().openFileInput("myImage"));
                colorAnalyzer.execute(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();

                Environment.createImageFromBitmap(Environment.drawableToBitmap(new ColorDrawable(Color.BLUE)), getActivity());

                try
                {
                    Bitmap b = BitmapFactory.decodeStream(getActivity().openFileInput("myImage"));
                    colorAnalyzer.execute(b);
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                    colorAnalyzer.execute(Environment.drawableToBitmap(new ColorDrawable(Color.BLUE)));
                }
            }
        }

        GridView matches = (GridView) root.findViewById(R.id.matches);
        matches.setOnItemClickListener(addMatchingColor);

        return root;
    }


    private GridView.OnItemClickListener addMatchingColor = new GridView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
        {
            final LinearLayout selections = (LinearLayout) getActivity().findViewById(R.id.selections);

            HSLColor colorToAdd = ((ColorCardView) view).getColor();
            if(selections.getChildCount() < 4 && !hasColor(selections, colorToAdd.getRGB()))
            {
                if(hasPrimaryColor(selections) && colorToAdd.isPrimaryColor())
                {
                    Toast.makeText(getActivity(), "You really shouldn't have multiple primary colors in an outfit. Proceed with caution.",
                            Toast.LENGTH_LONG).show();
                }

                final float scale = getActivity().getResources().getDisplayMetrics().density;
                int dpWHInPx = Math.round(Environment.CARD_SIZE_DP * scale);

                ColorHarmoniesAdapter adapter = (ColorHarmoniesAdapter) parent.getAdapter();

                ColorCardView card = new ColorCardView(getActivity());

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpWHInPx, dpWHInPx);
                lp.setMargins(0,0,(int) (5*scale),0);

                card.setLayoutParams(lp);
                card.setCardBackgroundColor(((HSLColor) adapter.getItem(position)).getRGB());
                card.setRadius(2 * scale);



                card.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        selections.removeView(v);
                        view.setEnabled(true);
                    }
                });

                selections.addView(card);
            }
        }
    };

    private boolean hasColor(LinearLayout selections, int color)
    {
        for(int i = 0; i < selections.getChildCount(); i++)
        {
            ColorCardView v = (ColorCardView) selections.getChildAt(i);

            if(v.getColor().getRGB() == color)
                return true;
        }

        return false;
    }

    private boolean hasPrimaryColor(LinearLayout selections)
    {
        for (int i = 0; i < selections.getChildCount(); i++)
        {
            ColorCardView v = (ColorCardView) selections.getChildAt(i);

            if (v.getColor().isPrimaryColor())
                return true;
        }

        return false;
    }
}
