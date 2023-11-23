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
package ca.uqac.lif.cep.mtnp;

import java.util.List;
import java.util.Map;

import ca.uqac.lif.mtnp.table.TableEntry;

/**
 * Adds contents to a table by receiving a stream of {@link Map} objects, each
 * corresponding to a tuple of key-value pairs to add to the table.
 * @author Sylvain Hallé
 */
public class UpdateTableMap extends UpdateTable
{
	public UpdateTableMap(String ... col_names)
	{
		super(1, col_names);
	}
	
	public UpdateTableMap(List<String> col_names)
	{
		super(1, col_names);
	}

  @Override
  protected boolean compute(Object[] inputs, Object[] outputs)
  {
    @SuppressWarnings("unchecked")
    Map<String,Object> map = (Map<String,Object>) inputs[0];
    TableEntry te = new TableEntry();
    for (String s : m_table.getColumnNames())
    {
      te.put(s, map.get(s));
    }
    m_table.add(te);
    outputs[0] = m_table;
    return true;
  }

  @Override
  public UpdateTableMap duplicate(boolean with_state)
  {
    UpdateTableMap utm = new UpdateTableMap(m_table.getColumnNames());
    if (with_state)
    {
      utm.m_table = m_table.duplicate(with_state);
    }
    return utm;
  }
}
