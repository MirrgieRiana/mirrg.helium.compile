package mirrg.helium.compile.oxygen.parser;

import static mirrg.helium.compile.oxygen.parser.HParserOxygen.*;
import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;
import static org.junit.Assert.*;

import java.awt.Color;
import java.util.Hashtable;
import java.util.function.ToDoubleFunction;

import org.junit.Test;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxOr;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSlot;
import mirrg.helium.compile.oxygen.util.ColoredString;
import mirrg.helium.standard.hydrogen.struct.Struct1;
import mirrg.helium.standard.hydrogen.struct.Struct2;

public class Test1
{

	private static final double D = 0.001;

	@Test
	public void test1()
	{
		ISyntax<IFormula> syntaxFactor = map(
			regex("\\d+"),
			s -> new FormulaLiteral(Integer.parseInt(s, 10)));
		ISyntax<IFormula> syntaxExpression = wrap(serial(FormulaOperation::new)
			.and(syntaxFactor, FormulaOperation::setLeft)
			.and(string("+"), (f, f2) -> f.function = (a, b) -> a + b)
			.and(syntaxFactor, FormulaOperation::setRight));

		ToDoubleFunction<String> f = src -> parse(syntaxExpression, src).value.calculate();

		assertEquals(2, f.applyAsDouble("1+1"), D);
		assertEquals(133, f.applyAsDouble("010+123"), D);
		assertEquals(50, f.applyAsDouble("0045+5"), D);
		assertEquals(700, f.applyAsDouble("200+500"), D);
		assertEquals(0, f.applyAsDouble("0+0"), D);
	}

	@Test
	public void test2()
	{
		Hashtable<String, Double> constants = new Hashtable<>();
		constants.put("pi", Math.PI);
		constants.put("e", Math.E);

		ISyntax<IFormula> syntaxInteger = map(
			regex("\\d+"),
			s -> new FormulaLiteral(Integer.parseInt(s, 10)));
		ISyntax<IFormula> syntaxConstant = map(
			regex("[a-zA-Z_][a-zA-Z_0-9]*"),
			s -> new FormulaLiteral(constants.get(s)));
		SyntaxOr<IFormula> syntaxFactor = or((IFormula) null)
			.or(syntaxInteger)
			.or(syntaxConstant);
		ISyntax<IFormula> syntaxExpression = wrap(serial(FormulaOperation::new)
			.and(syntaxFactor, FormulaOperation::setLeft)
			.and(string("+"), (f, f2) -> f.function = (a, b) -> a + b)
			.and(syntaxFactor, FormulaOperation::setRight));

		ToDoubleFunction<String> f = src -> parse(syntaxExpression, src).value.calculate();

		assertEquals(Math.PI, f.applyAsDouble("0+pi"), D);
	}

	public static ISyntax<IFormula> operation(
		ISyntax<IFormula> syntaxOperand,
		ISyntax<IFunction> syntaxOperator)
	{
		return wrap(serial(FormulaOperationArray::new)
			.and(syntaxOperand, (n1, n2) -> n1.left = n2)
			.and(repeat(serial(Struct2<IFunction, IFormula>::new)
				.and(syntaxOperator, (n1, n2) -> n1.x = n2)
				.and(syntaxOperand, (n1, n2) -> n1.y = n2)),
				(n1, n2) -> n1.right = n2));
	}

	public static SyntaxSlot<IFormula> test3_getSyntax()
	{
		Hashtable<String, Double> constants = new Hashtable<>();
		constants.put("pi", Math.PI);
		constants.put("e", Math.E);

		ISyntax<IFormula> syntaxInteger = map(
			regex("\\d+"),
			s -> new FormulaLiteral(Integer.parseInt(s, 10), Color.red));
		ISyntax<IFormula> syntaxConstant = map(
			regex("[a-zA-Z_][a-zA-Z_0-9]*"),
			s -> new FormulaLiteral(constants.get(s), Color.blue));
		SyntaxSlot<IFormula> syntaxExpression = slot();
		ISyntax<IFormula> syntaxBrackets = map(serial(Struct1<IFormula>::new)
			.and(map(string("("), s -> new ColoredString(s, Color.green)))
			.and(syntaxExpression, Struct1::setX)
			.and(map(string(")"), s -> new ColoredString(s, Color.green))),
			Struct1::getX);
		SyntaxOr<IFormula> syntaxFactor = or((IFormula) null)
			.or(syntaxInteger)
			.or(syntaxConstant)
			.or(syntaxBrackets);
		ISyntax<IFormula> syntaxTerm = wrap(operation(
			syntaxFactor,
			or((IFunction) null)
				.or(map(string("*"), s -> (a, b) -> a * b))
				.or(map(string("/"), s -> (a, b) -> a / b))));
		syntaxExpression.setSyntax(wrap(operation(
			syntaxTerm,
			or((IFunction) null)
				.or(map(string("+"), s -> (a, b) -> a + b))
				.or(map(string("-"), s -> (a, b) -> a - b)))));
		return syntaxExpression;
	}

	@Test
	public void test3()
	{
		ISyntax<IFormula> syntaxExpression = test3_getSyntax();

		ToDoubleFunction<String> f = src -> parse(syntaxExpression, src).value.calculate();

		assertEquals(77.9852278869, f.applyAsDouble("15/26*158+41-27*14/7+45/61*5-27/7"), D);
		assertEquals(-3.85706255112, f.applyAsDouble("15/(26*158+41-27)*(14/(7+45)/61)*5-27/7"), D);
	}

}
