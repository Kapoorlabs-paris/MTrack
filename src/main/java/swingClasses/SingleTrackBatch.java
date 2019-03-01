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
package swingClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import MTObjects.ResultsMT;
import drawandOverlay.DisplayGraph;
import graphconstructs.Trackproperties;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.io.FileSaver;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import labeledObjects.PlusMinusSeed;
import interactiveMT.SingleBatchMode;
import interactiveMT.Interactive_MTSingleChannel.WhichendSingle;
import lineFinder.FindlinesVia;
import lineFinder.LinefinderInteractiveHFMSER;
import lineFinder.LinefinderInteractiveHFMSERwHough;
import lineFinder.SingleFindlinesVia;
import lineFinder.SingleLinefinderInteractiveHFHough;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import velocityanalyser.Trackend;
import velocityanalyser.Trackstart;

public  class SingleTrackBatch {
	
        final SingleBatchMode parent;
        ArrayList<PlusMinusSeed> plusminusstartlist = new ArrayList<PlusMinusSeed>();
    	ArrayList<PlusMinusSeed> plusminusendlist = new ArrayList<PlusMinusSeed>();
    	HashMap<Integer, Double> startseedmap = new HashMap<Integer, Double>();
    	HashMap<Integer, Double> endseedmap = new HashMap<Integer, Double>();
    	 private volatile boolean exit = false;
	public SingleTrackBatch(final SingleBatchMode parent){
	
		this.parent = parent;
		
	}
	
	
	
	public  void Trackobject(final int next, final int endtime) {

		while(!exit) {
		parent.thirdDimensionSize = endtime;

		for (int index = next; index <= endtime; ++index) {

			
			
			

			parent.displayBitimg = false;
			parent.displayWatershedimg = false;
			parent.thirdDimension = index;
			parent.isStarted = true;
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg, parent.thirdDimension,
					parent.thirdDimensionSize);
			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension, parent.thirdDimensionSize);
			parent.updatePreview(ValueChange.THIRDDIMTrack);

		

			RandomAccessibleInterval<FloatType> groundframe = parent.currentimg;
			RandomAccessibleInterval<FloatType> groundframepre = parent.currentPreprocessedimg;

			if (parent.parent.FindLinesViaMSER) {
			
				

				parent.updatePreview(ValueChange.SHOWMSER);
				LinefinderInteractiveHFMSER newlineMser = new LinefinderInteractiveHFMSER(groundframe, groundframepre,
						parent.newtree,  parent.thirdDimension, parent.IDALL);
					parent.returnVector = SingleFindlinesVia.LinefindingMethodHF(groundframe, groundframepre, parent.PrevFrameparam,
							 parent.thirdDimension, parent.psf, newlineMser, parent.parent.userChoiceModel, parent.Domask, parent.Intensityratio,
							parent.Inispacing, parent.seedmap, parent.jpb, next, parent.thirdDimensionSize, parent.maxdist, parent.numgaussians);
					parent.Accountedframes.add(FindlinesVia.getAccountedframes());
					
					
				

				

			}

			if (parent.parent.FindLinesViaHOUGH) {
				

				parent.updatePreview(ValueChange.SHOWHOUGH);
				parent.updatePreview(ValueChange.SHOWMSERinHough);
				SingleLinefinderInteractiveHFHough newlineHough = new SingleLinefinderInteractiveHFHough(parent.parent,groundframe,
						groundframepre, parent.Maxlabel, parent.thirdDimension, parent.IDALL);
				
					parent.returnVector = SingleFindlinesVia.LinefindingMethodHF(groundframe, groundframepre, parent.PrevFrameparam,
							 parent.thirdDimension, parent.psf, newlineHough, parent.parent.userChoiceModel,parent.Domask, parent.Intensityratio,
							parent.Inispacing, parent.seedmap, parent.jpb, next, parent.thirdDimensionSize, parent.maxdist, parent.numgaussians);

					parent.Accountedframes.add(FindlinesVia.getAccountedframes());
					
	                   

			

			}

			if (parent.parent.FindLinesViaMSERwHOUGH) {
				
				parent.updatePreview(ValueChange.SHOWMSER);
				LinefinderInteractiveHFMSERwHough newlineMserwHough = new LinefinderInteractiveHFMSERwHough(groundframe,
						groundframepre, parent.newtree, parent.thirdDimension, parent.thetaPerPixel, parent.rhoPerPixel, parent.IDALL);
				if (parent.parent.showDeterministic) {
					parent.returnVector = SingleFindlinesVia.LinefindingMethodHF(groundframe, groundframepre, parent.PrevFrameparam,
							 parent.thirdDimension, parent.psf, newlineMserwHough, parent.parent.userChoiceModel, parent.Domask, parent.Intensityratio,
							parent.Inispacing, parent.seedmap, parent.jpb, next, parent.thirdDimensionSize, parent.maxdist, parent.numgaussians);

					parent.Accountedframes.add(FindlinesVia.getAccountedframes());
					
                     
					
				}
			

			}

				parent.NewFrameparam = parent.returnVector.getB();
				
				ArrayList<Trackproperties> startStateVectors = parent.returnVector.getA().getA();
				ArrayList<Trackproperties> endStateVectors = parent.returnVector.getA().getB();

				if( parent.returnVectorUser != null ){
					parent.UserframeNew = parent.returnVectorUser.getB();
					ArrayList<Trackproperties> userStateVectors = parent.returnVectorUser.getA();
					parent.AllUser.add(userStateVectors);
					parent.Userframe = parent.UserframeNew;
				}
		
				parent.detcount++;
				util.DrawingUtils.Trackplot(parent.detcount, parent.returnVector, parent.returnVectorUser,
						parent.AllpreviousRois, parent.colorLineTrack, parent.colorTrack, parent.inactiveColor, parent.overlay, parent.maxghost);
				
				
				parent.PrevFrameparam = parent.NewFrameparam;
				

				
				parent.Allstart.add(startStateVectors);
				parent.Allend.add(endStateVectors);

				if (index == endtime){
					parent.panel.remove(parent.jpb);
					parent.preprocessedimp.close();
					}
		
		}


