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
package ca.uqac.lif.cep.tuples;

import java.io.File;
import java.io.FileNotFoundException;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.io.SpliceSource;

/**
 * A splice source that generates a stream of tuples.
 * @author Sylvain Hallé
 * @since 0.8
 */
public class SpliceTupleSource extends SpliceSource
{
  public SpliceTupleSource(boolean read_stdin, String ... filenames)
  {
    super(read_stdin, filenames);
  }

  @Override
  protected Processor getSource(String filename)
  {
    return new GroupProcessor(0, 1) {{
      Processor in;
      try
      {
        if (filename.compareTo("-") == 0)
        {
          in = new ReadLines(System.in);
        }
        else
        {
          in = new ReadLines(new File(filename));
        }
      }
      catch (FileNotFoundException e)
      {
        throw new ProcessorException(e);
      }
      TupleFeeder feeder = new TupleFeeder();
      Connector.connect(in, feeder);
      addProcessors(in, feeder);
      associateOutput(0, feeder, 0);
    }};
  }
}