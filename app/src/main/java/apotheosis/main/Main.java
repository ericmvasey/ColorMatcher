package apotheosis.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.crittercism.app.Crittercism;

import java.io.IOException;

import apotheosis.Environment;
import apotheosis.adapters.ImageCallback;
import apotheosis.colormatcher.ColorDBHelper;
import apotheosis.colormatcher.R;
import apotheosis.result.ResultActivity;

public class Main extends ActionBarActivity implements ImageCallback
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Crittercism.initialize(getApplicationContext(), Environment.CRITTERCISM_APP_ID);

        ColorDBHelper colorDBHelper = new ColorDBHelper(this);
        try
        {
            colorDBHelper.createDB();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        if (savedInstanceState == null)
        {
            SharedPreferences prefs = getSharedPreferences(Environment.PREFERENCES_ID, Context.MODE_PRIVATE);

            if (prefs.getBoolean(Environment.FIRST_LAUNCH_PREFERENCE, true))
            {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.mainContainer, new FirstLaunchFragment())
                        .commit();
            }
            else
            {
                initCameraFragment();
            }
        }
    }

    public void loadImageCapture(View v)
    {
        SharedPreferences prefs = getSharedPreferences("colormatcher", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("firstLaunch", false);
        edit.apply();

        initCameraFragment();
    }

    private void initCameraFragment()
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, new CameraFragment())
                .commit();
    }

    @Override
    public void onPictureTaken()
    {
        startActivity(new Intent(Main.this, ResultActivity.class));
    }
}
