/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2025 Sylvain Hallé

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

import ca.uqac.lif.cep.Stateful;
import ca.uqac.lif.cep.SynchronousProcessor;

/**
 * Boolean implementation of the LTL <b>U</b> processor
 * @author Sylvain Hallé
 */
public class Until extends SynchronousProcessor implements Stateful
{
	protected int m_eventCount = 0;
	
	public Until()
	{
		super(2, 1);
		m_eventCount = 0;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_eventCount = 0;
	}

	@Override
	protected boolean compute(Object[] input, Queue<Object[]> outputs)
	{
		Boolean left = (Boolean) input[0];
		Boolean right = (Boolean) input[1];
		m_eventCount++;
		if (right)
		{
			for (int i = 0; i < m_eventCount; i++)
			{
				Object[] e = new Object[1];
				e[0] = true;
				outputs.add(e);
				m_outputCount++;
			}
			m_eventCount = 0;
			m_inputCount++;
			return true;
		}
		assert !right;
		if (!left)
		{
			for (int i = 0; i < m_eventCount; i++)
			{
				Object[] e = new Object[1];
				e[0] = false;
				outputs.add(e);
				m_outputCount++;
			}
			m_eventCount = 0;
			m_inputCount++;
			return true;			
		}
		m_inputCount++;
		return true;
	}

	@Override
	public Until duplicate(boolean with_state)
	{
		Until u = new Until();
		if (with_state)
		{
			u.m_eventCount = m_eventCount;
		}
		return u;
	}
	
	/**
	 * @since 0.11
	 */
	@Override
	public Object getState()
	{
		return m_eventCount;
	}
}
