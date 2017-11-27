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
package ca.uqac.lif.cep.dsl;

import java.util.Stack;

import ca.uqac.lif.bullwinkle.ParseNode;

/**
 * Expression parser that returns the top of the stack when the
 * traversal of the parse tree is over.
 * @author Sylvain Hallé
 *
 * @param <T> The type of the object being returned
 */
public abstract class TopStackParseTreeBuilder<T> extends ExpressionParser<T>
{
	@Override
	public T build(ParseNode tree)
	{
		m_stack = new Stack<Object>();
		preVisit();
		tree.postfixAccept(this);
		m_builtObject = postVisit(m_stack);
		return m_builtObject;
	}
	
	@Override
	public void preVisit()
	{
		// Nothing to do
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T postVisit(Stack<Object> stack)
	{
		if (stack.isEmpty())
		{
			return null;
		}
		return (T) stack.peek();
	}
}
