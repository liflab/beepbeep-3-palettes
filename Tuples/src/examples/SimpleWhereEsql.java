package examples;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tuples.Tuple;

/**
 * Execute a window query in ESQL.
 * 
 * @author Sylvain Hallé
 */
public class SimpleWhereEsql
{
	public static void main(String[] args) throws ParseException
	{
		// Instantiate interpreter and load a few palettes
		Interpreter my_int = new Interpreter();
		my_int.load(ca.uqac.lif.cep.io.PackageExtension.class);
		my_int.load(ca.uqac.lif.cep.tuples.PackageExtension.class);
		
		// Run query and extract results
		Pullable p = my_int.executeQuery("SELECT foo + bar AS abc, baz "
				+ "(FROM THE TUPLES OF FILE \"tuples.csv\") WHERE bar > baz");
		while (p.hasNext())
		{
			Tuple tup = (Tuple) p.pull();
			System.out.println("The tuple is: " + tup);
		}
	}
}
