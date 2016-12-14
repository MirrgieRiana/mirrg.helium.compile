package mirrg.helium.compile.oxygen.apatite2.type;

import java.awt.Color;
import java.util.Optional;
import java.util.function.Function;

import mirrg.helium.standard.hydrogen.struct.Tuple;

public interface IType<T>
{

	public String getName();

	public Color getColor();

	public boolean equals(IType<?> type);

	public Optional<Tuple<Integer, Function<?, ?>>> getDistance(IType<?> parent);

}
