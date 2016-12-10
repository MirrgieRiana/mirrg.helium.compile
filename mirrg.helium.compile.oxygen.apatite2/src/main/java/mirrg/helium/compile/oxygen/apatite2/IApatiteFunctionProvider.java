package mirrg.helium.compile.oxygen.apatite2;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.type.Type;
import mirrg.helium.standard.hydrogen.struct.Struct3;

public interface IApatiteFunctionProvider
{

	public String getName();

	public Optional<Struct3<Integer, Integer, IApatiteFunctionEntity>> matches(Type<?>... types);

}
