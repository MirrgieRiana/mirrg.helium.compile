package mirrg.helium.compile.oxygen.util.apatite.vm1;

import java.awt.Color;

import org.apache.commons.math3.complex.Complex;

import mirrg.helium.compile.oxygen.util.apatite.Type;
import mirrg.helium.compile.oxygen.util.apatite.VM;
import mirrg.helium.compile.oxygen.util.apatite.WrapperBaseType;
import mirrg.helium.compile.oxygen.util.apatite.WrapperType;

public class VM1 extends VM
{

	public WrapperType<Type> TYPE;
	public WrapperType<String> STRING;
	public WrapperType<Double> DOUBLE;
	public WrapperType<Integer> INTEGER;
	public WrapperType<Boolean> BOOLEAN;
	public WrapperType<Complex> COMPLEX;
	public WrapperBaseType<Lambda> LAMBDA;

	public VM1()
	{
		OBJECT.type.baseType.setColorFunction(l -> Color.decode("#545454"));
		BASETYPE.type.baseType.setColorFunction(t -> Color.decode("#AA7744"));
		TYPE = new WrapperType<>(registerBaseType("Type").apply());
		TYPE.type.baseType.setParentFunction(t -> OBJECT.type);
		TYPE.type.baseType.setColorFunction(l -> Color.decode("#72502D"));
		STRING = new WrapperType<>(registerBaseType("String").apply());
		STRING.type.baseType.setParentFunction(t -> OBJECT.type);
		STRING.type.baseType.setColorFunction(l -> Color.decode("#a64eaa"));
		DOUBLE = new WrapperType<>(registerBaseType("Double").apply());
		DOUBLE.type.baseType.setParentFunction(t -> OBJECT.type);
		DOUBLE.type.baseType.setColorFunction(l -> Color.decode("#53a540"));
		INTEGER = new WrapperType<>(registerBaseType("Integer").apply());
		INTEGER.type.baseType.setCaster(o -> (double) (Integer) o);
		INTEGER.type.baseType.setParentFunction(t -> DOUBLE.type);
		INTEGER.type.baseType.setColorFunction(l -> Color.decode("#424aaf"));
		BOOLEAN = new WrapperType<>(registerBaseType("Boolean").apply());
		BOOLEAN.type.baseType.setParentFunction(t -> OBJECT.type);
		BOOLEAN.type.baseType.setColorFunction(l -> Color.decode("#878334"));
		COMPLEX = new WrapperType<>(registerBaseType("Complex").apply());
		COMPLEX.type.baseType.setParentFunction(t -> OBJECT.type);
		COMPLEX.type.baseType.setColorFunction(l -> Color.decode("#960002"));
		LAMBDA = new WrapperBaseType<>(registerBaseType("Lambda"));
		LAMBDA.baseType.setParentFunction(t -> OBJECT.type);
		LAMBDA.baseType.setColorFunction(l -> Color.decode("#009BA0"));

		registerOperator("+", (a, b) -> a + b, STRING, OBJECT, STRING);
		registerOperator("+", (a, b) -> a + b, OBJECT, STRING, STRING);
		registerOperator("+", (a, b) -> a + b, STRING, INTEGER, STRING);
		registerOperator("+", (a, b) -> a + b, INTEGER, STRING, STRING);
		registerOperator("+", (a, b) -> a + b, STRING, STRING, STRING);

		registerOperator("+", (a, b) -> a + b, DOUBLE, DOUBLE, DOUBLE);
		registerOperator("+", (a, b) -> a + b, INTEGER, INTEGER, INTEGER);
		registerOperator("-", (a, b) -> a - b, DOUBLE, DOUBLE, DOUBLE);
		registerOperator("-", (a, b) -> a - b, INTEGER, INTEGER, INTEGER);
		registerOperator("*", (a, b) -> a * b, DOUBLE, DOUBLE, DOUBLE);
		registerOperator("*", (a, b) -> a * b, INTEGER, INTEGER, INTEGER);
		registerOperator("/", (a, b) -> a / b, DOUBLE, DOUBLE, DOUBLE);
		registerOperator("/", (a, b) -> a / b, INTEGER, INTEGER, INTEGER);
		registerOperator("%", (a, b) -> a % b, INTEGER, INTEGER, INTEGER);

		registerOperator("<=", (a, b) -> a <= b, INTEGER, INTEGER, BOOLEAN);
		registerOperator("<=", (a, b) -> a <= b, DOUBLE, DOUBLE, BOOLEAN);
		registerOperator(">=", (a, b) -> a >= b, INTEGER, INTEGER, BOOLEAN);
		registerOperator(">=", (a, b) -> a >= b, DOUBLE, DOUBLE, BOOLEAN);
		registerOperator("<", (a, b) -> a < b, INTEGER, INTEGER, BOOLEAN);
		registerOperator("<", (a, b) -> a < b, DOUBLE, DOUBLE, BOOLEAN);
		registerOperator(">", (a, b) -> a > b, INTEGER, INTEGER, BOOLEAN);
		registerOperator(">", (a, b) -> a > b, DOUBLE, DOUBLE, BOOLEAN);

		registerOperator("==", (a, b) -> a == b, INTEGER, INTEGER, BOOLEAN);
		registerOperator("==", (a, b) -> a == b, DOUBLE, DOUBLE, BOOLEAN);
		registerOperator("==", (a, b) -> a == b, BOOLEAN, BOOLEAN, BOOLEAN);
		registerOperator("==", (a, b) -> a.equals(b), OBJECT, OBJECT, BOOLEAN);
		registerOperator("!=", (a, b) -> a != b, INTEGER, INTEGER, BOOLEAN);
		registerOperator("!=", (a, b) -> a != b, DOUBLE, DOUBLE, BOOLEAN);
		registerOperator("!=", (a, b) -> a != b, BOOLEAN, BOOLEAN, BOOLEAN);
		registerOperator("!=", (a, b) -> !a.equals(b), OBJECT, OBJECT, BOOLEAN);

		registerOperator("&&", (a, b) -> a && b, BOOLEAN, BOOLEAN, BOOLEAN);
		registerOperator("||", (a, b) -> a || b, BOOLEAN, BOOLEAN, BOOLEAN);

		registerOperator("+", (a, b) -> a.add(b), COMPLEX, COMPLEX, COMPLEX);
		registerOperator("-", (a, b) -> a.subtract(b), COMPLEX, COMPLEX, COMPLEX);
		registerOperator("*", (a, b) -> a.multiply(b), COMPLEX, COMPLEX, COMPLEX);
		registerOperator("/", (a, b) -> a.divide(b), COMPLEX, COMPLEX, COMPLEX);
		registerOperator("+", (a, b) -> a.add(b), COMPLEX, DOUBLE, COMPLEX);
		registerOperator("-", (a, b) -> a.subtract(b), COMPLEX, DOUBLE, COMPLEX);
		registerOperator("*", (a, b) -> a.multiply(b), COMPLEX, DOUBLE, COMPLEX);
		registerOperator("/", (a, b) -> a.divide(b), COMPLEX, DOUBLE, COMPLEX);
		registerOperator("+", (a, b) -> b.add(a), DOUBLE, COMPLEX, COMPLEX);
		registerOperator("-", (a, b) -> new Complex(a).subtract(b), DOUBLE, COMPLEX, COMPLEX);
		registerOperator("*", (a, b) -> b.multiply(a), DOUBLE, COMPLEX, COMPLEX);
		registerOperator("/", (a, b) -> new Complex(a).divide(b), DOUBLE, COMPLEX, COMPLEX);

		registerConstant(DOUBLE, "pi", Math.PI);
		registerConstant(DOUBLE, "e", Math.E);
		registerConstant(BOOLEAN, "true", true);
		registerConstant(BOOLEAN, "false", false);
		registerConstant(STRING, "lineSeparator", System.lineSeparator());
	}

}
