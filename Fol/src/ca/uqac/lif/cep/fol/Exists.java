/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

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
package ca.uqac.lif.cep.fol;

import ca.uqac.lif.cep.CompoundFuture;
import ca.uqac.lif.cep.functions.Function;
import java.util.concurrent.Future;

/**
 * Function that acts as a first-order existential quantifier.
 * @author Sylvain Hallé
 *
 */
public class Exists extends FirstOrderQuantifier
{
  public Exists(String x, Function f)
  {
    super(x, f);
  }
  
  public Exists(String x, Function d, Function f)
  {
    super(x, d, f);
  }
  
  @Override
  protected void getVerdict(Object[][] inputs, Object[] outputs)
  {
    for (Object o[] : inputs)
    {
      if ((Boolean) o[0])
      {
        outputs[0] = true;
        return;
      }
    }
    outputs[0] = false;
  }

  @Override
  protected Future<Object[]> newFuture(Future<Object[]>[] futures)
  {
    return new ExistsFunctionFuture(futures);
  }

  @Override
  public Function duplicate(boolean with_state)
  {
    return new Exists(m_variable, m_function.duplicate(with_state));
  }
  
  protected static class ExistsFunctionFuture extends CompoundFuture<Object[],Object[]>
  {
    public ExistsFunctionFuture(Future<Object[]>[] futures)
    {
      super(futures);
    }
    
    @Override
    public Boolean[] compute(Object[][] values)
    {
      for (Object[] a : values)
      {
        if (((Boolean) a[0]))
        {
          return new Boolean[]{true};
        }
      }
      return new Boolean[]{false};
    }
  }
}
