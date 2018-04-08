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
 * Troolean implementation of the LTL <b>G</b> operator
 * @author Sylvain Hallé
 */
public class Always extends UniformProcessor 
{
	protected Value m_lastValue = Value.INCONCLUSIVE;
	
	public Always()
	{
		super(1, 1);
	}
	
	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) 
	{
		Value v = Troolean.trooleanValue(inputs[0]);
		if (v == Value.FALSE || m_lastValue == Value.FALSE)
		{
			m_lastValue = Value.FALSE;
		}
		if (m_lastValue == Value.FALSE)
		{
			outputs[0] = Value.FALSE;
		}
		else
		{
			outputs[0] = Value.INCONCLUSIVE;
		}
		return true;
	}

	@Override
	public Always duplicate(boolean with_state) 
	{
		Always st = new Always();
		if (with_state)
		{
			st.m_lastValue = m_lastValue;
		}
		return st;
	}
}