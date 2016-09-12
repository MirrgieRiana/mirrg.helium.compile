package mirrg.helium.compile.oxygen.parser.syntaxes;

import java.util.ArrayList;

import mirrg.helium.compile.oxygen.parser.core.Memo;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class SyntaxOr<T> extends Syntax<T>
{

	public final ArrayList<Syntax<T>> syntaxex = new ArrayList<>();

	public SyntaxOr<T> or(Syntax<T> syntax)
	{
		syntaxex.add(syntax);
		return this;
	}

	@Override
	protected Node<T> parseImpl(Memo memo, String text, int index)
	{
		for (Syntax<T> syntax : syntaxex) {
			Node<T> node = syntax.parse(memo, text, index);
			if (node != null) return node;
		}
		return null;
	}

}
