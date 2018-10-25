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
package ca.uqac.lif.cep.mtnp;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.mtnp.plot.Plot;
import ca.uqac.lif.mtnp.plot.Plot.ImageType;
import ca.uqac.lif.mtnp.plot.gnuplot.GnuPlot;
import ca.uqac.lif.mtnp.table.Table;

/**
 * Generates a GnuPlot file drawing a plot from a 
 * {@link ca.uqac.lif.mtnp.table.Table Table} object.
 *  
 * @author Sylvain Hallé
 */
public class PrintGnuPlot extends UniformProcessor
{
	protected GnuPlot m_plot;
	
	protected ImageType m_type;
	
	public PrintGnuPlot()
	{
		this(null, ImageType.PNG);
	}
	
	public PrintGnuPlot(GnuPlot plot, ImageType type)
	{
		super(1, 1);
		m_plot = plot;
		m_type = type;
	}
	
	public PrintGnuPlot(GnuPlot plot)
	{
		this(plot, ImageType.PNG);
	}
	
	public PrintGnuPlot setPlot(GnuPlot plot)
	{
		m_plot = plot;
		return this;
	}
	
	public Plot getPlot()
	{
		return m_plot;
	}
	
	public PrintGnuPlot setImageType(ImageType type)
	{
		m_type = type;
		return this;
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) throws ProcessorException 
	{
		Table t = (Table) inputs[0];
		m_plot.setTable(t);
		String s = m_plot.toGnuplot(m_type, false);
		outputs[0] = s;
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		// Does not make much sense to clone this
		throw new UnsupportedOperationException();
	}
}
