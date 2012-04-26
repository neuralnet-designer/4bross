package com.samsung.cares.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CustomImageView extends ImageView {

	private Bitmap bitmap;

    public CustomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
    
    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	if(bitmap != null) {
    		int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width * bitmap.getHeight() / bitmap.getWidth();
            setMeasuredDimension(width, height);
        } 
        else {
        	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
    	this.bitmap = bitmap;
        super.setImageBitmap(bitmap);
    }
}
