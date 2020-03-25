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
package ca.uqac.lif.cep.tuples;

import java.util.Queue;

import ca.uqac.lif.cep.SynchronousProcessor;

/**
 * Creates a feed of events from CRLF-separated string chunks.
 * Note that the input feed must have a trailing CRLF for all elements,
 * including the last. 
 * @author Sylvain Hallé
 */
public class TupleFeeder extends SynchronousProcessor
{
  /**
   * The builder object used to create tuples
   */
	protected FixedTupleBuilder m_builder;
	
	/**
	 * The character used to separate the text lines into fields
	 */
	protected String m_separator = ",";
	
	/**
	 * Create a new tuple feeder
	 */
	public TupleFeeder()
	{
		this(null);
	}
	
	/**
	 * Create a new tuple feeder using a predefined tuple builder
	 * @param builder The tuple buidler
	 */
	public TupleFeeder(FixedTupleBuilder builder)
	{
		super(1, 1);
		m_builder = builder;
	}
	
	/**
	 * Sets the character used to separate the text lines into fields
	 * @param separator The separator
	 * @return This tuple feeder
	 */
	/*@ non_null @*/ public TupleFeeder setSeparator(/*@ non_null @*/ String separator)
	{
	  m_separator = separator;
	  return this;
	}
	
	@Override
	public TupleFeeder duplicate(boolean with_state)
	{
	  if (with_state)
	  {
	    TupleFeeder tf = new TupleFeeder(m_builder);
	    tf.setSeparator(m_separator);
	    return tf;
	  }
		return new TupleFeeder().setSeparator(m_separator);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) 
	{
		String token = ((String) inputs[0]).trim();
		if (token.isEmpty() || token.startsWith("#"))
		{
			// Ignore comment and empty lines
			return true;
		}
		String[] parts = token.split(m_separator);
		if (m_builder == null)
		{
			// This is the first token we read; it contains the names
			// of the arguments
			m_builder = new FixedTupleBuilder(parts);
			return true;
		}
		outputs.add(new Object[]{m_builder.createTupleFromString(parts)});
		return true;
	}

}