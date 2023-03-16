/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
package ca.uqac.lif.cep.diagnostics;

import ca.uqac.lif.cep.UniformProcessor;

/**
 * A {@link ca.uqac.lif.cep.tmf.Passthrough Passthrough} processor that keeps
 * the count of events that went through it.
 * @author Sylvain Hallé
 */
public class PassthroughCounter extends UniformProcessor
{
  /**
   * The number of events that went through the processor
   */
  protected int m_eventCount;
  
  /**
   * The interval to call the callback
   */
  protected int m_interval = -1;
  
  /**
   * A callback to call (if any)
   */
  protected DiagnosticsCallback m_callback = null;
  
  /**
   * Creates a new passthrough counter
   */
  public PassthroughCounter()
  {
    super(1, 1);
  }
  
  public void setCallback(DiagnosticsCallback cb, int interval)
  {
    m_callback = cb;
    m_interval = interval;
  }
  
  /**
   * Returns the number of events that went through the processor
   * @return The number of events
   */
  public int getCount()
  {
    return m_eventCount;
  }
  
  @Override
  public void reset()
  {
    super.reset();
    m_eventCount = 0;
  }

  @Override
  protected boolean compute(Object[] inputs, Object[] outputs)
  {
    m_eventCount++;
    outputs[0] = inputs[0];
    if (m_callback != null && m_eventCount % m_interval == 0)
    {
      m_callback.notify(m_eventCount);
    }
    return true;
  }

  @Override
  public PassthroughCounter duplicate(boolean with_state)
  {
    PassthroughCounter pc = new PassthroughCounter();
    if (with_state)
    {
      pc.m_eventCount = m_eventCount;
    }
    return pc;
  }
}
