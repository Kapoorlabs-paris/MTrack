package util;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import graphconstructs.KalmanTrackproperties;
import graphconstructs.Trackproperties;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.process.ColorProcessor;
import kdTreeBlobs.FlagNode;
import kdTreeBlobs.NNFlagsearchKDtree;
import labeledObjects.Indexedlength;
import labeledObjects.KalmanIndexedlength;
import mpicbg.imglib.util.Util;
import net.imglib2.KDTree;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.componenttree.mser.Mser;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import peakFitter.SortListbyproperty;

public class DrawingUtils {

	
	
	
	public static ArrayList<Roi> getcurrentDoGRois(ArrayList<RefinedPeak<Point>> peaks, double sigma, double sigma2) {

		ArrayList<Roi> Allrois = new ArrayList<Roi>();

		for (final RefinedPeak<Point> peak : peaks) {
			float x = (float) (peak.getFloatPosition(0));
			float y = (float) (peak.getFloatPosition(1));

			final OvalRoi or = new OvalRoi(Util.round(x - sigma), Util.round(y - sigma), Util.round(sigma + sigma2),
					Util.round(sigma + sigma2));

			Allrois.add(or);

		}

		return Allrois;

	}

	
	/**
	 * 2D correlated Gaussian
	 * 
	 * @param mean
	 *            (x,y) components of mean vector
	 * @param cov
	 *            (xx, xy, yy) components of covariance matrix
	 * @return ImageJ roi
	 */
	public static EllipseRoi createEllipse(final double[] mean, final double[] cov, final double nsigmas) {

		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);
		final double scale1 = Math.sqrt(0.5 * (a + c + d)) * nsigmas;
		final double scale2 = Math.sqrt(0.5 * (a + c - d)) * nsigmas;
		final double theta = 0.5 * Math.atan2((2 * b), (a - c));
		final double x = mean[0];
		final double y = mean[1];
		final double dx = scale1 * Math.cos(theta);
		final double dy = scale1 * Math.sin(theta);
		final EllipseRoi ellipse = new EllipseRoi(x - dx, y - dy, x + dx, y + dy, scale2 / scale1);

