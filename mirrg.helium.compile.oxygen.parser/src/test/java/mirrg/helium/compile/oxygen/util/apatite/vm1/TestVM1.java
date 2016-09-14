package mirrg.helium.compile.oxygen.util.apatite.vm1;

import static org.junit.Assert.*;

import java.awt.CardLayout;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.junit.Test;

import mirrg.helium.compile.oxygen.parser.Test1;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.util.apatite.ErrorReporter;
import mirrg.helium.compile.oxygen.util.apatite.Formula;
import mirrg.helium.compile.oxygen.util.apatite.PanelApatite;

public class TestVM1
{

	private static PanelApatite panel;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();

		VM1 vm = new VM1();
		vm.registerConstant(VM1.STRING, "lineSeparator", "<br>");

		frame.setLayout(new CardLayout());
		frame.add(panel = new PanelApatite(Syntaxes1.root, vm));
		panel.getPanelSyntax().set("pi * 200");
		//pi * 200 + (3 * 4 -  3) * "214314" + pi * e - 4 * "a"

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
			Node<Formula> node = Syntaxes1.root.parse(src);
			VM1 vm = new VM1();
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
