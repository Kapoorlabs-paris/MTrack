package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import LineModels.UseLineModel.UserChoiceModel;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import mpicbg.imglib.multithreading.SimpleMultiThreading;


public class AdvancedSeedListener implements ItemListener {
	
final Interactive_MTDoubleChannel parent;
	
	public AdvancedSeedListener (final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED){
			
			parent.AdvancedChoiceSeeds = false;
			
		}
		else if (arg0.getStateChange() == ItemEvent.SELECTED){
			 
			 parent.AdvancedChoiceSeeds = true;
			 parent.DialogueModelChoice();
			 
		 }
		 
		

		
	}
}
