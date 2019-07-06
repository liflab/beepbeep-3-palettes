package ca.uqac.lif.cep.diagnostics;

import ca.uqac.lif.cep.tmf.Passthrough;

public class PrintThrough extends Passthrough
{
  @Override
  protected boolean compute(Object[] inputs, Object[] outputs)
  {
    super.compute(inputs, outputs);
    for (int i = 0; i < outputs.length; i++)
    {
      System.out.print(outputs[i] + " ");
    }
    System.out.println();
    return true;
  }
}