			if (parent.Allstart.get(0).size() > 0) {
				ImagePlus impstartsec = ImageJFunctions.show(parent.originalimg);
				final Trackstart trackerstart = new Trackstart(parent.Allstart, parent.thirdDimensionSize - next);
				trackerstart.process();
				SimpleWeightedGraph<double[], DefaultWeightedEdge> graphstart = trackerstart.getResult();
				DisplayGraph displaygraphtrackstart = new DisplayGraph(impstartsec, graphstart);
				displaygraphtrackstart.getImp();
				impstartsec.draw();
				impstartsec.setTitle("Graph Start A MT");
				FileSaver fsB = new FileSaver(impstartsec);
				fsB.saveAsTiff(  parent.batchfolder + "//" + parent.parent.addToName + ".tif");

			
			}
			if (parent.Allend.get(0).size() > 0) {
				ImagePlus impendsec = ImageJFunctions.show(parent.originalimg);
				final Trackend trackerend = new Trackend(parent.Allend, parent.thirdDimensionSize - next);

				trackerend.process();
				SimpleWeightedGraph<double[], DefaultWeightedEdge> graphend = trackerend.getResult();
				DisplayGraph displaygraphtrackend = new DisplayGraph(impendsec, graphend);
				displaygraphtrackend.getImp();
				impendsec.draw();
				impendsec.setTitle("Graph Start B MT");
				FileSaver fsB = new FileSaver(impendsec);
				fsB.saveAsTiff(  parent.batchfolder + "//" + parent.parent.addToName + ".tif");
				
			}
			
			if (parent.returnVectorUser != null  && parent.AllUser.get(0).size() > 0) {
				ImagePlus impstartsec = ImageJFunctions.show(parent.originalimg);
				final Trackstart trackerstart = new Trackstart(parent.AllUser, parent.thirdDimensionSize - next);
				trackerstart.process();
				SimpleWeightedGraph<double[], DefaultWeightedEdge> graphstart = trackerstart.getResult();
				DisplayGraph displaygraphtrackstart = new DisplayGraph(impstartsec, graphstart);
				displaygraphtrackstart.getImp();
				impstartsec.draw();
				impstartsec.setTitle("Graph Start User MT");
				FileSaver fsB = new FileSaver(impstartsec);
				fsB.saveAsTiff(  parent.batchfolder + "//" + parent.parent.addToName + ".tif");
			}
			

			int MaxSeedLabel, MinSeedLabel;
			
		
			
