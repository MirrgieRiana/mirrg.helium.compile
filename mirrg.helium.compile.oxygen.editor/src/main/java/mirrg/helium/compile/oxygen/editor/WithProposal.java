package mirrg.helium.compile.oxygen.editor;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.util.function.Function;
import java.util.stream.Stream;

import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class WithProposal<T> implements IProviderProposal
{

	public final T value;
	public final Function<T, Stream<Proposal>> function;

	public WithProposal(T value, Function<T, Stream<Proposal>> function)
	{
		this.value = value;
		this.function = function;
	}

	@Override
	public Stream<Proposal> getProposals()
	{
		return function.apply(value);
	}

	public static <T> Syntax<T> withProposal(Syntax<T> syntax, Function<T, Stream<Proposal>> function)
	{
		return pack(pack(syntax, s -> new WithProposal<T>(s, function)), s -> s.value);
	}

}
