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
package ca.uqac.lif.cep.tuples.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.Or;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.numbers.IsGreaterThan;
import ca.uqac.lif.cep.numbers.Subtraction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tuples.AttributeExpression;
import ca.uqac.lif.cep.tuples.AttributeGroup;
import ca.uqac.lif.cep.tuples.ExpandAsColumns;
import ca.uqac.lif.cep.tuples.From;
import ca.uqac.lif.cep.tuples.FromFunction;
import ca.uqac.lif.cep.tuples.GetAttribute;
import ca.uqac.lif.cep.tuples.MergeTuples;
import ca.uqac.lif.cep.tuples.Select;
import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.tuples.TupleFixed;
import ca.uqac.lif.cep.tuples.TupleMap;
import ca.uqac.lif.cep.tuples.Where;
import ca.uqac.lif.cep.tuples.Select.SelectFunction;

public class TupleTest 
{
	@Test
	public void stringTest()
	{
		// Call toString on every object, just so that it gets covered
		// by some test
		checkToString(new FromFunction("A"));
		checkToString(new From(new FromFunction("A")));
		checkToString(new AttributeGroup(new String[]{"A", "B", "C"}));
	}
	
	@Test
	public void testTupleFixed()
	{
		TupleFixed tuple = new TupleFixed(new String[]{"x", "q"}, new Object[]{4, "foo"});
		TupleFixed tuple_clone = tuple.clone();
		assertEquals(2, tuple.size());
		assertFalse(tuple.isEmpty());
		assertFalse(tuple.containsKey(null));
		assertFalse(tuple.containsKey(""));
		assertTrue(tuple.containsKey("x"));
		assertTrue(tuple.containsKey("q"));
		assertFalse(tuple.containsKey("z"));
		assertTrue(tuple.containsValue(4));
		assertTrue(tuple.containsValue("foo"));
		assertTrue(tuple_clone.containsKey("x"));
		assertTrue(tuple_clone.containsKey("q"));
		assertFalse(tuple_clone.containsKey("z"));
		assertTrue(tuple_clone.containsValue(4));
		assertTrue(tuple_clone.containsValue("foo"));
		assertNull(tuple.get(null));
		assertNull(tuple.get(""));
		assertEquals(4, tuple.get("x"));
		assertTrue(tuple.equals(tuple_clone));
		assertTrue(tuple_clone.equals(tuple));
		checkToString(tuple);
	}
	
	@Test
	public void testTupleMap()
	{
		TupleMap tuple = new TupleMap(new String[]{"x", "q"}, new Object[]{4, "foo"});
		TupleMap tuple_clone = tuple.clone();
		assertEquals(2, tuple.size());
		assertFalse(tuple.isEmpty());
		assertFalse(tuple.containsKey(null));
		assertFalse(tuple.containsKey(""));
		assertTrue(tuple.containsKey("x"));
		assertTrue(tuple.containsKey("q"));
		assertFalse(tuple.containsKey("z"));
		assertTrue(tuple.containsValue(4));
		assertTrue(tuple.containsValue("foo"));
		assertTrue(tuple_clone.containsKey("x"));
		assertTrue(tuple_clone.containsKey("q"));
		assertFalse(tuple_clone.containsKey("z"));
		assertTrue(tuple_clone.containsValue(4));
		assertTrue(tuple_clone.containsValue("foo"));
		assertNull(tuple.get(null));
		assertNull(tuple.get(""));
		assertEquals(4, tuple.get("x"));
		assertTrue(tuple.equals(tuple_clone));
		assertTrue(tuple_clone.equals(tuple));
		checkToString(tuple);
	}
	
	public static void checkToString(Object o)
	{
		assertNotNull(o);
		assertNotNull(o.toString());
	}
	
	@Test
	public void testFromFunction1()
	{
		FromFunction ff = new FromFunction("A", "B", "C");
		assertEquals(3, ff.getInputArity());
		assertEquals(1, ff.getOutputArity());
		Set<Class<?>> class_set = new HashSet<Class<?>>();
		ff.getInputTypesFor(class_set, 0);
		assertEquals(1, class_set.size());
		assertEquals(AttributeGroup.class, ff.getOutputTypeFor(0));
	}
	
	@Test
	public void testFromFunction2()
	{
		FromFunction ff = new FromFunction("A", "B", "C");
		assertEquals(3, ff.getInputArity());
		Object[] inputs = new Object[]
		{
			new TupleFixed(new String[]{"x", "y"}, new Integer[]{0, 1}),
			new TupleFixed(new String[]{"z", "t"}, new Object[]{2, "foo"}),
			new TupleFixed(new String[]{"x", "q"}, new Integer[]{4, 5})
		};
		Object[] outputs = new Object[1];
		ff.evaluate(inputs, outputs);
		assertTrue(outputs[0] instanceof AttributeGroup);
		AttributeGroup g = (AttributeGroup) outputs[0];
		int v;
		v = (Integer) g.getAttribute("x");
		assertTrue(v == 0 || v == 4);
		v = (Integer) g.getAttribute("A", "x");
		assertEquals(0, v);
		Object o = g.getAttribute("B", "t");
		assertTrue(o instanceof String);
	}
	
