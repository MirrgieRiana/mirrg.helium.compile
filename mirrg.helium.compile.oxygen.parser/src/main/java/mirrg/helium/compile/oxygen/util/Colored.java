package mirrg.helium.compile.oxygen.util;

import java.awt.Color;

public class Colored<T> implements IColoredNode
{

	public final T t;
	public final Color color;

	public Colored(T t, Color color)
	{
		this.t = t;
		this.color = color;
	}

	public T get()
	{
		return t;
	}

	@Override
	public Color getColor()
	{
		return color;
	}

}
