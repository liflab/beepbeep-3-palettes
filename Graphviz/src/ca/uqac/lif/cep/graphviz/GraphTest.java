/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2023 Sylvain Hallé and friends

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
package ca.uqac.lif.cep.graphviz;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.cep.graphviz.Graph.Edge;

/**
 * Unit tests for {@link Graph}.
 * @author Sylvain Hallé
 */
public class GraphTest
{
  @Test
  public void testDuplicate1()
  {
    Graph g = new Graph();
    g.add(new Edge("A", "B", 10));
    g.add(new Edge("B", "B", 3));
    Graph g_dup = g.duplicate();
    assertEquals(3, g_dup.getInWeight("B"), 0);
  }
}
