package ca.yorku.cse.mack.fittsdraganddrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Random;

/**
 * ExperimentPanel -- panel to present and sequence the targets
 * <p>
 *
 * @author Scott MacKenzie
 */
public class ExperimentPanel extends View
{
    final int START_TEXT_SIZE = 20; // may need to fiddle with this, depending on device
    final int START_CICLE_DIAMETER = 53; // x pixelDensity = one-third inch
    final int GAP_BETWEEN_LINES = 6;
    final int DRAG_OBJECT_WIDTH = 40; // dp (one quarter inch)
    final int TOUCH_POINT_DIAMETER = 60; // dp

    Target[] targetSet;
    Target toTarget; // the target to select
    Target fromTarget; // the source target from where the trial began
    Target startCircle;

    float xTouchPoint, yTouchPoint;
    Paint touchPointPaint;
    boolean fingerDown;
    boolean showBanner;

    float xDragObject, yDragObject;
    Paint dragObjectPaint;
    Bitmap apple, graphicTrial, graphicTrial2;
    Bitmap appleAndPig;
    Bitmap heatMap; // for finger contact point
    // edit the code below if new graphic images are added (make sure the two arrays are aligned)
    int[] graphicTargetId = {R.drawable.pig, R.drawable.chicken, R.drawable.cow, R.drawable.elephant, R.drawable
            .giraffe, R.drawable.horse, R.drawable.llama, R.drawable.lion, R.drawable.penguin, R.drawable.rhino, R
            .drawable.rooster, R.drawable.sheep, R.drawable.tiger, R.drawable.turkey, R.drawable.walrus};
    int[] graphicTarget2Id = {R.drawable.pig2, R.drawable.chicken2, R.drawable.cow2, R.drawable.elephant2, R.drawable
            .giraffe2, R.drawable.horse2, R.drawable.llama2, R.drawable.lion2, R.drawable.penguin2, R.drawable
            .rhino2, R.drawable.rooster2, R.drawable.sheep2, R.drawable.tiger2, R.drawable.turkey2, R.drawable.walrus2};

    Bitmap[] graphicTarget, graphicTarget2;
    Random random;

    float touchPointRadius;
    float defaultDragObjectWidth;
    RectF dragObjectRectangle; // for the bitmap
    RectF heatMapRectangle; // for the touch point
    RectF r; // for the 1D FittsFarm target (so it doesn't get stretched vertically)
    RectF bannerRectangle;

    // boolean variable for the "style" mode parameter in setup dialog
    boolean fittsFarmStyle = true;
    boolean showAllTargets = true;

    float panelWidth;
    float panelHeight;

    float d; // diameter of start circle (also used for positioning circle and text)
    float textSize;
    float gap;

    boolean waitStartCircleSelect, done;
    String mode;
    Paint targetPaint, targetRimPaint, normalPaint, startPaint;
    String[] resultsString = {"Tap to begin"};

    public ExperimentPanel(Context contextArg)
    {
        super(contextArg);
        initialize(contextArg);
    }

    public ExperimentPanel(Context contextArg, AttributeSet attrs)
    {
        super(contextArg, attrs);
        initialize(contextArg);
    }

    public ExperimentPanel(Context contextArg, AttributeSet attrs, int defStyle)
    {
        super(contextArg, attrs, defStyle);
        initialize(contextArg);
    }

