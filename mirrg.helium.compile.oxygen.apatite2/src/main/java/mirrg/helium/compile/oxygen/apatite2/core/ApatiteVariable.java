package mirrg.helium.compile.oxygen.apatite2.core;

import mirrg.helium.compile.oxygen.apatite2.type.Type;

public class ApatiteVariable<T>
{

	public final Type<T> type;
	public final String name;
	public T value;

	public ApatiteVariable(Type<T> type, String name)
	{
		this.type = type;
		this.name = name;
	}

	public ApatiteVariable(Type<T> type, String name, T value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
	}

}
