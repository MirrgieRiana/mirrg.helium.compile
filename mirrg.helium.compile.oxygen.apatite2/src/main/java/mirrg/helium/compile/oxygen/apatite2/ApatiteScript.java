package mirrg.helium.compile.oxygen.apatite2;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.apatite2.nodes.CodeFunction;
import mirrg.helium.compile.oxygen.apatite2.nodes.CodeIdentifier;
import mirrg.helium.compile.oxygen.apatite2.nodes.CodeLiteralDouble;
import mirrg.helium.compile.oxygen.apatite2.nodes.CodeLiteralInteger;
import mirrg.helium.compile.oxygen.apatite2.nodes.CodeLiteralString;
import mirrg.helium.compile.oxygen.editor.WithColor;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxOr;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSlot;
import mirrg.helium.standard.hydrogen.struct.Struct2;
import mirrg.helium.standard.hydrogen.struct.Struct3;

public class ApatiteScript
{

	public static Syntax<IApatiteCode> getSyntax()
	{
		return new ApatiteScript().root;
	}

	//

	public Syntax<String> $ = WithColor.withColor(named(pack(repeat(or((String) null)
		.or(regex("[ \\t\\r\\n　]+"))
		.or(regex("//[^\\r\\n]*"))
		.or(regex("/\\*((?!\\*/)(?s:.))*\\*/"))),
		t -> String.join("", t)), "Comment"), t -> Color.decode("#008800"));

	public SyntaxSlot<IApatiteCode> factor = slot();

	public SyntaxSlot<IApatiteCode> expression = slot();

	//

	public Syntax<IApatiteCode> literalDouble = named(packNode(or((String) null)
		.or(regex("[0-9]+(\\.[0-9]+)([eE]-?[0-9]+)?"))
		.or(regex("[0-9]+([eE]-?[0-9]+)")),
		n -> new CodeLiteralDouble(n.begin, n.end, n.value)), "Double");

	public Syntax<IApatiteCode> literalInteger = named(packNode(regex("[0-9]+"),
		n -> new CodeLiteralInteger(n.begin, n.end, n.value)), "Integer");

	public Syntax<IApatiteCode> literalString;
	{
		Syntax<String> specialCharacters = or((String) null)
			.or(pack(string("\\r"), t -> "\r"))
			.or(pack(string("\\n"), t -> "\n"))
			.or(pack(string("\\t"), t -> "\t"))
			.or(pack(regexMatcher("\\\\(.)"), t -> t.group(1)));

		literalString = named(packNode(tunnel((String) null)
			.and(string("\""))
			.extract(pack(repeat(or((String) null)
				.or(regex("[^\\\\\"]+"))
				.or(specialCharacters)),
				t -> String.join("", t)))
			.and(string("\"")),
			n -> new CodeLiteralString(n.begin, n.end, n.value.get())), "String");
	}

	public Syntax<IApatiteCode> brackets = createBrackets(c -> {
		c.accept(bracketsVoid("(", ")", "_bracketsRound"));
		c.accept(bracketsVoid("[", "]", "_bracketsSquare"));
		c.accept(bracketsVoid("{", "}", "_bracketsCurly"));
		c.accept(brackets("(", expression, ")", "_bracketsRound"));
		c.accept(brackets("[", expression, "]", "_bracketsSquare"));
		c.accept(brackets("{", expression, "}", "_bracketsCurly"));
	});

	public Syntax<IApatiteCode> identifier = named(packNode(regex("[a-zA-Z_][a-zA-Z_0-9]*"),
		n -> new CodeIdentifier(n.value, n.begin, n.end)), "Identifier");

	{
		factor.syntax = or((IApatiteCode) null)
			.or(literalDouble)
			.or(literalInteger)
			.or(literalString)
			.or(brackets)
			.or(identifier);
	}

	//

	public Syntax<IApatiteCode> operatorRight = createRight(factor, c -> {
		c.accept(rightOperator(".", factor, "_operatorPeriod"));
		c.accept(rightBracketsVoid("(", ")", "_rightBracketsRound"));
		c.accept(rightBracketsVoid("[", "]", "_rightBracketsSquare"));
		c.accept(rightBracketsVoid("{", "}", "_rightBracketsCurly"));
		c.accept(rightBrackets("(", expression, ")", "_rightBracketsRound"));
		c.accept(rightBrackets("[", expression, "]", "_rightBracketsSquare"));
		c.accept(rightBrackets("{", expression, "}", "_rightBracketsCurly"));
	});

