package comboListeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import mt.listeners.InteractiveRANSAC;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;

public class MinSlopeLocListener implements TextListener {

	final InteractiveRANSAC parent;
	boolean pressed;

	public MinSlopeLocListener(final InteractiveRANSAC parent, final boolean pressed) {

		this.parent = parent;
		this.pressed = pressed;

	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent) e.getSource();
		String s = tc.getText();
		if (s.contains("-")) {
			s.replace("-", "").trim();
		}

		int neg = 1;
		if (s.length() > 0 && s.charAt(0) == '-') {
			s = s.substring(1).trim();
			neg = -1;
		}

		if (s.length() > 0)
			parent.minSlope = Float.parseFloat(s);
		parent.minSlope = neg * parent.minSlope;

		parent.minSlopeLabel.setText(parent.minslopestring + " = " + ((parent.minSlope)) + "      ");
		parent.minSlopeSB.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.minSlope,
				parent.MIN_ABS_SLOPE, parent.MAX_ABS_SLOPE, parent.scrollbarSize));
		tc.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {

			}

			@Override
			public void keyReleased(KeyEvent arg0) {

				if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {

					pressed = false;

				}

			}

			@Override
			public void keyPressed(KeyEvent arg0) {

				if (arg0.getKeyChar() == KeyEvent.VK_ENTER && !pressed) {
					pressed = true;

					parent.updateRANSAC();
					parent.minSlopeSB.repaint();
					parent.minSlopeSB.validate();

				}

			}
		});

	}

}
