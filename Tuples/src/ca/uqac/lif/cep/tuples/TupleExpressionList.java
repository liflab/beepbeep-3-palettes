package ca.uqac.lif.cep.tuples;

import java.util.ArrayList;
import java.util.Stack;


/**
 * A list of input traces. This class exists only to provide
 * an object to build when parsing an expression.
 * 
 * @author Sylvain Hallé
 */
class TupleExpressionList extends ArrayList<TupleExpression>
{
	/**
	 * Dummy UID
	 */
	private static final long serialVersionUID = 1L;

	public static void build(Stack<Object> stack)
	{
		Object top = stack.peek();
		TupleExpressionList new_al = new TupleExpressionList();
		if (top instanceof TupleExpressionList)
		{
			TupleExpressionList al = (TupleExpressionList) stack.pop();
			stack.pop(); // ,
			TupleExpression def = (TupleExpression) stack.pop();
			new_al.add(def);
			new_al.addAll(al);
		}
		else
		{
			TupleExpression def = (TupleExpression) stack.pop();
			new_al.add(def);
		}
		stack.push(new_al);
	}

	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		boolean first = true;
		for (TupleExpression te : this)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				out.append(", ");
			}
			out.append(te);
		}
		return out.toString();
	}
}