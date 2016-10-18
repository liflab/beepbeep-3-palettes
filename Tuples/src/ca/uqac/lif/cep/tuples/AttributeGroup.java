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

/**
 * A group of attributes taken from the merging of one or more tuples.
 * Each attribute is identified
 * @author Sylvain Hallé
 *
 */
public class AttributeGroup 
{
	/**
	 * The tuples to  be stored in the group
	 */
	private final Tuple[] m_tuples;
	
	/**
	 * The name associated to each tuple
	 */
	private final String[] m_names;
	
	/**
	 * Create a new attribute group, by specifying the names given to each
	 * tuple.
	 * @param names The names
	 */
	public AttributeGroup(String[] names)
	{
		super();
		m_names = names;
		m_tuples = new Tuple[names.length];
	}
	
	/**
	 * Add a tuple in a specific position in the group
	 * @param position The position. This value should be at least 0 and
	 * at most the size of the name array minus one.
	 * @param t The tuple to add
	 */
	public void add(int position, Tuple t)
	{
		m_tuples[position] = t;
	}
	
	/**
	 * Gets the value of an attribute by giving its name and the of
	 * the tuple it comes from.
	 *
	 * @param trace_name The tuple name. Can be null.
	 * @param attribute_name The attribute name 
	 * @return The value of the attribute, or <code>null</code> if not found
	 */
	public Object getAttribute(String trace_name, String attribute_name)
	{
		if (trace_name == null)
		{
			return getAttribute(attribute_name);
		}
		Object out = null;
		for (int i = 0; i < m_names.length; i++)
		{
			if (trace_name.compareTo(m_names[i]) == 0)
			{
				out = m_tuples[i].get(attribute_name);
				break;
			}
		}
		return out;
	}
	
	/**
	 * Gets the value of an attribute by giving its name. The method
	 * will look in all tuples for an attribute with that name, and
	 * return the first found.
	 *
	 * @param attribute_name The attribute name 
	 * @return The value of the attribute, or <code>null</code> if not found
	 */
	public Object getAttribute(String attribute_name)
	{
		Object out = null;
		for (int i = 0; i < m_names.length; i++)
		{
			if (m_tuples[i].containsKey(attribute_name))
			{
				out = m_tuples[i].get(attribute_name);
				break;
			}
		}
		return out;
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < m_names.length; i++)
		{
			if (i > 0)
			{
				out.append(",");
			}
			out.append(m_names[i]).append(": {").append(m_tuples[i]).append("}");
		}
		return out.toString();
	}
}
