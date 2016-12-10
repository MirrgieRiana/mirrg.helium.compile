package mirrg.helium.compile.oxygen.apatite2;

import static org.junit.Assert.*;

import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Test;

import mirrg.helium.compile.oxygen.apatite2.loader.Loader;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;
import mirrg.helium.compile.oxygen.parser.core.ResultOxygen;

public class Test1
{

	@Test
	public void testComment()
	{
		assertSuccess(1, " 1 ");
		assertSuccess(1, "\r \n1 \r\n");
		assertSuccess(1, "\r\r\r\r1\n\n\n\n \n\r");
		assertSuccess(1, "1 // )(dsv];@[o.k0-@o:.-^");
		assertSuccess(1, "1 /* */ /* */ /* */ // */ ");
		assertSuccess(2, "1 // ][][ \r + 1 ");
	}

	@Test
	public void testLiteralNumber()
	{
		assertSuccess(1, "1");
		assertSuccess(1000, "1000");
		assertSuccess(1000, "01000");
		assertCompileError("10000000000");

		assertSuccess(1.0, "1.0");
		assertSuccess(1E3, "1E3");
		assertSuccess(1e3, "1e3");
		assertSuccess(1.50, "1.50");
		assertSuccess(1.05, "1.05");
		assertSuccess(01.05, "01.05");
		assertSuccess(0.5E1, "0.5E1");
		assertSuccess(0.5E-1, "0.5E-1");
		assertSuccess(0.5E0, "0.5E0");
		assertSuccess(0.0, "0.0");
		assertSyntaxError("10e");
	}

	@Test
	public void testLiteralString()
	{
		assertSuccess("", "\"\"");
		assertSyntaxError("\"\"\"");
		assertSuccess("abc123_[]' あ亜♨", "\"abc123_[]' あ亜♨\"");

		assertSuccess("\\", "\"\\\\\"");
		assertSuccess("\r\n\t", "\"\\r\\n\\t\"");
		assertSuccess("\"'", "\"\\\"\\'\"");
		assertSuccess("a1;", "\"\\a\\1\\;\"");
	}

	@Test
	public void testLiteralBoolean()
	{
		assertSuccess(true, "true");
		assertSuccess(false, "false");
	}

	@Test
	public void testLiteralFactor()
	{
		assertSuccess(3, "(((3)))");
	}

	@Test
	public void testOperatorRight()
	{
		assertSuccess(Math.sin(100.0), "sin(100.0)", 0.001);
		assertSuccess(Math.cos(100.0), "cos(100.0)", 0.001);
		assertSuccess(Math.tan(100.0), "tan(100.0)", 0.001);
		assertSuccess(Loader.DOUBLE, "type(100.0)");
		assertSuccess(Loader.STRING, "type(\"a\")");
		assertSuccess(Loader.BOOLEAN, "type(4 > 5)");
		assertCompileError("50(100.0)");
		assertCompileError("\"sin\"(100.0)");

		assertSuccess(-10, "_leftMinus(10)");
		assertSuccess(256.0, "_operatorHat(2, 8)", 0.001);
		assertSuccess(5, "_ternaryQuestionColon(true, 5, 6)");
		assertSuccess(a -> assertEquals((double) System.currentTimeMillis(), (Double) a, 100), "now()");
		assertSuccess(Math.sin(100.0), "_rightBracketsRound(sin, 100.0)");
		assertSuccess(Math.sin(100.0), "_rightBracketsRound(_rightBracketsRound, (_rightBracketsRound, (sin, 100.0)))");
	}

	@Test
	public void testOperatorLeft()
	{
		assertSuccess(+5, "+5");
		assertSuccess(-5, "-5");
		assertSuccess(+5.5, "+5.5");
		assertSuccess(-5.5, "-5.5");
		assertSuccess(false, "!true");
		assertSuccess(true, "!!true");
	}

	@Test
	public void testOperatorPow()
	{
		assertSuccess(256.0, "2 ^ 8", 0.001);
		assertSuccess(65536.0, "4 ^ 2 ^ 3", 0.001);
	}

	@Test
	public void testOperatorMul()
	{
		assertSuccess(55, "5 + 5 * 10");
		assertSuccess(35, "5 * 5 + 10");
		assertSuccess(100, "(5 + 5) * 10");
		assertSuccess(75, "5 * (5 + 10)");

		assertSuccess(25, "5 * 5");
		assertSuccess(25.0, "5 * 5.0");
		assertSuccess(25.0, "5.0 * 5");
		assertSuccess(25.0, "5.0 * 5.0");

		assertSuccess(2, "10 / 5");
		assertSuccess(2.0, "10 / 5.0");
		assertSuccess(2.0, "10.0 / 5");
		assertSuccess(2.0, "10.0 / 5.0");

		assertSuccess(3, "10 / 3");
		assertSuccess(10 / 3.0, "10 / 3.0", 0.001);

		assertSuccess(1, "10 % 3");
		assertSuccess(0, "10 % 5");

		assertSuccess(-20, "10 * -2");
		assertSuccess(-5, "10 / -2");
	}

