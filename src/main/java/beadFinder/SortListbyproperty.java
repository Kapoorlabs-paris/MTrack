package beadFinder;

import java.util.ArrayList;
import java.util.Iterator;


public class SortListbyproperty {

	
	
	public static void sortpointList(ArrayList<double[]> pointlist) {
		if (pointlist.size() <= 1)
			return;

		else {

			// the first element belonging to the right list childB
			final int splitIndex = (int) pointlist.size() / 2;

			Iterator<double[]> iterator = pointlist.iterator();

			final ArrayList<double[]> childA = new ArrayList<double[]>((int) pointlist.size() / 2);

			final ArrayList<double[]> childB = new ArrayList<double[]>((int) (pointlist.size() / 2 + pointlist.size() % 2));

			int index = 0;

			while (iterator.hasNext()) {
				iterator.next();

				if (index < splitIndex)
					childA.add(pointlist.get(index));

				else

					childB.add(pointlist.get(index));

				index++;

			}

			sortpointList(childA);

			sortpointList(childB);

			mergepointListValue(pointlist, childA, childB);

			

			

		}

	}

	/// ***** Returns a sorted list *********////
	public static void mergepointListValue(ArrayList<double[]> sortedlist, ArrayList<double[]> listA, ArrayList<double[]> listB) {

		int i = 0, j = 0, k = 0;

		while (i < listA.size() && j < listB.size()) {

			// Decide the property by which the list has to be sorted by
			if (listA.get(i)[0] < (listB.get(j)[0])) {

				sortedlist.set(k, listA.get(i));

				++i;
				++k;
			}

			else {

				sortedlist.set(k, listB.get(j));

				++j;
				++k;

			}

		}

		while (i < listA.size()) {
			sortedlist.set(k, listA.get(i));
			++i;
			++k;

		}

		while (j < listB.size()) {
			sortedlist.set(k, listB.get(j));
			++j;
			++k;

		}

	}
	
	
	
	
	
	
	
}
