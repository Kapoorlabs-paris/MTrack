package ransacBatch;

import ij.Prefs;
import mt.listeners.InteractiveRANSAC;

public class CreateRanfile {

	final InteractiveRANSAC parent;

	public CreateRanfile(final InteractiveRANSAC parent) {

		this.parent = parent;
	}

	

	public void RecordParent() {


		Prefs.set("MaxError.double", parent.maxError);
		Prefs.set("MinPoints.double", parent.minInliers);

		Prefs.set("MaxGap.double", parent.maxDist);
		Prefs.set("Rescue.double", parent.restolerance);
		Prefs.set("Linearity.double", parent.lambda);
		Prefs.set("Minslope.double", parent.minSlope);
		Prefs.set("Maxslope.double", parent.maxSlope);
		Prefs.set("DetectCat.boolean", parent.detectCatastrophe);
		Prefs.set("MinDist.double", parent.minDistanceCatastrophe);
		Prefs.set("Functionchoice.int", parent.functionChoice);
		Prefs.set("numTp.int", parent.numTimepoints);
		Prefs.savePreferences();

		 System.exit(1);

	}

}
