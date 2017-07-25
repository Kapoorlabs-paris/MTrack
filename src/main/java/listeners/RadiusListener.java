package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.Interactive_MTDoubleChannel.ValueChange;

public class RadiusListener implements TextListener  {
	
	
final Interactive_MTDoubleChannel parent;
	
	public RadiusListener(final Interactive_MTDoubleChannel parent){
		
		this.parent = parent;
		 parent.updatePreview(ValueChange.SHOWMSER);
	}
	
	
	@Override
	public void textValueChanged(TextEvent e) {
	
		 TextComponent tc = (TextComponent)e.getSource();
		    String s = tc.getText();
		   
		    if (s.length() > 0) { 
		parent.radiusfactor = (int)Float.parseFloat(s);

		
		    }
		    if (parent.inputFieldradi.getText().length() > 0)
				parent.radiusfactor = Double.parseDouble(parent.inputFieldradi.getText());
		    parent.updatePreview(ValueChange.SHOWMSER);
		   
	}

}
