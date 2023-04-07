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
package ca.uqac.lif.cep.diagnostics;

import ca.uqac.lif.cep.tmf.Passthrough;

/**
 * A {@link Passthrough} processor that prints every event it receives to the
 * standard output.
 * @author Sylvain Hallé
 */
public class PrintThrough extends Passthrough
{
	/**
	 * The separator between each event
	 */
	protected String m_separator = System.getProperty("line.separator");
	
  @Override
  protected boolean compute(Object[] inputs, Object[] outputs)
  {
    super.compute(inputs, outputs);
    for (int i = 0; i < outputs.length; i++)
    {
      System.out.print(outputs[i] + m_separator);
    }
    return true;
  }
}
