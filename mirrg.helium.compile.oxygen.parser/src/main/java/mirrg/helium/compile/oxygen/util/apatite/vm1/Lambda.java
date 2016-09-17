package mirrg.helium.compile.oxygen.util.apatite.vm1;

import mirrg.helium.compile.oxygen.util.apatite.Formula;
import mirrg.helium.compile.oxygen.util.apatite.Runtime;

public class Lambda
{

	private final String argumentEntries;
	private final Formula formula;

	public Lambda(String argumentEntries, Formula formula)
	{
		this.argumentEntries = argumentEntries;
		this.formula = formula;
	}

	public Object apply(Runtime runtime, Object arguments)
	{
		runtime.vm.registerConstant(VM1.INTEGER, argumentEntries, (int) arguments);
		return formula.calculate(runtime);
	}

}
