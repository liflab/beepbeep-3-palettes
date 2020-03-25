/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
package ca.uqac.lif.cep.widgets;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.io.Print;
import javax.swing.JButton;
import javax.swing.JFrame;

public class TestFrame
{

  public static void main(String[] args)
  {
    JFrame frame = new JFrame();
    JButton button = new JButton();
    button.setText("Click this button");
    frame.getContentPane().add(button);
    frame.pack();
    frame.setVisible(true);
    ListenerSource als = new ListenerSource();
    button.addActionListener(als);
    TurnInto ti = new TurnInto("foo");
    Connector.connect(als, ti);
    Print print = new Print();
    Connector.connect(ti, print);
  }

}
