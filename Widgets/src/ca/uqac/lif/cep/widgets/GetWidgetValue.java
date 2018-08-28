package ca.uqac.lif.cep.widgets;

import ca.uqac.lif.cep.SynchronousProcessor;
import java.util.Queue;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

public class GetWidgetValue extends SynchronousProcessor
{
  //protected Component m_component;
  
  public GetWidgetValue()
  {
    super(1, 1);
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    if (inputs[0] instanceof ChangeEvent)
    {
      ChangeEvent e = (ChangeEvent) inputs[0];
      Object[] out = new Object[1];
      if (getValue(e.getSource(), out))
      {
        outputs.add(out);
      }
    }
    return true;
  }

  @Override
  public GetWidgetValue duplicate(boolean with_state)
  {
    return new GetWidgetValue();
  }
  
  protected static boolean getValue(/*@ null @*/ Object source, Object[] out)
  {
    if (source == null)
    {
      return false;
    }
    if (source instanceof JSlider)
    {
      out[0] = ((JSlider) source).getValue();
    }
    return true;
  }
}
