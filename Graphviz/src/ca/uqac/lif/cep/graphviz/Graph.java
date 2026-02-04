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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple representation of a directed graph.
 * @author Sylvain Hallé
 */
public class Graph 
{
	/**
	 * A collection of directed edges in this graph. Edges are indexed
	 * by source node to facilitate lookup.
	 */
	protected final Map<Integer,Set<Edge>> m_edges;
	
	/**
	 * A map associating vertex IDs to labels.
	 */
	protected final Map<Integer,String> m_vertexLabels;
	
	/**
	 * A map associating labels to vertex IDs.
	 */
	protected final Map<String,Integer> m_vertexIds;
	
	/**
	 * A string representing the style of the graph.
	 */
	protected String m_graphStyle;
	
	/**
	 * A string representing the style of the nodes.
	 */
	protected String m_nodeStyle;
	
	/**
   * A map associating vertices to the sum of the weights
   * on its incoming edges.
   * Since edges are stored by source vertex, computing the input
   * degree would require looping through every source vertex to
   * look for a destination vertex, which would be very costly.
   * The alternative is to update a separate map of the input
   * degrees as the graph is manipulated.
   */
  protected final Map<Integer,Float> m_inWeights;
	
	/**
	 * A counter for giving unique numbers to vertices.
	 */
	protected int m_vertexCounter;
	
	/**
	 * Whether to increment the weights or to replace the weight of an
	 * edge with a new value.
	 */
	protected boolean m_incrementWeights = true;
	
	/**
	 * Creates a new empty graph
	 */
	public Graph()
	{
		super();
		m_edges = new HashMap<Integer,Set<Edge>>();
		m_vertexLabels = new HashMap<Integer,String>();
		m_vertexIds = new HashMap<String,Integer>();
		m_inWeights = new HashMap<Integer,Float>();
		m_vertexCounter = 0;
		m_graphStyle = "";
		m_nodeStyle = "";
	}
	