	public Syntax<IApatiteCode> operatorLeft = createLeft(operatorRight, c -> {
		c.accept(unary("+", "_leftPlus"));
		c.accept(unary("-", "_leftMinus"));
		c.accept(unary("!", "_leftExclamation"));
		c.accept(unary("$", "_leftDollar"));
		c.accept(unary("*", "_leftAsterisk"));
		c.accept(unary("&", "_leftAmpersand"));
	});

	public Syntax<IApatiteCode> operatorPow = createOperatorRight(operatorLeft, c -> {
		c.accept(operator("^", "_operatorHat"));
		c.accept(operator("**", "_operatorAsteriskAsterisk"));
	});

	public Syntax<IApatiteCode> operatorMul = createOperatorLeft(operatorPow, c -> {
		c.accept(operator("*", "_operatorAsterisk"));
		c.accept(operator("/", "_operatorSlash"));
		c.accept(operator("%", "_operatorPercent"));
	});

	public Syntax<IApatiteCode> operatorAdd = createOperatorLeft(operatorMul, c -> {
		c.accept(operator("+", "_operatorPlus"));
		c.accept(operator("-", "_operatorMinus"));
	});

	public Syntax<IApatiteCode> operatorCompare = createOperatorCompare(operatorAdd, c -> {
		c.accept(operator("<=", "_operatorLessEqual"));
		c.accept(operator("<", "_operatorLess"));
		c.accept(operator(">=", "_operatorGreaterEqual"));
		c.accept(operator(">", "_operatorGreater"));
	});

	public Syntax<IApatiteCode> operatorEquation = createOperatorLeft(operatorCompare, c -> {
		c.accept(operator("==", "_operatorEqualEqual"));
		c.accept(operator("!=", "_operatorExclamationEqual"));
	});

	public Syntax<IApatiteCode> operatorAnd = createOperatorLeft(operatorEquation, c -> {
		c.accept(operator("&&", "_operatorAmpersandAmpersand"));
	});

	public Syntax<IApatiteCode> operatorOr = createOperatorLeft(operatorAnd, c -> {
		c.accept(operator("||", "_operatorPipePipe"));
	});

	public SyntaxSlot<IApatiteCode> operatorIIf = slot();
	{
		operatorIIf.syntax = or((IApatiteCode) null)
			.or(packNode(serial(Struct3<IApatiteCode, IApatiteCode, IApatiteCode>::new)
				.and(operatorOr, Struct3::setX)
				.and($)
				.and(string("?"))
				.and($)
				.and(operatorIIf, Struct3::setY)
				.and($)
				.and(string(":"))
				.and($)
				.and(operatorIIf, Struct3::setZ),
				n -> new CodeFunction("_ternaryQuestionColon", n.begin, n.end, n.value.x, n.value.y, n.value.z)))
			.or(packNode(serial(Struct2<IApatiteCode, IApatiteCode>::new)
				.and(operatorOr, Struct2::setX)
				.and($)
				.and(string("?:"))
				.and($)
				.and(operatorIIf, Struct2::setY),
				n -> new CodeFunction("_operatorQuestionColon", n.begin, n.end, n.value.x, n.value.y)))
			.or(operatorOr);
	}

	public Syntax<IApatiteCode> operatorComma = packNode(serial(Struct2<IApatiteCode, ArrayList<Supplier<IApatiteCode>>>::new)
		.and(operatorIIf, Struct2::setX)
		.and(repeat(tunnel((IApatiteCode) null)
			.and($)
			.and(string(","))
			.and($)
			.extract(operatorIIf)), Struct2::setY),
		n -> {
			if (n.value.y.size() == 0) return n.value.x;

			IApatiteCode[] codes = new IApatiteCode[1 + n.value.y.size()];
			codes[0] = n.value.x;
			for (int i = 0; i < n.value.y.size(); i++) {
				codes[i + 1] = n.value.y.get(i).get();
			}
			return new CodeFunction("_enumerateComma", n.begin, n.end, codes);
		});

