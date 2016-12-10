package mirrg.helium.compile.oxygen.apatite2;

import mirrg.helium.compile.oxygen.apatite2.type.Type;

public class ApatiteConstant<T>
{

	public final Type<T> type;
	public final String name;
	public final T value;

	public ApatiteConstant(Type<T> type, String name, T value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
	}

}
