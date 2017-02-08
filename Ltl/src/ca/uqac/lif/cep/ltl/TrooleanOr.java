/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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

import java.util.ArrayDeque;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.Processor;

/**
 * Troolean implementation of logical disjunction.
 * @author Sylvain Hallé
 */
public class TrooleanOr extends FunctionProcessor 
{
	public TrooleanOr()
	{
		super(Troolean.OR_FUNCTION);
	}
	
	public static void build(ArrayDeque<Object> stack) throws ConnectorException 
	{
		stack.pop(); // (
		Processor right = (Processor) stack.pop();
		stack.pop(); // )
		stack.pop(); // op
		stack.pop(); // (
		Processor left = (Processor) stack.pop();
		stack.pop(); // )
		TrooleanOr op = new TrooleanOr();
		Connector.connect(left, op, 0, 0);
		Connector.connect(right, op, 0, 1);
		stack.push(op);
	}
	
	@Override
	public TrooleanOr clone()
	{
		return new TrooleanOr();
	}

}
