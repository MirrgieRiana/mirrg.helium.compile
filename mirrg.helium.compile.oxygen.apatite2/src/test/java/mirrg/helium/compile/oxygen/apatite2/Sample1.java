package mirrg.helium.compile.oxygen.apatite2;

import javax.swing.WindowConstants;

import mirrg.helium.compile.oxygen.apatite2.util.EventFrameApatite;
import mirrg.helium.compile.oxygen.apatite2.util.FrameApatite;

public class Sample1
{

	public static void main(String[] args)
	{
		FrameApatite frame = new FrameApatite(ApatiteScript.getSyntax(), "1 + 2 * (4 + 6)");
		frame.setTitle("Apatite Script");

		frame.event().register(EventFrameApatite.ChangeSource.class, e -> System.out.println(e.source));

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
