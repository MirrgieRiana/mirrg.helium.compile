package mirrg.helium.compile.oxygen.apatite2.node;

import mirrg.helium.compile.oxygen.apatite2.type.IType;

public interface IApatiteScript
{

	public IType<?> getType();

	public Object invoke();

}
