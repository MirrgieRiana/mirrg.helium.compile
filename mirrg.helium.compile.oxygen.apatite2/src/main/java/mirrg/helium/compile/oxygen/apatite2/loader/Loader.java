package mirrg.helium.compile.oxygen.apatite2.loader;

import java.awt.Color;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.math3.util.FastMath;

import mirrg.helium.compile.oxygen.apatite2.ApatiteConstant;
import mirrg.helium.compile.oxygen.apatite2.ApatiteFunction;
import mirrg.helium.compile.oxygen.apatite2.ApatiteFunctionProviderBase;
import mirrg.helium.compile.oxygen.apatite2.ApatiteMetaFunctionProviderBase;
import mirrg.helium.compile.oxygen.apatite2.ApatiteVM;
import mirrg.helium.compile.oxygen.apatite2.IApatiteFunctionEntity;
import mirrg.helium.compile.oxygen.apatite2.IApatiteMetaFunctionEntity;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.apatite2.nodes.NodeFunction;
import mirrg.helium.compile.oxygen.apatite2.nodes.NodeIdentifier;
import mirrg.helium.compile.oxygen.apatite2.type.Type;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.standard.hydrogen.struct.Struct3;

public class Loader
{

	public static final Type<Object> VALUE = new Type<>("value", Color.decode("#8888ff"));
	public static final Type<Integer> INTEGER = new Type<>("integer", Color.decode("#ff8800"));
	public static final Type<Double> DOUBLE = new Type<>("double", Color.decode("#000088"));
	public static final Type<StructureComplex> COMPLEX = new Type<>("complex", Color.decode("#880000"));
	public static final Type<String> STRING = new Type<>("string", Color.decode("#ff00ff"));
	public static final Type<String> KEYWORD = new Type<>("keyword", Color.decode("#00aa00"));
	public static final Type<Boolean> BOOLEAN = new Type<>("boolean", Color.decode("#888800"));
	public static final Type<Type<?>> TYPE = new Type<>("type", Color.decode("#00aa00"));

	public static final Type<Object> V = VALUE;
	public static final Type<Integer> I = INTEGER;
	public static final Type<Double> D = DOUBLE;
	public static final Type<StructureComplex> C = COMPLEX;
	public static final Type<String> S = STRING;
	public static final Type<String> K = KEYWORD;
	public static final Type<Boolean> B = BOOLEAN;
	public static final Type<Type<?>> T = TYPE;

	public static ApatiteVM createVM()
	{
		ApatiteVM vm = new ApatiteVM();
		load(vm);
		return vm;
	}

	public static void load(ApatiteVM vm)
	{
		new Loader(vm).load();
	}

	//

	protected ApatiteVM vm;

	protected Loader(ApatiteVM vm)
	{
		this.vm = vm;
	}

