/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package listeners;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import interactiveMT.Interactive_MTDoubleChannel;
import interactiveMT.MainFileChooser;


	public class FileChooserDirectory implements ActionListener {
		
        MainFileChooser parent;
      
        
		public FileChooserDirectory(MainFileChooser parent) {

			this.parent = parent;
		

		}
		
		
		

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			JFileChooser chooserA = new JFileChooser();
			chooserA.setCurrentDirectory(parent.chooserB.getCurrentDirectory());
			chooserA.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooserA.showOpenDialog(parent.panelIntro);
			
			
			parent.userfile = parent.chooserB.getSelectedFile();

		

			
		}

	
}
