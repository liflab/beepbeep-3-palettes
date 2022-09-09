/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2020 Sylvain Hallé

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
package ca.uqac.lif.cep.provenance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.cep.EventTracker;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.provenance.EventFunction.InputValue;
import ca.uqac.lif.petitpoucet.BrokenChain;
import ca.uqac.lif.petitpoucet.NodeFunction;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

/**
 * Tracks the relationship between output events produced by processors, and
 * the input events that were used to compute them. This tracker only keeps
 * track of the IDs of the processors, and the relative position of events in
 * their respective input and output streams. It does not keep the processors
 * or the events themselves. Therefore, its output is only a "skeleton" of the
 * dependencies between events, that can then be used to compute other
 * dependency graphs by other event trackers. 
 * 
 * @author Sylvain Hallé
 */
public class IndexEventTracker implements EventTracker
{
	/**
	 * A map containing all the input-output associations recorded so far.
	 * This map is a three-tiered nested data structure:
	 * <ul>
	 * <li>At the first level, it associates processor IDs to maps</li>
	 * <li>The resulting object is itself a map that associates stream
	 * indices for this processor to a map</li>
	 * <li>The resulting object is a map that associates positions in
	 * that stream to a {@link ca.uqac.lif.petitpoucet.ProvenanceNode ProvenanceNode}</li>  
	 * </ul>
	 * The structure could have been a simple set of {@code ProvenanceNode}s. However,
	 * if we know the processor ID, the stream index and the stream position, finding
	 * the corresponding node requires three hash lookups in the present structure,
	 * instead of a linear search through an unordered set (or list).
	 */
	protected final Map<Integer,Map<Integer,Map<Integer,ProvenanceNode>>> m_mapping = new HashMap<Integer,Map<Integer,Map<Integer,ProvenanceNode>>>();

	protected final Map<Integer,Map<Integer,ProcessorConnection>> m_inputConnections = new HashMap<Integer,Map<Integer,ProcessorConnection>>();

	protected final Map<Integer,Map<Integer,ProcessorConnection>> m_outputConnections = new HashMap<Integer,Map<Integer,ProcessorConnection>>();

	protected final Map<Integer,GroupProcessor> m_groups = new HashMap<Integer,GroupProcessor>();

	@Override
	public void add(GroupProcessor g)
	{
		m_groups.put(g.getId(), g);
	}

	protected void setConnection(Map<Integer,Map<Integer,ProcessorConnection>> map, int source_proc_id, int stream_index, ProcessorConnection connection)
	{
		if (!map.containsKey(source_proc_id))
		{
			map.put(source_proc_id, new HashMap<Integer,ProcessorConnection>());
		}
		Map<Integer,ProcessorConnection> m1 = map.get(source_proc_id);
		m1.put(stream_index, connection);
	}

	protected /*@ null @*/ ProcessorConnection getConnection(Map<Integer,Map<Integer,ProcessorConnection>> map, int source_proc_id, int stream_index)
	{
		if (!map.containsKey(source_proc_id))
		{
			return null;
		}
		Map<Integer,ProcessorConnection> m1 = map.get(source_proc_id);
		if (!m1.containsKey(stream_index))
		{
			return null;
		}
		return m1.get(stream_index);
	}

	@Override
	public void associateTo(int id, NodeFunction f, int out_stream_index, int out_stream_pos)
	{
		ProvenanceNode pn_child = fetchOrCreateProvenanceNode(id, out_stream_index, out_stream_pos);
		ProvenanceNode pn_parent = new ProvenanceNode(f);
		pn_child.addParent(pn_parent);
	}

	@Override
	public void associateToInput(int id, int in_stream_index, int in_stream_pos, int out_stream_index, int out_stream_pos)
	{
		ProvenanceNode pn_child = fetchOrCreateProvenanceNode(id, out_stream_index, out_stream_pos);
		ProvenanceNode pn_parent = new ProvenanceNode(new EventFunction.InputValue(id, in_stream_index, in_stream_pos));
		pn_child.addParent(pn_parent);
	}

	@Override
	public void associateToOutput(int id, int in_stream_index, int in_stream_pos, int out_stream_index, int out_stream_pos)
	{
		ProvenanceNode pn_child = fetchOrCreateProvenanceNode(id, out_stream_index, out_stream_pos);
		ProvenanceNode pn_parent = new ProvenanceNode(new EventFunction.OutputValue(id, in_stream_index, in_stream_pos));
		pn_child.addParent(pn_parent);
	}

	@Override
	public void setTo(Processor ... processors)
	{
		for (Processor p : processors)
		{
			p.setEventTracker(this);
		}
	}

