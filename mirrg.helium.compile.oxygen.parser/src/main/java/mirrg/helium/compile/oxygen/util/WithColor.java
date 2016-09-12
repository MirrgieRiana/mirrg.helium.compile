package mirrg.helium.compile.oxygen.util;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.awt.Color;
import java.util.function.Function;

import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class WithColor<T> implements IProviderColor
{

	public final T value;
	public final Color supplier;

	public WithColor(T value, Color supplier)
	{
		this.value = value;
		this.supplier = supplier;
	}

	public T get()
	{
		return value;
	}

	@Override
	public Color getColor()
	{
		return supplier;
	}

	public static <T> Syntax<T> withColor(Syntax<T> syntax, Function<T, Color> function)
	{
		return pack(pack(syntax, s -> new WithColor<T>(s, function.apply(s))), s -> s.value);
	}

}
