package swingClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import MTObjects.ResultsMT;
import drawandOverlay.DisplayGraph;
import drawandOverlay.DisplayGraphKalman;
import graphconstructs.KalmanTrackproperties;
import graphconstructs.Trackproperties;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import lineFinder.FindlinesVia;
import lineFinder.LinefinderInteractiveHFHough;
import lineFinder.LinefinderInteractiveHFMSER;
import lineFinder.LinefinderInteractiveHFMSERwHough;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import trackerType.KFsearch;
import trackerType.TrackModel;
import velocityanalyser.Trackend;
import velocityanalyser.Trackstart;

public  class Track {
	
        final Interactive_MTDoubleChannel parent;
	
	
	public Track(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	
	public  void Trackobject(final int next) {

		parent.maxStack();
		int Kalmancount = 0;

		for (int index = next; index <= parent.thirdDimensionSize; ++index) {

			Kalmancount++;

			parent.thirdDimension = index;
			parent.isStarted = true;
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg, parent.thirdDimension,
					parent.thirdDimensionSize);
			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension, parent.thirdDimensionSize);
			parent.updatePreview(ValueChange.THIRDDIMTrack);

			boolean dialog;
			boolean dialogupdate;

			RandomAccessibleInterval<FloatType> groundframe = parent.currentimg;
			RandomAccessibleInterval<FloatType> groundframepre = parent.currentPreprocessedimg;

			if (parent.FindLinesViaMSER) {
				if (index == next) {
					dialog = parent.DialogueModelChoiceHF();

					IJ.log("MSER parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
					IJ.log("Delta " + " " + parent.delta + " " + "minSize " + " " + parent.minSize + " " + "maxSize " + " " + parent.maxSize
							+ " " + " maxVar " + " " + parent.maxVar + " " + "minDIversity " + " " + parent.minDiversity);

					IJ.log("Optimization Parameters: " + "R" + parent.Intensityratio + " G"
							+ parent.Inispacing / Math.min(parent.psf[0], parent.psf[1]));

				}

				else
					dialog = false;

				parent.updatePreview(ValueChange.SHOWMSER);

				LinefinderInteractiveHFMSER newlineMser = new LinefinderInteractiveHFMSER(groundframe, groundframepre,
						parent.newtree, parent.minlength, parent.thirdDimension);
				if (parent.showDeterministic) {
					parent.returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, parent.PrevFrameparam,
							parent.minlength, parent.thirdDimension, parent.psf, newlineMser, parent.userChoiceModel, parent.Domask, parent.Intensityratio,
							parent.Inispacing, parent.seedmap, parent.jpb, parent.thirdDimensionSize);
					parent.Accountedframes.add(FindlinesVia.getAccountedframes());
				}

				if (parent.showKalman) {
					parent.returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
							parent.PrevFrameparamKalman, parent.minlength, parent.thirdDimension, parent.psf, newlineMser, parent.userChoiceModel, parent.Domask,
							Kalmancount, parent.Intensityratio, parent.Inispacing, parent.seedmap, parent.jpb, parent.thirdDimensionSize);

					parent.Accountedframes.add(FindlinesVia.getAccountedframes());
				}

			}