	/**
	 * Fetches the {@link ca.uqac.lif.petitpoucet.ProvenanceNode ProvenanceNode}
	 * for a given processor/index/position triplet. Since these nodes are stored
	 * in a bunch of nested hash maps, and that the maps for the desired keys may
	 * have not been initialized yet, the method takes care of creating empty
	 * maps as it drills down its way to the node it is looking for. 
	 * @see #m_mapping
	 * @param proc_id The ID of the processor
	 * @param stream_index The index of the output stream on that processor
	 * @param stream_pos The position of the event in that stream
	 * @return The provenance node corresponding to that particular event
	 */
	protected ProvenanceNode fetchOrCreateProvenanceNode(int proc_id, int stream_index, int stream_pos)
	{
		if (!m_mapping.containsKey(proc_id))
		{
			m_mapping.put(proc_id, new HashMap<Integer,Map<Integer,ProvenanceNode>>());
		}
		Map<Integer,Map<Integer,ProvenanceNode>> m1 = m_mapping.get(proc_id);
		if (!m1.containsKey(stream_index))
		{
			m1.put(stream_index, new HashMap<Integer,ProvenanceNode>());
		}
		Map<Integer,ProvenanceNode> m2 = m1.get(stream_index);
		if (!m2.containsKey(stream_pos))
		{
			m2.put(stream_pos, new ProvenanceNode(new EventFunction.OutputValue(proc_id, stream_index, stream_pos)));
		}
		return (ProvenanceNode) m2.get(stream_pos);
	}

	/**
	 * Fetches the {@link ca.uqac.lif.petitpoucet.ProvenanceNode ProvenanceNode}
	 * for a given processor/index/position triplet. Contrast this to
	 * {@link #fetchOrCreateProvenanceNode(int, int, int) fetchOrCreateProvenanceNode()}. 
	 * @param proc_id The ID of the processor
	 * @param stream_index The index of the output stream on that processor
	 * @param stream_pos The position of the event in that stream
	 * @return The provenance node corresponding to that particular event, or
	 *   {@code null} if no node exists for the specified parameters
	 */
	protected ProvenanceNode fetchProvenanceNode(int proc_id, int stream_index, int stream_pos)
	{
		if (!m_mapping.containsKey(proc_id))
		{
			return null;
		}
		Map<Integer,Map<Integer,ProvenanceNode>> m1 = m_mapping.get(proc_id);
		if (!m1.containsKey(stream_index))
		{
			return null;
		}
		Map<Integer,ProvenanceNode> m2 = m1.get(stream_index);
		if (!m2.containsKey(stream_pos))
		{
			return null;
		}
		return (ProvenanceNode) m2.get(stream_pos);
	}

	/**
	 * Gets the provenance tree for a given event.
	 * @param p The processor
	 * @param stream_index The index of the stream
	 * @param stream_pos The position of the event in the stream
	 * @return The provenance node corresponding to that particular event, or
	 *   {@code null} if no node exists for the specified parameters
	 */
	public /*@NotNull*/ ProvenanceNode getProvenanceTree(Processor p, int stream_index, int stream_pos)
	{
		return getProvenanceTree(p.getId(), stream_index, stream_pos);
	}

	/**
	 * Gets the provenance tree for a given event. The provenance tree is the
	 * directed acyclic graph of all the provenance nodes on which the current node
	 * depends. It is the reverse of the impact tree.
	 * @see #getImpactTree(int, int, int)
	 * @param proc_id The ID of the processor
	 * @param stream_index The index of the output stream on that processor
	 * @param stream_pos The position of the event in that stream
	 * @return The provenance node corresponding to that particular event, or
	 *   {@code null} if no node exists for the specified parameters
	 */
	public /*@NotNull*/ ProvenanceNode getProvenanceTree(int proc_id, int stream_index, int stream_pos)
	{
		ProvenanceNode node = fetchProvenanceNode(proc_id, stream_index, stream_pos);
		if (node == null)
		{
			if (m_groups.containsKey(proc_id))
			{
				GroupProcessor gp = m_groups.get(proc_id);
				EventTracker inner_tracker = gp.getInnerTracker();
				Processor gp_output = gp.getAssociatedOutput(stream_index);
				if (gp_output == null)
				{
					return BrokenChain.instance;
				}
				ProvenanceNode root = inner_tracker.getProvenanceTree(gp_output.getId(), stream_index, stream_pos);
				List<ProvenanceNode> leaves = ProvenanceTree.getInputLeaves(root);
				for (ProvenanceNode leaf : leaves)
				{
					if (leaf.getNodeFunction() instanceof InputValue)
					{
						InputValue iv = (InputValue) leaf.getNodeFunction();
						int group_input_index = gp.getGroupInputIndex(iv.getProcessorId(), iv.getStreamIndex());
						if (group_input_index == -1)
						{
							//leaf.addParent(BrokenChain.instance);
						}
						else
						{
							InputValue global_iv = new InputValue(gp.getId(), group_input_index, iv.getStreamPosition());
							ProvenanceNode sub_root = new ProvenanceNode(global_iv);
							ProvenanceNode expanded_sub_root = expandInputs(sub_root, gp.getId());
							leaf.addParent(expanded_sub_root);
						}
					}
				}
				return root;
			}
			return BrokenChain.instance;
		}
		return expandInputs(node, proc_id);
	}

