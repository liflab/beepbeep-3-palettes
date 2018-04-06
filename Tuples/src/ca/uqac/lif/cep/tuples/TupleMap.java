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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.cep.Duplicable;


/**
 * Implementation of a named tuple based on the <code>Map</code> class.
 * This named tuple is mutable: its contents can be modified during its
 * life cycle. This should be contrasted with the {@link TupleFixed},
 * whose contents are set at instantiation and cannot be changed
 * afterwards.
 * 
 * @author Sylvain Hallé
 *
 */
public class TupleMap extends Tuple implements Duplicable 
{
	/**
	 * The contents of the named tuple
	 */
	protected Map<String,Object> m_contents;
	
	/**
	 * Creates a new named tple
	 */
	public TupleMap()
	{
		super();
		m_contents = new HashMap<String,Object>();
	}
	
	public TupleMap(String[] names, Object[] values)
	{
		this();
		for (int i = 0; i < names.length; i++)
		{
			m_contents.put(names[i], values[i]);
		}
	}
	
	/**
	 * Creates a new named tuple by copying the contents of an existing
	 * named tuple
	 * @param ntm The named tuple to copy from
	 */
	public TupleMap(TupleMap ntm)
	{
		this();
		for (String key : ntm.m_contents.keySet())
		{
			m_contents.put(key, ntm.m_contents.get(key));
		}
	}
	
	/* 
	 * From this point on, these are merely delegate methods
	 * for the inner Map object
	 */

	@Override
	public void clear()
	{
		m_contents.clear();
	}

	@Override
	public boolean containsKey(Object arg0)
	{
		return m_contents.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return m_contents.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() 
	{
		return m_contents.entrySet();
	}

	@Override
	public boolean equals(Object o) 
	{
		if (o == null || !(o instanceof TupleMap))
		{
			return false;
		}
		TupleMap ntm = (TupleMap) o;
		return ntm.m_contents.equals(m_contents);
		/*
		if (ntm.m_contents.keySet().size() != m_contents.keySet().size())
		{
			return false;
		}
		for (String key : m_contents.keySet())
		{
			if (!ntm.m_contents.containsKey(key))
			{
				return false;
			}
			Object o1 = m_contents.get(key);
			Object o2 = ntm.m_contents.get(key);
			if (!o1.equals(o2))
			{
				return false;
			}
		}
		return true;*/
	}

	@Override
	public Object get(Object key) 
	{
		return m_contents.get(key);
	}

	@Override
	public int hashCode() 
	{
		return m_contents.hashCode();
	}

	@Override
	public boolean isEmpty() 
	{
		return m_contents.isEmpty();
	}

	@Override
	public Set<String> keySet() 
	{
		return m_contents.keySet();
	}

	public Object put(String key, Object value) 
	{
		return m_contents.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) 
	{
		m_contents.putAll(m);
	}

	
	public Object remove(Object key) 
	{
		return m_contents.remove(key);
	}

	@Override
	public int size() 
	{
		return m_contents.size();
	}

	@Override
	public Collection<Object> values() 
	{
		return m_contents.values();
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append("(");
		boolean first = true;
		for (String name : m_contents.keySet())
		{
			if (!first)
			{
				out.append(",");
			}
			Object value = m_contents.get(name);
			out.append(name).append("=").append(value);
			first = false;
		}
		out.append(")");
		return out.toString();
	}
	
	@Override
	public TupleMap duplicate(boolean with_state)
	{
		return new TupleMap(this);
	}
}
