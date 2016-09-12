package mirrg.helium.compile.oxygen.util;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.CardLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JList;

import mirrg.helium.standard.hydrogen.event.EventManager;

public class DialogProposal extends JDialog
{

	public static class EventDialogProposal
	{

		public static class Update extends DialogProposal.EventDialogProposal
		{

			public String value;

			public Update(String value)
			{
				this.value = value;
			}

		}

	}

	public final EventManager<DialogProposal.EventDialogProposal> eventManager = new EventManager<>();

	public DialogProposal(Vector<String> proposals)
	{
		setAutoRequestFocus(false);
		setAlwaysOnTop(true);
		setType(Window.Type.UTILITY);

		setLayout(new CardLayout());
		add(createScrollPane(get(() -> {
			JList<String> list = new JList<>(proposals);
			list.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e)
				{

				}

				@Override
				public void keyReleased(KeyEvent e)
				{
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						dispose();
					}
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						onAction(list);
					}
				}

				@Override
				public void keyPressed(KeyEvent e)
				{

				}

			});
			list.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e)
				{

				}

				@Override
				public void mousePressed(MouseEvent e)
				{

				}

				@Override
				public void mouseExited(MouseEvent e)
				{

				}

				@Override
				public void mouseEntered(MouseEvent e)
				{

				}

				@Override
				public void mouseClicked(MouseEvent e)
				{
					if (e.getClickCount() == 2) {
						onAction(list);
					}
				}
			});
			return list;
		}), 200, 200));
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e)
			{

			}

			@Override
			public void windowIconified(WindowEvent e)
			{

			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{

			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
				dispose();
			}

			@Override
			public void windowClosing(WindowEvent e)
			{

			}

			@Override
			public void windowClosed(WindowEvent e)
			{

			}

			@Override
			public void windowActivated(WindowEvent e)
			{

			}

		});
	}

	protected void onAction(JList<String> list)
	{
		String selectedValue = list.getSelectedValue();
		if (selectedValue != null) {
			eventManager.post(new EventDialogProposal.Update(selectedValue));
			dispose();
		}
	}

}
