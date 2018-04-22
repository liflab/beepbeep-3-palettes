/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
package ca.uqac.lif.cep.xml;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.xml.XPathExpression;

/**
 * Processor that evaluates an XPath expression. This processor differs from
 * {@link ca.uqac.lif.cep.functions.ApplyFunction ApplyFunction} in that calls
 * to {@link #setContext(Context) setContext()} trigger the replacement of
 * placeholders in the underlying XPath expression by elements of the
 * processor's context object.
 * 
 * @author Sylvain Hallé
 */
public class XPathEvaluator extends ApplyFunction
{	
	/**
	 * Creates a new XPath evaluator
	 * @param expression A string representing an XPath expression to evaluate
	 */
	public XPathEvaluator(String expression)
	{
		super(new XPathFunction(expression));
	}

	/**
	 * Creates a new XPath evaluator
	 * @param exp An XPath expression to evaluate
	 */
	public XPathEvaluator(XPathExpression exp)
	{
		super(new XPathFunction(exp));
	}

	@Override
	public void setContext(Context c)
	{
		XPathFunction xpf = (XPathFunction) m_function;
		XPathExpression n_xpe = XPathFunction.evaluatePlaceholders(xpf.m_expression, c);
		m_function = new XPathFunction(n_xpe);
	}
	
	@Override
	public void setContext(String key, Object value)
	{
		XPathFunction xpf = (XPathFunction) m_function;
		Context c = new Context();
		c.put(key, value);
		XPathExpression n_xpe = XPathFunction.evaluatePlaceholders(xpf.m_expression, c);
		m_function = new XPathFunction(n_xpe);
	}
}
