package apotheosis.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import apotheosis.Environment;
import apotheosis.colormatcher.R;

@SuppressWarnings("deprecation")
public class CameraFragment extends Fragment
{
    private Camera c;
    private CameraPreview preview;

    public CameraFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_camera, container, false);

    }

    @Override
    public void onResume()
    {
        super.onResume();

        try
        {
            c = Camera.open();
            preview = new CameraPreview(getActivity(),c);

            FrameLayout f = (FrameLayout) getActivity().findViewById(R.id.cameraView);
            f.addView(preview);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        c.stopPreview();
        c.setPreviewCallback(null);
        preview.getHolder().removeCallback(preview);
        ((FrameLayout) getActivity().findViewById(R.id.cameraView)).removeView(preview);
        c.release();
    }

    /** A basic Camera preview class */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
    {
        private SurfaceHolder mHolder;
        private Camera camera;
        private Rect rect;
        private Paint p;

        public CameraPreview(Context context, Camera camera)
        {
            super(context);
            this.camera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
        }

        public void surfaceCreated(SurfaceHolder holder)
        {
            // The Surface has been created, now tell the camera where to draw the preview.
            try
            {
                setWillNotDraw(false);
                camera.setPreviewDisplay(holder);
                //mCamera.startPreview();
                invalidate();

                setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        camera.setPreviewCallback(null);
                        camera.autoFocus(new Camera.AutoFocusCallback()
                        {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera)
                            {
                                if(success)
                                {
                                    camera.takePicture(
                                        new Camera.ShutterCallback()
                                        {
                                            @Override
                                            public void onShutter()
                                            {

                                            }
                                        },
                                        null, //raw data
                                        new Camera.PictureCallback()
                                        {
                                            @Override
                                            public void onPictureTaken(byte[] bytes, Camera camera)
                                            {
                                                try
                                                {
                                                    final float scale = getResources().getDisplayMetrics().density;
                                                    int width = getResources().getDisplayMetrics().widthPixels,
                                                            height = getResources().getDisplayMetrics().heightPixels;

                                                    Bitmap b = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                                                    BitmapRegionDecoder bitmapRegionDecoder =
                                                            BitmapRegionDecoder.newInstance(bytes, 0, bytes.length, false);

                                                    Point center = new Point(bitmapRegionDecoder.getWidth() / 2, bitmapRegionDecoder.getHeight() / 2);


                                                    rect = new Rect(
                                                            Math.round(center.x - (50 * scale)), //left
                                                            Math.round(center.y - (50 * scale)), //top
                                                            Math.round(center.x + (50 * scale)), //right
                                                            Math.round(center.y + (50 * scale))); //bottom

                                                    String rectStr = String.format(Locale.getDefault(), "Left: %d Top: %d Right: %d Bottom %d", rect.left, rect.top, rect.right, rect.bottom);
                                                    Log.d("Picture taken", rectStr);

                                                    if (Environment.createImageFromBitmap(bitmapRegionDecoder.decodeRegion(rect, null), getActivity()) == null)
                                                    {
                                                        camera = Camera.open();
                                                        camera.setPreviewDisplay(mHolder);
                                                        camera.startPreview();
                                                        Toast.makeText(getActivity(), "There was a problem taking your picture.", Toast.LENGTH_LONG).show();
                                                        setWillNotDraw(false);
                                                    }
                                                    else
                                                        ((Main) getActivity()).onPictureTaken();
                                                }
                                                catch (Exception e)
                                                {
                                                    e.printStackTrace();
                                                    camera.startPreview();
                                                    Toast.makeText(getActivity(), "There was a problem taking your picture.", Toast.LENGTH_LONG).show();
                                                    setWillNotDraw(false);
                                                }
                                            }
                                        });
                                }
                            }
                        });
                    }
                });
            }
            catch (IOException e)
            {
                Log.d("Camera", "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder)
        {
            try
            {
                if (camera != null)
                {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected void onDraw(final Canvas c)
        {
            if(rect != null)
            {
                c.drawRect(rect, p);
                Log.w(this.getClass().getName(), "On Draw Called");
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
        {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.
            if (mHolder.getSurface() == null)
            {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try
            {
                camera.stopPreview();
            }
            catch (Exception e)
            {
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here
            Camera.Parameters params = c.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            Camera.Size optimal = getOptimalPreviewSize(sizes, getResources().getDisplayMetrics().widthPixels,
                    getResources().getDisplayMetrics().heightPixels);

            params.setPreviewSize(optimal.width, optimal.height);
            c.setParameters(params);
            setCameraDisplayOrientation(getActivity(),0,camera);

            final float scale = getResources().getDisplayMetrics().density;
            int width = getResources().getDisplayMetrics().widthPixels,
                    height = getResources().getDisplayMetrics().heightPixels;
            Point center = new Point(width / 2, height / 2);

            rect = new Rect( Math.round(center.x - (50 * scale)),
                    Math.round(center.y - (50 * scale)),
                    Math.round(center.x + (50 * scale)),
                    Math.round(center.y + (50 * scale)));

            Log.d("Coordinates", rect.toString());

            p = new Paint();
            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(3*scale);

            // start preview with new settings
            try
            {
                camera.setPreviewDisplay(mHolder);
                camera.startPreview();
            }
            catch (Exception e)
            {
                Log.d("Camera", "Error starting camera preview: " + e.getMessage());
            }
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
        int cameraId, android.hardware.Camera camera)
    {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;

        switch (rotation)
        {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        }
        else
        {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h)
    {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes)
        {
            double ratio = (double) size.width / size.height;

            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - h) < minDiff)
            {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null)
        {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes)
            {
                if (Math.abs(size.height - h) < minDiff)
                {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }
}