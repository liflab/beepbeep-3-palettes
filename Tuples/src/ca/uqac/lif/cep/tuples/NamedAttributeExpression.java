package ca.uqac.lif.cep.tuples;

import java.util.Stack;

import ca.uqac.lif.cep.functions.Function;

class NamedAttributeExpression
{
	public static void build(Stack<Object> stack)
	{
		String name = (String) stack.pop();
		stack.pop(); // AS
		stack.pop(); // (
		Function expression = (Function) stack.pop();
		stack.pop(); // )
		AttributeExpression ae = new AttributeExpression(expression, name);
		stack.push(ae);
	}
}