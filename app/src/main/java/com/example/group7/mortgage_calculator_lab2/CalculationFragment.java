package com.example.group7.mortgage_calculator_lab2;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalculationFragment extends Fragment {
    public View calcView;
    public double radioTerms_value,monthly_amt,loan_amt;

    public CalculationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        calcView = inflater.inflate(R.layout.fragment_calculation, container, false);

        final Spinner spinner = (Spinner) calcView.findViewById(R.id.propType_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.proptype_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        final Spinner spinner1 = (Spinner) calcView.findViewById(R.id.state_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.state_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);


        final Button button_new = (Button) calcView.findViewById(R.id.button_new);
        button_new.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                final EditText et_street = (EditText) calcView.findViewById(R.id.street);
                final EditText et_city = (EditText) calcView.findViewById(R.id.city);
                final EditText et_zipcode= (EditText) calcView.findViewById(R.id.zipcode);
                final EditText et_propPrice = (EditText) calcView.findViewById(R.id.propPrice);
                final EditText et_downpmt = (EditText) calcView.findViewById(R.id.downPmt);
                final EditText et_apr = (EditText) calcView.findViewById(R.id.apr);
                final TextView tv_monthly = (TextView) calcView.findViewById(R.id.monthly);
                et_street.setText("");
                et_city.setText("");
                et_zipcode.setText("");
                et_propPrice.setText("");
                et_downpmt.setText("");
                et_apr.setText("");
                spinner.setSelection(0);
                spinner1.setSelection(0);
                tv_monthly.setText("");
                RadioGroup radioGroup_terms = (RadioGroup)calcView.findViewById(R.id.rg_terms);
                radioGroup_terms.clearCheck();


            }
        });


        final Button button_save = (Button) calcView.findViewById(R.id.button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SaveDataHelper mDbHelper = new SaveDataHelper(getActivity().getApplicationContext());
                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                final EditText et_street = (EditText) calcView.findViewById(R.id.street);
                final EditText et_city = (EditText) calcView.findViewById(R.id.city);

                final EditText et_apr= (EditText) calcView.findViewById(R.id.apr);

// Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(SaveDataContract.SaveDataEntry.COLUMN_NAME_PROPTYPE, spinner.getSelectedItem().toString());
                values.put(SaveDataContract.SaveDataEntry.COLUMN_NAME_STREET, et_street.getText().toString());
                values.put(SaveDataContract.SaveDataEntry.COLUMN_NAME_CITY, et_city.getText().toString());
                values.put(SaveDataContract.SaveDataEntry.COLUMN_NAME_LOANAMT, loan_amt);
                values.put(SaveDataContract.SaveDataEntry.COLUMN_NAME_APR,et_apr.getText().toString());
                values.put(SaveDataContract.SaveDataEntry.COLUMN_NAME_MPMT, monthly_amt);

// Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(SaveDataContract.SaveDataEntry.TABLE_NAME, null, values);

                if(newRowId != -1)
                    Log.v("row", "Inserted");
                else
                    Log.v("nrow", "Not Inserted");

                // Code here executes on main thread after user presses button
            }
        });

        final Button button_calc = (Button) calcView.findViewById(R.id.button_calc);
        button_calc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button Calc


                final EditText et_propPrice = (EditText) calcView.findViewById(R.id.propPrice);
                final EditText et_downpmt = (EditText) calcView.findViewById(R.id.downPmt);
                final EditText et_apr = (EditText) calcView.findViewById(R.id.apr);
                final TextView tv_monthly = (TextView) calcView.findViewById(R.id.monthly);

                double propPrice = Double.parseDouble(et_propPrice.getText().toString());
                double dwnPmt = Double.parseDouble(et_downpmt.getText().toString());
                double apr = Double.parseDouble(et_apr.getText().toString());
                apr = apr/100;
                radioTerms_value = radioTerms_value *12;
                Log.v("apr",apr+"");
                Log.v("xxx",Math.pow(1+apr,radioTerms_value) + " ");

                Log.v("thly",radioTerms_value+"");

                loan_amt = propPrice - dwnPmt;

                monthly_amt = (loan_amt)*((apr* (Math.pow(1+apr,radioTerms_value)))/(Math.pow(1+apr,radioTerms_value)-1));
                Log.v("monthly",monthly_amt+"");
                tv_monthly.setText(monthly_amt+"");


            }
        });
        return calcView;
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_fifteen:
                if (checked)
                    // Pirates are the best
                    radioTerms_value =  15;
                break;
            case R.id.radio_thirty:
                if (checked)
                    radioTerms_value =  30;
                break;
        }
    }
}