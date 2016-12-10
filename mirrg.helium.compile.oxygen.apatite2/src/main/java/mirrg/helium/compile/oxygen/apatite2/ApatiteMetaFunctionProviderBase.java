package mirrg.helium.compile.oxygen.apatite2;

public abstract class ApatiteMetaFunctionProviderBase implements IApatiteMetaFunctionProvider
{

	private String name;

	public ApatiteMetaFunctionProviderBase(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

}
