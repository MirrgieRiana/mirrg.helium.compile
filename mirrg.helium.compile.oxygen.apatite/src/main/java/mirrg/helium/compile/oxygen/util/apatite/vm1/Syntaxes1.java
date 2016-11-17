package mirrg.helium.compile.oxygen.util.apatite.vm1;

import static mirrg.helium.compile.oxygen.editor.WithColor.*;
import static mirrg.helium.compile.oxygen.editor.WithProposal.*;
import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.util.ArrayList;
import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;

import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.util.apatite.ErrorReporter;
import mirrg.helium.compile.oxygen.util.apatite.Formula;
import mirrg.helium.compile.oxygen.util.apatite.FormulaOperation;
import mirrg.helium.compile.oxygen.util.apatite.FormulaVariable;
import mirrg.helium.compile.oxygen.util.apatite.Type;
import mirrg.helium.compile.oxygen.util.apatite.VM;
import mirrg.helium.standard.hydrogen.struct.Struct2;
import mirrg.helium.standard.hydrogen.struct.Struct3;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class Syntaxes1
{

	public static Syntax<String> $;
	{
		$ = slot(() -> named(regex("[ \\t\\r\\n]*"), "Blank"));
	}

	public static Syntax<Formula> literalImaginaryInteger;
	{
		literalImaginaryInteger = slot(() -> wrap(withColor(pack(named(extract((String) null)
			.extract(regex("[0-9]+"))
			.and(string("i")), "ImaginaryInteger"),
			s -> new FormulaLiteral(
				vm -> vm.getBaseType("Complex").apply(),
				() -> new Complex(0, Integer.parseInt(s, 10)))),
			s -> s.getColor())));
	}

	public static Syntax<Formula> literalImaginaryDouble;
	{
		literalImaginaryDouble = slot(() -> wrap(withColor(pack(named(extract((String) null)
			.extract(regex("[0-9]+\\.[0-9]+"))
			.and(string("i")), "ImaginaryDouble"),
			s -> new FormulaLiteral(
				vm -> vm.getBaseType("Complex").apply(),
				() -> new Complex(0, Double.parseDouble(s)))),
			s -> s.getColor())));
	}

	public static Syntax<Formula> literalInteger;
	{
		literalInteger = slot(() -> wrap(withColor(pack(named(regex("[0-9]+"), "Integer"),
			s -> new FormulaLiteral(
				vm -> vm.getBaseType("Integer").apply(),
				() -> Integer.parseInt(s, 10))),
			s -> s.getColor())));
	}

	public static Syntax<Formula> literalDouble;
	{
		literalDouble = slot(() -> wrap(withColor(pack(named(regex("[0-9]+\\.[0-9]+"), "Double"),
			s -> new FormulaLiteral(
				vm -> vm.getBaseType("Double").apply(),
				() -> Double.parseDouble(s))),
			s -> s.getColor())));
	}

	public static Syntax<Formula> literalString;
	{
		literalString = slot(() -> wrap(withColor(pack(named(or((Struct3<Node<?>, String, Node<?>>) null)
			.or(serial(Struct3<Node<?>, String, Node<?>>::new)
				.and(packNode(string("\""), n -> n), Struct3::setX)
				.and(regex("[^\"]*"), Struct3::setY)
				.and(packNode(string("\""), n -> n), Struct3::setZ))
			.or(serial(Struct3<Node<?>, String, Node<?>>::new)
				.and(packNode(string("'"), n -> n), Struct3::setX)
				.and(regex("[^']*"), Struct3::setY)
				.and(packNode(string("'"), n -> n), Struct3::setZ)), "String"),
			s -> new FormulsLiteralString(vm -> vm.getBaseType("String").apply(), s.y, s.x.begin, s.z.end)),
			s -> s.getColor())));
	}

	public static Syntax<Formula> literalIdentifier;
	{
		literalIdentifier = slot(() -> wrap(withProposal(withColor(pack(named(regex("[a-zA-Z_][a-zA-Z_0-9]*"), "Identifier"),
			s -> new FormulaVariable(s)),
			s -> s.getColor()),
			s -> s.getProposals())));
	}

	public static Syntax<Formula> bracket;
	{
		bracket = slot(() -> pack(serial(Struct3<Node<?>, Formula, Node<?>>::new)
			.and(packNode(string("("), n -> n), Struct3::setX)
			.and($)
			.and(expression, Struct3::setY)
			.and($)
			.and(packNode(string(")"), n -> n), Struct3::setZ),
			s -> new Formula() {

				@Override
				public Type getType()
				{
					return s.y.getType();
				}

				@Override
				public boolean validateImpl(VM vm, ErrorReporter errorReporter)
				{
					return s.y.validateImpl(vm, errorReporter);
				}

				@Override
				public Object calculate()
				{
					return s.y.calculate();
				}

				@Override
				public int getBegin()
				{
					return s.x.begin;
				}

				@Override
				public int getEnd()
				{
					return s.z.end;
				}

			}));
	}

	public static Syntax<Formula> factor;
	{
		factor = slot(() -> or((Formula) null)
			.or(literalImaginaryDouble)
			.or(literalImaginaryInteger)
			.or(literalDouble)
			.or(literalInteger)
			.or(literalString)
			.or(literalIdentifier)
			.or(bracket));
	}

	public static Syntax<Formula> right;
	{
		right = slot(() -> pack(serial(Struct2<Formula, ArrayList<Function<Formula, Formula>>>::new)
			.and(factor, Struct2::setX)
			.and(repeat(extract((Function<Formula, Formula>) null)
				.and($)
				.extract(or((Function<Formula, Formula>) null)
					.or(extract((Function<Formula, Formula>) null)
						.and(string("("))
						.and($)
						.extract(pack(expression, f -> f2 -> new FormulaBracketsRight(f2, f)))
						.and($)
						.and(string(")"))))), Struct2::setY),
			s -> {
				Formula formula = s.x;

				for (Function<Formula, Formula> function : s.y) {
					formula = function.apply(formula);
				}

				return formula;
			}));
	}

	public static Syntax<Formula> mul;
	{
		mul = slot(() -> pack(serial(Struct2<Formula, ArrayList<Struct2<String, Formula>>>::new)
			.and(right, Struct2::setX)
			.and(repeat(serial(Struct2<String, Formula>::new)
				.and($)
				.and(or((String) null)
					.or(string("*"))
					.or(string("/"))
					.or(string("%")),
					Struct2<String, Formula>::setX)
				.and($)
				.and(right, Struct2::setY)), Struct2::setY),
			t -> FormulaOperation.chainLeft(t.x, t.y)));
	}

	public static Syntax<Formula> add;
	{
		add = slot(() -> pack(serial(Struct2<Formula, ArrayList<Struct2<String, Formula>>>::new)
			.and(mul, Struct2::setX)
			.and(repeat(serial(Struct2<String, Formula>::new)
				.and($)
				.and(or((String) null)
					.or(string("+"))
					.or(string("-")),
					Struct2<String, Formula>::setX)
				.and($)
				.and(mul, Struct2::setY)), Struct2::setY),
			t -> FormulaOperation.chainLeft(t.x, t.y)));
	}

	public static Syntax<Formula> compare;
	{
		compare = slot(() -> pack(serial(Struct2<Formula, ArrayList<Struct2<String, Formula>>>::new)
			.and(add, Struct2::setX)
			.and(repeat(serial(Struct2<String, Formula>::new)
				.and($)
				.and(or((String) null)
					.or(string(">="))
					.or(string("<="))
					.or(string(">"))
					.or(string("<"))
					.or(string("=="))
					.or(string("!=")),
					Struct2<String, Formula>::setX)
				.and($)
				.and(add, Struct2::setY)), Struct2::setY),
			t -> FormulaOperation.chainLeft(t.x, t.y)));
	}

	public static Syntax<Formula> and;
	{
		and = slot(() -> pack(serial(Struct2<Formula, ArrayList<Struct2<String, Formula>>>::new)
			.and(compare, Struct2::setX)
			.and(repeat(serial(Struct2<String, Formula>::new)
				.and($)
				.and(or((String) null)
					.or(string("&&")),
					Struct2<String, Formula>::setX)
				.and($)
				.and(compare, Struct2::setY)), Struct2::setY),
			t -> FormulaOperation.chainLeft(t.x, t.y)));
	}

	public static Syntax<Formula> or;
	{
		or = slot(() -> pack(serial(Struct2<Formula, ArrayList<Struct2<String, Formula>>>::new)
			.and(and, Struct2::setX)
			.and(repeat(serial(Struct2<String, Formula>::new)
				.and($)
				.and(or((String) null)
					.or(string("||")),
					Struct2<String, Formula>::setX)
				.and($)
				.and(and, Struct2::setY)), Struct2::setY),
			t -> FormulaOperation.chainLeft(t.x, t.y)));
	}

	public static Syntax<Formula> lambda;
	{
		lambda = slot(() -> or((Formula) null)
			.or(pack(serial(Struct3<String, Formula, Formula>::new)
				.and(named(regex("[a-zA-Z_][a-zA-Z_0-9]*"), "Identifier"), Struct3::setX)
				.and($)
				.and(string(":"))
				.and($)
				.and(or, Struct3::setY)
				.and($)
				.and(string("->"))
				.and($)
				.and(lambda, Struct3::setZ),
				s -> new FormulaLambda(s.z, new Tuple<>(s.x, s.y))))
			.or(or));
	}

	public static Syntax<Formula> expression;
	{
		expression = slot(() -> lambda);
	}

	public static Syntax<Formula> root;
	{
		root = slot(() -> extract((Formula) null)
			.and($)
			.extract(expression)
			.and($));
	}

	static {
		new Syntaxes1();
	}

}
