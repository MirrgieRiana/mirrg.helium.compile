package mirrg.helium.compile.oxygen.apatite2.util;

import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.editor.TextPaneOxygen;
import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class TextPaneApatite extends TextPaneOxygen<IApatiteCode>
{

	public TextPaneApatite(Syntax<IApatiteCode> syntax)
	{
		super(syntax);
	}

	public void setUnderline(int offset, int length)
	{
		setAttribute(offset, length, s -> {
			s.addAttribute(StyleConstants.Underline, true);
		});
	}

}
