package mirrg.helium.compile.oxygen.apatite2.type;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Optional;
import java.util.function.Function;

import mirrg.helium.standard.hydrogen.struct.Tuple;

public class Type<T>
{

	public final Color color;
	private Hashtable<Type<?>, Tuple<Integer, Function<?, ?>>> distanceTable = new Hashtable<>();

	public Type(String name, Color color)
	{
		this.name = name;
		this.color = color;
	}

	public Optional<Tuple<Integer, Function<?, ?>>> getDistance(Type<?> parent)
	{
		if (parent.equals(this)) return Optional.of(new Tuple<>(0, null));
		if (!distanceTable.containsKey(parent)) return Optional.empty();
		return Optional.of(distanceTable.get(parent));
	}

	public <U> void registerDistance(Type<U> parent, int distance, Function<T, U> caster)
	{
		distanceTable.put(parent, new Tuple<>(distance, caster));
	}

	public Color getColor()
	{
		return color;
	}

	@Override
	public String toString()
	{
		return "Type[" + getName() + "]";
	}

	public boolean equals(Type<?> type)
	{
		return getName().equals(type.getName());
	}

	//

	private final String name;

	public String getName()
	{
		return name;
	}

}
