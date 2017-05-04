package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import graphconstructs.Trackproperties;
import interactiveMT.Interactive_MTDoubleChannel;

public class NlengthListener implements ActionListener {

	
	 final Interactive_MTDoubleChannel parent;
		
		
		public NlengthListener(final Interactive_MTDoubleChannel parent){
		
			this.parent = parent;
		}
		
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		if (parent.Allstart.get(0).size() > 0) {
			int MaxFrame = parent.Allstart.get(parent.Allstart.size() - 1).get(0).Framenumber;
			int MinFrame = parent.Allstart.get(0).get(0).Framenumber;

			final ArrayList<Trackproperties> first = parent.Allstart.get(0);
			Collections.sort(first, parent.Seedcomparetrack);
			int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
			int MinSeedLabel = first.get(0).seedlabel;

			for (int frameindex = MinFrame; frameindex < MaxFrame; ++frameindex) {

				for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

					if (parent.pixellength.get(currentseed) != null)
						parent.sumlengthpixel = parent.pixellength.get(currentseed);

					if (parent.microlength.get(currentseed) != null)
						parent.sumlengthmicro = parent.microlength.get(currentseed);

					for (int listindex = 0; listindex < parent.startlengthlist.size(); ++listindex) {

						int currentframe = parent.startlengthlist.get(listindex).framenumber;

						if (currentframe == frameindex) {

							int seedID = parent.startlengthlist.get(listindex).seedid;

							if (seedID == currentseed) {

								parent.sumlengthpixel += parent.startlengthlist.get(listindex).totallengthpixel;
								parent.sumlengthmicro += parent.startlengthlist.get(listindex).totallengthreal;

								parent.pixellength.put(seedID, parent.sumlengthpixel);
								parent.microlength.put(seedID, parent.sumlengthmicro);

							}

						}

					}
				}

			}

			int timeInterval = MaxFrame - MinFrame;
			try {
				File meanfile = new File(parent.usefolder + "//" + parent.addToName + "Start" + "-MeanLength" + ".txt");
				FileWriter fw = new FileWriter(meanfile);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("\tSeedLabel\tMeanLength(px)\tMeanLength (real units) \n");

				for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
					System.out.println("Seed ID : " + currentseed + " " + "Average Length Pixels "
							+ parent.pixellength.get(currentseed) / timeInterval + " " + "Average Length Real Units "
							+ parent.microlength.get(currentseed) / timeInterval);

					bw.write("\t" + currentseed + "\t" + "\t"

							+ nf.format(parent.pixellength.get(currentseed) / timeInterval) + "\t" + "\t"
							+ nf.format(parent.microlength.get(currentseed) / timeInterval) + "\n");

				}

				bw.close();
				fw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (parent.Allend.get(0).size() > 0) {
			int MaxFrame = parent.Allend.get(parent.Allend.size() - 1).get(0).Framenumber;
			int MinFrame = parent.Allend.get(0).get(0).Framenumber;

			final ArrayList<Trackproperties> first = parent.Allend.get(0);
			Collections.sort(first, parent.Seedcomparetrack);
			int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
			int MinSeedLabel = first.get(0).seedlabel;

			for (int frameindex = MinFrame; frameindex < MaxFrame; ++frameindex) {

				for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

					if (parent.pixellength.get(currentseed) != null)
						parent.sumlengthpixel = parent.pixellength.get(currentseed);

					if (parent.microlength.get(currentseed) != null)
						parent.sumlengthmicro = parent.microlength.get(currentseed);

					for (int listindex = 0; listindex < parent.endlengthlist.size(); ++listindex) {

						int currentframe = parent.endlengthlist.get(listindex).framenumber;

						if (currentframe == frameindex) {

							int seedID = parent.endlengthlist.get(listindex).seedid;

							if (seedID == currentseed) {

								parent.sumlengthpixel += parent.endlengthlist.get(listindex).totallengthpixel;
								parent.sumlengthmicro += parent.endlengthlist.get(listindex).totallengthreal;

								parent.pixellength.put(seedID, parent.sumlengthpixel);
								parent.microlength.put(seedID, parent.sumlengthmicro);

							}

						}

					}
				}

			}
			try {
				File meanfile = new File(parent.usefolder + "//" + parent.addToName + "End" + "-MeanLength" + ".txt");
				FileWriter fw = new FileWriter(meanfile);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("\tSeedLabel\tMeanLength(px)\tMeanLength (real units) \n");

				int timeInterval = MaxFrame - MinFrame;

				for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
					System.out.println("Seed ID : " + currentseed + " " + "Average Length "
							+ parent.pixellength.get(currentseed) / timeInterval + " " + "Average Length Real Units "
							+ parent.microlength.get(currentseed) / timeInterval);

					bw.write("\t" + currentseed + "\t" + "\t"

							+ nf.format(parent.pixellength.get(currentseed) / timeInterval) + "\t" + "\t"
							+ nf.format(parent.microlength.get(currentseed) / timeInterval) + "\n");

				}
				bw.close();
				fw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}

