package mirrg.helium.compile.oxygen.util.apatite;

import java.util.Hashtable;

public class Runtime
{

	public final VM vm;

	public Hashtable<String, Object> variables = new Hashtable<>();

	public Runtime(VM vm)
	{
		this.vm = vm;
	}

}
