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

import ca.uqac.lif.xml.XPathExpression;
import ca.uqac.lif.xml.XmlElement;
import java.util.Collection;

/**
 * Utility function to evaluate an XPath expression, ending with 
 * a <code>text()</code> element
 */
public abstract class XPathFunctionGet<T> extends XPathFunction
{	
	/**
	 * The type to cast the output to
	 */
	protected Class<T> m_outputType;
	
	public XPathFunctionGet(String exp, Class<T> clazz)
	{
		super(exp);
		m_outputType = clazz;
	}
	
	public XPathFunctionGet(XPathExpression exp, Class<T> clazz)
	{
		super(exp);
		m_outputType = clazz;
	}
	
	@Override
	protected T postProcess(Collection<XmlElement> col)
	{
	  for (XmlElement e : col)
	  {
	    return castValue(e);
	  }
	  return null;
	}
	
	protected abstract T castValue(XmlElement e);
}