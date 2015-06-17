package apotheosis.result;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;

import apotheosis.Environment;
import apotheosis.adapters.OutfitListAdapter;
import apotheosis.colormatcher.ColorAnalyzer;
import apotheosis.colormatcher.HSLColor;
import apotheosis.colormatcher.R;
import apotheosis.outfit.OutfitDBHelper;
import apotheosis.outfit.OutfitFragment;
import apotheosis.views.ColorCardView;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ResultActivity extends ActionBarActivity
{
    private ActionBarDrawerToggle toggle;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Color Matcher");
        setSupportActionBar(toolbar);

        initResultFragment();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.outfit_list_name, R.string.app_name)
        {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                getSupportActionBar().setListNavigationCallbacks(null, null);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(ResultActivity.this, R.array.outfit_types,
                        R.layout.spinner_list_item);

                getSupportActionBar().setListNavigationCallbacks(spinnerAdapter, new ActionBar.OnNavigationListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(int itemPosition, long itemId)
                    {
                        ListView lv = (ListView) findViewById(R.id.left_drawer_result_activity);
                        if(itemPosition == 0)
                        {
                            OutfitListAdapter adapter = new OutfitListAdapter(new OutfitDBHelper(ResultActivity.this).getAllUserOutfits());
                            lv.setAdapter(adapter);

                            lv.setOnItemLongClickListener(new ListView.OnItemLongClickListener()
                            {
                                @Override
                                public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id)
                                {
                                    Log.d("Item Index Clicked", String.valueOf(position));
                                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            new OutfitDBHelper(ResultActivity.this).removeOutfit((HSLColor[]) parent.getAdapter().getItem(position));
                                            ((OutfitListAdapter)parent.getAdapter()).removeItem(position);
                                        }
                                    });

                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.setTitle("Delete this outfit?");
                                    builder.show();

                                    return true;
                                }
                            });

                            return true;
                        }
                        else if(itemPosition == 1)
                        {
                            OutfitListAdapter adapter = new OutfitListAdapter(new OutfitDBHelper(ResultActivity.this).getAllPreDefinedOutfits());
                            lv.setAdapter(adapter);
                            lv.setOnItemLongClickListener(null);
                            return true;
                        }
                        return false;
                    }
                });
            }

        };
        drawerLayout.setDrawerListener(toggle);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        boolean isDrawerOpen = drawerLayout.isDrawerOpen(findViewById(R.id.left_drawer_result));

        if(isDrawerOpen)
            drawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        OutfitListAdapter adapter = new OutfitListAdapter(new OutfitDBHelper(this).getAllUserOutfits());
        ListView lv = (ListView) findViewById(R.id.left_drawer_result_activity);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                HSLColor[] outfit = ((OutfitListAdapter) parent.getAdapter()).getItem(position);
                int[] rgbs = new int[outfit.length];
                for(int i = 0; i < outfit.length; i++)
                {
                    rgbs[i] = outfit[i].getRGB();
                }

                Bundle args = new Bundle();
                args.putIntArray("colors", rgbs);

                OutfitFragment frag = new OutfitFragment();
                frag.setArguments(args);

                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                boolean isDrawerOpen = drawerLayout.isDrawerOpen(findViewById(R.id.left_drawer_result));

                if(isDrawerOpen)
                    drawerLayout.closeDrawers();

                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.resultContainerFragment, frag)
                        .addToBackStack(null)
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                                android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                        .commit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggle state
        toggle.onConfigurationChanged(newConfig);
    }

    public void addOutfit(View view)
    {
        LinearLayout selections = (LinearLayout) findViewById(R.id.selections);
        ArrayList<HSLColor> items = new ArrayList<>();

        for(int i = 0; i < selections.getChildCount(); i++)
        {
            ColorCardView card = (ColorCardView) selections.getChildAt(i);
            items.add(card.getColor());
        }

        final HSLColor[] outfit = items.toArray(new HSLColor[items.size()]);
        final EditText nameInput = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Enter a Name");
        builder.setMessage("Enter a name for your new outfit!");
        builder.setView(nameInput);
        builder.setPositiveButton("Ok", new AlertDialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ListView lv = (ListView) findViewById(R.id.left_drawer_result_activity);
                ((OutfitListAdapter) lv.getAdapter()).addItem(nameInput.getText().toString(), outfit);
                new OutfitDBHelper(ResultActivity.this).addOutfit(nameInput.getText().toString(), outfit);
            }
        });

        builder.setNegativeButton("Cancel", new AlertDialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void initResultFragment()
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.resultContainerFragment, new ResultFragment())
                .commit();
    }

    public void setAvgColor(View v)
    {
        final ColorCardView avgColor = (ColorCardView) findViewById(R.id.avgColor);

        new AmbilWarnaDialog(this, avgColor.getColor().getRGB(), new AmbilWarnaDialog.OnAmbilWarnaListener()
        {
            @Override
            public void onCancel(AmbilWarnaDialog dialog)
            {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color)
            {
                final LinearLayout root = (LinearLayout) findViewById(R.id.fragment_result_root),
                        selections = (LinearLayout) findViewById(R.id.selections);

                avgColor.setCardBackgroundColor(color);
                selections.removeAllViews();
                Environment.createImageFromBitmap(Environment.drawableToBitmap(new ColorDrawable(color)), ResultActivity.this);

                try
                {
                    Bitmap b = BitmapFactory.decodeStream(ResultActivity.this.openFileInput("myImage"));
                    new ColorAnalyzer(ResultActivity.this, root).execute(b);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).show();
    }
}