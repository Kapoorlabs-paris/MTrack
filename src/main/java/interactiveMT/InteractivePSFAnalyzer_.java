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
package interactiveMT;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import fiji.tool.SliceObserver;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.RoiListener;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import psf_Tookit.GaussianFitParam;

public class InteractivePSFAnalyzer_ implements PlugIn {

	
	
	String usefolder = IJ.getDirectory("imagej");
	
	public Rectangle standardRectangle;
	RandomAccessibleInterval<FloatType> originalimgA;
	final int scrollbarSize = 1000;
	float sigma = 0.5f;
	float sigma2 = 0.5f;
	float deltaMin = 0;
	ColorProcessor cp = null;
	RoiListener bigroiListener;
	boolean displayoverlay = true;
	ImageStack prestack;
	float threshold = 1f;
	float thresholdMin = 0f;
	float thresholdMax = 1f;
	int thresholdInit = 1;
	ResultsTable rt = new ResultsTable();
	float thresholdHoughMin = 0f;
	float thresholdHoughMax = 1f;
	int thresholdHoughInit = 1;

	Roi nearestRoiCurr;
	ArrayList<GaussianFitParam> resultlist = new ArrayList<GaussianFitParam>();
	float radius = 50f;
	float radiusMin = 1f;
	float radiusMax = 30f;
	int radiusInit = 200;

	ArrayList<Pair<Integer, Roi>> AllSelectedrois;
	ArrayList<Pair<Integer, Roi>> AllSelectedoldrois;
	HashMap<Integer, Roi> AllOldrois;
	ArrayList<Pair<Integer, double[]>> AllSelectedcenter;
	public float minDiversity = 1;
	// steps per octave
	public static int standardSensitivity = 4;
	int sensitivity = standardSensitivity;
	JFrame frame = new JFrame();
	JPanel panel = new JPanel();
	float imageSigma = 0.5f;
	float sigmaMin = 0.5f;
	float sigmaMax = 100f;
	float sizeXMin = 0;
	float sizeYMin = 0;
	boolean isStarted = false;
	RoiListener roiListener;
	SliceObserver sliceObserver;
	float sizeXMax = 100f;
	float sizeYMax = 100f;
	int sigmaInit = 30;
	boolean pointinRoi;
	boolean findBlobsViaMSER = false;
	boolean findBlobsViaDOG = false;
	boolean isComputing = false;
	boolean lookForMaxima = true;
	boolean lookForMinima = false;
	boolean showMSER = false;
	boolean showDOG = false;
	boolean enableSigma2 = false;
	float delta = 1f;
	public float maxVar = 1;
	float deltaMax = 400f;
	float maxVarMin = 0;
	float maxVarMax = 1;
	public int minDiversityInit = 1;
	int minSizeInit = 1;
	int maxSizeInit = 100;
	ArrayList<double[]> AllmeanCovar;
	Overlay overlay;
	int deltaInit = 10;
	int maxVarInit = 1;
	int thirdDimension;
	int thirdDimensionSize = 0;
	float minDiversityMin = 0;
	float minDiversityMax = 1;
	int thirdDimensionslider = 1;
	int thirdDimensionsliderInit = 1;
	
	HashMap<Integer, double[]> ClickedPoints = new HashMap<Integer, double[]>();
	int timeMin = 1;
	long minSize = 1;
	long maxSize = 1000;
	long minSizemin = 0;
	long minSizemax = 100;
	long maxSizemin = 100;
	long maxSizemax = 10000;
	float initialSearchradius = 10;
	float maxSearchradius = 15;
	public int maxSearchradiusInit = (int) maxSearchradius;
	public float maxSearchradiusMin = 10;
	public float maxSearchradiusMax = 500;
	
	ArrayList<RefinedPeak<Point>> peaks;
	RandomAccessibleInterval<FloatType> currentimg;
	FinalInterval interval;
	Roi nearestRoi;
	Roi nearestoriginalRoi;
	Color colorSelect = Color.red;
	Color coloroutSelect = Color.CYAN;
	Color colorCreate = Color.red;
	Color colorDraw = Color.green;
	Color colorKDtree = Color.blue;
	Color colorOld = Color.MAGENTA;
	Color colorPrevious = Color.gray;
	Color colorFinal = Color.YELLOW;
	Color colorRadius = Color.yellow;
	boolean savefile;
	Roi selectedRoi;
	
	ImagePlus imp;
	MouseListener ml, mlnew;
	MouseListener removeml;
	RandomAccessibleInterval<FloatType> CurrentView;
	ArrayList<Roi> Rois;
	MserTree<UnsignedByteType> newtree;
	RandomAccessibleInterval<UnsignedByteType> newimg;

	int cellcount = 0;
	
	int length = 0;
	int height = 0;
    int maxghost = 5;
	int radiusSliderinit = 5;

	public  InteractivePSFAnalyzer_(final RandomAccessibleInterval<FloatType> originalimgA) {
		this.originalimgA = originalimgA;
		standardRectangle = new Rectangle(1, 1, (int) originalimgA.dimension(0) - 2,
				(int) originalimgA.dimension(1) - 2);

	}
	
	public  InteractivePSFAnalyzer_(){};
	
	@Override
	public void run(String arg) {
		// TODO Auto-generated method stub
		
	}

}
