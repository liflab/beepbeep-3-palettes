package ca.uqac.lif.cep.widgets;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.tmf.Sink;
import java.awt.Component;
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
      else
      {
        // Set label to string of object
        ((JLabel) c).setText(inputs[0].toString()); 
      }
      
    }
  }
}
