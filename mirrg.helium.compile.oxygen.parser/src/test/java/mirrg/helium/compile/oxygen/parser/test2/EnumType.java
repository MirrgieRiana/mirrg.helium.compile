package mirrg.helium.compile.oxygen.parser.test2;

import java.awt.Color;

import org.apache.commons.math3.complex.Complex;

public class EnumType<T>
{

	public static final EnumType<Object> OBJECT = new EnumType<>("OBJECT", Color.decode("#545454"));
	public static final EnumType<String> STRING = new EnumType<>("STRING", Color.decode("#a64eaa"));
	public static final EnumType<Integer> INTEGER = new EnumType<>("INTEGER", Color.decode("#424aaf"));
	public static final EnumType<Double> DOUBLE = new EnumType<>("DOUBLE", Color.decode("#53a540"));
	public static final EnumType<Boolean> BOOLEAN = new EnumType<>("BOOLEAN", Color.decode("#878334"));
	public static final EnumType<Complex> COMPLEX = new EnumType<>("COMPLEX", Color.decode("#960002"));

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
		if (this.equals(DOUBLE) && other.equals(INTEGER)) return true;
		if (this.equals(OBJECT) && other.equals(STRING)) return true;
		if (this.equals(OBJECT) && other.equals(INTEGER)) return true;
		if (this.equals(OBJECT) && other.equals(DOUBLE)) return true;
		if (this.equals(OBJECT) && other.equals(BOOLEAN)) return true;
		if (this.equals(OBJECT) && other.equals(COMPLEX)) return true;
		return this.equals(other);
	}

	public static <I, O> O cast(EnumType<I> from, EnumType<O> to, I value)
	{
		if (from.equals(INTEGER) && to.equals(DOUBLE)) return (O) (Double) (double) (Integer) value;
		return (O) value;
	}

}
