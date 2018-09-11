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
package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;
import java.util.List;

/**
 * Boolean implementation of the universal first-order quantifier.
 * @author Sylvain Hallé
 */
public class ForAll extends FirstOrderQuantifier 
{
	public ForAll(String var_name, Function dom_function, Processor expression)
	{
		super(var_name, dom_function, expression);
	}
	
	protected ForAll(FirstOrderSlice fos)
	{
		super(fos);
	}
	
	@Override
	public ForAll duplicate(boolean with_state)
	{
		ForAll f = new ForAll(m_slicer.duplicate(with_state));
		super.duplicateInto(f);
		f.setContext(m_context);
		return f;
	}

	@Override
	public Object combineValues(List<?> values) 
	{
		for (Object o : values)
		{
			if (Troolean.trooleanValue(o) == Troolean.Value.FALSE)
			{
				return Troolean.Value.FALSE;
			}
		}
		return null;
	}
}
