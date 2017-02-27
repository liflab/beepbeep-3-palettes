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
package ca.uqac.lif.cep.tuples;

import java.util.Set;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;

/**
 * Merges the key-value pairs of multiple tuples into a single tuple.
 * If two tuples have the same key, the value in the resulting tuple
 * is that of <em>one</em> of these tuples; which one is left undefined.
 * However, if the tuples have the same value for their common keys,
 * the resuting tuple is equivalent to that of a relational JOIN
 * operation.
 * 
 * @author Sylvain Hallé
 *
 */
public class MergeTuples extends Function
{
	/**
	 * The number of input tuples to merge
	 */
	protected int m_inputArity;

	/**
	 * A helper class to build instances of the merged tuples
	 */
	protected FixedTupleBuilder m_builder = null;

	public MergeTuples()
	{
		this(2);
	}

	public MergeTuples(int arity)
	{
		super();
		m_inputArity = arity;
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs, Context context)
	{
		evaluate(inputs, outputs);
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs)
	{
		Tuple new_t = new TupleMap();
		for (int i = 0; i < inputs.length; i++)
		{
			Tuple t = (Tuple) inputs[i];
			new_t.putAll(t);
		}
		outputs[0] = new_t;
	}

	@Override
	public int getInputArity() 
	{
		return m_inputArity;
	}

	@Override
	public int getOutputArity() 
	{
		return 1;
	}

	@Override
	public void reset() 
	{
		m_builder = null;
	}

	@Override
	public MergeTuples clone() 
	{
		return new MergeTuples(m_inputArity);
	}

	@Override
	public void getInputTypesFor(Set<Class<?>> classes, int index)
	{
		classes.add(Tuple.class);
	}

	@Override
	public Class<?> getOutputTypeFor(int index)
	{
		return Tuple.class;
	}

}
