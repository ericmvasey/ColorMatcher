package yuku.ambilwarna;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class ColorView extends View {

	private boolean mNoColor;
	private Paint mPaint;
	private float mScale;
	private RectF mRectF;
	private Drawable mCheckeredBg;
	private Paint mColorPaint;
	private Drawable mForegroundDrawable;

	public ColorView(Context context) {
		super(context);
		initialize(context);
	}

	public ColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	private void initialize(Context context) {
		mScale = context.getResources().getDisplayMetrics().density;
		mCheckeredBg = context.getResources().getDrawable(
				R.drawable.ambilwarna_checkers_tiled);
		mColorPaint = new Paint();
		mColorPaint.setStyle(Style.FILL);
	}

	public void setForegroundColor(int color) {
		// If fully transparent, show as no color
		int alpha = (int) ((color & 0xff000000L) >> 24);
		mNoColor = alpha == 0;
		mColorPaint.setColor(color);
		invalidate();
	}
	
	public void setForegroundDrawable(Drawable d) {
		mForegroundDrawable = d;
		final int dw = d.getIntrinsicWidth();
		final int dh = d.getIntrinsicHeight();
		
	}

	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);
		final int pl = getPaddingLeft();
		final int pr = getPaddingRight();
		final int pt = getPaddingTop();
		final int pb = getPaddingBottom();
		final int w = getMeasuredWidth() - pl - pr;
		final int h = getMeasuredHeight() - pt - pb;
		mCheckeredBg.setBounds(pl, pt, w + pl, h + pt);
		mCheckeredBg.draw(c);
		if (!mNoColor) {
			c.drawRect(pl, pt, w + pl, h + pt, mColorPaint);
			
			if (mForegroundDrawable != null) {
				final int dw = mForegroundDrawable.getIntrinsicWidth();
				final int dh = mForegroundDrawable.getIntrinsicHeight();
				final int left = (w - dw) / 2 + pl;
				final int top = (h - dh) / 2 + pt;
				mForegroundDrawable.setBounds(left, top, left + dw, top + dh);
				mForegroundDrawable.draw(c);
			}
		} else {
			if (mPaint == null) {
				mPaint = new Paint();
				mPaint.setStrokeWidth(3.5f * mScale);
				mPaint.setStyle(Style.STROKE);
				mPaint.setColor(0xFFFF0000);
				mPaint.setAntiAlias(true);
				mRectF = new RectF();
			}

			final int min;
			if (w < h) {
				min = w - (int) (4 * mScale);
			} else {
				min = h - (int) (4 * mScale);
			}
			final int wOffset = (w - min) / 2;
			final int hOffset = (h - min) / 2;
			mRectF.bottom = h - hOffset + pt;
			mRectF.top = hOffset + pt;
			mRectF.left = wOffset + pl;
			mRectF.right = w - wOffset + pl;
			c.drawOval(mRectF, mPaint);
			final int midX = w / 2 + pl;
			final int midY = h / 2 + pt;
			final int radius = min / 2;
			c.drawLine((int) (midX + radius * Math.cos(Math.PI / 4)),
					(int) (midY + radius * Math.sin(Math.PI / 4)),
					(int) (midX + radius * Math.cos(Math.PI / 4 * 5)),
					(int) (midY + radius * Math.sin(Math.PI / 4 * 5)), mPaint);
		}
	}

	public int getForegroundColor() {
		return mColorPaint.getColor();
	}

}
