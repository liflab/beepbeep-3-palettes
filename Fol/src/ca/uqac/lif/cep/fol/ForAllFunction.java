package ca.uqac.lif.cep.fol;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;

public class ForAllFunction extends QuantifierFunction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2588269178944223114L;

	public ForAllFunction(String variable_name, String domain_name, Function expression)
	{
		super(variable_name, domain_name, expression, false);
	}

	@Override
	public ForAllFunction duplicate() 
	{
		return new ForAllFunction(m_variableName, m_domainName, m_expression.duplicate());
	}
	
	@Override
	public ForAllFunction duplicate(Context context) 
	{
		return new ForAllFunction(m_variableName, m_domainName, m_expression.duplicate(context));
	}
	
	@Override
	public String toString()
	{
		return "forall " + m_variableName + " in " + m_domainName + " : " + m_expression;
	}
}