	/**
	 * Creates a new graph by copying another graph.
	 * @param g The graph to copy
	 */
	public Graph(Graph g)
	{
		this();
		for (Map.Entry<Integer, Set<Edge>> entry : g.m_edges.entrySet())
		{
			Set<Edge> new_edges = new HashSet<Edge>();
			for (Edge e : entry.getValue())
			{
				new_edges.add(e.duplicate(true));
			}
			m_edges.put(entry.getKey(), new_edges);
		}
		m_vertexIds.putAll(g.m_vertexIds);
		m_vertexLabels.putAll(g.m_vertexLabels);
		m_inWeights.putAll(g.m_inWeights);
		m_vertexCounter = g.m_vertexCounter;
		m_incrementWeights = g.m_incrementWeights;
		m_graphStyle = g.m_graphStyle;
		m_nodeStyle = g.m_nodeStyle;
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
	
	public Graph add(String source, String destination, float weight)
	{
		if (!m_vertexLabels.containsValue(source))
		{
			m_vertexLabels.put(m_vertexCounter, source);
			m_vertexIds.put(source, m_vertexCounter);
			m_vertexCounter++;
		}
		if (!m_vertexLabels.containsValue(destination))
		{
			m_vertexLabels.put(m_vertexCounter, destination);
			m_vertexIds.put(destination, m_vertexCounter);
			m_vertexCounter++;
		}
		int id_src = m_vertexIds.get(source);
		int id_dst = m_vertexIds.get(destination);
		return add(id_src, id_dst, weight);
	}
	
	public Graph add(int source, int destination, float weight)
	{
		Set<Edge> list = null;
		Edge e = new Edge(source, destination, weight);
		if (!m_edges.containsKey(source))
		{
			list = new HashSet<Edge>();
			m_edges.put(source, list);
		}
		else
		{
			list = m_edges.get(source);
		}
		list.add(e);
		if (!m_inWeights.containsKey(e.m_source))
		{
		  m_inWeights.put(e.m_source, e.m_weight);
		}
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
		if (m_edges.containsKey(e.m_source))
		{
			list = m_edges.get(e.m_source);
		}
		else
		{
			list = new HashSet<Edge>();
		}
		list.add(e);
		m_edges.put(e.m_source, list);
		if (!m_inWeights.containsKey(e.m_source))
		{
		  m_inWeights.put(e.m_source, e.m_weight);
		}
		return this;
	}
	
	/**
	 * Gets the number of vertices in the graph.
	 * @return The number of vertices
	 */
	public int vertexCount()
	{
		return m_edges.size();
	}
	
	/**
	 * Gets the number of edges in the graph.
	 * @return The number of edges
	 */
	public int edgeCount()
	{
		return m_edges.values().size();
	}
	
	/**
	 * Finds the edge with given source and destination vertices
	 * @param source The source vertex
	 * @param destination The destination vertex
	 * @return The edge, or {@code null} if no edge could be found
	 */
	/*@ null @*/ public Edge getEdge(int source, int destination)
	{
		if (!m_edges.containsKey(source))
		{
			return null;
		}
		Set<Edge> edges = m_edges.get(source);
		for (Edge e : edges)
		{
			if (source == e.m_source && destination == e.m_destination)
			{
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Finds the edge with given source and destination vertices
	 * @param source The label of the source vertex
	 * @param destination The label of the destination vertex
	 * @return The edge, or {@code null} if no edge could be found
	 */
	/*@ null @*/ public Edge getEdge(String source, String destination)
	{
		int src = m_vertexIds.getOrDefault(source, -1);
		int dst = m_vertexIds.getOrDefault(destination, -1);
		return getEdge(src, dst);
	}
	
	/**
	 * Returns the input weight of a vertex by designating a vertex label.
	 * @param vertex The vertex label
	 * @return The input weight, or -1 if the vertex does not exist
	 */
	public float getInWeight(String vertex)
	{
		if (m_vertexIds.containsKey(vertex))
		{
			return m_inWeights.get(m_vertexIds.get(vertex));
		}
		return -1;
	}
	
	/**
	 * Returns the input weight of a vertex
	 * @param vertex The vertex
	 * @return The input weight, or -1 if the vertex does not exist
	 */
	public float getInWeight(int vertex)
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
  public float getOutWeight(int vertex)
  {
    if (!m_edges.containsKey(vertex))
    {
      return 0;
    }
    return m_edges.get(vertex).size();
  }
  
  /**
	 * Increments the weight of an edge in the graph, by designating
	 * it with the labels of the source and destination vertices. 
	 * If the edge
	 * does not exist, a new edge is created and its weight is set
	 * to <tt>weight_increment</tt>. Note that this method assumes that
	 * each vertex in the graph has a different label in order to be used.
	 * @param source The label of the source vertex
	 * @param destination The label of the destination vertex
	 * @param weight_increment The amount to add to the edge's weight 
	 * @return This graph
	 */
	public Graph incrementWeight(String source, String destination, Number weight_increment)
	{
		if (!m_vertexLabels.containsValue(source))
		{
			m_vertexLabels.put(m_vertexCounter, source);
			m_vertexIds.put(source, m_vertexCounter);
			m_vertexCounter++;
		}
		if (!m_vertexLabels.containsValue(destination))
		{
			m_vertexLabels.put(m_vertexCounter, destination);
			m_vertexIds.put(destination, m_vertexCounter);
			m_vertexCounter++;
		}
		int id_src = m_vertexIds.get(source);
		int id_dst = m_vertexIds.get(destination);
		return incrementWeight(id_src, id_dst, weight_increment);
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
	public Graph incrementWeight(int source, int destination, Number weight_increment)
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
	 * Assigns a default style to the nodes of the graph. 
	 * @param style The style string, in the format expected by Graphviz
	 * @return This graph
	 */
	public Graph setNodeStyle(String style)
	{
		m_nodeStyle = style;
		return this;
	}
	
	/**
	 * Assigns a default style to the graph. 
	 * @param style The style string, in the format expected by Graphviz
	 * @return This graph
	 */
	public Graph setGraphStyle(String style)
	{
		m_graphStyle = style;
		return this;
	}
	
	/**
	 * Outputs the graph in DOT format.
	 * @return A DOT string
	 */
	public String toDot()
	{
		StringBuilder out = new StringBuilder();
		out.append("digraph G {\n");
		if (!m_graphStyle.isEmpty())
		{
			out.append("graph [").append(m_graphStyle).append("];\n");
		}
		if (!m_nodeStyle.isEmpty())
		{
			out.append("node [").append(m_nodeStyle).append("];\n");
		}
		for (Map.Entry<Integer,String> entry : m_vertexLabels.entrySet())
		{
			out.append(entry.getKey()).append(" [").append(renderVertex(entry.getValue())).append("];\n");
		}
		for (Map.Entry<Integer,Set<Edge>> entry : m_edges.entrySet())
		{
			for (Edge e : entry.getValue())
			{
			  int src_id = e.m_source;
			  int dst_id = e.m_destination;
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
	 * Creates a deep copy of the graph.
	 * @return A copy of the graph
	 */
	public Graph duplicate()
	{
		return duplicate(false);
	}
	
	/**
	 * Creates a deep copy of the graph.
	 * @param with_state Has no effect
	 * @return A copy of the graph
	 */
	public Graph duplicate(boolean with_state)
	{
		if (with_state)
		{
			return new Graph(this);
		}
		return new Graph();
	}
	
	/**
	 * Clears the contents of the graph.
	 */
	public void clear()
	{
	  m_edges.clear();
	  m_vertexLabels.clear();
	  m_vertexCounter = 0;
	}
	
	/**
	 * A weighted directed edge in a graph.
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
		protected int m_source = -1;
		
		/**
		 * The destination vertex
		 */
		protected int m_destination = -1;
		
		/**
		 * Creates a new edge
		 * @param source The source vertex
		 * @param destination The destination vertex
		 * @param weight The edge's weight
		 */
		protected Edge(int source, int destination, float weight)
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
		
		/**
		 * Gets the destination vertex of the edge.
		 * @return The vertex ID
		 */
		public int getDestination()
		{
			return m_destination;
		}
		
		/**
		 * Gets the source vertex of the edge.
		 * @return The vertex ID
		 */
		public int getSource()
		{
			return m_source;
		}
		
		@Override
		public int hashCode()
		{
			return m_source + m_destination;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof Edge))
			{
				return false;
			}
			Edge e = (Edge) o;
			return e.m_source == m_source &&
					e.m_destination == m_destination;
		}
		
		@Override
		public String toString()
		{
			return m_source + "->" + m_destination;
		}
	}
}
