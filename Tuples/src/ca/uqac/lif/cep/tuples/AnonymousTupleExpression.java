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
		stack.pop(); // (
		Processor p = (Processor) stack.pop();
		stack.pop(); // )
		AnonymousTupleExpression te = new AnonymousTupleExpression(p);
		stack.push(te);
	}
}