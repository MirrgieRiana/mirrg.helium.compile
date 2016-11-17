package mirrg.helium.compile.oxygen.util.apatite.vm1;

import java.util.function.Supplier;

import mirrg.helium.compile.oxygen.util.apatite.BaseType;
import mirrg.helium.compile.oxygen.util.apatite.ErrorReporter;
import mirrg.helium.compile.oxygen.util.apatite.Formula;
import mirrg.helium.compile.oxygen.util.apatite.FormulaNode;
import mirrg.helium.compile.oxygen.util.apatite.Type;
import mirrg.helium.compile.oxygen.util.apatite.VM;

public class FormulaBracketsRight extends FormulaNode
{

	private Formula formulaLeft;
	private Formula formulaRight;

	private Type type;
	private Supplier<Object> supplier;

	public FormulaBracketsRight(Formula formulaLeft, Formula formulaRight)
	{
		this.formulaLeft = formulaLeft;
		this.formulaRight = formulaRight;
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public boolean validateImpl(VM vm, ErrorReporter errorReporter)
	{
		boolean flag = false;
		if (!formulaLeft.validateImpl(vm, errorReporter)) flag = true;
		if (!formulaRight.validateImpl(vm, errorReporter)) flag = true;
		if (flag) return false;

		Type type = formulaLeft.getType();
		if (vm.getBaseType("Lambda").apply(/* TODO */).isAssignableFrom(type)) {
			this.type = vm.getBaseType("Lambda").apply();
			supplier = () -> ((Lambda) formulaLeft.calculate()).apply(formulaRight.calculate());
			return true;
		} else if (vm.getBaseType("BaseType").apply().isAssignableFrom(type)) {
			this.type = type;
			supplier = () -> ((BaseType) formulaLeft.calculate()).apply(/* TODO */);
			return true;
		} else {
			errorReporter.report(this, "Illegal BracketsRight Left Term: " + type);
			return false;
		}
	}

	@Override
	public Object calculate()
	{
		return supplier.get();
	}

}
