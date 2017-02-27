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
import java.util.ArrayDeque;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.numbers.NumberCast;

public class Threshold extends SingleProcessor
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
		float value = NumberCast.getNumber(inputs[0]).floatValue();
		if (Math.abs(value) > m_threshold)
		{
			outputs.add(wrapObject(value));
			return true;
		}
		outputs.add(wrapObject(0));
		return true;
	}

	public static void build(ArrayDeque<Object> stack) throws ConnectorException
	{
		float t_value = NumberCast.getNumber(stack.pop()).floatValue();
		stack.pop(); // THRESHOLD
		stack.pop(); // (
		Processor p = (Processor) stack.pop();
		stack.pop(); // )
		Threshold t = new Threshold(t_value);
		Connector.connect(p, t);
		stack.push(t);		
	}
	
	@Override
	public Threshold clone()
	{
		return new Threshold(m_threshold);
	}

}
