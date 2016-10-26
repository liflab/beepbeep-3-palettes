package ca.uqac.lif.cep.tuples;

import java.util.Set;

import ca.uqac.lif.cep.Context;
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
	public Object[] evaluate(Object[] inputs, Context context) 
	{
		Object[] out = new Object[1];
		AttributeGroup group = new AttributeGroup(m_traceNames);
		for (int i = 0; i < m_traceNames.length; i++)
		{
			group.add(i,  (Tuple) inputs[i]);
		}
		out[0] = group;
		return out;
	}

	@Override
	public Object[] evaluate(Object[] inputs) 
	{
		return evaluate(inputs, null);
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
		// Nothing to do
	}

	@Override
	public FromFunction clone() 
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