    // things that can be initialized from within this View
    private void initialize(Context c)
    {
        // this is the dragged object when in FittsFarm mode
        apple = BitmapFactory.decodeResource(this.getResources(), R.drawable.apple);

        // banner graphic for initial screen (FittsFarm style)
        appleAndPig = BitmapFactory.decodeResource(this.getResources(), R.drawable.appleandpig);

        // the targets when in FittsFarm mode (the "2" target is a bit darker)
        graphicTarget = new Bitmap[graphicTargetId.length];
        graphicTarget2 = new Bitmap[graphicTarget2Id.length];
        for (int i = 0; i < graphicTargetId.length; ++i)
        {
            graphicTarget[i] = BitmapFactory.decodeResource(this.getResources(), graphicTargetId[i]);
            graphicTarget2[i] = BitmapFactory.decodeResource(this.getResources(), graphicTarget2Id[i]);
        }

        random = new Random(); // to generate the target animals

        r = new RectF(); // for 1D tasks when drawing the graphic target (e.g., a pig)

        this.setBackgroundColor(Color.LTGRAY);

        DisplayMetrics dm = c.getResources().getDisplayMetrics();
        d = START_CICLE_DIAMETER * dm.density;
        startCircle = new Target(Target.CIRCLE, d, d, d, d, Target.NORMAL);
        textSize = START_TEXT_SIZE * dm.density;
        gap = GAP_BETWEEN_LINES * dm.density;
        defaultDragObjectWidth = DRAG_OBJECT_WIDTH * dm.density;
        touchPointRadius = TOUCH_POINT_DIAMETER * dm.density;

        // gradient heat map for finger touch point
        heatMap = getGradientEdgedCircle(TOUCH_POINT_DIAMETER, TOUCH_POINT_DIAMETER, TOUCH_POINT_DIAMETER / 2,
                Color.MAGENTA);
        heatMapRectangle = new RectF();

        float radius = 0.8f * Math.min(dm.widthPixels, dm.heightPixels) / 2.0f;
        float centreX = dm.widthPixels / 2.0f;
        float centreY = dm.heightPixels / 2.0f;
        bannerRectangle = new RectF(centreX - radius, centreY - radius, centreX + radius, centreY + radius);

        //dragObjectRectangle = new RectF(0, 0, defaultDragObjectWidth, defaultDragObjectWidth);

        touchPointPaint = new Paint();
        touchPointPaint.setColor(Color.MAGENTA);
        touchPointPaint.setStyle(Paint.Style.FILL);
        touchPointPaint.setAntiAlias(true);
        touchPointPaint.setAlpha(100); // semi-transparent

        dragObjectPaint = new Paint();
        dragObjectPaint.setColor(Color.BLACK);
        dragObjectPaint.setStyle(Paint.Style.FILL);
        dragObjectPaint.setAntiAlias(true);

        targetPaint = new Paint();
        targetPaint.setColor(0xffffaaaa);
        targetPaint.setStyle(Paint.Style.FILL);
        targetPaint.setAntiAlias(true);

        targetRimPaint = new Paint();
        targetRimPaint.setColor(Color.RED);
        targetRimPaint.setStyle(Paint.Style.STROKE);
        targetRimPaint.setStrokeWidth(2);
        targetRimPaint.setAntiAlias(true);

        normalPaint = new Paint();
        normalPaint.setColor(0xffff9999); // lighter red (to minimize distraction)
        normalPaint.setStyle(Paint.Style.STROKE);
        normalPaint.setStrokeWidth(2);
        normalPaint.setAntiAlias(true);

        startPaint = new Paint();
        startPaint.setColor(0xff0000ff);
        startPaint.setStyle(Paint.Style.FILL);
        startPaint.setAntiAlias(true);
        startPaint.setTextSize(textSize);
    }

