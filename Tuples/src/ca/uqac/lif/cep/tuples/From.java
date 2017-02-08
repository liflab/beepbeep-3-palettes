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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.functions.FunctionProcessor;

/**
 * Creates an {@link TupleExpression} from a set of input tuples and an
 * array of names. This performs roughly the same function as the
 * <code>FROM</code> clause in an SQL statement.
 * <p>
 * <strong>Caveat emptor:</strong> The input processors of FROM must be
 * <em>distinct</em>. If the same processor instance occurs multiple
 * times, it will be pulled more than once. (Note though that this applies
 * to any n-ary processor.) 
 * 
 * @author Sylvain Hallé
 */
public class From extends FunctionProcessor 
{
	public From(FromFunction comp) 
	{
		super(comp);
	}
	
	public static void build(ArrayDeque<Object> stack) throws ConnectorException
	{
		TupleExpressionList tel = (TupleExpressionList) stack.pop();
		stack.pop(); // FROM
		int size = tel.size();
		String[] trace_names = new String[size];
		int i = 0;
		for (TupleExpression te : tel)
		{
			trace_names[i] = te.m_name;
			i++;
		}
		FromFunction from_fct = new FromFunction(trace_names);
		From from = new From(from_fct);
		// Connect the from to the inputs
		i = 0;
		for (TupleExpression te : tel)
		{
			Connector.connect(te.m_processor, 0, from, i);
			i++;
		}
		stack.push(from);
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append("FROM ").append(m_function);
		return out.toString();
	}
}
