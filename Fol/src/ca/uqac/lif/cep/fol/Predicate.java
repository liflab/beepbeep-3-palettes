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
package ca.uqac.lif.cep.fol;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Constant;

public class Predicate extends SimpleFunction
{
	/**
	 * The predicate's name
	 */
	protected String m_name = null;

	/**
	 * The predicate's domain name for each of its arguments
	 */
	protected String[] m_domainNames;

	/**
	 * The definition of this predicate
	 */
	private Map<PredicateArgument,Boolean> m_definition;

	/**
	 * Creates a new predicate
	 * @param name The name of the predicate
	 * @param domain_names The name of the domains for each of its arguments
	 */
	public Predicate(String name, String ...domain_names)
	{
		super();
		m_name = name;
		m_domainNames = domain_names;
		m_definition = new ConcurrentHashMap<PredicateArgument,Boolean>();
	}
	
	/**
	 * Creates a new predicate by copying its contents from another predicate
	 * @param pred The predicate to copy from
	 */
	public Predicate(Predicate pred)
	{
		m_name = pred.m_name;
		m_domainNames = pred.m_domainNames;
		m_definition = new ConcurrentHashMap<PredicateArgument,Boolean>();
		m_definition.putAll(pred.m_definition);
	}
	
	/**
	 * Adds a new tuple to the definition of this predicate
	 * @param arguments The arguments
	 * @param value The value associated to these arguments
	 */
	public void addDefinition(PredicateArgument arguments, boolean value)
	{
		m_definition.put(arguments, value);
	}

	public void updateDefinition(Object[] inputs, boolean value)
	{
		PredicateArgument arg = new PredicateArgument(inputs);
		m_definition.put(arg, value);
	}

	public void updateDefinition(PredicateTuple tuple)
	{
		m_definition.put(tuple.m_arguments, tuple.m_value);
	}

	public String[] getDomainNames()
	{
		return m_domainNames;
	}
	
	public void clear()
	{
		m_definition.clear();
	}

	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append(m_definition);
		return out.toString();
	}

	@Override
	public void compute(Object[] inputs, Object[] outputs) 
	{
		PredicateArgument arg = new PredicateArgument(inputs);
		for (PredicateArgument def_arg : m_definition.keySet())
		{
			if (arg.matches(def_arg))
			{
				outputs[0] = m_definition.get(def_arg);
				return;				
			}
		}
		// No match: closed world assumption
		outputs[0] = false;
	}

	@Override
	public int getInputArity() 
	{
		return m_domainNames.length;
	}

	@Override
	public int getOutputArity() 
	{
		return 1;
	}

	@Override
	public void reset() 
	{
		m_definition.clear();
	}

	@Override
	public Predicate duplicate() 
	{
		Predicate pred = new Predicate(m_name, m_domainNames);
		pred.m_definition.putAll(m_definition);
		return pred;
	}

	@Override
	public Predicate duplicate(Context context) 
	{
		Predicate pred = new Predicate(m_name, m_domainNames);
		pred.m_definition.putAll(m_definition);
		pred.setContext(context);
		return pred;
	}

	@Override
	public void getInputTypesFor(Set<Class<?>> classes, int index)
	{
		if (index >= 0 && index < m_domainNames.length)
		{
			classes.add(Object.class);
		}
	}

	@Override
	public Class<?> getOutputTypeFor(int index)
	{
		return Boolean.class;
	}

	public static class PredicateArgument
	{
		/**
		 * The values of this argument
		 */
		Object[] m_values;

		/**
		 * Creates a new predicate argument with given values
		 * @param values The values
		 */
		public PredicateArgument(Object[] values)
		{
			super();
			m_values = values;
		}
		
		public Object get(int index)
		{
			if (index < 0 || index >= m_values.length)
			{
				return null;
			}
			return m_values[index];
		}
		
		public int size()
		{
			return m_values.length;
		}
		
		public boolean matches(PredicateArgument arg)
		{
			if (arg == null || arg.m_values.length != m_values.length)
			{
				return false;
			}
			for (int i = 0; i < m_values.length; i++)
			{
				Object o1 = m_values[i];
				Object o2 = arg.m_values[i];
				// TODO: detect wildcards better
				if (o1.toString().compareTo("_") != 0 && o2.toString().compareTo("_") != 0 && !(o1.equals(o2)))
				{
					return false;
				}
			}
			return true;			
		}

		@Override
		public int hashCode()
		{
			int value = 0;
			for (Object o : m_values)
			{
				value += o.hashCode();
			}
			return value;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof PredicateArgument))
			{
				return false;
			}
			PredicateArgument v = (PredicateArgument) o;
			if (v.m_values.length != m_values.length)
			{
				return false;
			}
			for (int i = 0; i < m_values.length; i++)
			{
				Object o1 = m_values[i];
				Object o2 = v.m_values[i];
				if (!o1.equals(o2))
				{
					return false;
				}
			}
			return true;
		}
		
		@Override
		public String toString()
		{
			StringBuilder out = new StringBuilder();
			for (int i = 0; i < m_values.length; i++)
			{
				if (i > 0)
				{
					out.append(",");
				}
				out.append(m_values[i]);
			}
			return out.toString();
		}
	}

	/**
	 * Special constant that can be used when evaluating a predicate to 
	 * indicate a "don't care" value
	 */
	public static class Wildcard extends Constant
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8316809576940654252L;
		public static final transient Wildcard instance = new Wildcard();
		
		Wildcard()
		{
			super("_");
		}

		@Override
		public void evaluate(Object[] inputs, Object[] outputs, Context context)
		{
			outputs[0] = this;
		}
		
		@Override
		public Wildcard duplicate()
		{
			return this;
		}
	}
}