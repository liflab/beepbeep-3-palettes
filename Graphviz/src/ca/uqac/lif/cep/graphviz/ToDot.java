/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé and friends

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
package ca.uqac.lif.cep.graphviz;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Converts a graph into a DOT string
 * @author Sylvain Hallé
 */
public class ToDot extends UnaryFunction<Graph, String>
{
	/**
	 * A unique publicly-accessible instance of the function
	 */
	public static final transient ToDot instance = new ToDot();
	
	protected ToDot()
	{
		super(Graph.class, String.class);
	}

	@Override
	public String getValue(Graph g) 
	{
		return g.toDot();
	}
}
