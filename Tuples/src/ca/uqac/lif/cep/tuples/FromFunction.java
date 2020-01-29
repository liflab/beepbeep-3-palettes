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
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.functions.Function;

/**
 * The internal function performing the actual work for the
 * <code>From</code> processor.
 * 
 * @author Sylvain Hallé
 */
public class FromFunction extends Function
{
	/**
	 * The name associated to each input trace in the <code>FROM</code>
	 * clause
	 */
	private final String[] m_traceNames;
	
	public FromFunction(String ... trace_names)
	{
		super();
		m_traceNames = trace_names;
	}
	
	@Override
	public void evaluate(Object[] inputs, Object[] out, Context context, EventTracker tracker) 
	{
		AttributeGroup group = new AttributeGroup(m_traceNames);
		for (int i = 0; i < m_traceNames.length; i++)
		{
			group.add(i, (Tuple) inputs[i]);
		}
		out[0] = group;
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs) 
	{
		evaluate(inputs, outputs, null);
	}

	@Override
	public int getInputArity() 
	{
		return m_traceNames.length;
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
	public FromFunction duplicate(boolean with_state) 
	{
		return new FromFunction(m_traceNames);
	}

	@Override
	public void getInputTypesFor(Set<Class<?>> classes, int index) 
	{
		classes.add(Tuple.class);
	}

	@Override
	public Class<?> getOutputTypeFor(int index) 
	{
		return AttributeGroup.class;
	}
}