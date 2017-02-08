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
package ca.uqac.lif.cep.ltl;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Function;

/**
 * Boolean implementation of the first-order existential quantifier.
 * @author Sylvain Hallé
 */
public class Exists extends BooleanQuantifier 
{
	Exists()
	{
		super();
	}
	
	public Exists(String var_name, Function split_function, Processor p)
	{
		super(var_name, split_function, p, ArrayOr.instance, Troolean.Value.FALSE);
	}
	
	@Override
	public String toString()
	{
		return "exists " + m_variableName + " in " + m_spawn.m_splitFunction.toString();
	}
	
	@Override
	public Exists clone() 
	{
		Processor new_proc =  m_spawn.m_processor.clone();
		new_proc.setContext(m_context);
		return new Exists(m_variableName, m_spawn.m_splitFunction, new_proc);
	}

}
