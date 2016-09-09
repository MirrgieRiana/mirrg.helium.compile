package mirrg.helium.compile.oxygen.util;

import java.awt.Color;

public class ColoredString implements CharSequence, IColoredNode
{

	public final String string;
	public final Color color;

	public ColoredString(String string, Color color)
	{
		this.string = string;
		this.color = color;
	}

	@Override
	public CharSequence subSequence(int start, int end)
	{
		return string.subSequence(start, end);
	}

	@Override
	public int length()
	{
		return string.length();
	}

	@Override
	public char charAt(int index)
	{
		return string.charAt(index);
	}

	@Override
	public Color getColor()
	{
		return color;
	}

}
