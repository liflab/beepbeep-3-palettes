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
package ca.uqac.lif.cep.gnuplot;

import java.util.ArrayDeque;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.io.Caller;

/**
 * Processor taking as input a string containing a Gnuplot graph,
 * and returning as its output the result of calling Gnuplot on
 * that graph. This is probably a string of bytes with the contents
 * of an image.
 * <p>
 * Note that the Gnuplot software must be installed and accessible
 * from the systems's command line by typing <code>gnuplot</code>.
 * @author Sylvain Hallé
 *
 */
public class GnuplotCaller extends Caller
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9108891341149112079L;

	public GnuplotCaller()
	{
		super("gnuplot");
	}
	
	public static void build(ArrayDeque<Object> stack) 
	{
		Processor p = (Processor) stack.pop();
		stack.pop(); // ON
		stack.pop(); // GNUPLOT
		GnuplotCaller caller = new GnuplotCaller();
		Connector.connect(p, caller);
	}
}
