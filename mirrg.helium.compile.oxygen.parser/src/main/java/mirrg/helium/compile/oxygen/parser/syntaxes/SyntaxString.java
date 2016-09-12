package mirrg.helium.compile.oxygen.parser.syntaxes;

import mirrg.helium.compile.oxygen.parser.core.Memo;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class SyntaxString extends Syntax<String>
{

	public final String string;

	public SyntaxString(String string)
	{
		this.string = string;
	}

	@Override
	protected Node<String> parseImpl(Memo memo, String text, int index)
	{
		if (index + string.length() > text.length()) return null;
		for (int i = 0; i < string.length(); i++) {
			if (text.charAt(index + i) != string.charAt(i)) return null;
		}
		return new Node<>(this, null, index, index + string.length(), string);
	}

}
