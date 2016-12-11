package mirrg.helium.compile.oxygen.apatite2.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.helium.compile.oxygen.apatite2.ApatiteVM;
import mirrg.helium.compile.oxygen.apatite2.IApatiteFunctionEntity;
import mirrg.helium.compile.oxygen.apatite2.IApatiteMetaFunctionEntity;
import mirrg.helium.compile.oxygen.apatite2.node.ApatiteCodeBase;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;
import mirrg.helium.compile.oxygen.apatite2.type.Type;
import mirrg.helium.compile.oxygen.editor.IProviderChildren;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class NodeFunction extends ApatiteCodeBase implements IProviderChildren
{

	public String name;
	public IApatiteCode[] codes;

	public NodeFunction(String name, int begin, int end, IApatiteCode... codes)
	{
		super(begin, end);
		this.name = name;
		this.codes = codes;
	}

	@Override
	public Optional<IApatiteScript> validateImpl(ApatiteVM vm)
	{

		// メタ関数呼び出し

		Optional<IApatiteMetaFunctionEntity> oMetaFunction = vm.getMetaFunctions().stream()
			.filter(f -> f.getName().equals(name))
			.map(f -> f.matches(codes))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst();

		if (oMetaFunction.isPresent()) {
			Optional<IApatiteScript> oMetaFunctionScript = oMetaFunction.get().validate(begin, end, vm);
			if (!oMetaFunctionScript.isPresent()) return failure();
			return success(oMetaFunctionScript.get());
		}

		// 通常関数呼び出し

		boolean flag = true;

		IApatiteScript[] scripts = new IApatiteScript[codes.length];
		Type<?>[] types = new Type<?>[codes.length];
		for (int i = 0; i < codes.length; i++) {
			Optional<IApatiteScript> oScript = codes[i].validate(vm);
			if (!oScript.isPresent()) {
				flag = false;
			} else {
				scripts[i] = oScript.get();
				types[i] = oScript.get().getType();
			}
		}

		if (!flag) return failure();

		// ここで引数のチェックが終わり型まで確定した

		Optional<IApatiteFunctionEntity> oFunction = vm.getFunction(name, types);

		if (oFunction.isPresent()) return success(oFunction.get(), scripts);

		vm.reportError(begin, end, "No such function: " + name + "(" + Stream.of(scripts)
			.map(s -> s.getType().getName())
			.collect(Collectors.joining(", ")) + ")");
		return failure();
	}

	@SuppressWarnings("unchecked")
	private Optional<IApatiteScript> success(IApatiteFunctionEntity function, IApatiteScript[] scripts)
	{
		return success((Type<Object>) function.getType(), () -> {
			Object[] arguments = new Object[scripts.length];
			for (int i = 0; i < scripts.length; i++) {
				arguments[i] = scripts[i].invoke();
			}
			return function.invoke(arguments);
		});
	}

	@SuppressWarnings("unchecked")
	private Optional<IApatiteScript> success(IApatiteScript function)
	{
		return success((Type<Object>) function.getType(), function::invoke);
	}

	@Override
	public List<Node<?>> getChildren()
	{
		return Stream.of(codes)
			.map(c -> new Node<>(null, new ArrayList<>(), c.getBegin(), c.getEnd(), c))
			.collect(Collectors.toList());
	}

}
