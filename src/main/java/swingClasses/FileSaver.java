package swingClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

import MTObjects.ResultsMT;
import graphconstructs.Trackproperties;
import labeledObjects.PlusMinusSeed;

public class FileSaver {

	public static void SaveResults(ArrayList<ArrayList<Trackproperties>> Ends,
			ArrayList<PlusMinusSeed> plusminusendlist, ArrayList<ResultsMT> lengthlist, double[] calibration,
			File userfile, String addToName, NumberFormat nf) {

		if (Ends.get(0).size() > 0) {

			int MaxSeedLabel = Ends.get(0).get(Ends.get(0).size() - 1).seedlabel;
			int MinSeedLabel = Ends.get(0).get(0).seedlabel;

			for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

				for (int j = 0; j < plusminusendlist.size(); ++j) {

					if (plusminusendlist.get(j).seedid == currentseed) {
						try {
							File fichier = new File(userfile + "//" + addToName + "SeedLabel" + currentseed
									+ plusminusendlist.get(j).plusorminus + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFrame\tLength (px)\tLength (real)\tiD\tCurrentPosX (px)\tCurrentPosY (px)\tCurrentPosX (real)\tCurrentPosY (real)"
											+ "\tdeltaL (px) \tdeltaL (real)  \tCalibrationX  \tCalibrationY  \tCalibrationT \n");
							for (int index = 0; index < lengthlist.size(); ++index) {

								if (lengthlist.get(index).seedid == currentseed) {
									if (index > 0
											&& lengthlist.get(index).currentpointpixel[0] != lengthlist
													.get(index - 1).currentpointpixel[0]
											&& lengthlist.get(index).currentpointpixel[1] != lengthlist
													.get(index - 1).currentpointpixel[1])

										bw.write("\t" + nf.format(lengthlist.get(index).framenumber) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).totallengthpixel) + "\t" + "\t" + "\t"
												+ "\t" + nf.format(lengthlist.get(index).totallengthreal) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).seedid) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).currentpointpixel[0]) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).currentpointpixel[1]) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).currentpointreal[0]) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).currentpointreal[1]) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).lengthpixelperframe) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).lengthrealperframe) + "\t" + "\t"
												+ nf.format(calibration[0]) + "\t" + "\t" + nf.format(calibration[1])
												+ "\t" + "\t" + nf.format(calibration[2]) +

												"\n");

								}

							}
							bw.close();
							fw.close();

						} catch (IOException e) {
						}
					}
				}

			}

		}

	}
	
	
	public static void SaveUserResults(ArrayList<ArrayList<Trackproperties>> Ends, ArrayList<ResultsMT> userlengthlist, double[] calibration,
			File userfile, String addToName, NumberFormat nf) {
		
		final ArrayList<Trackproperties> first = Ends.get(0);
		int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
		int MinSeedLabel = first.get(0).seedlabel;
		
		for (int seedID = MinSeedLabel; seedID <= MaxSeedLabel; ++seedID) {
			

			

				try {
					File fichier = new File(userfile + "//" + addToName + "SeedLabel" + seedID
							+ "-Zeroend" + ".txt");

					FileWriter fw = new FileWriter(fichier);
					BufferedWriter bw = new BufferedWriter(fw);

					bw.write(
							"\tFrame\tLength (px)\tLength (real)\tiD\tCurrentPosX (px)\tCurrentPosY (px)\tCurrentPosX (real)\tCurrentPosY (real)"
									+ "\tdeltaL (px) \tdeltaL (real) \tCalibrationX  \tCalibrationY  \tFrametoSec \n");

					for (int index = 0; index < userlengthlist.size(); ++index) {
						if (userlengthlist.get(index).seedid == seedID) {

							if (index > 0
									&& userlengthlist
											.get(index).currentpointpixel[0] != userlengthlist
													.get(index - 1).currentpointpixel[0]
									&& userlengthlist
											.get(index).currentpointpixel[1] != userlengthlist
													.get(index - 1).currentpointpixel[1])

								bw.write("\t" + nf.format(userlengthlist.get(index).framenumber) + "\t" + "\t"
										+ nf.format(userlengthlist.get(index).totallengthpixel) + "\t"+ "\t"
										+ "\t" + "\t"+ nf.format(userlengthlist.get(index).totallengthreal)
										+ "\t" + "\t" + nf.format(userlengthlist.get(index).seedid)
										+ "\t" + "\t"
										+ nf.format(userlengthlist.get(index).currentpointpixel[0])
										+ "\t" + "\t"
										+ nf.format(userlengthlist.get(index).currentpointpixel[1])
										+ "\t" + "\t"
										+ nf.format(userlengthlist.get(index).currentpointreal[0])
										+ "\t" + "\t"
										+ nf.format(userlengthlist.get(index).currentpointreal[1])
										+ "\t" + "\t"
										+ nf.format(userlengthlist.get(index).lengthpixelperframe)
										+ "\t" + "\t"
										+ nf.format(userlengthlist.get(index).lengthrealperframe)
										+ "\t" + "\t"
										+ nf.format(calibration[0])  
										+ "\t" + "\t"
										+ nf.format(calibration[1])  
										+ "\t" + "\t"
										+ nf.format(calibration[2]) + 
										
										"\n");

						}

					}
					bw.close();
					fw.close();

				} catch (IOException e) {
				}
			}
		
	}
	
	public static void SaveResults(ArrayList<ArrayList<Trackproperties>> Ends,
			ArrayList<PlusMinusSeed> plusminusendlist, ArrayList<ResultsMT> lengthlist, double[] calibration,
			String userfile, String addToName, NumberFormat nf) {

		if (Ends.get(0).size() > 0) {

			int MaxSeedLabel = Ends.get(0).get(Ends.get(0).size() - 1).seedlabel;
			int MinSeedLabel = Ends.get(0).get(0).seedlabel;

			for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

				for (int j = 0; j < plusminusendlist.size(); ++j) {

					if (plusminusendlist.get(j).seedid == currentseed) {
						try {
							File fichier = new File(userfile + "//" + addToName + "SeedLabel" + currentseed
									+ plusminusendlist.get(j).plusorminus + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFrame\tLength (px)\tLength (real)\tiD\tCurrentPosX (px)\tCurrentPosY (px)\tCurrentPosX (real)\tCurrentPosY (real)"
											+ "\tdeltaL (px) \tdeltaL (real)  \tCalibrationX  \tCalibrationY  \tCalibrationT \n");
							for (int index = 0; index < lengthlist.size(); ++index) {

								if (lengthlist.get(index).seedid == currentseed) {
									if (index > 0
											&& lengthlist.get(index).currentpointpixel[0] != lengthlist
													.get(index - 1).currentpointpixel[0]
											&& lengthlist.get(index).currentpointpixel[1] != lengthlist
													.get(index - 1).currentpointpixel[1])

										bw.write("\t" + nf.format(lengthlist.get(index).framenumber) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).totallengthpixel) + "\t" + "\t" + "\t"
												+ "\t" + nf.format(lengthlist.get(index).totallengthreal) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).seedid) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).currentpointpixel[0]) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).currentpointpixel[1]) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).currentpointreal[0]) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).currentpointreal[1]) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).lengthpixelperframe) + "\t" + "\t"
												+ nf.format(lengthlist.get(index).lengthrealperframe) + "\t" + "\t"
												+ nf.format(calibration[0]) + "\t" + "\t" + nf.format(calibration[1])
												+ "\t" + "\t" + nf.format(calibration[2]) +

												"\n");

								}

							}
							bw.close();
							fw.close();

						} catch (IOException e) {
						}
					}
				}

			}

		}

	}
	
}
