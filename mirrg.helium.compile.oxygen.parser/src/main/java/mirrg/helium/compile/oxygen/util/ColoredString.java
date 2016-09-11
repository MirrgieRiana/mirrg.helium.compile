package mirrg.helium.compile.oxygen.util;

import java.awt.Color;

public class ColoredString extends Colored<String> implements CharSequence
{

	public ColoredString(String string, Color color)
	{
		super(string, color);
	}

	@Override
	public CharSequence subSequence(int start, int end)
	{
		return get().subSequence(start, end);
	}

	@Override
	public int length()
	{
		return get().length();
	}

	@Override
	public char charAt(int index)
	{
		return get().charAt(index);
	}

}
