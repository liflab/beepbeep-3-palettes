/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2020 Sylvain Hallé

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

import ca.uqac.lif.cep.SynchronousProcessor;

/**
 * Boolean implementation of the LTL <b>F</b> processor
 * @author Sylvain Hallé
 */
public class Eventually extends SynchronousProcessor 
{
	protected int m_notTrueCount = 0;
	
	public Eventually()
	{
		super(1, 1);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_notTrueCount = 0;
	}
	
	@Override
	public Eventually duplicate(boolean with_state)
	{
		Eventually e = new Eventually();
		if (with_state)
		{
			e.m_notTrueCount = m_notTrueCount;
		}
		return e;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) 
	{
		Boolean v = (Boolean) inputs[0];
		if (v == true)
		{
			for (int i = 0; i <= m_notTrueCount; i++)
			{
				Object[] e = new Object[1];
				e[0] = true;
				outputs.add(e);
				m_outputCount++;
				associateToOutput(0, m_inputCount, 0, m_outputCount);
			}
			m_notTrueCount = 0;
		}
		else
		{
			m_notTrueCount++;
		}
		m_inputCount++;
		return true;
	}
}
