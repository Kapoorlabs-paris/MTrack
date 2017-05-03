package util;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

public class RoiFunctions {

	
	
	public static void MakeRois(Roi rorig, final ImagePlus Kymoimp, int nbRois, ArrayList<float[]> lengthKymo, String usefolder, String addToName) {

		RoiManager roimanager = RoiManager.getInstance();

		rorig = Kymoimp.getRoi();

		if (rorig == null) {
			IJ.showMessage("Roi required");
		}
		nbRois = roimanager.getCount();
		Roi[] RoisOrig = roimanager.getRoisAsArray();

		Overlay overlaysec = Kymoimp.getOverlay();

		if (overlaysec == null) {
			overlaysec = new Overlay();

			Kymoimp.setOverlay(overlaysec);

		}
		overlaysec.clear();
		lengthKymo = new ArrayList<float[]>();
		for (int i = 0; i < nbRois; ++i) {

			PolygonRoi l = (PolygonRoi) RoisOrig[i];

			int n = l.getNCoordinates();
			float[] xCord = l.getFloatPolygon().xpoints;
			int[] yCord = l.getYCoordinates();

			for (int index = 0; index < n - 1; index++) {

				float[] cords = { xCord[index], (int) yCord[index] };
				float[] nextcords = { xCord[index + 1], (int) yCord[index + 1] };

				float slope = (float) ((nextcords[1] - cords[1]) / (nextcords[0] - cords[0]));
				float intercept = nextcords[1] - slope * nextcords[0];

				Line newlineKymo = new Line(cords[0], cords[1], nextcords[0], nextcords[1]);
				overlaysec.setStrokeColor(Color.RED);

				overlaysec.add(newlineKymo);
				Kymoimp.setOverlay(overlaysec);
				float[] cordsLine = new float[n];

				for (int y = (int) cords[1]; y < nextcords[1]; ++y) {
					cordsLine[1] = y;
					cordsLine[0] = (y - intercept) / (slope);
					if (slope != 0)
						lengthKymo.add(new float[] { cordsLine[0], cordsLine[1] });

				}

			}

		}

		/********
		 * The part below removes the duplicate entries in the array dor the
		 * time co-ordinate
		 ********/

		int j = 0;

		for (int index = 0; index < lengthKymo.size() - 1; ++index) {

			j = index + 1;

			while (j < lengthKymo.size()) {

				if (lengthKymo.get(index)[1] == lengthKymo.get(j)[1]) {

					lengthKymo.remove(index);
				}

				else {
					++j;

				}

			}
		}
		try {
			FileWriter fw;
			File fichierKy = new File(usefolder + "//" + addToName + "KymoWill-start" + ".txt");
			fw = new FileWriter(fichierKy);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("\tFramenumber\tlengthKymo\n");
			for (int index = 0; index < lengthKymo.size(); ++index) {
				bw.write("\t" + (lengthKymo.get(index)[1]) + "\t" + (lengthKymo.get(index)[0] + "\n"));
			}

			bw.close();
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Kymoimp.show();

	}

	
}
