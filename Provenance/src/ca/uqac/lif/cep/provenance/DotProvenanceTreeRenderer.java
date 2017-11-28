package ca.uqac.lif.cep.provenance;

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.petitpoucet.AggregateFunction;
import ca.uqac.lif.petitpoucet.BrokenChain;
import ca.uqac.lif.petitpoucet.InfiniteLoop;
import ca.uqac.lif.petitpoucet.NodeFunction;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

/**
 * Renders a provenance node into a picture
 *  
 * @author Sylvain Hallé
 */
public class DotProvenanceTreeRenderer 
{
	protected int m_nodeCounter;

	protected Map<String,Integer> m_ids;

	public DotProvenanceTreeRenderer()
	{
		super();
		m_nodeCounter = 0;
		m_ids = new HashMap<String,Integer>();
	}
	
	public String toDot(ProvenanceNode ... nodes)
	{
		StringBuilder out = new StringBuilder();
		out.append("digraph {\n");
		out.append("node [shape=\"rect\"];\n");
		for (ProvenanceNode node : nodes)
		{
			toDot(node, "", -1, out);
		}
		out.append("}\n");
		return out.toString();
	}

	protected void toDot(ProvenanceNode node, String parent_id, int parent, StringBuilder out)
	{
		NodeFunction nf = node.getNodeFunction();
		String datapoint_id = nf.getDataPointId();
		int id = -1;
		boolean already_visited = m_ids.containsKey(datapoint_id);
		if (!already_visited)
		{
			// First time we process this node
			id = m_nodeCounter++;
			m_ids.put(datapoint_id, id);
			String style = styleNode(node);
			String url = "#"; // Eventually, replace with something clickable?
			out.append(id).append(" [label=\"").append(escape(node.toString())).append("\",href=\"").append(url).append("\"");
			if (!style.isEmpty())
			{
				out.append(",").append(style);
			}
			out.append("];\n");
		}
		else
		{
			id = m_ids.get(datapoint_id);
		}
		if (parent >= 0)
		{
			out.append(id).append(" -> ").append(parent).append(";\n");
		}
		if (!already_visited)
		{
			for (ProvenanceNode pn : node.getParents())
			{
				String p_id = node.getNodeFunction().getDataPointId();
				toDot(pn, p_id, id, out);
			}
		}
	}

	protected static String escape(String s)
	{
		s = s.replaceAll("\\[", "(");
		s = s.replaceAll("\\]", ")");
		s = s.replaceAll("\"", "'");
		return s;
	}

	public String styleNode(ProvenanceNode n)
	{
		String style = "";
		NodeFunction nf = n.getNodeFunction();
		if (nf instanceof AggregateFunction)
		{
			style = "style=filled,fillcolor=deeppink";
		}
		else if (nf instanceof EventFunction)
		{
			style = "style=filled,fillcolor=darkgoldenrod1";
		}
		else if (n instanceof BrokenChain)
		{
			style = "style=filled,fillcolor=crimson";
		}
		else if (n instanceof InfiniteLoop)
		{
			style = "style=filled,fillcolor=crimson";
		}
		return style;
	}
}
