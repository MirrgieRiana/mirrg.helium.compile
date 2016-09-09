package mirrg.helium.compile.oxygen.parser.syntaxes;

import java.util.function.Function;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class SyntaxMap<I, O> implements ISyntax<O>
{

	public final ISyntax<I> syntax;
	public final Function<I, O> function;

	public SyntaxMap(ISyntax<I> syntax, Function<I, O> function)
	{
		this.syntax = syntax;
		this.function = function;
	}

	@Override
	public Node<O> parse(String text, int index)
	{
		Node<I> node = syntax.parse(text, index);
		if (node == null) return null;
		return new Node<>(this, node.children, node.begin, node.end, function.apply(node.value));
	}
}
