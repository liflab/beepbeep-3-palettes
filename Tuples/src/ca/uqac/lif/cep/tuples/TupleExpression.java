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

import ca.uqac.lif.cep.Processor;

/**
 * Association between an input trace (producing tuples) and a name.
 * This corresponds to the "something [AS somename]" portion of the
 * <code>FROM</code> clause in an SQL expression.
 */
class TupleExpression
{
	/**
	 * The input processor producing the tuples
	 */
	protected Processor m_processor;
	
	/**
	 * The name to be associated with
	 */
	protected String m_name;
	
	/**
	 * Creates a new TupleExpression
	 * @param p The input processor producing the tuples
	 * @param n The name to be associated with
	 */
	public TupleExpression(Processor p, String n)
	{
		super();
		m_processor = p;
		m_name = n;
	}
	
	public static void build(ArrayDeque<Object> stack)
	{
		// Do nothing
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append("(").append(m_processor).append(")");
		if (m_name == null)
		{
			out.append(" AS ").append(m_name);
		}
		return out.toString();
	}
}