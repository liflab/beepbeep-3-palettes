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
 * Boolean implementation of the LTL <b>G</b> processor
 * @author Sylvain Hallé
 */
public class Globally extends SynchronousProcessor 
{
	protected int m_notFalseCount = 0;
	
	public Globally()
	{
		super(1, 1);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_notFalseCount = 0;
	}
	
	@Override
	public Globally duplicate(boolean with_state)
	{
		Globally g = new Globally();
		if (with_state)
		{
			g.m_notFalseCount = m_notFalseCount;
		}
		return g;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) 
	{
		Boolean v = (Boolean) inputs[0];
		if (v == false)
		{
			for (int i = 0; i <= m_notFalseCount; i++)
			{
				Object[] e = new Object[1];
				e[0] = false;
				outputs.add(e);
				associateToOutput(0, m_inputCount, 0, m_outputCount);
				m_outputCount++;
			}
			m_notFalseCount = 0;
		}
		else
		{
			m_notFalseCount++;
		}
		m_inputCount++;
		return true;
	}
}
