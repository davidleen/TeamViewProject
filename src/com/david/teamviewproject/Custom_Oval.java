package com.david.teamviewproject;

import android.graphics.Point;
import android.graphics.drawable.shapes.OvalShape;

public class Custom_Oval extends OvalShape {

	public static float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;
	public static float TO_DEGREES = (1 / (float) Math.PI) * 180;

	/**
	 * fetch oval locatiion on degree
	 * 
	 * @param radias
	 * @return
	 */
	public void getLocationOnDegree(float degree, Point point) {
		float radias = degree * TO_RADIANS;
		getLocationOnRadias(radias, point);
	}

	/**
	 * fetch oval locatiion on radias
	 * 
	 * @param radias
	 * @return
	 */
	public void getLocationOnRadias(float radias, Point loactation) {

		loactation.x = (int) (getWidth() / 2 * Math.cos(radias));
		loactation.y = (int) (getHeight() / 2 * Math.sin(radias));

	}
}
