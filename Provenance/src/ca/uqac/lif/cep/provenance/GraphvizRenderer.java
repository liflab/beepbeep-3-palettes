package ca.uqac.lif.cep.provenance;

import ca.uqac.lif.mtnp.plot.gnuplot.GnuPlot;

/**
 * Renders a string into an image using the Graphviz tool
 * @author Sylvain Hallé
 *
 */
public class GraphvizRenderer 
{
	public static final boolean s_dotPresent = isDotPresent();

	public static final byte[] s_blankImage = GnuPlot.s_blankImagePng;

	/**
	 * The time to wait before polling DOT's result
	 */
	protected static transient long s_waitInterval = 100;

	public byte[] dotToImage(String dot_string, String filetype)
	{
		byte[] image = null;
		String[] command = new String[]{"dot", "-T" + filetype};
		CommandRunner runner = new CommandRunner(command, dot_string);
		runner.start();
		// Wait until the command is done
		while (runner.isAlive())
		{
			// Wait 0.1 s and check again
			try
			{
				Thread.sleep(s_waitInterval);
			}
			catch (InterruptedException e)
			{
				// This happens if the user cancels the command manually
				runner.stopCommand();
				runner.interrupt();
				return s_blankImage;
			}
		}
		image = runner.getBytes();
		if (runner.getErrorCode() != 0 || image == null || image.length == 0)
		{
			// Gnuplot could not produce a picture; return the blank image
			image = s_blankImage;
		}
		return image;
	}

	/**
	 * Checks if DOT is present on the command line
	 * @return {@code true} if present
	 */
	public static boolean isDotPresent()
	{
		// Check if DOT is present
		String[] args = {"dot", "--version"};
		CommandRunner runner = new CommandRunner(args);
		runner.run();
		// Exception: dot returns 1 when called
		return runner.getErrorCode() == 1;
	}
}
