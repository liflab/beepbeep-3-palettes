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

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.ltl.Troolean.Value;

/**
 * Troolean implementation of the LTL <b>F</b> processor
 * @author Sylvain Hallé
 */
public class Sometime extends UniformProcessor 
{
	protected Value m_lastValue = Value.INCONCLUSIVE;
	
	public Sometime()
	{
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) 
	{
		Value v = Troolean.trooleanValue(inputs[0]);
		if (v == Value.TRUE || m_lastValue == Value.TRUE)
		{
			m_lastValue = Value.TRUE;
		}
		if (m_lastValue == Value.TRUE)
		{
			outputs[0] = Value.TRUE;
		}
		else
		{
			outputs[0] = Value.INCONCLUSIVE;
		}
		return true;
	}

	@Override
	public Sometime duplicate(boolean with_state) 
	{
		Sometime st = new Sometime();
		if (with_state)
		{
			st.m_lastValue = m_lastValue;
		}
		return st;
	}
}