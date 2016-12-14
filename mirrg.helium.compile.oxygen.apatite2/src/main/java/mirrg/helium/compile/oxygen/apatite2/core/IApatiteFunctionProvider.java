package mirrg.helium.compile.oxygen.apatite2.core;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.type.IType;
import mirrg.helium.standard.hydrogen.struct.Struct3;

public interface IApatiteFunctionProvider
{

	public Optional<Struct3<Integer, Integer, IApatiteFunctionEntity>> matches(IType<?>... types);

}
