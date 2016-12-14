package mirrg.helium.compile.oxygen.apatite2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.math3.util.FastMath;

import mirrg.helium.compile.oxygen.apatite2.core.ApatiteConstant;
import mirrg.helium.compile.oxygen.apatite2.core.ApatiteFunction;
import mirrg.helium.compile.oxygen.apatite2.core.ApatiteVM;
import mirrg.helium.compile.oxygen.apatite2.core.IApatiteFunctionEntity;
import mirrg.helium.compile.oxygen.apatite2.core.IApatiteFunctionProvider;
import mirrg.helium.compile.oxygen.apatite2.core.IApatiteMetaFunctionEntity;
import mirrg.helium.compile.oxygen.apatite2.core.IApatiteMetaFunctionProvider;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;
import mirrg.helium.compile.oxygen.apatite2.nodes.CodeFunction;
import mirrg.helium.compile.oxygen.apatite2.nodes.CodeIdentifier;
import mirrg.helium.compile.oxygen.apatite2.type.IType;
import mirrg.helium.compile.oxygen.apatite2.type.TypeArray;
import mirrg.helium.compile.oxygen.apatite2.type.TypeBasic;
import mirrg.helium.compile.oxygen.apatite2.type.TypeInteger;
import mirrg.helium.compile.oxygen.apatite2.type.TypeValue;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.standard.hydrogen.struct.Tuple3;

public class ApatiteLoader
{

	public static final IType<Object> VALUE = new TypeValue("Value", Color.decode("#8888ff"));
	public static final IType<StructureComplex> COMPLEX = new TypeBasic<>("Complex", Color.decode("#880000"));
	public static final IType<Double> DOUBLE = new TypeBasic<>("Double", Color.decode("#000088"));
	public static final IType<Integer> INTEGER = new TypeInteger("Integer", Color.decode("#ff8800"));
	public static final IType<String> STRING = new TypeBasic<>("String", Color.decode("#ff00ff"));
	public static final IType<String> KEYWORD = new TypeBasic<>("Keyword", Color.decode("#00aa00"));
	public static final IType<Boolean> BOOLEAN = new TypeBasic<>("Boolean", Color.decode("#888800"));
	public static final IType<IType<?>> TYPE = new TypeBasic<>("Type", Color.decode("#00aa00"));

	public static final IType<Object> V = VALUE;
	public static final IType<StructureComplex> C = COMPLEX;
	public static final IType<Double> D = DOUBLE;
	public static final IType<Integer> I = INTEGER;
	public static final IType<String> S = STRING;
	public static final IType<String> K = KEYWORD;
	public static final IType<Boolean> B = BOOLEAN;
	public static final IType<IType<?>> T = TYPE;

	public static <T> IType<T[]> ARRAY(IType<T> type)
	{
		return new TypeArray<T>(type);
	}

	public static ApatiteVM createVM()
	{
		ApatiteVM vm = new ApatiteVM();
		load(vm);
		return vm;
	}

	public static void load(ApatiteVM vm)
	{
		new ApatiteLoader(vm).load();
	}

	//

	protected ApatiteVM vm;

	protected ApatiteLoader(ApatiteVM vm)
	{
		this.vm = vm;
	}

