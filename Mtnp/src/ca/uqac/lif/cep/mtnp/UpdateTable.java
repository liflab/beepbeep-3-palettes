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

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.mtnp.table.HardTable;
import ca.uqac.lif.mtnp.table.TableEntry;

/**
 * Adds contents to a table based on incoming events. This abstract processor
 * exists in three concrete versions:
 * <ul>
 * <li>{@link UpdateTableArray} updates a table by creating new entries
 * from incoming arrays of object; each array represents a new line in the
 * table</li>
 * <li>{@link UpdateTableStream} work in the same way, but receives <i>n</i>
 * input streams instead of a single array of <i>n</i> elements</li>
 * <li>{@link UpdateTableMap} receives a stream of {@link java.util.Map Map}
 * objects, each corresponding to a tuple of key-value pairs to add to the
 * table</li>
 * </ul> 
 * @author Sylvain Hallé
 */
public abstract class UpdateTable extends UniformProcessor
{
  /**
   * The table that is being updated by input events
   */
	protected HardTable m_table;

	/**
	 * Creates a new instance of the processor.
	 * @param in_arity The input arity
	 * @param column_names The names of the columns in the resulting table
	 */
	public UpdateTable(int in_arity, String ... column_names)
	{
		super(in_arity, 1);
		m_table = new HardTable(column_names);
	}
	
	/**
	 * Creates a new instance of the processor.
	 * @param in_arity The input arity
	 * @param t The table where entries will be added from incoming
	 * events
	 */
	public UpdateTable(int in_arity, HardTable t)
	{
	  super(in_arity, 1);
	  m_table = t;
	}
	
	/**
	 * Creates a table entry from an array of objects
	 * @param inputs The objects
	 * @return The table entry
	 */
	protected TableEntry createEntry(Object[] inputs)
	{
		String[] col_names = m_table.getColumnNames();
		TableEntry e = new TableEntry();
		for (int i = 0; i < Math.min(col_names.length, inputs.length); i++)
		{
			e.put(col_names[i], inputs[i]);
		}
		return e;
	}
	
	@Override
	public void reset()
	{
	  super.reset();
		m_table.clear();
	}
}
