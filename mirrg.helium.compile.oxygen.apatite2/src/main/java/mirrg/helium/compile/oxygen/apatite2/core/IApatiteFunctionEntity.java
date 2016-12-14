package mirrg.helium.compile.oxygen.apatite2.core;

import mirrg.helium.compile.oxygen.apatite2.type.Type;

public interface IApatiteFunctionEntity
{

	public Type<?> getType();

	public Object invoke(Object... arguments);

}
