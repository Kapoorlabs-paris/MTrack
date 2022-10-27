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
package updateListeners;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import ij.gui.ImageCanvas;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import interactiveMT.Interactive_MTDoubleChannel;
import labeledObjects.Indexedlength;
import mpicbg.imglib.util.Util;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import peakFitter.SubpixelLengthUserSeed;

public class Markendsnew {
	
	
	
    final Interactive_MTDoubleChannel parent;
	
	
	public Markendsnew(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	public void markendnew(){
		
		parent.preprocessedimp.getCanvas().addMouseListener(parent.ml = new MouseListener() {
			final ImageCanvas canvas = parent.preprocessedimp.getWindow().getCanvas();
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() == false && e.isAltDown() == false){
					
					int x = canvas.offScreenX(e.getX());
					int y = canvas.offScreenY(e.getY());

					Overlay o = parent.preprocessedimp.getOverlay();

					if (o == null) {
						o = new Overlay();

						parent.preprocessedimp.setOverlay(o);

					}
				
					

					OvalRoi nearestRoiCurr = util.DrawingUtils.getNearestRois(parent.AllSeedrois, new double[] { x, y });
					
					
					if(parent.Userframe.size() > 0){
						
						for (int index = 0; index < parent.Userframe.size(); ++index){
							
							if(nearestRoiCurr.getStrokeColor()==parent.colorUser   && parent.Userframe.get(index).roi == nearestRoiCurr ){
								nearestRoiCurr.setStrokeColor(parent.colorUnselectUser);
								o.add(nearestRoiCurr);
							parent.Userframe.remove(index);
							
							
							
							--index;
							
							
							for (int indexx = 0; indexx < parent.ClickedPoints.size(); ++indexx){
								
								if (parent.ClickedPoints.get(indexx).getB() == nearestRoiCurr){
									parent.ClickedPoints.remove(indexx);
								--indexx;
								}
								
							}
							
						}
							
                 
						
					}
					
						
					
					
				}
					/*
				
					if (nearestRoiCurr!=null){
					Rectangle rect = nearestRoiCurr.getBounds();

					double newx = rect.x + rect.width / 2.0;
					double newy = rect.y + rect.height / 2.0;
					OvalRoi Bigroi = nearestRoiCurr;
					
					
					
					if (nearestRoiCurr.getStrokeColor() == parent.colorConfirm){
					
					Bigroi.setStrokeColor(parent.colorUnselect);
					o.add(Bigroi);
					
					
			
					
					for (int index = 0; index < parent.ClickedPoints.size(); ++index){
						
						if (parent.ClickedPoints.get(index).getB() == nearestRoiCurr){
							parent.ClickedPoints.remove(index);
						--index;
						}
						
					}
					}
				
					else if(nearestRoiCurr.getStrokeColor()==parent.colorUnselect){
						Bigroi.setStrokeColor(parent.colorConfirm);
						o.add(Bigroi);
						
						Pair<double[], OvalRoi> newpoint = new ValuePair<double[], OvalRoi>(new double[]{newx, newy}, nearestRoiCurr);
						
						parent.ClickedPoints.add(newpoint);
						
						System.out.println("You added: " + newx + "," + newy);
						}
					
	               System.out.println("clicked" + parent.ClickedPoints.size());
					
					
				  System.out.println("You deleted: " + newx + "," + newy);
					}
					*/
			}
				
				if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()){
					
					
					int x = canvas.offScreenX(e.getX());
					int y = canvas.offScreenY(e.getY());

				Overlay	o = parent.preprocessedimp.getOverlay();

					if (o == null) {
						parent.overlaysec = new Overlay();

						parent.preprocessedimp.setOverlay(o);

					}
				
						
					final OvalRoi Bigroi = new OvalRoi(Util.round(x - parent.radiusseed),
							Util.round(y - parent.radiusseed), Util.round(2 * parent.radiusseed), Util.round(2 * parent.radiusseed));
					Bigroi.setStrokeColor(parent.colorUser);
					o.add(Bigroi);
					
					Pair<double[], OvalRoi > newpoint = new ValuePair<double[], OvalRoi>(new double[]{x, y}, Bigroi);
					
					SubpixelLengthUserSeed newseed = new SubpixelLengthUserSeed(parent);
					
					
					Indexedlength userseed = newseed.UserSeed(new double[]{x, y}, parent.nextseed, Bigroi);
						
					parent.Userframe.add(userseed);
					parent.nextseed++;
					
					parent.ClickedPoints.add(newpoint);
					parent.AllSeedrois.add(Bigroi);
				
					
					
					System.out.println("User clicked: " + x + " ," + y + " " + "Seed ID:" + parent.nextseed);
					
					
				}
				
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
	}

}
