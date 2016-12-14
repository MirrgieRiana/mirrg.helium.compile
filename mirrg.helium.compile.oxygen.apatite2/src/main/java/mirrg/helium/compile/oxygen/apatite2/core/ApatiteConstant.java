package mirrg.helium.compile.oxygen.apatite2.core;

import mirrg.helium.compile.oxygen.apatite2.type.IType;

public class ApatiteConstant<T>
{

	public final IType<T> type;
	public final String name;
	public final T value;

	public ApatiteConstant(IType<T> type, String name, T value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
	}

}
