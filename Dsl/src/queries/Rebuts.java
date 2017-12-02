package queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.junit.Test;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.ltl.ParseException;
import ca.uqac.lif.cep.ltl.Troolean.Value;
import ca.uqac.lif.cep.tmf.QueueSource;

public class Rebuts {

	
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
