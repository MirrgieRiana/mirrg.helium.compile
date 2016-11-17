package mirrg.helium.compile.oxygen.util.apatite.vm1;

import java.awt.Color;
import java.util.function.Function;

import mirrg.helium.compile.oxygen.util.apatite.ErrorReporter;
import mirrg.helium.compile.oxygen.util.apatite.FormulaNode;
import mirrg.helium.compile.oxygen.util.apatite.Type;
import mirrg.helium.compile.oxygen.util.apatite.VM;

public class FormulsLiteralString extends FormulaNode
{

	private Function<VM, Type> functionType;
	private String string;
	private int begin;
	private int end;

	public Type type;

	public FormulsLiteralString(Function<VM, Type> functionType, String string, int begin, int end)
	{
		this.functionType = functionType;
		this.string = string;
		this.begin = begin;
		this.end = end;
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
		return string;
	}

	@Override
	public int getBegin()
	{
		return begin;
	}

	@Override
	public int getEnd()
	{
		return end;
	}

	public Color getColor()
	{
		if (type == null) return Color.black;
		return type.getColor();
	}

}
