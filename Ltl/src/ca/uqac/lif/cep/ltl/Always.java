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
package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.ltl.Troolean.Value;
import ca.uqac.lif.cep.tmf.SinkLast;

/**
 * Troolean implementation of the LTL <b>G</b> operator
 * @author Sylvain Hallé
 */
public class Always extends UnaryOperator 
{
	public Always(Processor p)
	{
		super(p);
	}
	
	public Always()
	{
		super();
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) 
	{
		if (m_lastValue != Value.INCONCLUSIVE)
		{
			outputs[0] = m_lastValue;
			return true;
		}
		spawn();
		for (int i = 0; i < m_pushables.size(); i++)
		{
			Pushable p = m_pushables.get(i);
			p.push(inputs[0]);
			SinkLast sink = m_sinks.get(i);
			Troolean.Value val = (Troolean.Value) sink.getLast()[0];
			if (val == Value.FALSE)
			{
				m_lastValue = Value.FALSE;
				m_sinks.clear();
				m_processors.clear();
				m_pushables.clear();
				outputs[0] = m_lastValue;
				return true;
			}
			if (val == Value.TRUE)
			{
				m_pushables.remove(i);
				m_sinks.remove(i);
				m_processors.remove(i);
				i--;
			}
		}
		outputs[0] = Value.INCONCLUSIVE;
		return true;
	}

	@Override
	public Always duplicate(boolean with_state) 
	{
		Always st = new Always(m_processor.duplicate());
		super.cloneInto(st, with_state);
		return st;
	}
}