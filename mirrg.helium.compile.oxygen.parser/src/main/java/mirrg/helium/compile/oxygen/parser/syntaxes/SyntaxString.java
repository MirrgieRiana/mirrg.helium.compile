package mirrg.helium.compile.oxygen.parser.syntaxes;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class SyntaxString implements ISyntax<String>
{

	public final String string;

	public SyntaxString(String string)
	{
		this.string = string;
	}

	@Override
	public Node<String> parse(String text, int index)
	{
		if (index + string.length() > text.length()) return null;
		for (int i = 0; i < string.length(); i++) {
			if (text.charAt(index + i) != string.charAt(i)) return null;
		}
		return new Node<>(this, null, index, index + string.length(), string);
	}

}
