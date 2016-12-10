package mirrg.helium.compile.oxygen.apatite2.node;

import java.util.function.Supplier;

import mirrg.helium.compile.oxygen.apatite2.type.Type;

public class ApatiteScriptBase implements IApatiteScript
{

	private Type<?> type;
	private Supplier<Object> supplier;

	public ApatiteScriptBase(Type<?> type, Supplier<Object> supplier)
	{
		this.type = type;
		this.supplier = supplier;
	}

	@Override
	public Type<?> getType()
	{
		return type;
	}

	@Override
	public Object invoke()
	{
		return supplier.get();
	}

}
