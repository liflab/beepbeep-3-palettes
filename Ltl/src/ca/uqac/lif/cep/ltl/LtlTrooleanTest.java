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
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.ltl.Troolean.Value;
import ca.uqac.lif.cep.tmf.Freeze;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Unit tests for the LTL operators
 * @author Sylvain Hallé
 */
public class LtlTrooleanTest 
{
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
    Always g = new Always(new Freeze());
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
  public void testCompoundTroolean1() 
  {
    QueueSource src = new QueueSource(1);
    Vector<Object> input_events = new Vector<Object>();
    input_events.add(Value.TRUE);
    input_events.add(Value.TRUE);
    input_events.add(Value.FALSE);
    input_events.add(Value.TRUE);
    src.setEvents(input_events);
    Always g = new Always(new Freeze());
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
  public void testSometime1() 
  {
    QueueSource src = new QueueSource(1);
    Vector<Object> input_events = new Vector<Object>();
    input_events.add(Value.FALSE);
    input_events.add(Value.FALSE);
    input_events.add(Value.TRUE);
    input_events.add(Value.FALSE);
    src.setEvents(input_events);
    Sometime g = new Sometime(new Freeze());
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
  public void testNot() 
  {
    QueueSource src = new QueueSource(1);
    Vector<Object> input_events = new Vector<Object>();
    input_events.add(Value.FALSE);
    input_events.add(Value.TRUE);
    input_events.add(Value.TRUE);
    input_events.add(Value.FALSE);
    src.setEvents(input_events);
    ApplyFunction g = new ApplyFunction(Troolean.NOT_FUNCTION);
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
    ApplyFunction g = new ApplyFunction(Troolean.AND_FUNCTION);
    Connector.connect(src_left, 0, g, 0);
    Connector.connect(src_right, 0, g, 1);
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
    ApplyFunction g = new ApplyFunction(Troolean.AND_FUNCTION);
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
    ApplyFunction g = new ApplyFunction(Troolean.OR_FUNCTION);
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


}
