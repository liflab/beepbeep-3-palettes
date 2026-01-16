/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2026 Sylvain Hallé and friends

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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.cep.functions.BinaryFunction;
import ca.uqac.lif.cep.graphviz.Graph.Edge;

/**
 * Merges the content of two graphs. This is done by taking the union
 * of vertices and edges, assuming that nodes with the same label are
 * the "same" node.
 */
public class MergeWeightedGraphs extends BinaryFunction<Graph, Graph, Graph>
{
	/**
	 * A single publicly visible instance of the function.
	 */
	public static final MergeWeightedGraphs instance = new MergeWeightedGraphs();
	
	/**
	 * Creates a new instance of the function.
	 */
	MergeWeightedGraphs()
	{
		super(Graph.class, Graph.class, Graph.class);
	}

	@Override
	public Graph getValue(Graph g1, Graph g2)
	{
		Graph g = new Graph(g1);
		for (Map.Entry<Integer,Set<Edge>> me : g2.m_edges.entrySet())
		{
			for (Edge e : me.getValue())
			{
				String src = g2.m_vertexLabels.get(e.getSource());
				String dst = g2.m_vertexLabels.get(e.getDestination());
				g.incrementWeight(src, dst, e.getWeight());
			}
		}
		return g;
	}
	
	@Override
	public Graph getStartValue()
	{
		return new Graph();
	}
	
	protected static int getOrCreate(Graph g, String label)
	{
		if (g.m_vertexIds.containsKey(label))
		{
			return g.m_vertexIds.get(label);
		}
		int v_id = g.m_vertexCounter;
		g.m_vertexCounter++;
		g.m_edges.put(v_id, new HashSet<Edge>());
		g.m_vertexIds.put(label, v_id);
		g.m_vertexLabels.put(v_id, label);
		return v_id;
	}
}
