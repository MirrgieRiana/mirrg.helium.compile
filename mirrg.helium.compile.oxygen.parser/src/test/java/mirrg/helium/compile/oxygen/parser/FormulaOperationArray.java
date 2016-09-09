package mirrg.helium.compile.oxygen.parser;

import java.util.ArrayList;

import mirrg.helium.standard.hydrogen.struct.Struct2;

public class FormulaOperationArray implements IFormula
{

	public IFormula left;
	public ArrayList<Struct2<IFunction, IFormula>> right;

	@Override
	public double calculate()
	{
		double value = left.calculate();
		for (Struct2<IFunction, IFormula> a : right) {
			value = a.x.apply(value, a.y.calculate());
		}
		return value;
	}

}
