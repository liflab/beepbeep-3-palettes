/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2026 Sylvain Hallé

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
package ca.uqac.lif.cep.zeppelin;

import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.mtnp.table.HardTable;

/**
 * Renders a {@link HardTable} as a tab-separated values string.
 * @author Sylvain Hallé
 * @since 3.14
 */
public class RenderTable extends UnaryFunction<HardTable,String>
{
	/**
	 * A singleton instance of the function.
	 */
	public static final RenderTable instance = new RenderTable();
	
	/**
	 * Creates a new instance of the function.
	 */
	protected RenderTable()
	{
		super(HardTable.class, String.class);
	}

	@Override
	public String getValue(HardTable t)
	{
		return t.toCsv("\t", "?");
	}
}
