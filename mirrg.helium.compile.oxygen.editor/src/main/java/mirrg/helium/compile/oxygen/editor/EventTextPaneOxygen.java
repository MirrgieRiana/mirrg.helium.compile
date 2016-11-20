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

		public static final int TIMING_MAIN = 0;
		public static final int TIMING_PROPOSAL = 1;

		public final int timing;

		public Syntax(int timing)
		{
			this.timing = timing;
		}

		public static class Error extends Syntax
		{

			public final Exception exception;

			public Error(int timing, Exception exception)
			{
				super(timing);
				this.exception = exception;
			}

		}

		public static class NoError extends Syntax
		{

			public final ResultOxygen<?> result;

			public NoError(int timing, ResultOxygen<?> result)
			{
				super(timing);
				this.result = result;
			}

		}

		public static class Failure extends NoError
		{

			public Failure(int timing, ResultOxygen<?> result)
			{
				super(timing, result);
			}

		}

		public static class Success extends NoError
		{

			public Success(int timing, ResultOxygen<?> result)
			{
				super(timing, result);
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
