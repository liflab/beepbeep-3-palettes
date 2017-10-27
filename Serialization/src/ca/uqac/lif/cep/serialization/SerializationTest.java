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
package ca.uqac.lif.cep.serialization;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.tmf.QueueSource;

public class SerializationTest
{
	@Test
	public void testNumber1() throws ConnectorException
	{
		QueueSource source = new QueueSource();
		source.addEvent(1.3f);
		source.addEvent(2.6f);
		source.addEvent(3.9f);
		FunctionProcessor ser = new FunctionProcessor(new JsonSerialize());
		FunctionProcessor deser = new FunctionProcessor(new JsonDeserialize<Number>(Number.class));
		Connector.connect(source, ser);
		Connector.connect(ser, deser);
		Pullable p = deser.getPullableOutput();
		Number n;
		Object o = p.pull();
		assertNotNull(o);
		assertTrue(o instanceof Number);
		n = (Number) o;
		assertEquals(1.3f, n.floatValue(), 0.001f);
		n = (Number) p.pull();
		assertEquals(2.6f, n.floatValue(), 0.001f);
		n = (Number) p.pull();
		assertEquals(3.9f, n.floatValue(), 0.001f);
	}
	
	@Test
	public void testCompound1() throws ConnectorException
	{
		QueueSource source = new QueueSource();
		CompoundObject c1 = new CompoundObject(3, "foo", null);
		CompoundObject c2 = new CompoundObject(8, "bar", new CompoundObject(7, "arf", null));
		CompoundObject c3 = new CompoundObject(5, "baz", null);
		source.addEvent(c1);
		source.addEvent(c2);
		source.addEvent(c3);
		FunctionProcessor ser = new FunctionProcessor(new JsonSerialize());
		FunctionProcessor deser = new FunctionProcessor(new JsonDeserialize<CompoundObject>(CompoundObject.class));
		Connector.connect(source, ser);
		Connector.connect(ser, deser);
		Pullable p = deser.getPullableOutput();
		CompoundObject co;
		Object o = p.pull();
		assertNotNull(o);
		assertTrue(o instanceof CompoundObject);
		co = (CompoundObject) o;
		assertEquals(c1, co);
		co = (CompoundObject) p.pull();
		assertEquals(c2, co);
		co = (CompoundObject) p.pull();
		assertEquals(c3, co);
	}
	
	@Test
	public void testCompoundString1() throws ConnectorException
	{
		QueueSource source = new QueueSource();
		CompoundObject c1 = new CompoundObject(3, "foo", null);
		CompoundObject c2 = new CompoundObject(8, "bar", new CompoundObject(7, "arf", null));
		CompoundObject c3 = new CompoundObject(5, "baz", null);
		source.addEvent(c1);
		source.addEvent(c2);
		source.addEvent(c3);
		FunctionProcessor ser = new FunctionProcessor(new JsonSerializeString());
		FunctionProcessor deser = new FunctionProcessor(new JsonDeserializeString<CompoundObject>(CompoundObject.class));
		Connector.connect(source, ser);
		Connector.connect(ser, deser);
		Pullable p = deser.getPullableOutput();
		CompoundObject co;
		Object o = p.pull();
		assertNotNull(o);
		assertTrue(o instanceof CompoundObject);
		co = (CompoundObject) o;
		assertEquals(c1, co);
		co = (CompoundObject) p.pull();
		assertEquals(c2, co);
		co = (CompoundObject) p.pull();
		assertEquals(c3, co);
	}
	
	/**
	 * A dummy object used to test serialization
	 */
	protected static class CompoundObject
	{
		int a;
		String b;
		CompoundObject c;
		
		protected CompoundObject()
		{
			super();
		}
		
		public CompoundObject(int a, String b, CompoundObject c)
		{
			super();
			this.a = a;
			this.b = b;
			this.c = c;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof CompoundObject))
			{
				return false;
			}
			CompoundObject co = (CompoundObject) o;
			if (this.a != co.a || this.b.compareTo(co.b) != 0)
			{
				return false;
			}
			if ((this.c == null && co.c == null) || (this.c != null && co.c != null && this.c.equals(co.c)))
			{
				return true;
			}
			return false;
		}
	}
}
