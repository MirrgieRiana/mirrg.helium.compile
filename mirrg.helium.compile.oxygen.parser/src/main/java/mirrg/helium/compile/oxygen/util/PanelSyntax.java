package mirrg.helium.compile.oxygen.util;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Panel;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.standard.hydrogen.event.EventManager;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.swing.nitrogen.wrapper.artifacts.logging.HLog;

public class PanelSyntax extends Panel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2664189373288181817L;
	private Syntax<?> syntax;
	private JTextPane textPane1;
	private JTextPane textPane2;

	private Thread threadRecolor;
	private boolean occurEvent = true;
	public final EventManager<String> eventManager = new EventManager<>();

	public PanelSyntax(Syntax<?> syntax, String text)
	{
		this.syntax = syntax;

		setLayout(new CardLayout());
		add(createSplitPaneVertical(
			createScrollPane(get(() -> {
				textPane1 = new JTextPane();
				textPane1.setEditable(false);
				return textPane1;
			}), 600, 400),
			createScrollPane(textPane2 = new JTextPane(), 600, 100)));

		occurEvent = false;
		set(text);
		occurEvent = true;

		hookChange(textPane2, e -> {
			if (occurEvent) onUserEdit();
		});
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

				occurEvent = false;
				update();
				occurEvent = true;

				textPane2.setCaretPosition(caretPosition);
			});
		});
		threadRecolor.start();
	}

	private void set(String text)
	{
		textPane2.setText(text);
		update();
	}

	private void update()
	{
		String text = textPane2.getText();

		Node<?> node;
		try {
			node = syntax.parse(text);
		} catch (Exception e) {
			textPane1.setText("");
			appendText(textPane1, toString(e), Color.red);
			return;
		}

		if (node != null) {
			textPane1.setText("");
			appendText(textPane1, text, node, 0);

			SimpleAttributeSet attr = new SimpleAttributeSet();
			attr.addAttribute(StyleConstants.Foreground, Color.black);
			((DefaultStyledDocument) textPane2.getDocument()).setCharacterAttributes(
				0, textPane2.getDocument().getLength(), attr, false);

			updateText(textPane2, node);
		} else {
			textPane1.setText("");
		}
	}

	private String toString(Exception e)
	{
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
		String string = new String(bytes2);
		return string;
	}

	private void appendText(JTextPane textPane, String text, Node<?> node, int indent)
	{
		appendText(textPane, HString.rept("  ", indent), Color.black);
		appendText(textPane, "[b=", Color.gray);
		appendText(textPane, "" + node.begin, Color.magenta);
		appendText(textPane, ", e=", Color.gray);
		appendText(textPane, "" + node.end, Color.magenta);
		appendText(textPane, ", s=", Color.gray);
		{
			if (node.children == null) {
				Color color = null;
				if (node.value instanceof IProviderColor) {
					color = ((IProviderColor) node.value).getColor();
				} else {
					color = Color.black;
				}
				appendText(textPane, text.substring(node.begin, node.end), color);
			} else {
				appendText(textPane, text.substring(node.begin, node.end), Color.gray);
			}
		}
		appendText(textPane, ", v=", Color.gray);
		if (node.value != null) {
			appendText(textPane, "" + node.value.getClass().getSimpleName(), Color.blue);
		} else {
			appendText(textPane, "null", Color.gray);
		}
		appendText(textPane, "]\n", Color.gray);

		if (node.children != null) {
			for (Node<?> child : node.children) {
				appendText(textPane, text, child, indent + 1);
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

	private void updateText(JTextPane textPane, Node<?> node)
	{
		if (node.value instanceof IProviderColor) {
			SimpleAttributeSet attr = new SimpleAttributeSet();
			attr.addAttribute(StyleConstants.Foreground, ((IProviderColor) node.value).getColor());
			((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(
				node.begin, node.end - node.begin, attr, false);
		}

		if (node.children != null) {
			for (Node<?> child : node.children) {
				updateText(textPane, child);
			}
		}

	}

}
