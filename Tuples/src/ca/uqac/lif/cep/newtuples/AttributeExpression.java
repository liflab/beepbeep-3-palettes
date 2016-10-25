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
package ca.uqac.lif.cep.newtuples;


import java.util.Stack;

import ca.uqac.lif.cep.functions.Function;

/**
 * Association between an expression to compute a value from an
 * AttributeGroup and an attribute name. A <code>SELECT</code>
 * processor is made of one or more such expressions; each is
 * responsible for computing the value of one of the attributes of
 * the output tuple, expressed in terms of elements of an input
 * {@link AttributeGroup}. In terms of SQL, this corresponds to
 * the "<code>exp AS somename</code>" construct following the
 * <code>SELECT</code> keyword.
 * 
 * @author Sylvain Hallé
 */
public class AttributeExpression
{
	/**
	 * The expression to compute the output value
	 */
	protected Function m_expression;
	
	/**
	 * The attribute name to be given to this value
	 */
	protected String m_targetName;
	
	protected AttributeExpression()
	{
		super();
	}
	
	/**
	 * Creates a new attribute expression
	 * @param expression The expression to compute the output value
	 * @param name The attribute name to be given to this value
	 */
	public AttributeExpression(Function expression, String name)
	{
		super();
		m_expression = expression;
		if (name != null)
		{
			m_targetName = name;
		}
		else
		{
			// Guess a name for the attribute based on the function
			if (m_expression instanceof GetAttribute)
			{
				m_targetName = ((GetAttribute) m_expression).getAttributeName();
			}
			else
			{
				m_targetName = null;
			}
		}
	}
	
	/**
	 * Gets the attribute name given to that expression
	 * @return The name
	 */
	public String getName()
	{
		return m_targetName;
	}
	
	/**
	 * Gets the value associated to the attribute name
	 * @param group The attribute group from which the value is to
	 * be computed
	 * @return The value
	 */
	public Object getValue(AttributeGroup group)
	{
		Object[] inputs = new Object[1];
		inputs[0] = group;
		return m_expression.evaluate(inputs)[0];
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append("(").append(m_expression).append(")");
		if (m_targetName != null)
		{
			out.append(" AS ").append(m_targetName);
		}
		return out.toString();
	}
	
	public static void build(Stack<Object> stack)
	{
		// Do nothing
	}
}
