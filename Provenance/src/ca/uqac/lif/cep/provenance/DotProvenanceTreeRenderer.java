package ca.uqac.lif.cep.provenance;

import ca.uqac.lif.cep.EventFunction;
import ca.uqac.lif.mtnp.table.TableCellNode;
import ca.uqac.lif.mtnp.table.TableNode;
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
	
	protected GraphvizRenderer m_renderer;
	
	public DotProvenanceTreeRenderer()
	{
		super();
		m_nodeCounter = 0;
		m_renderer = new GraphvizRenderer();
	}
	
	public String toDot(ProvenanceNode node)
	{
		StringBuilder out = new StringBuilder();
		out.append("digraph {\n");
		out.append("node [shape=\"rect\"];\n");
		toDot(node, "", -1, out);
		out.append("}\n");
		return out.toString();
	}
	
	protected void toDot(ProvenanceNode node, String parent_id, int parent, StringBuilder out)
	{
		int id = m_nodeCounter++;
		String style = styleNode(node);
		String url = "#"; // Eventually, replace with something clickable?
		out.append(id).append(" [label=\"").append(escape(node.toString())).append("\",href=\"").append(url).append("\"");
		if (!style.isEmpty())
		{
			out.append(",").append(style);
		}
		out.append("];\n");
		if (parent >= 0)
		{
			out.append(id).append(" -> ").append(parent).append(";\n");
		}
		for (ProvenanceNode pn : node.getParents())
		{
			String p_id = node.getNodeFunction().getDataPointId();
			toDot(pn, p_id, id, out);
		}
	}
	
	public byte[] toImage(ProvenanceNode node, String filetype)
	{
		String instructions = toDot(node);
		return m_renderer.dotToImage(instructions, filetype);
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
		if (nf instanceof TableCellNode)
		{
			style = "style=filled,fillcolor=cornflowerblue";
		}
		else if (nf instanceof TableNode)
		{
			style = "style=filled,fillcolor=deepskyblue2";
		}
		else if (nf instanceof AggregateFunction)
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
