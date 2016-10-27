package examples;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.interpreter.Interpreter;
import ca.uqac.lif.cep.tuples.Tuple;

/**
 * Execute a <code>SELECT</code> statement on a stream of tuples to
 * produce another stream of tuples.
 * 
 * @author Sylvain Hallé
 */
public class SelectFromEsqlTuple
{
	public static void main(String[] args)
	{
		// Instantiate interpreter and load a few palettes
		Interpreter my_int = new Interpreter();
		my_int.extendGrammar(ca.uqac.lif.cep.io.PackageExtension.class);
		my_int.extendGrammar(ca.uqac.lif.cep.tuples.PackageExtension.class);
		
		// Run query and extract results
		Pullable p = my_int.executeQuery("SELECT ((foo) + (bar)) AS abc, (baz) "
				+ "FROM (THE TUPLES OF (FILE \"tuples.csv\"))");
		while (p.hasNext())
		{
			Tuple tup = (Tuple) p.pull();
			System.out.println("The tuple is: " + tup);
		}
	}
}
