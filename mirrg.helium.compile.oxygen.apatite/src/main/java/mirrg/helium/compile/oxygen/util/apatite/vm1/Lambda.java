package mirrg.helium.compile.oxygen.util.apatite.vm1;

import mirrg.helium.compile.oxygen.util.apatite.Formula;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class Lambda
{

	public final Tuple<String, Formula> argumentEntries;
	public final Formula formula;

	public Lambda(Tuple<String, Formula> argumentEntries, Formula formula)
	{
		this.argumentEntries = argumentEntries;
		this.formula = formula;
	}

	public Object apply(Object arguments)
	{
		return formula.calculate();
	}

}
