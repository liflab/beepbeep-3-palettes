/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2026 Sylvain Hallé

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
package ca.uqac.lif.cep.zeppelin;

import java.util.Base64;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Converts a byte array representing an image into a Base64-encoded
 * HTML &lt;img&gt; tag.
 * @author Sylvain Hallé
 * @since 3.14
 */
public class ToBase64Image extends UnaryFunction<byte[], String>
{
	/**
	 * A singleton instance of the function.
	 */
	public static final ToBase64Image instance = new ToBase64Image();
	
	/**
	 * Creates a new instance of the function.
	 */
	protected ToBase64Image()
	{
		super(byte[].class, String.class);
	}

	@Override
	public String getValue(byte[] bytes)
	{
		Base64.Encoder e = Base64.getEncoder();
		return "<img src=\"data:image/png;base64," + e.encodeToString(bytes) + "\"/>";
	}
}
