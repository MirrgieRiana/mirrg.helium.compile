package mirrg.helium.compile.oxygen.parser.syntaxes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class SyntaxRegex implements ISyntax<String>
{

	public final Pattern pattern;

	public SyntaxRegex(String regex)
	{
		this.pattern = Pattern.compile(regex);
	}

	@Override
	public Node<String> parse(String text, int index)
	{
		Matcher matcher = pattern.matcher(text);
		if (matcher.find(index)) {
			if (matcher.start() == index) {
				return new Node<>(this, null, index, matcher.end(), matcher.group());
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