	{
		expression.syntax = operatorComma;
	}

	//

	public Syntax<IApatiteCode> root = pack(tunnel((IApatiteCode) null)
		.and($)
		.extract(expression)
		.and($),
		t -> t.get());

	//

	protected Syntax<UnaryOperator<IApatiteCode>> unary(
		String operator,
		String name)
	{
		return packNode(string(operator),
			n -> a -> new CodeFunction(name, n.begin, a.getEnd(), a));
	}

	protected Syntax<IApatiteCode> brackets(
		String left,
		Syntax<IApatiteCode> inner,
		String right,
		String name)
	{
		return packNode(tunnel((IApatiteCode) null)
			.and(string(left))
			.and($)
			.extract(inner)
			.and($)
			.and(string(right)),
			n -> new CodeFunction(name, n.begin, n.end, n.value.get()));
	}

	protected Syntax<IApatiteCode> bracketsVoid(
		String left,
		String right,
		String name)
	{
		return packNode(tunnel(null)
			.and(string(left))
			.and($)
			.and(string(right)),
			n -> new CodeFunction(name, n.begin, n.end));
	}

	protected Syntax<UnaryOperator<IApatiteCode>> rightOperator(
		String operator,
		Syntax<IApatiteCode> right,
		String name)
	{
		return packNode(tunnel((IApatiteCode) null)
			.and(string(operator))
			.and($)
			.extract(right),
			n -> a -> new CodeFunction(name, a.getBegin(), n.end, a, n.value.get()));
	}

	protected Syntax<UnaryOperator<IApatiteCode>> rightBrackets(
		String left,
		Syntax<IApatiteCode> inner,
		String right,
		String name)
	{
		return packNode(tunnel((IApatiteCode) null)
			.and(string(left))
			.and($)
			.extract(inner)
			.and($)
			.and(string(right)),
			n -> a -> new CodeFunction(name, a.getBegin(), n.end, a, n.value.get()));
	}

	protected Syntax<UnaryOperator<IApatiteCode>> rightBracketsVoid(
		String left,
		String right,
		String name)
	{
		return packNode(serial(() -> null)
			.and(string(left))
			.and($)
			.and(string(right)),
			n -> a -> new CodeFunction(name, a.getBegin(), n.end, a));
	}

	protected Syntax<IApatiteCode> createBrackets(
		Consumer<Consumer<Syntax<IApatiteCode>>> operator)
	{
		SyntaxOr<IApatiteCode> operators = or((IApatiteCode) null);
		operator.accept(operators::or);

		return operators;
	}

	@SuppressWarnings("deprecation")
	protected Syntax<IApatiteCode> createRight(
		Syntax<IApatiteCode> operand,
		Consumer<Consumer<Syntax<UnaryOperator<IApatiteCode>>>> operator)
	{
		SyntaxOr<UnaryOperator<IApatiteCode>> operators = or((UnaryOperator<IApatiteCode>) null);
		operator.accept(operators::or);

		return pack(serial(Struct2<IApatiteCode, ArrayList<UnaryOperator<IApatiteCode>>>::new)
			.and(operand, Struct2::setX)
			.and(repeat(extract((UnaryOperator<IApatiteCode>) null)
				.and($)
				.extract(operators)), Struct2::setY),
			t -> {
				IApatiteCode left = t.x;
				for (int i = 0; i < t.y.size(); i++) {
					left = t.y.get(i).apply(left);
				}
				return left;
			});
	}

	@SuppressWarnings("deprecation")
	protected Syntax<IApatiteCode> createLeft(
		Syntax<IApatiteCode> operand,
		Consumer<Consumer<Syntax<UnaryOperator<IApatiteCode>>>> operator)
	{
		SyntaxOr<UnaryOperator<IApatiteCode>> operators = or((UnaryOperator<IApatiteCode>) null);
		operator.accept(operators::or);

		return pack(serial(Struct2<ArrayList<UnaryOperator<IApatiteCode>>, IApatiteCode>::new)
			.and(repeat(extract((UnaryOperator<IApatiteCode>) null)
				.extract(operators)
				.and($)), Struct2::setX)
			.and(operand, Struct2::setY),
			t -> {
				IApatiteCode right = t.y;
				for (int i = t.x.size() - 1; i >= 0; i--) {
					right = t.x.get(i).apply(right);
				}
				return right;
			});
	}

