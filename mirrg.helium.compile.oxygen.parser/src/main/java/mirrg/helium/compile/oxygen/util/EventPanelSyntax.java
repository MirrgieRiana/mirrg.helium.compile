package mirrg.helium.compile.oxygen.util;

import javax.swing.JTextPane;

import mirrg.helium.compile.oxygen.parser.core.Node;

public class EventPanelSyntax
{

	public static class UserEdit extends EventPanelSyntax
	{

		public final String source;

		public UserEdit(String source)
		{
			this.source = source;
		}

	}

	public static class Parsed extends EventPanelSyntax
	{

		public final Node<?> node;
		public final int timing;
		public static final int TIMING_USER_EDIT = 0;
		public static final int TIMING_BEFORE_PROPOSAL = 1;

		public Parsed(Node<?> node, int timing)
		{
			this.node = node;
			this.timing = timing;
		}

	}

	public static class AfterHighlight extends EventPanelSyntax
	{

		public final JTextPane textPane;
		public final Node<?> node;

		public AfterHighlight(JTextPane textPane, Node<?> node)
		{
			this.textPane = textPane;
			this.node = node;
		}

	}

}
