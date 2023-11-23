/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
package ca.uqac.lif.cep.json;

import java.io.InputStream;
import java.util.Queue;
import java.util.Scanner;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.json.JsonParser;
import ca.uqac.lif.json.JsonParser.JsonParseException;

/**
 * A source of JSON events taking its data from a JSON file. To facilitate
 * parsing, the source expects one complete JSON snippet per text line.
 * @since 0.8
 * @author Sylvain Hallé
 */
public class JsonLineFeeder extends Source
{
	/**
   * The parser used to parse the elements. All instances of the
   * processor share the same parser.
   */
  /*@ non_null @*/ protected static final JsonParser s_parser = new JsonParser();
  
  /**
   * The input stream to read JSON content from.
   */
  /*@ non_null @*/ protected final InputStream m_is;
	
  /**
   * The scanner pulling lines from the input stream.
   */
  /*@ non_null @*/ protected final Scanner m_scanner;
  
  /**
   * Creates a new instance of the source, by providing an input stream to
   * read text from.
   * @param is The input stream
   */
	public JsonLineFeeder(InputStream is)
	{
		super(1);
		m_is = is;
		m_scanner = new Scanner(m_is);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		if (!m_scanner.hasNextLine())
		{
			return false;
		}
		String line = m_scanner.nextLine();
		try
		{
			outputs.add(new Object[] {s_parser.parse(line)});
		}
		catch (JsonParseException e)
		{
			throw new ProcessorException(e);
		}
		return true;
	}

	@Override
	public JsonLineFeeder duplicate(boolean with_state)
	{
		throw new UnsupportedOperationException("This source cannot be duplicated");
	}
	
	@Override
	public void stop()
	{
		m_scanner.close();
	}
}