	@Test
	public void testFromFunction3()
	{
		FromFunction ff_o = new FromFunction("A", "B", "C");
		FromFunction ff = ff_o.clone();
		ff.reset();
		assertEquals(3, ff.getInputArity());
		Object[] inputs = new Object[]
		{
			new TupleFixed(new String[]{"x", "y"}, new Integer[]{0, 1}),
			new TupleFixed(new String[]{"z", "t"}, new Object[]{2, "foo"}),
			new TupleFixed(new String[]{"x", "q"}, new Integer[]{4, 5})
		};
		Object[] outputs = new Object[1];
		ff.evaluate(inputs, outputs);
		assertTrue(outputs[0] instanceof AttributeGroup);
		AttributeGroup g = (AttributeGroup) outputs[0];
		int v;
		v = (Integer) g.getAttribute("x");
		assertTrue(v == 0 || v == 4);
		v = (Integer) g.getAttribute("A", "x");
		assertEquals(0, v);
		Object o = g.getAttribute("B", "t");
		assertTrue(o instanceof String);
	}
	
	@Test
	public void testAttributeExpression1() throws FunctionException
	{
		FunctionTree ft = new FunctionTree(Addition.instance, new GetAttribute("A", "y"), new GetAttribute("C", "q"));
		AttributeGroup group = new AttributeGroup(new String[]{"A", "B", "C"});
		group.add(0, new TupleFixed(new String[]{"x", "y"}, new Integer[]{0, 1}));
		group.add(1, new TupleFixed(new String[]{"z", "t"}, new Object[]{2, "foo"}));
		group.add(2, new TupleFixed(new String[]{"x", "q"}, new Integer[]{4, 5}));
		AttributeExpression ae = new AttributeExpression(ft, "p1");
		checkToString(ae);
		Object o = ae.getValue(group);
		assertNotNull(o);
		assertTrue(o instanceof Number);
		assertEquals(6, ((Number) o).intValue());
	}
	
	@Test
	public void testSelectFunction1() throws FunctionException
	{
		AttributeGroup group = new AttributeGroup(new String[]{"A", "B", "C"});
		group.add(0, new TupleFixed(new String[]{"x", "y"}, new Integer[]{0, 1}));
		group.add(1, new TupleFixed(new String[]{"z", "t"}, new Object[]{2, "foo"}));
		group.add(2, new TupleFixed(new String[]{"x", "q"}, new Integer[]{4, 5}));
		// SELECT A.y + C.q as p1, z - C.x as p2, t as p3
		SelectFunction sel_f = new SelectFunction(new AttributeExpression[]{
				new AttributeExpression(new FunctionTree(Addition.instance, new GetAttribute("A", "y"), new GetAttribute("C", "q")), "p1"),
				new AttributeExpression(new FunctionTree(Subtraction.instance, new GetAttribute("z"), new GetAttribute("C", "x")), "p2"),
				new AttributeExpression(new GetAttribute("t"), "p3")
		});
		Object[] values = new Object[1];
		sel_f.evaluate(new Object[]{group}, values);
		assertTrue(values[0] instanceof Tuple);
		Tuple t = (Tuple) values[0];
		Object o;
		o = t.get("p1");
		assertTrue(o instanceof Number);
		assertEquals(6, ((Number) o).intValue());
		o = t.get("p2");
		assertTrue(o instanceof Number);
		assertEquals(-2, ((Number) o).intValue());
		o = t.get("p3");
		assertTrue(o instanceof String);
		assertEquals("foo", o);
		// Just so we cover the toString() method
		checkToString(sel_f);
	}
	
	@Test
	public void testSelect() throws ConnectorException
	{
		// SELECT A.y + C.q as p1, z - C.x as p2, t as p3
		QueueSource source = createGroupSource();
		Select sel = new Select(new AttributeExpression[]{
				new AttributeExpression(new FunctionTree(Addition.instance, new GetAttribute("A", "y"), new GetAttribute("C", "q")), "p1"),
				new AttributeExpression(new FunctionTree(Subtraction.instance, new GetAttribute("z"), new GetAttribute("C", "x")), "p2"),
				new AttributeExpression(new GetAttribute("t"), "p3")});
		Connector.connect(source, sel);
		Pullable p = sel.getPullableOutput();
		Tuple t = (Tuple) p.pullSoft();
		assertNotNull(t);
		assertEquals(6, ((Number) t.get("p1")).intValue());
		assertEquals(-2, ((Number) t.get("p2")).intValue());
		assertEquals("foo", t.get("p3"));
		t = (Tuple) p.pullSoft();
		assertNotNull(t);
		assertEquals(14, ((Number) t.get("p1")).intValue());
		assertEquals(3, ((Number) t.get("p2")).intValue());
		assertEquals("bar", t.get("p3"));
		// Just so we cover the toString() method
		checkToString(sel);
	}
	
