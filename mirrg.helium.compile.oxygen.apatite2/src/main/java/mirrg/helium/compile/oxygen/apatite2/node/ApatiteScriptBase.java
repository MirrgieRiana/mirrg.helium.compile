package mirrg.helium.compile.oxygen.apatite2.node;

import mirrg.helium.compile.oxygen.apatite2.type.IType;

public abstract class ApatiteScriptBase implements IApatiteScript
{

	private IType<?> type;

	public ApatiteScriptBase(IType<?> type)
	{
		this.type = type;
	}

	@Override
	public IType<?> getType()
	{
		return type;
	}

}
