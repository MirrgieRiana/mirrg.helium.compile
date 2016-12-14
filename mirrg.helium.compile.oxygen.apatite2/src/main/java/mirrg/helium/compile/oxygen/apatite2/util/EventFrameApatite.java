package mirrg.helium.compile.oxygen.apatite2.util;

import javax.swing.JTextPane;

import mirrg.helium.compile.oxygen.apatite2.core.ApatiteVM;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;
import mirrg.helium.compile.oxygen.parser.core.ResultOxygen;

public abstract class EventFrameApatite
{

	public final TextPaneApatite textPaneIn;
	public final JTextPane textPaneOut;

	public EventFrameApatite(TextPaneApatite textPaneIn, JTextPane textPaneOut)
	{
		this.textPaneIn = textPaneIn;
		this.textPaneOut = textPaneOut;
	}

	public static class ChangeSource extends EventFrameApatite
	{

		public final String source;

		public ChangeSource(TextPaneApatite textPaneIn, JTextPane textPaneOut, String source)
		{
			super(textPaneIn, textPaneOut);
			this.source = source;
		}

	}

	public static class SyntaxError extends EventFrameApatite
	{

		public final ResultOxygen<?> result;

		public SyntaxError(TextPaneApatite textPaneIn, JTextPane textPaneOut, ResultOxygen<?> result)
		{
			super(textPaneIn, textPaneOut);
			this.result = result;
		}

	}

	public static class SyntaxException extends EventFrameApatite
	{

		public final Exception exception;

		public SyntaxException(TextPaneApatite textPaneIn, JTextPane textPaneOut, Exception exception)
		{
			super(textPaneIn, textPaneOut);
			this.exception = exception;
		}

	}

	public abstract static class SyntaxSuccess extends EventFrameApatite
	{

		public final ApatiteVM vm;

		public SyntaxSuccess(TextPaneApatite textPaneIn, JTextPane textPaneOut, ApatiteVM vm)
		{
			super(textPaneIn, textPaneOut);
			this.vm = vm;
		}

	}

	public static class CompileError extends SyntaxSuccess
	{

		public CompileError(TextPaneApatite textPaneIn, JTextPane textPaneOut, ApatiteVM vm)
		{
			super(textPaneIn, textPaneOut, vm);
		}

	}

	public abstract static class CompileSuccess extends SyntaxSuccess
	{

		public final IApatiteScript script;

		public CompileSuccess(TextPaneApatite textPaneIn, JTextPane textPaneOut, ApatiteVM vm, IApatiteScript script)
		{
			super(textPaneIn, textPaneOut, vm);
			this.script = script;
		}

	}

	public static class RuntimeError extends CompileSuccess
	{

		public final Exception exception;

		public RuntimeError(TextPaneApatite textPaneIn, JTextPane textPaneOut, ApatiteVM vm, IApatiteScript script, Exception exception)
		{
			super(textPaneIn, textPaneOut, vm, script);
			this.exception = exception;
		}

	}

	public static class Success extends CompileSuccess
	{

		public final Object value;

		public Success(TextPaneApatite textPaneIn, JTextPane textPaneOut, ApatiteVM vm, IApatiteScript script, Object value)
		{
			super(textPaneIn, textPaneOut, vm, script);
			this.value = value;
		}

	}

}
