/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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
package ca.uqac.lif.cep.newtuples.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.interpreter.Interpreter;
import ca.uqac.lif.cep.interpreter.Interpreter.ParseException;
import ca.uqac.lif.cep.newtuples.AttributeGroup;
import ca.uqac.lif.cep.newtuples.PackageExtension;
import ca.uqac.lif.cep.newtuples.Tuple;
import ca.uqac.lif.cep.newtuples.TupleFixed;
import ca.uqac.lif.cep.tmf.QueueSource;

public class GrammarTest 
{
	protected Interpreter m_interpreter;
	
	@Before
	public void setup()
	{
		m_interpreter = new Interpreter();
		m_interpreter.extendGrammar(PackageExtension.class);
		m_interpreter.setDebugMode(true);
	}
	
	@Test
	public void testFrom1() throws ParseException
	{
		QueueSource qs = getQueueSource1();
		m_interpreter.addPlaceholder("@foo", "processor", qs);
		Processor proc = (Processor) m_interpreter.parseQuery("FROM (@foo) AS a");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof AttributeGroup);
		AttributeGroup ag = (AttributeGroup) o;
		assertEquals(0, ((Number) ag.getAttribute("a", "x")).intValue());
		assertNull(ag.getAttribute("b", "x"));
	}
	
	@Test
	public void testFrom2() throws ParseException
	{
		m_interpreter.addPlaceholder("@foo", "processor", getQueueSource1());
		m_interpreter.addPlaceholder("@bar", "processor", getQueueSource2());
		Processor proc = (Processor) m_interpreter.parseQuery("FROM (@foo) AS a, (@bar) AS b");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof AttributeGroup);
		AttributeGroup ag = (AttributeGroup) o;
		assertEquals(0, ((Number) ag.getAttribute("a", "x")).intValue());
		assertEquals(10, ((Number) ag.getAttribute("b", "z")).intValue());
		assertNull(ag.getAttribute("b", "x"));
	}
	
	@Test
	public void testFrom3() throws ParseException
	{
		QueueSource qs = getQueueSource1();
		m_interpreter.addPlaceholder("@foo", "processor", qs);
		Processor proc = (Processor) m_interpreter.parseQuery("FROM (@foo)");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof AttributeGroup);
		AttributeGroup ag = (AttributeGroup) o;
		assertEquals(0, ((Number) ag.getAttribute("x")).intValue());
		assertNull(ag.getAttribute("b", "x"));
	}
	
	@Test
	public void testSelect1() throws ParseException
	{
		QueueSource qs = createGroupSource();
		m_interpreter.addPlaceholder("@foo", "processor", qs);
		Processor proc = (Processor) m_interpreter.parseQuery("SELECT (A.x) @foo");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof Tuple);
		Tuple tup = (Tuple) o;
		assertEquals(0, ((Number) tup.get("x")).intValue());
		assertNull(tup.get("y"));
	}
	
	@Test
	public void testSelect2() throws ParseException
	{
		QueueSource qs = createGroupSource();
		m_interpreter.addPlaceholder("@foo", "processor", qs);
		Processor proc = (Processor) m_interpreter.parseQuery("SELECT (A.x) AS t @foo");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof Tuple);
		Tuple tup = (Tuple) o;
		assertEquals(0, ((Number) tup.get("t")).intValue());
		assertNull(tup.get("x"));
	}
	
	@Test
	public void testSelect3() throws ParseException
	{
		QueueSource qs = createGroupSource();
		m_interpreter.addPlaceholder("@foo", "processor", qs);
		Processor proc = (Processor) m_interpreter.parseQuery("SELECT (A.x) AS t, (B.t) AS ululu @foo");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof Tuple);
		Tuple tup = (Tuple) o;
		assertEquals(0, ((Number) tup.get("t")).intValue());
		assertEquals("foo", tup.get("ululu"));
		assertNull(tup.get("x"));
	}
	
	@Test
	public void testSelect4() throws ParseException
	{
		QueueSource qs = createGroupSource();
		m_interpreter.addPlaceholder("@foo", "processor", qs);
		Processor proc = (Processor) m_interpreter.parseQuery("SELECT ((A.x) + (3)) @foo");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof Number);
		assertEquals(3, ((Number) o).intValue());
	}
	
	@Test
	public void testSelect5() throws ParseException
	{
		QueueSource qs = createGroupSource();
		m_interpreter.addPlaceholder("@foo", "processor", qs);
		Processor proc = (Processor) m_interpreter.parseQuery("SELECT ((A.x) + (3)) AS t, ((B.z) × (B.z)) AS u @foo");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof Tuple);
		Tuple tup = (Tuple) o;
		assertEquals(3, ((Number) tup.get("t")).intValue());
		assertEquals(4, ((Number) tup.get("u")).intValue());
	}
	
	@Test
	public void testSelectFrom1() throws ParseException
	{
		QueueSource qs = getQueueSource1();
		m_interpreter.addPlaceholder("@foo", "processor", qs);
		Processor proc = (Processor) m_interpreter.parseQuery("SELECT (A.x) AS t FROM (@foo) AS A");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof Tuple);
		Tuple tup = (Tuple) o;
		assertEquals(0, ((Number) tup.get("t")).intValue());
		assertNull(tup.get("x"));
	}
	
	@Test
	public void testSelectFrom2() throws ParseException
	{
		m_interpreter.addPlaceholder("@foo", "processor", getQueueSource1());
		m_interpreter.addPlaceholder("@bar", "processor", getQueueSource2());
		Processor proc = (Processor) m_interpreter.parseQuery("SELECT (A.x) AS t FROM (@foo) AS A, (@bar) AS B");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof Tuple);
		Tuple tup = (Tuple) o;
		assertEquals(0, ((Number) tup.get("t")).intValue());
		assertNull(tup.get("x"));
	}
	
	@Test
	public void testSelectFrom3() throws ParseException
	{
		m_interpreter.addPlaceholder("@foo", "processor", getQueueSource1());
		m_interpreter.addPlaceholder("@bar", "processor", getQueueSource2());
		Processor proc = (Processor) m_interpreter.parseQuery("SELECT (A.x) AS t, (B.z) AS u FROM (@foo) AS A, (@bar) AS B");
		Pullable p = proc.getPullableOutput();
		Object o = p.pull();
		assertTrue(o instanceof Tuple);
		Tuple tup = (Tuple) o;
		assertEquals(0, ((Number) tup.get("t")).intValue());
		assertEquals(10, ((Number) tup.get("u")).intValue());
		assertNull(tup.get("x"));
	}
	
	/**
	 * Creates a simple source with a few tuples
	 * @return The source
	 */
	public static QueueSource getQueueSource1()
	{
		QueueSource qs = new QueueSource();
		qs.addEvent(new TupleFixed(new String[]{"x"}, new Object[]{0}));
		return qs;
	}
	
	/**
	 * Creates a simple source with a few tuples
	 * @return The source
	 */
	public static QueueSource getQueueSource2()
	{
		QueueSource qs = new QueueSource();
		qs.addEvent(new TupleFixed(new String[]{"z"}, new Object[]{10}));
		return qs;
	}
	
	/**
	 * Creates a simple group source with a few attribute groups
	 * @return The source
	 */
	public static QueueSource createGroupSource()
	{
		QueueSource qs = new QueueSource(1);
		{
			AttributeGroup group = new AttributeGroup(new String[]{"A", "B", "C"});
			group.add(0, new TupleFixed(new String[]{"x", "y"}, new Integer[]{0, 1}));
			group.add(1, new TupleFixed(new String[]{"z", "t"}, new Object[]{2, "foo"}));
			group.add(2, new TupleFixed(new String[]{"x", "q"}, new Integer[]{4, 5}));
			qs.addEvent(group);
		}
		{
			AttributeGroup group = new AttributeGroup(new String[]{"A", "B", "C"});
			group.add(0, new TupleFixed(new String[]{"x", "y"}, new Integer[]{9, 8}));
			group.add(1, new TupleFixed(new String[]{"z", "t"}, new Object[]{10, "bar"}));
			group.add(2, new TupleFixed(new String[]{"x", "q"}, new Integer[]{7, 6}));
			qs.addEvent(group);
		}
		return qs;
	}

}
