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
	 * A map associating edge labels to numbers
	 */
	protected Map<String,Integer> m_edgeLabels;
	
	/**
   * A map associating vertex labels to the sum of the weights
   * on its incoming edges.
   * Since edges are stored by source vertex, computing the input
   * degree would require looping through every source vertex to
   * look for a destination vertex, which would be very costly.
   * The alternative is to update a separate map of the input
   * degrees as the graph is manipulated.
   */
  protected Map<String,Float> m_inWeights;
	
	/**
	 * A counter for giving unique numbers to edges
	 */
	protected int m_edgeCounter;
	
	/**
	 * Whether to increment the weights or to replace the weight of an
	 * edge with a new value
	 */
	protected boolean m_incrementWeights = true;
	
	/**
	 * Creates a new empty graph
	 */
	public Graph()
	{
		super();
		m_edges = new HashMap<String,Set<Edge>>();
		m_edgeLabels = new HashMap<String,Integer>();
		m_inWeights = new HashMap<String,Float>();
		m_edgeCounter = 0;
	}
	
	/**
	 * Sets whether to increment the weights or to replace the weight of an
   * edge with a new value
	 * @param b Set to {@code true} to increment weights, {@code false}
	 * to replace
	 * @return This graph
	 */
	public Graph incrementWeight(boolean b)
	{
	  m_incrementWeights = b;
	  return this;
	}
	
	/**
	 * Adds an edge to the graph
	 * @param e The edge
	 * @return This graph
	 */
	public Graph add(Edge e)
	{
		Set<Edge> list = null;
		int src_id = -1, dst_id = -1;
		if (m_edgeLabels.containsKey(e.m_source))
		{
		  src_id = m_edgeLabels.get(e.m_source);
		}
		else
		{
		  src_id = m_edgeCounter++;
		  m_edgeLabels.put(e.m_source, src_id);
		}
		if (m_edgeLabels.containsKey(e.m_destination))
    {
      dst_id = m_edgeLabels.get(e.m_destination);
    }
    else
    {
      dst_id = m_edgeCounter++;
      m_edgeLabels.put(e.m_destination, dst_id);
    }
		if (m_edges.containsKey(e.m_source))
			list = m_edges.get(e.m_source);
		else
			list = new HashSet<Edge>();
		list.add(e);
		m_edges.put(e.m_source, list);
		if (!m_inWeights.containsKey(e.m_source))
		{
		  m_inWeights.put(e.m_source, e.m_weight);
		}
		return this;
	}
	
	/**
	 * Finds the edge with given source and destination vertices
	 * @param source The source vertex
	 * @param destination The destination vertex
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
	 * Returns the input weight of a vertex
	 * @param vertex The vertex
	 * @return The input weight, or -1 if the vertex does not exist
	 */
	public float getInWeight(String vertex)
	{
	  if (!m_inWeights.containsKey(vertex))
	  {
	    return -1;
	  }
	  return m_inWeights.get(vertex);
	}
	
	/**
   * Returns the output weight of a vertex
   * @param vertex The vertex
   * @return The output weight, or 0 if the vertex does not exist
   */
  public float getOutWeight(String vertex)
  {
    if (!m_edges.containsKey(vertex))
    {
      return 0;
    }
    return m_edges.get(vertex).size();
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
	public Graph incrementWeight(String source, String destination, Number weight_increment)
	{
		Edge e = getEdge(source, destination);
		float f_weight = weight_increment.floatValue();
		if (e == null)
		{
			add(new Edge(source, destination, f_weight));
			return this;
		}
		if (m_incrementWeights)
		{
		  e.m_weight += f_weight;
		  m_inWeights.put(destination, m_inWeights.get(source) + f_weight);
		}
		else
		{
		  e.m_weight = f_weight;
		  m_inWeights.put(destination, f_weight);
		}
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
		for (Map.Entry<String,Integer> entry : m_edgeLabels.entrySet())
		{
			out.append(entry.getValue()).append(" [").append(renderVertex(entry.getKey())).append("];\n");
		}
		for (Map.Entry<String,Set<Edge>> entry : m_edges.entrySet())
		{
			for (Edge e : entry.getValue())
			{
			  int src_id = m_edgeLabels.get(e.m_source);
			  int dst_id = m_edgeLabels.get(e.m_destination);
				out.append(src_id).append(" -> ").append(dst_id).append(" [").append(renderEdge(e)).append("];\n");
			}
		}
		out.append("}");
		return out.toString();
	}
	
	/**
	 * Renders an edge. Override this method to customize
	 * the rendering.
	 * @param e The edge to render
	 * @return The label
	 */
	public String renderEdge(Edge e)
	{
	  return "label=\"" + formatWeight(e.m_weight) + "\"";
	}
	
	/**
   * Renders a vertex. Override this method to customize
   * the rendering.
   * @param vertex The string corresponding to the vertex to render
   * @return The Graphviz string that goes between the brackets
   */
  public String renderVertex(String vertex)
  {
    return "label=\"" + vertex + "\"";
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
		return duplicate(false);
	}
	
	/**
	 * Creates a deep copy of the graph
	 * @param with_state Has no effect
	 * @return A copy of the graph
	 */
	public Graph duplicate(boolean with_state)
	{
		Graph g = new Graph();
		for (Map.Entry<String, Set<Edge>> entry : m_edges.entrySet())
		{
			Set<Edge> new_edges = new HashSet<Edge>();
			for (Edge e : entry.getValue())
			{
				new_edges.add(e.duplicate(with_state));
			}
			g.m_edges.put(entry.getKey(), new_edges);
		}
		g.m_edgeLabels.putAll(m_edgeLabels);
		return g;
	}
	
	/**
	 * Clears the contents of the graph
	 */
	public void clear()
	{
	  m_edges.clear();
	  m_edgeLabels.clear();
	  m_edgeCounter = 0;
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
			return duplicate(false);
		}
		
		/**
		 * Creates a deep copy of the edge
		 * @param with_state Has no effect
		 * @return A copy of the edge
		 */
		public Edge duplicate(boolean with_state)
		{
			return new Edge(m_source, m_destination, m_weight);
		}
		
		/**
		 * Gets the weight of an edge
		 * @return The weight
		 */
		public float getWeight()
		{
		  return m_weight;
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
