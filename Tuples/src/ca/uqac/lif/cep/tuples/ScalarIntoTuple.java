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
package ca.uqac.lif.cep.tuples;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Turns a scalar object into a singleton tuple.
 * @author Sylvain Hallé
 */
public class ScalarIntoTuple extends UnaryFunction<Object,TupleFixed>
{
	/**
	 * The name given to the key for the resulting tuple
	 */
	protected String m_keyName;
	
	ScalarIntoTuple()
	{
		super(Object.class, TupleFixed.class);
	}
	
	/**
	 * Creates a new instance of the function
	 * @param name The name given to the key in the resulting tuple
	 */
	public ScalarIntoTuple(String name)
	{
		this();
		m_keyName = name;
	}

	@Override
	public TupleFixed getValue(Object x)
	{
		return new TupleFixed(new String[]{m_keyName}, new Object[]{x});
	}
	
	@Override
	public ScalarIntoTuple duplicate(boolean with_state)
	{
		return new ScalarIntoTuple(m_keyName);
	}
}
