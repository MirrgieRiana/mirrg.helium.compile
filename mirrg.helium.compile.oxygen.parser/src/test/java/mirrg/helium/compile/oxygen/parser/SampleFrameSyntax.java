package mirrg.helium.compile.oxygen.parser;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import mirrg.helium.compile.oxygen.util.PanelSyntax;

public class SampleFrameSyntax
{

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();

		frame.setLayout(new CardLayout());
		PanelSyntax panel = new PanelSyntax(Test1.test3_getSyntax());
		panel.set("15/(26*158+pi-27)*(e/(7+45)/61)*5-27/7");
		frame.add(panel);

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

}
