package mirrg.helium.compile.oxygen.parser.test2;

public abstract class Formula
{

	/**
	 * このメソッドの前に必ず{@link #validate(VM, ErrorReporter)}を実行し、
	 * 値が真であるかを確認してください。
	 */
	public abstract EnumType<?> getType(VM vm);

	private ErrorReporter errorReporter;

	/**
	 * 戻り値の{@link ErrorReporter#isValid()}がfalseの場合、{@link #getType(VM)}の値は未定義です。
	 * このメソッドの戻り値はキャッシュされます。
	 */
	public ErrorReporter validate(VM vm)
	{
		if (errorReporter == null) {
			errorReporter = new ErrorReporter();
			validateImpl(vm, errorReporter);
		}
		return errorReporter;
	}

	/**
	 * falseを返した場合、{@link #getType(VM)}の値は未定義です。
	 */
	protected abstract boolean validateImpl(VM vm, ErrorReporter errorReporter);

	public abstract Object calculate(Runtime runtime);

	public abstract int getBegin();

	public abstract int getEnd();

}
