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
package ca.uqac.lif.cep.apache;

import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionProcessor;

/**
 * Converts a stream of lines from an Apache log into a stream of
 * HTTP request objects
 * @author Sylvain Hallé
 */
public class ApacheLogFeeder extends FunctionProcessor 
{
	public ApacheLogFeeder()
	{
		super(ParseCommonLog.instance);
	}
	
	ApacheLogFeeder(Function f)
	{
		super(f);
	}

	@Override
	public ApacheLogFeeder duplicate() 
	{
		return new ApacheLogFeeder(getFunction().duplicate());
	}

}
