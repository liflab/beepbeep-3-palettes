package ca.uqac.lif.cep.widgets;

import ca.uqac.lif.cep.functions.UnaryFunction;
import javax.swing.ImageIcon;

public class ToImageIcon extends UnaryFunction<byte[],ImageIcon>
{
  public static final transient ToImageIcon instance = new ToImageIcon();
  
  protected ToImageIcon()
  {
    super(byte[].class, ImageIcon.class);
  }

  @Override
  public ImageIcon getValue(byte[] x)
  {
    return new ImageIcon(x);
  }

}
