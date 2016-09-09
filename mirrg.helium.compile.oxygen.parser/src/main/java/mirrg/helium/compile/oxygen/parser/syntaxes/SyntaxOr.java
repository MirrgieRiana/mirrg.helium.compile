package mirrg.helium.compile.oxygen.parser.syntaxes;

import java.util.ArrayList;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class SyntaxOr<T> implements ISyntax<T>
{

	public final ArrayList<ISyntax<T>> syntaxex = new ArrayList<>();

	public SyntaxOr<T> or(ISyntax<T> syntax)
	{
		syntaxex.add(syntax);
		return this;
	}

	@Override
	public Node<T> parse(String text, int index)
	{
		for (ISyntax<T> syntax : syntaxex) {
			Node<T> node = syntax.parse(text, index);
			if (node != null) return node;
		}
		return null;
	}

}
