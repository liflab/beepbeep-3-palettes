/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2020 Sylvain Hallé

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

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.mtnp.table.HardTable;
import ca.uqac.lif.mtnp.table.TableEntry;

/**
 * Adds contents to a table from a stream of arrays. Each array represents
 * a new line to add to the table.
 * @author Sylvain Hallé
 */
public class UpdateTableArray extends UpdateTable
{
  /**
   * Creates a new instance of the processor.
   * @param column_names The names of the columns of the table to update.
   * Each element of an array will be the value of the attribute at
   * the corresponding position. 
   */
	public UpdateTableArray(String ... column_names)
	{
		super(1, column_names);
	}
	
	/**
	 * Creates a new instance of the processor.
	 * @param t The table where entries will be added from incoming
   * events
	 */
	public UpdateTableArray(HardTable t)
	{
	  super(1, t);
	}
	
	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) throws ProcessorException
	{
		if (inputs[0] instanceof TableEntry)
		{
			m_table.add((TableEntry) inputs[0]);
		}
		else if (inputs[0].getClass().isArray())
		{
			TableEntry e = createEntry((Object[]) inputs[0]);
			m_table.add(e);
		}
		outputs[0] = m_table;
		return true;
	}
	
	@Override
	public UpdateTableArray duplicate(boolean with_state)
	{
		UpdateTableArray uta = new UpdateTableArray(m_table.getColumnNames());
		if (with_state)
		{
			uta.m_table = m_table.duplicate(with_state);
		}
		return uta;
	}
}
