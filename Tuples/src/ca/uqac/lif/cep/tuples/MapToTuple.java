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

import java.util.Map;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Converts a {@code Map} object into a {@code Tuple}
 * @author Sylvain Hallé
 */
public class MapToTuple extends UnaryFunction<Object,Tuple>
{
	public static final MapToTuple instance = new MapToTuple();
	
	MapToTuple()
	{
		super(Object.class, Tuple.class);
	}

	@Override
	public Tuple getValue(Object x)
	{
		if (x instanceof Tuple)
			return (Tuple) x;
		if (x instanceof Map)
		{
			@SuppressWarnings("unchecked")
			Map<Object,Object> m = (Map<Object,Object>) x;
			Tuple t = new TupleMap();
			for (Map.Entry<Object,Object> entry : m.entrySet())
			{
				t.put(entry.getKey().toString(), entry.getValue());
			}
			return t;
		}
		return null;
	}
}
