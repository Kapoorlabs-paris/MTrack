package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import interactiveMT.Interactive_MTDoubleChannel;

public class AnalyzeListener implements ActionListener {

	
	final Interactive_MTDoubleChannel parent;
	
	
	public AnalyzeListener(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		// Needs to be fixed for analysis
		int frametosec = 5;
		if (parent.analyzekymo)
			parent.numberKymo = true;

		ArrayList<float[]> deltaL = new ArrayList<>();
		if (parent.numberKymo) {

			for (int index = 1; index < parent.lengthKymo.size(); ++index) {

				float delta = parent.lengthKymo.get(index)[0] - parent.lengthKymo.get(index - 1)[0];

				float[] deltalt = { delta, parent.lengthKymo.get(index)[1] };

				deltaL.add(deltalt);

			}

			double velocity = 0;
			for (int index = 0; index < deltaL.size(); ++index) {

				if (deltaL.get(index)[1] >= parent.starttime && deltaL.get(index)[1] <= parent.endtime) {
					velocity += deltaL.get(index)[0];

				}
			}

			velocity /= parent.endtime - parent.starttime;

			float[] rates = { parent.starttime, parent.endtime, (float) velocity,
					(float) (velocity * parent.calibration[0] / frametosec) };
			parent.finalvelocityKymo.add(rates);

			FileWriter vw;
			File fichierKyvel = new File(parent.usefolder + "//" + parent.addToName + "KymoWill-velocity" + ".txt");
			try {
				vw = new FileWriter(fichierKyvel);
				BufferedWriter bvw = new BufferedWriter(vw);

				for (int i = 0; i < parent.finalvelocityKymo.size(); ++i) {
					System.out.println("KymoResult: " + "\t" + parent.finalvelocityKymo.get(i)[0] + "\t"
							+ parent.finalvelocityKymo.get(i)[1] + "\t" + parent.finalvelocityKymo.get(i)[2] + "\t "
							+ parent.finalvelocityKymo.get(i)[3] + "\n");
					IJ.log("KymoResult: " + "\t" + parent.finalvelocityKymo.get(i)[0] + "\t" + parent.finalvelocityKymo.get(i)[1]
							+ "\t" + parent.finalvelocityKymo.get(i)[2] + "\t " + parent.finalvelocityKymo.get(i)[3] + "\n");
					bvw.write("\tStarttime\tEndtime\tRate(velocity pixel units)\tRate(velocity real units)\n");
					bvw.write("\t" + parent.finalvelocityKymo.get(i)[0] + "\t" +parent.finalvelocityKymo.get(i)[1] + "\t"
							+ parent.finalvelocityKymo.get(i)[2] + "\t " + parent.finalvelocityKymo.get(i)[3] + "\n");

				}
				bvw.close();
				vw.close();
			}

			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		ArrayList<float[]> deltadeltaL = new ArrayList<>();
		ArrayList<float[]> deltaLMT = new ArrayList<>();
		if (parent.lengthtimestart != null) {
			for (int index = 1; index < parent.lengthtimestart.size(); ++index) {

				if ((int) parent.lengthtimestart.get(index)[2] == parent.selectedSeed) {
					float delta = (float) (parent.lengthtimestart.get(index)[0] - parent.lengthtimestart.get(index - 1)[0]);

					float[] deltalt = { delta, (int) parent.lengthtimestart.get(index)[1], parent.selectedSeed };

					deltaLMT.add(deltalt);

				}

			}
		}
		if (parent.lengthtimeend != null) {
			for (int index = 1; index < parent.lengthtimeend.size(); ++index) {
				if ((int) parent.lengthtimeend.get(index)[2] == parent.selectedSeed) {
					float delta = (float) (parent.lengthtimeend.get(index)[0] - parent.lengthtimeend.get(index - 1)[0]);

					float[] deltalt = { delta, (int) parent.lengthtimeend.get(index)[1], parent.selectedSeed };

					deltaLMT.add(deltalt);

				}
			}
		}

		if (parent.numberKymo) {
			for (int index = 0; index < deltaLMT.size(); ++index) {

				int time = (int) deltaLMT.get(index)[1];

				for (int secindex = 0; secindex < deltaL.size(); ++secindex) {

					if ((int) deltaL.get(secindex)[1] == time) {

						float delta = deltaLMT.get(index)[0] - deltaL.get(secindex)[0];
						float[] cudeltadeltaLstart = { delta, time };
						deltadeltaL.add(cudeltadeltaLstart);

					}

				}

			}

		}

		double velocity = 0;
		for (int index = 0; index < deltaLMT.size(); ++index) {

			if (deltaLMT.get(index)[1] >= parent.starttime && deltaLMT.get(index)[1] <=parent.endtime) {
				velocity += deltaLMT.get(index)[0];

			}
		}

		velocity /= parent.endtime - parent.starttime;

		if (parent.numberKymo) {

			FileWriter deltal;
			File fichierKylel = new File(parent.usefolder + "//" + parent.addToName + "MTtracker-deltadeltal" + ".txt");

			try {
				deltal = new FileWriter(fichierKylel);
				BufferedWriter bdeltal = new BufferedWriter(deltal);

				bdeltal.write("\ttime\tDeltaDeltal(pixel units)\n");

				for (int index = 0; index < deltadeltaL.size(); ++index) {
					bdeltal.write("\t" + deltadeltaL.get(index)[1] + "\t" + deltadeltaL.get(index)[0] + "\n");

				}

				bdeltal.close();
				deltal.close();
			}

			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		float[] rates = { parent.starttime, parent.endtime, (float) velocity, (float) (velocity * parent.calibration[0] / frametosec),
				parent.selectedSeed };
		parent.finalvelocity.add(rates);

		FileWriter vw;
		File fichierKyvel = new File(parent.usefolder + "//" + parent.addToName + "MTtracker-velocity" + ".txt");
		try {
			vw = new FileWriter(fichierKyvel);
			BufferedWriter bvw = new BufferedWriter(vw);
			bvw.write(
					"\tStarttime\tEndtime\tRate(velocity pixel units)\tRate (velocity in real units)\tSelectedSeed\n");
			for (int i = 0; i < parent.finalvelocity.size(); ++i) {

				System.out.println("MT tracker: " + "\t" + parent.finalvelocity.get(i)[0] + "\t" + parent.finalvelocity.get(i)[1]
						+ "\t" + parent.finalvelocity.get(i)[2] + "\t" + parent.finalvelocity.get(i)[3] + "\t"
						+ parent.finalvelocity.get(i)[4] + "\n");

				IJ.log("MT tracker: " + "\t" + parent.finalvelocity.get(i)[0] + "\t" + parent.finalvelocity.get(i)[1] + "\t"
						+ parent.finalvelocity.get(i)[2] + "\t" + parent.finalvelocity.get(i)[3] + "\t" + parent.finalvelocity.get(i)[4]
						+ "\n");

				bvw.write("\t" + parent.finalvelocity.get(i)[0] + "\t" + parent.finalvelocity.get(i)[1] + "\t"
						+ parent.finalvelocity.get(i)[2] + "\t" + parent.finalvelocity.get(i)[3] + "\t" + parent.finalvelocity.get(i)[4]
						+ "\n");

			}
			bvw.close();
			vw.close();
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}