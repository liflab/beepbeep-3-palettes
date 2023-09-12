/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2023 Sylvain Hallé and friends

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

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Abstract function that replaces node labels by others.
 * @author Sylvain Hallé
 */
public abstract class RenameNodes extends UnaryFunction<Graph,Graph>
{
	/**
	 * Function that deletes the labels of all nodes.
	 */
	public static final DeleteLabels deleteLabels = new DeleteLabels();
	
	protected RenameNodes()
	{
		super(Graph.class, Graph.class);
	}

	@Override
	public Graph getValue(Graph g)
	{
		Graph new_g = new Graph(g);
		Map<String,String> mapping = new HashMap<String,String>();
		for (Map.Entry<String,Integer> entry : g.m_vertexIds.entrySet())
		{
			String new_l = rename(entry.getKey());
			mapping.put(entry.getKey(), new_l);
		}
		new_g.m_vertexIds.clear();
		new_g.m_vertexLabels.clear();
		for (Map.Entry<String,Integer> entry : g.m_vertexIds.entrySet())
		{
			new_g.m_vertexIds.put(mapping.get(entry.getKey()), entry.getValue());
			new_g.m_vertexLabels.put(entry.getValue(), mapping.get(entry.getKey()));
		}
		return new_g;
	}
	
	/**
	 * Transforms a node label.
	 * @param name The original node label
	 * @return The new node label
	 */
	protected abstract String rename(String name);
	
	/**
	 * Function that deletes the labels of all nodes (i.e.<!-- -->
	 * replaces them with the empty string.
	 */
	public static class DeleteLabels extends RenameNodes
	{
		protected DeleteLabels()
		{
			super();
		}
		
		@Override
		protected String rename(String name)
		{
			return "";
		}
	}
}
