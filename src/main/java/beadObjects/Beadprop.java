package beadObjects;

import ij.gui.Roi;
import net.imglib2.Localizable;

public class Beadprop {

	public final int zplane;
	public final Localizable point;
	public final Roi roi;
	public final long radius;
	
	
	
	public Beadprop(final int zplane, final Localizable point, final Roi roi, final long radius){
		
		this.zplane = zplane;
		this.point = point;
		this.roi = roi;
		this.radius = radius;
		
		
		
	}
	
	
}
