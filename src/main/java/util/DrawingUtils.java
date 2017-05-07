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
import net.imglib2.KDTree;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.componenttree.mser.Mser;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import peakFitter.SortListbyproperty;

public class DrawingUtils {

	

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
	
	
	public static ArrayList<double[]> OriginalPoints(Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>, 
			Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> returnVector){
		
		ArrayList<double[]> Originalpoints = new ArrayList<double[]>();
		
		for (int index = 0; index < returnVector.getA().getA().size(); ++index) {

			Trackproperties vector = returnVector.getA().getA().get(index);
			
			Originalpoints.add(new double[]{vector.originalpoint[0], vector.originalpoint[1]});
			
		}
		
		for (int index = 0; index < returnVector.getA().getB().size(); ++index) {

			Trackproperties vector = returnVector.getA().getB().get(index);
			
			Originalpoints.add(new double[]{vector.originalpoint[0], vector.originalpoint[1]});
			
		}
		
		return Originalpoints;
		
	}
	
	public static ArrayList<double[]> OriginalPointsKalman(Pair<Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>>, 
			Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>> returnVector){
		
		ArrayList<double[]> Originalpoints = new ArrayList<double[]>();
		
		for (int index = 0; index < returnVector.getA().getA().size(); ++index) {

			KalmanTrackproperties vector = returnVector.getA().getA().get(index);
			
			Originalpoints.add(new double[]{vector.originalpoint[0], vector.originalpoint[1]});
			
		}
		
		for (int index = 0; index < returnVector.getA().getB().size(); ++index) {

			KalmanTrackproperties vector = returnVector.getA().getB().get(index);
			
			Originalpoints.add(new double[]{vector.originalpoint[0], vector.originalpoint[1]});
			
		}
		
		return Originalpoints;
		
	}
	
	public static void Trackplot(int detcount, Pair<Pair<ArrayList<Trackproperties>, ArrayList<Trackproperties>>, 
			Pair<ArrayList<Indexedlength>, ArrayList<Indexedlength>>> returnVector,
			Pair<ArrayList<Trackproperties>,ArrayList<Indexedlength>> returnVectorUser,
			HashMap<Integer, ArrayList<Roi>> AllpreviousRois,
			Color colorLineTrack, Color colorTrack, Color inactiveColor, Overlay overlay, int maxghost) {


		ArrayList<Roi> AllselectedRoi = new ArrayList<Roi>();
		double strokewidth = 0.05;
		if(returnVector!=null){
			
			
			
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
		
		if (returnVectorUser!=null){
		for (int index = 0; index < returnVectorUser.getA().size(); ++index){
			
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

		if (detcount <= maxghost){
			for (int i = 1; i <= detcount; ++i) {

				for (int index = 0; index < AllpreviousRois.get(i).size(); ++index) {

					overlay.add(AllpreviousRois.get(i).get(index));
					
					

				}

			}
		}
		else{
			for (int i = 1; i <= detcount; ++i) {
			for (int index = 0; index < AllpreviousRois.get(i).size(); ++index) {
				Roi roi = AllpreviousRois.get(i).get(index);
			overlay.remove(roi);
            
			
			}
			}
			for (int i = detcount - maxghost + 1; i <= detcount; ++i) {
			for (int index = 0; index < AllpreviousRois.get(i).size(); ++index){
				
				Roi oldroi = AllpreviousRois.get(i).get(index);
				oldroi.setStrokeColor(colorTrack);
				
				overlay.add(oldroi);
				
				
			}
			
		}

            
		
		}
	
	
		
		}

	

	
}
	
	
	
	public static void TrackplotKalman(int Kalmancount, Pair<Pair<ArrayList<KalmanTrackproperties>, ArrayList<KalmanTrackproperties>>, Pair<ArrayList<KalmanIndexedlength>, ArrayList<KalmanIndexedlength>>> returnVector,
			HashMap<Integer, ArrayList<Roi>> AllpreviousRois,
			Color colorLineTrack, Color colorTrack, Overlay overlay, int maxghost) {



		
		
		Kalmancount++;
		ArrayList<Roi> AllselectedRoi = new ArrayList<Roi>();
		
		if(returnVector!=null){
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
		AllpreviousRois.put(Kalmancount, AllselectedRoi);

		if (Kalmancount <= maxghost){
			for (int i = 1; i < Kalmancount; ++i) {

				for (int index = 0; index < AllpreviousRois.get(i).size(); ++index) {

					overlay.add(AllpreviousRois.get(i).get(index));
					
					

				}

			}
		}
		else{
			for (int i = 1; i < Kalmancount; ++i) {
			for (int index = 0; index < AllpreviousRois.get(i).size(); ++index) {
				Roi roi = AllpreviousRois.get(i).get(index);
			overlay.remove(roi);

			
			}
			}
			for (int i = Kalmancount - maxghost + 2; i < Kalmancount; ++i) {
			for (int index = 0; index < AllpreviousRois.get(i).size(); ++index){
				
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
			 
			 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
			 

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
	
	
	public static ArrayList<EllipseRoi> getcurrentRois(MserTree<UnsignedByteType> newtree, ArrayList<double[]> AllmeanCovar) {

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

			EllipseRoi roi = util.DrawingUtils.createEllipse(mean, covar, 3);

			Allrois.add(roi);

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
			 
			 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
			 

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
