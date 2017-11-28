package ca.uqac.lif.cep.provenance;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.CumulativeProcessor.StartValue;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.functions.Negation;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.provenance.EventFunction.InputValue;
import ca.uqac.lif.cep.provenance.EventFunction.OutputValue;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Passthrough;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.QueueSource.QueueFunction;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.petitpoucet.NodeFunction;
import ca.uqac.lif.petitpoucet.ProvenanceNode;

public class IndexEventTrackerTest 
{
	@Test
	public void testFunctionUnary() 
	{
		IndexEventTracker tracker = new IndexEventTracker();
		FunctionProcessor add = new FunctionProcessor(Negation.instance);
		QueueSource source = new QueueSource(1);
		source.addEvent(true);
		Connector.connect(source, add);
		tracker.setTo(source, add);
		int add_id = add.getId();
		Pullable p = add.getPullableOutput();
		p.pull();
		ProvenanceNode node = tracker.fetchProvenanceNode(add_id, 0, 0);
		assertEquals(1, node.getParents().size());
		p.pull();
		node = tracker.fetchProvenanceNode(add_id, 0, 1);
		assertEquals(1, node.getParents().size());
	}
	
	@Test
	public void testFunctionBinary() 
	{
		IndexEventTracker tracker = new IndexEventTracker();
		FunctionProcessor add = new FunctionProcessor(Addition.instance);
		QueueSource source1 = new QueueSource(1);
		source1.addEvent(1).addEvent(2);
		QueueSource source2 = new QueueSource(1);
		source2.addEvent(3).addEvent(4).addEvent(5);;
		Connector.connect(source1, 0, add, 0);
		Connector.connect(source2, 0, add, 1);
		tracker.setTo(source1, source2, add);
		int add_id = add.getId();
		Pullable p = add.getPullableOutput();
		p.pull();
		ProvenanceNode node = tracker.fetchProvenanceNode(add_id, 0, 0);
		assertEquals(2, node.getParents().size());
		p.pull();
		node = tracker.fetchProvenanceNode(add_id, 0, 1);
		assertEquals(2, node.getParents().size());
	}
	
	@Test
	public void testFunctionCumulative() 
	{
		IndexEventTracker tracker = new IndexEventTracker();
		CumulativeProcessor add = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
		QueueSource source = new QueueSource(1);
		source.addEvent(1);
		Connector.connect(source, add);
		tracker.setTo(source, add);
		int add_id = add.getId();
		Pullable p = add.getPullableOutput();
		p.pull();
		ProvenanceNode node = tracker.fetchProvenanceNode(add_id, 0, 0);
		assertEquals(2, node.getParents().size());
		InputValue ei1 = (InputValue) node.getParents().get(0).getNodeFunction();
		assertEquals(0, ei1.getStreamIndex());
		assertEquals(0, ei1.getStreamPosition());
		StartValue sv1 = (StartValue) node.getParents().get(1).getNodeFunction();
		assertEquals(0, sv1.getStreamIndex()); 
		p.pull();
		node = tracker.fetchProvenanceNode(add_id, 0, 1);
		assertEquals(2, node.getParents().size());
		ei1 = (InputValue) node.getParents().get(0).getNodeFunction();
		assertEquals(0, ei1.getStreamIndex());
		assertEquals(1, ei1.getStreamPosition());
		OutputValue ov2 = (OutputValue) node.getParents().get(1).getNodeFunction();
		assertEquals(0, ov2.getStreamIndex());
		assertEquals(0, ov2.getStreamPosition());
		assertEquals(add_id, ov2.getProcessorId());
		p.pull();
		node = tracker.fetchProvenanceNode(add_id, 0, 2);
		assertEquals(2, node.getParents().size());
		ei1 = (InputValue) node.getParents().get(0).getNodeFunction();
		assertEquals(0, ei1.getStreamIndex());
		assertEquals(2, ei1.getStreamPosition());
		ov2 = (OutputValue) node.getParents().get(1).getNodeFunction();
		assertEquals(0, ov2.getStreamIndex());
		assertEquals(1, ov2.getStreamPosition());
		assertEquals(add_id, ov2.getProcessorId());
	}
	
	@Test
	public void testCountDecimate() 
	{
		int decimate_interval = 3;
		QueueSource source = new QueueSource(1);
		source.addEvent("A").addEvent("B").addEvent("C").addEvent("D").addEvent("E");
		CountDecimate dec = new CountDecimate(decimate_interval);
		IndexEventTracker tracker = new IndexEventTracker();
		tracker.setTo(source, dec);
		Connector.connect(tracker, source, dec);
		Pullable p = dec.getPullableOutput();
		p.pull();
		p.pull();
		ProvenanceNode pn;
		pn = tracker.getProvenanceTree(dec.getId(), 0, 0);
		OutputValue ov;
		ov = (OutputValue) pn.getNodeFunction();
		assertEquals(0, ov.getStreamPosition());
		pn = pn.getParents().get(0);
		ov = (OutputValue) pn.getNodeFunction();
		assertEquals(0, ov.getStreamPosition());
		pn = tracker.getProvenanceTree(dec.getId(), 0, 1);
		ov = (OutputValue) pn.getNodeFunction();
		assertEquals(1, ov.getStreamPosition());
		pn = pn.getParents().get(0);
		ov = (OutputValue) pn.getNodeFunction();
		assertEquals(decimate_interval, ov.getStreamPosition());
	}
	
	@Test
	public void testUnaryChain1() 
	{
		IndexEventTracker tracker = new IndexEventTracker();
		FunctionProcessor add = new FunctionProcessor(Negation.instance);
		QueueSource source = new QueueSource(1);
		Passthrough pt = new Passthrough(1);
		source.addEvent(true);
		Connector.connect(tracker, source, add, pt);
		tracker.setTo(source, add, pt);
		Pullable p1 = pt.getPullableOutput();
		p1.pull();
		ProvenanceNode n1 = tracker.getProvenanceTree(pt.getId(), 0, 0);
		assertNotNull(n1);
		assertTrue(n1.getNodeFunction() instanceof OutputValue);
		assertEquals(1, n1.getParents().size());
		ProvenanceNode n2 = n1.getParents().get(0);
		NodeFunction n2_f = n2.getNodeFunction();
		assertTrue(n2_f instanceof OutputValue);
		assertEquals(1, n2.getParents().size());
		NodeFunction n3_f = n2.getParents().get(0).getParents().get(0).getNodeFunction();
		assertTrue(n3_f instanceof QueueFunction);
		assertEquals(0, ((QueueFunction) n3_f).getIndex());
	}
	
	@Test
	public void testUnaryChain2() 
	{
		IndexEventTracker tracker = new IndexEventTracker();
		FunctionProcessor add = new FunctionProcessor(Negation.instance);
		QueueSource source = new QueueSource(1);
		int delay = 3;
		Trim pt = new Trim(delay);
		source.addEvent(true);
		Connector.connect(tracker, source, add, pt);
		tracker.setTo(source, add, pt);
		Pullable p1 = pt.getPullableOutput();
		p1.pull();
		ProvenanceNode n1 = tracker.getProvenanceTree(pt.getId(), 0, 0);
		assertNotNull(n1);
		assertTrue(n1.getNodeFunction() instanceof OutputValue);
		assertEquals(1, n1.getParents().size());
		ProvenanceNode n2 = n1.getParents().get(0);
		NodeFunction n2_f = n2.getNodeFunction();
		assertTrue(n2_f instanceof OutputValue);
		assertEquals(delay, ((OutputValue) n2_f).getStreamPosition());
	}
}
