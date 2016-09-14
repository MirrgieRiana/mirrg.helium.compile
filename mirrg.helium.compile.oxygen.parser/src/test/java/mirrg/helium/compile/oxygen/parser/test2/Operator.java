package mirrg.helium.compile.oxygen.parser.test2;

import java.util.function.BiFunction;

public class Operator<A, B, R>
{

	public final String token;
	public final BiFunction<A, B, R> function;
	public final EnumType<A> left;
	public final EnumType<B> right;
	public final EnumType<R> result;

	public Operator(
		String token,
		BiFunction<A, B, R> function,
		EnumType<A> left,
		EnumType<B> right,
		EnumType<R> result)
	{
		this.token = token;
		this.function = function;
		this.left = left;
		this.right = right;
		this.result = result;
	}

	@Override
	public String toString()
	{
		return "`" + token + "`(" + left + ", " + right + ")";
	}

	public boolean isAssignableFrom(Operator<?, ?, ?> other)
	{
		if (!left.isAssignableFrom(other.left)) return false;
		if (!right.isAssignableFrom(other.right)) return false;
		return true;
	}

	public R apply(Runtime runtime, Formula formulaLeft, Formula formulaRight)
	{
		A a = EnumType.cast((EnumType<Object>) formulaLeft.getType(runtime.vm), left, formulaLeft.calculate(runtime));
		B b = EnumType.cast((EnumType<Object>) formulaRight.getType(runtime.vm), right, formulaRight.calculate(runtime));

		return function.apply(a, b);
	}

}
