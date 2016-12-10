package mirrg.helium.compile.oxygen.apatite2;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.apatite2.loader.Loader;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;
import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.compile.oxygen.editor.IProviderColor;
import mirrg.helium.compile.oxygen.editor.TextPaneOxygen;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.standard.hydrogen.util.HString.LineProvider;

public class Sample1
{

	public static void main(String[] args)
	{
		JFrame frame = new Frame1(ApatiteScript.getSyntax());
		frame.setTitle("Apatite Script");

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static class Frame1 extends JFrame
	{

		protected JTextPane textPaneTree;
		protected TextPaneOxygen<IApatiteCode> textPaneOxygen;
		protected JTextPane textPaneOut;

		public Frame1(Syntax<IApatiteCode> syntax)
		{

			add(createBorderPanelDown(
				createSplitPaneVertical(
					createScrollPane(get(() -> {
						textPaneTree = new JTextPane();
						textPaneTree.setEditable(false);
						textPaneTree.setFont(new Font(Font.MONOSPACED, textPaneTree.getFont().getStyle(), textPaneTree.getFont().getSize()));
						return textPaneTree;
					}), 500, 200),
					createScrollPane(get(() -> {
						textPaneOxygen = new TextPaneOxygen<>(syntax);
						textPaneOxygen.setText("1 + 2 * (4 + 6)");
						textPaneOxygen.setPreferredSize(new Dimension(500, 100));
						textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Success.class, e -> {
							if (e.timing == EventTextPaneOxygen.Syntax.Success.TIMING_MAIN) {
								if (textPaneOut == null) return;

								textPaneTree.setText("");
								appendText(textPaneTree, textPaneOxygen.getText(), textPaneOxygen.getResult().node, 0);

								ApatiteVM vm = Loader.createVM();
								Optional<IApatiteScript> script = textPaneOxygen.getValue().validate(vm);
								if (script.isPresent()) {
									textPaneOut.setText("" + script.get().invoke());
								} else {
									LineProvider lineProvider = HString.getLineProvider(textPaneOxygen.getText());

									textPaneOut.setText(vm.getErrors().stream()
										.map(t -> toPosition(lineProvider, t.getX()) + ": " + t.getZ())
										.collect(Collectors.joining("\n")));
								}

							}
						});
						textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Failure.class, e -> {
							if (textPaneOut == null) return;

							LineProvider lineProvider = HString.getLineProvider(textPaneOxygen.getText());
							int index = textPaneOxygen.getResult().getTokenProposalIndex();
							textPaneOut.setText(String.format("[SyntaxError%s] expected: %s\n%s",
								toPosition(lineProvider, index),
								textPaneOxygen.getResult().getTokenProposal().stream()
									.map(p -> p.getName())
									.distinct()
									.collect(Collectors.joining(" ")),
								String.join("\n", getPositionString(lineProvider, index))));
						});
						textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Error.class, e -> {
							if (textPaneOut == null) return;

							textPaneTree.setText(toString(e.exception));

							textPaneOut.setText("" + e.exception);
						});
						return textPaneOxygen;
					}))),
				get(() -> {
					textPaneOut = new JTextPane();
					textPaneOut.setEditable(false);
					textPaneOut.setOpaque(false);
					textPaneOut.setFont(new Font(Font.MONOSPACED, textPaneOut.getFont().getStyle(), textPaneOut.getFont().getSize()));
					return textPaneOut;
				})));

			textPaneOxygen.update();
		}

		public static String toPosition(LineProvider lineProvider, int characterIndex)
		{
			int row = lineProvider.getLineNumber(characterIndex);
			int column = characterIndex - lineProvider.getStartIndex(row);

			return String.format("R:%d, C:%d", row, column);
		}

		public static String[] getPositionString(LineProvider lineProvider, int characterIndex)
		{
			int row = lineProvider.getLineNumber(characterIndex);
			int column = characterIndex - lineProvider.getStartIndex(row);
			String line = lineProvider.getContent(row);

			return new String[] {
				line,
				HString.rept(" ", column) + "^",
			};
		}

		// TODO mirrg
		private static String toString(Exception e)
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

		private static void appendText(JTextPane textPane, String text, Node<?> node, int indent)
		{
			appendText(textPane, HString.rept("  ", indent), Color.black);
			appendText(textPane, "[b=", Color.gray);
			appendText(textPane, "" + node.begin, Color.magenta);
			appendText(textPane, ", e=", Color.gray);
			appendText(textPane, "" + node.end, Color.magenta);
			appendText(textPane, ", s=", Color.gray);
			{
				Color color = node.children == null ? Color.black : Color.gray;
				if (node.value instanceof IProviderColor) {
					Color color2 = ((IProviderColor) node.value).getColor();
					if (color2 != null) color = color2;
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

		private static void appendText(JTextPane textPane, String string, Color color)
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

}
