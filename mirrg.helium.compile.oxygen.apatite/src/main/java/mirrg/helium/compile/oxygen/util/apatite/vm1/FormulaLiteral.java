package mirrg.helium.compile.oxygen.util.apatite.vm1;

import java.awt.Color;
import java.util.function.Function;
import java.util.function.Supplier;

import mirrg.helium.compile.oxygen.util.apatite.ErrorReporter;
import mirrg.helium.compile.oxygen.util.apatite.FormulaNode;
import mirrg.helium.compile.oxygen.util.apatite.Type;
import mirrg.helium.compile.oxygen.util.apatite.VM;

public class FormulaLiteral extends FormulaNode
{

	private Function<VM, Type> functionType;
	private Supplier<Object> supplierValue;

	public Type type;

	public FormulaLiteral(Function<VM, Type> functionType, Supplier<Object> supplierValue)
	{
		this.functionType = functionType;
		this.supplierValue = supplierValue;
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public boolean validateImpl(VM vm, ErrorReporter errorReporter)
	{
		type = functionType.apply(vm);
		return true;
	}

	@Override
	public Object calculate()
	{
		return supplierValue.get();
	}

	public Color getColor()
	{
		if (type == null) return Color.black;
		return type.getColor();
	}

}
