package ca.uqac.lif.cep.provenance;

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.LEFT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.RIGHT;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

/**
 * Not actually a test, but has that suffix so that the build script won't
 * include it in the complied JAR 
 */
public class DemoTest 
{
	public static void main(String[] args) throws ConnectorException
	{
		QueueSource s1 = new QueueSource(1);
		s1.addEvent(2).addEvent(5).addEvent(4).addEvent(6);
		QueueSource s2 = new QueueSource(1);
		s2.addEvent(0).addEvent(1).addEvent(1);
		CountDecimate dec = new CountDecimate(2);
		FunctionProcessor add = new FunctionProcessor(Addition.instance);
		IndexEventTracker it = new IndexEventTracker();
		it.setTo(s1, s2, dec, add);
		Connector.connect(s1, OUTPUT, add, LEFT, it);
		Connector.connect(s2, OUTPUT, dec, INPUT, it);
		Connector.connect(dec, OUTPUT, add, RIGHT, it);
		Pullable p = add.getPullableOutput();
		p.pull();
		p.pull();
		p.pull();
		p.pull();
		p.pull();
		//ProvenanceNode root = it.getProvenanceTree(add, OUTPUT, 2);
		DotProvenanceTreeRenderer renderer = new DotProvenanceTreeRenderer();
		int n = 5;
		ProvenanceNode[] trees = new ProvenanceNode[n];
		for (int i = 0; i < n; i++)
		{
			trees[i] = it.getProvenanceTree(add, OUTPUT, i);
		}
		String dot_file = renderer.toDot(trees);
		System.out.println(dot_file);
	}
}
