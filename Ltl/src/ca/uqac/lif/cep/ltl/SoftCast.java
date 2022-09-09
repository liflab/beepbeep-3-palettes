/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2022 Sylvain Hallé

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
package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.ltl.Troolean.Value;

/**
 * Casts a Boolean value into a Troolean value, by converting (Boolean)
 * false into (Troolean) inconclusive.
 * @author Sylvain Hallé
 */
public class SoftCast extends UnaryFunction<Boolean,Troolean.Value>
{
	/**
	 * A public instance of the function.
	 */
	public static final SoftCast instance = new SoftCast();
	
	/**
	 * Creates a new instance of the SoftCast function.
	 */
	protected SoftCast()
	{
		super(Boolean.class, Troolean.Value.class);
	}

	@Override
	public Value getValue(Boolean x)
	{
		if (Boolean.FALSE.equals(x))
		{
			return Value.INCONCLUSIVE;
		}
		return Value.TRUE;
	}
	
	@Override
	public SoftCast duplicate(boolean with_state)
	{
		return this;
	}
}
