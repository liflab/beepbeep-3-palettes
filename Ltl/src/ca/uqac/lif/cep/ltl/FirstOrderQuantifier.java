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

import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tmf.SinkLast;

public abstract class FirstOrderQuantifier extends SynchronousProcessor
{
	protected FirstOrderSlice m_slicer;
	
	protected Pushable m_slicerPushable;
	
	protected SinkLast m_sink;
	
	public FirstOrderQuantifier(String var_name, Function dom_function, Processor expression)
	{
		super(1, 1);
		m_slicer = new FirstOrderSlice(var_name, dom_function, expression);
		m_slicerPushable = m_slicer.getPushableInput();
		m_sink = new SinkLast();
		Connector.connect(m_slicer, m_sink);
	}
	
	protected FirstOrderQuantifier(FirstOrderSlice fos)
	{
		super(1, 1);
		m_slicer = fos;
	}
	
	@Override
	public void reset()
	{
		m_slicer.reset();
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		m_slicerPushable.push(inputs[0]);
		Object[] vals = (Object[]) m_sink.getLast()[0];
		Object value = combineValues(vals);
		if (value != null)
		{
			outputs.add(new Object[]{value});
		}
		return true;
	}
	
	public abstract Object combineValues(Object[] values);
}
