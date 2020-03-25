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