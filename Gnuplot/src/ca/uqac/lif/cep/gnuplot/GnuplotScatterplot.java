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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.uqac.lif.cep.sets.Multiset;
import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.tuples.TupleComparator;

/**
 * Generates a scatterplot from a {@link ca.uqac.lif.cep.sets.Multiset}
 * of tuples.
 */
public class GnuplotScatterplot extends TwoDimensionalPlotFunction
{
	/**
	 * The name of the column containing the <i>x</i> values of the
	 * plot
	 */
	protected String m_xHeader;

	/**
	 * The names of the other columns. We store them into an array,
	 * as we cannot guarantee that every line of the input data will
	 * list them in the same order.
	 */
	protected String[] m_otherHeaders;

	public GnuplotScatterplot()
	{
		super();
		m_otherHeaders = null;
	}

	public GnuplotScatterplot(String column_name)
	{
		super();
		m_otherHeaders = null;
		setX(column_name);
	}	

	/**
	 * Defines the name of the column that contains the "x" values of
	 * the plot
	 * @param name The name of the column
	 * @return The plot
	 */
	public GnuplotScatterplot setX(String name)
	{
		m_xHeader = name;
		return this;
	}

	@Override
	public String getValue(Multiset bag) 
	{
		if (m_otherHeaders == null)
		{
			// We haven't harvested the column names yet: do that first
			getColumnNames(bag);
		}
		return generatePlot(bag);
	}

	protected String generatePlot(Multiset bag)
	{
		StringBuilder out = new StringBuilder();
		fillHeader(out);
		StringBuilder plot_data = generatePlotData(bag);
		out.append("plot ");
		for (int i = 0; i < m_otherHeaders.length; i++)
		{
			String header = m_otherHeaders[i];
			if (i > 0)
			{
				out.append(", ");
			}
			out.append("\"-\" u 1:").append(i + 2).append(" t \"").append(header).append("\" w linespoints");
		}
		out.append("\n");
		// Repeat the data as many times as there are columns
		for (int i = 0; i < m_otherHeaders.length; i++)
		{
			out.append(plot_data).append("\ne\n");
		}
		return out.toString();
	}

	protected StringBuilder generatePlotData(Multiset bag)
	{
		StringBuilder out = new StringBuilder();
		List<Tuple> tuples = new ArrayList<Tuple>();
		for (Object o : bag.keySet())
		{
			tuples.add((Tuple) o);
		}
		Collections.sort(tuples, new TupleComparator(m_xHeader));
		// First, fetch all lines
		for (Tuple tuple : tuples)
		{
			out.append(format(tuple.get(m_xHeader)));
			for (String key : m_otherHeaders)
			{
				out.append(",").append(format(tuple.get(key)));
			}
			out.append("\n");
		}
		return out;
	}

	/**
	 * Fetches column names from an element (i.e. a tuple) from the
	 * bag.
	 * <p><strong>Warning:</strong> Gnuplot can handle a maximum of
	 * <u>7</u> columns, and <em>segfaults</em> when given more. To avoid
	 * that, this method limits itself to the first 7 column names it
	 * encounters and discards the rest.</p> 
	 * @param bag The data bag
	 */
	protected void getColumnNames(Multiset bag)
	{
		Tuple nt = (Tuple) bag.getAnyElement();
		List<String> names = new ArrayList<String>();
		int col_count = 0;
		for (String s : nt.keySet())
		{
			if (s.compareTo(m_xHeader) != 0)
			{
				names.add(s);
				col_count++;
			}
			if (col_count == 7)
			{
				// We reached 7 elements: skip the others
				break;
			}
		}
		m_otherHeaders = new String[names.size()];
		names.toArray(m_otherHeaders);
	}

}
