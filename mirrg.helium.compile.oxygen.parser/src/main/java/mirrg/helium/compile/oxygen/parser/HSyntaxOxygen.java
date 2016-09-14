package mirrg.helium.compile.oxygen.parser;

import java.util.function.Function;
import java.util.function.Supplier;

import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxExtract;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxMap;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxOptional;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxOr;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxPack;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxRegex;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxRepeat;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSerial;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSlot;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxString;

public class HSyntaxOxygen
{

	public static SyntaxRegex regex(String regex)
	{
		return new SyntaxRegex(regex);
	}

	public static SyntaxString string(String string)
	{
		return new SyntaxString(string);
	}

	public static <T> SyntaxSerial<T> serial(Supplier<T> supplier)
	{
		return new SyntaxSerial<>(supplier);
	}

	public static <T> SyntaxRepeat<T> repeat(Syntax<T> syntax)
	{
		return repeat(syntax, 0, Integer.MAX_VALUE);
	}

	public static <T> SyntaxRepeat<T> repeat1(Syntax<T> syntax)
	{
		return repeat(syntax, 1, Integer.MAX_VALUE);
	}

	public static <T> SyntaxRepeat<T> repeat(Syntax<T> syntax, int min, int max)
	{
		return new SyntaxRepeat<>(syntax, min, max);
	}

	public static <T> SyntaxOr<T> or(T dummy)
	{
		return new SyntaxOr<>();
	}

	public static <T> SyntaxOptional<T> optional(Syntax<T> syntax)
	{
		return new SyntaxOptional<>(syntax);
	}

	@Deprecated
	public static <I, O> SyntaxMap<I, O> map(Syntax<I> syntax, Function<I, O> function)
	{
		return new SyntaxMap<>(syntax, n -> function.apply(n.value));
	}

	@Deprecated
	public static <I, O> SyntaxMap<I, O> mapNode(Syntax<I> syntax, Function<Node<I>, O> function)
	{
		return new SyntaxMap<>(syntax, function);
	}

	public static <I, O> SyntaxPack<I, O> pack(Syntax<I> syntax, Function<I, O> function)
	{
		return new SyntaxPack<>(syntax, n -> function.apply(n.value));
	}

	public static <I, O> SyntaxPack<I, O> packNode(Syntax<I> syntax, Function<Node<I>, O> function)
	{
		return new SyntaxPack<>(syntax, function);
	}

	public static <I extends O, O> Syntax<O> wrap(Syntax<I> syntax)
	{
		return map(syntax, i -> i);
	}

	public static <T> SyntaxSlot<T> slot()
	{
		return new SyntaxSlot<>();
	}

	public static <T> SyntaxSlot<T> slot(Supplier<Syntax<T>> supplier)
	{
		return new SyntaxSlot<>(supplier);
	}

	public static <T> SyntaxExtract<T> extract(T dummy)
	{
		return new SyntaxExtract<>();
	}

}
