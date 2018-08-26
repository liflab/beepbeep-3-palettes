package ca.uqac.lif.cep.xml;

import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.xml.XmlElement;
import ca.uqac.lif.xml.XmlElement.XmlParseException;

/**
 * Function that converts a string into an XML element
 */
public class ParseXml extends UnaryFunction<String,XmlElement> 
{
	/**
	 * Instance of the function
	 */
	public static ParseXml instance = new ParseXml();
	
	protected ParseXml()
	{
		super(String.class, XmlElement.class);
	}
	
	@Override
	public /*@NotNull*/ XmlElement getValue(String x)
	{
		try 
		{
			return XmlElement.parse(x);
		} 
		catch (XmlParseException e) 
		{
			return new XmlElement("");
		}
	}
}