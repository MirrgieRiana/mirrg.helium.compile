package mirrg.helium.compile.oxygen.apatite2.node;

import mirrg.helium.compile.oxygen.apatite2.type.Type;

public abstract class ApatiteScriptBase implements IApatiteScript
{

	private Type<?> type;

	public ApatiteScriptBase(Type<?> type)
	{
		this.type = type;
	}

	@Override
	public Type<?> getType()
	{
		return type;
	}

}
