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

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.mtnp.table.HardTable;
import ca.uqac.lif.mtnp.table.TableEntry;

/**
 * Adds contents to a table from multiple streams. A new line in the table is
 * added for every event front received by the processor.
 * @author Sylvain Hallé
 */
public class UpdateTableStream extends UpdateTable
{
  /**
   * Creates a new instance of the processor.
   * @param column_names The names of the columns of the table to update.
   * Each input stream will correspond to the value of an attribute at
   * the corresponding position. 
   */
	public UpdateTableStream(String ... column_names)
	{
		super(column_names.length, column_names);
	}
	
	/**
   * Creates a new instance of the processor.
   * @param column_names The names of the columns of the table to update.
   * Each input stream will correspond to the value of an attribute at
   * the corresponding position. 
   */
  public UpdateTableStream(List<String> column_names)
  {
    super(column_names.size(), column_names);
  }
	
	/**
   * Creates a new instance of the processor.
   * @param t The table where entries will be added from incoming events
   * @param column_names The names of the columns of the table to update.
   * Each input stream will correspond to the value of an attribute at
   * the corresponding position. 
   */
	public UpdateTableStream(HardTable t, String ... column_names)
  {
    super(column_names.length, t);
  }
	
	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) throws ProcessorException
	{
		TableEntry e = createEntry(inputs);
		m_table.add(e);
		outputs[0] = m_table;
		return true;
	}
	
	@Override
	public UpdateTableStream duplicate(boolean with_state)
	{
		UpdateTableStream uta = new UpdateTableStream(m_table.getColumnNames());
		if (with_state)
		{
			uta.m_table = m_table.duplicate(with_state);
		}
		return uta;
	}
}
