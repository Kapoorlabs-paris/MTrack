package houghandWatershed;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.type.numeric.RealType;

public abstract class Finaldistance {

	double[] realpoints;
	double[] funcparamone;
	double funcparamtwo;
	double funcparamthree;
	
	public Finaldistance(double[] realpoints, double[] funcparamone, double funcparamtwo, double funcparamthree) {

		this.realpoints = realpoints;
		this.funcparamone = funcparamone;
		this.funcparamtwo = funcparamtwo;
		this.funcparamthree = funcparamthree;

	}

	public Finaldistance(double[] realpoints, double funcparamtwo, double funcparamthree) {

		this.realpoints = realpoints;
		this.funcparamtwo = funcparamtwo;
		this.funcparamthree = funcparamthree;

	}

	// General distance between two points
	public static double Generalfunctiondist(double[] secondpos, double[] firstpos) {
		double distance;

		distance = Math.pow((secondpos[0] - firstpos[0]), 2) + Math.pow((secondpos[1] - firstpos[1]), 2);

		return Math.sqrt(distance);
	}

	// Ditance of a point (realpos) from the Normal line to a curve at some point (secondrealpos), derivative of the function
	// is also needed at the same point
	
	public static <T extends RealType<T>> double disttocurvenormal(double[] secondrealpos, double[] realpos,
			 double [] derivrealpos ) {

		Finalfunction Normalline = new Finalfunction(realpos, -1.0 / derivrealpos[1],
				secondrealpos[0] / derivrealpos[1] + secondrealpos[1]);
		final double distanceline = Normalline.Linefunctiondist();

		return distanceline;
	}
	
	// Ditance of a point (realpos) from the Tangent line to a curve at some point (secondrealpos), derivative of the function
	// is also needed at the same point
	
	public static <T extends RealType<T>> double disttocurvetangent(double[] secondrealpos, double[] realpos,
			double[] derivrealpos) {
		Finalfunction Tangentline = new Finalfunction(realpos, derivrealpos[1],
				secondrealpos[1] - secondrealpos[0] * derivrealpos[1]);
		final double distanceline = Tangentline.Linefunctiondist();

		return distanceline;
	}

}
