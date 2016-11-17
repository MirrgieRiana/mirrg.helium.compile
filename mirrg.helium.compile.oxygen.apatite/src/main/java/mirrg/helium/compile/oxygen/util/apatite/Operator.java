package mirrg.helium.compile.oxygen.util.apatite;

public class Operator
{

	public final String token;
	public final IFunction function;
	public final Type left;
	public final Type right;
	public final Type result;

	public Operator(
		String token,
		IFunction function,
		Type left,
		Type right,
		Type result)
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

	public boolean isAssignableFrom(Operator other)
	{
		if (!left.isAssignableFrom(other.left)) return false;
		if (!right.isAssignableFrom(other.right)) return false;
		return true;
	}

	public Object apply(Formula formulaLeft, Formula formulaRight)
	{
		return function.apply(
			Type.cast(left, formulaLeft.getType(), formulaLeft.calculate()),
			Type.cast(right, formulaRight.getType(), formulaRight.calculate()));
	}

	public static interface IFunction
	{

		public Object apply(Object a, Object b);

	}

	public boolean isSameSignature(Operator other)
	{
		if (!token.equals(other.token)) return false;
		if (!left.equals(other.left)) return false;
		if (!right.equals(other.right)) return false;
		return true;
	}

}
