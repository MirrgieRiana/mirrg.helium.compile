package mirrg.helium.compile.oxygen.editor;

public class EventTextPaneOxygen
{

	public static class ChangeSource extends EventTextPaneOxygen
	{

	}

	public static class Update extends EventTextPaneOxygen
	{

	}

	public static class Syntax extends EventTextPaneOxygen
	{

		public static class Error extends Syntax
		{

			public final Exception exception;

			public Error(Exception exception)
			{
				this.exception = exception;
			}

		}

		public static class Failure extends Syntax
		{

		}

		public static class Success extends Syntax
		{

			public static class Main extends Success
			{

			}

			public static class Proposal extends Success
			{

			}

		}

	}

	public static class Highlight extends EventTextPaneOxygen
	{

		public static class Pre extends Highlight
		{

		}

		public static class Cleared extends Highlight
		{

		}

		public static class Post extends Highlight
		{

		}

	}

}
