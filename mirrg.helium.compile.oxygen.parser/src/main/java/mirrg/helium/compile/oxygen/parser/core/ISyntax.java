package mirrg.helium.compile.oxygen.parser.core;

public interface ISyntax<T>
{

	public Node<T> parse(String text, int index);

	public default Node<T> parse(String text)
	{
		Node<T> node = parse(text, 0);
		if (node.end == text.length()) return node;
		return null;
	}

}
