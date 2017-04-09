/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.tuples;

import java.util.Comparator;

/**
 * Compares two tuples according to the values of one or many of their
 * attributes. Such a comparator can be used to sort a list of tuples.
 * @author Sylvain Hallé
 */
public class TupleComparator implements Comparator<Tuple> 
{
	/**
	 * The attributes over which to perform the comparison
	 */
	protected String[] m_sortAttributes;
	
	/**
	 * Creates a new tuple comparator
	 * @param attributes The attributes over which to perform the comparison
	 */
	public TupleComparator(String ... attributes)
	{
		super();
		m_sortAttributes = attributes;
	}

	@Override
	public int compare(Tuple x, Tuple y)
	{
		for (String attribute : m_sortAttributes)
		{
			Object val_x = x.get(attribute);
			Object val_y = y.get(attribute);
			if (val_x == null && val_y == null)
			{
				// Two nulls: same thing
				continue;
			}
			if (val_x == null && val_y != null)
			{
				return 1;
			}
			if (val_x != null && val_y == null)
			{
				return -1;
			}
			// val_x and val_y are both non null
			try
			{
				Double d_x = Double.parseDouble(val_x.toString());
				Double d_y = Double.parseDouble(val_y.toString());
				// If we get here, we managed to parse both values as numbers
				if (d_x > d_y)
				{
					return 1;
				}
				if (d_x < d_y)
				{
					return -1;
				}
				continue;
			}
			catch (NumberFormatException e)
			{
				// If we get here, one of the values is not a number.
				// Just compare both as strings
				int val = val_x.toString().compareTo(val_y.toString());
				if (val != 0)
					return val;
			}
		}
		return 0;
	}

}
