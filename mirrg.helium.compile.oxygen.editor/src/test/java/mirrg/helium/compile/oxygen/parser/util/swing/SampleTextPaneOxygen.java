package mirrg.helium.compile.oxygen.parser.util.swing;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;
import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.compile.oxygen.editor.IProviderColor;
import mirrg.helium.compile.oxygen.editor.Proposal;
import mirrg.helium.compile.oxygen.editor.TextPaneOxygen;
import mirrg.helium.compile.oxygen.editor.WithColor;
import mirrg.helium.compile.oxygen.editor.WithProposal;
import mirrg.helium.compile.oxygen.parser.HSyntaxOxygen;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSlot;
import mirrg.helium.standard.hydrogen.struct.Struct2;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.standard.hydrogen.util.HString.LineProvider;

public class SampleTextPaneOxygen
{

	public static void main(String[] args)
	{
		JFrame frame = new Frame1();

		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public static Syntax<Integer> getSyntax()
	{
		SyntaxSlot<Integer> syntaxExpression = slot();

		Syntax<String> syntaxComment = named(regex("[ \\t\\r\\n]*"), "Comment");
		Syntax<Integer> syntaxInteger = pack(WithColor.withColor(named(regex("[0-9]+"), "Integer"),
			s -> Color.decode("#ff8800")),
			s -> Integer.parseInt(s));
		Syntax<Integer> syntaxVariable = pack(WithProposal.withProposal(WithColor.withColor(named(regex("[a-zA-Z_][a-zA-Z0-9_]*"), "Identifier"),
			s -> Color.decode("#000088")),
			s -> Stream.of(Color.class.getMethods())
				.map(Method::getName)
				.map(s2 -> new Proposal1(s2))),
			s -> s.hashCode());
		Syntax<Integer> syntaxBracket = extract((Integer) null)
			.and(string("("))
			.and(syntaxComment)
			.extract(syntaxExpression)
			.and(syntaxComment)
			.and(string(")"));
		Syntax<Integer> syntaxFactor = or((Integer) null)
			.or(syntaxInteger)
			.or(syntaxVariable)
			.or(syntaxBracket);

		Syntax<Integer> syntaxMul = pack(serial(() -> new Integer[1])
			.and(syntaxFactor, (t1, t2) -> t1[0] = t2)
			.and(syntaxComment)
			.and(HSyntaxOxygen.<IntUnaryOperator> repeat(pack(serial(() -> new Struct2<IntBinaryOperator, Integer>())
				.and(syntaxComment)
				.and(or((IntBinaryOperator) null)
					.or(pack(string("*"), s -> (a, b) -> a * b))
					.or(pack(string("/"), s -> (a, b) -> a / b)), Struct2::setX)
				.and(syntaxComment)
				.and(syntaxFactor, Struct2::setY),
				t -> a -> t.x.applyAsInt(a, t.y))),
				(t1, t2) -> t2.forEach(t3 -> t1[0] = t3.applyAsInt(t1[0]))),
			t -> t[0]);
		Syntax<Integer> syntaxAdd = pack(serial(() -> new Integer[1])
			.and(syntaxMul, (t1, t2) -> t1[0] = t2)
			.and(syntaxComment)
			.and(HSyntaxOxygen.<IntUnaryOperator> repeat(pack(serial(() -> new Struct2<IntBinaryOperator, Integer>())
				.and(syntaxComment)
				.and(or((IntBinaryOperator) null)
					.or(pack(string("+"), s -> (a, b) -> a + b))
					.or(pack(string("-"), s -> (a, b) -> a - b)), Struct2::setX)
				.and(syntaxComment)
				.and(syntaxMul, Struct2::setY),
				t -> a -> t.x.applyAsInt(a, t.y))),
				(t1, t2) -> t2.forEach(t3 -> t1[0] = t3.applyAsInt(t1[0]))),
			t -> t[0]);
		syntaxExpression.syntax = syntaxAdd;

		return extract((Integer) null)
			.and(syntaxComment)
			.extract(syntaxExpression)
			.and(syntaxComment);
	}

	public static class Proposal1 extends Proposal
	{

		private Proposal1(String text)
		{
			super(text);
		}

		@Override
		public void decorateListCellRendererComponent(JLabel label)
		{
			label.setForeground(new Color(text.hashCode()).darker().darker());
			label.setBackground(new Color(("a" + text).hashCode()).brighter().brighter());
		}

	}

	public static class Frame1 extends JFrame
	{

		protected JTextPane textPaneTree;
		protected TextPaneOxygen<Integer> textPaneOxygen;
		protected JTextPane textPaneOut;

		public Frame1()
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
						textPaneOxygen = new TextPaneOxygen<>(getSyntax());
						textPaneOxygen.setText("1 + 2 * (4 + 6)");
						textPaneOxygen.setPreferredSize(new Dimension(500, 100));
						textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Success.class, e -> {
							if (e.timing == EventTextPaneOxygen.Syntax.Success.TIMING_MAIN) {
								if (textPaneOut == null) return;

								textPaneTree.setText("");
								appendText(textPaneTree, textPaneOxygen.getText(), textPaneOxygen.getResult().node, 0);

								textPaneOut.setText("" + textPaneOxygen.getValue());
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
