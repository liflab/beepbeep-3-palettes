/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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
package ca.uqac.lif.cep.tuples.examples;

import java.io.InputStream;

import static ca.uqac.lif.cep.Connector.connect;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.io.LineReader;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tuples.AttributeExpression;
import ca.uqac.lif.cep.tuples.Select;
import ca.uqac.lif.cep.tuples.TupleFeeder;
import ca.uqac.lif.cep.tuples.Select.SelectFunction;

public class WindowAverage
{
	public static void main(String[] args) throws ConnectorException
	{
		InputStream is = WindowAverage.class.getResourceAsStream("stocks.csv");
		LineReader lines = new LineReader(is);
		TupleFeeder tuples = new TupleFeeder();
		connect(lines, tuples);
		Fork fork = new Fork(2);
		connect(tuples, fork); 
		Select select = new Select(new SelectFunction(new AttributeExpression[]{
				
		}));
		
		
	}
}
