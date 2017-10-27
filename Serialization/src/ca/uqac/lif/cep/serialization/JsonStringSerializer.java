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

import ca.uqac.lif.azrael.Serializer;
import ca.uqac.lif.azrael.SerializerException;
import ca.uqac.lif.azrael.json.JsonSerializer;
import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.json.JsonParser;
import ca.uqac.lif.json.JsonParser.JsonParseException;

class JsonStringSerializer implements Serializer<String>
{
	protected JsonSerializer m_serializer;

	protected JsonParser m_parser;

	public JsonStringSerializer()
	{
		super();
		m_serializer = new JsonSerializer();
		m_parser = new JsonParser();
	}

	@Override
	public void addClassLoader(ClassLoader arg0)
	{
		m_serializer.addClassLoader(arg0);
	}

	@Override
	public Object deserializeAs(String arg0, Class<?> arg1) throws SerializerException 
	{
		JsonElement je;
		try 
		{
			je = m_parser.parse(arg0);
		}
		catch (JsonParseException e)
		{
			throw new SerializerException(e);
		}
		Object o = m_serializer.deserializeAs(je, arg1);
		return o;
	}

	@Override
	public Class<?> findClass(String arg0) throws ClassNotFoundException 
	{
		return m_serializer.findClass(arg0);
	}

	@Override
	public String serialize(Object arg0) throws SerializerException
	{
		JsonElement je = m_serializer.serialize(arg0);
		return je.toString();
	}

	@Override
	public String serializeAs(Object arg0, Class<?> arg1) throws SerializerException 
	{
		JsonElement je = m_serializer.serializeAs(arg0, arg1);
		return je.toString();
	}
}