package interactiveMT;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

public class InteractiveKymoAnalyze_ implements PlugIn {

	final RandomAccessibleInterval<FloatType> Kymoimg;
	
	ImagePlus imp;
	int nbRois;
    Roi rorig = null;
    File fichier;
    ArrayList<float[]> Mask = new ArrayList<float[]>();
    ArrayList<float[]> Base = new ArrayList<float[]>();
    ArrayList<float[]> Length = new ArrayList<float[]>();
    

  	
	
    
	public InteractiveKymoAnalyze_ (final RandomAccessibleInterval<FloatType> Kymoimg , File fichier){
		
		this.Kymoimg = Kymoimg;
		this.fichier = fichier;
		
		
	}
	
	



	@Override
	public void run(String arg) {
		
		RoiManager roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
			roimanager.setVisible(true);
		}
		
		
		imp = ImageJFunctions.show(Kymoimg);
		
		Card();
		
	
	}

	public void MakeRois(){
		
	    RoiManager roimanager = RoiManager.getInstance();
		rorig = imp.getRoi();
		
		if (rorig == null) {
            IJ.showMessage("Roi required");
        }
		  nbRois = roimanager.getCount();
          Roi[] RoisOrig = roimanager.getRoisAsArray();
          
        
          
         
          
          for (int i = 0; i < nbRois; ++i){
        	  
        PolygonRoi l = (PolygonRoi) RoisOrig[i];
        
        int n = l.getNCoordinates(); 
        float[] xCord = l.getFloatPolygon().xpoints;
        int[] yCord = l.getYCoordinates();
        
        for (int index = 0; index < n - 1; index++) {
      
       float[] cords = {xCord[index], yCord[index] } ;
       float[] nextcords = {xCord[index + 1], yCord[index + 1] };
       
       float slope = (nextcords[1] - cords[1]) / (nextcords[0] - cords[0]);
       float intercept = nextcords[1] - slope * nextcords[0];
       float[] cordsLine = new float[n];
       for (int y = (int)cords[1]; y < nextcords[1]; ++y){
    	   
    	   cordsLine[1] =  y;
 		  if (nextcords[0] != cords[0]){
 		   cordsLine[0] = (y - intercept) / (slope);
 		 
 		  
       }
 		  
 		  else{
 			 cordsLine[0] = cords[0];
 			 
 			  
 		  }
 		  
 		  
 		  Mask.add(cordsLine);
    	   
 		  System.out.println(cordsLine[1] + " " + cordsLine[0] );
       }
      
       
       
        }
        
        		  
          }
          
          
        	
          
          /********
  		 * The part below removes the duplicate entries in the array
  		 * dor the time co-ordinate
  		 ********/
  		
  			int j = 0;

  			for (int index = 0; index < Mask.size() - 1; ++index) {
  				
  				
  				j = index + 1;
  				
  				
  					
  					
  				while (j < Mask.size()) {

  					if (Mask.get(index)[1] == Mask.get(j)[1]) {

  						Mask.remove(j);
  					}

  					else {
  						++j;
  						
  					}
  					
  					

  				}
  			}
          
          roimanager.close();
          
		
	}
	
       public void MakeBaseRois(){
		
	    RoiManager roimanager = RoiManager.getInstance();
	    if (roimanager == null) {
			roimanager = new RoiManager();
			roimanager.setVisible(true);
		}
		rorig = imp.getRoi();
		
		if (rorig == null) {
            IJ.showMessage("Roi required");
        }
		  nbRois = roimanager.getCount();
          Roi[] RoisOrig = roimanager.getRoisAsArray();
          
         
          
        
       
       
      
        for (int i = 0; i < nbRois; ++i){
      	  
            PolygonRoi l = (PolygonRoi) RoisOrig[i];
            
            int n = l.getNCoordinates(); 
            float[] xCord = l.getFloatPolygon().xpoints;
            int[] yCord = l.getYCoordinates();
            
            for (int index = 0; index < n - 1; index++) {
          
           float[] cords = {xCord[index], yCord[index] } ;
           float[] nextcords = {xCord[index + 1], yCord[index + 1] };
           
           float slope = (nextcords[1] - cords[1]) / (nextcords[0] - cords[0]);
           float intercept = nextcords[1] - slope * nextcords[0];
           
           for (int y = (int)cords[1]; y < nextcords[1]; ++y){
        	   
        	 
     		  
     		  float[] cordsLine = {xCord[index], y};
     		  
     		  
     		  
     		  
     		  Base.add(cordsLine);
        	   
     		 
           }
           
         
   					
   					

   				}
   			}
       
        /********
   		 * The part below removes the duplicate entries in the array
   		 * dor the time co-ordinate
   		 ********/
   		
   			int j = 0;

   			for (int index = 0; index < Base.size() - 1; ++index) {
   				
   				
   				j = index + 1;
   				
   				if (Base.get(j)[0]== Float.NaN && Base.get(index)[0]!=Float.NaN)
   					Base.get(j)[0] = Base.get(index)[0];
   				else{
   				Base.remove(j);	
   				Base.remove(index);
   				}
   					
   					
   				while (j < Base.size()) {

   					if (Base.get(index)[1] == Base.get(j)[1]) {

   						Base.remove(j);
   					}

   					else {
   						++j;
   						
   					}
   				}
   				
   			
   			}
           
   			for (int index = 0; index < Base.size() ; ++index) {
   		 System.out.println(Base.get(index)[1] + " " + Base.get(index)[0]  );
   			}
        }
	
	JFrame Cardframe = new JFrame("Extract Kymo");
	JPanel panelCont = new JPanel();
	JPanel panelFirst = new JPanel();
        public void  Card() {
		
		CardLayout cl = new CardLayout();
		 
		
        panelCont.setLayout(cl);
    	
     
		
    	panelCont.add(panelFirst, "1");
    	final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();
		panelFirst.setLayout(layout);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0; 
		c.weightx = 1;

    	
    	final Label Select = new Label("Make Segmented Line selection");
    	final Button ExtractKymo = new Button("Extract Mask Co-ordinates :");
    	final Button ExtractBaseLine = new Button("Extract Baseline Co-ordinates :");
    	final Button GetLength = new Button("Get absolute Lengths :");
    	final Label Result = new Label("The generated file contains time (row 1) and length (row 2)");
    	
    	++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(Select, c);
		
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(ExtractKymo, c);
    	
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(ExtractBaseLine, c);
		
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(GetLength, c);
		
		++c.gridy;
		c.insets = new Insets(10, 10, 0, 0);
		panelFirst.add(Result, c);
		
       panelFirst.setVisible(true);
		
		cl.show(panelCont, "1");
		
		
		ExtractKymo.addActionListener(new GetCords());
		ExtractBaseLine.addActionListener(new GetBaseCords());
		GetLength.addActionListener(new GetLength());
		Cardframe.add(panelCont, BorderLayout.CENTER);
		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
    	
        }
	
        protected class GetCords implements ActionListener {
    		@Override
    		public void actionPerformed(final ActionEvent arg0) {
    			
    			MakeRois();
    			
    			
    		}
        
        }
        
        protected class GetBaseCords implements ActionListener {
    		@Override
    		public void actionPerformed(final ActionEvent arg0) {
    			
    			
    			
    			MakeBaseRois();
    			
    			
    		}
        
        }
        
        protected class GetLength implements ActionListener {
    		@Override
    		public void actionPerformed(final ActionEvent arg0) {
    			FileWriter fw;
				try {
					fw = new FileWriter(fichier);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(
	    					"\tFramenumber\tLength\n");
				
    			

					float meanbase = 0;
					for (int index = 0; index < Base.size(); ++index){
						
						meanbase += Base.get(index)[0];
						
					}
					
					meanbase /= Base.size(); 
					
    			int size = Mask.size() - Base.size();
    			int listsize =  Mask.size();
    			for (int index = 0; index <listsize; ++index){
    				
    				float currentlength = Mask.get(index)[0] - meanbase;
    				
    				float[] lengthtime = {currentlength, Mask.get(index)[1]};
    				
    				Length.add(lengthtime);
    				bw.write("\t" + (lengthtime[1]) + "\t" + (lengthtime[0] + "\n"));
    				 System.out.println(lengthtime[1]+ " " + lengthtime[0] );
    			}
    			bw.close();
    			fw.close();
    			
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
    		}
        
        }
        
	
	public static  void main (String[] args){
		
		new ImageJ();
		String usefolder = ("/Users/varunkapoor/Documents/20170229/Video4/");
				//IJ.getDirectory("imagej");
		String addToName = "MT4porcineWH";
		
		RandomAccessibleInterval<FloatType> img = util.ImgLib2Util.openAs32Bit(new File("/Users/varunkapoor/Documents/20170229/Video4/Kymograph3-1.tif"),
				new ArrayImgFactory<FloatType>());
		
		File fichier = new File(usefolder + "//" + addToName + "ID" + 0 + ".txt");
		
		
		new InteractiveKymoAnalyze_(img, fichier).run(null);
		
	}
	
	
	
}
