/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
package ca.uqac.lif.cep.ltl;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tmf.SinkLast;

public class FirstOrderSlice extends SingleProcessor 
{
	protected String m_varName;
	
	protected Function m_function;
	
	protected Processor m_expression;
	
	protected List<Processor> m_slices;
	
	protected List<Pushable> m_pushables;
	
	protected List<SinkLast> m_sinks;
	
	public FirstOrderSlice(String var_name, Function dom_function, Processor expression)
	{
		super(1, 1);
		m_varName = var_name;
		m_function = dom_function;
		m_expression = expression;
		m_slices = new ArrayList<Processor>();
		m_pushables = new ArrayList<Pushable>();
		m_sinks = new ArrayList<SinkLast>();
	}

	@Override
	public FirstOrderSlice duplicate(boolean with_state) 
	{
		FirstOrderSlice fos = new FirstOrderSlice(m_varName, m_function, m_expression);
		if (with_state)
		{
			throw new UnsupportedOperationException("Duplication with state not supported yet on this processor");
		}
		return fos;
	}
	
	@Override
	public void reset()
	{
		m_slices.clear();
		m_function.reset();
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) 
	{
		// Evaluate domain function
		Object[] values = new Object[1];
		m_function.evaluate(inputs, values);
		// Create new slice and set context
		Processor new_p = m_expression.duplicate();
		new_p.setContext(m_context);
		new_p.setContext(m_varName, values[0]);
		// Connect to sink, add to lists
		m_slices.add(new_p);
		m_pushables.add(new_p.getPushableInput());
		SinkLast sink = new SinkLast();
		Connector.connect(new_p, sink);
		m_sinks.add(sink);
		// Push inputs to all slices
		for (Pushable p : m_pushables)
		{
			p.push(inputs[0]);
		}
		// Collect output into an array
		Object[] out_vals = new Object[m_pushables.size()];
		for (int i = 0; i < out_vals.length; i++)
		{
			out_vals[0] = m_sinks.get(0).getLast()[0];
		}
		outputs.add(new Object[]{out_vals});
		return true;
	}
}
