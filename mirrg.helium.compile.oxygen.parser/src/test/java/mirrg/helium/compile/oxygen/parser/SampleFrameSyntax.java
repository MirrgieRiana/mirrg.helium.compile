package mirrg.helium.compile.oxygen.parser;

import static mirrg.helium.compile.oxygen.parser.HParserOxygen.*;

import mirrg.helium.compile.oxygen.util.FrameSyntax;

public class SampleFrameSyntax
{

	public static void main(String[] args)
	{
		String text = "15/(26*158+pi-27)*(e/(7+45)/61)*5-27/7";
		new FrameSyntax(parse(Test1.test3_getSyntax(), text), text).setVisible(true);
	}

}