	public void load()
	{
		rc("true", B, true);
		rc("false", B, false);
		rf0("now", D, () -> (double) System.currentTimeMillis());
		rf1("sin", D, D, Math::sin);
		rf1("cos", D, D, Math::cos);
		rf1("tan", D, D, Math::tan);
		vm.registerFunction(new IApatiteFunctionProvider() {

			@Override
			public Optional<Tuple3<Integer, Integer, IApatiteFunctionEntity>> matches(IType<?>... types)
			{
				if (types.length != 1) return Optional.empty();
				if (!(types[0] instanceof TypeArray)) return Optional.empty();

				return Optional.of(new Tuple3<>(0, 0, new IApatiteFunctionEntity() {

					@Override
					public IType<?> getType()
					{
						return INTEGER;
					}

					@Override
					public Object invoke(Object... arguments)
					{
						return ((ArrayList<?>) arguments[0]).size();
					}

				}));
			}

		}, "length");

		vm.registerFunction(new IApatiteFunctionProvider() {

			@Override
			public Optional<Tuple3<Integer, Integer, IApatiteFunctionEntity>> matches(IType<?>... types)
			{
				if (types.length != 1) return Optional.empty();

				return Optional.of(new Tuple3<>(0, 0, new IApatiteFunctionEntity() {

					@Override
					public IType<?> getType()
					{
						return types[0];
					}

					@Override
					public Object invoke(Object... arguments)
					{
						return arguments[0];
					}

				}));
			}

		}, "_bracketsRound");
		vm.registerMetaFunction(new IApatiteMetaFunctionProvider() {

			@Override
			public Optional<IApatiteMetaFunctionEntity> matches(IApatiteCode... codes)
			{
				IApatiteCode[] codes2 = Stream.of(codes)
					.flatMap(c -> {
						if ((c instanceof CodeFunction) && ((CodeFunction) c).name.equals("_enumerateComma")) {
							return Stream.of(((CodeFunction) c).codes);
						}
						return Stream.of(c);
					})
					.toArray(IApatiteCode[]::new);

				if (codes2.length == 0) return Optional.empty(); // TODO

				return Optional.of(new IApatiteMetaFunctionEntity() {

					@Override
					public Optional<IApatiteScript> validate(int begin, int end, ApatiteVM vm)
					{
						IType<?> type = null;
						boolean flag = true;

						IApatiteScript[] scripts = new IApatiteScript[codes2.length];
						for (int i = 0; i < codes2.length; i++) {
							Optional<IApatiteScript> oScript = codes2[i].validate(vm);
							if (!oScript.isPresent()) {
								flag = false;
							} else {
								scripts[i] = oScript.get();
								if (type == null) {
									type = oScript.get().getType();
								} else {
									if (!type.equals(oScript.get().getType())) {
										flag = false;
										vm.reportError(codes2[i].getBegin(), codes2[i].getEnd(),
											"Type mismatch: " + oScript.get().getType().getName() + " != " + type.getName()); // TODO
									}
								}
							}
						}

						if (!flag) return Optional.empty();
						IType<?> type2 = type;

						return Optional.of(new IApatiteScript() {

							@Override
							public IType<?> getType()
							{
								return ARRAY(type2);
							}

							@Override
							public Object invoke()
							{
								ArrayList<Object> array = new ArrayList<>();
								for (IApatiteScript script : scripts) {
									array.add(script.invoke());
								}
								return array;
							}

						});
					}

				});
			}

		}, "_bracketsSquare");

		vm.registerMetaFunction(new IApatiteMetaFunctionProvider() {

			@Override
			public Optional<IApatiteMetaFunctionEntity> matches(IApatiteCode... codes)
			{
				if (codes.length != 1) return Optional.empty();

				return Optional.of(new IApatiteMetaFunctionEntity() {

					@Override
					public Optional<IApatiteScript> validate(int begin, int end, ApatiteVM vm)
					{
						Optional<IApatiteScript> script = codes[0].validate(vm);
						if (!script.isPresent()) return Optional.empty();

						return Optional.of(new IApatiteScript() {

							@Override
							public mirrg.helium.compile.oxygen.apatite2.type.IType<?> getType()
							{
								return TYPE;
							}

							@Override
							public Object invoke()
							{
								return script.get().getType();
							}

						});
					}

				});
			}

		}, "type");

		vm.registerMetaFunction(new IApatiteMetaFunctionProvider() {

			@Override
			public Optional<IApatiteMetaFunctionEntity> matches(IApatiteCode... codes)
			{
				if (codes.length < 1) return Optional.empty();
				if (!(codes[0] instanceof CodeIdentifier)) return Optional.empty();
				String name = ((CodeIdentifier) codes[0]).name;

				IApatiteCode[] codes2 = Stream.of(codes)
					.skip(1)
					.flatMap(c -> {
						if ((c instanceof CodeFunction) && ((CodeFunction) c).name.equals("_enumerateComma")) {
							return Stream.of(((CodeFunction) c).codes);
						}
						return Stream.of(c);
					})
					.toArray(IApatiteCode[]::new);

				return Optional.of((begin, end, vm) -> new CodeFunction(name, begin, end, codes2).validate(vm));
			}

		}, "_rightBracketsRound");
		vm.registerFunction(new IApatiteFunctionProvider() {

			@Override
			public Optional<Tuple3<Integer, Integer, IApatiteFunctionEntity>> matches(IType<?>... types)
			{
				if (types.length != 2) return Optional.empty();
				if (!(types[0] instanceof TypeArray)) return Optional.empty();
				if (!(types[1].equals(INTEGER))) return Optional.empty();

				return Optional.of(new Tuple3<>(0, 0, new IApatiteFunctionEntity() {

					@Override
					public IType<?> getType()
					{
						return ((TypeArray<?>) types[0]).type;
					}

					@Override
					public Object invoke(Object... arguments)
					{
						return ((ArrayList<?>) arguments[0]).get((Integer) arguments[1]);
					}

				}));
			}

		}, "_rightBracketsSquare");

		rf1("_leftPlus", I, I, a -> a);
		rf1("_leftPlus", D, D, a -> a);
		rf1("_leftMinus", I, I, a -> -a);
		rf1("_leftMinus", D, D, a -> -a);
		rf1("_leftExclamation", B, B, a -> !a);

		rf2("_operatorHat", D, D, I, FastMath::pow);
		rf2("_operatorHat", D, D, D, FastMath::pow);

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

		rf2("_operatorEqualEqual", B, V, V, (a, b) -> a.equals(b));
		rf2("_operatorExclamationEqual", B, V, V, (a, b) -> !a.equals(b));

		rf2("_operatorAmpersandAmpersand", B, B, B, (a, b) -> a && b);
		rf2("_operatorPipePipe", B, B, B, (a, b) -> a || b);

		vm.registerFunction(new IApatiteFunctionProvider() {

			@Override
			public Optional<Tuple3<Integer, Integer, IApatiteFunctionEntity>> matches(IType<?>... types)
			{
				if (types.length != 3) return Optional.empty();
				if (!types[0].equals(BOOLEAN)) return Optional.empty();
				if (!types[1].equals(types[2])) return Optional.empty();

				return Optional.of(new Tuple3<>(0, 0, new IApatiteFunctionEntity() {

					@Override
					public IType<?> getType()
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

		}, "_ternaryQuestionColon");
		vm.registerFunction(new IApatiteFunctionProvider() {

			@Override
			public Optional<Tuple3<Integer, Integer, IApatiteFunctionEntity>> matches(IType<?>... types)
			{
				if (types.length != 2) return Optional.empty();
				if (!types[0].equals(BOOLEAN)) return Optional.empty();
				if (!types[0].equals(types[1])) return Optional.empty();

				return Optional.of(new Tuple3<>(0, 0, new IApatiteFunctionEntity() {

					@Override
					public IType<?> getType()
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

		}, "_operatorQuestionColon");

	}

	protected <O> void rc(String name, IType<O> out, O value)
	{
		vm.registerConstant(new ApatiteConstant<>(out, name, value));
	}

	protected <O> void rf0(String name, IType<O> out, IRF0<O> function)
	{
		vm.registerFunction(new ApatiteFunction<>(out, a -> function.apply()), name);
	}

	public static interface IRF0<O>
	{

		public O apply();

	}

	@SuppressWarnings("unchecked")
	protected <O, I1> void rf1(String name, IType<O> out, IType<I1> in1, IRF1<I1, O> function)
	{
		vm.registerFunction(new ApatiteFunction<>(out, a -> function.apply((I1) a[0]), in1), name);
	}

	public static interface IRF1<I1, O>
	{

		public O apply(I1 a);

	}

	@SuppressWarnings("unchecked")
	protected <O, I1, I2> void rf2(String name, IType<O> out, IType<I1> in1, IType<I2> in2, IRF2<I1, I2, O> function)
	{
		vm.registerFunction(new ApatiteFunction<>(out, a -> function.apply((I1) a[0], (I2) a[1]), in1, in2), name);
	}

	public static interface IRF2<I1, I2, O>
	{

		public O apply(I1 a, I2 b);

	}

	@SuppressWarnings("unchecked")
	protected <O, I1, I2, I3> void rf3(String name, IType<O> out, IType<I1> in1, IType<I2> in2, IType<I3> in3, IRF3<I1, I2, I3, O> function)
	{
		vm.registerFunction(new ApatiteFunction<>(out, a -> function.apply((I1) a[0], (I2) a[1], (I3) a[2]), in1, in2, in3), name);
	}

	public static interface IRF3<I1, I2, I3, O>
	{

		public O apply(I1 a, I2 b, I3 c);

	}

}
