package examples;

import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.interpreter.Interpreter;
import ca.uqac.lif.cep.tuples.Tuple;

/**
 * Read tuples from a CSV file with ESQL
 * 
 * @author Sylvain Hallé
 */
public class ReadTuplesEsql
{
	public static void main(String[] args) throws ConnectorException
	{
		// Instantiate interpreter and load a few palettes
		Interpreter my_int = new Interpreter();
		my_int.load(ca.uqac.lif.cep.io.PackageExtension.class);
		my_int.load(ca.uqac.lif.cep.tuples.PackageExtension.class);
		
		// Run query and extract results
		Pullable p = my_int.executeQuery("THE TUPLES OF (FILE \"tuples.csv\")");
		while (p.hasNext())
		{
			Tuple tup = (Tuple) p.pull();
			System.out.println("The tuple is: " + tup);
		}
	}
}
