package apotheosis.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.IOException;

import apotheosis.Environment;
import apotheosis.adapters.ImageCallback;
import apotheosis.colormatcher.ColorDBHelper;
import apotheosis.colormatcher.R;
import apotheosis.result.ResultActivity;

public class Main extends AppCompatActivity implements ImageCallback
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if(!(permissionCheck == PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    123);
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch(requestCode)
        {
            case 123:
                if(!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    finish();
        }
    }

    private void initCameraFragment()
    {
        CameraFragment cameraFragment = new CameraFragment();
        cameraFragment.setImageCallback(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, cameraFragment)
                .commit();
    }

    @Override
    public void onPictureTaken()
    {
        startActivity(new Intent(Main.this, ResultActivity.class));
    }
}
