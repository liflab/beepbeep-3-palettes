package ca.uqac.lif.cep.provenance;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.cep.provenance.EventFunction.InputValue;
import ca.uqac.lif.petitpoucet.BrokenChain;
import ca.uqac.lif.petitpoucet.NodeFunction;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

public class ProvenanceTree
{
	public static List<ProvenanceNode> getInputLeaves(ProvenanceNode root)
	{
		List<ProvenanceNode> indices = new ArrayList<ProvenanceNode>();
		getInputLeaves(root, indices);
		return indices;
	}
	
	protected static void getInputLeaves(ProvenanceNode root, List<ProvenanceNode> leaves)
	{
		NodeFunction nf = root.getNodeFunction();
		if ((root.getParents().isEmpty() || root.getParents().get(0) instanceof BrokenChain) && nf instanceof InputValue)
		{
			if (!leaves.contains(root))
			{
				leaves.add(root);
			}
		}
		else
		{
			for (ProvenanceNode parent : root.getParents())
			{
				getInputLeaves(parent, leaves);
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
}
