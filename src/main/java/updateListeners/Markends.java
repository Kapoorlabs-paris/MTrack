package updateListeners;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import ij.gui.ImageCanvas;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import interactiveMT.Interactive_MTDoubleChannel;
import labeledObjects.Indexedlength;
import mpicbg.imglib.util.Util;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import peakFitter.SubpixelLengthUserSeed;

public class Markends {

	
	
final Interactive_MTDoubleChannel parent;
	
	
	public Markends(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	public void markend() {
		
		parent.preprocessedimp.getCanvas().addMouseListener(parent.ml = new MouseListener() {
			final ImageCanvas canvas = parent.preprocessedimp.getWindow().getCanvas();
			final int maxSeed = parent.PrevFrameparam.getA().get(parent.PrevFrameparam.getA().size() - 1).seedLabel;
			int nextseed = maxSeed;
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
				
			
				
				
				OvalRoi nearestRoiCurrUser = util.DrawingUtils.getNearestRoisPair(parent.ClickedPoints, new double[] { x, y });

				OvalRoi nearestRoiCurr = util.DrawingUtils.getNearestRois(parent.AllSeedrois, new double[] { x, y });
				
				if(nearestRoiCurrUser!=null){
					nearestRoiCurr = nearestRoiCurrUser;
				    
				if(parent.Userframe.size() > 0){
					
					for (int index = 0; index < parent.Userframe.size(); ++index){
						
						
						parent.Userframe.remove(index);
						
						--index;
						
					}
					
					
				}
				
				}
				Rectangle rect = nearestRoiCurr.getBounds();

				double newx = rect.x + rect.width / 2.0;
				double newy = rect.y + rect.height / 2.0;
				final OvalRoi Bigroi = nearestRoiCurr;
				Bigroi.setStrokeColor(parent.colorUnselect);
				o.add(Bigroi);
				
				
				for (int index = 0; index < parent.ClickedPoints.size(); ++index){
					
					if (parent.ClickedPoints.get(index).getB() == nearestRoiCurr){
						parent.ClickedPoints.remove(index);
					--index;
					}
					
				}
				
				System.out.println("You deleted: " + newx + "," + newy);
				}
				
				
				if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() && e.isAltDown() == false){
					
					int x = canvas.offScreenX(e.getX());
					int y = canvas.offScreenY(e.getY());

				Overlay	o = parent.preprocessedimp.getOverlay();

					if (o == null) {
						parent.overlaysec = new Overlay();

						parent.preprocessedimp.setOverlay(o);

					}
					
					
					
					OvalRoi nearestRoiCurr = util.DrawingUtils.getNearestRois(parent.AllSeedrois, new double[] { x, y });

					Rectangle rect = nearestRoiCurr.getBounds();

					double newx = rect.x + rect.width / 2.0;
					double newy = rect.y + rect.height / 2.0;
					final OvalRoi Bigroi = nearestRoiCurr;
					Bigroi.setStrokeColor(parent.colorConfirm);
					o.add(Bigroi);
					
					Pair<double[], OvalRoi> newpoint = new ValuePair<double[], OvalRoi>(new double[]{newx, newy}, nearestRoiCurr);
					
					parent.ClickedPoints.add(newpoint);
					System.out.println("You added: " + newx + "," + newy);
					}
					
				if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() && e.isAltDown()){
					
					
					
					
					
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
					
					
					Indexedlength userseed = newseed.UserSeed(new double[]{x, y}, nextseed);
					
					parent.Userframe.add(userseed);
					nextseed++;
					
					parent.ClickedPoints.add(newpoint);
					
					System.out.println("User clicked: " + x + " ," + y);
					
					
				}
				
				
				
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
	}

	
}
