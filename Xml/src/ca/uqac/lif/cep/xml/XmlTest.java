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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.xml.TextElement;
import ca.uqac.lif.xml.XPathExpression.XPathParseException;
import ca.uqac.lif.xml.XmlElement;
import ca.uqac.lif.xml.XmlElement.XmlParseException;

/**
 * Unit tests for the XML processors
 * @author Sylvain Hallé
 */
public class XmlTest
{

	@Test
	public void testSingle1() 
	{
		ApplyFunction feeder = new ApplyFunction(ParseXml.instance);
		Pushable in = feeder.getPushableInput(0);
		assertNotNull(in);
		SinkLast sink = new SinkLast(1);
		Connector.connect(feeder, sink);
		in.push("<a>123</a>");
		Object[] os = sink.getLast();
		assertNotNull(os);
		assertEquals(1, os.length);
		assertTrue(os[0] instanceof XmlElement);
	}
	
	@Test
	public void testXPath1() throws XPathParseException, XmlParseException
	{
		ApplyFunction xpath = new ApplyFunction(new XPathFunction("a/b/text()"));
		Pushable in = xpath.getPushableInput(0);
		assertNotNull(in);
		SinkLast sink = new SinkLast(1);
		Connector.connect(xpath, sink);
		in.push(XmlElement.parse("<a><b>1</b><b>2</b></a>"));
		Object[] os = sink.getLast();
		assertNotNull(os);
		assertTrue(os[0] instanceof Collection<?>);
	}
	
	@Test
  public void testXPath2() throws XPathParseException, XmlParseException
  {
    XPathFunction xpath = new XPathFunction("a[b=$x]/c/text()");
    Object[] out = new Object[1];
    Context c = new Context();
    c.put("x", "1");
    xpath.evaluate(new Object[] {XmlElement.parse("<a><b>1</b><c>2</c></a>")}, out, c);
    assert (out[0] instanceof Collection<?>);
    @SuppressWarnings("unchecked")
    Collection<XmlElement> col = (Collection<XmlElement>) out[0];
    assertEquals(1, col.size());
    for (XmlElement xe : col)
    {
      assertTrue(xe instanceof TextElement);
      assertEquals("2", xe.toString());
    }
  }

}
