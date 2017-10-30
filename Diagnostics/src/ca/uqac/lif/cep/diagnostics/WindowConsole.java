package ca.uqac.lif.cep.diagnostics;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Prints whatever is given to it in a text window.
 * @author Sylvain Hallé
 */
public class WindowConsole extends DiagnosticsCallback
{
	protected final JTextArea m_textArea;
	
	public WindowConsole(String title)
	{
		JFrame frame = new JFrame(title);
		m_textArea = new JTextArea();
		m_textArea.setEditable(false);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(m_textArea),BorderLayout.CENTER);
		frame.setLocationRelativeTo(null); 
		frame.pack(); 
		frame.setVisible(true); 
	}
	
	@Override
	public void notify(Object o)
	{
		m_textArea.setText(o.toString());
	}
}
