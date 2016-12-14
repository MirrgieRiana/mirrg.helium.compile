package mirrg.helium.compile.oxygen.apatite2.type;

import java.awt.Color;

import mirrg.helium.swing.nitrogen.util.HColor;

public class TypeArray<T> extends TypeBasic<T[]>
{

	public final IType<T> type;

	public TypeArray(IType<T> type)
	{
		super("Array(" + type.getName() + ")", HColor.createLinearRatioColor(0.75, type.getColor(), Color.decode("#0000ff")));
		this.type = type;
	}

}
