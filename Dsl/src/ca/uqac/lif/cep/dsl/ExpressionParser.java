package ca.uqac.lif.cep.dsl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Stack;

import ca.uqac.lif.bullwinkle.BnfParser;
import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.BnfParser.ParseException;
import ca.uqac.lif.bullwinkle.ParseNode;
import ca.uqac.lif.bullwinkle.ParseNodeVisitor;

public abstract class ExpressionParser<T> implements ParseNodeVisitor
{
	protected Stack<Object> m_stack;
	
	protected T m_builtObject;
	
	protected BnfParser m_parser;
	
	Map<String,Method> m_buildMethods;
	
	public ExpressionParser()
	{
		super();
		m_parser = new BnfParser();
		try 
		{
			m_parser.setGrammar(getGrammar());
		}
		catch (InvalidGrammarException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public T parse(String expression) throws ParseException
	{
		ParseNode tree = m_parser.parse(expression);
		return build(tree);
	}
	
	public T build(ParseNode tree)
	{
		m_stack = new Stack<Object>();
		preVisit();
		tree.postfixAccept(this);
		m_builtObject = postVisit(m_stack);
		return m_builtObject;
	}
	
	protected abstract void preVisit();
	
	protected abstract T postVisit(Stack<Object> stack);
	
	protected abstract String getGrammar();
	
	@Override
	public void pop()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ParseNode node)
	{
		String token_name = node.getToken();
		if (token_name.startsWith("<"))
		{
			String method_name = "do" + token_name.substring(1, token_name.length() - 1);
			Method m;
			try
			{
				m = this.getClass().getDeclaredMethod(method_name, Stack.class);
				if (m != null)
				{
					m.invoke(this, m_stack);
				}
				else
				{
					// Ignore
				}
			} 
			catch (NoSuchMethodException e)
			{
				// That's OK
			}
			catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			m_stack.push(token_name);
		}
	}

}
