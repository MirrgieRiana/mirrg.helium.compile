package mirrg.helium.compile.oxygen.parser.core;

public abstract class Syntax<T>
{

	public Node<T> parse(Memo memo, String text, int index)
	{
		return memo.get(this, index, () -> parseImpl(memo, text, index));
	}

	protected abstract Node<T> parseImpl(Memo memo, String text, int index);

	public Node<T> parse(String text)
	{
		Node<T> node = parse(new Memo(), text, 0);
		if (node == null) return null;
		if (node.end == text.length()) return node;
		return null;
	}

}
