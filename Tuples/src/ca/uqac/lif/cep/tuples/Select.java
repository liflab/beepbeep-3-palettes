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

import java.util.Collection;
import java.util.ArrayDeque;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.Variant;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Creates a Tuple from an {@link AttributeGroup}. This performs roughly the
 * same function as the <code>SELECT</code> clause in an SQL statement.
 * 
 * @author Sylvain Hallé
 */
public class Select extends ApplyFunction
{
	public Select(SelectFunction comp) 
	{
		super(comp);
	}
	
	public Select(AttributeExpression ... expressions)
	{
		super(new SelectFunction(expressions));
	}
	
	public Select(Collection<AttributeExpression> expressions)
	{
		super(new SelectFunction(expressions));
	}
	
	/**
	 * The internal function performing the actual work for the
	 * <code>From</code> processor.
	 * 
	 * @author Sylvain Hallé
	 */
	public static class SelectFunction extends UnaryFunction<AttributeGroup,Object>
	{
		/**
		 * The expressions used to build the output tuple
		 */
		protected AttributeExpression[] m_expressions;
		
		SelectFunction()
		{
			super(AttributeGroup.class, Object.class);
		}
		
		/**
		 * Creates a new instance of the <code>SELECT</code> function
		 * @param expressions The definition of each attribute of the output
		 * tuples to create
		 */
		public SelectFunction(AttributeExpression ... expressions)
		{
			super(AttributeGroup.class, Object.class);
			m_expressions = expressions;
		}
		
		/**
		 * Creates a new instance of the <code>SELECT</code> function
		 * @param expressions The definition of each attribute of the output
		 * tuples to create
		 */
		public SelectFunction(Collection<AttributeExpression> expressions)
		{
			super(AttributeGroup.class, Object.class);
			int size = expressions.size();
			m_expressions = new AttributeExpression[size];
			expressions.toArray(m_expressions);
		}

		@Override
		public Object getValue(AttributeGroup group) 
		{
			String[] names = new String[m_expressions.length];
			Object[] values = new Object[m_expressions.length];
			boolean contains_named = false;
			Object no_named = null;
			for (int i = 0; i < m_expressions.length; i++)
			{
				AttributeExpression exp = m_expressions[i];
				names[i] = exp.getName();
				Object value = exp.getValue(group);
				if (names[i] == null)
				{
					no_named = value;
				}
				else
				{
					contains_named = true;
				}
				values[i] = exp.getValue(group);
			}
			if (contains_named || m_expressions.length > 1)
			{
				// Tuple contains at least one "... AS name" construct,
				// or more than one value: return result as a tuple
				TupleFixed tuple = new TupleFixed(names, values);
				return tuple;
			}
			else
			{
				// Tuple contains exactly one unnamed value: return it
				// directly, without wrapping it in a tuple
				return no_named;
			}
		}
		
		@Override
		public Class<?> getOutputTypeFor(int index)
		{
			/*
			 * We guess the output type based on the contents of the SELECT
			 * expression.
			 * - If it contains more than one attribute expression, the
			 *   return type is Tuple
			 * - Otherwise, the return type is that of the unnamed
			 *   expression 
			 */
			if (m_expressions.length > 1)
			{
				return Tuple.class;
			}
			Class<?> type = Variant.class;
			for (AttributeExpression exp : m_expressions)
			{
				if (exp.getName() == null)
				{
					type = exp.m_expression.getOutputTypeFor(0);
					break;
				}
			}
			return type;
		}
	}
	
	public static void build(ArrayDeque<Object> stack) 
	{
		Processor from = (Processor) stack.pop();
		AttributeExpressionList ael = (AttributeExpressionList) stack.pop();
		stack.pop(); // SELECT
		Select sel = new Select(ael);
		Connector.connect(from, sel);
		stack.push(sel);
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append("SELECT ").append(m_function);
		return out.toString();
	}
}
