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

import java.util.ArrayDeque;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Gets the value of an attribute in an {@link AttributeGroup}
 * 
 * @author Sylvain Hallé
 */
public class GetAttribute extends UnaryFunction<AttributeGroup,Object>
{
	/**
	 * The name of the trace to get the attribute from
	 */
	private final String m_traceName;
	
	/**
	 * The name of the attribute to get
	 */
	private final String m_attributeName;
	
	/**
	 * Creates a new instance of the function
	 * @param trace_name The name of the trace to get the attribute from
	 * @param attribute_name The name of the attribute to get
	 */
	public GetAttribute(String trace_name, String attribute_name) 
	{
		super(AttributeGroup.class, Object.class);
		m_traceName = trace_name;
		m_attributeName = attribute_name;
	}
	
	/**
	 * Creates a new instance of the function
	 * @param attribute_name The name of the attribute to get
	 */
	public GetAttribute(String attribute_name) 
	{
		this(null, attribute_name);
	}
	
	/**
	 * Gets the name of the attribute to get
	 * @return The name
	 */
	public String getAttributeName()
	{
		return m_attributeName;
	}
	
	/**
	 * Gets the name of the trace to get the attribute from
	 * @return The name; null if no name has been given
	 */
	public String getTraceName()
	{
		return m_traceName;
	}

	@Override
	public Object getValue(AttributeGroup g) 
	{
		return g.getAttribute(m_traceName, m_attributeName);
	}
	
	@Override
	public GetAttribute clone()
	{ 
		return this;
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		if (m_traceName != null)
		{
			out.append(m_traceName).append(".");
		}
		out.append(m_attributeName);
		return out.toString();
	}
	
	public static void build(ArrayDeque<Object> stack)
	{
		// Do nothing
	}
}
