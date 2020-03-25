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
package ca.uqac.lif.cep.widgets;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.tmf.Sink;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.Queue;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSlider;

public class WidgetSink extends Sink
{
  /**
   * The component on which the input events are displayed
   */
  protected Component m_component;
  
  public WidgetSink(/*@ non_null @*/ Component c, int in_arity)
  {
    super(in_arity);
    m_component = c;
  }
  
  public WidgetSink(/*@ non_null @*/ Component c)
  {
    this(c, 1);
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    changeComponent(m_component, inputs);
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    throw new UnsupportedOperationException("Method duplicate is not supported on this processor");
  }
  
  protected void changeComponent(Component c, Object[] inputs)
  {
    // Perform some default behaviours for a few components
    if (c instanceof JSlider)
    {
      ((JSlider) c).setValue(((Number) inputs[0]).intValue()); 
    }
    if (c instanceof JLabel)
    {
      if (inputs[0] instanceof ImageIcon)
      {
        // We assume array of bytes: set label to image
        ((JLabel) c).setIcon((ImageIcon) inputs[0]);
      }
      else if (inputs[0] instanceof BufferedImage)
      {
        ((JLabel) c).setIcon(new ImageIcon((BufferedImage) inputs[0]));
      }
      else if (inputs[0] instanceof byte[])
      {
        byte[] bytes = (byte[]) inputs[0];
        ((JLabel) c).setIcon(new ImageIcon(bytes));
      }
      else
      {
        // Set label to string of object
        ((JLabel) c).setText(inputs[0].toString()); 
      }
      
    }
  }
}
