package ca.uqac.lif.cep.fol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.functions.Function;

public abstract class QuantifierInterpretation extends Function
{
	protected String m_variableName;
	
	protected String m_domainName;
	
	protected Function m_expression;
	
	protected boolean m_stopValue;
	
	protected boolean m_failFast = false;
	
	public QuantifierInterpretation(String variable_name, String domain_name, Function expression, boolean stop_value)
	{
		super();
		m_variableName = variable_name;
		m_domainName = domain_name;
		m_expression = expression;
		m_stopValue = stop_value;
	}
	
	@Override
	public void evaluate(Object[] inputs, Object[] out, Context context, EventTracker tracker) 
	{
		Interpretation inter = (Interpretation) inputs[0];
		Set<Object> values = new HashSet<Object>();
		values.addAll(inter.getDomain(m_domainName));
		int dom_count = 0;
		Context new_context = new Context(context);
		int num_values = values.size();
		List<Object[]> all_vals = new ArrayList<Object[]>(num_values);
		List<Function> all_expressions = new ArrayList<Function>(num_values);
		// Start the evaluation of the function for each value in the domain
		for (Object value : values)
		{
			Object[] return_values = new Object[1];
			Function exp = m_expression.duplicate();
			all_vals.add(return_values);
			all_expressions.add(exp);
			dom_count++;
			if (m_variableName.compareTo("v") == 0 && dom_count % 1 == 0)
			{
				System.out.println("Dom: " + dom_count);
			}
			new_context.put(m_variableName, value);
			exp.evaluate(inputs, return_values, new_context);
		}
		out[0] = !m_stopValue;
		for (int i = 0; i < num_values; i++)
		{
			Object[] return_values = all_vals.get(i);
			if (return_values != null && return_values.length > 0 
					&& return_values[0] instanceof Boolean 
					&& (Boolean) return_values[0] == m_stopValue)
			{
				out[0] = m_stopValue;
				if (m_failFast)
				{
					return;
				}
			}
		}
		return;
	}
	
	/**
	 * Sets whether the quantifier evaluates the functions in "fail fast"
	 * mode
	 * @param b Set to {@code true} to use fail fast
	 */
	public void setFailFast(boolean b)
	{
		m_failFast = b;
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs) 
	{
		evaluate(inputs, outputs, new Context());
	}

	@Override
	public int getInputArity() 
	{
		return 1;
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
		classes.add(Interpretation.class);
	}

	@Override
	public Class<?> getOutputTypeFor(int index)
	{
		return Boolean.class;
	}
}
