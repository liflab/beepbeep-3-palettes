package ca.uqac.lif.cep.fol;

import ca.uqac.lif.cep.functions.Function;

public class ExistsInterpretation extends QuantifierInterpretation
{
	public ExistsInterpretation(String variable_name, String domain_name, Function expression)
	{
		super(variable_name, domain_name, expression, true);
	}

	@Override
	public ExistsInterpretation duplicate(boolean with_state) 
	{
		ExistsInterpretation ef = new ExistsInterpretation(m_variableName, m_domainName, m_expression.duplicate(with_state));
		return ef;
	}
	
	@Override
	public String toString()
	{
		return "exists " + m_variableName + " in " + m_domainName + " : " + m_expression;
	}

}
