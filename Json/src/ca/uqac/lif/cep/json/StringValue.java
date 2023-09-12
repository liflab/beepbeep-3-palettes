/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2023 Sylvain Hallé

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
package ca.uqac.lif.cep.json;

import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.json.JsonString;

/**
 * Extracts a number value from a JSON element.
 * @author Sylvain Hallé
 */
public class StringValue extends UnaryFunction<JsonElement,String>
{
  public static final transient StringValue instance = new StringValue();
  
  protected StringValue()
  {
    super(JsonElement.class, String.class);
  }
  
  @Override
  public String getValue(JsonElement x)
  {
    if (x instanceof JsonString)
    {
      return ((JsonString) x).stringValue();
    }
    return "";
  }
}
