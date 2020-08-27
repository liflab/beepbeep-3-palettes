package ca.uqac.lif.cep.widgets;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.UnaryFunction;

public class MouseCoordinates extends UnaryFunction<EventObject,Object[]>
{
  public static final transient MouseCoordinates instance = new MouseCoordinates();

  private MouseCoordinates()
  {
    super(EventObject.class, Object[].class);
  }

  @Override
  public Object[] getValue(EventObject x)
  {
    if (!(x instanceof MouseEvent))
    {
      throw new FunctionException("Invalid input type");
    }
    MouseEvent me = (MouseEvent) x;
    Object[] coords = new Object[2];
    coords[0] = me.getX();
    coords[1] = me.getY();
    return coords;
  }
}