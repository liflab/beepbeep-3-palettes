package examples;

import java.io.InputStream;

import static ca.uqac.lif.cep.Connector.connect;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.io.LineReader;
import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.tuples.TupleFeeder;

/**
 * Read tuples from a CSV file
 * 
 * @author Sylvain Hallé
 */
public class ReadTuples
{
	public static void main(String[] args) throws ConnectorException
	{
		InputStream is = ReadTuples.class.getResourceAsStream("tuples.csv");
		LineReader lr = new LineReader(is);
		TupleFeeder tf = new TupleFeeder();
		connect(lr, tf);
		Pullable p = tf.getPullableOutput();
		while (p.hasNext())
		{
			Tuple tup = (Tuple) p.pull();
			System.out.println("The tuple is: " + tup);
		}
	}
}
