package mirrg.helium.compile.oxygen.parser.test2;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;
import static org.junit.Assert.*;

import java.awt.CardLayout;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.text.StyleConstants;

import org.junit.Test;

import mirrg.helium.compile.oxygen.parser.Test1;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.util.EventPanelSyntax;
import mirrg.helium.compile.oxygen.util.PanelSyntax;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.standard.hydrogen.util.HString.LineProvider;

public class Test2
{

	private static JLabel label;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();

		VM vm = new VM();

		frame.setLayout(new CardLayout());
		frame.add(createSplitPaneVertical(
			get(() -> {
				PanelSyntax panel = new PanelSyntax(Syntaxes.root);
				panel.eventManager.register(EventPanelSyntax.UserEdit.class, e -> {
					Node<Formula> node = Syntaxes.root.parse(e.source);
					if (node != null) {
						ErrorReporter errorReporter = node.value.validate(vm);

						if (errorReporter.isValid()) {
							label.setText("<html>" + node.value.calculate(vm.createRuntime()) + "</html>");
						} else {
							LineProvider lineProvider = HString.getLineProvider(e.source);

							label.setText("<html>" + errorReporter.messages.stream()
								.map(t -> {
									int lineNumber = lineProvider.getLineNumber(t.getX().getBegin());
									return String.format("[L: %d, C: %d] %s",
										lineNumber,
										t.getX().getBegin() - lineProvider.getStartIndex(lineNumber),
										t.getY());
								})
								.collect(Collectors.joining("<br>")) + "</html>");
						}
					} else {
						label.setText("<html>" + "構文エラーです" + "</html>");
					}
				});
				panel.eventManager.register(EventPanelSyntax.Parsed.class, e -> {
					((Node<Formula>) e.node).value.validate(vm);
				});
				panel.eventManager.register(EventPanelSyntax.AfterHighlight.class, e -> {
					ErrorReporter errorReporter = ((Node<Formula>) e.node).value.validate(vm);
					errorReporter.messages.forEach(m -> {
						panel.setAttribute(m.getX().getBegin(), m.getX().getEnd() - m.getX().getBegin(), a -> {
							a.addAttribute(StyleConstants.Underline, true);
						});
					});
				});//pi * 200 + (3 * 4 -  3) * "214314" + pi * e - 4 * "a"
				panel.set("pi * 200");
				return panel;
			}),
			createScrollPane(get(() -> {
				label = new JLabel("<html></html>");
				label.setVerticalAlignment(JLabel.TOP);
				return label;
			}), 100, 80)));

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

	@SuppressWarnings("unused")
	@Test
	public void test1()
	{
		Function<String, Object> run = src -> {
			Node<Formula> node = Syntaxes.root.parse(src);
			VM vm = new VM();
			ErrorReporter errorReporter = node.value.validate(vm);

			errorReporter.messages.forEach(t -> {
				System.err.println("b: " + t.getX().getBegin() + ", e: " + t.getX().getEnd());
				System.err.println(t.getY());
			});

			if (errorReporter.messages.size() > 0) {
				return null;
			}

			return node.value.calculate(vm.createRuntime());
		};

		assertEquals("a", run.apply("( ( 'a' ) )"));
		assertEquals("20a", run.apply("20 + 'a'"));
		assertEquals("[560]", run.apply("'[' + (500 + 60) + ']'"));

		assertEquals(54, (int) run.apply("20 + 34"));
		assertEquals(54.0, (double) run.apply("20 + 34.0"), Test1.D);
		assertEquals(54.0, (double) run.apply("20.0 + 34"), Test1.D);
		assertEquals(54.0, (double) run.apply("20.0 + 34.0"), Test1.D);

		assertEquals(1, run.apply("10 % 3"));
		assertEquals(null, run.apply("5.0 % 3"));
		assertEquals(null, run.apply("5 % 3.0"));

		assertEquals(15 / 26 * 158 + 41 - 27 * 14 / 7 + 45 / 61 * 5 - 27 / 7, run.apply("15/26*158+41-27*14/7+45/61*5-27/7"));
		assertEquals(15 / (26 * 158 + 41 - 27) * (14 / (7 + 45) / 61) * 5 - 27 / 7, run.apply("15/(26*158+41-27)*(14/(7+45)/61)*5-27/7"));

		assertEquals(true, (boolean) run.apply("10 > 3"));
		assertEquals(false, (boolean) run.apply("10 < 3"));
		assertEquals(true, (boolean) run.apply("10 >= 3"));
		assertEquals(false, (boolean) run.apply("10 < 3"));
		assertEquals(false, (boolean) run.apply("10 == 3"));
		assertEquals(true, (boolean) run.apply("10 != 3"));

		assertEquals(false, (boolean) run.apply("10 > 10"));
		assertEquals(false, (boolean) run.apply("10 < 10"));
		assertEquals(true, (boolean) run.apply("10 >= 10"));
		assertEquals(true, (boolean) run.apply("10 <= 10"));
		assertEquals(true, (boolean) run.apply("10 == 10"));
		assertEquals(false, (boolean) run.apply("10 != 10"));

		assertEquals(3 > 5 && 7 < 8 || 2 > 5, (boolean) run.apply("3 > 5 && 7 < 8 || 2 > 5"));
		assertEquals(7 > 3 && 2 < 4 || 1 > 6, (boolean) run.apply("7 > 3 && 2 < 4 || 1 > 6"));
		assertEquals(8 > 3 && 6 < 2 || 1 > 3, (boolean) run.apply("8 > 3 && 6 < 2 || 1 > 3"));

		assertEquals(Math.PI * 10, (double) run.apply("10 * pi"), Test1.D);
		assertEquals(Math.E * 10, (double) run.apply("10 * e"), Test1.D);

		assertEquals(true, (boolean) run.apply("true || true"));
		assertEquals(true, (boolean) run.apply("true || false"));
		assertEquals(true, (boolean) run.apply("false || true"));
		assertEquals(false, (boolean) run.apply("false || false"));
		assertEquals(true, (boolean) run.apply("true && true"));
		assertEquals(false, (boolean) run.apply("true && false"));
		assertEquals(false, (boolean) run.apply("false && true"));
		assertEquals(false, (boolean) run.apply("false && false"));

		assertEquals(null, run.apply("false && pi"));
	}

}
