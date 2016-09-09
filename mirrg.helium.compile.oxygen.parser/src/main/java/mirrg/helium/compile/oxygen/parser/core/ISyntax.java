package mirrg.helium.compile.oxygen.parser.core;

public interface ISyntax<T>
{

	public Node<T> parse(String text, int index);

}
