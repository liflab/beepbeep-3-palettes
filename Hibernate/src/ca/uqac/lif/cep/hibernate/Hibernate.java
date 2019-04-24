/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2019 Sylvain Hallé

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
package ca.uqac.lif.cep.hibernate;

import ca.uqac.lif.azrael.fridge.Fridge;
import ca.uqac.lif.azrael.fridge.FridgeException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.tmf.QueueSink;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Stores an internal processor using an object fridge. 
 * @author Sylvain Hallé
 */
public class Hibernate extends SynchronousProcessor
{
  /**
   * A fridge used to store the processor
   */
  protected Fridge m_fridge;
  
  /**
   * A sink to receive the events produced by the processor
   */
  protected QueueSink m_sink;
  
  /**
   * Each of the queues contained in the sink (one for each output pipe)
   */
  protected List<Queue<Object>> m_queues;
  
  /**
   * Creates a new hibernate processor
   * @param f The fridge used to store the processor. The fridge must
   * already store the processor.
   * @param in_arity The input arity of the processor
   * @param out_arity The output arity of the processor
   */
  public Hibernate(Fridge f, int in_arity, int out_arity)
  {
    super(in_arity, out_arity);
    m_fridge = f;
    m_sink = new QueueSink(out_arity);
    m_queues = new ArrayList<Queue<Object>>(out_arity);
    for (int i = 0; i < out_arity; i++)
    {
      m_queues.add(m_sink.getQueue(i));
    }
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    Processor proc = null;
    try
    {
      proc = (Processor) m_fridge.fetch();
    }
    catch (FridgeException e)
    {
      throw new ProcessorException(e);
    }
    Connector.connect(proc, m_sink);
    for (int i = 0; i < inputs.length; i++)
    {
      Pushable p = proc.getPushableInput(i);
      p.push(inputs[i]);
    }
    boolean processed = true;
    while (processed)
    {
      
      Object[] out_front = new Object[m_queues.size()];
      for (int i = 0; i < out_front.length; i++)
      {
        Queue<Object> q = m_queues.get(i);
        if (q.isEmpty())
        {
          processed = false;
          break;
        }
        out_front[i] = q.remove();
      }
      if (processed)
      {
        outputs.add(out_front);
      }
    }
    try
    {
      m_fridge.store(proc);
    }
    catch (FridgeException e)
    {
      throw new ProcessorException(e);
    }
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    throw new UnsupportedOperationException("This processor cannot be duplicated");
  }

}
