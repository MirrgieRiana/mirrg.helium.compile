package mirrg.helium.compile.oxygen.util.apatite;

public abstract class FormulaVirtual extends Formula
{

	private int begin;
	private int end;

	public FormulaVirtual(int begin, int end)
	{
		this.begin = begin;
		this.end = end;
	}

	@Override
	public int getBegin()
	{
		return begin;
	}

	@Override
	public int getEnd()
	{
		return end;
	}

}
