package ca.uqac.lif.cep.widgets;

import ca.uqac.lif.cep.Connector.Variant;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.Context;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

public class GetWidgetValue extends Function
{ 
  /**
   * A single visible instance of the function
   */
  public static final transient GetWidgetValue instance = new GetWidgetValue();
  
  GetWidgetValue()
  {
    super();
  }

  @Override
  public void evaluate(Object[] inputs, Object[] outputs, Context context)
  {
    if (inputs[0] instanceof ChangeEvent)
    {
      ChangeEvent e = (ChangeEvent) inputs[0];
      Object[] out = new Object[1];
      if (fetchWidgettValue(e.getSource(), out))
      {
        outputs[0] = out[0];
      }
    }
  }

  @Override
  public GetWidgetValue duplicate(boolean with_state)
  {
    return new GetWidgetValue();
  }

  protected boolean fetchWidgettValue(Object source, Object[] outputs)
  {
    if (source instanceof JSlider)
    {
      outputs[0] = ((JSlider) source).getValue();
      return true;
    }
    if (source instanceof JLabel)
    {
      outputs[0] = ((JLabel) source).getText();
      return true;
    }
    return false;
  }

  @Override
  public int getInputArity()
  {
    return 1;
  }

  @Override
  public int getOutputArity()
  {
    return 1;
  }

  @Override
  public void getInputTypesFor(Set<Class<?>> classes, int index)
  {
    if (index == 0)
    {
      classes.add(Variant.class);
    }
  }

  @Override
  public Class<?> getOutputTypeFor(int index)
  {
    if (index == 0)
    {
      return Variant.class;
    }
    return null;
  }
}