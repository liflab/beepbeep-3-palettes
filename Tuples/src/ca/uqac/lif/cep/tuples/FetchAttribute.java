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

import java.util.ArrayDeque;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Gets the value of an attribute in a {@link Tuple}
 * 
 * @author Sylvain Hallé
 */
public class FetchAttribute extends UnaryFunction<Tuple,Object>
{
	/**
	 * The name of the attribute to get
	 */
	private final String m_attributeName;
	
	/**
	 * Creates a new instance of the function
	 * @param attribute_name The name of the attribute to get
	 */
	public FetchAttribute(String attribute_name) 
	{
		super(Tuple.class, Object.class);
		m_attributeName = attribute_name;
	}
	
	/**
	 * Gets the name of the attribute to get
	 * @return The name
	 */
	public String getAttributeName()
	{
		return m_attributeName;
	}

	@Override
	public Object getValue(Tuple t) 
	{
		return t.get(m_attributeName);
	}
	
	@Override
	public FetchAttribute duplicate(boolean with_state)
	{ 
		return new FetchAttribute(m_attributeName);
	}
	
	@Override
	public String toString()
	{
		return m_attributeName;
	}
	
	public static void build(ArrayDeque<Object> stack)
	{
		// Do nothing
	}
}