	@Test
	public void testOperatorAdd()
	{
		assertSuccess(10, "5 + 5");
		assertSuccess(20, "5 + 5 + 10");
		assertSuccess(120, " 5+ /* */ 5 + 10 \r\n+ //\r 100 ");
		assertSyntaxError("5 + ");

		assertSuccess(10.0, "5 + 5.0");
		assertSuccess(10.0, "5.0 + 5");
		assertSuccess(10.0, "5.0 + 5.0");
		assertSuccess(120.0, "5 + 5 + 10 + 100.0");

		assertSuccess(5, "10 - 5");
		assertSuccess(8, "10 - 5 + 7 - 6 - 4 + 6");

		assertSuccess(0, "5 + -5");
		assertSuccess(10, "5 + --5");
		assertSuccess(0, "5 + ---5");

		assertSuccess("a20", "\"a\" + 20");
	}

	@Test
	public void testOperatorCompare()
	{
		assertSuccess(true, "1 <= 1");
		assertSuccess(true, "1 <= 2");
		assertSuccess(false, "2 <= 1");
		assertSuccess(false, "1 < 1");
		assertSuccess(true, "1 < 2");
		assertSuccess(false, "2 < 1");
		assertSuccess(true, "1 >= 1");
		assertSuccess(false, "1 >= 2");
		assertSuccess(true, "2 >= 1");
		assertSuccess(false, "1 > 1");
		assertSuccess(false, "1 > 2");
		assertSuccess(true, "2 > 1");

		assertSuccess(true, "1 < 2 < 3");
		assertSuccess(false, "1 < 2 > 3");
		assertSuccess(false, "1 > 2 < 3");
		assertSuccess(true, "-1 < 0 < 0.5 < 0.55 < 1 < 200 > 34 > 7 > 2 < 3 < 4 <= 4 < 5");
		assertSuccess(false, "-1 < 0 < 0.5 < 0.55 < 1 < 200 > 34 > 7 > 20 < 3 < 4 <= 4 < 5");

		assertSuccess(true, "10 * 10 > 99");
	}

	@Test
	public void testOperatorEquation()
	{
		assertSuccess(true, "1 == 1");
		assertSuccess(false, "2 == 1");
		assertSuccess(false, "1.0 == 1");
		assertSuccess(true, "false == false");
		assertSuccess(true, "true == true");
		assertSuccess(false, "true == false");
		assertSuccess(true, "\"a\" == \"a\"");
		assertSuccess(false, "\"a\" == \"ab\"");

		assertSuccess(false, "1 != 1");
		assertSuccess(true, "2 != 1");
		assertSuccess(true, "1.0 != 1");
		assertSuccess(false, "false != false");
		assertSuccess(false, "true != true");
		assertSuccess(true, "true != false");
		assertSuccess(false, "\"a\" != \"a\"");
		assertSuccess(true, "\"a\" != \"ab\"");

		assertSuccess(true, "100 > 1 == 1000 > 1");
		assertSuccess(false, "100 > 1 == 1000 > 10000");
		assertSuccess(true, "100 > 10000 == 1000 > 10000");
	}

	@Test
	public void testOperatorLogic()
	{
		assertSuccess(true, "true && true");
		assertSuccess(false, "true && false");
		assertSuccess(false, "false && true");
		assertSuccess(false, "false && false");

		assertSuccess(true, "true || true");
		assertSuccess(true, "true || false");
		assertSuccess(true, "false || true");
		assertSuccess(false, "false || false");

		assertSuccess(true, "true && true || true && false");
		assertSuccess(true, "40 == 40 || 40 == 50");
		assertSuccess(false, "3 == 40 || 3 == 50");
	}

	@Test
	public void testOperatorIIf()
	{
		assertSuccess(5, "true ? 5 : 10");
		assertSuccess(10, "false ? 5 : 10");
		assertSuccess(4, "false ? 5 : true ? 4 : 6");
		assertSuccess(6, "true ? false ? 3 : 6 : true ? 4 : 6");
		assertSuccess("a", "true ? \"a\" : \"b\"");
		assertSuccess(5.0, "true ? 5.0 : 10.0");
		assertSuccess(false, "true ? false : true");

		assertSuccess(true, "true ?: true");
		assertSuccess(true, "true ?: false");
		assertSuccess(true, "false ?: true");
		assertSuccess(false, "false ?: false");
	}

	private void assertSyntaxError(String source)
	{
		ResultOxygen<IApatiteCode> result = ApatiteScript.getSyntax().matches(source);

		assertFalse(result.isValid);
	}

	private void assertCompileError(String source)
	{
		ResultOxygen<IApatiteCode> result = ApatiteScript.getSyntax().matches(source);

		if (!result.isValid) {
			fail();
		}

		ApatiteVM vm = Loader.createVM();
		Optional<IApatiteScript> oScript = result.node.value.validate(vm);

		assertTrue(!oScript.isPresent());
	}

	private void assertSuccess(Consumer<Object> consumer, String source)
	{
		ResultOxygen<IApatiteCode> result = ApatiteScript.getSyntax().matches(source);

		if (!result.isValid) {
			fail();
		}

		ApatiteVM vm = Loader.createVM();
		Optional<IApatiteScript> oScript = result.node.value.validate(vm);

		if (!oScript.isPresent()) {
			vm.getErrors().forEach(System.err::println);
			fail();
		}

		consumer.accept(oScript.get().invoke());
	}

	private void assertSuccess(Object expected, String source)
	{
		assertSuccess(a -> assertEquals(expected, a), source);
	}

	private void assertSuccess(double expected, String source, double delta)
	{
		assertSuccess(a -> assertEquals(expected, (Double) a, delta), source);
	}

}
