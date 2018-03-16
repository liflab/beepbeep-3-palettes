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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple representation of a directed graph
 * @author Sylvain Hallé
 */
public class Graph 
{
	/**
	 * A collection of directed edges in this graph. Edges are indexed
	 * by source node to facilitate lookup.
	 */
	protected Map<String,Set<Edge>> m_edges;
	
	/**
	 * Creates a new empty graph
	 */
	public Graph()
	{
		super();
		m_edges = new HashMap<String,Set<Edge>>();
	}
	
	/**
	 * Adds an edge to the graph
	 * @param e The edge
	 * @return This graph
	 */
	public Graph add(Edge e)
	{
		Set<Edge> list = null;
		if (m_edges.containsKey(e.m_source))
			list = m_edges.get(e.m_source);
		else
			list = new HashSet<Edge>();
		list.add(e);
		m_edges.put(e.m_source, list);
		return this;
	}
	
	/**
	 * Finds the edge with given source and destination vertices
	 * @param source The source vertex
	 * @param destination The destinatino vertex
	 * @return The edge, or {@code null} if no edge could be found
	 */
	public /*@Null*/ Edge getEdge(String source, String destination)
	{
		if (!m_edges.containsKey(source))
			return null;
		Set<Edge> edges = m_edges.get(source);
		for (Edge e : edges)
		{
			if (source.compareTo(e.m_source) == 0 && destination.compareTo(e.m_destination) == 0)
				return e;
		}
		return null;
	}
	
	/**
	 * Increments the weight of an edge in the graph. If the edge
	 * does not exist, a new edge is created and its weight is set
	 * to <tt>weight_increment</tt>.
	 * @param source The source vertex
	 * @param destination The destination vertex
	 * @param weight_increment The amount to add to the edge's weight 
	 * @return This graph
	 */
	public Graph incrementWeight(String source, String destination, float weight_increment)
	{
		Edge e = getEdge(source, destination);
		if (e == null)
		{
			add(new Edge(source, destination, weight_increment));
			return this;
		}
		e.m_weight += weight_increment;
		return this;
	}
	
	/**
	 * Outputs the graph in DOT format
	 * @return A DOT string
	 */
	public String toDot()
	{
		StringBuilder out = new StringBuilder();
		out.append("digraph G {\n");
		for (String vertex : m_edges.keySet())
		{
			out.append(vertex).append("[label=\"").append(vertex).append("\"];\n");
		}
		for (Map.Entry<String,Set<Edge>> entry : m_edges.entrySet())
		{
			for (Edge e : entry.getValue())
			{
				out.append(e).append(" [label=\"").append(formatWeight(e.m_weight)).append("\"];\n");
			}
		}
		out.append("}");
		return out.toString();
	}
	
	/**
	 * Removes decimals if number is an integer
	 * @param w The number
	 * @return A formatted string for that number
	 */
	protected static String formatWeight(float w)
	{
		if (w % 1 == 0)
		{
			return Integer.toString((int) w);
		}
		return Float.toString(w);
	}
	
	/**
	 * Creates a deep copy of the graph
	 * @return A copy of the graph
	 */
	public Graph duplicate()
	{
		Graph g = new Graph();
		for (Map.Entry<String, Set<Edge>> entry : m_edges.entrySet())
		{
			Set<Edge> new_edges = new HashSet<Edge>();
			for (Edge e : entry.getValue())
			{
				new_edges.add(e.duplicate());
			}
			g.m_edges.put(entry.getKey(), new_edges);
		}
		return g;
	}
	
	/**
	 * A weighted directed edge in a graph
	 */
	public static class Edge
	{
		/**
		 * The weight of an edge
		 */
		protected float m_weight = 0;
		
		/**
		 * The source vertex
		 */
		protected String m_source = "";
		
		/**
		 * The destination vertex
		 */
		protected String m_destination = "";
		
		/**
		 * Creates a new edge
		 * @param source The source vertex
		 * @param destination The destination vertex
		 * @param weight The edge's weight
		 */
		public Edge(String source, String destination, float weight)
		{
			super();
			m_source = source;
			m_destination = destination;
			m_weight = weight;
		}
		
		/**
		 * Creates a deep copy of the edge
		 * @return A copy of the edge
		 */
		public Edge duplicate()
		{
			return new Edge(m_source, m_destination, m_weight);
		}
		
		@Override
		public int hashCode()
		{
			return m_source.hashCode() + m_destination.hashCode();
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof Edge))
			{
				return false;
			}
			Edge e = (Edge) o;
			return e.m_source.compareTo(m_source) == 0 &&
					e.m_destination.compareTo(m_destination) == 0;
		}
		
		@Override
		public String toString()
		{
			return m_source + "->" + m_destination;
		}
	}
}
