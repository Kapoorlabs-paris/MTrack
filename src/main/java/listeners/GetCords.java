/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package listeners;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import interactiveMT.Interactive_MTDoubleChannel;

public class GetCords implements ActionListener {
	
final Interactive_MTDoubleChannel parent;
	
	
	public GetCords(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		MakeRois();

	}




   public void MakeRois() {

	RoiManager roimanager = RoiManager.getInstance();

	parent.rorig = parent.Kymoimp.getRoi();

	if (parent.rorig == null) {
		IJ.showMessage("Roi required");
	}
	parent.nbRois = roimanager.getCount();
	Roi[] RoisOrig = roimanager.getRoisAsArray();

	Overlay overlaysec = parent.Kymoimp.getOverlay();

	if (overlaysec == null) {
		overlaysec = new Overlay();

		parent.Kymoimp.setOverlay(overlaysec);

	}
	overlaysec.clear();
	parent.lengthKymo = new ArrayList<float[]>();
	for (int i = 0; i < parent.nbRois; ++i) {

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
			parent.Kymoimp.setOverlay(overlaysec);
			float[] cordsLine = new float[n];

			for (int y = (int) cords[1]; y < nextcords[1]; ++y) {
				cordsLine[1] = y;
				cordsLine[0] = (y - intercept) / (slope);
				if (slope != 0)
					parent.lengthKymo.add(new float[] { cordsLine[0], cordsLine[1] });

			}

		}

	}

	/********
	 * The part below removes the duplicate entries in the array dor the
	 * time co-ordinate
	 ********/

	int j = 0;

	for (int index = 0; index < parent.lengthKymo.size() - 1; ++index) {

		j = index + 1;

		while (j < parent.lengthKymo.size()) {

			if (parent.lengthKymo.get(index)[1] == parent.lengthKymo.get(j)[1]) {

				parent.lengthKymo.remove(index);
			}

			else {
				++j;

			}

		}
	}
	try {
		FileWriter fw;
		File fichierKy = new File(parent.usefolder + "//" + parent.addToName + "KymoWill-start" + ".txt");
		fw = new FileWriter(fichierKy);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("\tFramenumber\tlengthKymo\n");
		for (int index = 0; index < parent.lengthKymo.size(); ++index) {
			bw.write("\t" + (parent.lengthKymo.get(index)[1]) + "\t" + (parent.lengthKymo.get(index)[0] + "\n"));
		}

		bw.close();
		fw.close();

	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	parent.Kymoimp.show();

}
}

