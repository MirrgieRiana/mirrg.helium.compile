package mirrg.helium.compile.oxygen.parser.test2;

import java.awt.Color;

public class EnumType<T>
{

	public String name;
	public Color color;

	public EnumType(String name, Color color)
	{
		this.name = name;
		this.color = color;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public boolean isAssignableFrom(EnumType<?> other)
	{
		if (this.equals(VM.DOUBLE) && other.equals(VM.INTEGER)) return true;
		if (this.equals(VM.OBJECT) && other.equals(VM.STRING)) return true;
		if (this.equals(VM.OBJECT) && other.equals(VM.INTEGER)) return true;
		if (this.equals(VM.OBJECT) && other.equals(VM.DOUBLE)) return true;
		if (this.equals(VM.OBJECT) && other.equals(VM.BOOLEAN)) return true;
		if (this.equals(VM.OBJECT) && other.equals(VM.COMPLEX)) return true;
		return this.equals(other);
	}

	public static Object cast(EnumType<?> from, EnumType<?> to, Object value)
	{
		if (from.equals(VM.INTEGER) && to.equals(VM.DOUBLE)) return (double) (Integer) value;
		return value;
	}

}
