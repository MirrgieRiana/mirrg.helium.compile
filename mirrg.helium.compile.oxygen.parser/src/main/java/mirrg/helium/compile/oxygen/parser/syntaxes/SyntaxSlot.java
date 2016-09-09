package mirrg.helium.compile.oxygen.parser.syntaxes;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class SyntaxSlot<T> implements ISyntax<T>
{

	public ISyntax<T> syntax;

	@Override
	public Node<T> parse(String text, int index)
	{
		return syntax.parse(text, index);
	}

	public void setSyntax(ISyntax<T> syntax)
	{
		this.syntax = syntax;
	}

}
