/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2020 Sylvain Hallé

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
package ca.uqac.lif.cep.widgets;

import ca.uqac.lif.cep.tmf.Source;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Queue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A {@link ca.uqac.lif.cep.tmf.Source Source} processor that can be added
 * as an {@link java.awt.event.ActionListener ActionListener} to an AWT or
 * Swing widget.
 * @author Sylvain Hallé
 */
public class ListenerSource extends Source implements ActionListener, ChangeListener, MouseListener, MouseMotionListener
{
  /**
   * Creates a new action listener source
   */
  public ListenerSource()
  {
    super(1);
  }
  
  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    outputs.addAll(m_tempQueue);
    m_tempQueue.clear();
    return true;
  }
  
  @Override
  public void reset()
  {
    super.reset();
    m_tempQueue.clear();
  }

  @Override
  public ListenerSource duplicate(boolean with_state)
  {
    throw new UnsupportedOperationException("Method duplicate is not supported on this processor");
  }

  @Override
  public void stateChanged(ChangeEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }

  @Override
  public void mouseDragged(MouseEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }

  @Override
  public void mouseMoved(MouseEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }

  @Override
  public void mouseClicked(MouseEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }

  @Override
  public void mousePressed(MouseEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }

  @Override
  public void mouseReleased(MouseEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }

  @Override
  public void mouseEntered(MouseEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }

  @Override
  public void mouseExited(MouseEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }
}
