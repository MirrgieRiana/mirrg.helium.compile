package mirrg.helium.compile.oxygen.util;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.util.DialogProposal.EventDialogProposal;
import mirrg.helium.standard.hydrogen.event.EventManager;
import mirrg.helium.standard.hydrogen.util.HLambda;
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
	public final EventManager<EventPanelSyntax> eventManager = new EventManager<>();

	public PanelSyntax(Syntax<?> syntax)
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

		hookChange(textPane2, e -> {
			if (occurEvent) onUserEdit();
		});
		textPane2.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e)
			{
				if (e.isControlDown()) {
					e.consume();

					{
						String text = textPane2.getText();
						int caretPosition = textPane2.getCaretPosition();
						String left = text.substring(0, caretPosition);
						String right = text.substring(caretPosition);
						String src2 = left + "a" + right;

						Node<?> node = syntax.parse(src2);
						if (node == null) return;

						eventManager.post(new EventPanelSyntax.Parsed(node, EventPanelSyntax.Parsed.TIMING_BEFORE_PROPOSAL));

						ArrayList<Node<?>> hierarchy = new ArrayList<>();
						while (true) {
							hierarchy.add(node);
							if (node.children == null) break;
							Optional<Node<?>> node2 = node.children.stream()
								.filter(n -> n.begin <= caretPosition)
								.filter(n -> n.end > caretPosition)
								.findFirst();
							if (!node2.isPresent()) break;
							node = node2.get();
						}

						Optional<Node<?>> node2 = HLambda.reverse(hierarchy.stream())
							.filter(n -> n.value instanceof IProviderProposal)
							.findFirst();
						if (!node2.isPresent()) return;

						IProviderProposal providerProposal = (IProviderProposal) node2.get().value;

						Stream<Proposal> stream = providerProposal.getProposals();
						if (stream == null) return;

						{
							DialogProposal dialog = new DialogProposal(stream);

							dialog.eventManager.register(EventDialogProposal.Update.class, e2 -> {

								try {
									((DefaultStyledDocument) textPane2.getDocument()).replace(
										node2.get().begin,
										node2.get().end - node2.get().begin - 1,
										e2.proposal.text, null);
								} catch (BadLocationException e1) {
									HLog.processException(e1);
								}

								update();
							});

							{
								Point a = textPane2.getCaret().getMagicCaretPosition();
								Point b = textPane2.getLocationOnScreen();
								if (a != null && b != null) {
									dialog.setLocation(a.x + b.x, a.y + b.y + 20);
								}
							}
							dialog.pack();
							dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
							dialog.setVisible(true);
						}
					}

				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{

			}

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
				eventManager.post(new EventPanelSyntax.UserEdit(textPane2.getText()));

				update();

				textPane2.setCaretPosition(caretPosition);
			});
		});
		threadRecolor.start();
	}

	public void set(String text)
	{
		occurEvent = false;
		textPane2.setText(text);
		occurEvent = true;

		update();
	}

	public void update()
	{
		occurEvent = false;

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
			eventManager.post(new EventPanelSyntax.Parsed(node, EventPanelSyntax.Parsed.TIMING_USER_EDIT));

			textPane1.setText("");
			appendText(textPane1, text, node, 0);

			// clear
			SimpleAttributeSet attr = new SimpleAttributeSet();
			((DefaultStyledDocument) textPane2.getDocument()).setCharacterAttributes(
				0, textPane2.getDocument().getLength() + 1, attr, true);

			updateText(textPane2, node);
		} else {
			textPane1.setText("");
		}

		occurEvent = true;
	}

	public void setAttribute(int offset, int length, Consumer<SimpleAttributeSet> consumer)
	{
		SimpleAttributeSet attr = new SimpleAttributeSet();
		consumer.accept(attr);
		((DefaultStyledDocument) textPane2.getDocument()).setCharacterAttributes(offset, length, attr, false);
	}

	// TODO mirrg
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
			Color color = null;
			if (node.value instanceof IProviderColor) {
				color = ((IProviderColor) node.value).getColor();
			} else {
				if (node.children == null) {
					color = Color.black;
				} else {
					color = Color.gray;
				}
			}
			appendText(textPane, text.substring(node.begin, node.end), color);
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
		updateTextImpl(textPane, node);
		eventManager.post(new EventPanelSyntax.AfterHighlight(textPane, node));
	}

	private void updateTextImpl(JTextPane textPane, Node<?> node)
	{
		if (node.value instanceof IProviderColor) {
			setAttribute(node.begin, node.end - node.begin, a -> {
				a.addAttribute(StyleConstants.Foreground, ((IProviderColor) node.value).getColor());
			});
		}

		if (node.children != null) {
			for (Node<?> child : node.children) {
				updateTextImpl(textPane, child);
			}
		}
	}

}
