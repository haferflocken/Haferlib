//A polygon that can be changed at runtime without creating entirely new polygons.
//This class exists to reduce overhead caused by Slick2D's insistence on creating new shapes all the time.

package org.haferlib.slick.geom;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

public class Polygon extends Shape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Polygon(float[] points) {
		this.points = new float[points.length];
		setPoints(points);
	}

	public Polygon(Polygon other) {
		this(other.points);
	}

	public void setPoints(float[] newPoints) {
		//Copy the points into the shape
		int length = newPoints.length;
		if (length % 2 != 0)
			length--;
		for (int i = 0; i < length; i++) {
			this.points[i] = newPoints[i];
		}

		pointsChanged();
	}

	public float[] getPoints() {
		return points;
	}

	public void pointsChanged() {
		updateBounds();
		findCenter();
        calculateRadius();
        pointsDirty = true;
	}

	//taken from Slick2D org.newdawn.slick.geom.Polygon
	private void updateBounds() {
		maxX = -Float.MIN_VALUE;
		maxY = -Float.MIN_VALUE;
		minX = Float.MAX_VALUE;
		minY = Float.MAX_VALUE;
		x = Float.MAX_VALUE;
		y = Float.MAX_VALUE;

		for(int i=0;i<points.length;i++) {
			if(i % 2 == 0) {
				if(points[i] > maxX) {
					maxX = points[i];
				}
				if(points[i] < minX) {
					minX = points[i];
				}
				if(points[i] < x) {
					x = points[i];
				}
			}
			else {
				if(points[i] > maxY) {
					maxY = points[i];
				}
				if(points[i] < minY) {
					minY = points[i];
				}
				if(points[i] < y) {
					y = points[i];
				}
			}
		}
	}

	public void createPoints() {
	}

	public Shape transform(Transform transform) {
		return new org.newdawn.slick.geom.Polygon(points).transform(transform);
	}

	public String toString() {
		StringBuilder out = new StringBuilder();
		for (int x = 0, y = 1; y < points.length; x += 2, y += 2){
			out.append('(');
			out.append(points[x]);
			out.append(',');
			out.append(' ');
			out.append(points[y]);
			out.append(')');
		}
		return out.toString();
	}
}