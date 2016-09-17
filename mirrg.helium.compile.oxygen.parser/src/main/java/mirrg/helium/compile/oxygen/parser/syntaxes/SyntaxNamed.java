package mirrg.helium.compile.oxygen.parser.syntaxes;

import mirrg.helium.compile.oxygen.parser.core.Memo;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class SyntaxNamed<T> extends Syntax<T>
{

	public final Syntax<T> syntax;
	public final String name;

	public SyntaxNamed(Syntax<T> syntax, String name)
	{
		this.syntax = syntax;
		this.name = name;
	}

	@Override
	protected Node<T> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index)
	{
		return syntax.parse(memo, shouldTokenProposal, text, index);
	}

	@Override
	public String getName()
	{
		return name;
	}

}
