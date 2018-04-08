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
import ca.uqac.lif.cep.ltl.Troolean.Value;

/**
 * Troolean implementation of the LTL <b>X</b> operator
 * @author Sylvain Hallé
 */
public class After extends UnaryOperator 
{
	/**
	 * The number of events received so far
	 */
	private int m_eventCount = 0;
	
	protected Value m_valueToReturn = Value.INCONCLUSIVE;
	
	public After(Processor p)
	{
		super(p);
	}

	public After() 
	{
		super();
	}

	@Override
	protected boolean compute(Object[] input, Object[] outputs) 
	{
		if (m_eventCount == 0)
		{
			spawn();
			m_eventCount = 1;
			outputs[0] = Value.INCONCLUSIVE;
			return true;
		}
		else if (m_eventCount == 1)
		{
			m_eventCount = 2;
			m_pushables.get(0).push(input[0]);
			m_valueToReturn = (Value) m_sinks.get(0).getLast()[0];
		}
		outputs[0] = m_valueToReturn;
		return true;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_valueToReturn = Value.INCONCLUSIVE;
		m_eventCount = 0;
	}

	@Override
	public After duplicate(boolean with_state) 
	{
		After a = new After(m_processor.duplicate(with_state));
		super.cloneInto(a, with_state);
		if (with_state)
		{
			a.m_eventCount = m_eventCount;
		}
		return a;
	}

}
