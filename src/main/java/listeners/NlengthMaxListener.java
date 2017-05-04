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

import MTObjects.MTcounter;
import graphconstructs.Trackproperties;
import interactiveMT.Interactive_MTDoubleChannel;

public class NlengthMaxListener implements ActionListener {

	
	 final Interactive_MTDoubleChannel parent;
		
		
		public NlengthMaxListener(final Interactive_MTDoubleChannel parent){
		
			this.parent = parent;
		}
		
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		int MinFrame = 0;
		int MaxFrame = 0;
		int term = 0;
		ArrayList<MTcounter> ALLcountsstart = new ArrayList<MTcounter>();
		ArrayList<MTcounter> ALLcountsend = new ArrayList<MTcounter>();
		if (parent.Allstart.get(0).size() > 0) {
			MaxFrame = parent.Allstart.get(parent.Allstart.size() - 1).get(0).Framenumber;
			MinFrame = parent.Allstart.get(0).get(0).Framenumber;

			final ArrayList<Trackproperties> first = parent.Allstart.get(0);
			Collections.sort(first, parent.Seedcomparetrack);
			int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
			int MinSeedLabel = first.get(0).seedlabel;

			for (int frameindex = MinFrame; frameindex < MaxFrame; ++frameindex) {

				for (int maxlength = 0; maxlength < Double.parseDouble(parent.Maxdpixel.getText()); maxlength += 5) {

					int MTcount = 0;

					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

						for (int listindex = 0; listindex < parent.startlengthlist.size(); ++listindex) {

							int currentframe = parent.startlengthlist.get(listindex).framenumber;

							if (currentframe == frameindex) {

								int seedID = parent.startlengthlist.get(listindex).seedid;

								if (seedID == currentseed) {

									double pixellength = parent.startlengthlist.get(listindex).totallengthpixel;

									if (pixellength >= maxlength) {

										MTcount++;

									}

								}

							}

						}

					}

					MTcounter newcounter = new MTcounter(frameindex, MTcount, maxlength);

					ALLcountsstart.add(newcounter);

				}
			}

		}
		term = 0;
		if (parent.Allend.get(0).size() > 0) {
			MaxFrame = parent.Allend.get(parent.Allend.size() - 1).get(0).Framenumber;
			MinFrame = parent.Allend.get(0).get(0).Framenumber;

			final ArrayList<Trackproperties> first = parent.Allend.get(0);
			Collections.sort(first, parent.Seedcomparetrack);
			int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
			int MinSeedLabel = first.get(0).seedlabel;

			for (int frameindex = MinFrame; frameindex < MaxFrame; ++frameindex) {

				for (int maxlength = 0; maxlength < Double.parseDouble(parent.Maxdpixel.getText()); maxlength += 5) {

					int MTcount = 0;

					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

						for (int listindex = 0; listindex < parent.endlengthlist.size(); ++listindex) {

							int currentframe = parent.endlengthlist.get(listindex).framenumber;

							if (currentframe == frameindex) {

								int seedID = parent.endlengthlist.get(listindex).seedid;

								if (seedID == currentseed) {

									double pixellength = parent.endlengthlist.get(listindex).totallengthpixel;

									if (pixellength >= maxlength) {

										MTcount++;

									}

								}

							}

						}

					}

					MTcounter newcounter = new MTcounter(frameindex, MTcount, maxlength);

					ALLcountsend.add(newcounter);

				}
			}

		}

		if (ALLcountsstart.size() > 0 && ALLcountsend.size() > 0) {

			for (int index = 0; index < ALLcountsstart.size(); ++index) {

				for (int secindex = 0; secindex < ALLcountsend.size(); ++secindex) {

					if (ALLcountsstart.get(index).framenumber == ALLcountsend.get(secindex).framenumber
							&& ALLcountsstart.get(index).maxlength == ALLcountsend.get(secindex).maxlength) {
						MTcounter newcounter = new MTcounter(ALLcountsstart.get(index).framenumber,
								ALLcountsstart.get(index).MTcountnumber + ALLcountsend.get(secindex).MTcountnumber,
								ALLcountsstart.get(index).maxlength);
						parent.ALLcounts.add(newcounter);

					}

				}

			}

		}

		else

			parent.ALLcounts = (ALLcountsstart.size() == 0) ? ALLcountsend : ALLcountsstart;

		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		try {
			File newfile = new File(parent.usefolder + "//" + parent.addToName + "STAT" + "N_L" + ".txt");

			FileWriter fw;

			fw = new FileWriter(newfile);

			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("\tFramenumber\tMTcount\tMaxLength\n");

			for (int index = 0; index < parent.ALLcounts.size(); ++index) {

				System.out.println("\t" + nf.format(parent.ALLcounts.get(index).framenumber) + "\t" + "\t"
						+ nf.format(parent.ALLcounts.get(index).MTcountnumber) + "\t" + "\t"
						+ nf.format(parent.ALLcounts.get(index).maxlength) + "\n");

				bw.write("\t" + nf.format(parent.ALLcounts.get(index).framenumber) + "\t" + "\t"
						+ nf.format(parent.ALLcounts.get(index).MTcountnumber) + "\t" + "\t"
						+ nf.format(parent.ALLcounts.get(index).maxlength) + "\n");

			}

			bw.close();
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
