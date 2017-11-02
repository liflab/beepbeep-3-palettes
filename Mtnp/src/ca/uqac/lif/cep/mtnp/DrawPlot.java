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
import ca.uqac.lif.mtnp.table.Table;

/**
 * Generates a bitmap from a {@link ca.uqac.lif.mtnp.table.Table Table}
 * object. The bitmap is represented as a byte array in some predefined
 * image format (PNG by default).
 *  
 * @author Sylvain Hallé
 *
 */
public class DrawPlot extends UniformProcessor
{
	protected Plot m_plot;
	
	protected ImageType m_type;
	
	public DrawPlot()
	{
		this(null, ImageType.PNG);
	}
	
	public DrawPlot(Plot plot, ImageType type)
	{
		super(1, 1);
		m_plot = plot;
		m_type = type;
	}
	
	public DrawPlot(Plot plot)
	{
		this(plot, ImageType.PNG);
	}
	
	public DrawPlot setPlot(Plot plot)
	{
		m_plot = plot;
		return this;
	}
	
	public Plot getPlot()
	{
		return m_plot;
	}
	
	public DrawPlot setImageType(ImageType type)
	{
		m_type = type;
		return this;
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) throws ProcessorException 
	{
		Table t = (Table) inputs[0];
		m_plot.setTable(t);
		byte[] bytes = m_plot.getImage(m_type);
		outputs[0] = bytes;
		return true;
	}

	@Override
	public Processor clone()
	{
		// Does not make much sense to clone this
		throw new UnsupportedOperationException();
	}
}
