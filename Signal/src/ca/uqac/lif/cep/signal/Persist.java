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
package ca.uqac.lif.cep.signal;

import ca.uqac.lif.cep.UniformProcessor;

/**
 * Persists a locally maximal value for a certain amount of time.
 * @author Sylvain Hallé
 */
public class Persist extends UniformProcessor
{
  protected int m_width;
  
  protected float m_lastValue;
  
  protected int m_timeSinceLast;
  
  /**
   * Creates a new decay processor
   * @param width The width of the window
   */
  //@ requires width > 0
  public Persist(int width)
  {
    super(1, 1);
    m_width = width;
    m_lastValue = 0f;
    m_timeSinceLast = 0;
  }
  
  @Override
  protected boolean compute(Object[] inputs, Object[] outputs)
  {
    float f = ((Number) inputs[0]).floatValue();
    m_timeSinceLast++;
    if (f > m_lastValue)
    {
      m_lastValue = f;
      m_timeSinceLast = 0;
    }
    if (m_timeSinceLast > m_width)
    {
      m_lastValue = 0f;
      m_timeSinceLast = 0;
    }
    outputs[0] = m_lastValue;
    return true;
  }

  @Override
  public Persist duplicate(boolean with_state)
  {
    Persist d = new Persist(m_width);
    d.m_context.putAll(getContext());
    if (with_state)
    {
      d.m_lastValue = m_lastValue;
      d.m_timeSinceLast = m_timeSinceLast;
    }
    return d;
  }
  
  @Override
  public void reset()
  {
    m_lastValue = 0f;
    m_timeSinceLast = 0;
  }
}
