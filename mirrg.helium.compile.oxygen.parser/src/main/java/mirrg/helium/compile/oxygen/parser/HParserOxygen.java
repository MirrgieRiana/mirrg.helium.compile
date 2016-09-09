package mirrg.helium.compile.oxygen.parser;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class HParserOxygen
{

	public static <T> Node<T> parse(ISyntax<T> syntax, String text)
	{
		return syntax.parse(text, 0);
	}

}
