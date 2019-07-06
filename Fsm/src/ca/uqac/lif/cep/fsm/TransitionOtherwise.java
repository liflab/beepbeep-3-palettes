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
package ca.uqac.lif.cep.fsm;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.ContextAssignment;

/**
 * Represents the "otherwise" transition in the Moore machine
 * @author Sylvain Hallé
 *
 */
public final class TransitionOtherwise extends FunctionTransition
{

	public TransitionOtherwise(TransitionOtherwise t)
	{
		super(t);
	}

	public TransitionOtherwise(int destination)
	{
		super(null, destination);
	}
	
	public TransitionOtherwise(int destination, ContextAssignment assignment)
  {
    super(null, destination, assignment);
  }
	
	public TransitionOtherwise(int destination, ContextAssignment ... assignments)
	{
		super(null, destination, assignments);
	}

	@Override
	public boolean isFired(Object[] inputs, Context context)
	{
		// Always fires
		return true;
	}

	@Override
	public TransitionOtherwise duplicate(boolean with_state)
	{
		return new TransitionOtherwise(this);
	}

	@Override
	public String toString()
	{
		return "* -> " + m_destination + "[" + m_assignments + "]";
	}
}