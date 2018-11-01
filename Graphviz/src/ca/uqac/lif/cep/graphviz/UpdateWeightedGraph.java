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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.UniformProcessor;

/**
 * Receives a stream of directed edges, and progressively
 * updates a directed weighted graph.
 * @author Sylvain Hallé
 */
public class UpdateWeightedGraph extends UniformProcessor 
{
	/**
	 * The graph that is updated by this processor
	 */
	protected Graph m_graph;
	
	/**
	 * Creates a new update graph processor
	 */
	public UpdateWeightedGraph()
	{
		this(new Graph());
	}
	
	/**
	 * Creates a new update graph processor, starting with a given graph
	 * @param g The graph
	 */
	public UpdateWeightedGraph(Graph g)
	{
		super(3, 1);
		m_graph = g;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		return new UpdateWeightedGraph(m_graph.duplicate(with_state));
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		m_graph.incrementWeight((String) inputs[0], (String) inputs[1], (Number) inputs[2]);
		outputs[0] = m_graph;
		return true;
	}
}
