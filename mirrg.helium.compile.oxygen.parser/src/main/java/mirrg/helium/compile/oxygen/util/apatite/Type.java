package mirrg.helium.compile.oxygen.util.apatite;

import java.awt.Color;
import java.util.function.Function;

public class Type<T>
{

	public String name;
	public Color color;
	public Type<?> parent;
	public Function<?, ?> caster;

	public Type(String name, Color color)
	{
		this(name, color, null, null);
	}

	public Type(String name, Color color, Type<?> parent)
	{
		this(name, color, parent, null);
	}

	public <P> Type(String name, Color color, Type<P> parent, Function<T, P> caster)
	{
		this.name = name;
		this.color = color;
		this.parent = parent;
		this.caster = caster;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public boolean isAssignableFrom(Type<?> other)
	{
		while (true) {
			if (this.equals(other)) return true;
			other = other.parent;
			if (other == null) return false;
		}
	}

	public Object castToParent(Object value)
	{
		if (caster != null) {
			return ((Function) caster).apply(value);
		} else {
			return value;
		}
	}

	public static Object cast(Type<?> typeSuper, Type<?> typeSub, Object value)
	{
		while (true) {
			if (typeSuper.equals(typeSub)) return value;
			value = typeSub.castToParent(value);
			typeSub = typeSub.parent;
			if (typeSub == null) throw new ClassCastException("" + typeSub + "(" + value + ") is not " + typeSuper);
		}
	}

}