			if (parent.Allstart.get(0).size() > 0) {
				
				 MaxSeedLabel = parent.Allstart.get(0).get(parent.Allstart.get(0).size() - 1).seedlabel;
				 MinSeedLabel = parent.Allstart.get(0).get(0).seedlabel;
				 
				final ArrayList<Trackproperties> first = parent.Allstart.get(0);

				Collections.sort(first, parent.Seedcomparetrack);

				
				for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

				

					double startlengthreal = 0;
					double startlengthpixel = 0;
					double growratestart = 0;
					double growrateend = 0;
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
										thirdDimension.get(frameindex).newpoint[0] * parent.calibrationX,
										thirdDimension.get(frameindex).newpoint[1] * parent.calibrationY };
								final double[] oldpointCal = new double[] {
										thirdDimension.get(frameindex).oldpoint[0] * parent.calibrationX,
										thirdDimension.get(frameindex).oldpoint[1] * parent.calibrationY };

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
									growratestart += lengthpixelperframe;

								}

								double[] currentlocationpixel = new double[parent.ndims];

								if (framenumber == parent.thirdDimensionsliderInit)
									currentlocationpixel = originalpoint;
								else
									currentlocationpixel = newpoint;

								double[] currentlocationreal = new double[parent.ndims];

								currentlocationreal = new double[] { currentlocationpixel[0] * parent.calibrationX,
										currentlocationpixel[1] * parent.calibrationY };

								ResultsMT startMT = new ResultsMT(framenumber, startlengthpixel, startlengthreal, seedID,
										currentlocationpixel, currentlocationreal, lengthpixelperframe, lengthrealperframe);

								parent.startlengthlist.add(startMT);

								startseedmap.put(seedID, growratestart);
								
							}
						}
					}
					
				
			

			
			}
			}
				

			

					if (parent.Allend.get(0).size() > 0) {

						MaxSeedLabel = parent.Allend.get(0).get(parent.Allend.get(0).size() - 1).seedlabel;
						MinSeedLabel = parent.Allend.get(0).get(0).seedlabel;
						
						for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {
						double endlengthreal = 0;
						double endlengthpixel = 0;
						double growratestart = 0;
						double growrateend = 0;
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
											thirdDimension.get(frameindex).newpoint[0] * parent.calibrationX,
											thirdDimension.get(frameindex).newpoint[1] * parent.calibrationY };
									final double[] oldpointCal = new double[] {
											thirdDimension.get(frameindex).oldpoint[0] * parent.calibrationX,
											thirdDimension.get(frameindex).oldpoint[1] * parent.calibrationY };

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
										
										growrateend += lengthpixelperframe;

									}

									double[] currentlocationpixel = new double[parent.ndims];

									if (framenumber == parent.thirdDimensionsliderInit)
										currentlocationpixel = originalpoint;
									else
										currentlocationpixel = newpoint;

									double[] currentlocationreal = new double[parent.ndims];

									currentlocationreal = new double[] { currentlocationpixel[0] * parent.calibrationX,
											currentlocationpixel[1] * parent.calibrationY };

									ResultsMT endMT = new ResultsMT(framenumber, endlengthpixel, endlengthreal, seedID,
											currentlocationpixel, currentlocationreal, lengthpixelperframe,
											lengthrealperframe);

									parent.endlengthlist.add(endMT);

									endseedmap.put(seedID, growrateend);

								}
							}
						}

					
						}
					
					
					
						}
						

						

					Iterator it = startseedmap.entrySet().iterator();

					while (it.hasNext()) {

						Map.Entry<Integer, Double> pair = (Map.Entry<Integer, Double>) it.next();

						int key = pair.getKey();
						double endrate = 0;
						double startrate = 0;

						if (endseedmap.containsKey(key) && endseedmap != null)
							endrate = endseedmap.get(key);
						if (startseedmap.containsKey(key) && startseedmap != null)
							startrate = startseedmap.get(key);

						String plusorminusend = (startrate > endrate) ? "Minus" : "Plus";
						String plusorminusstart = (startrate > endrate) ? "Plus" : "Minus";

						if (parent.seedmap.get(key) == WhichendSingle.start
								|| parent.seedmap.get(key) == WhichendSingle.end && parent.seedmap.get(key) != WhichendSingle.both) {
							plusorminusend = "Zeroend";
							plusorminusstart = "Zeroend";
						}

						PlusMinusSeed pmseedEndB = new PlusMinusSeed(key, plusorminusend);
						plusminusendlist.add(pmseedEndB);

						PlusMinusSeed pmseedEndA = new PlusMinusSeed(key, plusorminusstart);
						plusminusstartlist.add(pmseedEndA);
					}

					Iterator itend = endseedmap.entrySet().iterator();

					while (itend.hasNext()) {

						Map.Entry<Integer, Double> pair = (Map.Entry<Integer, Double>) itend.next();

						int key = pair.getKey();
						double endrate = 0;
						double startrate = 0;

						if (endseedmap.containsKey(key) && endseedmap != null)
							endrate = endseedmap.get(key);
						if (startseedmap.containsKey(key) && startseedmap != null)
							startrate = startseedmap.get(key);

						String plusorminusend;
						String plusorminusstart;
						if (parent.seedmap.get(key) == WhichendSingle.start
								|| parent.seedmap.get(key) == WhichendSingle.end && parent.seedmap.get(key) != WhichendSingle.both) {
							plusorminusend = "Zeroend";
							plusorminusstart = "Zeroend";

							PlusMinusSeed pmseedEndB = new PlusMinusSeed(key, plusorminusend);

							plusminusendlist.add(pmseedEndB);

							PlusMinusSeed pmseedEndA = new PlusMinusSeed(key, plusorminusstart);

							plusminusstartlist.add(pmseedEndA);

						}

					}
							
							
						
							
							
					//	FileSaver.SaveResults(parent.Allend, plusminusendlist, parent.endlengthlist, parent.parent.calibration,
					//			parent.batchfolder, parent.parent.addToName, parent.nf);
							
					//	FileSaver.SaveResults(parent.Allstart, plusminusstartlist, parent.startlengthlist, parent.parent.calibration,
					//			parent.batchfolder, parent.parent.addToName, parent.nf);
					
					if (parent.Allend.get(0).size() > 0) {

						MaxSeedLabel = parent.Allend.get(0).get(parent.Allend.get(0).size() - 1).seedlabel;
						MinSeedLabel = parent.Allend.get(0).get(0).seedlabel;
						
						for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {

				
				
					
				

		                for (int j = 0; j < plusminusendlist.size(); ++j){
						
						if (plusminusendlist.get(j).seedid == currentseed){
					
						try {
							File fichier = new File(
									parent.batchfolder + "//" + parent.parent.addToName + "SeedLabel" + currentseed + plusminusendlist.get(j).plusorminus + ".txt");

							FileWriter fw = new FileWriter(fichier);
							BufferedWriter bw = new BufferedWriter(fw);

							bw.write(
									"\tFrame\tLength (px)\tLength (real)\tiD\tCurrentPosX (px)\tCurrentPosY (px)\tCurrentPosX (real)\tCurrentPosY (real)"
											+ "\tdeltaL (px) \tdeltaL (real)  \tCalibrationX  \tCalibrationY  \tCalibrationT \n");

							for (int index = 0; index < parent.endlengthlist.size(); ++index) {

								if (parent.endlengthlist.get(index).seedid == currentseed) {
									
									if (index > 0
											&& parent.endlengthlist.get(index).currentpointpixel[0] != parent.endlengthlist
													.get(index - 1).currentpointpixel[0]
											&& parent.endlengthlist.get(index).currentpointpixel[1] != parent.endlengthlist
													.get(index - 1).currentpointpixel[1])
										

										bw.write("\t" + parent.nf.format(parent.endlengthlist.get(index).framenumber) + "\t" + "\t"
												+ parent.nf.format(parent.endlengthlist.get(index).totallengthpixel) + "\t"+ "\t"
												+ "\t"+ "\t" + parent.nf.format(parent.endlengthlist.get(index).totallengthreal)
												+ "\t" + "\t" + parent.nf.format(parent.endlengthlist.get(index).seedid)
												+ "\t" + "\t"
												+ parent.nf.format(parent.endlengthlist.get(index).currentpointpixel[0])
												+ "\t" + "\t"
												+ parent.nf.format(parent.endlengthlist.get(index).currentpointpixel[1])
												+ "\t" + "\t"
												+ parent.nf.format(parent.endlengthlist.get(index).currentpointreal[0])
												+ "\t" + "\t"
												+ parent.nf.format(parent.endlengthlist.get(index).currentpointreal[1])
												+ "\t" + "\t"
												+ parent.nf.format(parent.endlengthlist.get(index).lengthpixelperframe)
												+ "\t" + "\t"
												+ parent.nf.format(parent.endlengthlist.get(index).lengthrealperframe)
												+ "\t" + "\t"
												+ parent.nf.format(parent.calibrationX)  
												+ "\t" + "\t"
												+ parent.nf.format(parent.calibrationY)  
												+ "\t" + "\t"
												+ parent.nf.format(parent.calibrationZ) + 
												
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
				
			
			
				if (parent.Allstart.get(0).size() > 0) {
					
					 MaxSeedLabel = parent.Allstart.get(0).get(parent.Allstart.get(0).size() - 1).seedlabel;
					 MinSeedLabel = parent.Allstart.get(0).get(0).seedlabel;
				

					
					for (int currentseed = MinSeedLabel; currentseed < MaxSeedLabel + 1; ++currentseed) {



						for (int j = 0; j < plusminusstartlist.size(); ++j){
							
						if (plusminusstartlist.get(j).seedid == currentseed){
							
							
						

								try {
									File fichier = new File(parent.batchfolder + "//" + parent.parent.addToName + "SeedLabel" + currentseed
											+ plusminusstartlist.get(j).plusorminus + ".txt");

									FileWriter fw = new FileWriter(fichier);
									BufferedWriter bw = new BufferedWriter(fw);

									bw.write(
											"\tFrame\tLength (px)\tLength (real)\tiD\tCurrentPosX (px)\tCurrentPosY (px)\tCurrentPosX (real)\tCurrentPosY (real)"
													+ "\tdeltaL (px)  \tdeltaL (real)  \tCalibrationX  \tCalibrationY  \tCalibrationT \n");

									for (int index = 0; index < parent.startlengthlist.size(); ++index) {

										if (parent.startlengthlist.get(index).seedid == currentseed) {
											
											if (index > 0
													&& parent.startlengthlist
															.get(index).currentpointpixel[0] != parent.startlengthlist
																	.get(index - 1).currentpointpixel[0]
													&& parent.startlengthlist
															.get(index).currentpointpixel[1] != parent.startlengthlist
																	.get(index - 1).currentpointpixel[1])
											
												

												bw.write(
														"\t" + parent.nf.format(parent.startlengthlist.get(index).framenumber) + "\t" + "\t"
																+ parent.nf.format(
																		parent.startlengthlist.get(index).totallengthpixel)
																+ "\t" + "\t"
																+ parent.nf.format(
																		parent.startlengthlist.get(index).totallengthreal)
																+ "\t" + "\t"
																+ parent.nf.format(parent.startlengthlist.get(index).seedid)
																+ "\t" + "\t"
																+ parent.nf.format(
																		parent.startlengthlist.get(index).currentpointpixel[0])
																+ "\t" + "\t"
																+ parent.nf.format(
																		parent.startlengthlist.get(index).currentpointpixel[1])
																+ "\t" + "\t"
																+ parent.nf.format(
																		parent.startlengthlist.get(index).currentpointreal[0])
																+ "\t" + "\t"
																+ parent.nf.format(
																		parent.startlengthlist.get(index).currentpointreal[1])
																+ "\t" + "\t"
																+ parent.nf.format(
																		parent.startlengthlist.get(index).lengthpixelperframe)
																+ "\t" + "\t"
																+ parent.nf.format(
																		parent.startlengthlist.get(index).lengthrealperframe)
																+ "\t" + "\t"
																+  parent.nf.format(parent.calibrationX)  
																+ "\t" + "\t"
																+ parent.nf.format(parent.calibrationY)  
																+ "\t" + "\t"
																+ parent.nf.format(parent.calibrationZ) + 
																
														
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
					
			
			if (parent.lengthtimestart != null)
				parent.lengthtime = parent.lengthtimestart;
			else
				parent.lengthtime = parent.lengthtimeend;
			

			parent.displaystack();
			if (parent.displayoverlay) {
				parent.prestack.deleteLastSlice();
				new ImagePlus(parent.parent.addToName, parent.prestack).show();
			}

			parent.frame.dispose();
			DisplayID.displayseeds(parent.parent.addToName,Views.hyperSlice(parent.originalimg, 2, 0), parent.IDALL);
			
		}
}

	public void stop(){
        exit = true;
    }
}
