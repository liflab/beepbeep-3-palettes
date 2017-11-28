/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

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

import java.util.ArrayDeque;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.xml.XmlElement;
import ca.uqac.lif.xml.XmlElement.XmlParseException;

/**
 * Processor that turns input strings into output XML documents
 */
public class XmlFeeder extends FunctionProcessor
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5527196983530487846L;

	public XmlFeeder()
	{
		super(XmlParsingFunction.instance);
	}
	
	/**
	 * Function that converts a string into an XML element
	 */
	public static class XmlParsingFunction extends UnaryFunction<String,XmlElement> 
	{
		/**
		 * Instance of the function
		 */
		public static XmlParsingFunction instance = new XmlParsingFunction();
		
		private XmlParsingFunction()
		{
			super(String.class, XmlElement.class);
		}
		
		@Override
		public /*@Nullable*/ XmlElement getValue(String x)
		{
			try 
			{
				return XmlElement.parse(x);
			} 
			catch (XmlParseException e) 
			{
				// Do nothing
			}
			return null;
		}
	}
	
	public static void build(ArrayDeque<Object> stack) 
	{
		Processor p = (Processor) stack.pop();
		stack.pop(); // OF
		stack.pop(); // XML
		stack.pop(); // THE
		XmlFeeder feeder = new XmlFeeder();
		Connector.connect(p, feeder);
		stack.push(feeder);
	}
}
