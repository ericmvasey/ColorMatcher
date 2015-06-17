package yuku.ambilwarna;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class AmbilWarnaAlpha extends View {
	Paint paint, colorPaint;
	Shader luar;
	int[] color;
	private GradientDrawable drawable;
	private BitmapDrawable checkers;

	public AmbilWarnaAlpha(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public AmbilWarnaAlpha(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}
	
	private void initialize () {
		setColor (0);
		checkers = (BitmapDrawable)getResources().getDrawable(R.drawable.ambilwarna_checkers);
		checkers.setTileModeX(Shader.TileMode.REPEAT);
		checkers.setTileModeY(Shader.TileMode.REPEAT);
	}
	
	@SuppressLint("NewApi") public void setColor (int color) {
		if (this.color == null) {
			this.color = new int[] { color & 0x00FFFFFF, color | 0xFF000000 };
		} else {
			this.color[0] = color & 0x00FFFFFF;
			this.color[1] = color | 0xFF000000;
 		}
		
		if (this.drawable == null || Build.VERSION.SDK_INT < 16) {
			drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, this.color);
		} else {
			drawable.setColors(this.color); 
		}
		invalidate();
	}
	
	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		checkers.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
		checkers.draw(canvas);
		drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
		drawable.draw(canvas);
		
		/* if (paint == null) {
			paint = new Paint();
			paint.setShader(luar);
			luar = new LinearGradient(0.f, 0.f, 0.f, this.getMeasuredHeight(), 0xffffffff, 0x00ffffff, TileMode.CLAMP);
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			colorPaint = new Paint();
			colorPaint.setColor(this.color);
		}
		canvas.drawRect(0.f, 0.f, this.getMeasuredWidth(), this.getMeasuredHeight(), colorPaint);
		canvas.drawRect(0.f, 0.f, this.getMeasuredWidth(), this.getMeasuredHeight(), paint); */
	}
}
