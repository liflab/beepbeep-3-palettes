/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package examples;

import java.io.InputStream;

import static ca.uqac.lif.cep.Connector.connect;
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
	public static void main(String[] args) 
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
