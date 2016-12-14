package mirrg.helium.compile.oxygen.apatite2.core;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;

public interface IApatiteMetaFunctionProvider
{

	public Optional<IApatiteMetaFunctionEntity> matches(IApatiteCode... codes);

}
