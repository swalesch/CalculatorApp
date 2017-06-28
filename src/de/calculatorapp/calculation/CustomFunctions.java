package de.calculatorapp.calculation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.objecthunter.exp4j.function.Function;

public class CustomFunctions {

    private static Function getCustomFunction_Random() {
        return new Function("random", 0) {
            @Override
            public double apply(double... args) {
                return Math.random();
            }

            @Override
            public String toString() {
                return "<h3>random()</h3><p>Retruns a random double ranging between 0 and 1.</p>";
            }
        };
    }

    private static Function getCustomFunction_Cos() {
        return new Function("cos", 1) {
            @Override
            public double apply(double... args) {
                double scale = Math.pow(10, 15);
                return Math.round(Math.cos(args[0]) * scale) / scale;
            }

            @Override
            public String toString() {
                return "<h3>cos(float)</h3><p>Tacks a float in rad and returns a number between -1 and 1.</p>";
            }
        };
    }

    private static Function getCustomFunction_Acos() {
        return new Function("acos", 1) {
            @Override
            public double apply(double... args) {
                double scale = Math.pow(10, 15);
                return Math.round(Math.acos(args[0]) * scale) / scale;
            }

            @Override
            public String toString() {
                return "<h3>acos(float)</h3><p>Is the revers function for cos.</p>";
            }
        };
    }

    private static Function getCustomFunction_Tan() {
        return new Function("tan", 1) {
            @Override
            public double apply(double... args) {
                double scale = Math.pow(10, 15);
                return Math.round(Math.tan(args[0]) * scale) / scale;
            }

            @Override
            public String toString() {
                return "<h3>tan(float)</h3><p>Tacks a float in rad and returns a number between -1 and 1.</p>";
            }
        };
    }

    private static Function getCustomFunction_Atan() {
        return new Function("atan", 1) {
            @Override
            public double apply(double... args) {
                double scale = Math.pow(10, 15);
                return Math.round(Math.atan(args[0]) * scale) / scale;
            }

            @Override
            public String toString() {
                return "<h3>atan(float)</h3><p>Is the revers function for tan</p>";
            }
        };
    }

    private static Function getCustomFunction_Sin() {
        return new Function("sin", 1) {
            @Override
            public double apply(double... args) {
                double scale = Math.pow(10, 15);
                return Math.round(Math.sin(args[0]) * scale) / scale;
            }

            @Override
            public String toString() {
                return "<h3>sin(float)</h3><p>Tacks a float in rad and returns a number between -1 and 1.</p>";
            }

        };
    }

    private static Function getCustomFunction_Asin() {
        return new Function("asin", 1) {
            @Override
            public double apply(double... args) {
                double scale = Math.pow(10, 15);
                return Math.round(Math.asin(args[0]) * scale) / scale;
            }

            @Override
            public String toString() {
                return "<h3>asin(float)</h3><p>Is the revers function for sin.</p>";
            }

        };
    }

    private static Function getCustomFunction_Round() {
        return new Function("round", 2) {
            @Override
            public double apply(double... args) {
                double scale = Math.pow(10, (int) args[1]);
                return Math.round(args[0] * scale) / scale;
            }

            @Override
            public String toString() {
                return "<h3>round(float, int)</h3><p>Used to round a float to given int numbers after the comma. 0 will be next int.</p>";
            }
        };
    }

    private static Function getCustomFunction_Abs() {
        return new Function("abs", 1) {
            @Override
            public double apply(double... args) {
                return Math.abs(args[0]);
            }

            @Override
            public String toString() {
                return "<h3>abs(float)</h3><p>Returns the absolute value of a float value. If the argument is not negative, the argument is returned. If the argument is negative, the negation of the argument is returned.</p>";
            }
        };
    }

    private static Function getCustomFunction_Sqrt() {
        return new Function("sqrt", 1) {
            @Override
            public double apply(double... args) {
                return Math.sqrt(args[0]);
            }

            @Override
            public String toString() {
                return "<h3>sqrt(float)</h3><p>Returns the correctly rounded positive square root of a double value.</p>";
            }
        };
    }

    private static Function getCustomFunction_Pow() {
        return new Function("pow", 2) {
            @Override
            public double apply(double... args) {
                return Math.pow(args[0], args[1]);
            }

            @Override
            public String toString() {
                return "<h3>pow(float, float)</h3><p>Returns the value of the first argument raised to the power of the second argument.</p>";
            }
        };
    }

    public static Function[] getCustomFunctions() {
        List<Function> functions = new ArrayList<Function>();
        functions.add(getCustomFunction_Sin());
        functions.add(getCustomFunction_Asin());
        functions.add(getCustomFunction_Cos());
        functions.add(getCustomFunction_Acos());
        functions.add(getCustomFunction_Tan());
        functions.add(getCustomFunction_Atan());
        functions.add(getCustomFunction_Round());
        functions.add(getCustomFunction_Random());
        functions.add(getCustomFunction_Abs());
        functions.add(getCustomFunction_Sqrt());
        functions.add(getCustomFunction_Pow());
        sortByName(functions);
        return functions.toArray(new Function[functions.size()]);
    }

    private static void sortByName(List<Function> functions) {
        Collections.sort(functions, new Comparator<Function>() {
            @Override
            public int compare(Function o1, Function o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
