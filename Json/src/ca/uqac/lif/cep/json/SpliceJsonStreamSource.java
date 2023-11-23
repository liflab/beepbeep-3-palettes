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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.io.SpliceSource;

/**
 * A splice source that reads a JSONStream document and produces a stream of
 * JSON elements.
 * @author Sylvain Hallé
 * @since 0.8
 */
public class SpliceJsonStreamSource extends SpliceSource
{
  public SpliceJsonStreamSource(boolean read_stdin, String ... filenames)
  {
    super(read_stdin, filenames);
  }

  @Override
  protected Processor getSource(String filename)
  {
    try
    {
      if (filename.compareTo("-") == 0)
      {
        return new JsonLineFeeder(System.in);
      }
      else
      {
        return new JsonLineFeeder(new FileInputStream(new File(filename)));
      }
    }
    catch (FileNotFoundException e)
    {
      throw new ProcessorException(e);
    }
  }
}