    /**
     * This method is called from the main activity to pass in the width of the smallest target in a block.  The drag
     * object (circle or apple) will be 1/4 inch wide, unless the smallest target is smaller than that.  In this
     * case, the drag object will assume the width of the smallest target.
     */
    void setDragObjectWidth(int widthArg)
    {
        // smaller of 1/4 inch or the width of the smallest target in a block
        float width = Math.min(widthArg, defaultDragObjectWidth);

        // for the apple (when using Fitts Farm style)
        dragObjectRectangle = new RectF(0, 0, width, width);

        // for the drag circle
        defaultDragObjectWidth = width;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (waitStartCircleSelect) // draw start circle and prompt/results string
        {
            canvas.drawCircle(startCircle.xCenter, startCircle.yCenter, startCircle.width / 2f,
                    startPaint);
            for (int i = 0; i < resultsString.length; ++i)
                canvas.drawText(resultsString[i], d / 2, d / 2 + 2 * startCircle.width / 2f + (i + 1)
                        * (textSize + gap), startPaint);

            if (fittsFarmStyle && showBanner)
            {
                canvas.drawBitmap(appleAndPig, null, bannerRectangle, null);
            }

        } else if (!done) // draw task targets
        {
            if (showAllTargets)
            {
                for (Target value : targetSet)
                {
                    if (mode.equals("1D"))
                        canvas.drawRect(value.r, normalPaint);
                    else // 2D
                        canvas.drawOval(value.r, normalPaint);
                }
            }

            // draw target to select last (so it is on top of any overlapping targets)
            if (mode.equals("1D"))
            {
                canvas.drawRect(toTarget.r, targetPaint);
                canvas.drawRect(toTarget.r, targetRimPaint);

                if (fittsFarmStyle) // draw graphic on top of target
                {
                    r.left = toTarget.r.left;
                    r.top = toTarget.r.centerY() - toTarget.r.width() / 2;
                    r.right = toTarget.r.right;
                    r.bottom = toTarget.yCenter + toTarget.r.width() / 2;

                    if (graphicTrial != null)
                        if (r.contains(xDragObject, yDragObject) && graphicTrial != null && graphicTrial2 != null)
                            canvas.drawBitmap(graphicTrial2, null, r, null); // touch point "on" target
                        else
                            canvas.drawBitmap(graphicTrial, null, r, null); // touch point "off" target
                }

            } else // 2D
            {
                canvas.drawOval(toTarget.r, targetPaint);
                canvas.drawOval(toTarget.r, targetRimPaint);

                if (fittsFarmStyle) // draw graphic on top of target
                {
                    if (graphicTrial != null)
                        if (toTarget.r.contains(xDragObject, yDragObject))
                            canvas.drawBitmap(graphicTrial2, null, toTarget.r, null); // drag object "on" target
                        else
                            canvas.drawBitmap(graphicTrial, null, toTarget.r, null); // drag object "off" target
                }
            }

            // draw the drag object (for FittsFarm, this is the apple to feed the animals)
            if (!fittsFarmStyle)
                canvas.drawCircle(xDragObject, yDragObject, defaultDragObjectWidth / 2, dragObjectPaint);
            else
            {
                dragObjectRectangle.set(xDragObject - defaultDragObjectWidth / 2, yDragObject -
                                defaultDragObjectWidth / 2,
                        xDragObject + defaultDragObjectWidth / 2, yDragObject + defaultDragObjectWidth / 2);
                canvas.drawBitmap(apple, null, dragObjectRectangle, null);
            }


            // finally, draw touch point "heat map"
            if (fingerDown)
            {
                heatMapRectangle.left = xTouchPoint - touchPointRadius;
                heatMapRectangle.top = yTouchPoint - touchPointRadius;
                heatMapRectangle.right = xTouchPoint + touchPointRadius;
                heatMapRectangle.bottom = yTouchPoint + touchPointRadius;
                canvas.drawBitmap(heatMap, null, heatMapRectangle, null);
            }
        }
        invalidate(); // will cause onDraw to run again immediately
    }

    void resetDragObject()
    {
        xDragObject = fromTarget.xCenter;
        yDragObject = fromTarget.yCenter;
        invalidate();
    }

    void nextRandomGraphic()
    {
        int i = random.nextInt(graphicTarget.length);
        graphicTrial = graphicTarget[i];
        graphicTrial2 = graphicTarget2[i];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension((int)panelWidth, (int)panelHeight);
    }

    // return a circular bitmap with a gradient edge
    public Bitmap getGradientEdgedCircle(int width, int height, int radius, int color)
    {
        Paint p = new Paint();

        // create the bitmap to draw into
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int[] colors = {color & 0x88ffffff, color & 0x00ffffff};
        RectF r = new RectF(0, 0, width, height);
        RadialGradient circle = new RadialGradient(width / 2, height / 2, radius, colors, null, Shader.TileMode.CLAMP);
        p.setShader(circle);
        canvas.drawRect(r, p);

        return bitmap;
    }
}