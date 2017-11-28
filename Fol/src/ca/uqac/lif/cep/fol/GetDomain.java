package ca.uqac.lif.cep.fol;

import java.util.Set;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Function taking an interpretation as its input, and returning the
 * set of values associated with some domain as its output.
 */
@SuppressWarnings("rawtypes")
public class GetDomain extends UnaryFunction<Interpretation,Set>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5812468213644567902L;
	/**
	 * The name of the domain to fetch from the interpretation
	 */
	protected String m_domainName;
	
	/**
	 * Creates a new instance of domain function
	 * @param domain_name The name of the domain
	 * to fetch from the interpretation
	 */
	public GetDomain(String domain_name)
	{
		super(Interpretation.class, Set.class);
		m_domainName = domain_name;
	}

	@Override
	public Set getValue(Interpretation x)
	{
		return x.getDomain(m_domainName);
	}
	
	@Override
	public GetDomain duplicate()
	{
		return new GetDomain(m_domainName);
	}
}