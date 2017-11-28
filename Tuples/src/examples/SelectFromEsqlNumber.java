package examples;

import ca.uqac.lif.cep.Pullable;

/**
 * Execute a <code>SELECT</code> statement on a stream of tuples to
 * produce a stream of numbers.
 * 
 * @author Sylvain Hallé
 */
public class SelectFromEsqlNumber
{
	public static void main(String[] args) throws ParseException
	{
		// Instantiate interpreter and load a few palettes
		Interpreter my_int = new Interpreter();
		my_int.load(ca.uqac.lif.cep.io.PackageExtension.class);
		my_int.load(ca.uqac.lif.cep.tuples.PackageExtension.class);
		
		// Run query and extract results
		Pullable p = my_int.executeQuery("SELECT foo + bar "
				+ "FROM THE TUPLES OF (FILE \"tuples.csv\")");
		while (p.hasNext())
		{
			Object o = p.pull();
			System.out.println("The event is of type " 
				+ o.getClass().getSimpleName() + " and is " + o);
		}
	}
}
