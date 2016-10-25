package ca.uqac.lif.cep.newtuples;

import java.util.Stack;

import ca.uqac.lif.cep.functions.Function;

class AnonymousAttributeExpression
{
	public static void build(Stack<Object> stack)
	{
		stack.pop(); // (
		Function expression = (Function) stack.pop();
		stack.pop(); // )
		AttributeExpression ae = new AttributeExpression(expression, null);
		stack.push(ae);
	}
}