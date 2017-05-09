package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import LineModels.UseLineModel.UserChoiceModel;
import interactiveMT.Interactive_MTDoubleChannel;


public class AdvancedTrackerListener implements ItemListener {
	
final Interactive_MTDoubleChannel parent;
	
	public AdvancedTrackerListener (final Interactive_MTDoubleChannel parent ){
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED){
			
			parent.AdvancedChoice = false;
		
		}
		else  if (arg0.getStateChange() == ItemEvent.SELECTED){
			 
			 parent.AdvancedChoice = true;
			 parent.DialogueModelChoiceHF();
			 
		 }

		
	}
}
