package mirrg.helium.compile.oxygen.util.apatite.vm1;

import mirrg.helium.compile.oxygen.util.apatite.ErrorReporter;
import mirrg.helium.compile.oxygen.util.apatite.Formula;
import mirrg.helium.compile.oxygen.util.apatite.FormulaNode;
import mirrg.helium.compile.oxygen.util.apatite.Runtime;
import mirrg.helium.compile.oxygen.util.apatite.Type;
import mirrg.helium.compile.oxygen.util.apatite.VM;

public class FormulaApply extends FormulaNode
{

	private Formula formulaLambda;
	private Formula formulaArguments;

	public FormulaApply(Formula formulaLambda, Formula formulaArguments)
	{
		this.formulaLambda = formulaLambda;
		this.formulaArguments = formulaArguments;
	}

	@Override
	public Type<?> getType(VM vm)
	{
		return VM1.INTEGER;
	}

	@Override
	public boolean validateImpl(VM vm, ErrorReporter errorReporter)
	{
		boolean flag = false;
		if (!formulaLambda.validateImpl(vm, errorReporter)) flag = true;
		if (!formulaArguments.validateImpl(vm, errorReporter)) flag = true;
		if (flag) return false;

		return true;
	}

	@Override
	public Object calculate(Runtime runtime)
	{
		Object lambda = formulaLambda.calculate(runtime);
		Object arguments = formulaArguments.calculate(runtime);

		return ((Lambda) lambda).apply(runtime, arguments);
	}

}
