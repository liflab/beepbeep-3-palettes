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

import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.tuples.TupleComparator;

/**
 * Generates a scatterplot from a {@link ca.uqac.lif.cep.sets.Multiset}
 * of tuples.
 */
public class GnuplotScatterplot extends TwoDimensionalPlotFunction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1092510159329816142L;

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
	
	/**
	 * Whether the columns in each tuple are fixed, or may vary from one tuple
	 * to the next 
	 */
	protected boolean m_fixedColumns = true;
	
	/**
	 * Whether the multiple data series are stacked (i.e. added on top
	 * of each other)
	 */
	protected boolean m_stacked = false;

	public GnuplotScatterplot()
	{
		super();
		m_otherHeaders = null;
	}

	public GnuplotScatterplot(String column_name)
	{
		this(column_name, true);
	}
	
	public GnuplotScatterplot(String column_name, boolean fixed_columns)
	{
		super();
		m_otherHeaders = null;
		m_fixedColumns = fixed_columns;
		setX(column_name);
	}
	
	/**
	 * Sets whether the multiple data series should be stacked 
	 * (i.e. added on top of each other)
	 * @param b Set to {@code true} to have a stacked plot
	 */
	public void setStacked(boolean b)
	{
		m_stacked = b;
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
		if (m_otherHeaders == null || !m_fixedColumns)
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
		String style = "linespoints";
		if (m_stacked)
		{
			style = "filledcurves x1";
		}
		for (int i = 0; i < m_otherHeaders.length; i++)
		{
			String header = m_otherHeaders[i];
			if (i > 0)
			{
				out.append(", ");
			}
			out.append("\"-\" u 1:");
			if (m_stacked)
			{
				out.append("(");
				for (int j = 2; j <= m_otherHeaders.length - i + 1; j++)
				{
					if (j > 2)
					{
						out.append("+");
					}
					out.append("$").append(j);
				}
				out.append(")");
			}
			else
			{
				out.append(i + 2);
			}
			out.append(" t \"").append(header).append("\" w ").append(style);
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
		String missing_symbol = s_datafileMissing;
		if (m_stacked)
		{
			// In a stacked plot, we must treat missing data as 0
			missing_symbol = "0";
		}
		for (Tuple tuple : tuples)
		{
			out.append(format(tuple.get(m_xHeader)));
			for (String key : m_otherHeaders)
			{
				out.append(",").append(format(tuple.get(key), missing_symbol));
			}
			out.append("\n");
		}
		return out;
	}

	/**
	 * Fetches column names from an element (i.e. a tuple) from the
	 * bag. If {@link #m_fixedColumns} is set to {@code true}, the method
	 * picks <em>any</em> tuple and fetches its columns. Otherwise, the method
	 * picks the <em>largest</em> tuple and fetches its columns.
	 * <p><strong>Warning:</strong> Gnuplot can handle a maximum of
	 * <u>7</u> columns, and <em>segfaults</em> when given more. To avoid
	 * that, this method limits itself to the first 7 column names it
	 * encounters and discards the rest.</p> 
	 * @param bag The data bag
	 */
	protected void getColumnNames(Multiset bag)
	{
		Tuple nt = null;
		if (m_fixedColumns)
		{
			nt = (Tuple) bag.getAnyElement();
		}
		else
		{
			int max_size = 0;
			for (Object o : bag)
			{
				Tuple t = (Tuple) o;
				if (t.size() > max_size)
				{
					nt = t;
					max_size = t.size();
				}
			}
		}
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
