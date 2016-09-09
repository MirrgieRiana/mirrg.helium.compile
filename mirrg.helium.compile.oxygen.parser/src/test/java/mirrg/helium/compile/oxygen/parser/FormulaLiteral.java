package mirrg.helium.compile.oxygen.parser;

import java.awt.Color;

import mirrg.helium.compile.oxygen.util.IColoredNode;

public class FormulaLiteral implements IFormula, IColoredNode
{

	public double value;
	public Color color;

	public FormulaLiteral(double value)
	{
		this(value, Color.black);
	}

	public FormulaLiteral(double value, Color color)
	{
		this.value = value;
		this.color = color;
	}

	@Override
	public double calculate()
	{
		return value;
	}

	@Override
	public Color getColor()
	{
		return color;
	}

}
