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

import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.numbers.NumberCast;

/**
 * Outputs at most one non-zero event in an interval of width <i>n</i>.
 * @author Sylvain Hallé
 *
 */
public class Limiter extends SingleProcessor
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4340299428481717138L;

	protected final int m_limit;
	
	protected int m_counter;
	
	public Limiter()
	{
		this(5);
	}
	
	public Limiter(int width)
	{
		super(1, 1);
		m_limit = width;
		m_counter = 0;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		m_counter--;
		float value = NumberCast.getNumber(inputs[0]).floatValue();
		if (m_counter > 0 || value == 0)
		{
			// Ignore this event
			outputs.add(wrapObject(0));
			return true;
		}
		m_counter = m_limit;
		outputs.add(wrapObject(value));
		return true;
	}

	public static void build(ArrayDeque<Object> stack) 
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public Limiter duplicate()
	{
		return new Limiter(m_limit);
	}

}
