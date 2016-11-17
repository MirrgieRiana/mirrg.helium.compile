package mirrg.helium.compile.oxygen.editor;

public class EventDialogProposal
{

	public static class Update extends EventDialogProposal
	{

		public final Proposal proposal;

		public Update(Proposal proposal)
		{
			this.proposal = proposal;
		}

	}

}
