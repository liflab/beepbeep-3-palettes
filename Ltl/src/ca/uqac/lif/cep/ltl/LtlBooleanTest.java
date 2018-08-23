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

import java.util.Vector;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Unit tests for the LTL operators
 * @author Sylvain Hallé
 */
public class LtlBooleanTest 
{
	@Test
	public void testGlobally1() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(true);
		input_events.add(true);
		input_events.add(false);
		input_events.add(true);
		src.setEvents(input_events);
		Globally g = new Globally();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Boolean b;
		b = (Boolean) p.pullSoft();
		assertNull(b);
		b = (Boolean) p.pullSoft();
		assertNull(b);
		b = (Boolean) p.pullSoft();
		assertEquals(false, b);
		b = (Boolean) p.pullSoft();
		assertEquals(false, b);
	}
	
	@Test
	public void testGlobally2() 
	{
		QueueSource src = new QueueSource(1).setEvents(false);
		Globally g = new Globally();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Boolean b;
		b = (Boolean) p.pullSoft();
		assertEquals(false, b);
	}

	@Test
	public void testEventually1() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(false);
		input_events.add(false);
		input_events.add(true);
		input_events.add(false);
		src.setEvents(input_events);
		Eventually g = new Eventually();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Boolean b;
		b = (Boolean) p.pullSoft();
		assertNull(b);
		b = (Boolean) p.pullSoft();
		assertNull(b);
		b = (Boolean) p.pullSoft();
		assertNotNull(b);
		assertEquals(true, b);
		b = (Boolean) p.pullSoft();
		assertEquals(true, b);
	}
	
	@Test
	public void testNext1() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(false);
		input_events.add(false);
		input_events.add(true);
		input_events.add(false);
		src.setEvents(input_events);
		Next g = new Next();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Boolean b;
		b = (Boolean) p.pullSoft();
		assertNull(b);
		b = (Boolean) p.pullSoft();
		assertNotNull(b);
		assertEquals(false, b);
		b = (Boolean) p.pullSoft();
		assertEquals(true, b);
	}
	
	@Test
	public void testNext2() 
	{
		QueueSource src = new QueueSource(1);
		Vector<Object> input_events = new Vector<Object>();
		input_events.add(false);
		input_events.add(true);
		input_events.add(true);
		input_events.add(false);
		src.setEvents(input_events);
		Next g = new Next();
		Connector.connect(src, g);
		Pullable p = g.getPullableOutput(0);
		Boolean b;
		b = (Boolean) p.pullSoft();
		assertNull(b);
		b = (Boolean) p.pullSoft();
		assertNotNull(b);
		assertEquals(true, b);
	}
	
	@Test
	public void testUntil1() 
	{
		QueueSource src_left = new QueueSource(1);
		QueueSource src_right = new QueueSource(1);
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(false);
			input_events.add(true);
			input_events.add(true);
			input_events.add(false);
			src_left.setEvents(input_events);
		}
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(false);
			input_events.add(true);
			input_events.add(false);
			input_events.add(true);
			src_right.setEvents(input_events);
		}
		Until g = new Until();
		Connector.connect(src_left, 0, g, 0);
		Connector.connect(src_right, 0, g, 1);
		Pullable p = g.getPullableOutput(0);
		Boolean b;
		b = (Boolean) p.pullSoft();
		assertEquals(false, b);
		b = (Boolean) p.pullSoft();
		assertEquals(true, b);
		b = (Boolean) p.pullSoft();
		assertNull(b);
		b = (Boolean) p.pullSoft();
		assertEquals(true, b);
	}
	
	@Test
	public void testUntil2() 
	{
		QueueSource src_left = new QueueSource(1);
		QueueSource src_right = new QueueSource(1);
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(true);
			input_events.add(true);
			input_events.add(true);
			input_events.add(false);
			src_left.setEvents(input_events);
		}
		{
			Vector<Object> input_events = new Vector<Object>();
			input_events.add(false);
			input_events.add(false);
			input_events.add(true);
			input_events.add(false);
			src_right.setEvents(input_events);
		}
		Until g = new Until();
		Connector.connect(src_left, 0, g, 0);
		Connector.connect(src_right, 0, g, 1);
		Pullable p = g.getPullableOutput(0);
		Boolean b;
		b = (Boolean) p.pullSoft();
		assertNull(b);
		b = (Boolean) p.pullSoft();
		assertNull(b);
		b = (Boolean) p.pullSoft();
		assertEquals(true, b);
		b = (Boolean) p.pullSoft();
		assertEquals(true, b);
		b = (Boolean) p.pullSoft();
    assertEquals(true, b);
    b = (Boolean) p.pullSoft();
    assertEquals(false, b);
	}
}
