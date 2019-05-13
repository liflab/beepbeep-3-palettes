/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2019 Sylvain Hallé

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

import ca.uqac.lif.azrael.json.JsonStringReader;
import java.util.Set;

/**
 * Function that deserializes a JSON <em>string</em> into an object
 * @param <U> The type of the deserialized objects
 * @author Sylvain Hallé
 */
public class JsonDeserializeString<U> extends DeserializeEvents<String,U>
{
	public JsonDeserializeString(Class<U> output_type)
	{
		super(new JsonStringReader(), String.class, output_type);
	}

	@Override
	public void getInputTypesFor(Set<Class<?>> classes, int index) 
	{
		classes.add(String.class);
	}
}
