package mirrg.helium.compile.oxygen.util.apatite.vm1;

import mirrg.helium.compile.oxygen.util.apatite.ErrorReporter;
import mirrg.helium.compile.oxygen.util.apatite.Formula;
import mirrg.helium.compile.oxygen.util.apatite.FormulaNode;
import mirrg.helium.compile.oxygen.util.apatite.Type;
import mirrg.helium.compile.oxygen.util.apatite.VM;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class FormulaLambda extends FormulaNode
{

	protected Formula formula;
	protected Tuple<String, Formula> arguments;

	protected Type type;

	public FormulaLambda(Formula formula, Tuple<String, Formula> arguments)
	{
		this.formula = formula;
		this.arguments = arguments;
	}

	@Override
	public Type getType()
	{
		return type;
	}

	protected Lambda lambda;

	@Override
	public boolean validateImpl(VM vm, ErrorReporter errorReporter)
	{
		vm.pushFrame();
		try {
			{
				boolean flag = false;
				if (!arguments.getY().validateImpl(vm, errorReporter)) flag = true;
				if (flag) return false;
			}

			vm.frame.define(arguments.getX(), (Type) arguments.getY().calculate());

			{
				boolean flag = false;
				if (!formula.validateImpl(vm, errorReporter)) flag = true;
				if (flag) return false;
			}

			lambda = new Lambda(arguments, formula);
			type = vm.getBaseType("Lambda").apply((Type) arguments.getY().calculate(), formula.getType());
			return true;
		} finally {
			vm.popFrame();
		}

	}

	@Override
	public Object calculate()
	{
		return lambda;
	}

}
