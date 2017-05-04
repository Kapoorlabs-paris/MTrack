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
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class Markends {

	
	
final Interactive_MTDoubleChannel parent;
	
	
	public Markends(final Interactive_MTDoubleChannel parent){
	
		this.parent = parent;
	}
	
	
	public void markend() {

		parent.preprocessedimp.getCanvas().addMouseListener(parent.ml = new MouseListener() {
			final ImageCanvas canvas = parent.preprocessedimp.getWindow().getCanvas();

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() == false){
				
				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());

				Overlay o = parent.preprocessedimp.getOverlay();

				if (o == null) {
					o = new Overlay();

					parent.preprocessedimp.setOverlay(o);

				}
				
			
				
				
				OvalRoi nearestRoiCurr = util.DrawingUtils.getNearestRois(parent.AllSeedrois, new double[] { x, y });

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
				
				
				if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown() ){
					
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
					
					Pair<double[], Roi> newpoint = new ValuePair<double[], Roi>(new double[]{newx, newy}, nearestRoiCurr);
					
					parent.ClickedPoints.add(newpoint);
					System.out.println("You added: " + newx + "," + newy);
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
