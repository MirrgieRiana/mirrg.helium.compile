package mirrg.helium.compile.oxygen.apatite2;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;

public interface IApatiteMetaFunctionEntity
{

	public Optional<IApatiteScript> validate(int begin, int end, ApatiteVM vm);

}
