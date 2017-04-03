package houghandWatershed;

public class Finalfunction extends Finaldistance {

	public Finalfunction(double[] realpoints, double[] funcparamone, double funcparamtwo, double funcparamthree) {

		super(realpoints, funcparamone, funcparamtwo, funcparamthree);

	}

	public Finalfunction(double[] realpoints, double funcparamtwo, double funcparamthree) {

		super(realpoints, funcparamtwo, funcparamthree);

	}

// Shortest distance of a point from a user defined circle
	public double Circlefunctiondist() {

		// funcparamone = center fo the circle, funcparamtwo = radius;

		double distance;

		distance = Math.abs(Math
				.sqrt(Math.pow((realpoints[1] - funcparamone[1]), 2) + Math.pow((realpoints[0] - funcparamone[0]), 2))
				- funcparamtwo);

		return distance;

	}
	// Shortest distance of a point from a user defined line
	public double Linefunctiondist() {

		// funcparamtwo = slope, funcparamthree = intercept along x-axis

		double distance;

		double minX = (realpoints[0] - funcparamtwo * (funcparamthree - realpoints[1]))
				/ (1 + funcparamtwo * funcparamtwo);
		double minY = minX * funcparamtwo + funcparamthree;

		distance = Math.pow((minX - realpoints[0]), 2) + Math.pow((minY - realpoints[1]), 2);

		return Math.sqrt(distance);
	}

	
}
