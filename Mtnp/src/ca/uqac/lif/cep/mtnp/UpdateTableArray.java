package ca.uqac.lif.cep.mtnp;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.mtnp.table.TableEntry;

public class UpdateTableArray extends UpdateTable
{
	public UpdateTableArray(String ... column_names)
	{
		super(1, column_names);
	}
	
	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) throws ProcessorException
	{
		if (inputs[0] instanceof TableEntry)
		{
			m_table.add((TableEntry) inputs[0]);
		}
		else if (inputs[0].getClass().isArray())
		{
			TableEntry e = createEntry((Object[]) inputs[0]);
			m_table.add(e);
		}
		outputs[0] = m_table;
		return true;
	}
	
	@Override
	public UpdateTableArray duplicate()
	{
		return new UpdateTableArray(m_table.getColumnNames());
	}
}
