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
package ca.uqac.lif.cep.mtnp;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.mtnp.table.TableEntry;

public class UpdateTableStream extends UpdateTable
{
	public UpdateTableStream(String ... column_names)
	{
		super(column_names.length, column_names);
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
