package com.midcore.reader.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import com.glview.graphics.Bitmap;
import com.glview.graphics.Rect;
import com.glview.hwui.GLCanvas;
import com.glview.widget.GridView;
import com.midcore.reader.R;

public class BookGridView extends GridView {

	private Bitmap background;
	
	Rect mRect = new Rect();

	public BookGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		background = new Bitmap(BitmapFactory.decodeResource(context.getResources(),
				R.drawable.bookshelf_layer_center));
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void dispatchDraw(GLCanvas canvas) {
		int count = getChildCount();
		int top = count > 0 ? getChildAt(0).getTop() : 0;
		int backgroundWidth = background.getWidth();
		int backgroundHeight = background.getHeight()+2;
		int width = getWidth();
		int height = getHeight();

		for (int y = top; y < height; y += backgroundHeight) {
			mRect.set(0, y, width, y + backgroundHeight);
			canvas.drawBitmap(background, null, mRect, null);
		}

		super.dispatchDraw(canvas);
	}

}
