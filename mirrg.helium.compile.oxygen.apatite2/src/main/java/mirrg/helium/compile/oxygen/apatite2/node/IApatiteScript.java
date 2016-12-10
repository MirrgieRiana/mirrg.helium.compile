package mirrg.helium.compile.oxygen.apatite2.node;

import mirrg.helium.compile.oxygen.apatite2.type.Type;

public interface IApatiteScript
{

	public Type<?> getType();

	public Object invoke();

}
