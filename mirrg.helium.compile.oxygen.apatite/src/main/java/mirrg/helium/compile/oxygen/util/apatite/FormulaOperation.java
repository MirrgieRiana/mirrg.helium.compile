package mirrg.helium.compile.oxygen.util.apatite;

import java.util.ArrayList;

import mirrg.helium.standard.hydrogen.struct.Struct2;

public class FormulaOperation extends Formula
{

	public final String token;
	public final Formula left;
	public final Formula right;

	protected Operator operator;

	public FormulaOperation(String token, Formula left, Formula right)
	{
		this.token = token;
		this.left = left;
		this.right = right;
	}

	@Override
	public Type getType()
	{
		return operator.result;
	}

	@Override
	public boolean validateImpl(VM vm, ErrorReporter errorReporter)
	{
		boolean flag = false;
		if (!left.validateImpl(vm, errorReporter)) flag = true;
		if (!right.validateImpl(vm, errorReporter)) flag = true;
		if (flag) return false;

		ArrayList<Operator> operator = vm.getOperator(token, left.getType(), right.getType());

		if (operator.isEmpty()) {
			errorReporter.report(this,
				"そのような演算子はありません: `" + token + "`(" + left.getType() + ", " + right.getType() + ")");
			return false;
		} else if (operator.size() > 1) {
			ArrayList<String> messages = new ArrayList<>();
			messages.add("呼び出し`" + token + "`(" + left.getType() + ", " + right.getType() + ")は曖昧です: ");

			operator.stream()
				.forEach(o -> messages.add("  " + o.toString()));

			errorReporter.report(this, String.join("\n", messages));
			return false;
		}

		this.operator = operator.get(0);
		return true;
	}

	@Override
	public Object calculate()
	{
		return operator.apply(left, right);
	}

	public static Formula chainLeft(Formula head, ArrayList<Struct2<String, Formula>> tail)
	{
		Formula formula = head;

		for (Struct2<String, Formula> t2 : tail) {
			formula = new FormulaOperation(t2.x, formula, t2.y);
		}

		return formula;
	}

	@Override
	public int getBegin()
	{
		return left.getBegin();
	}

	@Override
	public int getEnd()
	{
		return right.getEnd();
	}

}
