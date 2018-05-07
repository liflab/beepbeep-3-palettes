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

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Function that takes as input a multiset of objects, and produces a text
 * string suitable for sending into the Gnuplot software.
 * @author Sylvain Hallé
 */
@Deprecated
public abstract class PlotFunction extends UnaryFunction<Multiset,String> 
{
	/**
	 * The possible types of image produced by a plot function
	 */
	public static enum Terminal {PNG, PDF};
	
	/**
	 * Symbol for missing data
	 */
	public static final transient String s_datafileMissing = "?";

	/**
	 * The type of image produced by this plot function
	 */
	protected Terminal m_terminal = Terminal.PNG;
	
	/**
	 * The title of the plot
	 */
	protected String m_title = "BeepBeep 3 untitled plot";
	
	public PlotFunction()
	{
		super(Multiset.class, String.class);
	}
	
	/**
	 * Sets the type of image produced by this plot function
	 * @param term The terminal
	 */
	public void setTerminal(Terminal term)
	{
		m_terminal = term;
	}
	
	/**
	 * Sets the title of the plot
	 * @param title The title
	 */
	public void setTitle(String title)
	{
		if (title == null)
		{
			m_title = "";
		}
		else
		{
			m_title = title;
		}
	}
	
	/**
	 * Converts a terminal value into its Gnuplot string value
	 * @param term The terminal
	 * @return the value
	 */
	public static String terminalToString(Terminal term)
	{
		switch (term)
		{
		case PNG:
			return "png";
		case PDF:
			return "pdf";
		default:
			return "dumb";
		}
	}
	
	public void fillHeader(StringBuilder out)
	{
		out.append("set terminal ").append(terminalToString(m_terminal)).append("\n");
		out.append("set datafile separator \",\"\n");
		out.append("set datafile missing \"").append(s_datafileMissing).append("\"\n");
	}
	
	public static String format(Object o, String missing_symbol)
	{
		if (o == null)
		{
			return missing_symbol;
		}
		if (o instanceof Number)
		{
			return o.toString();
		}
		return "\"" + o.toString() + "\"";
	}
	
	public static String format(Object o)
	{
		return format(o, s_datafileMissing);
	}
}
