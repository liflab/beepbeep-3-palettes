/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2025 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	public GetDomain duplicate(boolean with_context)
	{
		return new GetDomain(m_domainName);
	}
}