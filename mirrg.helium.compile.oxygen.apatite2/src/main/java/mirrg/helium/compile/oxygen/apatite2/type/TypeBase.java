package mirrg.helium.compile.oxygen.apatite2.type;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Optional;
import java.util.function.Function;

import mirrg.helium.standard.hydrogen.struct.Tuple;

public abstract class TypeBase<T> implements IType<T>
{

	public TypeBase(String name, Color color)
	{
		this.name = name;
		this.color = color;
	}

	//

	private final String name;

	@Override
	public String getName()
	{
		return name;
	}

	//

	private final Color color;

	@Override
	public Color getColor()
	{
		return color;
	}

	//

	private Hashtable<IType<?>, Tuple<Integer, Function<?, ?>>> distanceTable = new Hashtable<>();

	@Override
	public Optional<Tuple<Integer, Function<?, ?>>> getDistance(IType<?> parent)
	{
		if (parent.equals(this)) return Optional.of(new Tuple<>(0, null));
		if (!distanceTable.containsKey(parent)) return Optional.empty();
		return Optional.of(distanceTable.get(parent));
	}

	protected <U> void registerDistance(IType<U> parent, int distance, Function<T, U> caster)
	{
		distanceTable.put(parent, new Tuple<>(distance, caster));
	}

	protected <U> void registerDistance(IType<U> parent, int distance)
	{
		distanceTable.put(parent, new Tuple<>(distance, null));
	}

	//

	@Override
	public boolean equals(IType<?> type)
	{
		return getName().equals(type.getName());
	}

	@Override
	public String toString()
	{
		return "Type:" + getName();
	}

}
