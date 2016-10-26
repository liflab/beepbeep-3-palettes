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
		String name = (String) stack.pop();
		stack.pop(); // AS
		stack.pop(); // (
		Processor p = (Processor) stack.pop();
		stack.pop(); // )
		NamedTupleExpression te = new NamedTupleExpression(p, name);
		stack.push(te);
	}
}