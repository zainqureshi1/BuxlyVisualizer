package com.e2esp.buxlypaints.visualizer.models;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.e2esp.buxlypaints.visualizer.interfaces.OnWallImageTouchListener;

/**
 * Created by Zain on 2/1/2017.
 */

public class Wall {
    private String name;
    private int imageRes;
    private int borderImageRes;
    private int defaultColor;

    private ImageView wallImage;
    private ImageView borderImage;

    private boolean selected;
    private SecondaryColor selectedColor;

    public Wall(String name, int imageRes, int borderImageRes, int defaultColor) {
        this.name = name;
        this.imageRes = imageRes;
        this.borderImageRes = borderImageRes;
        this.defaultColor = defaultColor;
        this.wallImage = null;
        this.borderImage = null;
        this.selected = false;
        this.selectedColor = null;
    }

    public String getName() {
        return name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public ImageView getWallImage() {
        return wallImage;
    }

    public int getBorderImageRes() {
        return borderImageRes;
    }

    public void setWallImage(final ImageView wallImage, ImageView borderImage, final OnWallImageTouchListener onWallTouchListener) {
        this.wallImage = wallImage;
        this.borderImage = borderImage;
        this.wallImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float eventX = event.getX();
                float eventY = event.getY();
                float[] eventXY = new float[] {eventX, eventY};

                Matrix invertMatrix = new Matrix();
                ((ImageView)view).getImageMatrix().invert(invertMatrix);

                invertMatrix.mapPoints(eventXY);
                int x = Integer.valueOf((int)eventXY[0]);
                int y = Integer.valueOf((int)eventXY[1]);

                Drawable drawable = ((ImageView)view).getDrawable();
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

                //Limit x, y range within bitmap
                if(x < 0) {
                    x = 0;
                } else if (x > bitmap.getWidth()-1) {
                    x = bitmap.getWidth()-1;
                }

                if (y < 0) {
                    y = 0;
                } else if (y > bitmap.getHeight()-1) {
                    y = bitmap.getHeight()-1;
                }

                int touchedRGB = bitmap.getPixel(x, y);

                /*Log.d("Touched", "touched position: "
                        + String.valueOf(eventX) + " / "
                        + String.valueOf(eventY));
                Log.d("Inverted", "touched position: "
                        + String.valueOf(x) + " / "
                        + String.valueOf(y));
                Log.d("Size", "drawable size: "
                        + String.valueOf(bitmap.getWidth()) + " / "
                        + String.valueOf(bitmap.getHeight()));
                Log.d("Color", "touched color: " + "#" + Integer.toHexString(touchedRGB));*/

                if (touchedRGB != 0) {
                    onWallTouchListener.onWallTouch(Wall.this);
                    return true;
                }

                return false;
            }
        });
    }

    public void showBorder(boolean show) {
        if (borderImage != null) {
            borderImage.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public SecondaryColor getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(SecondaryColor selectedColor) {
        this.selectedColor = selectedColor;
        if (wallImage != null) {
            wallImage.setColorFilter(selectedColor.getColor());
        }
    }

    public Wall clone() {
        return new Wall(getName(), getImageRes(), getBorderImageRes(), getDefaultColor());
    }

    public void recycle() {
        if (wallImage != null) {
            ((BitmapDrawable)wallImage.getDrawable()).getBitmap().recycle();
        }
        if (borderImage != null) {
            ((BitmapDrawable)borderImage.getDrawable()).getBitmap().recycle();
        }
    }

}
