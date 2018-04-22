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

import java.util.Collection;

import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.xml.XPathExpression;
import ca.uqac.lif.xml.XmlElement;

/**
 * Utility function to evaluate an XPath expression, ending with 
 * a <code>text()</code> element
 */
public abstract class XPathFunctionGet<T> extends UnaryFunction<XmlElement,T>
{	
	/**
	 * The expression to evaluate
	 */
	protected XPathExpression m_expression;
	
	public XPathFunctionGet(String exp, Class<T> clazz)
	{
		super(XmlElement.class, clazz);
		m_expression = XPathFunction.parseExpression(exp);
	}
	
	public XPathFunctionGet(XPathExpression exp, Class<T> clazz)
	{
		super(XmlElement.class, clazz);
		m_expression = exp;
	}

	@Override
	public T getValue(XmlElement x)
	{
		Collection<XmlElement> col = m_expression.evaluate(x);
		for (XmlElement xe : col)
		{
			return castValue(xe);
		}
		return null;
	}
	
	public abstract T castValue(XmlElement e);
	
	@Override
	public String toString()
	{
		return m_expression.toString();
	}
}