package mirrg.helium.compile.oxygen.util.apatite;

import java.awt.Color;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Type
{

	public final BaseType baseType;
	public final Type[] arguments;
	public final String string;

	public Type(BaseType baseType, Type[] arguments)
	{
		this.baseType = baseType;
		this.arguments = arguments;
		string = baseType.toString() + "(" + String.join(", ", Stream.of(arguments)
			.map(Type::toString)
			.collect(Collectors.joining(", "))) + ")";
	}

	@Override
	public String toString()
	{
		return string;
	}

	public boolean isAssignableFrom(Type other)
	{
		while (true) {
			if (this.equals(other)) return true;
			other = other.getParent();
			if (other == null) return false;
		}
	}

	public Type getParent()
	{
		if (baseType.functionParent == null) return null;
		return baseType.functionParent.apply(this);
	}

	public Object castToParent(Object value)
	{
		if (baseType.caster != null) {
			return baseType.caster.apply(value);
		} else {
			return value;
		}
	}

	public Color getColor()
	{
		return baseType.functionColor.apply(this);
	}

	public static Object cast(Type typeSuper, Type typeSub, Object value)
	{
		while (true) {
			if (typeSuper.equals(typeSub)) return value;
			value = typeSub.castToParent(value);
			typeSub = typeSub.getParent();
			if (typeSub == null) throw new ClassCastException("" + typeSub + "(" + value + ") is not " + typeSuper);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((string == null) ? 0 : string.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Type other = (Type) obj;
		if (string == null) {
			if (other.string != null) return false;
		} else if (!string.equals(other.string)) return false;
		return true;
	}

}
