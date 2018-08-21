package ca.uqac.lif.cep.fol;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import ca.uqac.lif.cep.CompoundFuture;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.UnaryFunction;

public abstract class QuantifierFunction extends Function
{
	protected String m_variable;
	
	protected UnaryFunction<?,Boolean> m_function;
	
	public QuantifierFunction(String x, UnaryFunction<?,Boolean> f) 
	{
		super();
		m_variable = x;
		m_function = f;
	}

	@Override
	public Future<? extends Object[]> evaluateFast(Object[] inputs, Object[] outputs, Context context, ExecutorService service) 
	{
		Collection<?> values = (Collection<?>) inputs[0];
		Object[][] all_vals = new Object[values.size()][1];
		@SuppressWarnings("unchecked")
		Future<? extends Object[]>[] all_futures = new Future[values.size()];
		int dom_count = 0;
		for (Object value : values)
		{
			Context new_context = new Context(context);
			Function exp = m_function.duplicate();
			all_vals[dom_count] = new Object[1];
			new_context.put(m_variable, value);
			all_futures[dom_count] = exp.evaluateFast(new Object[]{value}, all_vals[dom_count], new_context, service);
			dom_count++;
		}
		return newFuture(all_futures); 
	}

	@Override
	public void evaluate(Object[] inputs, Object[] outputs, Context context)
	{
		Collection<?> values = (Collection<?>) inputs[0];
		Object[][] all_vals = new Object[values.size()][1];
		int dom_count = 0;
		for (Object value : values)
		{
			Context new_context = new Context(context);
			Function exp = m_function.duplicate();
			all_vals[dom_count] = new Object[1];
			new_context.put(m_variable, value);
			exp.evaluate(new Object[]{value}, all_vals[dom_count], new_context);
			dom_count++;
		}
		getVerdict(all_vals, outputs);
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
	public void getInputTypesFor(Set<Class<?>> classes, int index) 
	{
		if (index == 0)
		{
			classes.add(Collection.class);
		}
	}

	@Override
	public Class<?> getOutputTypeFor(int index)
	{
		if (index == 0) 
		{
			return Boolean.class;
		}
		return null;
	}
	
	public static abstract class QuantifierFunctionFuture extends CompoundFuture<Boolean,Boolean[]>
	{
		// Nothing here
	}
	
	protected abstract void getVerdict(Object[] inputs, Object[] outputs);
	
	protected abstract Future<? extends Object[]> newFuture(Future<?>[] futures);
	
}
