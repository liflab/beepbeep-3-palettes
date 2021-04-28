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
package ca.uqac.lif.cep.fsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Vector;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Equals;

/**
 * Unit tests for the Moore Machine processor
 */
public class MooreTest
{
  // State names; this is just to improve readability
  public static final int ST_0 = 0;
  public static final int ST_1 = 1;
  public static final int ST_2 = 2;
  public static final int ST_3 = 3;

  @Test
  public void testMoore1() 
  {
    // Setup event source
    QueueSource source = new QueueSource(1);
    Vector<Object> events = new Vector<Object>();
    events.add(1);
    events.add(2);
    source.setEvents(events);
    // Setup Moore machine
    MooreMachine m = new MooreMachine(1, 1);
    m.addTransition(ST_0, new ProcessorTransition(ST_1,
        // in state 0, event = 2, go to state 1
        new ApplyFunction(new FunctionTree(Equals.instance, 
            StreamVariable.X, new Constant(2)))));
    m.addTransition(ST_0, new ProcessorTransition(ST_0,
        // in state 0, event = 1, go to state 0
        new ApplyFunction(new FunctionTree(Equals.instance, 
            StreamVariable.X, new Constant(1)))));
    m.addSymbol(0, new Constant("In zero"));
    m.addSymbol(1, new Constant("In one"));
    Connector.connect(source, m);
    Pullable p = m.getPullableOutput(0);
    Object event = null;
    event = p.pullSoft();
    assertNotNull(event);
    assertEquals("In zero", event);
    event = p.pullSoft();
    assertEquals("In one", event);
  }

  @Test
  public void testMooreDuplicate1() 
  {
    MooreMachine aut1 = new MooreMachine(1, 1);
    aut1.addTransition(0, new ProcessorTransition(1, new ApplyFunction(new IdentityFunction(1))));
    aut1.addTransition(0, new TransitionOtherwise(0));
    aut1.addTransition(1, new TransitionOtherwise(1));
    aut1.addSymbol(0, new Constant(Boolean.TRUE));
    aut1.addSymbol(1, new Constant(Boolean.FALSE));
    MooreMachine aut2 = aut1.duplicate(true);
    QueueSource source1 = new QueueSource().setEvents(false, false, true);
    QueueSource source2 = new QueueSource().setEvents(false, false, true);
    Connector.connect(source1, aut1);
    Connector.connect(source2, aut2);
    Pullable p1 = aut1.getPullableOutput();
    Pullable p2 = aut1.getPullableOutput();
    assertEquals(p1.pull(), p2.pull());
    assertEquals(p1.pull(), p2.pull());
    assertEquals(p1.pull(), p2.pull());
  }
  
  @Test
  public void testMooreDuplicate2() 
  {
    MooreMachine aut1 = new MooreMachine(1, 1);
    aut1.addTransition(0, new ProcessorTransition(1, new ApplyFunction(new IdentityFunction(1))));
    aut1.addTransition(0, new TransitionOtherwise(0));
    aut1.addTransition(1, new TransitionOtherwise(1));
    aut1.addSymbol(0, new Constant(Boolean.TRUE));
    aut1.addSymbol(1, new Constant(Boolean.FALSE));
    MooreMachine aut2 = aut1.duplicate(true);
    QueueSource source1 = new QueueSource().setEvents(false, false, true);
    QueueSource source2 = new QueueSource().setEvents(false, false, true);
    Connector.connect(source1, aut1);
    Connector.connect(source2, aut2);
    boolean[] out1 = new boolean[3];
    boolean[] out2 = new boolean[3];
    Pullable p1 = aut1.getPullableOutput();
    Pullable p2 = aut2.getPullableOutput();
    for (int i = 0; i < 3; i++)
    {
      out1[i] = (Boolean) p1.pull();
    }
    for (int i = 0; i < 3; i++)
    {
      out2[i] = (Boolean) p2.pull();
    }
    for (int i = 0; i < 3; i++)
    {
      assertEquals(out1[i], out2[i]);
    }
  }
}
