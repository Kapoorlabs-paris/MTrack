package parallelization;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import LineModels.UseLineModel.UserChoiceModel;
import MTObjects.MTcounter;
import MTObjects.ResultsMT;
import graphconstructs.Trackproperties;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.EllipseRoi;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.Opener;
import interactiveMT.BatchMode;
import interactiveMT.Interactive_MTSingleChannel.ValueChange;
import interactiveMT.SingleBatchMode;
import interactiveMT.Interactive_MTSingleChannel.Whichend;
import labeledObjects.CommonOutputHF;
import labeledObjects.Indexedlength;
import lineFinder.FindlinesVia;
import lineFinder.LinefinderInteractiveHough;
import lineFinder.LinefinderInteractiveMSER;
import lineFinder.LinefinderInteractiveMSERwHough;
import mpicbg.imglib.util.Util;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import peakFitter.FitterUtils;
import swingClasses.SingleTrackBatch;
import swingClasses.TrackBatch;

public class SplitSingleChannel implements Runnable  {

	private final File file;
	final SingleBatchMode parent;
	final int fileindex;

	public SplitSingleChannel(File file, SingleBatchMode parent, final int fileindex) {

		this.file = file;
		this.parent = parent;
		this.fileindex = fileindex;
	}
	
	
	

	@Override
	public void run()  {

		JProgressBar fileprogress = new JProgressBar();

		fileprogress.setIndeterminate(false);

		fileprogress.setMaximum(parent.AllImages.length);

		parent.label = new JLabel("Progress..");
		parent.frame = new JFrame();
		parent.panel = new JPanel();
		parent.panel.add(parent.label);
		parent.panel.add(fileprogress);
		parent.frame.add(parent.panel);
		parent.frame.pack();
		parent.frame.setSize(200, 200);
		parent.frame.setVisible(true);

		

			
			
			
			parent.parent.addToName = file.getName().replaceFirst("[.][^.]+$", "");
			System.out.println(parent.parent.addToName);
			double percent = Math.round(100 * (fileindex + 1) / (parent.AllImages.length - 1));

			FitterUtils.SetProgressBarTime(fileprogress, percent, (fileindex + 1), (parent.AllImages.length), "Processing File");

			if (parent.modelnumber == 1)
				parent.parent.userChoiceModel = UserChoiceModel.Line;
			if (parent.modelnumber == 2)
				parent.parent.userChoiceModel = UserChoiceModel.Splineordersec;
			else
				parent.parent.userChoiceModel = UserChoiceModel.Splineorderthird;
			parent.colorDraw = Color.red;
			parent.colorCurrent = Color.yellow;
			parent.colorTrack = Color.yellow;
			parent.colorLineTrack = Color.GRAY;
			parent.colorUnselect = Color.MAGENTA;
			parent.colorConfirm = Color.GREEN;
			parent.colorUser = Color.ORANGE;
			parent.seedmap = new HashMap<Integer, Whichend>();
			parent.Progressmin = 0;
			parent.Progressmax = 100;
			parent.max = parent.Progressmax;
			parent.deltadcutoff = 5;
			parent.deltadstart = new ArrayList<>();
			parent.deltadend = new ArrayList<>();
			parent.deltad = new ArrayList<>();
			parent.lengthtimestart = new ArrayList<double[]>();
			parent.lengthtimeuser = new ArrayList<double[]>();
			parent.AllMSERrois = new HashMap<Integer, ArrayList<EllipseRoi>>();
			parent.AllPoints = new HashMap<Integer, double[]>();

			parent.lengthtimeend = new ArrayList<double[]>();
			parent.lengthtime = new ArrayList<double[]>();
			parent.ALLcounts = new ArrayList<MTcounter>();
			parent.AllSeedrois = new ArrayList<OvalRoi>();
			parent.jpb = new JProgressBar();
			parent.newHoughtree = new HashMap<Integer, MserTree<UnsignedByteType>>();
			parent.Userframe = new ArrayList<Indexedlength>();
			parent.AllpreviousRois = new HashMap<Integer, ArrayList<Roi>>();
			parent.maxghost = 1;
			parent.whichend = new HashMap<Integer, Boolean>();
			parent.pixellength = new HashMap<Integer, Double>();
			parent.microlength = new HashMap<Integer, Double>();
			parent.finalvelocity = new ArrayList<float[]>();
			parent.finalvelocityKymo = new ArrayList<float[]>();
			parent.Allstart = new ArrayList<ArrayList<Trackproperties>>();
			parent.AllUser = new ArrayList<ArrayList<Trackproperties>>();
			parent.Allend = new ArrayList<ArrayList<Trackproperties>>();

			parent.startlengthlist = new ArrayList<ResultsMT>();
			parent.userlengthlist = new ArrayList<ResultsMT>();
			parent.endlengthlist = new ArrayList<ResultsMT>();
			parent.IDALL = new ArrayList<Pair<Integer, double[]>>();
			parent.ClickedPoints = new ArrayList<Pair<double[], OvalRoi>>();
			parent.nf = NumberFormat.getInstance(Locale.ENGLISH);
			parent.sumlengthpixel = 0;
			parent.sumlengthmicro = 0;
			parent.AllmeanCovar = new ArrayList<double[]>();
			parent.count = 0;
			parent.detcount = 0;
			parent.overlay = new Overlay();
			parent.nf.setMaximumFractionDigits(3);

		

			ImagePlus impB = new Opener().openImage(file.getPath());

			parent.originalimg = ImageJFunctions.convertFloat(impB);

			parent.originalPreprocessedimg = util.CopyUtils.Preprocess(parent.originalimg, parent.psf);

			parent.standardRectangle = new Rectangle(parent.inix, parent.iniy,
					(int) parent.originalimg.dimension(0) - 2 * parent.inix,
					(int) parent.originalimg.dimension(1) - 2 * parent.iniy);

			parent.userfile = file.getName().replaceFirst("[.][^.]+$", "");

			parent.parent.usefolder = Prefs.get(".Folder.file", IJ.getDirectory("imagej"));

			parent.parent.FindLinesViaMSER = Prefs.getBoolean(".FindLinesViaMSER.boolean", false);

			parent.parent.doSegmentation = Prefs.getBoolean(".doSegmentation.boolean", false);
			parent.parent.doMserSegmentation = Prefs.getBoolean(".doMserSegmentation.boolean", false);
			parent.parent.FindLinesViaHOUGH = Prefs.getBoolean(".FindLinesViaHough.boolean", false);
			parent.parent.FindLinesViaMSERwHOUGH = Prefs.getBoolean(".FindLinesViaMSERwHough.boolean", false);

			parent.parent.ShowMser = Prefs.getBoolean(".ShowMser.boolean", false);
			parent.parent.ShowHough = Prefs.getBoolean(".ShowHough.boolean", false);
			parent.parent.update = Prefs.getBoolean(".update.boolean", false);
			parent.parent.Canny = Prefs.getBoolean(".Canny.boolean", false);

			parent.parent.showDeterministic = Prefs.getBoolean(".showDeterministic.boolean", true);
			parent.parent.RoisViaMSER = Prefs.getBoolean(".RoiViaMSER.boolean", false);
			parent.parent.RoisViaWatershed = Prefs.getBoolean(".RoiViaWatershed.boolean", false);
			parent.parent.SaveTxt = Prefs.getBoolean(".SaveTxt.boolean", true);
			parent.calibration = new double[parent.originalimg.numDimensions() - 1];
			parent.calibration[0] = Prefs.getDouble(".CalibrationX.double", 1);
			parent.calibration[1] = Prefs.getDouble(".CalibrationY.double", 1);
			parent.AllSeedrois = new ArrayList<OvalRoi>();
			parent.newHoughtree = new HashMap<Integer, MserTree<UnsignedByteType>>();
			parent.Userframe = new ArrayList<Indexedlength>();
			parent.AllpreviousRois = new HashMap<Integer, ArrayList<Roi>>();
			parent.Inispacing = 0.5 * Math.min(parent.psf[0], parent.psf[1]);
			parent.count = 0;
			parent.overlay = new Overlay();
			parent.nf.setMaximumFractionDigits(3);

			parent.Cannyradius = (long) (parent.radiusfactor
					* Math.ceil(Math.sqrt(parent.psf[0] * parent.psf[0] + parent.psf[1] * parent.psf[1])));
			if (parent.originalimg.numDimensions() < 3) {

				parent.thirdDimensionSize = 0;
			}

			if (parent.originalimg.numDimensions() == 3) {

				parent.thirdDimension = 1;
				parent.startdim = 1;
				parent.thirdDimensionSize = (int) parent.originalimg.dimension(2);

			}

			if (parent.originalimg.numDimensions() > 3) {

				System.out.println("Image has wrong dimensionality, upload an XYT image");

			}

			parent.prestack = new ImageStack((int) parent.originalimg.dimension(0),
					(int) parent.originalimg.dimension(1), java.awt.image.ColorModel.getRGBdefault());

			parent.CurrentView = util.CopyUtils.getCurrentView(parent.originalimg, parent.thirdDimension,
					parent.thirdDimensionSize);
			parent.CurrentPreprocessedView = util.CopyUtils.getCurrentPreView(parent.originalPreprocessedimg,
					parent.thirdDimension, parent.thirdDimensionSize);

			parent.output = new ArrayList<CommonOutputHF>();
			parent.endStack = parent.thirdDimensionSize;
			parent.thirdDimensionSizeOriginal = parent.thirdDimensionSize;
			parent.preprocessedimp = ImageJFunctions.show(parent.CurrentView);

			Roi roi = parent.preprocessedimp.getRoi();

			if (roi == null) {
				// IJ.log( "A rectangular ROI is required to define the area..."
				// );
				parent.preprocessedimp.setRoi(parent.standardRectangle);
				roi = parent.preprocessedimp.getRoi();
			}

			// copy the ImagePlus into an ArrayImage<FloatType> for faster
			// access
			// displaySliders();

			parent.isStarted = true;

			// check whenever roi is modified to update accordingly

			parent.preprocessedimp.getCanvas().addMouseListener(parent.roiListener);

			parent.updatePreview(ValueChange.ALL);

			parent.jpb.setIndeterminate(false);

			parent.jpb.setMaximum(parent.max);
			parent.panel.add(parent.label);
			parent.panel.add(parent.jpb);
			parent.frame.add(parent.panel);

			RandomAccessibleInterval<FloatType> groundframe = parent.currentimg;
			RandomAccessibleInterval<FloatType> groundframepre = parent.currentPreprocessedimg;

			// Step 1 Locate the seeds and find the end points

			if (parent.parent.FindLinesViaMSER) {

				parent.updatePreview(ValueChange.SHOWMSER);

				LinefinderInteractiveMSER newlineMser = new LinefinderInteractiveMSER(groundframe, groundframepre,
						parent.newtree, parent.thirdDimension);

				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
						parent.thirdDimension, parent.psf, newlineMser, UserChoiceModel.Line, parent.Domask,
						parent.Intensityratio, parent.Inispacing, parent.jpb);

			}

