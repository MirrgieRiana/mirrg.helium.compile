package mirrg.helium.compile.oxygen.apatite2.core;

import mirrg.helium.compile.oxygen.apatite2.type.IType;

public interface IApatiteFunctionEntity
{

	public IType<?> getType();

	public Object invoke(Object... arguments);

}
