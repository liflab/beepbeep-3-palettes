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
package ca.uqac.lif.cep.signal;

import java.util.Queue;

import ca.uqac.lif.cep.SynchronousProcessor;

public class Threshold extends SynchronousProcessor
{
	/**
	 * The threshold to cut values
	 */
	protected final float m_threshold;
	
	public Threshold(float threshold)
	{
		super(1, 1);
		m_threshold = threshold;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		float value = ((Number) inputs[0]).floatValue();
		if (Math.abs(value) > m_threshold)
		{
			outputs.add(new Object[] {value});
			return true;
		}
		outputs.add(new Object[] {0});
		return true;
	}
	
	@Override
	public Threshold duplicate(boolean with_state)
	{
		return new Threshold(m_threshold);
	}

}
