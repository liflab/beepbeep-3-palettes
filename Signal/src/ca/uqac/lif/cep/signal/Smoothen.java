package ca.uqac.lif.cep.signal;

import java.util.Set;

import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.tmf.WindowFunction;

public class Smoothen extends WindowFunction
{
	public Smoothen(int width)
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
		public ArrayAverage duplicate()
		{
			return this;
		}

		@Override
		public void evaluate(Object[] inputs, Object[] outputs) 
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
			// Nothing to do
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
