package mirrg.helium.compile.oxygen.editor;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.awt.Color;
import java.util.function.Function;

import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class WithColor<T> implements IProviderColor
{

	public final T value;
	public final Function<T, Color> function;

	public WithColor(T value, Function<T, Color> function)
	{
		this.value = value;
		this.function = function;
	}

	public T get()
	{
		return value;
	}

	@Override
	public Color getColor()
	{
		return function.apply(value);
	}

	public static <T> Syntax<T> withColor(Syntax<T> syntax, Function<T, Color> function)
	{
		return pack(pack(syntax, s -> new WithColor<T>(s, function)), s -> s.value);
	}

}
