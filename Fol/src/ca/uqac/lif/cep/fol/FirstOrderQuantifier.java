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
package ca.uqac.lif.cep.fol;

import java.util.Collection;
import java.util.Set;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;

public abstract class FirstOrderQuantifier extends Function
{
	protected String m_variable;
	
	protected Function m_function;
	
	protected Function m_domainFunction;
	
	public FirstOrderQuantifier(String x, Function d, Function f) 
	{
		super();
		m_variable = x;
		m_function = f;
		m_domainFunction = d;
	}
	
	public FirstOrderQuantifier(String x, Function f) 
  {
    this(x, null, f);
  }

	@Override
	public void evaluate(Object[] inputs, Object[] outputs, Context context)
	{
	  Collection<?> values;
    if (m_domainFunction == null)
    {
      values = (Collection<?>) inputs[0];
    }
    else
    {
      Object[] dom = new Object[m_domainFunction.getOutputArity()];
      m_domainFunction.evaluate(inputs, dom, context);
      values = (Collection<?>) dom[0];
    }
		Object[][] all_vals = new Object[values.size()][1];
		int dom_count = 0;
		for (Object value : values)
		{
			Context new_context = new Context(context);
			Function exp = m_function.duplicate();
			all_vals[dom_count] = new Object[1];
			new_context.put(m_variable, value);
			if (m_domainFunction == null)
			{
			  exp.evaluate(new Object[]{value}, all_vals[dom_count], new_context);
			}
			else
			{
			  exp.evaluate(inputs, all_vals[dom_count], new_context);
			}
			dom_count++;
		}
		getVerdict(all_vals, outputs);
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
	
	protected abstract void getVerdict(Object[][] inputs, Object[] outputs);
}
