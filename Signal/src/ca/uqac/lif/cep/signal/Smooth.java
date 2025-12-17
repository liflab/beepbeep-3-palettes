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
package ca.uqac.lif.cep.signal;

import java.util.Set;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tmf.WindowFunction;

public class Smooth extends WindowFunction
{
	public Smooth(int width)
	{
		super(new ArrayAverage(width));
	}
	
	/**
	 * Computes the average of values in an array
	 */
	protected static class ArrayAverage extends Function
	{	
		/**
		 * The input arity of this function
		 */
		protected int m_width;
		
		public ArrayAverage(int width)
		{
			super();
			m_width = width;
		}
		
		@Override
		public ArrayAverage duplicate(boolean with_state)
		{
			return this;
		}

		@Override
		public void evaluate(Object[] inputs, Object[] outputs, Context context) 
		{
			if (m_width == 0)
			{
				outputs[0] = 0f;
				return;
			}
			float sum = 0;
			for (Object o : inputs)
			{
				sum += ((Number) o).floatValue();
			}
			outputs[0] = sum / (float) inputs.length;
		}

		@Override
		public int getInputArity()
		{
			return m_width;
		}

		@Override
		public int getOutputArity() 
		{
			return 1;
		}

		@Override
		public void reset() 
		{
			super.reset();
		}

		@Override
		public void getInputTypesFor(Set<Class<?>> classes, int index) 
		{
			classes.add(Number.class);
		}

		@Override
		public Class<?> getOutputTypeFor(int index) 
		{
			return Number.class;
		}
	}
}
