package mirrg.helium.compile.oxygen.parser.syntaxes;

import java.util.ArrayList;
import java.util.function.Function;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class SyntaxPack<I, O> implements ISyntax<O>
{

	public final ISyntax<I> syntax;
	public final Function<I, O> function;

	public SyntaxPack(ISyntax<I> syntax, Function<I, O> function)
	{
		this.syntax = syntax;
		this.function = function;
	}

	@Override
	public Node<O> parse(String text, int index)
	{
		Node<I> node = syntax.parse(text, index);
		if (node == null) return null;

		ArrayList<Node<?>> children = new ArrayList<>();
		children.add(node);

		return new Node<>(this, children, node.begin, node.end, function.apply(node.value));
	}

}
