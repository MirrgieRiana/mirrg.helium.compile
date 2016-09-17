package mirrg.helium.compile.oxygen.parser.syntaxes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mirrg.helium.compile.oxygen.parser.core.Memo;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class SyntaxRegex extends Syntax<String>
{

	public final Pattern pattern;

	public SyntaxRegex(String regex)
	{
		this.pattern = Pattern.compile(regex);
	}

	@Override
	protected Node<String> parseImpl(Memo memo, boolean shouldTokenProposal, String text, int index)
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

	@Override
	public String getName()
	{
		String name = super.getName();
		if (name != null) return name;
		return pattern.pattern();
	}

}
