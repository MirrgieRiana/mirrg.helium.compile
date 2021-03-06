package mirrg.helium.compile.oxygen.util.apatite;

public abstract class Formula
{

	/**
	 * このメソッドの前に必ず{@link #validate(VM)}を実行し、
	 * 値が真であるかを確認してください。
	 */
	public abstract Type getType();

	private ErrorReporter errorReporter;

	/**
	 * 戻り値の{@link ErrorReporter#isValid()}がfalseの場合、{@link #getType()}の値は未定義です。
	 * このメソッドの戻り値はキャッシュされます。
	 */
	public ErrorReporter validate(VM vm)
	{
		if (errorReporter == null) {
			errorReporter = new ErrorReporter();
			boolean res = validateImpl(vm, errorReporter);
			if (!res) {
				errorReporter.setInvalid();
				return errorReporter;
			}
		}
		return errorReporter;
	}

	/**
	 * falseを返した場合、{@link #getType()}の値は未定義です。
	 */
	public abstract boolean validateImpl(VM vm, ErrorReporter errorReporter);

	public abstract Object calculate();

	public abstract int getBegin();

	public abstract int getEnd();

}
