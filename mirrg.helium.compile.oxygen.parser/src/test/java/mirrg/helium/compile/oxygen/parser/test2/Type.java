package mirrg.helium.compile.oxygen.parser.test2;

import java.awt.Color;

public class Type<T>
{

	public String name;
	public Color color;

	public Type(String name, Color color)
	{
		this.name = name;
		this.color = color;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public boolean isAssignableFrom(Type<?> other)
	{
		if (this.equals(VMTest2.DOUBLE) && other.equals(VMTest2.INTEGER)) return true;
		if (this.equals(VMTest2.OBJECT) && other.equals(VMTest2.STRING)) return true;
		if (this.equals(VMTest2.OBJECT) && other.equals(VMTest2.INTEGER)) return true;
		if (this.equals(VMTest2.OBJECT) && other.equals(VMTest2.DOUBLE)) return true;
		if (this.equals(VMTest2.OBJECT) && other.equals(VMTest2.BOOLEAN)) return true;
		if (this.equals(VMTest2.OBJECT) && other.equals(VMTest2.COMPLEX)) return true;
		return this.equals(other);
	}

	public static Object cast(Type<?> from, Type<?> to, Object value)
	{
		if (from.equals(VMTest2.INTEGER) && to.equals(VMTest2.DOUBLE)) return (double) (Integer) value;
		return value;
	}

}
