package mirrg.helium.compile.oxygen.parser.syntaxes;

import mirrg.helium.compile.oxygen.parser.core.Memo;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class SyntaxSlot<T> extends Syntax<T>
{

	public Syntax<T> syntax;

	@Override
	protected Node<T> parseImpl(Memo memo, String text, int index)
	{
		return syntax.parse(memo, text, index);
	}

	public void setSyntax(Syntax<T> syntax)
	{
		this.syntax = syntax;
	}

}