			if (parent.parent.FindLinesViaHOUGH) {

				parent.updatePreview(ValueChange.SHOWHOUGH);
				LinefinderInteractiveHough newlineHough = new LinefinderInteractiveHough(groundframe, groundframepre,
						parent.intimg, parent.Maxlabel, parent.thetaPerPixel, parent.rhoPerPixel, parent.thirdDimension,
						parent.jpb);

				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
						parent.thirdDimension, parent.psf, newlineHough, UserChoiceModel.Line, parent.Domask,
						parent.Intensityratio, parent.Inispacing, parent.jpb);

			}

			if (parent.parent.FindLinesViaMSERwHOUGH) {
				parent.updatePreview(ValueChange.SHOWHOUGH);
				parent.updatePreview(ValueChange.SHOWMSER);
				LinefinderInteractiveMSERwHough newlineMserwHough = new LinefinderInteractiveMSERwHough(groundframe,
						groundframepre, parent.newtree, parent.thirdDimension, parent.thetaPerPixel, parent.rhoPerPixel,
						parent.jpb);

				parent.PrevFrameparam = FindlinesVia.LinefindingMethod(groundframe, groundframepre,
						parent.thirdDimension, parent.psf, newlineMserwHough, UserChoiceModel.Line, parent.Domask,
						parent.Intensityratio, parent.Inispacing, parent.jpb);

			}

			Overlay o = parent.preprocessedimp.getOverlay();

			if (parent.preprocessedimp.getOverlay() == null) {
				o = new Overlay();
				parent.preprocessedimp.setOverlay(o);
			}
			o.clear();
			for (int index = 0; index < parent.PrevFrameparam.getA().size(); ++index) {

				parent.Seedroi = new OvalRoi(
						Util.round(parent.PrevFrameparam.getA().get(index).currentpos[0] - parent.radiusseed),
						Util.round(parent.PrevFrameparam.getA().get(index).currentpos[1] - parent.radiusseed),
						Util.round(2 * parent.radiusseed), Util.round(2 * parent.radiusseed));
				parent.Seedroi.setStrokeColor(parent.colorConfirm);
				parent.Seedroi.setStrokeWidth(0.8);

				parent.AllSeedrois.add(parent.Seedroi);
				o.add(parent.Seedroi);

			}

			for (int index = 0; index < parent.PrevFrameparam.getB().size(); ++index) {

				parent.Seedroi = new OvalRoi(
						Util.round(parent.PrevFrameparam.getB().get(index).currentpos[0] - parent.radiusseed),
						Util.round(parent.PrevFrameparam.getB().get(index).currentpos[1] - parent.radiusseed),
						Util.round(2 * parent.radiusseed), Util.round(2 * parent.radiusseed));
				parent.Seedroi.setStrokeColor(parent.colorConfirm);
				parent.Seedroi.setStrokeWidth(0.8);

				parent.AllSeedrois.add(parent.Seedroi);
				o.add(parent.Seedroi);

			}
			for (int index = 0; index < parent.AllSeedrois.size(); ++index) {

				Rectangle rect = parent.AllSeedrois.get(index).getBounds();
				double newx = rect.x + rect.width / 2.0;
				double newy = rect.y + rect.height / 2.0;
				Pair<double[], OvalRoi> newpoint = new ValuePair<double[], OvalRoi>(new double[] { newx, newy },
						parent.AllSeedrois.get(index));

				parent.ClickedPoints.add(newpoint);
			}
			parent.preprocessedimp.updateAndDraw();

			// After the seed ends are found, the hash map fo both ends to be
			// tracked is created

			Collections.sort(parent.PrevFrameparam.getA(), parent.Seedcompare);
			Collections.sort(parent.PrevFrameparam.getB(), parent.Seedcompare);

			int minSeed = parent.PrevFrameparam.getA().get(0).seedLabel;
			int maxSeed = parent.PrevFrameparam.getA().get(parent.PrevFrameparam.getA().size() - 1).seedLabel;

			for (int i = 0; i < parent.PrevFrameparam.getA().size(); ++i) {

				Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(
						parent.PrevFrameparam.getA().get(i).seedLabel, parent.PrevFrameparam.getA().get(i).fixedpos);
				parent.IDALL.add(seedpair);
				parent.seedmap.put(parent.PrevFrameparam.getA().get(i).seedLabel, Whichend.start);

			}

			for (int i = 0; i < parent.PrevFrameparam.getB().size(); ++i) {

				Pair<Integer, double[]> seedpair = new ValuePair<Integer, double[]>(
						parent.PrevFrameparam.getB().get(i).seedLabel, parent.PrevFrameparam.getB().get(i).fixedpos);
				parent.IDALL.add(seedpair);
				if (parent.seedmap.get(parent.PrevFrameparam.getA().get(i).seedLabel) == Whichend.start)
					parent.seedmap.put(parent.PrevFrameparam.getA().get(i).seedLabel, Whichend.both);

			}

			// Now we track it from the first image in the dynamic channel to
			// the last

			int next = 2;

			SingleTrackBatch newtrack = new SingleTrackBatch(parent);
			newtrack.Trackobject(next, parent.thirdDimensionSize);

		

		}





		
		
	
	
}
