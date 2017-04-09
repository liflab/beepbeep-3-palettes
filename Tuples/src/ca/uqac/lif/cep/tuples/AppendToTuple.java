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

import ca.uqac.lif.cep.functions.UnaryFunction;

public class AppendToTuple extends UnaryFunction<Tuple,TupleMap>
{
	String[] m_names;
	
	Object[] m_values;
	
	public AppendToTuple(String name, Object value)
	{
		super(Tuple.class, TupleMap.class);
		m_names = new String[]{name};
		m_values = new Object[]{value};
	}
	
	public AppendToTuple(String[] names, Object[] values)
	{
		super(Tuple.class, TupleMap.class);
		m_names = names;
		m_values = values;
	}

	@Override
	public TupleMap getValue(Tuple x) 
	{
		TupleMap tm = null;
		if (x instanceof TupleMap)
			tm = (TupleMap) x;
		else
		{
			tm = new TupleMap();
			tm.putAll(x);
		}
		for (int i = 0; i < m_names.length; i++)
		{
			tm.put(m_names[i], m_values[i]);
		}
		return tm;
	}
}
