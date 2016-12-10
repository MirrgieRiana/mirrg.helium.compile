package mirrg.helium.compile.oxygen.apatite2;

import mirrg.helium.compile.oxygen.apatite2.type.Type;

public interface IApatiteFunctionEntity
{

	public Type<?> getType();

	public Object invoke(Object... arguments);

}
