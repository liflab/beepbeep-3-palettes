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
package ca.uqac.lif.cep.tuples;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.numbers.Subtraction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tuples.From.FromFunction;
import ca.uqac.lif.cep.tuples.Select.SelectFunction;

public class TupleTest 
{
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
		Object[] outputs = ff.evaluate(inputs);
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
	public void testAttributeExpression1()
	{
		FunctionTree ft = new FunctionTree(Addition.instance, new GetAttribute("A", "y"), new GetAttribute("C", "q"));
		AttributeGroup group = new AttributeGroup(new String[]{"A", "B", "C"});
		group.add(0, new TupleFixed(new String[]{"x", "y"}, new Integer[]{0, 1}));
		group.add(1, new TupleFixed(new String[]{"z", "t"}, new Object[]{2, "foo"}));
		group.add(2, new TupleFixed(new String[]{"x", "q"}, new Integer[]{4, 5}));
		AttributeExpression ae = new AttributeExpression(ft, "p1");
		Object o = ae.getValue(group);
		assertNotNull(o);
		assertTrue(o instanceof Number);
		assertEquals(6, ((Number) o).intValue());
	}
	
	@Test
	public void testSelectFunction1()
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
		Object[] values = sel_f.evaluate(new Object[]{group});
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
	}
	
	@Test
	public void testSelect() throws ConnectorException
	{
		// SELECT A.y + C.q as p1, z - C.x as p2, t as p3
		SelectFunction sel_f = new SelectFunction(new AttributeExpression[]{
				new AttributeExpression(new FunctionTree(Addition.instance, new GetAttribute("A", "y"), new GetAttribute("C", "q")), "p1"),
				new AttributeExpression(new FunctionTree(Subtraction.instance, new GetAttribute("z"), new GetAttribute("C", "x")), "p2"),
				new AttributeExpression(new GetAttribute("t"), "p3")
		});
		QueueSource source = createGroupSource();
		Select sel = new Select(sel_f);
		Connector.connect(source, sel);
		Pullable p = sel.getPullableOutput(0);
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
	}
	
	public QueueSource createGroupSource()
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
