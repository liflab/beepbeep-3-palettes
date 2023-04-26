/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2023 Sylvain Hallé

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

import ca.uqac.lif.cep.functions.Function;

/**
 * Function that acts as a first-order universal quantifier.
 * @author Sylvain Hallé
 *
 */
public class ForAll extends FirstOrderQuantifier
{
  public ForAll(String x, Function f)
  {
    super(x, f);
  }
  
  public ForAll(String x, Function d, Function f)
  {
    super(x, d, f);
  }
  
  @Override
  protected void getVerdict(Object[][] inputs, Object[] outputs)
  {
    for (Object[] o : inputs)
    {
      if (!(Boolean) o[0])
      {
        outputs[0] = false;
        return;
      }
    }
    outputs[0] = true;
  }

  @Override
  public Function duplicate(boolean with_state)
  {
    return new ForAll(m_variable, m_function.duplicate(with_state));
  }
}
