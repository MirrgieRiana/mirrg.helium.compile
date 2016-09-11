package mirrg.helium.compile.oxygen.util;

import java.awt.CardLayout;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.standard.hydrogen.event.EventManager;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.swing.nitrogen.util.HSwing;

public class FrameSyntax extends JFrame
{

	/**
	 *
	 */
	private static final long serialVersionUID = -59694329365843532L;
	private ISyntax<?> syntax;
	private JTextPane textPane1;
	private JTextPane textPane2;

	private Thread threadRecolor;
	private boolean occurEvent = true;
	public final EventManager<String> eventManager = new EventManager<>();

	public FrameSyntax(ISyntax<?> syntax, String text)
	{
		this.syntax = syntax;

		setLayout(new CardLayout());
		add(HSwing.createSplitPaneVertical(
			HSwing.createScrollPane(HSwing.get(() -> {
				textPane1 = new JTextPane();
				textPane1.setEditable(false);
				return textPane1;
			}), 600, 400),
			HSwing.createScrollPane(textPane2 = new JTextPane(), 600, 100)));

		set(text);

		HSwing.hookChange(textPane2, e -> {
			if (occurEvent) onUserEdit();
		});

		pack();
		setLocationByPlatform(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	private void onUserEdit()
	{
		if (threadRecolor != null) threadRecolor.interrupt();
		threadRecolor = new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				return;
			}
			SwingUtilities.invokeLater(() -> {
				int caretPosition = textPane2.getCaretPosition();
				eventManager.post(textPane2.getText());
				set(textPane2.getText());
				textPane2.setCaretPosition(caretPosition);
			});
		});
		threadRecolor.start();
	}

	private void set(String text)
	{
		Node<?> node;
		try {
			node = syntax.parse(text);
		} catch (Exception e) {
			ArrayList<Byte> bytes = new ArrayList<>();
			try {
				e.printStackTrace(new PrintStream(new OutputStream() {

					@Override
					public void write(int b) throws IOException
					{
						bytes.add((byte) b);
					}

				}, true, "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			byte[] bytes2 = new byte[bytes.size()];
			for (int i = 0; i < bytes.size(); i++) {
				bytes2[i] = bytes.get(i);
			}
			resetText(textPane1);
			appendText(textPane1, new String(bytes2), Color.red);
			return;
		}
		if (node != null) {
			resetText(textPane1);
			resetText(textPane2);
			appendText(text, node, 0);
		} else {
			resetText(textPane1);
			resetText(textPane2);
			appendText(textPane2, text, Color.black);
		}
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

	private void resetText(JTextPane textPane)
	{
		occurEvent = false;
		textPane.setText("");
		occurEvent = true;
	}

	private void appendText(JTextPane textPane, String string, Color color)
	{
		SimpleAttributeSet attr = new SimpleAttributeSet();
		attr.addAttribute(StyleConstants.Foreground, color);
		occurEvent = false;
		try {
			textPane.getDocument().insertString(textPane.getDocument().getLength(), string, attr);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		occurEvent = true;
	}

}
