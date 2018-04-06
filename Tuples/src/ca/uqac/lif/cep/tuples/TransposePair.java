/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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
 * Creates two Applies a triple substitution to a singleton tuple. Given a tuple
 * {(foo,bar)} two new names baz and biz, the transformation will produce
 * the tuple {(baz=foo,biz=bar)}. 
 * @author Sylvain Hallé
 */
public class TransposePair extends UnaryFunction<Tuple,Tuple> 
{
	/**
	 * The key associated to the tuple's header
	 */
	protected String m_columnHeader;
	
	/**
	 * The key associated to the tuple's value
	 */
	protected String m_columnValue;
	
	/**
	 * Creates a new instance of the tuple transposition function.
	 * @param column_header The key associated to the tuple's header
	 * @param column_value The key associated to the tuple's value
	 */
	public TransposePair(String column_header, String column_value)
	{
		super(Tuple.class, Tuple.class);
		m_columnHeader = column_header;
		m_columnValue = column_value;
	}

	@Override
	public Tuple getValue(Tuple x) 
	{
		TupleMap out_tuple = new TupleMap();
		Set<String> keys = x.keySet();
		for (String key : keys)
		{
			out_tuple.put(m_columnHeader, key);
			out_tuple.put(m_columnValue, x.get(key));
			break;
		}
		return out_tuple;
	}
	
	@Override
	public TransposePair duplicate(boolean with_state)
	{
		return new TransposePair(m_columnHeader, m_columnValue);
	}
}
