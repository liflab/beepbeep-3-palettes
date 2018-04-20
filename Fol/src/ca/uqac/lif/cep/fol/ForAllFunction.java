package ca.uqac.lif.cep.fol;

import ca.uqac.lif.cep.functions.Function;

public class ForAllFunction extends QuantifierFunction
{
	public ForAllFunction(String variable_name, String domain_name, Function expression)
	{
		super(variable_name, domain_name, expression, false);
	}

	@Override
	public ForAllFunction duplicate(boolean with_state) 
	{
		return new ForAllFunction(m_variableName, m_domainName, m_expression.duplicate(with_state));
	}
	@Override
	public String toString()
	{
		return "forall " + m_variableName + " in " + m_domainName + " : " + m_expression;
	}
}
