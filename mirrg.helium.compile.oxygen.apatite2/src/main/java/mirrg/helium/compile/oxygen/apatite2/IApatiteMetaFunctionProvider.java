package mirrg.helium.compile.oxygen.apatite2;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;

public interface IApatiteMetaFunctionProvider
{

	public Optional<IApatiteMetaFunctionEntity> matches(IApatiteCode... codes);

}
