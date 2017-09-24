package mt.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import org.apache.commons.io.filefilter.CanReadFileFilter;

public class FunctionItemListener implements ActionListener
{
	final InteractiveRANSAC parent;
	final JComboBox<String> choice;

	public FunctionItemListener( final InteractiveRANSAC parent, final JComboBox<String> choice )
	{
		this.parent = parent;
		this.choice = choice;
	}

	
	
	
	
	






	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		int selectedindex = choice.getSelectedIndex();
		
		if (selectedindex == 0)
			parent.functionChoice = 0;
		if (selectedindex == 1)
			parent.functionChoice = 1;
		if (selectedindex == 2)
			parent.functionChoice = 2;
		System.out.println(selectedindex);
		parent.setFunction();
		parent.updateRANSAC();
	}
}
