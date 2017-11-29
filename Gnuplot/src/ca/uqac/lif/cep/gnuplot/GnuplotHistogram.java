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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.tuples.TupleComparator;

public class GnuplotHistogram extends TwoDimensionalPlotFunction 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7261854378181577894L;

	/**
	 * The attribute used to group the values in the histogram
	 */
	protected String m_lineAttribute;
	
	/**
	 * The gap (in number of colums) between clusters of bars
	 */
	protected int m_gap = 1;
	
	/**
	 * A comparator used to sort tuples according to their first column
	 */
	protected TupleComparator m_tupleComparator;
	
	public GnuplotHistogram(String line_attribute)
	{
		super();
		m_lineAttribute = line_attribute;
		m_tupleComparator = new TupleComparator(line_attribute);
	}

	@Override
	public String getValue(Multiset set)
	{
		StringBuilder out = new StringBuilder();
		fillHeader(out);
		StringBuilder data_string = new StringBuilder();
		List<String> keys = null;
		List<Tuple> tuples = new ArrayList<Tuple>(set.size());
		for (Object o : set.keySet())
		{
			tuples.add((Tuple) o);
		}
		Collections.sort(tuples, m_tupleComparator);
		for (Tuple tup : tuples)
		{
			if (keys == null)
			{
				keys = getKeys(tup, m_lineAttribute);
				Collections.sort(keys);
				data_string.append(format(m_lineAttribute));
				for (String key : keys)
				{
					data_string.append(",").append(format(key));
				}
				data_string.append("\n");
			}
			data_string.append(format(tup.get(m_lineAttribute).toString()));
			for (String key : keys)
			{
				data_string.append(",").append(format(tup.get(key)));
			}
			data_string.append("\n");
		}
		out.append("plot ");
		for (int i = 0; i < keys.size(); i++)
		{
			if (i > 0)
				out.append(", ");
			out.append("'-' using ").append(i+2).append(":xtic(1) title col");
		}
		out.append("\n");
		for (int i = 0; i < keys.size(); i++)
		{
			out.append(data_string).append("e\n");
		}
		return out.toString();
	}
	
	protected static List<String> getKeys(Tuple tup, String line_attribute)
	{
		List<String> keys = new LinkedList<String>();
		Set<String> s_keys = tup.keySet();
		for (String key : s_keys)
		{
			if (key.compareTo(line_attribute) != 0)
				keys.add(key);
		}
		return keys;
	}
	
	@Override
	public void fillHeader(StringBuilder out)
	{
		super.fillHeader(out);
		out.append("set style data histogram\n");
		out.append("set style histogram cluster gap 1\n");
		out.append("set style fill solid border rgb \"black\"\n");
		out.append("set auto x\n");
	}

	@Override
	public GnuplotHistogram duplicate() 
	{
		return new GnuplotHistogram(m_lineAttribute);
	}
	
	/**
	 * Sets the gap (in number of colums) between clusters of bars
	 * @param gap The gap
	 * @return This plot
	 */
	public GnuplotHistogram setGap(int gap)
	{
		m_gap = gap;
		return this;
	}
}
