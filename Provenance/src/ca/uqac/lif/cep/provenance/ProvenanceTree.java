package ca.uqac.lif.cep.provenance;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.cep.provenance.EventFunction.InputValue;
import ca.uqac.lif.cep.provenance.EventFunction.OutputValue;
import ca.uqac.lif.petitpoucet.BrokenChain;
import ca.uqac.lif.petitpoucet.NodeFunction;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

public class ProvenanceTree
{
	public static List<ProvenanceNode> getLeaves(ProvenanceNode root)
	{
		List<ProvenanceNode> indices = new ArrayList<ProvenanceNode>();
		getLeaves(root, indices);
		return indices;
	}
	
	protected static void getLeaves(ProvenanceNode root, List<ProvenanceNode> leaves)
	{
		NodeFunction nf = root.getNodeFunction();
		if ((root.getParents().isEmpty() || root.getParents().get(0) instanceof BrokenChain))
		{
			if ((nf instanceof InputValue || nf instanceof OutputValue) && !leaves.contains(root))
			{
				leaves.add(root);
			}
		}
		else
		{
			for (ProvenanceNode parent : root.getParents())
			{
				getLeaves(parent, leaves);
			}
		}
	}
	
	public static List<Integer> getIndices(ProvenanceNode root)
	{
		List<Integer> indices = new ArrayList<Integer>();
		getIndices(root, indices);
		return indices;
	}
	
	protected static void getIndices(ProvenanceNode root, List<Integer> indices)
	{
		NodeFunction nf = root.getNodeFunction();
		if ((root.getParents().isEmpty() || root.getParents().get(0) instanceof BrokenChain) && nf instanceof InputValue)
		{
			InputValue iv = (InputValue) nf;
			int pos = iv.getStreamPosition();
			if (!indices.contains(pos))
			{
				indices.add(pos);
			}
		}
		else
		{
			for (ProvenanceNode parent : root.getParents())
			{
				getIndices(parent, indices);
			}
		}
	}
	
	public static void printTree(ProvenanceNode root, PrintStream out)
	{
		printTree(root, out, "");
	}
	
	protected static void printTree(ProvenanceNode root, PrintStream out, String indent)
	{
		out.println(indent + root.toString());
		String new_indent = indent + "  ";
		for (ProvenanceNode parent : root.getParents())
		{
			printTree(parent, out, new_indent);
		}
	}
}
