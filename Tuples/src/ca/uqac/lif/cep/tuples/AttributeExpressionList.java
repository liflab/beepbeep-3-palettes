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

import java.util.ArrayList;
import java.util.Stack;

/**
 * A list of attribute expressions. This class exists only to provide
 * an object to build when parsing an expression.
 * 
 * @author Sylvain Hallé
 */
class AttributeExpressionList extends ArrayList<AttributeExpression>
{
	/**
	 * Dummy UID
	 */
	private static final long serialVersionUID = 1L;

	public static void build(Stack<Object> stack)
	{
		Object top = stack.peek();
		AttributeExpressionList new_al = new AttributeExpressionList();
		if (top instanceof AttributeExpressionList)
		{
			AttributeExpressionList al = (AttributeExpressionList) stack.pop();
			stack.pop(); // ,
			AttributeExpression def = (AttributeExpression) stack.pop();
			new_al.add(def);
			new_al.addAll(al);
		}
		else
		{
			AttributeExpression def = (AttributeExpression) stack.pop();
			new_al.add(def);
		}
		stack.push(new_al);
	}

	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		boolean first = true;
		for (AttributeExpression te : this)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				out.append(", ");
			}
			out.append(te);
		}
		return out.toString();
	}
}