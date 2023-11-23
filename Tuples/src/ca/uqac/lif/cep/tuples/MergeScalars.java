/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2023 Sylvain Hallé

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

import java.util.List;
import java.util.Set;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.functions.Function;

/**
 * Creates a named tuple out of multiple input traces, each containing scalar
 * values. For example, if {@code p1} and {@code p2} are two processors that
 * return numbers, one can create a trace of tuples with attributes "foo" and
 * "bar", where foo is the value of the event in p1 and bar is the value of the
 * event in p2, by writing:
 * <pre>
 * FunctionProcessor fp = new FunctionProcessor(new MergeScalars("foo", "bar"));
 * Connector.connect(p1, 0, fp, 0);
 * Connector.connect(p2, 0, fp, 1);
 * </pre>
 * @author Sylvain Hallé
 */
public class MergeScalars extends Function 
{
	/**
	 * The names given to the attributes corresponding to each scalar
	 */
	protected final String[] m_attributeNames;
	
	protected FixedTupleBuilder m_builder;
	
	public MergeScalars(String ... names)
	{
		super();
		m_attributeNames = names;
		m_builder = new FixedTupleBuilder(names);
	}
	
	public MergeScalars(List<String> names)
	{
		super();
		m_attributeNames = new String[names.size()];
		for (int i = 0; i < names.size(); i++)
		{
			m_attributeNames[i] = names.get(i);
		}
		m_builder = new FixedTupleBuilder(m_attributeNames);
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs, Context context, EventTracker tracker) 
	{
		outputs[0] = m_builder.createTuple(inputs);
		if (tracker != null)
		{
		  for (int i = 0; i < inputs.length; i++)
		  {
		    tracker.associateToOutput(-1, i, 0, 0, 0);
		  }
		}
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs) 
	{
		evaluate(inputs, outputs, null);
	}

	@Override
	public int getInputArity()
	{
		return m_attributeNames.length;
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
	public Function duplicate(boolean with_state) 
	{
		return new MergeScalars(m_attributeNames);
	}

	@Override
	public void getInputTypesFor(Set<Class<?>> classes, int index)
	{
		classes.add(Connector.Variant.class);
		
	}

	@Override
	public Class<?> getOutputTypeFor(int index) 
	{
		return TupleFixed.class;
	}
}