			if (parent.FindLinesViaHOUGH) {

				if (index == next) {
					dialog = parent.DialogueModelChoiceHF();

					IJ.log("Hough parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
					IJ.log("thetaPerPixel " + " " + parent.thetaPerPixel + " " + "rhoPerPixel " + " " + parent.rhoPerPixel);
					IJ.log("Optimization Parameters: " + "R" + parent.Intensityratio + " G"
							+ parent.Inispacing / Math.min(parent.psf[0], parent.psf[1]));

				}

				else
					dialog = false;

				parent.updatePreview(ValueChange.SHOWHOUGH);
				LinefinderInteractiveHFHough newlineHough = new LinefinderInteractiveHFHough(groundframe,
						groundframepre, parent.intimg, parent.Maxlabel, parent.thetaPerPixel, parent.rhoPerPixel, parent.thirdDimension);
				if (parent.showDeterministic) {
					parent.returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, parent.PrevFrameparam,
							parent.minlength, parent.thirdDimension, parent.psf, newlineHough, parent.userChoiceModel,parent.Domask, parent.Intensityratio,
							parent.Inispacing, parent.seedmap, parent.jpb, parent.thirdDimensionSize);

					parent.Accountedframes.add(FindlinesVia.getAccountedframes());
				}

				if (parent.showKalman) {
					parent.returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
							parent.PrevFrameparamKalman, parent.minlength,parent.thirdDimension, parent.psf, newlineHough, parent.userChoiceModel, parent.Domask,
							Kalmancount, parent.Intensityratio, parent.Inispacing, parent.seedmap, parent.jpb, parent.thirdDimensionSize);
					parent.Accountedframes.add(FindlinesVia.getAccountedframes());
				}

			}

			if (parent.FindLinesViaMSERwHOUGH) {
				if (index == next) {
					dialog = parent.DialogueModelChoice();

					IJ.log("MSER parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
					IJ.log("Delta " + " " + parent.delta + " " + "minSize " + " " + parent.minSize + " " + "maxSize " + " " + parent.maxSize
							+ " " + " maxVar " + " " + parent.maxVar + " " + "minDIversity " + " " + parent.minDiversity);
					IJ.log("Hough parameters:" + " " + " thirdDimension: " + " " + parent.thirdDimension);
					IJ.log("thetaPerPixel " + " " + parent.thetaPerPixel + " " + "rhoPerPixel " + " " + parent.rhoPerPixel);
					IJ.log("Optimization Parameters: " + "R" + parent.Intensityratio + " G"
							+ parent.Inispacing / Math.min(parent.psf[0], parent.psf[1]));

				} else
					dialog = false;

				parent.updatePreview(ValueChange.SHOWMSER);
				LinefinderInteractiveHFMSERwHough newlineMserwHough = new LinefinderInteractiveHFMSERwHough(groundframe,
						groundframepre, parent.newtree, parent.minlength, parent.thirdDimension, parent.thetaPerPixel, parent.rhoPerPixel);
				if (parent.showDeterministic) {
					parent.returnVector = FindlinesVia.LinefindingMethodHF(groundframe, groundframepre, parent.PrevFrameparam,
							parent.minlength, parent.thirdDimension, parent.psf, newlineMserwHough, parent.userChoiceModel, parent.Domask, parent.Intensityratio,
							parent.Inispacing, parent.seedmap, parent.jpb, parent.thirdDimensionSize);

					parent.Accountedframes.add(FindlinesVia.getAccountedframes());
				}
				if (parent.showKalman) {
					parent.returnVectorKalman = FindlinesVia.LinefindingMethodHFKalman(groundframe, groundframepre,
							parent.PrevFrameparamKalman, parent.minlength, parent.thirdDimension, parent.psf, newlineMserwHough, parent.userChoiceModel,
							parent.Domask, Kalmancount, parent.Intensityratio, parent.Inispacing, parent.seedmap, parent.jpb, parent.thirdDimensionSize);

					parent.Accountedframes.add(FindlinesVia.getAccountedframes());
				}

			}

			if (parent.showDeterministic) {
				parent.NewFrameparam = parent.returnVector.getB();

				ArrayList<Trackproperties> startStateVectors = parent.returnVector.getA().getA();
				ArrayList<Trackproperties> endStateVectors = parent.returnVector.getA().getB();

		
				parent.detcount++;
				util.DrawingUtils.Trackplot(parent.detcount, parent.returnVector, parent.AllpreviousRois, parent.colorLineTrack, parent.colorTrack, parent.overlay, parent.maxghost);
				
				
				parent.PrevFrameparam = parent.NewFrameparam;

				parent.Allstart.add(startStateVectors);
				parent.Allend.add(endStateVectors);
			}

			if (parent.showKalman) {
				parent.NewFrameparamKalman = parent.returnVectorKalman.getB();

				ArrayList<KalmanTrackproperties> startStateVectorsKalman = parent.returnVectorKalman.getA().getA();
				ArrayList<KalmanTrackproperties> endStateVectorsKalman = parent.returnVectorKalman.getA().getB();

				parent.PrevFrameparamKalman = parent.NewFrameparamKalman;

				parent.AllstartKalman.add(startStateVectorsKalman);
				parent.AllendKalman.add(endStateVectorsKalman);
			}
			
		
		}

		if (parent.showDeterministic) {

			if (parent.Allstart.get(0).size() > 0) {
				ImagePlus impstartsec = ImageJFunctions.show(parent.originalimg);
				final Trackstart trackerstart = new Trackstart(parent.Allstart, parent.thirdDimensionSize - next);
				trackerstart.process();
				SimpleWeightedGraph<double[], DefaultWeightedEdge> graphstart = trackerstart.getResult();
				ArrayList<Pair<Integer, double[]>> ID = trackerstart.getSeedID();
				DisplayGraph displaygraphtrackstart = new DisplayGraph(impstartsec, graphstart, ID);
				parent.IDALL.addAll(ID);
				displaygraphtrackstart.getImp();
				impstartsec.draw();
				impstartsec.setTitle("Graph Start A MT");
			}
			if (parent.Allend.get(0).size() > 0) {
				ImagePlus impendsec = ImageJFunctions.show(parent.originalimg);
				final Trackend trackerend = new Trackend(parent.Allend, parent.thirdDimensionSize - next);

				trackerend.process();
				SimpleWeightedGraph<double[], DefaultWeightedEdge> graphend = trackerend.getResult();
				ArrayList<Pair<Integer, double[]>> ID = trackerend.getSeedID();
				DisplayGraph displaygraphtrackend = new DisplayGraph(impendsec, graphend, ID);
				parent.IDALL.addAll(ID);
				displaygraphtrackend.getImp();
				impendsec.draw();
				impendsec.setTitle("Graph Start B MT");
			}

		}

		if (parent.showKalman) {

			ResultsTable rtAll = new ResultsTable();
			if (parent.AllstartKalman.get(0).size() > 0) {

				parent.MTtrackerstart = new KFsearch(parent.AllstartKalman, parent.UserchosenCostFunction, parent.maxSearchradius,
						parent.initialSearchradius, parent.thirdDimension, parent.thirdDimensionSize, parent.missedframes);
				parent.MTtrackerstart.reset();
				parent.MTtrackerstart.process();

				ImagePlus impstartsecKalman = ImageJFunctions.show(parent.originalimg);

				impstartsecKalman.setTitle("Kalman Graph Start A MT");
				SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graphstartKalman = parent.MTtrackerstart
						.getResult();

				DisplayGraphKalman Startdisplaytracks = new DisplayGraphKalman(impstartsecKalman, graphstartKalman);
				Startdisplaytracks.getImp();
				impstartsecKalman.draw();

				TrackModel modelstart = new TrackModel(graphstartKalman);
				modelstart.getDirectedNeighborIndex();

				// Get all the track id's
				for (final Integer id : modelstart.trackIDs(true)) {
					if (parent.SaveTxt) {
						try {
							File fichier = new File(parent.usefolder + "//" + parent.addToName + "Trackid" + id + "-endA" + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFramenumber\tTotal Length (pixel)\tTotal Length (real)\tSeed iD\tCurrentPosition X (px units)\tCurrentPosition Y (px units)\tCurrentPosition X (real units)\tCurrentPosition Y (real units)"
											+ "\tLength per frame (px units)" + "\tLength per frame (real units)\n");

							// Get the corresponding set for each id
							modelstart.setName(id, "Track" + id);
							final HashSet<KalmanTrackproperties> Snakeset = modelstart.trackKalmanTrackpropertiess(id);
							ArrayList<KalmanTrackproperties> list = new ArrayList<KalmanTrackproperties>();

							Comparator<KalmanTrackproperties> ThirdDimcomparison = new Comparator<KalmanTrackproperties>() {

								@Override
								public int compare(final KalmanTrackproperties A, final KalmanTrackproperties B) {

									return A.thirdDimension - B.thirdDimension;

								}

							};

							Iterator<KalmanTrackproperties> Snakeiter = Snakeset.iterator();

							while (Snakeiter.hasNext()) {

								KalmanTrackproperties currentsnake = Snakeiter.next();

								list.add(currentsnake);

							}
							Collections.sort(list, ThirdDimcomparison);

							final double[] originalpoint = list.get(0).originalpoint;
							double startlengthreal = 0;
							double startlengthpixel = 0;
							for (int index = 1; index < list.size() - 1; ++index) {

								final double[] currentpoint = list.get(index).currentpoint;
								final double[] oldpoint = list.get(index - 1).currentpoint;
								final double[] currentpointCal = new double[] { currentpoint[0] * parent.calibration[0],
										currentpoint[1] * parent.calibration[1] };
								final double[] oldpointCal = new double[] { oldpoint[0] * parent.calibration[0],
										oldpoint[1] * parent.calibration[1] };
								final double lengthpixelperframe = util.Boundingboxes.Distance(currentpoint, oldpoint);
								final double lengthrealperframe = util.Boundingboxes.Distance(currentpointCal,
										oldpointCal);
								final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, currentpoint);
								final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
								final boolean shrink = seedtoold > seedtocurrent ? true : false;
								final boolean growth = seedtoold > seedtocurrent ? false : true;

								if (shrink) {
									// MT shrank

									startlengthreal -= lengthrealperframe;
									startlengthpixel -= lengthpixelperframe;

								}
								if (growth) {

									// MT grew
									startlengthreal += lengthrealperframe;
									startlengthpixel += lengthpixelperframe;

								}

								double[] currentlocationpixel = new double[parent.ndims];

								if (list.get(index).thirdDimension == parent.thirdDimensionsliderInit)
									currentlocationpixel = originalpoint;
								else
									currentlocationpixel = currentpoint;

								double[] currentlocationreal = new double[parent.ndims];

								currentlocationreal = new double[] { currentlocationpixel[0] * parent.calibration[0],
										currentlocationpixel[1] * parent.calibration[1] };

								ResultsMT startMT = new ResultsMT(list.get(index).thirdDimension, startlengthpixel,
										startlengthreal, id, currentlocationpixel, currentlocationreal,
										lengthpixelperframe, lengthrealperframe);

								parent.startlengthlist.add(startMT);

								bw.write("\t" + list.get(index).thirdDimension + "\t" + "\t"
										+ parent.nf.format(startlengthpixel) + "\t" + "\t" + parent.nf.format(startlengthreal) + "\t"
										+ "\t" + parent.nf.format(id) + "\t" + "\t" + parent.nf.format(currentlocationpixel[0]) + "\t"
										+ "\t" + parent.nf.format(currentlocationpixel[1]) + "\t" + "\t"
										+ parent.nf.format(currentlocationreal[0]) + "\t" + "\t"
										+parent.nf.format(currentlocationreal[1]) + "\t" + "\t"
										+ parent.nf.format(lengthpixelperframe) + "\t" + "\t" + parent.nf.format(lengthrealperframe)
										+ "\n");

								double[] landt = { startlengthpixel, list.get(index).thirdDimension, id };
								parent.lengthtimestart.add(landt);
								rtAll.incrementCounter();
								rtAll.addValue("FrameNumber", list.get(index).thirdDimension);
								rtAll.addValue("Total Length (pixel)", startlengthpixel);
								rtAll.addValue("Total Length (real)", startlengthreal);
								rtAll.addValue("Track iD", id);
								rtAll.addValue("CurrentPosition X (px units)", currentlocationpixel[0]);
								rtAll.addValue("CurrentPosition Y (px units)", currentlocationpixel[1]);
								rtAll.addValue("CurrentPosition X (real units)", currentlocationreal[0]);
								rtAll.addValue("CurrentPosition Y (real units)", currentlocationreal[1]);
								rtAll.addValue("Length per frame (px units)", lengthpixelperframe);
								rtAll.addValue("Length per frame (real units)", lengthrealperframe);

							}

							bw.close();
							fw.close();

						} catch (IOException e) {
						}
					}

				}
			}

			if (parent.AllendKalman.get(0).size() > 0) {

				ImagePlus impendKalman = ImageJFunctions.show(parent.originalimg);

				parent.MTtrackerend = new KFsearch(parent.AllendKalman, parent.UserchosenCostFunction, parent.maxSearchradius, parent.initialSearchradius,
						parent.thirdDimension, parent.thirdDimensionSize, parent.missedframes);

				parent.MTtrackerend.reset();
				parent.MTtrackerend.process();
				SimpleWeightedGraph<KalmanTrackproperties, DefaultWeightedEdge> graphendKalman = parent.MTtrackerend
						.getResult();

				impendKalman.draw();

				ImagePlus impendsecKalman = ImageJFunctions.show(parent.originalimg);
				impendsecKalman.setTitle("Kalman Graph Start B MT");
				DisplayGraphKalman Enddisplaytracks = new DisplayGraphKalman(impendsecKalman, graphendKalman);
				Enddisplaytracks.getImp();
				impendsecKalman.draw();
				TrackModel modelend = new TrackModel(graphendKalman);
				modelend.getDirectedNeighborIndex();
				// Get all the track id's

				for (final Integer id : modelend.trackIDs(true)) {

					if (parent.SaveTxt) {
						try {
							File fichier = new File(parent.usefolder + "//" + parent.addToName + "Trackid" + id + "-endB" + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFramenumber\tTotal Length (pixel)\tTotal Length (real)\tSeed iD\tCurrentPosition X (px units)\tCurrentPosition Y (px units)\tCurrentPosition X (real units)\tCurrentPosition Y (real units)"
											+ "\tLength per frame (px units)" + "\tLength per frame (real units)\n");
							// Get the corresponding set for each id
							modelend.setName(id, "Track" + id);
							final HashSet<KalmanTrackproperties> Snakeset = modelend.trackKalmanTrackpropertiess(id);
							ArrayList<KalmanTrackproperties> list = new ArrayList<KalmanTrackproperties>();

							Comparator<KalmanTrackproperties> ThirdDimcomparison = new Comparator<KalmanTrackproperties>() {

								@Override
								public int compare(final KalmanTrackproperties A, final KalmanTrackproperties B) {

									return A.thirdDimension - B.thirdDimension;

								}

							};

							Iterator<KalmanTrackproperties> Snakeiter = Snakeset.iterator();

							while (Snakeiter.hasNext()) {

								KalmanTrackproperties currentsnake = Snakeiter.next();

								list.add(currentsnake);

							}
							Collections.sort(list, ThirdDimcomparison);

							double endlengthreal = 0;
							double endlengthpixel = 0;
							final double[] originalpoint = list.get(0).originalpoint;
							for (int index = 1; index < list.size() - 1; ++index) {

								final double[] currentpoint = list.get(index).currentpoint;
								final double[] oldpoint = list.get(index - 1).currentpoint;
								final double[] currentpointCal = new double[] { currentpoint[0] * parent.calibration[0],
										currentpoint[1] * parent.calibration[1] };
								final double[] oldpointCal = new double[] { oldpoint[0] * parent.calibration[0],
										oldpoint[1] * parent.calibration[1] };
								final double lengthpixelperframe = util.Boundingboxes.Distance(currentpoint, oldpoint);
								final double lengthrealperframe = util.Boundingboxes.Distance(currentpointCal,
										oldpointCal);
								final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, currentpoint);
								final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
								final boolean shrink = seedtoold > seedtocurrent ? true : false;
								final boolean growth = seedtoold > seedtocurrent ? false : true;

								if (shrink) {

									// MT shrank
									endlengthreal -= lengthrealperframe;
									endlengthpixel -= lengthpixelperframe;

								}
								if (growth) {

									// MT grew
									endlengthreal += lengthrealperframe;
									endlengthpixel += lengthpixelperframe;

								}

								double[] currentlocationpixel = new double[parent.ndims];

								if (list.get(index).thirdDimension == parent.thirdDimensionsliderInit)
									currentlocationpixel = originalpoint;
								else
									currentlocationpixel = currentpoint;

								double[] currentlocationreal = new double[parent.ndims];

								currentlocationreal = new double[] { currentlocationpixel[0] * parent.calibration[0],
										currentlocationpixel[1] * parent.calibration[1] };

								ResultsMT endMT = new ResultsMT(list.get(index).thirdDimension, endlengthpixel,
										endlengthreal, id, currentlocationpixel, currentlocationreal,
										lengthpixelperframe, lengthrealperframe);

								parent.endlengthlist.add(endMT);

								bw.write("\t" + list.get(index).thirdDimension + "\t" + "\t" + parent.nf.format(endlengthpixel)
										+ "\t" + "\t" + parent.nf.format(endlengthreal) + "\t" + "\t" + parent.nf.format(id) + "\t"
										+ "\t" + parent.nf.format(currentlocationpixel[0]) + "\t" + "\t"
										+ parent.nf.format(currentlocationpixel[1]) + "\t" + "\t"
										+ parent.nf.format(currentlocationreal[0]) + "\t" + "\t"
										+ parent.nf.format(currentlocationreal[1]) + "\t" + "\t"
										+ parent.nf.format(lengthpixelperframe) + "\t" + "\t" + parent.nf.format(lengthrealperframe)
										+ "\n");

								double[] landt = { endlengthpixel, list.get(index).thirdDimension, id };
								parent.lengthtimeend.add(landt);
								rtAll.incrementCounter();
								rtAll.addValue("FrameNumber", list.get(index).thirdDimension);
								rtAll.addValue("Total Length (pixel)", endlengthpixel);
								rtAll.addValue("Total Length (real)", endlengthreal);
								rtAll.addValue("Track iD", id);
								rtAll.addValue("CurrentPosition X (px units)", currentlocationpixel[0]);
								rtAll.addValue("CurrentPosition Y (px units)", currentlocationpixel[1]);
								rtAll.addValue("CurrentPosition X (real units)", currentlocationreal[0]);
								rtAll.addValue("CurrentPosition Y (real units)", currentlocationreal[1]);
								rtAll.addValue("Length per frame (px units)", lengthpixelperframe);
								rtAll.addValue("Length per frame (real units)", lengthrealperframe);

							}
							bw.close();
							fw.close();

						} catch (IOException e) {
						}
					}

				}

			}
			rtAll.show("Results");
		}
		if (parent.showDeterministic) {

			ResultsTable rtAll = new ResultsTable();
			if (parent.Allstart.get(0).size() > 0) {
				final ArrayList<Trackproperties> first = parent.Allstart.get(0);

				Collections.sort(first, parent.Seedcomparetrack);

				int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
				int MinSeedLabel = first.get(0).seedlabel;
				for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
					double startlengthreal = 0;
					double startlengthpixel = 0;
					System.out.println(currentseed);
					for (int index = 0; index < parent.Allstart.size(); ++index) {

						final ArrayList<Trackproperties> thirdDimension = parent.Allstart.get(index);

						for (int frameindex = 0; frameindex < thirdDimension.size(); ++frameindex) {

							final Integer seedID = thirdDimension.get(frameindex).seedlabel;
							final int framenumber = thirdDimension.get(frameindex).Framenumber;
							if (seedID == currentseed) {
								final Integer[] FrameID = { framenumber, seedID };
								final double[] originalpoint = thirdDimension.get(frameindex).originalpoint;
								final double[] newpoint = thirdDimension.get(frameindex).newpoint;
								final double[] oldpoint = thirdDimension.get(frameindex).oldpoint;
								final double[] newpointCal = new double[] {
										thirdDimension.get(frameindex).newpoint[0] * parent.calibration[0],
										thirdDimension.get(frameindex).newpoint[1] * parent.calibration[1] };
								final double[] oldpointCal = new double[] {
										thirdDimension.get(frameindex).oldpoint[0] * parent.calibration[0],
										thirdDimension.get(frameindex).oldpoint[1] * parent.calibration[1] };

								final double lengthrealperframe = util.Boundingboxes.Distance(newpointCal, oldpointCal);
								final double lengthpixelperframe = util.Boundingboxes.Distance(newpoint, oldpoint);
								final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, newpoint);
								final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
								final boolean shrink = seedtoold > seedtocurrent ? true : false;
								final boolean growth = seedtoold > seedtocurrent ? false : true;

								if (shrink) {
									// MT shrank

									startlengthreal -= lengthrealperframe;
									startlengthpixel -= lengthpixelperframe;

								}
								if (growth) {

									// MT grew
									startlengthreal += lengthrealperframe;
									startlengthpixel += lengthpixelperframe;

								}

								double[] currentlocationpixel = new double[parent.ndims];

								if (framenumber == parent.thirdDimensionsliderInit)
									currentlocationpixel = originalpoint;
								else
									currentlocationpixel = newpoint;

								double[] currentlocationreal = new double[parent.ndims];

								currentlocationreal = new double[] { currentlocationpixel[0] * parent.calibration[0],
										currentlocationpixel[1] * parent.calibration[1] };

								ResultsMT startMT = new ResultsMT(framenumber, startlengthpixel, startlengthreal,
										seedID, currentlocationpixel, currentlocationreal, lengthpixelperframe,
										lengthrealperframe);

								parent.startlengthlist.add(startMT);

							}
						}
					}
				}
				for (int seedID = MinSeedLabel; seedID <= MaxSeedLabel; ++seedID) {
					if (parent.SaveTxt) {
						try {
							File fichier = new File(
									parent.usefolder + "//" + parent.addToName + "SeedLabel" + seedID + "-endA" + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFramenumber\tTotal Length (pixel)\tTotal Length (real)\tSeed iD\tCurrentPosition X (px units)\tCurrentPosition Y (px units)\tCurrentPosition X (real units)\tCurrentPosition Y (real units)"
											+ "\tLength per frame (px units)" + "\tLength per frame (real units)\n");

							for (int index = 0; index < parent.startlengthlist.size(); ++index) {
								if (parent.startlengthlist.get(index).seedid == seedID) {

									bw.write("\t" + parent.startlengthlist.get(index).framenumber + "\t" + "\t"
											+ parent.nf.format(parent.startlengthlist.get(index).totallengthpixel) + "\t" + "\t"
											+ parent.nf.format(parent.startlengthlist.get(index).totallengthreal) + "\t" + "\t"
											+ parent.nf.format(parent.startlengthlist.get(index).seedid) + "\t" + "\t"
											+ parent.nf.format(parent.startlengthlist.get(index).currentpointpixel[0]) + "\t" + "\t"
											+ parent.nf.format(parent.startlengthlist.get(index).currentpointpixel[1]) + "\t" + "\t"
											+ parent.nf.format(parent.startlengthlist.get(index).currentpointreal[0]) + "\t" + "\t"
											+ parent.nf.format(parent.startlengthlist.get(index).currentpointreal[1]) + "\t" + "\t"
											+ parent.nf.format(parent.startlengthlist.get(index).lengthpixelperframe) + "\t" + "\t"
											+ parent.nf.format(parent.startlengthlist.get(index).lengthrealperframe) + "\n");

								}

							}
							bw.close();
							fw.close();

						} catch (IOException e) {
						}
					}
				}
				for (int index = 0; index < parent.startlengthlist.size(); ++index) {

					double[] landt = { parent.startlengthlist.get(index).totallengthpixel,
							parent.startlengthlist.get(index).framenumber, parent.startlengthlist.get(index).seedid };
					parent.lengthtimestart.add(landt);

					rtAll.incrementCounter();
					rtAll.addValue("FrameNumber", parent.startlengthlist.get(index).framenumber);
					rtAll.addValue("Total Length (pixel)", parent.startlengthlist.get(index).totallengthpixel);
					rtAll.addValue("Total Length (real)", parent.startlengthlist.get(index).totallengthreal);
					rtAll.addValue("Track iD", parent.startlengthlist.get(index).seedid);
					rtAll.addValue("CurrentPosition X (px units)", parent.startlengthlist.get(index).currentpointpixel[0]);
					rtAll.addValue("CurrentPosition Y (px units)", parent.startlengthlist.get(index).currentpointpixel[1]);
					rtAll.addValue("CurrentPosition X (real units)", parent.startlengthlist.get(index).currentpointreal[0]);
					rtAll.addValue("CurrentPosition Y (real units)", parent.startlengthlist.get(index).currentpointreal[1]);
					rtAll.addValue("Length per frame (px units)", parent.startlengthlist.get(index).lengthpixelperframe);
					rtAll.addValue("Length per frame (real units)", parent.startlengthlist.get(index).lengthrealperframe);

				}

			}

			if (parent.Allend.get(0).size() > 0) {
				final ArrayList<Trackproperties> first = parent.Allend.get(0);
				Collections.sort(first, parent.Seedcomparetrack);
				int MaxSeedLabel = first.get(first.size() - 1).seedlabel;
				int MinSeedLabel = first.get(0).seedlabel;
				for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
					System.out.println(currentseed);
					double endlengthreal = 0;
					double endlengthpixel = 0;
					for (int index = 0; index < parent.Allend.size(); ++index) {

						final ArrayList<Trackproperties> thirdDimension = parent.Allend.get(index);

						for (int frameindex = 0; frameindex < thirdDimension.size(); ++frameindex) {
							final int framenumber = thirdDimension.get(frameindex).Framenumber;
							final Integer seedID = thirdDimension.get(frameindex).seedlabel;

							if (seedID == currentseed) {
								final Integer[] FrameID = { framenumber, seedID };
								final double[] originalpoint = thirdDimension.get(frameindex).originalpoint;
								final double[] newpoint = thirdDimension.get(frameindex).newpoint;
								final double[] oldpoint = thirdDimension.get(frameindex).oldpoint;

								final double[] newpointCal = new double[] {
										thirdDimension.get(frameindex).newpoint[0] * parent.calibration[0],
										thirdDimension.get(frameindex).newpoint[1] * parent.calibration[1] };
								final double[] oldpointCal = new double[] {
										thirdDimension.get(frameindex).oldpoint[0] * parent.calibration[0],
										thirdDimension.get(frameindex).oldpoint[1] * parent.calibration[1] };

								final double lengthrealperframe = util.Boundingboxes.Distance(newpointCal, oldpointCal);
								final double lengthpixelperframe = util.Boundingboxes.Distance(newpoint, oldpoint);
								final double seedtocurrent = util.Boundingboxes.Distancesq(originalpoint, newpoint);
								final double seedtoold = util.Boundingboxes.Distancesq(originalpoint, oldpoint);
								final boolean shrink = seedtoold > seedtocurrent ? true : false;
								final boolean growth = seedtoold > seedtocurrent ? false : true;

								if (shrink) {

									// MT shrank

									endlengthreal -= lengthrealperframe;
									endlengthpixel -= lengthpixelperframe;

								}

								if (growth) {

									// MT grew

									endlengthreal += lengthrealperframe;
									endlengthpixel += lengthpixelperframe;

								}

								double[] currentlocationpixel = new double[parent.ndims];

								if (framenumber == parent.thirdDimensionsliderInit)
									currentlocationpixel = originalpoint;
								else
									currentlocationpixel = newpoint;

								double[] currentlocationreal = new double[parent.ndims];

								currentlocationreal = new double[] { currentlocationpixel[0] * parent.calibration[0],
										currentlocationpixel[1] * parent.calibration[1] };

								ResultsMT endMT = new ResultsMT(framenumber, endlengthpixel, endlengthreal, seedID,
										currentlocationpixel, currentlocationreal, lengthpixelperframe,
										lengthrealperframe);

								parent.endlengthlist.add(endMT);

							}
						}
					}

				}
				for (int seedID = MinSeedLabel; seedID <= MaxSeedLabel; ++seedID) {
					if (parent.SaveTxt) {
						try {
							File fichier = new File(
									parent.usefolder + "//" + parent.addToName + "SeedLabel" + seedID + "-endA" + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFramenumber\tTotal Length (pixel)\tTotal Length (real)\tSeed iD\tCurrentPosition X (px units)\tCurrentPosition Y (px units)\tCurrentPosition X (real units)\tCurrentPosition Y (real units)"
											+ "\tLength per frame (px units)" + "\tLength per frame (real units)\n");

							for (int index = 0; index < parent.endlengthlist.size(); ++index) {
								if (parent.endlengthlist.get(index).seedid == seedID) {

									bw.write("\t" + parent.endlengthlist.get(index).framenumber + "\t" + "\t"
											+ parent.nf.format(parent.endlengthlist.get(index).totallengthpixel) + "\t" + "\t"
											+ parent.nf.format(parent.endlengthlist.get(index).totallengthreal) + "\t" + "\t"
											+ parent.nf.format(parent.endlengthlist.get(index).seedid) + "\t" + "\t"
											+ parent.nf.format(parent.endlengthlist.get(index).currentpointpixel[0]) + "\t" + "\t"
											+ parent.nf.format(parent.endlengthlist.get(index).currentpointpixel[1]) + "\t" + "\t"
											+ parent.nf.format(parent.endlengthlist.get(index).currentpointreal[0]) + "\t" + "\t"
											+ parent.nf.format(parent.endlengthlist.get(index).currentpointreal[1]) + "\t" + "\t"
											+ parent.nf.format(parent.endlengthlist.get(index).lengthpixelperframe) + "\t" + "\t"
											+ parent.nf.format(parent.endlengthlist.get(index).lengthrealperframe) + "\n");

								}

							}
							bw.close();
							fw.close();

						} catch (IOException e) {
						}
					}
				}
				for (int index = 0; index < parent.endlengthlist.size(); ++index) {

					double[] landt = { parent.endlengthlist.get(index).totallengthpixel, parent.endlengthlist.get(index).framenumber,
							parent.endlengthlist.get(index).seedid };
					parent.lengthtimestart.add(landt);

					rtAll.incrementCounter();
					rtAll.addValue("FrameNumber", parent.endlengthlist.get(index).framenumber);
					rtAll.addValue("Total Length (pixel)", parent.endlengthlist.get(index).totallengthpixel);
					rtAll.addValue("Total Length (real)", parent.endlengthlist.get(index).totallengthreal);
					rtAll.addValue("Track iD", parent.endlengthlist.get(index).seedid);
					rtAll.addValue("CurrentPosition X (px units)", parent.endlengthlist.get(index).currentpointpixel[0]);
					rtAll.addValue("CurrentPosition Y (px units)", parent.endlengthlist.get(index).currentpointpixel[1]);
					rtAll.addValue("CurrentPosition X (real units)", parent.endlengthlist.get(index).currentpointreal[0]);
					rtAll.addValue("CurrentPosition Y (real units)", parent.endlengthlist.get(index).currentpointreal[1]);
					rtAll.addValue("Length per frame (px units)", parent.endlengthlist.get(index).lengthpixelperframe);
					rtAll.addValue("Length per frame (real units)", parent.endlengthlist.get(index).lengthrealperframe);

				}

			}

			rtAll.show("Start and End of MT");
			if (parent.lengthtimestart != null)
				parent.lengthtime =parent. lengthtimestart;
			else
				parent.lengthtime = parent.lengthtimeend;
			if (parent.analyzekymo) {
				double lengthcheckstart = 0;
				double lengthcheckend = 0;
				if (parent.lengthtimestart != null) {

					parent.lengthtime = parent.lengthtimestart;
					for (int index = 0; index < parent.lengthtimestart.size(); ++index) {

						int time = (int) parent.lengthtimestart.get(index)[1];

						lengthcheckstart += parent.lengthtimestart.get(index)[0];

						for (int secindex = 0; secindex < parent.lengthKymo.size(); ++secindex) {

							for (int accountindex = 0; accountindex < parent.Accountedframes.size(); ++accountindex) {

								if ((int) parent.lengthKymo.get(secindex)[1] == time
										&& parent.Accountedframes.get(accountindex) == time) {

									float delta = (float) (parent.lengthtimestart.get(index)[0] - parent.lengthKymo.get(secindex)[0]);
									float[] cudeltadeltaLstart = { delta, time };
									parent.deltadstart.add(cudeltadeltaLstart);

								}
							}

						}
					}

					/********
					 * The part below removes the duplicate entries in the array
					 * dor the time co-ordinate
					 ********/

					int j = 0;

					for (int index = 0; index < parent.deltadstart.size() - 1; ++index) {

						j = index + 1;

						while (j < parent.deltadstart.size()) {

							if (parent.deltadstart.get(index)[1] == parent.deltadstart.get(j)[1]) {

								parent.deltadstart.remove(index);
							}

							else {
								++j;

							}

						}
					}

					for (int index = 0; index < parent.deltadstart.size(); ++index) {

						for (int secindex = 0; secindex < parent.Accountedframes.size(); ++secindex) {

							if ((int) parent.deltadstart.get(index)[1] == parent.Accountedframes.get(secindex)) {

								parent.netdeltadstart += Math.abs(parent.deltadstart.get(index)[0]);

							}

						}

					}
					parent.deltad = parent.deltadstart;

				}

				if (parent.lengthtimeend != null) {
					parent.lengthtime = parent.lengthtimeend;
					for (int index = 0; index < parent.lengthtimeend.size(); ++index) {

						int time = (int) parent.lengthtimeend.get(index)[1];

						lengthcheckend += parent.lengthtimeend.get(index)[0];

						for (int secindex = 0; secindex < parent.lengthKymo.size(); ++secindex) {

							for (int accountindex = 0; accountindex < parent.Accountedframes.size(); ++accountindex) {

								if ((int) parent.lengthKymo.get(secindex)[1] == time
										&& parent.Accountedframes.get(accountindex) == time) {

									if ((int) parent.lengthKymo.get(secindex)[1] == time
											&& parent.Accountedframes.get(accountindex) == time) {

										float delta = (float) (parent.lengthtimeend.get(index)[0]
												- parent.lengthKymo.get(secindex)[0]);
										float[] cudeltadeltaLend = { delta, time };
										parent.deltadend.add(cudeltadeltaLend);
									}
								}

							}

						}
					}
					/********
					 * The part below removes the duplicate entries in the array
					 * dor the time co-ordinate
					 ********/

					int j = 0;

					for (int index = 0; index < parent.deltadend.size() - 1; ++index) {

						j = index + 1;

						while (j < parent.deltadend.size()) {

							if (parent.deltadend.get(index)[1] == parent.deltadend.get(j)[1]) {

								parent.deltadend.remove(index);
							}

							else {
								++j;

							}

						}
					}

					for (int index = 0; index < parent.deltadend.size(); ++index) {

						for (int secindex = 0; secindex < parent.Accountedframes.size(); ++secindex) {

							if ((int) parent.deltadend.get(index)[1] == parent.Accountedframes.get(secindex)) {

								parent.netdeltadend += Math.abs(parent.deltadend.get(index)[0]);

							}

						}

					}

					parent.deltad = parent.deltadend;
				}

				FileWriter deltaw;
				File fichierKydel = new File(parent.usefolder + "//" + parent.addToName + "MTtracker-deltad" + ".txt");

				try {
					deltaw = new FileWriter(fichierKydel);
					BufferedWriter bdeltaw = new BufferedWriter(deltaw);

					bdeltaw.write("\ttime\tDeltad(pixel units)\n");

					for (int index = 0; index < parent.deltad.size(); ++index) {
						bdeltaw.write("\t" + parent.deltad.get(index)[1] + "\t" + parent.deltad.get(index)[0] + "\n");

					}

					bdeltaw.close();
					deltaw.close();
				}

				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (int index = 0; index < parent.deltad.size(); ++index) {

					for (int secindex = 0; secindex < parent.Accountedframes.size(); ++secindex) {

						if ((int) parent.deltad.get(index)[1] == parent.Accountedframes.get(secindex)) {

							parent.netdeltad += Math.abs(parent.deltad.get(index)[0]);

						}

					}

				}
				parent.netdeltad /= parent.deltad.size();

				if (parent.netdeltad > parent.deltadcutoff) {

					parent.redo = true;

				} else
					parent.redo = false;

			}
		}
		if (parent.Kymoimg != null) {
			ImagePlus newimp = parent.Kymoimp.duplicate();
			for (int index = 0; index < parent.lengthtime.size() - 1; ++index) {

				Overlay overlay = parent.Kymoimp.getOverlay();
				if (overlay == null) {
					overlay = new Overlay();
					parent.Kymoimp.setOverlay(overlay);
				}
				Line newline = new Line(parent.lengthtime.get(index)[0], parent.lengthtime.get(index)[1],
						parent.lengthtime.get(index + 1)[0], parent.lengthtime.get(index + 1)[1]);
				newline.setFillColor(parent.colorDraw);

				overlay.add(newline);

				parent.Kymoimp.setOverlay(overlay);
				RoiManager roimanager = RoiManager.getInstance();

				roimanager.addRoi(newline);

			}

			parent.Kymoimp.show();
		}
		parent.displaystack();
		if (parent.displayoverlay) {
			parent.prestack.deleteLastSlice();
			new ImagePlus("Overlays", parent.prestack).show();
		}

	}


}
