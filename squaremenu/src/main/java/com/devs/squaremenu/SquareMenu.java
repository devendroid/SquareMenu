package com.devs.squaremenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ValueAnimator;


/**
 * @author Deven
 *
 *         Licensed under the Apache License 2.0 license see:
 *         http://www.apache.org/licenses/LICENSE-2.0
 */

public class SquareMenu extends View {

    private static final String TAG = SquareMenu.class.getSimpleName();

    // Default values
    private static final int FAB_MARGIN = 8;
    private static final int FAB_ICON_PADDING = 20;
    private static final float FAB_ICON_WIDTH = 2;
    private static final int MARGIN_BW_SQUARES = 3;
    private static final int FAB_SIZE = 56;
    private static final int FAB_COLOR = Color.RED;
    private static final long ANIM_SPEED = 200;
    private static final String TOP_LEFT = "top_left";
    private static final String TOP_RIGHT = "top_right";
    private static final String DEFAULT_DIRECTION = TOP_LEFT;

    // Custom values
    private int fabWidth = FAB_SIZE, fabHeight = FAB_SIZE;
    private int fabColor = FAB_COLOR;
    private boolean autoClose = false;
    private String menuOpenDirection = DEFAULT_DIRECTION;
    private Paint paintFAB, paintFabBG, paintFabBGWidShadow, paintFabBGWithoutShadow, paintFabPlus, paintIcons;

    private Bitmap bitmapM1 = null, bitmapM2 = null, bitmapM3 = null;

    private int xTransTL = 0, yTransTL = 0;
    private int xTransTR = 0, yTransTR = 0;
    private int xTransBL = 0, yTransBL = 0;
    private int xTransBR = 0, yTransBR = 0;
    private float factorScaleS = 1f, factorScaleP = 0;
    private int plusRotationDegree = 0;
    private int alphaAmount = 0;
    private ValueAnimator scaleFAB, rotatePlus, alphaIcon;

    private boolean isOpened = false;
    private OnMenuClickListener onMenuClickListener = null;

