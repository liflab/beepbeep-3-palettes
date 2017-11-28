package ca.uqac.lif.cep.xml;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.input.TokenFeeder;

/**
 * Splits an input stream into chunks of XML, and parses each chunk into
 * an <code>XmlElement</code> 
 * @author Sylvain Hallé
 */
public class XmlReader extends GroupProcessor
{
	/**
	 * Dummy UID
	 */
	private static final long serialVersionUID = -1444635792420830597L;

	/**
	 * Processor responsible for splitting the string into chunks
	 */
	protected TokenFeeder m_tokenFeeder;
	
	/**
	 * Processor responsible for parsing the chunks into XML elements
	 */
	protected XmlFeeder m_xmlFeeder;
	
	/**
	 * Create a new XmlReader
	 * @param sep_begin The string representing the start of a well-formed
	 *   chunk
	 * @param sep_end The string representing the end of a well-formed
	 *   chunk
	 */
	public XmlReader(String sep_begin, String sep_end)
	{
		super(1, 1);
		m_tokenFeeder = new XmlTokenFeeder(sep_begin, sep_end);
		addProcessor(m_tokenFeeder);
		m_xmlFeeder  = new XmlFeeder();
		addProcessor(m_xmlFeeder);
		Connector.connect(m_tokenFeeder, m_xmlFeeder);
		associateInput(0, m_tokenFeeder, 0);
		associateOutput(0, m_xmlFeeder, 0);
	}

	@Override
	public XmlReader duplicate() 
	{
		return new XmlReader(m_tokenFeeder.getSeparatorBegin(), m_tokenFeeder.getSeparatorEnd());
	}
	
	/**
	 * Inner processor splitting the string into chunks
	 * @author Sylvain Hallé
	 */
	protected static class XmlTokenFeeder extends TokenFeeder
	{
		/**
		 * Dummy UID
		 */
		private static final long serialVersionUID = 6658632788873940899L;

		public XmlTokenFeeder(String sep_begin, String sep_end)
		{
			super();
			setSeparatorBegin(sep_begin);
			setSeparatorEnd(sep_end);
		}

		@Override
		protected Object createTokenFromInput(String token) 
		{
			return token;
		}

		@Override
		public XmlTokenFeeder duplicate() 
		{
			return new XmlTokenFeeder(getSeparatorBegin(), getSeparatorEnd());
		}
		
	}

}