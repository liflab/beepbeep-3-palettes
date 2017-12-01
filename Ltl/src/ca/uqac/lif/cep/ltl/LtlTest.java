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
package ca.uqac.lif.cep.ltl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import ca.uqac.lif.bullwinkle.BnfParser.ParseException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.ltl.Troolean.Value;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Unit tests for the LTL operators
 * @author Sylvain Hallé
 */
public class LtlTest 
{
	protected Interpreter m_interpreter;

	@Before
	public void setUp()
	{
		m_interpreter = new Interpreter();
		m_interpreter.load(ca.uqac.lif.cep.ltl.PackageExtension.class);
	}
	
	@Test
	public void testGlobally1() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(Value.TRUE);
		input_events.add(Value.TRUE);
		input_events.add(Value.FALSE);
		input_events.add(Value.TRUE);
		src.setEvents(input_events);
		Globally g = new Globally();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
	}
	
	@Test
	public void testAlways1() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(Value.TRUE);
		input_events.add(Value.TRUE);
		input_events.add(Value.FALSE);
		input_events.add(Value.TRUE);
		src.setEvents(input_events);
		Always g = new Always();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertEquals(Value.INCONCLUSIVE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.INCONCLUSIVE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
	}
	
	@Test
	public void testEventually1() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(Value.FALSE);
		input_events.add(Value.FALSE);
		input_events.add(Value.TRUE);
		input_events.add(Value.FALSE);
		src.setEvents(input_events);
		Eventually g = new Eventually();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertNotNull(b);
		assertEquals(Value.TRUE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
	}
	
	@Test
	public void testSometime1() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(Value.FALSE);
		input_events.add(Value.FALSE);
		input_events.add(Value.TRUE);
		input_events.add(Value.FALSE);
		src.setEvents(input_events);
		Sometime g = new Sometime();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertEquals(Value.INCONCLUSIVE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.INCONCLUSIVE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
	}
	
	@Test
	public void testNext1() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(Value.FALSE);
		input_events.add(Value.FALSE);
		input_events.add(Value.TRUE);
		input_events.add(Value.FALSE);
		src.setEvents(input_events);
		Next g = new Next();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertNotNull(b);
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
	}
	
	@Test
	public void testNext2() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(Value.FALSE);
		input_events.add(Value.TRUE);
		input_events.add(Value.TRUE);
		input_events.add(Value.FALSE);
		src.setEvents(input_events);
		Next g = new Next();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertNotNull(b);
		assertEquals(Value.TRUE, b);
	}
	
	@Test
	public void testNot() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(Value.FALSE);
		input_events.add(Value.TRUE);
		input_events.add(Value.TRUE);
		input_events.add(Value.FALSE);
		src.setEvents(input_events);
		TrooleanNot g = new TrooleanNot();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
	}
	
	@Test
	public void testAnd1() 
	{
		QueueSource src_left = new QueueSource(1);
		QueueSource src_right = new QueueSource(1);
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src_left.setEvents(input_events);
		}
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			src_right.setEvents(input_events);
		}
		TrooleanAnd g = new TrooleanAnd();
		Connector.connect(src_left, g, 0, 0);
		Connector.connect(src_right, g, 0, 1);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
	}
	
	@Test
	public void testAnd2() 
	{
		QueueSource src_left = new QueueSource(1);
		QueueSource src_right = new QueueSource(1);
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			src_left.setEvents(input_events);
		}
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(null);
			input_events.add(Value.TRUE);
			src_right.setEvents(input_events);
		}
		TrooleanAnd g = new TrooleanAnd();
		Connector.connect(src_left, 0, g, 0);
		Connector.connect(src_right, 0, g, 1);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
	}
	
	@Test
	public void testOr() 
	{
		QueueSource src_left = new QueueSource(1);
		QueueSource src_right = new QueueSource(1);
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src_left.setEvents(input_events);
		}
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			src_right.setEvents(input_events);
		}
		TrooleanOr g = new TrooleanOr();
		Connector.connect(src_left, 0, g, 0);
		Connector.connect(src_right, 0, g, 1);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
	}
	
	@Test
	public void testUntil1() 
	{
		QueueSource src_left = new QueueSource(1);
		QueueSource src_right = new QueueSource(1);
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src_left.setEvents(input_events);
		}
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			src_right.setEvents(input_events);
		}
		Until g = new Until();
		Connector.connect(src_left, 0, g, 0);
		Connector.connect(src_right, 0, g, 1);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
	}
	
	@Test
	public void testUntil2() 
	{
		QueueSource src_left = new QueueSource(1);
		QueueSource src_right = new QueueSource(1);
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src_left.setEvents(input_events);
		}
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src_right.setEvents(input_events);
		}
		Until g = new Until();
		Connector.connect(src_left, 0, g, 0);
		Connector.connect(src_right, 0, g, 1);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
	}
	
	@Test
	public void testUpTo1() 
	{
		QueueSource src_left = new QueueSource(1);
		QueueSource src_right = new QueueSource(1);
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src_left.setEvents(input_events);
		}
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			src_right.setEvents(input_events);
		}
		UpTo g = new UpTo();
		Connector.connect(src_left, 0, g, 0);
		Connector.connect(src_right, 0, g, 1);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
	}
	
	@Test
	public void testUpTo2() 
	{
		QueueSource src_left = new QueueSource(1);
		QueueSource src_right = new QueueSource(1);
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src_left.setEvents(input_events);
		}
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src_right.setEvents(input_events);
		}
		UpTo g = new UpTo();
		Connector.connect(src_left, 0, g, 0);
		Connector.connect(src_right, 0, g, 1);
		Pullable p = g.getPullableOutput(0);
		Value b;
		b = (Value) p.pullSoft();
		assertEquals(Value.INCONCLUSIVE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.INCONCLUSIVE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
	}
	
	@Test
	public void testExpression1() throws ParseException
	{
		String expression = "(@T) AND (@U)";
		{
			QueueSource src = new QueueSource(1);
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src.setEvents(input_events);
			m_interpreter.addPlaceholder("@T", "processor", src);
		}
		{
			QueueSource src = new QueueSource(1);
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src.setEvents(input_events);
			m_interpreter.addPlaceholder("@U", "processor", src);
		}
		m_interpreter.setDebugMode(true);
		Pullable p = m_interpreter.executeQuery(expression);
		Value b;
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
		b = (Value) p.pullSoft();
		assertEquals(Value.FALSE, b);
	}
	
	@Test
	public void testExpression2() throws ParseException
	{
		String expression = "(@T) AND (X (@U))";
		{
			QueueSource src = new QueueSource(1);
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src.setEvents(input_events);
			m_interpreter.addPlaceholder("@T", "processor", src);
		}
		{
			QueueSource src = new QueueSource(1);
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src.setEvents(input_events);
			m_interpreter.addPlaceholder("@U", "processor", src);
		}
		Pullable p = m_interpreter.executeQuery(expression);
		assertNotNull(p);
		Value b;
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
	}
	
	@Test
	public void testExpression3() throws ParseException
	{
		String expression = "X (@U)";
		{
			QueueSource src = new QueueSource(1);
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src.setEvents(input_events);
			m_interpreter.addPlaceholder("@U", "processor", src);
		}
		Pullable p = m_interpreter.executeQuery(expression);
		assertNotNull(p);
		Value b;
		b = (Value) p.pullSoft();
		assertNull(b);
		b = (Value) p.pullSoft();
		assertEquals(Value.TRUE, b);
	}
	
	@Test
	public void testGrammarConstant1() throws ParseException
	{
		Pullable p = m_interpreter.executeQuery("CONSTANT (true)");
		assertNotNull(p);
	}
	
	@Test
	public void testGrammarConstant2() throws ParseException
	{
		Pullable p = m_interpreter.executeQuery("CONSTANT (⊥)");
		assertNotNull(p);
	}
	
	@Test
	public void testGrammarConnectives1() throws ParseException
	{
		Pullable p = m_interpreter.executeQuery("(CONSTANT (⊥)) AND (CONSTANT (⊥))");
		assertNotNull(p);
	}
	
	@Test
	public void testGrammarConnectives2() throws ParseException
	{
		Pullable p = m_interpreter.executeQuery("(CONSTANT (⊥)) OR (CONSTANT (⊥))");
		assertNotNull(p);
	}
	
	@Test
	public void testGrammarConnectives3() throws ParseException
	{
		Pullable p = m_interpreter.executeQuery("NOT (CONSTANT(⊥))");
		assertNotNull(p);
	}
	
	@Test
	public void testGrammarConnectives4() throws ParseException
	{
		Pullable p = m_interpreter.executeQuery("X (CONSTANT(⊥))");
		assertNotNull(p);
	}
	
	@Test
	public void testGrammarConnectives5() throws ParseException
	{
		Pullable p = m_interpreter.executeQuery("G (CONSTANT(⊥))");
		assertNotNull(p);
	}
	
	@Test
	public void testGrammarConnectives6() throws ParseException
	{
		Pullable p = m_interpreter.executeQuery("F (CONSTANT(⊥))");
		assertNotNull(p);
	}
	
	@Test
	public void testGrammarConnectives7() throws ParseException
	{
		Pullable p = m_interpreter.executeQuery("(CONSTANT(⊥)) U (CONSTANT(⊥))");
		assertNotNull(p);
	}
	
	@Test
	public void testGrammarConnectives8() throws ParseException
	{
		Pullable p = m_interpreter.executeQuery("(X ((CONSTANT(⊥)) U (CONSTANT(⊥)))) AND (NOT (F (CONSTANT(⊥))))");
		assertNotNull(p);
	}
	
	@Test
	public void testGrammarMultiline() throws ParseException
	{
		String expression = "(CONSTANT(⊥))\nAND\n(X (CONSTANT(⊥)))";
		{
			QueueSource src = new QueueSource(1);
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(Value.FALSE);
			input_events.add(Value.TRUE);
			input_events.add(Value.TRUE);
			input_events.add(Value.FALSE);
			src.setEvents(input_events);
			m_interpreter.addPlaceholder("@P", "processor", src);
		}
		Pullable p = m_interpreter.executeQuery(expression);
		assertNotNull(p);
	}
	
	@Test
	public void testMultipleQueries2() throws ParseException, IOException
	{
		InputStream is = this.getClass().getResourceAsStream("test2.esql");
		m_interpreter.executeQueries(is);
	}
	
}
