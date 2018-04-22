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

import ca.uqac.lif.xml.TextElement;
import ca.uqac.lif.xml.XPathExpression;
import ca.uqac.lif.xml.XmlElement;

/**
 * Utility function to evaluate an XPath expression, ending with 
 * a <code>text()</code> element
 */
public class XPathFunctionGetNumber extends XPathFunctionGet<Number>
{	
	public XPathFunctionGetNumber(String exp)
	{
		super(exp, Number.class);
	}
	
	public XPathFunctionGetNumber(XPathExpression exp)
	{
		super(exp, Number.class);
	}

	@Override
	public Number castValue(XmlElement e)
	{
		if (e instanceof TextElement)
		{
			return Float.parseFloat(((TextElement) e).getText());
		}
		return 0;
	}
	
	@Override
	public XPathFunctionGetNumber duplicate(boolean with_state)
	{
		return new XPathFunctionGetNumber(m_expression.duplicate());
	}
}