/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 - 2022 MTrack developers.
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

public class FileSaverDummy {

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
							File fichier = new File(userfile + "//" + addToName + "DummySeedLabel" + currentseed
									+ plusminusendlist.get(j).plusorminus + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFrame\tdeltaL (px) \n");
							for (int index = 0; index < lengthlist.size(); ++index) {

								if (lengthlist.get(index).seedid == currentseed) {
									if (index > 0
											&& lengthlist.get(index).currentpointpixel[0] != lengthlist
													.get(index - 1).currentpointpixel[0]
											&& lengthlist.get(index).currentpointpixel[1] != lengthlist
													.get(index - 1).currentpointpixel[1])

										bw.write("\t" + nf.format(lengthlist.get(index).framenumber) + "\t" + "\t"
												
												+ nf.format(lengthlist.get(index).lengthpixelperframe) + "\t" + "\t"
												
												 +

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
					File fichier = new File(userfile + "//" + addToName + "DummySeedLabel" + seedID
							+ "-Zeroend" + ".txt");

					FileWriter fw = new FileWriter(fichier);
					BufferedWriter bw = new BufferedWriter(fw);

					bw.write(
							"\tFrame\tdeltaL (px) \n");

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
										
										+ nf.format(userlengthlist.get(index).lengthpixelperframe)
										+ 
										
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
							File fichier = new File(userfile + "//" + addToName + "DummySeedLabel" + currentseed
									+ plusminusendlist.get(j).plusorminus + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFrame\tdeltaL (px)  \n");
							for (int index = 0; index < lengthlist.size(); ++index) {

								if (lengthlist.get(index).seedid == currentseed) {
									if (index > 0
											&& lengthlist.get(index).currentpointpixel[0] != lengthlist
													.get(index - 1).currentpointpixel[0]
											&& lengthlist.get(index).currentpointpixel[1] != lengthlist
													.get(index - 1).currentpointpixel[1])

										bw.write("\t" + nf.format(lengthlist.get(index).framenumber) + "\t" + "\t"
												
												+ nf.format(lengthlist.get(index).lengthpixelperframe) + "\t" + "\t"
												+

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
