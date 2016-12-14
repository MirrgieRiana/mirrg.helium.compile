package mirrg.helium.compile.oxygen.apatite2.core;

import java.util.Optional;

import mirrg.helium.compile.oxygen.apatite2.type.IType;
import mirrg.helium.standard.hydrogen.struct.Tuple3;

public interface IApatiteFunctionProvider
{

	/**
	 * @return Optional&lt;Tuple3&lt;A, B, IApatiteFunctionEntity&gt;&gt;<br>
	 *         A: (no cast) ? 0 : (the length of arguments) - (the index of
	 *         casted argument)<br>
	 *         B: distance
	 */
	public Optional<Tuple3<Integer, Integer, IApatiteFunctionEntity>> matches(IType<?>... types);

}
