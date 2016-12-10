package mirrg.helium.compile.oxygen.apatite2;

public abstract class ApatiteFunctionProviderBase implements IApatiteFunctionProvider
{

	private final String name;

	public ApatiteFunctionProviderBase(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

}
