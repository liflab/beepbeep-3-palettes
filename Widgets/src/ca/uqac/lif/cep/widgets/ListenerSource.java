package ca.uqac.lif.cep.widgets;

import ca.uqac.lif.cep.tmf.Source;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A {@link ca.uqac.lif.cep.tmf.Source Source} processor that can be added
 * as an {@link java.awt.event.ActionListener ActionListener} to an AWT or
 * Swing widget.
 * @author Sylvain Hallé
 */
public class ListenerSource extends Source implements ActionListener, ChangeListener
{
  /**
   * Creates a new action listener source
   * @param out_arity The output arity of the source
   */
  public ListenerSource()
  {
    super(1);
  }
  
  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    outputs.addAll(m_tempQueue);
    m_tempQueue.clear();
    return true;
  }
  
  @Override
  public void reset()
  {
    super.reset();
    m_tempQueue.clear();
  }

  @Override
  public ListenerSource duplicate(boolean with_state)
  {
    throw new UnsupportedOperationException("Method duplicate is not supported on this processor");
  }

  @Override
  public void stateChanged(ChangeEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    m_tempQueue.add(new Object[] {e});
    push(); 
  }
}
