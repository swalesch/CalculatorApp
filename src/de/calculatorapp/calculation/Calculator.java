
package de.calculatorapp.calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.ArrayMap;
import de.calculatorapp.database.DatabaseConnection;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException;

public class Calculator {
    private DatabaseConnection _dbConnection;

    public Calculator(DatabaseConnection dbConnection) {
        _dbConnection = dbConnection;
    }

    public void evaluateAndSaveCalculation(String calculationString) {
        if (calculationString.isEmpty())
            return;

        String variable = null;
        if (isStartingWithVariable(calculationString)) {
            variable = getVariableName(calculationString);
            calculationString = removeEquation(calculationString);
        }

        Map<String, String> variablesWithResult = getVariableResultMap(calculationString);

        String result;
        try {
            result = String.valueOf(calc(calculationString, variablesWithResult));
        } catch (UnknownFunctionOrVariableException e) {
            result = "Unknown function or variable: " + e.getToken();
        } catch (IllegalArgumentException e) {
            result = e.getMessage();
        } catch (Exception e) {
            result = e.getMessage();
        }
        if (_dbConnection.isVariableExisting(variable)) {
            _dbConnection.updateVariable(calculationString, result, variable);
        } else {
            _dbConnection.insertCalculation(calculationString, result, variable);
        }
    }

    private double calc(String calculationString, Map<String, String> variablesWithResult) throws Exception {
        ExpressionBuilder calculationBuilder = new ExpressionBuilder(calculationString);
        calculationBuilder.functions(CustomFunctions.getCustomFunctions());
        for (Map.Entry<String, String> variableWithResult : variablesWithResult.entrySet()) {
            calculationBuilder.variable(variableWithResult.getKey());
        }

        Expression calculation = calculationBuilder.build();

        for (Map.Entry<String, String> variableWithResult : variablesWithResult.entrySet()) {
            calculation.setVariable(variableWithResult.getKey(), Double.valueOf(variableWithResult.getValue()));
        }
        return calculation.evaluate();
    }

    private Map<String, String> getVariableResultMap(String calculationString) {
        ArrayMap<String, String> variableWithResult = new ArrayMap<String, String>();
        for (String variable : getVariablesFromCalculationString(calculationString)) {
            String variableResult = _dbConnection.getVariableResultByName(variable);
            if (!variableResult.isEmpty()) {
                variableWithResult.put(variable, variableResult);
            }
        }
        return variableWithResult;
    }

    private static List<String> getVariablesFromCalculationString(String calculationString) {
        ArrayList<String> variables = new ArrayList<String>();
        Matcher matcher = Pattern.compile("[\\(\\+\\-\\*\\/]*([a-zA-Z]+)[\\)\\+\\-\\*\\/]*").matcher(calculationString);
        while (matcher.find()) {
            String foundVariable = matcher.group(1);
            if (!variables.contains(foundVariable)) {
                variables.add(foundVariable);
            }
        }

        return variables;
    }

    private static String getVariableName(String calculationString) {
        return calculationString.split("=")[0].trim();
    }

    private static String removeEquation(String calculationString) {
        return calculationString.split("=")[1];
    }

    private static boolean isStartingWithVariable(String calculationString) {
        return Pattern.compile("^[a-zA-Z_ ]+=").matcher(calculationString).find();
    }
}
