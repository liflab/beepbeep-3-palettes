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
