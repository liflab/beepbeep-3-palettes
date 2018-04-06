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

import java.util.Set;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Transforms a tuple by replacing two key-value pairs by a single
 * new key-value pair. The new pair is created by
 * taking the value of a column as the key, and the value of another
 * column as the value.
 * <p>
 * For example, with the tuple: {(foo,1), (bar,2), (baz,3)}, using
 * "foo" as the "key" column and "baz" as the value column, the
 * resulting tuple would be: {(1,3), (bar,2)}. The value of foo is
 * the new key, and the value of baz is the new value.
 * <p>
 * If the value of the "key" pair is not a string, it is converted
 * into a string by calling its {@code toString()} method (since the
 * key of a tuple is always a string).
 * 
 * @author Sylvain Hallé
 *
 */
public class ExpandAsColumns extends UnaryFunction<Tuple, Tuple>
{
	/**
	 * The name of the column whose value will be used as a column
	 * header
	 */
	protected String m_columnName;
	
	/**
	 * The name of the column whose value will be used as a value
	 * for the newly created column
	 */
	protected String m_columnValue;
	
	ExpandAsColumns()
	{
		super(Tuple.class, Tuple.class);
	}
	
	/**
	 * Creates a new instance of the function. 
	 * @param col_name The name of the column whose value will be used as a column
	 * header
	 * @param col_value  The name of the column whose value will be used as a value
	 * for the newly created column
	 */
	public ExpandAsColumns(String col_name, String col_value)
	{
		this();
		m_columnName = col_name;
		m_columnValue = col_value;
	}

	@Override
	public Tuple getValue(Tuple x)
	{
		Tuple t = new TupleMap();
		String col_header = null;
		Object col_value = null;
		Set<String> keys = x.keySet();
		for (String key : keys)
		{
			if (key.compareTo(m_columnName) == 0)
			{
				Object o = x.get(key);
				if (o != null)
				{
					col_header = x.get(key).toString();
				}
			}
			else if (key.compareTo(m_columnValue) == 0)
			{
				Object o = x.get(key);
				if (o != null)
				{
					col_value = x.get(key);
				}
			}
			else
			{
				t.put(key, x.get(key));
			}
		}
		if (col_header != null)
		{
			t.put(col_header, col_value);
		}
		return t;
	}
	
	@Override
	public ExpandAsColumns duplicate(boolean with_state)
	{
		return new ExpandAsColumns(m_columnName, m_columnValue);
	}
}