	protected Syntax<BinaryOperator<IApatiteCode>> operator(
		String operator,
		String name)
	{
		return packNode(string(operator),
			n -> (a, b) -> new CodeFunction(name, a.getBegin(), b.getEnd(), a, b));
	}

	protected Syntax<IApatiteCode> createOperatorLeft(
		Syntax<IApatiteCode> operand,
		Consumer<Consumer<Syntax<BinaryOperator<IApatiteCode>>>> operator)
	{
		SyntaxOr<BinaryOperator<IApatiteCode>> operators = or((BinaryOperator<IApatiteCode>) null);
		operator.accept(operators::or);

		return pack(serial(Struct2<IApatiteCode, ArrayList<Struct2<BinaryOperator<IApatiteCode>, IApatiteCode>>>::new)
			.and(operand, Struct2::setX)
			.and(repeat(serial(Struct2<BinaryOperator<IApatiteCode>, IApatiteCode>::new)
				.and($)
				.and(operators, Struct2::setX)
				.and($)
				.and(operand, Struct2::setY)), Struct2::setY),
			t -> {
				IApatiteCode left = t.x;
				for (int i = 0; i < t.y.size(); i++) {
					left = t.y.get(i).x.apply(left, t.y.get(i).y);
				}
				return left;
			});
	}

	protected Syntax<IApatiteCode> createOperatorRight(
		Syntax<IApatiteCode> operand,
		Consumer<Consumer<Syntax<BinaryOperator<IApatiteCode>>>> operator)
	{
		SyntaxOr<BinaryOperator<IApatiteCode>> operators = or((BinaryOperator<IApatiteCode>) null);
		operator.accept(operators::or);

		return pack(serial(Struct2<ArrayList<Struct2<IApatiteCode, BinaryOperator<IApatiteCode>>>, IApatiteCode>::new)
			.and(repeat(serial(Struct2<IApatiteCode, BinaryOperator<IApatiteCode>>::new)
				.and(operand, Struct2::setX)
				.and($)
				.and(operators, Struct2::setY)
				.and($)), Struct2::setX)
			.and(operand, Struct2::setY),
			t -> {
				IApatiteCode right = t.y;
				for (int i = t.x.size() - 1; i >= 0; i--) {
					right = t.x.get(i).y.apply(t.x.get(i).x, right);
				}
				return right;
			});
	}

	protected Syntax<IApatiteCode> createOperatorCompare(
		Syntax<IApatiteCode> operand,
		Consumer<Consumer<Syntax<BinaryOperator<IApatiteCode>>>> operator)
	{
		SyntaxOr<BinaryOperator<IApatiteCode>> operators = or((BinaryOperator<IApatiteCode>) null);
		operator.accept(operators::or);

		return pack(serial(Struct2<IApatiteCode, ArrayList<Struct2<BinaryOperator<IApatiteCode>, IApatiteCode>>>::new)
			.and(operand, Struct2::setX)
			.and(repeat(serial(Struct2<BinaryOperator<IApatiteCode>, IApatiteCode>::new)
				.and($)
				.and(operators, Struct2::setX)
				.and($)
				.and(operand, Struct2::setY)), Struct2::setY),
			t -> {
				if (t.y.size() == 0) return t.x;
				if (t.y.size() == 1) return t.y.get(0).x.apply(t.x, t.y.get(0).y);

				IApatiteCode[] codes = new IApatiteCode[1 + t.y.size()];
				{
					codes[0] = t.x;
					for (int i = 0; i < t.y.size(); i++) {
						codes[i + 1] = t.y.get(i).y;
					}
				}

				IApatiteCode[] codes2 = new IApatiteCode[codes.length - 1];
				for (int i = 0; i < codes.length - 1; i++) {
					codes2[i] = t.y.get(i).x.apply(codes[i], codes[i + 1]);
				}

				IApatiteCode left = codes2[0];
				for (int i = 1; i < codes2.length; i++) {
					left = new CodeFunction("_operatorAmpersandAmpersand", left.getBegin(), codes2[i].getEnd(), left, codes2[i]);
				}
				return left;
			});
	}

}