	protected ProvenanceNode expandInputs(ProvenanceNode node, int proc_id)
	{
		ProvenanceNode expanded_node = new ProvenanceNode(node.getNodeFunction());
		for (ProvenanceNode parent : node.getParents())
		{
			ProvenanceNode new_parent;
			NodeFunction nf = parent.getNodeFunction();
			if (nf instanceof InputValue)
			{
				InputValue iv = (InputValue) nf;
				// Try to find what processor produced the event
				// that was given as an input to this processor
				ProcessorConnection pc = getConnection(m_inputConnections, proc_id, iv.getStreamIndex());
				if (pc == null)
				{
					// Not found; declare a broken chain
					new_parent = parent;
					//parent.addParent(BrokenChain.instance);
				}
				else
				{
					// Found it: recurse
					new_parent = getProvenanceTree(pc.m_procId, pc.m_streamIndex, iv.getStreamPosition());
				}
			}
			else
			{
				new_parent = parent;
			}
			expanded_node.addParent(new_parent);
		}
		return expanded_node;
	}

	/**
	 * Gets the impact tree for a given event. The impact tree is the directed
	 * acyclic graph of all the downstream provenance nodes that depend on the
	 * given node.
	 * @see #getProvenanceTree(int, int, int)
	 * @param proc_id The ID of the processor
	 * @param stream_index The index of the output stream on that processor
	 * @param stream_pos The position of the event in that stream
	 * @return The provenance node corresponding to that particular event, or
	 *   {@code null} if no node exists for the specified parameters
	 */
	public /*@NotNull*/ ProvenanceNode getImpactTree(int proc_id, int stream_index, int stream_pos)
	{
		ProvenanceNode node = fetchProvenanceNode(proc_id, stream_index, stream_pos);
		if (node == null)
		{
			return BrokenChain.instance;
		}
		ProvenanceNode expanded_node = new ProvenanceNode(node.getNodeFunction());
		for (ProvenanceNode parent : node.getChildren())
		{
			ProvenanceNode new_parent;
			NodeFunction nf = parent.getNodeFunction();
			if (nf instanceof InputValue)
			{
				InputValue iv = (InputValue) nf;
				// Try to find what processor produced the event
				// that was given as an input to this processor
				ProcessorConnection pc = getConnection(m_inputConnections, proc_id, iv.getStreamIndex());
				if (pc == null)
				{
					// Not found; declare a broken chain
					new_parent = BrokenChain.instance;
				}
				else
				{
					// Found it: recurse
					new_parent = getProvenanceTree(pc.m_procId, pc.m_streamIndex, iv.getStreamPosition());
				}
			}
			else
			{
				new_parent = parent;
			}
			expanded_node.addParent(new_parent);
		}
		return expanded_node;
	}

	@Override
	public void setConnection(int output_proc_id, int output_stream_index, int input_proc_id, int input_stream_index)
	{
		setConnection(m_inputConnections, input_proc_id, input_stream_index, new ProcessorConnection(output_proc_id, output_stream_index));
		setConnection(m_outputConnections, output_proc_id, output_stream_index, new ProcessorConnection(input_proc_id, input_stream_index));
	}

	protected static class ProcessorConnection
	{
		public int m_procId;
		public int m_streamIndex;

		public ProcessorConnection(int proc_id, int stream_index)
		{
			super();
			m_procId = proc_id;
			m_streamIndex = stream_index;
		}

		@Override
		public String toString()
		{
			return "P" + m_procId + "." + m_streamIndex; 
		}
	}

	/**
	 * Returns the size of this tracker. This corresponds to the number of unique
	 * input-output associations that are stored.
	 * @return The size
	 */
	public int getSize()
	{
		int size = 0;
		for (Map.Entry<Integer,Map<Integer,Map<Integer,ProvenanceNode>>> m1 : m_mapping.entrySet())
		{
			for (Map.Entry<Integer,Map<Integer,ProvenanceNode>> m2 : m1.getValue().entrySet())
			{
				size += m2.getValue().size();
			}
		}
		return size;
	}

	@Override
	public EventTracker getCopy(boolean with_state)
	{
		IndexEventTracker iet = new IndexEventTracker();
		if (with_state)
		{
			iet.m_inputConnections.putAll(m_inputConnections);
			iet.m_mapping.putAll(m_mapping);
			iet.m_outputConnections.putAll(m_outputConnections);
			iet.m_groups.putAll(m_groups);
		}
		return iet;
	}
}
