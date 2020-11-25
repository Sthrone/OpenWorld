package particles;

import java.util.List;

public class InsertionSort 
{
	/**
	 * Sorts a list of particles so that the particles with the highest distance
	 * from the camera are first, and the particles with the shortest distance
	 * are last.
	 * 
	 * @param list
	 *            - the list of particles needing sorting.
	 */
	
	public static void SortHighToLow(List<Particle> list) 
	{
		for (int i = 1; i < list.size(); i++) 
		{
			Particle item = list.get(i);
			if (item.getDistanceFromCamera() > list.get(i - 1).getDistanceFromCamera()) 
			{
				sortUpHighToLow(list, i);
			}
		}
	}

	private static void sortUpHighToLow(List<Particle> list, int i) 
	{
		Particle item = list.get(i);
		int attemptPos = i - 1;
		
		while (attemptPos != 0 && list.get(attemptPos - 1).getDistanceFromCamera() < item.getDistanceFromCamera()) 
		{
			attemptPos--;
		}
		
		list.remove(i);
		list.add(attemptPos, item);
	}

}
