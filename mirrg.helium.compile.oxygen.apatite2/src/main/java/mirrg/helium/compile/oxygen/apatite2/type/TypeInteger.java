package mirrg.helium.compile.oxygen.apatite2.type;

import java.awt.Color;

import mirrg.helium.compile.oxygen.apatite2.ApatiteLoader;

public class TypeInteger extends TypeBase<Integer>
{

	public TypeInteger(String name, Color color)
	{
		super(name, color);
		registerDistance(ApatiteLoader.VALUE, 2);
		registerDistance(ApatiteLoader.DOUBLE, 1, a -> (double) a);
	}

}
