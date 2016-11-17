package mirrg.helium.compile.oxygen.util.apatite.vm1;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import mirrg.helium.compile.oxygen.util.apatite.PanelApatite;

public class SampleVM1
{

	private static PanelApatite panel;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();

		VM1 vm = new VM1();
		vm.registerConstant(vm.STRING, "lineSeparator", "<br>");

		frame.setLayout(new CardLayout());
		frame.add(panel = new PanelApatite(Syntaxes1.root, vm));
		panel.getPanelSyntax().setText("pi * 200");
		//pi * 200 + (3 * 4 -  3) * "214314" + pi * e - 4 * "a"

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

}
