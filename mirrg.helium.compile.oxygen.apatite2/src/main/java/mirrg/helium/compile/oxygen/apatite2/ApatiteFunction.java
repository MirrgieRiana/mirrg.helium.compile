package mirrg.helium.compile.oxygen.apatite2;

import java.util.Optional;
import java.util.function.Function;

import mirrg.helium.compile.oxygen.apatite2.type.Type;
import mirrg.helium.standard.hydrogen.struct.Struct3;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class ApatiteFunction<T> implements IApatiteFunctionProvider
{

	private final Type<T> type;
	private final Type<?>[] argumentTypes;
	private final Function<Object[], T> function;

	public ApatiteFunction(Type<T> type, Function<Object[], T> function, Type<?>... argumentTypes)
	{
		this.type = type;
		this.argumentTypes = argumentTypes;
		this.function = function;
	}

	@Override
	public Optional<Struct3<Integer, Integer, IApatiteFunctionEntity>> matches(Type<?>... types)
	{
		int index = 0;
		int distance = 0;
		Function<?, ?>[] casters = new Function<?, ?>[types.length];

		if (types.length != argumentTypes.length) return Optional.empty();
		for (int i = 0; i < types.length; i++) {
			Optional<Tuple<Integer, Function<?, ?>>> res = types[i].getDistance(argumentTypes[i]);
			if (!res.isPresent()) return Optional.empty();
			if (res.get().getX() > 0) {
				index = i;
				distance = res.get().getX();
				casters[i] = res.get().getY();
			}
		}

		return Optional.of(new Struct3<>(index, distance, new IApatiteFunctionEntity() {

			@Override
			public Type<?> getType()
			{
				return type;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Object invoke(Object... arguments)
			{
				for (int i = 0; i < arguments.length; i++) {
					if (casters[i] != null) arguments[i] = ((Function<Object, Object>) casters[i]).apply(arguments[i]);
				}
				return function.apply(arguments);
			}

		}));
	}

}
