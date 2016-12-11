package mirrg.helium.compile.oxygen.apatite2;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.Dimension;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.apatite2.loader.Loader;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;
import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.compile.oxygen.editor.TextPaneOxygen;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.standard.hydrogen.util.HString.LineProvider;

public class Sample1
{

	public static void main(String[] args)
	{
		JFrame frame = new Frame1(ApatiteScript.getSyntax(), "1 + 2 * (4 + 6)");
		frame.setTitle("Apatite Script");

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static class TextPaneOxygen1 extends TextPaneOxygen<IApatiteCode>
	{

		public TextPaneOxygen1(Syntax<IApatiteCode> syntax)
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

	public static class Frame1 extends JFrame
	{

		public TextPaneOxygen1 textPaneOxygen;
		public JTextPane textPaneOut;

		private ApatiteVM vm;
		private Optional<IApatiteScript> script;

		public Frame1(Syntax<IApatiteCode> syntax, String source)
		{
			add(process(createSplitPaneVertical(
				createScrollPane(get(() -> {
					textPaneOxygen = new TextPaneOxygen1(syntax);
					textPaneOxygen.setText(source);
					textPaneOxygen.setPreferredSize(new Dimension(500, 100));
					return textPaneOxygen;
				})),
				createScrollPane(get(() -> {
					textPaneOut = new JTextPane();
					textPaneOut.setPreferredSize(new Dimension(500, 80));
					textPaneOut.setEditable(false);
					textPaneOut.setOpaque(false);
					textPaneOut.setFont(new Font(Font.MONOSPACED, textPaneOut.getFont().getStyle(), textPaneOut.getFont().getSize()));
					return textPaneOut;
				}))),
				c -> ((JSplitPane) c).setResizeWeight(1) /* TODO mirrg */));

			{
				textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Success.class, e -> {
					ApatiteVM vm = Loader.createVM();
					Optional<IApatiteScript> script = textPaneOxygen.getValue().validate(vm);

					if (e.timing == EventTextPaneOxygen.Syntax.Success.TIMING_MAIN) {
						this.vm = vm;
						this.script = script;

						if (script.isPresent()) {
							a:
							{
								Object invoke;
								try {
									invoke = script.get().invoke();
								} catch (Exception e1) {
									{
										// ランタイムエラー
										ByteArrayOutputStream out = new ByteArrayOutputStream();
										try {
											e1.printStackTrace(new PrintStream(out, true, "Unicode"));
											textPaneOut.setText("" + out.toString("Unicode"));
										} catch (UnsupportedEncodingException e2) {
											e2.printStackTrace();
										}
									}
									break a;
								}
								{
									// 成功
									textPaneOut.setText("" + invoke);
								}
							}
						} else {
							// コンパイルエラー
							LineProvider lineProvider = HString.getLineProvider(textPaneOxygen.getText());
							textPaneOut.setText(vm.getErrors().stream()
								.map(t -> toPosition(lineProvider, t.getX()) + ": " + t.getZ())
								.collect(Collectors.joining("\n")));
						}

					}

				});
				textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Failure.class, e -> {
					// シンタックスエラー
					LineProvider lineProvider = HString.getLineProvider(textPaneOxygen.getText());
					int index = textPaneOxygen.getResult().getTokenProposalIndex();
					textPaneOut.setText(String.format("[SyntaxError %s] expected: %s\n%s",
						toPosition(lineProvider, index),
						textPaneOxygen.getResult().getTokenProposal().stream()
							.map(p -> p.getName())
							.distinct()
							.collect(Collectors.joining(" ")),
						String.join("\n", getPositionString(lineProvider, index))));
				});
				textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Error.class, e -> {
					// パースエラー
					textPaneOut.setText("" + e.exception);
				});
				textPaneOxygen.event().register(EventTextPaneOxygen.Highlight.Post.class, e -> {
					if (!script.isPresent()) {
						// コンパイルエラー時の下線表示
						vm.getErrors()
							.forEach(t -> {
								textPaneOxygen.setUnderline(
									t.getX(),
									t.getY() - t.getX());
							});
					}
				});
			}

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

	}

}
