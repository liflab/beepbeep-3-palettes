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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Merges all tuples in a set with the same value for a given attribute.
 * This function accepts as its input a multiset of tuples, and outputs
 * another multiset of tuples.
 * @author Sylvain Hallé
 */
public class JoinSet extends UnaryFunction<Multiset,Multiset>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6739343371520180037L;
	/**
	 * The attribute over which to perform the join
	 */
	protected String m_joinAttribute;
	
	/**
	 * Creates a new join set function
	 * @param join_attribute The attribute over which to perform the join.
	 * All tuples in the input set with the same value for that attribute
	 * will be merged into a single tuple.
	 */
	public JoinSet(String join_attribute)
	{
		super(Multiset.class, Multiset.class);
		m_joinAttribute = join_attribute;
	}

	@Override
	public Multiset getValue(Multiset x)
	{
		Map<Object,Set<Tuple>> map = new HashMap<Object,Set<Tuple>>();
		Set<Object> set = x.keySet();
		for (Object o : set)
		{
			if (o instanceof Tuple)
			{
				Tuple tup = (Tuple) o;
				Object join_val = tup.get(m_joinAttribute);
				Set<Tuple> set_tuples = null;
				if (map.containsKey(join_val))
				{
					set_tuples = map.get(join_val);
				}
				else
				{
					set_tuples = new HashSet<Tuple>();
					map.put(join_val, set_tuples);
				}
				set_tuples.add(tup);
			}
		}
		Multiset new_set = new Multiset();
		for (Map.Entry<Object,Set<Tuple>> entry : map.entrySet())
		{
			TupleMap tm = new TupleMap();
			for (Tuple t : entry.getValue())
			{
				tm.putAll(t);
			}
			new_set.add(tm);
		}
		return new_set;
	}
	
	@Override
	public JoinSet duplicate()
	{
		return new JoinSet(m_joinAttribute);
	}
}
