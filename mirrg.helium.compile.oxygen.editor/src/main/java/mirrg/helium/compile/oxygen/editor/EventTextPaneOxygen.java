package mirrg.helium.compile.oxygen.editor;

import mirrg.helium.compile.oxygen.parser.core.ResultOxygen;

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

		public static class NoError extends Syntax
		{

			public final ResultOxygen<?> result;

			public NoError(ResultOxygen<?> result)
			{
				this.result = result;
			}

		}

		public static class Failure extends NoError
		{

			public Failure(ResultOxygen<?> result)
			{
				super(result);
			}

		}

		public static class Success extends NoError
		{

			public Success(ResultOxygen<?> result)
			{
				super(result);
			}

			public static class Main extends Success
			{

				public Main(ResultOxygen<?> result)
				{
					super(result);
				}

			}

			public static class Proposal extends Success
			{

				public Proposal(ResultOxygen<?> result)
				{
					super(result);
				}

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
