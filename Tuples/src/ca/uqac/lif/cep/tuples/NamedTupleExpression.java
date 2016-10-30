package ca.uqac.lif.cep.tuples;

import java.util.Stack;

import ca.uqac.lif.cep.Processor;

class NamedTupleExpression extends TupleExpression
{
	public NamedTupleExpression(Processor p, String n) 
	{
		super(p, n);
	}

	public static void build(Stack<Object> stack)
	{
		Object o;
		Processor p;
		String name = (String) stack.pop();
		stack.pop(); // AS
		o = stack.pop(); // ( ?
		if (o instanceof String)
		{
			p = (Processor) stack.pop();
			stack.pop(); // )
		}
		else
		{
			p = (Processor) o;
		}
		NamedTupleExpression te = new NamedTupleExpression(p, name);
		stack.push(te);
	}
}