	@Test
	public void testWhere1() throws ConnectorException
	{
		QueueSource qs = createGroupSource();
		FunctionTree condition = new FunctionTree(Or.instance,
				new FunctionTree(IsGreaterThan.instance,
						new GetAttribute("A", "x"),
						new Constant(0)),
				new FunctionTree(IsGreaterThan.instance,
						new GetAttribute("C", "q"),
						new Constant(9))
				);
		Where w = new Where(condition);
		Connector.connect(qs, w);
		Pullable p = w.getPullableOutput();
		Object o = p.pull();
		assertNotNull(o);
		assertTrue(o instanceof AttributeGroup);
		AttributeGroup ag = (AttributeGroup) o;
		assertEquals(10, ((Number) ag.getAttribute("B", "z")).intValue());
	}
	
	@Test
	public void testWhere2() throws ConnectorException
	{
		QueueSource qs = createGroupSource();
		FunctionTree condition = new FunctionTree(Or.instance,
				new FunctionTree(IsGreaterThan.instance,
						new GetAttribute("A", "x"),
						new Constant(0)),
				new FunctionTree(IsGreaterThan.instance,
						new GetAttribute("C", "q"),
						new Constant(9))
				);
		Where w_old = new Where(condition);
		Where w = w_old.clone();
		Connector.connect(qs, w);
		Pullable p = w.getPullableOutput();
		Object o = p.pull();
		assertNotNull(o);
		assertTrue(o instanceof AttributeGroup);
		AttributeGroup ag = (AttributeGroup) o;
		assertEquals(10, ((Number) ag.getAttribute("B", "z")).intValue());
	}
	
	@Test
	public void testExpand1() throws FunctionException
	{
		Tuple t = new TupleFixed(new String[]{"foo", "bar", "baz"}, new String[]{"a", "b", "c"});
		ExpandAsColumns eac = new ExpandAsColumns("foo", "bar");
		Object[] o = new Object[1];
		eac.evaluate(new Object[]{t}, o);
		assertTrue(o[0] instanceof Tuple);
		Tuple new_t = (Tuple) o[0];
		assertEquals(2, new_t.keySet().size());
		assertEquals("b", new_t.get("a"));
		assertEquals("c", new_t.get("baz"));
	}
	
	@Test
	public void testExpand2() throws FunctionException
	{
		Tuple t = new TupleFixed(new String[]{"foo", "bar", "baz"}, new Integer[]{1, 2, 3});
		ExpandAsColumns eac = new ExpandAsColumns("foo", "bar");
		Object[] o = new Object[1];
		eac.evaluate(new Object[]{t}, o);
		assertTrue(o[0] instanceof Tuple);
		Tuple new_t = (Tuple) o[0];
		assertEquals(2, new_t.keySet().size());
		assertEquals(2, new_t.get("1"));
		assertEquals(3, new_t.get("baz"));
	}
	
	@Test
	public void testMerge1()
	{
		Tuple t1 = new TupleFixed(new String[]{"foo", "bar", "baz"}, new String[]{"a", "b", "c"});
		Tuple t2 = new TupleFixed(new String[]{"arf", "ard", "arz"}, new String[]{"d", "e", "f"});
		MergeTuples mt = new MergeTuples(2);
		Object[] o = new Object[1];
		mt.evaluate(new Object[]{t1, t2}, o);
		assertTrue(o[0] instanceof Tuple);
		Tuple new_t = (Tuple) o[0];
		assertEquals(6, new_t.keySet().size());
		assertEquals("a", new_t.get("foo"));
		assertEquals("b", new_t.get("bar"));
		assertEquals("d", new_t.get("arf"));
		assertEquals("f", new_t.get("arz"));
	}
	
	/**
	 * Creates a simple group source with a few attribute groups
	 * @return The source
	 */
	public QueueSource createGroupSource()
	{
		QueueSource qs = new QueueSource();
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
		{
			AttributeGroup group = new AttributeGroup(new String[]{"A", "B", "C"});
			group.add(0, new TupleFixed(new String[]{"x", "y"}, new Integer[]{9, 8}));
			group.add(1, new TupleFixed(new String[]{"z", "t"}, new Object[]{10, "bar"}));
			group.add(2, new TupleFixed(new String[]{"x", "q"}, new Integer[]{7, 9}));
			qs.addEvent(group);
		}
		return qs;
	}
}
