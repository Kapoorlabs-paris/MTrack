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
