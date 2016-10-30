package examples;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.interpreter.Interpreter;

/**
 * Extract values from a trace of tuples
 * with a <code>SELECT</code> statement, and compute their
 * sum over a sliding <code>WINDOW</code>.
 * 
 * @author Sylvain Hallé
 */
public class WindowSumEsql
{
	public static void main(String[] args)
	{
		// Instantiate interpreter and load a few palettes
		Interpreter my_int = new Interpreter();
		my_int.load(ca.uqac.lif.cep.io.PackageExtension.class);
		my_int.load(ca.uqac.lif.cep.tuples.PackageExtension.class);
		
		// Run query and extract results
		Pullable p = my_int.executeQuery("APPLY "
				+ "(COMBINE (*) WITH ADDITION) " 
				+ "ON ("
				+ "  SELECT (foo × bar) "
				+ "  FROM (THE TUPLES OF (FILE \"tuples.csv\"))) "
				+ "ON A WINDOW OF 3");
		while (p.hasNext())
		{
			Object o = p.pull();
			System.out.println("The value is: " + o);
		}
	}
}
