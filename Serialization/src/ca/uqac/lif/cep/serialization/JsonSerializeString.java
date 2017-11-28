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
package ca.uqac.lif.cep.serialization;

import java.util.Set;

import ca.uqac.lif.cep.Connector.Variant;

/**
 * Function that serializes its input into a JSON <em>string</em>
 * @author Sylvain Hallé
 */
public class JsonSerializeString extends SerializeEvents<String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -114255450878538951L;

	public JsonSerializeString()
	{
		super(new JsonStringSerializer(), String.class);
	}

	@Override
	public void getInputTypesFor(Set<Class<?>> classes, int index) 
	{
		classes.add(Variant.class);
	}
}
