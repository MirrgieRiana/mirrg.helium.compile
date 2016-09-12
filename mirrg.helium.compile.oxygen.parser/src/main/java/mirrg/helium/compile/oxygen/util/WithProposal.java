package mirrg.helium.compile.oxygen.util;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.util.function.Function;
import java.util.stream.Stream;

import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class WithProposal<T> implements IProviderProposal
{

	public final T value;
	public final Stream<String> supplier;

	public WithProposal(T value, Stream<String> supplier)
	{
		this.value = value;
		this.supplier = supplier;
	}

	@Override
	public Stream<String> getProposals()
	{
		return supplier;
	}

	public static <T> Syntax<T> withProposal(Syntax<T> syntax, Function<T, Stream<String>> function)
	{
		return pack(pack(syntax, s -> new WithProposal<T>(s, function.apply(s))), s -> s.value);
	}

}