		return ellipse;
	}

	/**
	 * 2D correlated Gaussian
	 * 
	 * @param mean
	 *            (x,y) components of mean vector
	 * @param cov
	 *            (xx, xy, yy) components of covariance matrix
	 * @return ImageJ roi
	 */
	public static EllipseRoi createEllipseInt(final double[] mean, final double[] cov, final double nsigmas, final RandomAccessibleInterval<IntType> intimg) {

		final double a = cov[0];
		final double b = cov[1];
		final double c = cov[2];
		final double d = Math.sqrt(a * a + 4 * b * b - 2 * a * c + c * c);
		final double scale1 = Math.sqrt(0.5 * (a + c + d)) * nsigmas;
		final double scale2 = Math.sqrt(0.5 * (a + c - d)) * nsigmas;
		final double theta = 0.5 * Math.atan2((2 * b), (a - c));
		final double x = mean[0];
		final double y = mean[1];
		final double dx = scale1 * Math.cos(theta);
		final double dy = scale1 * Math.sin(theta);
		double xnew = x;
		double ynew = y;
		final int xint = (int) Math.round(x);
		final int yint = (int) Math.round(y);
		
		RandomAccess<IntType> intranac = intimg.randomAccess();
		 intranac.setPosition(new int[]{xint, yint});
		int label = intranac.get().get();
		
		long[] minCorners = Boundingboxes.GetMaxcorners(intimg, label);
		long[] maxCorners = Boundingboxes.GetMincorners(intimg, label);
		
		double largeEigen = (a + c + d) / 2;
		double[] tipA = {mean[0] + 0.5 * largeEigen / Math.sqrt(4 * b * b + Math.pow(c- a + d, 2) )* 2* b, mean[1] + 0.5 * largeEigen / Math.sqrt(4 * b * b + Math.pow(c- a + d, 2) )* (c - a + d) };
		double[] tipB = {mean[0] - 0.5 * largeEigen / Math.sqrt(4 * b * b + Math.pow(c- a + d, 2) )* 2* b, mean[1] - 0.5 * largeEigen / Math.sqrt(4 * b * b + Math.pow(c- a + d, 2) )* (c - a + d) };

		if (tipB[0] <= minCorners[0] || tipB[1] <= minCorners[1]){
			
			xnew = mean[0] -  (minCorners[0] - tipB[0]);
		    ynew = (c - a + d)/ (2*b) * xnew + y - x * (c - a + d) / (2 * b);
		}
		  
       if (tipA[0] >= maxCorners[0] || tipA[1] >= maxCorners[1]){
			
			xnew = mean[0] -  (maxCorners[0] - tipA[0]);
		    ynew = (c - a + d)/ (2*b) * xnew + y - x * (c - a + d) / (2 * b);
		}
		    
		final EllipseRoi ellipse = new EllipseRoi(xnew - dx, ynew - dy, xnew + dx, ynew + dy, scale2 / scale1);

		
		
		
		return ellipse;
	}

	
	public static ArrayList<double[]> OriginalPoints(
			Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>, Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> returnVector) {

		ArrayList<double[]> Originalpoints = new ArrayList<double[]>();

		for (int index = 0; index < returnVector.getA().getA().size(); ++index) {

			Trackproperties vector = returnVector.getA().getA().get(index);

			Originalpoints.add(new double[] { vector.originalpoint[0], vector.originalpoint[1] });

		}

		for (int index = 0; index < returnVector.getA().getB().size(); ++index) {

			Trackproperties vector = returnVector.getA().getB().get(index);

			Originalpoints.add(new double[] { vector.originalpoint[0], vector.originalpoint[1] });

		}

		return Originalpoints;

	}

	public static ArrayList<double[]> OriginalPointsKalman(
			Pair<Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>>, Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>> returnVector) {

		ArrayList<double[]> Originalpoints = new ArrayList<double[]>();

		for (int index = 0; index < returnVector.getA().getA().size(); ++index) {

			KalmanTrackproperties vector = returnVector.getA().getA().get(index);

			Originalpoints.add(new double[] { vector.originalpoint[0], vector.originalpoint[1] });

		}

		for (int index = 0; index < returnVector.getA().getB().size(); ++index) {

			KalmanTrackproperties vector = returnVector.getA().getB().get(index);

			Originalpoints.add(new double[] { vector.originalpoint[0], vector.originalpoint[1] });

		}

		return Originalpoints;

	}

	public static void Trackplot(int detcount,
			Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>, Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> returnVector,
			Pair<ArrayList<Trackproperties>, ArrayList<Indexedlength>> returnVectorUser,
			HashMap<Integer, ArrayList<Roi>> AllpreviousRois, Color colorLineTrack, Color colorTrack,
			Color inactiveColor, Overlay overlay, int maxghost) {

		ArrayList<Roi> AllselectedRoi = new ArrayList<Roi>();
		double strokewidth = 0.05;
		if (returnVector != null) {

			for (int index = 0; index < returnVector.getA().getA().size(); ++index) {

				Trackproperties vector = returnVector.getA().getA().get(index);

				PointRoi selectedRoi = new PointRoi(vector.newpoint[0], vector.newpoint[1]);
				Line selectedLineRoi = new Line(vector.newpoint[0], vector.newpoint[1], vector.oldpoint[0],
						vector.oldpoint[1]);
				selectedLineRoi.setStrokeColor(colorLineTrack);
				selectedLineRoi.setStrokeWidth(strokewidth);
				overlay.add(selectedLineRoi);

				AllselectedRoi.add(selectedRoi);

			}

			for (int index = 0; index < returnVector.getA().getB().size(); ++index) {

				Trackproperties vector = returnVector.getA().getB().get(index);

				PointRoi selectedRoi = new PointRoi(vector.newpoint[0], vector.newpoint[1]);
				Line selectedLineRoi = new Line(vector.newpoint[0], vector.newpoint[1], vector.oldpoint[0],
						vector.oldpoint[1]);
				selectedLineRoi.setStrokeColor(colorLineTrack);
				selectedLineRoi.setStrokeWidth(strokewidth);
				overlay.add(selectedLineRoi);

				AllselectedRoi.add(selectedRoi);

			}

			if (returnVectorUser != null) {
				for (int index = 0; index < returnVectorUser.getA().size(); ++index) {

					Trackproperties vector = returnVectorUser.getA().get(index);

					PointRoi selectedRoi = new PointRoi(vector.newpoint[0], vector.newpoint[1]);
					Line selectedLineRoi = new Line(vector.newpoint[0], vector.newpoint[1], vector.oldpoint[0],
							vector.oldpoint[1]);
					selectedLineRoi.setStrokeColor(colorLineTrack);
					selectedLineRoi.setStrokeWidth(strokewidth);
					overlay.add(selectedLineRoi);

					AllselectedRoi.add(selectedRoi);

				}
			}

			AllpreviousRois.put(detcount, AllselectedRoi);

			if (detcount <= maxghost) {
				for (int i = 1; i <= detcount; ++i) {

					for (int index = 0; index < AllpreviousRois.get(i).size(); ++index) {

						overlay.add(AllpreviousRois.get(i).get(index));

					}

				}
			} else {
				for (int i = 1; i <= detcount; ++i) {
					for (int index = 0; index < AllpreviousRois.get(i).size(); ++index) {
						Roi roi = AllpreviousRois.get(i).get(index);
						overlay.remove(roi);

					}
				}
				for (int i = detcount - maxghost + 1; i <= detcount; ++i) {
					for (int index = 0; index < AllpreviousRois.get(i).size(); ++index) {

						Roi oldroi = AllpreviousRois.get(i).get(index);
						oldroi.setStrokeColor(colorTrack);

						overlay.add(oldroi);

					}

				}

			}

		}

	}

	public static void TrackplotKalman(int detcount,
			Pair<Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>>, 
			Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>> returnVector,
			Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanIndexedlength>> returnVectorUser,
			HashMap<Integer, ArrayList<Roi>> AllpreviousRois, Color colorLineTrack, Color colorTrack,
			Color inactiveColor, Overlay overlay, int maxghost) {

		ArrayList<Roi> AllselectedRoi = new ArrayList<Roi>();

		if (returnVector != null) {
			for (int index = 0; index < returnVector.getA().getA().size(); ++index) {

				KalmanTrackproperties vector = returnVector.getA().getA().get(index);

				PointRoi selectedRoi = new PointRoi(vector.currentpoint[0], vector.currentpoint[1]);

				AllselectedRoi.add(selectedRoi);

			}

			for (int index = 0; index < returnVector.getA().getB().size(); ++index) {

				KalmanTrackproperties vector = returnVector.getA().getB().get(index);

				PointRoi selectedRoi = new PointRoi(vector.currentpoint[0], vector.currentpoint[1]);

				AllselectedRoi.add(selectedRoi);

			}
			AllpreviousRois.put(detcount, AllselectedRoi);

			if (detcount <= maxghost) {
				for (int i = 1; i < detcount; ++i) {

					for (int index = 0; index < AllpreviousRois.get(i).size(); ++index) {

						overlay.add(AllpreviousRois.get(i).get(index));

					}

				}
			} else {
				for (int i = 1; i < detcount; ++i) {
					for (int index = 0; index < AllpreviousRois.get(i).size(); ++index) {
						Roi roi = AllpreviousRois.get(i).get(index);
						overlay.remove(roi);

					}
				}
				for (int i = detcount - maxghost + 2; i < detcount; ++i) {
					for (int index = 0; index < AllpreviousRois.get(i).size(); ++index) {

						Roi oldroi = AllpreviousRois.get(i).get(index);
						oldroi.setStrokeColor(colorTrack);

						overlay.add(oldroi);

					}

				}

			}

		}

	}

	public static OvalRoi getNearestRois(ArrayList<OvalRoi> Allrois, double[] Clickedpoint) {

		OvalRoi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<OvalRoi>> targetNodes = new ArrayList<FlagNode<OvalRoi>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			Roi r = Allrois.get(index);
			Rectangle rect = r.getBounds();

			targetCoords.add(new RealPoint(rect.x + rect.width / 2.0, rect.y + rect.height / 2.0));

			targetNodes.add(new FlagNode<OvalRoi>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<OvalRoi>> Tree = new KDTree<FlagNode<OvalRoi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<OvalRoi> Search = new NNFlagsearchKDtree<OvalRoi>(Tree);

			final double[] source = Clickedpoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<OvalRoi> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
	}

	public static ArrayList<EllipseRoi> getcurrentRois(MserTree<UnsignedByteType> newtree,
			ArrayList<double[]> AllmeanCovar) {

		ArrayList<double[]> meanandcovchildlist = new ArrayList<double[]>();
		ArrayList<double[]> meanandcovlist = new ArrayList<double[]>();
		ArrayList<double[]> redmeanandcovlist = new ArrayList<double[]>();
		final HashSet<Mser<UnsignedByteType>> rootset = newtree.roots();
		
		
		final Iterator<Mser<UnsignedByteType>> rootsetiterator = rootset.iterator();
		
		
		AllmeanCovar = new ArrayList<double[]>();
		
		while (rootsetiterator.hasNext()) {

			Mser<UnsignedByteType> rootmser = rootsetiterator.next();

			if (rootmser.size() > 0) {

				final double[] meanandcov = { rootmser.mean()[0], rootmser.mean()[1], rootmser.cov()[0],
						rootmser.cov()[1], rootmser.cov()[2] };
				meanandcovlist.add(meanandcov);
			}
		}
		
		// We do this so the ROI remains attached the the same label and is not changed if the program is run again
	   
	       final Iterator<Mser<UnsignedByteType>> treeiterator = newtree.iterator();
	       
	       while (treeiterator.hasNext()) {

				Mser<UnsignedByteType> mser = treeiterator.next();
				//System.out.println(mser.getChildren().size());
				if (mser.getChildren().size()  > 1) {

					for (int index = 0; index < mser.getChildren().size(); ++index) {

						final double[] meanandcovchild = { mser.getChildren().get(index).mean()[0],
								mser.getChildren().get(index).mean()[1], mser.getChildren().get(index).cov()[0],
								mser.getChildren().get(index).cov()[1], mser.getChildren().get(index).cov()[2] };

						meanandcovchildlist.add(meanandcovchild);
						AllmeanCovar.add(meanandcovchild);
						
					}

				}

			}
	       redmeanandcovlist = meanandcovlist;
	       
	       /*
	        * Remove parent, not always a good idea
	        * 
			for (int childindex = 0; childindex < meanandcovchildlist.size(); ++childindex) {

				final double[] meanchild = new double[] { meanandcovchildlist.get(childindex)[0],
						meanandcovchildlist.get(childindex)[1] };

				for (int index = 0; index < meanandcovlist.size(); ++index) {

					final double[] mean = new double[] { meanandcovlist.get(index)[0], meanandcovlist.get(index)[1] };
					final double[] covar = new double[] { meanandcovlist.get(index)[2], meanandcovlist.get(index)[3],
							meanandcovlist.get(index)[4] };
					final EllipseRoi ellipse = createEllipse(mean, covar, 3);

					if (ellipse.contains((int) meanchild[0], (int) meanchild[1]))
						redmeanandcovlist.remove(index);

				}

			}
			*/

			for (int index = 0; index < redmeanandcovlist.size(); ++index) {

				final double[] meanandcov = new double[] { redmeanandcovlist.get(index)[0], redmeanandcovlist.get(index)[1],
						redmeanandcovlist.get(index)[2], redmeanandcovlist.get(index)[3], redmeanandcovlist.get(index)[4] };
				AllmeanCovar.add(meanandcov);

			}

		ArrayList<EllipseRoi> Allrois = new ArrayList<EllipseRoi>();

		
		for (int index = 0; index < AllmeanCovar.size(); ++index) {

			final double[] mean = { AllmeanCovar.get(index)[0], AllmeanCovar.get(index)[1] };
			final double[] covar = { AllmeanCovar.get(index)[2], AllmeanCovar.get(index)[3],
					AllmeanCovar.get(index)[4] };

			EllipseRoi roi = util.DrawingUtils.createEllipse(mean, covar, 3);

			Allrois.add(roi);

		}

		return Allrois;
	}

	public static ArrayList<EllipseRoi> getcurrentRoisInt(MserTree<UnsignedByteType> newtree,
			ArrayList<double[]> AllmeanCovar, final RandomAccessibleInterval<IntType> intimg) {

		final HashSet<Mser<UnsignedByteType>> rootset = newtree.roots();

		ArrayList<EllipseRoi> Allrois = new ArrayList<EllipseRoi>();

		ArrayList<EllipseRoi> Allroiscopy = new ArrayList<EllipseRoi>();
		final Iterator<Mser<UnsignedByteType>> rootsetiterator = rootset.iterator();

		AllmeanCovar = new ArrayList<double[]>();

		while (rootsetiterator.hasNext()) {

			Mser<UnsignedByteType> rootmser = rootsetiterator.next();

			if (rootmser.size() > 0) {

				final double[] meanandcov = { rootmser.mean()[0], rootmser.mean()[1], rootmser.cov()[0],
						rootmser.cov()[1], rootmser.cov()[2] };
				AllmeanCovar.add(meanandcov);

			}
		}

		// We do this so the ROI remains attached the the same label and is not
		// changed if the program is run again
		SortListbyproperty.sortpointList(AllmeanCovar);
		for (int index = 0; index < AllmeanCovar.size(); ++index) {

			final double[] mean = { AllmeanCovar.get(index)[0], AllmeanCovar.get(index)[1] };
			final double[] covar = { AllmeanCovar.get(index)[2], AllmeanCovar.get(index)[3],
					AllmeanCovar.get(index)[4] };

			EllipseRoi roi = util.DrawingUtils.createEllipseInt(mean, covar, 3, intimg);

			Allrois.add(roi);

		}

		return Allrois;

	}
	
	
	public static ArrayList<EllipseRoi> getcurrentRois(ArrayList<MserTree<UnsignedByteType>> newtree,
			ArrayList<double[]> AllmeanCovar) {

		AllmeanCovar = new ArrayList<double[]>();
		ArrayList<EllipseRoi> Allrois = new ArrayList<EllipseRoi>();

		for (int indexx = 0; indexx < newtree.size(); ++indexx) {

			final HashSet<Mser<UnsignedByteType>> rootset = newtree.get(indexx).roots();

			final Iterator<Mser<UnsignedByteType>> rootsetiterator = rootset.iterator();

			while (rootsetiterator.hasNext()) {

				Mser<UnsignedByteType> rootmser = rootsetiterator.next();

				if (rootmser.size() > 0) {

					final double[] meanandcov = { rootmser.mean()[0], rootmser.mean()[1], rootmser.cov()[0],
							rootmser.cov()[1], rootmser.cov()[2] };
					AllmeanCovar.add(meanandcov);

				}
			}

			// We do this so the ROI remains attached the the same label and is
			// not
			// changed if the program is run again
			SortListbyproperty.sortpointList(AllmeanCovar);
			for (int index = 0; index < AllmeanCovar.size(); ++index) {

				final double[] mean = { AllmeanCovar.get(index)[0], AllmeanCovar.get(index)[1] };
				final double[] covar = { AllmeanCovar.get(index)[2], AllmeanCovar.get(index)[3],
						AllmeanCovar.get(index)[4] };

				EllipseRoi roi = util.DrawingUtils.createEllipse(mean, covar, 3);

				Allrois.add(roi);

			}
		}
		return Allrois;

	}

	public static OvalRoi getNearestRoisPair(ArrayList<Pair<double[], OvalRoi>> Allrois, double[] Clickedpoint) {
		OvalRoi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<OvalRoi>> targetNodes = new ArrayList<FlagNode<OvalRoi>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			Roi r = Allrois.get(index).getB();
			Rectangle rect = r.getBounds();

			targetCoords.add(new RealPoint(rect.x + rect.width / 2.0, rect.y + rect.height / 2.0));

			targetNodes.add(new FlagNode<OvalRoi>(Allrois.get(index).getB()));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<OvalRoi>> Tree = new KDTree<FlagNode<OvalRoi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<OvalRoi> Search = new NNFlagsearchKDtree<OvalRoi>(Tree);

			final double[] source = Clickedpoint;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<OvalRoi> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
	}

}
