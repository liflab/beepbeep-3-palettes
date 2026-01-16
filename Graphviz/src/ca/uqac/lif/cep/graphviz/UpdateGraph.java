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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.UniformProcessor;

/**
 * Receives a stream of directed edges, and progressively
 * updates a directed graph.
 * @author Sylvain Hallé
 */
public class UpdateGraph extends UniformProcessor 
{
	/**
	 * The graph that is updated by this processor
	 */
	protected Graph m_graph;
	
	/**
	 * The graph used as the starting point for the update. This
	 * graph is restored on a call to {@link Processor#reset()}.
	 */
	protected final Graph m_startGraph; 
	
	/**
	 * Creates a new update graph processor
	 */
	public UpdateGraph()
	{
		this(new Graph());
	}
	
	/**
	 * Creates a new update graph processor, starting with a given graph
	 * @param g The graph
	 */
	public UpdateGraph(Graph g)
	{
		this(g.duplicate(), g);
	}
	
	/**
	 * Creates a new update graph processor, defining the current graph and
	 * the initial graph.
	 * @param g The current graph
	 * @param start The starting graph
	 * @see #m_startGraph
	 */
	protected UpdateGraph(Graph g, Graph start)
	{
		super(2, 1);
		m_graph = g;
		m_startGraph = start;
	}

	@Override
	public UpdateGraph duplicate(boolean with_state)
	{
		if (with_state)
		{
			return new UpdateGraph(m_graph.duplicate(with_state), m_startGraph);
		}
		return new UpdateGraph(m_startGraph.duplicate());
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_graph = m_startGraph.duplicate();
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
	  if (inputs[0].getClass().isArray())
    {
     Object[] ins = (Object[]) inputs[0];
     m_graph.incrementWeight((String) ins[0], (String) ins[1], 1);
    }
	  else
	  {
	    m_graph.incrementWeight((String) inputs[0], (String) inputs[1], 1);
	  }
		outputs[0] = m_graph;
		return true;
	}
}
