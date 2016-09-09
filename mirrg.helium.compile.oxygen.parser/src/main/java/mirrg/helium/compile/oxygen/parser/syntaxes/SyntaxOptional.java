package mirrg.helium.compile.oxygen.parser.syntaxes;

import java.util.Optional;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class SyntaxOptional<T> implements ISyntax<Optional<T>>
{

	public final ISyntax<T> syntax;

	public SyntaxOptional(ISyntax<T> syntax)
	{
		this.syntax = syntax;
	}

	@Override
	public Node<Optional<T>> parse(String text, int index)
	{
		Node<T> node = syntax.parse(text, index);
		if (node == null) return new Node<>(this, null, index, index, Optional.empty());
		return new Node<>(this, node.children, node.begin, node.end, Optional.of(node.value));
	}

}
