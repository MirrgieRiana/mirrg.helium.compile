package mirrg.helium.compile.oxygen.apatite2.core;

import mirrg.helium.compile.oxygen.apatite2.type.IType;

public class ApatiteVariable<T>
{

	public final IType<T> type;
	public final String name;
	public T value;

	public ApatiteVariable(IType<T> type, String name)
	{
		this.type = type;
		this.name = name;
	}

	public ApatiteVariable(IType<T> type, String name, T value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
	}

}