    public SquareMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(attrs);
        setupPaint();
    }

    private void setupAttributes(AttributeSet attrs) {
        // Obtain a typed array of attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SquareMenu, 0, 0);
        // Extract custom attributes into member variables
        Drawable iconM1, iconM2, iconM3;
        try {
            autoClose = a.getBoolean(R.styleable.SquareMenu_autoClose, false);
            fabColor = a.getColor(R.styleable.SquareMenu_fabColor, FAB_COLOR);
            fabWidth = fabHeight = a.getInteger(R.styleable.SquareMenu_squareFabSize, FAB_SIZE);
            menuOpenDirection = a.getString(R.styleable.SquareMenu_menuOpenDirection);
            if(menuOpenDirection == null) menuOpenDirection =  DEFAULT_DIRECTION;
            iconM1 = a.getDrawable(R.styleable.SquareMenu_iconM1);
            iconM2 = a.getDrawable(R.styleable.SquareMenu_iconM2);
            iconM3 = a.getDrawable(R.styleable.SquareMenu_iconM3);
        } finally {
            // TypedArray objects are shared and must be recycled.
            a.recycle();
        }

        if(iconM1 != null) bitmapM1 = ((BitmapDrawable) iconM1).getBitmap();
        if(iconM2 != null) bitmapM2 = ((BitmapDrawable) iconM2).getBitmap();
        if(iconM3 != null) bitmapM3 = ((BitmapDrawable) iconM3).getBitmap();
    }

    private void setupPaint() {
        paintIcons = new Paint();
        paintIcons.setAlpha(0);

        paintFAB = new Paint();
        paintFAB.setStyle(Paint.Style.FILL);
        paintFAB.setColor(fabColor);
        paintFAB.setShadowLayer(0, 0, 0, Color.BLACK);
        // Important for certain APIs
        setLayerType(LAYER_TYPE_SOFTWARE, paintFAB);

        paintFabBGWidShadow = new Paint();
        paintFabBGWidShadow.setStyle(Paint.Style.FILL);
        paintFabBGWidShadow.setColor(Color.GREEN);
        paintFabBGWidShadow.setShadowLayer(5, 1, 1, Color.BLACK);
        setLayerType(LAYER_TYPE_SOFTWARE, paintFabBGWidShadow);

        paintFabBGWithoutShadow = new Paint();
        paintFabBGWithoutShadow.setStyle(Paint.Style.FILL);
        paintFabBGWithoutShadow.setColor(Color.TRANSPARENT);

        paintFabBG = paintFabBGWidShadow;

        paintFabPlus = new Paint();
        paintFabPlus.setColor(Color.WHITE);
        paintFabPlus.setAntiAlias(true);
        paintFabPlus.setStrokeWidth(FAB_ICON_WIDTH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Defines the extra padding for the shape name text

        // Resolve the width based on our minimum and the measure spec
        int minw = 2*fabWidth + getPaddingLeft() + getPaddingRight() + FAB_MARGIN ;
        int w = resolveSizeAndState(minw, widthMeasureSpec, 0);

        // Ask for a height that would let the view get as big as it can
        int minh = 2*fabHeight + getPaddingBottom() + getPaddingTop() +FAB_MARGIN ;

        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);
        // Calling this method determines the measured width and height
        // Retrieve with getMeasuredWidth or getMeasuredHeight methods later
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

       // Draw Squares
        if(menuOpenDirection.equals(TOP_LEFT))
            drawSquares4TopLeftMenu(canvas);
        else
            drawSquares4TopRightMenu(canvas);
    }

    private void drawSquares4TopRightMenu(Canvas canvas){

        // Draw BG Rect
        canvas.drawRect(0, fabHeight, fabWidth, getMeasuredHeight()-FAB_MARGIN, paintFabBG);

        canvas.save();
        canvas.translate(xTransTR, yTransTR); //-(fabWidth/6-factorScaleP)
        canvas.scale(factorScaleS, factorScaleS, fabWidth/2 , fabHeight+factorScaleP);
        // Top Right Rect
        canvas.drawRect(fabWidth, fabHeight, fabWidth/2, fabHeight*1.5f, paintFAB);
        canvas.restore();

        canvas.save();
        canvas.translate(xTransTL, yTransTL);
        canvas.scale(factorScaleS, factorScaleS, 0, fabHeight+factorScaleP);
        // Top Left Rect
        canvas.drawRect(0, fabHeight, fabWidth/2, fabHeight*1.5f, paintFAB);
        canvas.restore();

        canvas.save();
        canvas.translate(xTransBR, yTransBR);
        canvas.scale(factorScaleS, factorScaleS, fabWidth/2, fabHeight*1.5f+factorScaleP);
        // Bottom Right Rect
        canvas.drawRect(fabWidth/2, fabHeight*1.5f, fabWidth, getMeasuredHeight()-FAB_MARGIN, paintFAB);
        canvas.restore();

        canvas.save();
        canvas.scale(factorScaleS, factorScaleS, 0, fabHeight*1.5f+factorScaleP);
        // Bottom Right Rect
        canvas.drawRect(0, fabHeight*1.5f, fabWidth/2, getMeasuredHeight()-FAB_MARGIN, paintFAB);
        canvas.restore();

        // Draw PLUS
        drawPlusIcon(canvas,  FAB_ICON_PADDING, fabHeight*1.5f,  fabWidth-(2*FAB_ICON_PADDING), plusRotationDegree);

        // Draw Menus icon
        if( isOpened ) drawTopRightMenuIcons(canvas);
    }

    private void drawSquares4TopLeftMenu(Canvas canvas){

        // Draw BG Rect
        canvas.drawRect(fabWidth, fabHeight, getMeasuredWidth()-FAB_MARGIN, getMeasuredHeight()-FAB_MARGIN, paintFabBG);

        canvas.save();
        canvas.translate(xTransTL, yTransTL);
        canvas.scale(factorScaleS, factorScaleS, fabWidth+factorScaleP, fabHeight+factorScaleP);
        // Top Left Rect
        canvas.drawRect(fabWidth, fabHeight, fabWidth*1.5f, fabHeight*1.5f, paintFAB);
        canvas.restore();

        canvas.save();
        canvas.translate(xTransTR, yTransTR);
        canvas.scale(factorScaleS, factorScaleS, fabWidth*1.5f+factorScaleP, fabHeight+factorScaleP);
        // Top Right Rect
        canvas.drawRect(fabWidth*1.5f, fabHeight, getMeasuredWidth()-FAB_MARGIN, fabHeight*1.5f, paintFAB);
        canvas.restore();

        canvas.save();
        canvas.translate(xTransBL, yTransBL);
        canvas.scale(factorScaleS, factorScaleS, fabWidth+factorScaleP, fabHeight*1.5f+factorScaleP);
        // Bottom Left Rect
        canvas.drawRect(fabWidth, fabHeight*1.5f, fabWidth*1.5f, getMeasuredHeight()-FAB_MARGIN, paintFAB);
        canvas.restore();

        canvas.save();
        canvas.scale(factorScaleS, factorScaleS, fabWidth*1.5f+factorScaleP, fabHeight*1.5f+factorScaleP);
        // Bottom Right Rect
        canvas.drawRect(fabWidth*1.5f, fabHeight*1.5f, getMeasuredWidth()-FAB_MARGIN, getMeasuredHeight()-FAB_MARGIN, paintFAB);
        canvas.restore();

        // Draw PLUS
        drawPlusIcon(canvas, fabWidth+FAB_ICON_PADDING, fabHeight*1.5f, fabWidth-(2*FAB_ICON_PADDING), plusRotationDegree);

        // Draw Menus icon
        if( isOpened ) drawTopLeftMenuIcons(canvas);
    }

    private void drawPlusIcon(Canvas canvas, float startX, float startY, int size, int rotateDegree) {
        canvas.save();
        canvas.rotate(rotateDegree, startX + size / 2, startY);
        canvas.drawLine(startX, startY, startX + size, startY, paintFabPlus);
        canvas.rotate(90, startX + size / 2, startY);
        canvas.drawLine(startX, startY, startX + size, startY, paintFabPlus);
        canvas.restore();
    }

    private void drawTopLeftMenuIcons(Canvas canvas){
        // imageStartX = (rectStartX + (rectWidth/2)) - (imageWidth/2);
        // imageStartY = (rectStartY + (rectHeight/2)) - (imageHeight/2);
        int imageStartX,imageStartY;
        if(bitmapM1 !=null) {
            imageStartX = (0 + (fabWidth/2)) - (bitmapM1.getWidth()/2);
            imageStartY = (fabHeight + (fabWidth/2)) - (bitmapM1.getHeight()/2);
            canvas.drawBitmap(bitmapM1, imageStartX , imageStartY , paintIcons);
        }

        if(bitmapM2 !=null) {
            imageStartX = (0 + (fabWidth/2)) - (bitmapM2.getWidth()/2);
            imageStartY = (0 + (fabHeight/2)) - (bitmapM2.getHeight()/2);
            canvas.drawBitmap(bitmapM2, imageStartX , imageStartY, paintIcons);
        }

        if(bitmapM3 !=null) {
            imageStartX = (fabWidth + (fabWidth/2)) - (bitmapM2.getWidth()/2);
            imageStartY = (0 + (fabHeight/2)) - (bitmapM2.getHeight()/2);
            canvas.drawBitmap(bitmapM3, imageStartX , imageStartY, paintIcons);
        }
    }

    private void drawTopRightMenuIcons(Canvas canvas){
        // imageStartX = (rectStartX + (rectWidth/2)) - (imageWidth/2);
        // imageStartY = (rectStartY + (rectHeight/2)) - (imageHeight/2);
        int imageStartX,imageStartY;
        if(bitmapM1 !=null) {
            imageStartX = (fabWidth + (fabWidth/2)) - (bitmapM1.getWidth()/2);
            imageStartY = (fabHeight + (fabWidth/2)) - (bitmapM1.getHeight()/2);
            canvas.drawBitmap(bitmapM1, imageStartX , imageStartY , paintIcons);
        }

        if(bitmapM2 !=null) {
            imageStartX = (fabWidth + (fabWidth/2)) - (bitmapM2.getWidth()/2);
            imageStartY = (0 + (fabHeight/2)) - (bitmapM2.getHeight()/2);
            canvas.drawBitmap(bitmapM2, imageStartX , imageStartY, paintIcons);
        }

        if(bitmapM3 !=null) {
            imageStartX = (0 + (fabWidth/2)) - (bitmapM2.getWidth()/2);
            imageStartY = (0 + (fabHeight/2)) - (bitmapM2.getHeight()/2);
            canvas.drawBitmap(bitmapM3, imageStartX , imageStartY, paintIcons);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if(menuOpenDirection.equals(TOP_LEFT))
                 touch4TopLeftMenu(event);
            else
                touch4TopRightMenu(event);

            return true;
        }
        return result;
    }

    private void touch4TopLeftMenu(MotionEvent event) {
        if(!isOpened) {
            if(event.getX() > fabWidth && event.getY() > fabHeight) {
                isOpened = true;
                startAnimationTopLeftMenu();
                if(onMenuClickListener!=null)onMenuClickListener.onMenuOpen();
            }
        }
        else {
            // Touch of BOTTOM_RIGHT
            if(event.getX() > fabWidth && event.getY() > fabHeight) {
                closeTopLeftM();
            }
            // Touch of TOP-LEFT
            else if(event.getX() < fabWidth && event.getY() < fabHeight) {
                if(onMenuClickListener!=null)onMenuClickListener.onClickMenu2();
                if(autoClose)closeTopLeftM();
            }
            // Touch of TOP-RIGHT
            else if(event.getX() > fabWidth && event.getY() < fabHeight) {
                if(onMenuClickListener!=null)onMenuClickListener.onClickMenu3();
                if(autoClose)closeTopLeftM();
            }
            // Touch of BOTTOM-LEFT
            else if(event.getX() < fabWidth && event.getY() > fabHeight) {
                if(onMenuClickListener!=null)onMenuClickListener.onClickMenu1();
                if(autoClose)closeTopLeftM();
            }
        }
    }

    private void closeTopLeftM(){
        isOpened = false;
        resetAnimationTopLeftMenu();
        if(onMenuClickListener!=null)onMenuClickListener.onMenuClose();
    }

    private void touch4TopRightMenu(MotionEvent event) {
        Log.i(TAG, "touch4TopRightMenu");
        if(!isOpened) {
            if(event.getX() < fabWidth && event.getY() > fabHeight) {
                isOpened = true;
                startAnimationTopRightMenu();
                if(onMenuClickListener!=null)onMenuClickListener.onMenuOpen();
            }
        }
        else {
            // Touch of BOTTOM_LEFT
            if(event.getX() < fabWidth && event.getY() > fabHeight) {
                closeTopRightM();
            }
            // Touch of TOP-RIGHT
            else if(event.getX() > fabWidth && event.getY() < fabHeight) {
                if(onMenuClickListener!=null)onMenuClickListener.onClickMenu2();
                if(autoClose) closeTopRightM();
            }
            // Touch of TOP-LEFT
            else if(event.getX() < fabWidth && event.getY() < fabHeight) {
                if(onMenuClickListener!=null)onMenuClickListener.onClickMenu3();
                if(autoClose) closeTopRightM();
            }
            // Touch of BOTTOM-RIGHT
            else if(event.getX() > fabWidth && event.getY() > fabHeight) {
                if(onMenuClickListener!=null)onMenuClickListener.onClickMenu1();
                if(autoClose) closeTopRightM();
            }
        }
    }

    private void closeTopRightM(){
        isOpened = false;
        resetAnimationTopRightMenu();
        if(onMenuClickListener!=null)onMenuClickListener.onMenuClose();
    }

    private void startAnimationTopRightMenu() {
        // Setup Background and Foreground Shadow
        paintFabBG = paintFabBGWithoutShadow;
        paintFAB.setShadowLayer(2,0,0,Color.BLACK);

        AnimatorSet fabsAnimSet = new AnimatorSet();
        ValueAnimator moveTR = ValueAnimator.ofInt(0, (fabWidth/2)+MARGIN_BW_SQUARES);
        moveTR.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                xTransTR = (int)animation.getAnimatedValue();
                // To convert xTransTR value in negative
                yTransTR = xTransTR - 2*xTransTR;
                invalidate();
            }
        });

        ValueAnimator moveTL = ValueAnimator.ofInt(0, -(fabWidth/2)-MARGIN_BW_SQUARES);
        moveTL.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                yTransTL = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator moveBR = ValueAnimator.ofInt(0, (fabWidth/2)+MARGIN_BW_SQUARES);
        moveBR.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xTransBR = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        scaleFAB = ValueAnimator.ofFloat(1f, 1.9f);
        scaleFAB.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                factorScaleS = (float)animation.getAnimatedValue();
                factorScaleP = fabWidth/2;
                invalidate();
            }
        });

        rotatePlus = ValueAnimator.ofInt(0, 45);
        rotatePlus.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                plusRotationDegree = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        alphaIcon = ValueAnimator.ofInt(0, 255);
        alphaIcon.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alphaAmount = (int)animation.getAnimatedValue();
                paintIcons.setAlpha(alphaAmount);
                invalidate();
            }
        });
        alphaIcon.setDuration(1000);
        alphaIcon.start();

        fabsAnimSet.playTogether(moveTR, moveTL, moveBR,  scaleFAB, rotatePlus);
        fabsAnimSet.setDuration(ANIM_SPEED);
        fabsAnimSet.setInterpolator(new LinearInterpolator());
        fabsAnimSet.start();


    }

    private void resetAnimationTopRightMenu(){
        AnimatorSet fabsAnimSet = new AnimatorSet();
        ValueAnimator moveTR = ValueAnimator.ofInt((fabWidth/2)+MARGIN_BW_SQUARES, 0);
        moveTR.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                xTransTR = (int)animation.getAnimatedValue();
                // To convert xTransTR value in negative
                yTransTR = xTransTR - 2*xTransTR;
                invalidate();
            }
        });

        ValueAnimator moveTL = ValueAnimator.ofInt(-(fabWidth/2)-MARGIN_BW_SQUARES, 0 );
        moveTL.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                yTransTL = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator moveBR = ValueAnimator.ofInt((fabWidth/2)+MARGIN_BW_SQUARES, 0);
        moveBR.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xTransBR = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        scaleFAB.reverse();
        rotatePlus.reverse();
        alphaIcon.reverse();

        fabsAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Setup Background and Foreground Shadow
                paintFabBG = paintFabBGWidShadow;
                paintFAB.setShadowLayer(0,0,0,Color.TRANSPARENT);
            }
        });
        fabsAnimSet.playTogether(moveTL, moveTR, moveBR);
        fabsAnimSet.setDuration(ANIM_SPEED);
        fabsAnimSet.setInterpolator(new LinearInterpolator());
        fabsAnimSet.start();
    }

    private void startAnimationTopLeftMenu() {
        // Setup Background and Foreground Shadow
        paintFabBG = paintFabBGWithoutShadow;
        paintFAB.setShadowLayer(2,0,0,Color.BLACK);

        AnimatorSet fabsAnimSet = new AnimatorSet();
        ValueAnimator moveTL = ValueAnimator.ofInt(0, -(fabWidth/2)-MARGIN_BW_SQUARES);
        moveTL.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                xTransTL = yTransTL = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator moveTR = ValueAnimator.ofInt(0, -(fabWidth/2)-MARGIN_BW_SQUARES);
        moveTR.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                yTransTR = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator moveBL = ValueAnimator.ofInt(0, -(fabWidth/2)-MARGIN_BW_SQUARES);
        moveBL.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xTransBL = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        scaleFAB = ValueAnimator.ofFloat(1f, 1.9f);
        scaleFAB.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                factorScaleS = (float)animation.getAnimatedValue();
                factorScaleP = fabWidth/2;
                invalidate();
            }
        });

        rotatePlus = ValueAnimator.ofInt(0, 45);
        rotatePlus.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                plusRotationDegree = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        alphaIcon = ValueAnimator.ofInt(0, 255);
        alphaIcon.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alphaAmount = (int)animation.getAnimatedValue();
                paintIcons.setAlpha(alphaAmount);
                invalidate();
            }
        });
        alphaIcon.setDuration(1000);
        alphaIcon.start();

        fabsAnimSet.playTogether(moveTL, moveTR, moveBL, scaleFAB, rotatePlus);
        fabsAnimSet.setDuration(ANIM_SPEED);
        fabsAnimSet.start();
    }

    private void resetAnimationTopLeftMenu(){
        AnimatorSet fabsAnimSet = new AnimatorSet();
        ValueAnimator moveTL = ValueAnimator.ofInt(-(fabWidth/2)-MARGIN_BW_SQUARES, 0);
        moveTL.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xTransTL = yTransTL = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator moveTR = ValueAnimator.ofInt(-(fabWidth/2)-MARGIN_BW_SQUARES, 0);
        moveTR.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                yTransTR = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator moveBL = ValueAnimator.ofInt(-(fabWidth/2)-MARGIN_BW_SQUARES, 0);
        moveBL.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xTransBL = (int)animation.getAnimatedValue();
                invalidate();
            }
        });

        scaleFAB.reverse();
        rotatePlus.reverse();
        alphaIcon.reverse();

        fabsAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Setup Background and Foreground Shadow
                paintFabBG = paintFabBGWidShadow;
                paintFAB.setShadowLayer(0,0,0,Color.TRANSPARENT);
            }
        });
        fabsAnimSet.playTogether(moveTL, moveTR, moveBL);
        fabsAnimSet.setDuration(ANIM_SPEED);
        fabsAnimSet.start();
    }

    /**
     * @return
     */
    public String getMenuOpenDirection() {
        return menuOpenDirection;
    }

    /**
     * @param menuOpenDirection  "top_left" OR "top_right",  Default direction will be "top_left"
     */
    public void setMenuOpenDirection(String menuOpenDirection) {
        this.menuOpenDirection = menuOpenDirection;
    }

    /**
     * @return
     */
    public int getFABColor() {
        return fabColor;
    }

    /**
     * @param color
     */
    public void setFABColor(int color) {
        this.fabColor = color;
        invalidate();
        requestLayout();
    }

    /**
     * @param autoClose true or false : default is false
     */
    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    /**
     * Register callbacks to be invoked when this Menu is clicked.
     *
     * @param onMenuClickListener The callback that will run
     */
    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }
}