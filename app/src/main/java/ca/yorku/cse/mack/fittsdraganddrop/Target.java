package ca.yorku.cse.mack.fittsdraganddrop;

import android.graphics.PointF;
import android.graphics.RectF;

@SuppressWarnings("unused")
public class Target
{
	final static int NORMAL = 1;
	final static int TARGET = 2;
	final static int ALREADY_SELECTED = 3;
	final static int OTHER = 4;

	final static int RECTANGLE = 0;
	final static int CIRCLE = 1;

	public float xCenter, yCenter, width, height;
	RectF r;
	int status;
	int type;

	Target(int typeArg, float xCenterArg, float yCenterArg, float widthArg, float heightArg, int statusArg)
	{
		type = typeArg;
		r = new RectF(xCenterArg - widthArg / 2f, yCenterArg - heightArg / 2f, xCenterArg + widthArg / 2f, yCenterArg
				+ heightArg / 2f);
		xCenter = xCenterArg;
		yCenter = yCenterArg;
		width = widthArg;
		height = heightArg;
		status = statusArg;
	}

	/**
	 * Returns true if the specified coordinate is inside the target.
	 */
	public boolean inTarget(float xTest, float yTest)
	{
		//Log.i("MYDEBUG", "type=" + type);
		if (type == CIRCLE)
			return distanceFromTargetCenter(xTest, yTest) <= (width / 2f);
		else
			return r.contains(xTest, yTest);
	}

	public float distanceFromTargetCenter(float xTest, float yTest)
	{
		return (float) Math.sqrt((xCenter - xTest) * (xCenter - xTest) + (yCenter - yTest) * (yCenter - yTest));
	}

	public PointF targetCenter()
	{
		return new PointF(xCenter, yCenter);
	}
}