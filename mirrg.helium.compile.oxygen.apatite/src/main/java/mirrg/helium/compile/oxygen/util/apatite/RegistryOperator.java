package mirrg.helium.compile.oxygen.util.apatite;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegistryOperator
{

	private ArrayList<Operator> operators = new ArrayList<>();

	public void register(Operator operator)
	{
		for (int i = 0; i < operators.size(); i++) {
			if (operators.get(i).isSameSignature(operator)) {
				operators.set(i, operator);
				return;
			}
		}

		operators.add(operator);
	}

	public ArrayList<Operator> get(String token, Type left, Type right)
	{
		ArrayList<Operator> operators2 = operators.stream()
			.filter(o -> o.token.equals(token))
			.filter(o -> o.left.isAssignableFrom(left))
			.filter(o -> o.right.isAssignableFrom(right))
			.collect(Collectors.toCollection(ArrayList::new));

		boolean[] markDelete = new boolean[operators2.size()];

		for (int i = 0; i < operators2.size(); i++) {
			for (int j = 0; j < operators2.size(); j++) {
				if (i == j) continue;
				if (markDelete[i]) continue;
				if (markDelete[j]) continue;

				if (operators2.get(j).isAssignableFrom(operators2.get(i))) {
					// jがiのスーパークラス
					// i: String, String
					// j: Object, String

					markDelete[j] = true;
				}
			}
		}

		ArrayList<Operator> operators3 = new ArrayList<>();
		for (int i = 0; i < operators2.size(); i++) {
			if (!markDelete[i]) operators3.add(operators2.get(i));
		}

		return operators3;
	}

	public Stream<Operator> getOperators()
	{
		return operators.stream();
	}

}
