package mirrg.helium.compile.oxygen.parser;

import mirrg.helium.compile.oxygen.util.FrameSyntax;

public class SampleFrameSyntax
{

	public static void main(String[] args)
	{
		new FrameSyntax(
			Test1.test3_getSyntax(),
			"15/(26*158+pi-27)*(e/(7+45)/61)*5-27/7").setVisible(true);
	}

}
