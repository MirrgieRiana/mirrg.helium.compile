package mirrg.helium.compile.oxygen.parser.syntaxes;

import java.util.ArrayList;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class SyntaxRepeat<T> implements ISyntax<ArrayList<T>>
{

	public final ISyntax<T> syntax;
	public final int min;
	public final int max;

	public SyntaxRepeat(ISyntax<T> syntax, int min, int max)
	{
		this.syntax = syntax;
		this.min = min;
		this.max = max;
	}

	@Override
	public Node<ArrayList<T>> parse(String text, int index)
	{
		ArrayList<Node<?>> children = new ArrayList<>();
		ArrayList<T> value = new ArrayList<>();
		int begin = index;
		int end = begin;

		while (true) {
			Node<T> node = syntax.parse(text, index);
			if (node == null) break;
			children.add(node);
			value.add(node.value);
			index += node.end - node.begin;
			end = node.end;
		}

		return new Node<>(this, children, begin, end, value);
	}

}
