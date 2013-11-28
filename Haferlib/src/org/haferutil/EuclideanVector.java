//An object that stores a magnitude and direction in several ways
//When quantities are changed any relevant quanitites are updated

package org.haferutil;

public class EuclideanVector {

	private double angle; //Angle of the vector, in radians
	private double magnitude; //Magnitude of the vector
	private int xComponent; //X Component of the vector
	private int yComponent; //Y Component of the vector
	private double tinyXComponent; //The X Component divided by the magnitude
	private double tinyYComponent; //The Y Component divided by the magnitude

    @SuppressWarnings("unused")
	private EuclideanVector() {
    }

    public EuclideanVector(double a, double m) {
    	angle = a;
    	magnitude = m;
    	xComponent = getXComponent(angle, magnitude);
    	yComponent = getYComponent(angle, magnitude);
    	tinyXComponent = getTinyXComponent(angle);
    	tinyYComponent = getTinyYComponent(angle);

    	System.out.println("-New Vector-\nComponents: " + xComponent + ", " + yComponent + "\nAngle: " + Math.toDegrees(angle) + "\nMagnitude: " + magnitude + "\n");
    }

    public EuclideanVector(int xComp, int yComp) {
    	xComponent = xComp;
    	yComponent = yComp;
    	angle = getAngle(xComponent, yComponent);
    	magnitude = getMagnitude(xComponent, yComponent);
    	tinyXComponent = getTinyXComponent(angle);
    	tinyYComponent = getTinyYComponent(angle);

    	System.out.println("-New Vector-\nComponents: " + xComponent + ", " + yComponent + "\nAngle: " + Math.toDegrees(angle) + "\nMagnitude: " + magnitude + "\n");
    }

    public static double getTinyXComponent(double a) {
    	return Math.cos(a);
    }

    public static double getTinyYComponent(double a) {
    	return Math.sin(a);
    }

    public static int getXComponent(double a, double m) {
    	return getXComponentFromTiny(getTinyXComponent(a), m);
    }

    public static int getYComponent(double a, double m) {
    	return getYComponentFromTiny(getTinyYComponent(a), m);
    }

    public static int getXComponentFromTiny(double tinyX, double m) {
    	return (int)Math.rint(tinyX*m);
    }

    public static int getYComponentFromTiny(double tinyY, double m) {
    	return (int)Math.rint(tinyY*m);
    }

    public static double getAngle(int xComp, int yComp) {
    	//X != 0, Y == ?, return the tangent
    	if (xComp != 0) {
    		return Math.atan2(yComp, xComp);
    	}
    	//X == 0, Y > 0, return 90 degrees
    	if (yComp > 0)  {
    		return Math.PI/2.0;
    	}
    	//X == 0, Y < 0, return 270 degrees
    	if (yComp < 0) {
    		return Math.PI*3.0/2.0;
    	}
    	//X == 0, Y == 0, return 0 degrees
    	return 0;
    }

    public static double getMagnitude(int xComp, int yComp) {
    	return Math.hypot(xComp, yComp);
    }

	public void setComponents(int xComp, int yComp) {
		xComponent = xComp;
		yComponent = yComp;
		angle = getAngle(xComponent, yComponent);
		magnitude = getMagnitude(xComponent, yComponent);

		tinyXComponent = getTinyXComponent(angle);
		tinyYComponent = getTinyYComponent(angle);
	}

	public void setXComponent(int xComp) {
		xComponent = xComp;
		angle = getAngle(xComponent, yComponent);
		magnitude = getMagnitude(xComponent, yComponent);
		tinyXComponent = getTinyXComponent(angle);
		tinyYComponent = getTinyYComponent(angle);
	}

	public void setYComponent(int yComp) {
		yComponent = yComp;
		angle = getAngle(xComponent, yComponent);
		magnitude = getMagnitude(xComponent, yComponent);
		tinyXComponent = getTinyXComponent(angle);
		tinyYComponent = getTinyYComponent(angle);
	}

	public void setAngle(double a) {
		angle = a;
		tinyXComponent = getTinyXComponent(angle);
		tinyYComponent = getTinyYComponent(angle);
		xComponent = getXComponent(angle, magnitude);
		yComponent = getYComponent(angle, magnitude);
	}

	public void setMagnitude(double m) {
		magnitude = m;
		xComponent = getXComponent(angle, magnitude);
		yComponent = getYComponent(angle, magnitude);
	}

	public double getTinyXComponent() {
		return tinyXComponent;
	}

	public double getTinyYComponent() {
		return tinyYComponent;
	}

	public int getXComponent() {
		return xComponent;
	}

	public int getYComponent() {
		return yComponent;
	}

	public double getAngle() {
		return angle;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public static double dotProduct(EuclideanVector a, EuclideanVector b) {
		return a.xComponent * b.xComponent + a.yComponent*b.yComponent;
	}

	public static double dotProductWithComponents(double xComp1, double yComp1, double xComp2, double yComp2) {
		return xComp1*xComp2 + yComp1*yComp2;
	}

	public static double dotProductWithAngle(double a1, double m1, double a2, double m2) {
		double x1, y1, x2, y2;
		x1 = getTinyXComponent(a1)*m1;
		y1 = getTinyYComponent(a1)*m1;
		x2 = getTinyXComponent(a2)*m2;
		y2 = getTinyYComponent(a2)*m2;
		return dotProductWithComponents(x1, y1, x2, y2);
	}

}