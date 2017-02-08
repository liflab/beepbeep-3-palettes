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
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.functions.Function;

/**
 * Filters a trace of <code>AttributeGroup</code> objects according to a
 * Boolean function
 * 
 * @author Sylvain Hallé
 */
public class Where extends SingleProcessor
{
	/**
	 * The condition to evaluate. Based on its return value on a given
	 * attribute group, the group will be returned or discarded.
	 */
	protected Function m_condition;
	
	Where() 
	{
		super(1, 1);
	}
	
	public Where(Function condition) 
	{
		super(1, 1);
		setCondition(condition);
	}
	
	public Where setCondition(Function f)
	{
		m_condition = f;
		return this;
	}

	@Override
	protected Queue<Object[]> compute(Object[] inputs)
	{
		Queue<Object[]> out_queue = new ArrayDeque<Object[]>();
		Object[] values = m_condition.evaluate(inputs);
		boolean value = (Boolean) values[0];
		if (value == true)
		{
			out_queue.add(inputs);
		}
		return out_queue;
	}

	@Override
	public Where clone()
	{
		return new Where(m_condition.clone());
	}
	
	public static void build(ArrayDeque<Object> stack) throws ConnectorException
	{
		Object o;
		Function f;
		o = stack.pop(); // ( ?
		if (o instanceof String)
		{
			f = (Function) stack.pop();
			stack.pop(); // )
		}
		else
		{
			f = (Function) o;
		}
		stack.pop(); // WHERE
		stack.pop(); // (
		Processor p = (Processor) stack.pop();
		stack.pop(); // )
		Where w = new Where(f);
		Connector.connect(p, w);
		stack.push(w);
	}

}
