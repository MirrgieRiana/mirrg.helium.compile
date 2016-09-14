package mirrg.helium.compile.oxygen.parser.test2;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;
import static mirrg.helium.compile.oxygen.util.WithColor.*;
import static mirrg.helium.compile.oxygen.util.WithProposal.*;

import java.util.ArrayList;

import org.apache.commons.math3.complex.Complex;

import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.standard.hydrogen.struct.Struct2;
import mirrg.helium.standard.hydrogen.struct.Struct3;

public class Syntaxes
{

	public static Syntax<String> $;
	{
		$ = slot(() -> regex("[ \\t\\r\\n]*"));
	}

	public static Syntax<Formula> literalIntegerImaginary;
	{
		literalIntegerImaginary = slot(() -> withColor(pack(extract((String) null)
			.extract(regex("[0-9]+"))
			.and(string("i")),
			s -> new FormulaNode() {

				@Override
				public Type<?> getType(VM vm)
				{
					return VM.COMPLEX;
				}

				@Override
				protected boolean validateImpl(VM vm, ErrorReporter errorReporter)
				{
					return true;
				}

				@Override
				public Object calculate(Runtime runtime)
				{
					return new Complex(0, Integer.parseInt(s, 10));
				}

			}),
			s -> VM.COMPLEX.color));
	}

	public static Syntax<Formula> literalDoubleImaginary;
	{
		literalDoubleImaginary = slot(() -> withColor(pack(extract((String) null)
			.extract(regex("[0-9]+\\.[0-9]+"))
			.and(string("i")),
			s -> new FormulaNode() {

				@Override
				public Type<?> getType(VM vm)
				{
					return VM.COMPLEX;
				}

				@Override
				protected boolean validateImpl(VM vm, ErrorReporter errorReporter)
				{
					return true;
				}

				@Override
				public Object calculate(Runtime runtime)
				{
					return new Complex(0, Double.parseDouble(s));
				}

			}),
			s -> VM.COMPLEX.color));
	}

	public static Syntax<Formula> literalInteger;
	{
		literalInteger = slot(() -> withColor(pack(regex("[0-9]+"),
			s -> new FormulaNode() {

				@Override
				public Type<?> getType(VM vm)
				{
					return VM.INTEGER;
				}

				@Override
				protected boolean validateImpl(VM vm, ErrorReporter errorReporter)
				{
					return true;
				}

				@Override
				public Object calculate(Runtime runtime)
				{
					return Integer.parseInt(s, 10);
				}

			}),
			s -> VM.INTEGER.color));
	}

	public static Syntax<Formula> literalDouble;
	{
		literalDouble = slot(() -> withColor(pack(regex("[0-9]+\\.[0-9]+"),
			s -> new FormulaNode() {

				@Override
				public Type<?> getType(VM vm)
				{
					return VM.DOUBLE;
				}

				@Override
				protected boolean validateImpl(VM vm, ErrorReporter errorReporter)
				{
					return true;
				}

				@Override
				public Object calculate(Runtime runtime)
				{
					return Double.parseDouble(s);
				}

			}),
			s -> VM.DOUBLE.color));
	}

	public static Syntax<Formula> literalString;
	{
		literalString = slot(() -> withColor(pack(or((Struct3<Node<?>, String, Node<?>>) null)
			.or(serial(Struct3<Node<?>, String, Node<?>>::new)
				.and(packNode(string("\""), n -> n), Struct3::setX)
				.and(regex("[^\"]*"), Struct3::setY)
				.and(packNode(string("\""), n -> n), Struct3::setZ))
			.or(serial(Struct3<Node<?>, String, Node<?>>::new)
				.and(packNode(string("'"), n -> n), Struct3::setX)
				.and(regex("[^']*"), Struct3::setY)
				.and(packNode(string("'"), n -> n), Struct3::setZ)),
			s -> new Formula() {

				@Override
				public Type<?> getType(VM vm)
				{
					return VM.STRING;
				}

				@Override
				protected boolean validateImpl(VM vm, ErrorReporter errorReporter)
				{
					return true;
				}

				@Override
				public Object calculate(Runtime runtime)
				{
					return s.y;
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

			}),
			s -> VM.STRING.color));
	}

	public static Syntax<Formula> literalIdentifier;
	{
		literalIdentifier = slot(() -> wrap(withProposal(withColor(pack(regex("[a-zA-Z_][a-zA-Z_0-9]*"),
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
				public Type<?> getType(VM vm)
				{
					return s.y.getType(vm);
				}

				@Override
				protected boolean validateImpl(VM vm, ErrorReporter errorReporter)
				{
					return s.y.validateImpl(vm, errorReporter);
				}

				@Override
				public Object calculate(Runtime runtime)
				{
					return s.y.calculate(runtime);
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
			.or(literalDoubleImaginary)
			.or(literalIntegerImaginary)
			.or(literalDouble)
			.or(literalInteger)
			.or(literalString)
			.or(literalIdentifier)
			.or(bracket));
	}

	public static Syntax<Formula> mul;
	{
		mul = slot(() -> pack(serial(Struct2<Formula, ArrayList<Struct2<String, Formula>>>::new)
			.and(factor, Struct2::setX)
			.and(repeat(serial(Struct2<String, Formula>::new)
				.and($)
				.and(or((String) null)
					.or(string("*"))
					.or(string("/"))
					.or(string("%")),
					Struct2<String, Formula>::setX)
				.and($)
				.and(factor, Struct2::setY)), Struct2::setY),
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

	public static Syntax<Formula> expression;
	{
		expression = slot(() -> or);
	}

	public static Syntax<Formula> root;
	{
		root = slot(() -> extract((Formula) null)
			.and($)
			.extract(expression)
			.and($));
	}

	static {
		new Syntaxes();
	}

}
