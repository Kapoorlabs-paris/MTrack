package updateListeners;

import LineModels.UseLineModel.UserChoiceModel;
import interactiveMT.Interactive_MTDoubleChannel;

public class DefaultModel {

final Interactive_MTDoubleChannel parent;
	
	public DefaultModel(final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
	}
	
	
	
	public  void LoadDefault(){
		
		
		parent.userChoiceModel = UserChoiceModel.Line;
		parent.Intensityratio = 0.5;
		parent.Inispacing = 0.5 * Math.min(parent.psf[0], parent.psf[1]);
		parent.displayoverlay = true;
		parent.Domask = true;
		
	}
	
	
}