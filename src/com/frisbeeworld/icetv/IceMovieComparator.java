package com.frisbeeworld.icetv;

import java.util.Comparator;

public class IceMovieComparator implements Comparator
{
	@Override
	public int compare(Object object1, Object object2)
	{
		IceMovie movie1 = (IceMovie)object1;
		IceMovie movie2 = (IceMovie)object2;
		int		result;

		result = (int)(10.0 * (movie2.GetRating() - movie1.GetRating()));
		if (result == 0)
		{
			result = movie1.GetBaseMovieName().compareTo(movie2.GetBaseMovieName());
		}
		return result;
	}
}
