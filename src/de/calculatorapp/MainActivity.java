package de.calculatorapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import de.calculatorapp.calculation.Calculator;
import de.calculatorapp.calculation.CustomFunctions;
import de.calculatorapp.database.DatabaseConnection;
import net.objecthunter.exp4j.function.Function;

public class MainActivity extends Activity {

    private Calculator _calculator;
    private ArrayAdapter<String> _logViewAdapter;
    private DatabaseConnection _dbConnection;

    private enum ViewState {
        BACKLOG_VIEW, MATH_FUNCTION_VIEW, VARIABLE_VIEW
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _dbConnection = new DatabaseConnection(getApplicationContext());
        _calculator = new Calculator(_dbConnection);

        initializeCalculationBar();

        initialzeLogView();
    }

    private void initializeCalculationBar() {
        ((EditText) findViewById(R.id.calculationBar)).setOnEditorActionListener(getCalculateAction());
    }

    private void initialzeLogView() {
        ListView listView = (ListView) findViewById(R.id.logView);
        _logViewAdapter = createArrayAdapter();
        listView.setAdapter(_logViewAdapter);
        relodeLogViewFromDatabase(ViewState.BACKLOG_VIEW);
    }

    private void relodeLogViewFromDatabase(ViewState viewState) {
        _logViewAdapter.clear();
        switch (viewState) {
        case BACKLOG_VIEW:
            _logViewAdapter.addAll(_dbConnection.getAllCalculationsAsHtml());
            break;
        case MATH_FUNCTION_VIEW:
            _logViewAdapter.addAll(getFunctionHelpText());
            break;
        case VARIABLE_VIEW:
            _logViewAdapter.addAll(_dbConnection.getAllVariablesAsHtml());
        default:
            break;
        }
    }

    private String[] getFunctionHelpText() {
        Function[] customFunctions = CustomFunctions.getCustomFunctions();
        String[] functionsHelpText = new String[customFunctions.length];
        for (int i = 0; i < customFunctions.length; i++) {
            functionsHelpText[i] = customFunctions[i].toString();
        }
        return functionsHelpText;
    }

    private OnEditorActionListener getCalculateAction() {
        return new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (isActionDone(actionId, event)) {
                    String calculationString = textView.getText().toString();
                    _calculator.evaluateAndSaveCalculation(calculationString);
                    relodeLogViewFromDatabase(ViewState.BACKLOG_VIEW);
                    textView.setText("");
                    return true;
                }
                return false;
            }
        };
    }

    private ArrayAdapter<String> createArrayAdapter() {
        return new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setBackgroundColor(Color.TRANSPARENT);
                text.setTextColor(Color.BLACK);
                text.setText(Html.fromHtml(text.getText().toString()));
                view.setOnLongClickListener(createLongTextViewClick());
                return view;
            }
        };
    }

    private OnLongClickListener createLongTextViewClick() {
        return new OnLongClickListener() {

            @Override
            public boolean onLongClick(View selectedView) {
                copySelectedViewTitleToCalculationBar(selectedView);
                return true;
            }

            private void copySelectedViewTitleToCalculationBar(View view) {
                EditText calculationBar = (EditText) findViewById(R.id.calculationBar);
                Editable calculationBarText = calculationBar.getText();
                calculationBarText = deleteSelectedText(calculationBarText, calculationBar);
                calculationBarText = insertSelectionToIndex(view, calculationBar, calculationBarText);
                calculationBar.setText(calculationBarText);
                calculationBar.setSelection(calculationBar.length());
            }

            private Editable deleteSelectedText(Editable calculationBarText, EditText calculationBar) {
                return calculationBarText.delete(calculationBar.getSelectionStart(), calculationBar.getSelectionEnd());
            }

            private Editable insertSelectionToIndex(View selectedView, EditText calculationBar,
                    Editable calculationBarText) {
                return calculationBarText.insert(calculationBar.getSelectionStart(), getViewTitle(selectedView));
            }
        };
    };

    private String getViewTitle(View selectedView) {
        TextView selectedTextView = (TextView) selectedView.findViewById(android.R.id.text1);
        String fullCalculationString = selectedTextView.getText().toString();
        Pattern pattern = Pattern.compile("[\\s\\S]*?\\n");
        Matcher matcher = pattern.matcher(fullCalculationString);
        if (matcher.find()) {
            String onlyTitle = matcher.group(0);
            return onlyTitle.trim();
        } else {
            // TODO error?
            return fullCalculationString;
        }
    }

    private boolean isActionDone(int actionId, KeyEvent event) {
        return actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String toastMsg = "";
        if (item.getItemId() == R.id.clearBacklog) {
            toastMsg = "clear Backlog";
            _dbConnection.deleteBacklogEntries();
            relodeLogViewFromDatabase(ViewState.BACKLOG_VIEW);
        } else if (item.getItemId() == R.id.clearVariables) {
            toastMsg = "clear Variables";
            _dbConnection.deleteVariables();
            relodeLogViewFromDatabase(ViewState.BACKLOG_VIEW);
        } else if (item.getItemId() == R.id.showMathFunctions) {
            toastMsg = "show Math functions";
            relodeLogViewFromDatabase(ViewState.MATH_FUNCTION_VIEW);
        } else if (item.getItemId() == R.id.showVariables) {
            toastMsg = "show Variables";
            relodeLogViewFromDatabase(ViewState.VARIABLE_VIEW);
        } else if (item.getItemId() == R.id.showBacklog) {
            toastMsg = "show Backlog";
            relodeLogViewFromDatabase(ViewState.BACKLOG_VIEW);
        }

        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }
}
