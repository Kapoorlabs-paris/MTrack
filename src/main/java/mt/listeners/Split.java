package mt.listeners;



public class Split implements Runnable  {

	final InteractiveRANSAC parent;
	final int fileindex;

	public Split(InteractiveRANSAC parent, final int fileindex) {

		this.parent = parent;
		this.fileindex = fileindex;
	}

	public void run() {
		
		parent.displayclicked(fileindex);
		
		
		
	}
	
}
