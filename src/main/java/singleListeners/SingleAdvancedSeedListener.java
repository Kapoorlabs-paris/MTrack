package singleListeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import LineModels.UseLineModel.UserChoiceModel;
import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;
import interactiveMT.Interactive_MTSingleChannel;
import mpicbg.imglib.multithreading.SimpleMultiThreading;


public class SingleAdvancedSeedListener implements ItemListener {
	
final Interactive_MTSingleChannel parent;
	
	public SingleAdvancedSeedListener (final Interactive_MTSingleChannel parent ){
		
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
