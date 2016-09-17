package mirrg.helium.compile.oxygen.util.apatite.vm1;

import mirrg.helium.compile.oxygen.util.apatite.ErrorReporter;
import mirrg.helium.compile.oxygen.util.apatite.Formula;
import mirrg.helium.compile.oxygen.util.apatite.FormulaNode;
import mirrg.helium.compile.oxygen.util.apatite.Runtime;
import mirrg.helium.compile.oxygen.util.apatite.Type;
import mirrg.helium.compile.oxygen.util.apatite.VM;

public class FormulaLambda extends FormulaNode
{

	protected Formula formula;
	protected String arguments;

	public FormulaLambda(Formula formula, String arguments)
	{
		this.formula = formula;
		this.arguments = arguments;
	}

	@Override
	public Type<?> getType(VM vm)
	{
		return VM1.LAMBDA;
	}

	protected Lambda lambda;

	@Override
	public boolean validateImpl(VM vm, ErrorReporter errorReporter)
	{
		boolean flag = false;
		if (!formula.validateImpl(vm, errorReporter)) flag = true;
		if (flag) return false;

		vm.registerConstant(VM1.INTEGER, arguments, 0);

		lambda = new Lambda(arguments, formula);
		return true;
	}

	@Override
	public Object calculate(Runtime runtime)
	{
		return lambda;
	}

}
