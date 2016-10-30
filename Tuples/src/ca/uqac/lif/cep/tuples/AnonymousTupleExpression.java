package ca.uqac.lif.cep.tuples;

import java.util.Stack;

import ca.uqac.lif.cep.Processor;

class AnonymousTupleExpression extends TupleExpression
{
	public AnonymousTupleExpression(Processor p) 
	{
		super(p, null);
	}

	public static void build(Stack<Object> stack)
	{
		Object o;
		Processor p;
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
		AnonymousTupleExpression te = new AnonymousTupleExpression(p);
		stack.push(te);
	}
}