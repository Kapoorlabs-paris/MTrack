package updateListeners;

import LineModels.UseLineModel.UserChoiceModel;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTSingleChannel;

public class SingleDefaultModelHF {

final Interactive_MTSingleChannel parent;
	
	public SingleDefaultModelHF(final Interactive_MTSingleChannel parent ){
		
		this.parent = parent;
	}
	
	
	
	public  void LoadDefault(){
		
		
		parent.userChoiceModel = UserChoiceModel.Splineorderthird;
		parent.Intensityratio = 0.5;
		parent.Inispacing = 0.5 * Math.min(parent.psf[0], parent.psf[1]);
		parent.displayoverlay = true;
		parent.Domask = true;
		
	}
	
	
}