	public void load()
	{

		INTEGER.registerDistance(VALUE, 2, a -> a);
		INTEGER.registerDistance(DOUBLE, 1, a -> (double) a);
		DOUBLE.registerDistance(VALUE, 1, a -> a);
		STRING.registerDistance(VALUE, 1, a -> a);
		KEYWORD.registerDistance(VALUE, 1, a -> a);
		BOOLEAN.registerDistance(VALUE, 1, a -> a);
		TYPE.registerDistance(VALUE, 1, a -> a);

		rc("true", B, true);
		rc("false", B, false);
		rf0("now", D, () -> (double) System.currentTimeMillis());
		rf1("sin", D, D, Math::sin);
		rf1("cos", D, D, Math::cos);
		rf1("tan", D, D, Math::tan);

		rf1("type", T, I, a -> I);
		rf1("type", T, D, a -> D);
		rf1("type", T, S, a -> S);
		rf1("type", T, B, a -> B);
		rf1("type", T, T, a -> T);

		vm.registerMetaFunction(new ApatiteMetaFunctionProviderBase("_rightBracketsRound") {

			@Override
			public Optional<IApatiteMetaFunctionEntity> matches(IApatiteCode... codes)
			{
				if (codes.length < 1) return Optional.empty();
				if (!(codes[0] instanceof NodeIdentifier)) return Optional.empty();
				String name = ((NodeIdentifier) codes[0]).name;

				IApatiteCode[] codes2 = Stream.of(codes)
					.skip(1)
					.flatMap(c -> {
						if ((c instanceof NodeFunction) && ((NodeFunction) c).name.equals("_enumerateComma")) {
							return Stream.of(((NodeFunction) c).codes);
						}
						return Stream.of(c);
					})
					.toArray(IApatiteCode[]::new);

				return Optional.of((begin, end, vm) -> new NodeFunction(name, begin, end, codes2).validate(vm));
			}

		});

		rf1("_leftPlus", I, I, a -> a);
		rf1("_leftPlus", D, D, a -> a);
		rf1("_leftMinus", I, I, a -> -a);
		rf1("_leftMinus", D, D, a -> -a);
		rf1("_leftExclamation", B, B, a -> !a);

		rf2("_operatorHat", D, D, I, (a, b) -> FastMath.pow(a, b));
		rf2("_operatorHat", D, D, D, (a, b) -> FastMath.pow(a, b));

		rf2("_operatorAsterisk", I, I, I, (a, b) -> a * b);
		rf2("_operatorAsterisk", D, D, D, (a, b) -> a * b);
		rf2("_operatorSlash", I, I, I, (a, b) -> a / b);
		rf2("_operatorSlash", D, D, D, (a, b) -> a / b);

		rf2("_operatorPercent", I, I, I, (a, b) -> a % b);

		rf2("_operatorPlus", I, I, I, (a, b) -> a + b);
		rf2("_operatorPlus", D, D, D, (a, b) -> a + b);
		rf2("_operatorPlus", S, V, V, (a, b) -> "" + a + b);
		rf2("_operatorMinus", I, I, I, (a, b) -> a - b);
		rf2("_operatorMinus", D, D, D, (a, b) -> a - b);

		rf2("_operatorLessEqual", B, I, I, (a, b) -> a <= b);
		rf2("_operatorLessEqual", B, D, D, (a, b) -> a <= b);
		rf2("_operatorLess", B, I, I, (a, b) -> a < b);
		rf2("_operatorLess", B, D, D, (a, b) -> a < b);
		rf2("_operatorGreaterEqual", B, I, I, (a, b) -> a >= b);
		rf2("_operatorGreaterEqual", B, D, D, (a, b) -> a >= b);
		rf2("_operatorGreater", B, I, I, (a, b) -> a > b);
		rf2("_operatorGreater", B, D, D, (a, b) -> a > b);

		rf2("_operatorEqualEqual", B, I, I, (a, b) -> a == b);
		rf2("_operatorEqualEqual", B, I, D, (a, b) -> false);
		rf2("_operatorEqualEqual", B, D, I, (a, b) -> false);
		rf2("_operatorEqualEqual", B, D, D, (a, b) -> a == b);
		rf2("_operatorEqualEqual", B, B, B, (a, b) -> a == b);
		rf2("_operatorEqualEqual", B, S, S, (a, b) -> a.equals(b));
		rf2("_operatorExclamationEqual", B, I, I, (a, b) -> a != b);
		rf2("_operatorExclamationEqual", B, I, D, (a, b) -> true);
		rf2("_operatorExclamationEqual", B, D, I, (a, b) -> true);
		rf2("_operatorExclamationEqual", B, D, D, (a, b) -> a != b);
		rf2("_operatorExclamationEqual", B, B, B, (a, b) -> a != b);
		rf2("_operatorExclamationEqual", B, S, S, (a, b) -> !a.equals(b));

		rf2("_operatorAmpersandAmpersand", B, B, B, (a, b) -> a && b);
		rf2("_operatorPipePipe", B, B, B, (a, b) -> a || b);

		vm.registerFunction(new ApatiteFunctionProviderBase("_ternaryQuestionColon") {

			@Override
			public Optional<Struct3<Integer, Integer, IApatiteFunctionEntity>> matches(Type<?>... types)
			{
				if (types.length != 3) return Optional.empty();
				if (types[0] != BOOLEAN) return Optional.empty();
				if (types[1] != types[2]) return Optional.empty();

				return Optional.of(new Struct3<>(0, 0, new IApatiteFunctionEntity() {

					@Override
					public Type<?> getType()
					{
						return types[1];
					}

					@Override
					public Object invoke(Object... arguments)
					{
						return (Boolean) arguments[0] ? arguments[1] : arguments[2];
					}

				}));
			}

		});
		vm.registerFunction(new ApatiteFunctionProviderBase("_operatorQuestionColon") {

			@Override
			public Optional<Struct3<Integer, Integer, IApatiteFunctionEntity>> matches(Type<?>... types)
			{
				if (types.length != 2) return Optional.empty();
				if (types[0] != BOOLEAN) return Optional.empty();
				if (types[0] != types[1]) return Optional.empty();

				return Optional.of(new Struct3<>(0, 0, new IApatiteFunctionEntity() {

					@Override
					public Type<?> getType()
					{
						return types[0];
					}

					@Override
					public Object invoke(Object... arguments)
					{
						return (Boolean) arguments[0] ? arguments[0] : arguments[1];
					}

				}));
			}

		});

	}

	protected <O> void rc(String name, Type<O> out, O value)
	{
		vm.registerConstant(new ApatiteConstant<>(out, name, value));
	}

	protected <O> void rf0(String name, Type<O> out, Supplier<O> function)
	{
		vm.registerFunction(new ApatiteFunction<>(out, name, a -> function.get()));
	}

	@SuppressWarnings("unchecked")
	protected <O, I1> void rf1(String name, Type<O> out, Type<I1> in1, Function<I1, O> function)
	{
		vm.registerFunction(new ApatiteFunction<>(out, name, a -> function.apply((I1) a[0]), in1));
	}

	@SuppressWarnings("unchecked")
	protected <O, I1, I2> void rf2(String name, Type<O> out, Type<I1> in1, Type<I2> in2, BiFunction<I1, I2, O> function)
	{
		vm.registerFunction(new ApatiteFunction<>(out, name, a -> function.apply((I1) a[0], (I2) a[1]), in1, in2));
	}

}