package mirrg.helium.compile.oxygen.apatite2.type;

import java.awt.Color;

import mirrg.helium.compile.oxygen.apatite2.ApatiteLoader;

public class TypeBasic<T> extends TypeBase<T>
{

	public TypeBasic(String name, Color color)
	{
		super(name, color);
		registerDistance(ApatiteLoader.VALUE, 1);
	}

}
