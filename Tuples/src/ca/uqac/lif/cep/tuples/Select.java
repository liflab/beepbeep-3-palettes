/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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

import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Creates a Tuple from an {@link AttributeGroup}. This performs roughly the
 * same function as the <code>SELECT</code> clause in an SQL statement.
 * 
 * @author Sylvain Hallé
 */
public class Select extends FunctionProcessor
{
	Select(SelectFunction comp) 
	{
		super(comp);
	}
	
	public Select(AttributeExpression ... expressions)
	{
		super(new SelectFunction(expressions));
	}
	
	/**
	 * The internal function performing the actual work for the
	 * <code>From</code> processor.
	 * 
	 * @author Sylvain Hallé
	 */
	public static class SelectFunction extends UnaryFunction<AttributeGroup,Tuple>
	{
		/**
		 * The expressions used to build the output tuple
		 */
		protected AttributeExpression[] m_expressions;
		
		SelectFunction()
		{
			super(AttributeGroup.class, Tuple.class);
		}
		
		/**
		 * Creates a new instance of the <code>SELECT</code> function
		 * @param expressions The definition of each attribute of the output
		 * tuples to create
		 */
		public SelectFunction(AttributeExpression ... expressions)
		{
			super(AttributeGroup.class, Tuple.class);
			m_expressions = expressions;
		}

		@Override
		public Tuple getValue(AttributeGroup group) 
		{
			String[] names = new String[m_expressions.length];
			Object[] values = new Object[m_expressions.length];
			for (int i = 0; i < m_expressions.length; i++)
			{
				AttributeExpression exp = m_expressions[i];
				names[i] = exp.getName();
				values[i] = exp.getValue(group);
			}
			TupleFixed tuple = new TupleFixed(names, values);
			return tuple;
		}
	}
}
