package yuku.ambilwarna;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class AmbilWarnaDialog {
	public interface OnAmbilWarnaListener {
		void onCancel(AmbilWarnaDialog dialog);
		void onOk(AmbilWarnaDialog dialog, int color);
	}

	final AlertDialog dialog;
	final OnAmbilWarnaListener listener;
	final View viewHue;
	final AmbilWarnaKotak viewSatVal;
	final ImageView viewCursor;
	final ColorView viewOldColor;
	final ColorView viewNewColor;
	final ImageView viewTarget;
	final ViewGroup viewContainer;
	final float[] currentColorHsv = new float[3];
	private AmbilWarnaAlpha viewAlpha;
	private float currentAlpha;
	private ImageView viewCursorAlpha;
	private boolean mDoAlpha;
	
	private static final String TAG = "AmbilWarnaDialog";
	
	/**
	 * create an AmbilWarnaDialog. call this only from OnCreateDialog() or from a background thread.
	 * 
	 * @param context
	 *            current context
	 * @param color
	 *            current color
	 * @param doAlpha
	 * 			  true if alpha can be picked too
	 * @param listener
	 *            an OnAmbilWarnaListener, allowing you to get back error or
	 */
	public AmbilWarnaDialog(final Context context, int color, OnAmbilWarnaListener listener) {
		this(context, color, false, listener);
	}

	/**
	 * create an AmbilWarnaDialog. call this only from OnCreateDialog() or from a background thread.
	 * 
	 * @param context
	 *            current context
	 * @param color
	 *            current color
	 * @param doAlpha
	 * 			  true if alpha can be picked too
	 * @param listener
	 *            an OnAmbilWarnaListener, allowing you to get back error or
	 */
	public AmbilWarnaDialog(final Context context, int color, boolean doAlpha, OnAmbilWarnaListener listener) {
		this.listener = listener;
		Color.colorToHSV(color, currentColorHsv);
		
		// We need to do this to work around the signedness of Java ints
		currentAlpha = (float)((color & 0x00000000ffffffffL) >> 24) / 256f;

		final View view = LayoutInflater.from(context).inflate(R.layout.ambilwarna_dialog, null);
		viewHue = view.findViewById(R.id.ambilwarna_viewHue);
		viewAlpha = (AmbilWarnaAlpha)view.findViewById(R.id.ambilwarna_viewAlpha);
		
		viewSatVal = (AmbilWarnaKotak) view.findViewById(R.id.ambilwarna_viewSatBri);
		viewCursor = (ImageView) view.findViewById(R.id.ambilwarna_cursor);
		viewCursorAlpha = (ImageView) view.findViewById(R.id.ambilwarna_cursorAlpha);
		viewOldColor = (ColorView)view.findViewById(R.id.ambilwarna_warnaLama);
		viewNewColor = (ColorView)view.findViewById(R.id.ambilwarna_warnaBaru);
		viewTarget = (ImageView) view.findViewById(R.id.ambilwarna_target);
		viewContainer = (ViewGroup) view.findViewById(R.id.ambilwarna_viewContainer);
		
		if (!doAlpha) {
			viewAlpha.setVisibility(View.GONE);
			viewCursorAlpha.setVisibility(View.GONE);
		}
		
		mDoAlpha = doAlpha;
		
		viewSatVal.setHue(getHue());
		viewAlpha.setColor(color);
		viewOldColor.setForegroundColor(color);
		viewNewColor.setForegroundColor(color);

		viewHue.setOnTouchListener(new View.OnTouchListener() {
			@Override public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_UP) {

					float y = event.getY();
					if (y < 0.f) y = 0.f;
					if (y > viewHue.getMeasuredHeight()) y = viewHue.getMeasuredHeight() - 0.001f; // to avoid looping from end to start.
					float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
					if (hue == 360.f) hue = 0.f;
					setHue(hue);

					// update view
					viewSatVal.setHue(getHue());
					moveCursor();
					int currentColor = getColor();
					if (mDoAlpha) {
						viewNewColor.setForegroundColor(getColorWithAlpha());
					} else {
						viewNewColor.setForegroundColor(currentColor);
					}
					viewAlpha.setColor(currentColor);

					return true;
				}
				return false;
			}
		});

		viewAlpha.setOnTouchListener(new View.OnTouchListener() {
			@Override public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_UP) {

					float y = event.getY();
					if (y < 0.f) y = 0.f;
					if (y > viewAlpha.getMeasuredHeight()) y = viewAlpha.getMeasuredHeight() - 0.001f; // to avoid looping from end to start.
					float alpha = 1.0f - 1.0f / viewHue.getMeasuredHeight() * y;
					if (alpha < 1.0f/255f) {
						alpha = 1.0f/255f;
					}
					Log.d (TAG, "Alpha " + alpha + ", h " + viewHue.getMeasuredHeight() + " y " + y);
					setAlpha(alpha);

					// TODO: update view
					moveCursorAlpha();
					viewNewColor.setForegroundColor(getColorWithAlpha());
					
					return true;
				}
				return false;
			}
		});
		
		viewSatVal.setOnTouchListener(new View.OnTouchListener() {
			@Override public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_UP) {

					float x = event.getX(); // touch event are in dp units.
					float y = event.getY();

					if (x < 0.f) x = 0.f;
					if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
					if (y < 0.f) y = 0.f;
					if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

					setSat(1.f / viewSatVal.getMeasuredWidth() * x);
					setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

					// update view
					moveTarget();
					int currentColor = getColor();
					if (mDoAlpha) {
						viewNewColor.setForegroundColor(getColorWithAlpha());
					} else {
						viewNewColor.setForegroundColor(currentColor);
					}
					viewAlpha.setColor(currentColor);

					return true;
				}
				return false;
			}
		});

		dialog = new AlertDialog.Builder(context)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					if (AmbilWarnaDialog.this.listener != null) {
						AmbilWarnaDialog.this.listener.onOk(AmbilWarnaDialog.this, getColorWithAlpha());
					}
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					if (AmbilWarnaDialog.this.listener != null) {
						AmbilWarnaDialog.this.listener.onCancel(AmbilWarnaDialog.this);
					}
				}
			})
			.setOnCancelListener(new OnCancelListener() {
				// if back button is used, call back our listener.
				@Override public void onCancel(DialogInterface paramDialogInterface) {
					if (AmbilWarnaDialog.this.listener != null) {
						AmbilWarnaDialog.this.listener.onCancel(AmbilWarnaDialog.this);
					}

				}
			})
			.create();
		// kill all padding from the dialog window
		dialog.setView(view, 0, 0, 0, 0);

		// move cursor & target on first draw
		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override public void onGlobalLayout() {
				moveCursor();
				moveCursorAlpha();
				moveTarget();
				view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	protected void moveCursor() {
		float y = viewHue.getMeasuredHeight() - (getHue() * viewHue.getMeasuredHeight() / 360.f);
		if (y == viewHue.getMeasuredHeight()) y = 0.f;
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
		layoutParams.leftMargin = (int) (viewHue.getLeft() - Math.floor(viewCursor.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
		layoutParams.topMargin = (int) (viewHue.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
		viewCursor.setLayoutParams(layoutParams);
	}
	
	protected void moveCursorAlpha() {
		float y = viewAlpha.getMeasuredHeight() - (getAlpha() * viewAlpha.getMeasuredHeight());
		if (y == viewAlpha.getMeasuredHeight()) y = 0.f;
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursorAlpha.getLayoutParams();
		layoutParams.leftMargin = (int) (viewAlpha.getLeft() - Math.floor(viewCursorAlpha.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
		layoutParams.topMargin = (int) (viewAlpha.getTop() + y - Math.floor(viewCursorAlpha.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
		viewCursorAlpha.setLayoutParams(layoutParams);
	}

	protected void moveTarget() {
		float x = getSat() * viewSatVal.getMeasuredWidth();
		float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
		layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
		layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
		viewTarget.setLayoutParams(layoutParams);
	}

	private int getColor() {
		return Color.HSVToColor(currentColorHsv);
	}
	
	private int getColorWithAlpha() {
		return (Color.HSVToColor(currentColorHsv) & 0x00FFFFFF) + ((int)(currentAlpha * 255) << 24);
	}

	private float getHue() {
		return currentColorHsv[0];
	}

	private float getSat() {
		return currentColorHsv[1];
	}

	private float getVal() {
		return currentColorHsv[2];
	}

	private void setHue(float hue) {
		currentColorHsv[0] = hue;
	}
	
	private void setAlpha(float alpha) {
		currentAlpha = alpha;
	}
	
	private float getAlpha() {
		return currentAlpha;
	}

	private void setSat(float sat) {
		currentColorHsv[1] = sat;
	}

	private void setVal(float val) {
		currentColorHsv[2] = val;
	}

	public void show() {
		dialog.show();
	}

	public AlertDialog getDialog() {
		return dialog;
	}
}
