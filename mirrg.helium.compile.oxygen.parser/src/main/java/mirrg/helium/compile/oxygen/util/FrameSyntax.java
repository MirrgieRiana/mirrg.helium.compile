package mirrg.helium.compile.oxygen.util;

import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.swing.nitrogen.util.HSwing;

public class FrameSyntax extends JFrame
{

	private JTextPane textPane1;
	private JTextPane textPane2;

	public FrameSyntax(Node<?> node, String text)
	{
		setLayout(new CardLayout());
		add(HSwing.createSplitPaneVertical(
			HSwing.createScrollPane(textPane1 = new JTextPane(), 400, 400),
			HSwing.createScrollPane(textPane2 = new JTextPane(), 400, 100)));

		appendText(text, node, 0);

		pack();
		setLocationByPlatform(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	private void appendText(String text, Node<?> node, int indent)
	{
		appendText(textPane1, HString.rept("  ", indent), Color.black);
		appendText(textPane1, "[b=", Color.gray);
		appendText(textPane1, "" + node.begin, Color.magenta);
		appendText(textPane1, ", e=", Color.gray);
		appendText(textPane1, "" + node.end, Color.magenta);
		appendText(textPane1, ", s=", Color.gray);
		{
			if (node.children == null) {
				Color color = null;
				if (node.value instanceof IColoredNode) {
					color = ((IColoredNode) node.value).getColor();
				} else {
					color = Color.black;
				}
				appendText(textPane1, text.substring(node.begin, node.end), color);
				appendText(textPane2, text.substring(node.begin, node.end), color);
			} else {
				appendText(textPane1, text.substring(node.begin, node.end), Color.gray);
			}
		}
		appendText(textPane1, ", v=", Color.gray);
		if (node.value != null) {
			appendText(textPane1, "" + node.value.getClass().getSimpleName(), Color.blue);
		} else {
			appendText(textPane1, "null", Color.gray);
		}
		appendText(textPane1, "]\n", Color.gray);

		if (node.children != null) {
			for (Node<?> child : node.children) {
				appendText(text, child, indent + 1);
			}
		}

	}

	private void appendText(JTextPane textPane, String string, Color color)
	{
		SimpleAttributeSet attr = new SimpleAttributeSet();
		attr.addAttribute(StyleConstants.Foreground, color);
		try {
			textPane.getDocument().insertString(textPane.getDocument().getLength(), string, attr);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
