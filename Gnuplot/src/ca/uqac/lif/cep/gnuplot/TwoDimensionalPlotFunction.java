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

/**
 * Common ancestor of all two-dimensional plot functions.
 * @author Sylvain Hallé
 */
public abstract class TwoDimensionalPlotFunction extends PlotFunction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3856986619628694581L;

	/**
	 * The label given to the x axis
	 */
	protected String m_labelX = "";
	
	/**
	 * The label given to the y axis
	 */
	protected String m_labelY = "";
	
	/**
	 * Sets the label given to the x axis
	 * @param label The label
	 * @return This function
	 */
	public TwoDimensionalPlotFunction setLabelX(String label)
	{
		m_labelX = label;
		return this;
	}
	
	/**
	 * Sets the label given to the y axis
	 * @param label The label
	 * @return This function
	 */
	public TwoDimensionalPlotFunction setLabelY(String label)
	{
		m_labelY = label;
		return this;
	}
	
	@Override
	public void fillHeader(StringBuilder out)
	{
		super.fillHeader(out);
		out.append("set xlabel \"").append(m_labelX).append("\"\n");
		out.append("set ylabel \"").append(m_labelY).append("\"\n");
	}
}
