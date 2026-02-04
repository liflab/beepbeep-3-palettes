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

import java.util.Map;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Renders a {@link Map} as a tab-separated values string.
 * @author Sylvain Hallé
 * @since 3.14
 */
@SuppressWarnings("rawtypes")
public class RenderMap extends UnaryFunction<Map,String>
{
	/**
	 * The name of the "key" header in the table.
	 */
	protected final String m_key;
	
	/**
	 * The name of the "value" header in the table.
	 */
	protected final String m_value;
	
	/**
	 * Creates a new instance of the function.
	 */
	public RenderMap(String key, String value)
	{
		super(Map.class, String.class);
		m_key = key;
		m_value = value;
	}

	@Override
	public String getValue(Map m)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("%table\n");
		sb.append(m_key);
		sb.append("\t");
		sb.append(m_value);
		sb.append("\n");
		for (Object o : m.entrySet())
		{
			Map.Entry e = (Map.Entry) o;
			sb.append(e.getKey().toString());
			sb.append("\t");
			sb.append(e.getValue().toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
