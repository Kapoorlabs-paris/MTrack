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
package swingClasses;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import interactiveMT.SingleBatchMode;
import parallelization.ProcessFiles;

public class SingleProgressBatch extends SwingWorker<Void, Void> {

	final SingleBatchMode parent;

	public SingleProgressBatch(final SingleBatchMode parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {

		int nThreads = Runtime.getRuntime().availableProcessors();
		// set up executor service
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);

		ProcessFiles.process(parent.AllImages, taskExecutor);

		return null;

	}

	@Override
	protected void done() {
		try {
			parent.jpb.setIndeterminate(false);
			get();
			parent.frame.dispose();
			JOptionPane.showMessageDialog(parent.jpb.getParent(), "Success", "Success",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
