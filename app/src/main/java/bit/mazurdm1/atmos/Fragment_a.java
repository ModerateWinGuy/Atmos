package bit.mazurdm1.atmos;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

public class Fragment_a extends Fragment implements SensorEventListener,FragmentHasBecomeVisible
{


    public Fragment_a()
    {
        // Required empty public constructor
    }
    private static final String FILENAME_LOCATIONS = "Locations";
    private static final String FILENAME_DATA = "SavedDataFile";

    private SensorManager sensorManager;
    private Sensor mPressure;
    private double currentPressure;
    private Sensor mTemp;
    private double currentTemp;
    private Sensor mHumid;
    private double currentHumid;
    private List<LogData> dataList;
    private List<String> locationOptions;
    private Spinner locationSpinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private void flushAllData()
    {
        deleteLocationTagFile();
        deleteLogFile();
    }
    private void deleteLogFile()
    {
        try // for clearing the files when i make mistakes
        {
            FileOutputStream fos = getActivity().openFileOutput(FILENAME_DATA, Context.MODE_PRIVATE);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void deleteLocationTagFile()
    {
        try // for clearing the files when i make mistakes
        {
            FileOutputStream fos = getActivity().openFileOutput(FILENAME_LOCATIONS, Context.MODE_PRIVATE);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        sensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mHumid, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onStop()
    {
        super.onStop();
        //Saves the data list out to a file when the program stops
        saveOutLocations();
        saveOutReadings();
    }
    private void populateSpinner()
    {
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, locationOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setPrompt("Select a location to tag with");

        locationSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        getActivity()));
    }

    //Runs when the save reading button is pressed and saves a reading to the datalist and saves it to the file.
    private void takeReadings()
    {
        LogData newReading;
        int selectedItem = locationSpinner.getSelectedItemPosition() - 1;
        if(selectedItem >= 0)
        {
            newReading = new LogData(currentTemp, currentPressure, currentHumid, locationOptions.get(selectedItem));
            dataList.add(newReading);
            saveOutReadings();
            Toast.makeText(getActivity(), "Reading saved", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(), "You must select a location", Toast.LENGTH_LONG).show();
        }
    }
    private void addNewLocation()
    {
        String locationName = "";
        EditText userInput = (EditText)getActivity().findViewById(R.id.editTxtNewLocation);
        locationName = userInput.getText().toString();
        if (locationName != "")
        {
            locationOptions.add(locationName);
            Toast.makeText(getActivity(), locationName +" added.", Toast.LENGTH_SHORT).show();
            saveOutLocations();
        }
        else
        {
            Toast.makeText(getActivity(), "To add a location you just give it a name", Toast.LENGTH_LONG).show();
        }
    }
    private void removeSelectedLocation()
    {
        final int locationToRemove = locationSpinner.getSelectedItemPosition() - 1;
        if (locationToRemove >= 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to delete this?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
            {

                public void onClick(DialogInterface dialog, int which)
                {
                    // Do nothing but close the dialog

                    locationOptions.remove(locationToRemove);
                    Toast.makeText(getActivity(), "Location removed", Toast.LENGTH_SHORT).show();
                    saveOutLocations();
                    populateSpinner();
                    dialog.dismiss();
                }

            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        }
        else
        {
            Toast.makeText(getActivity(), "To remove a location you must select one", Toast.LENGTH_LONG).show();
        }
    }
    private void readInLocations()
    {
        locationOptions = FileOperations.readInFile(FILENAME_LOCATIONS,getActivity());
    }
    private void saveOutLocations()
    {
        FileOperations.saveToFile(locationOptions, FILENAME_LOCATIONS, getActivity());
    }
    private void readInReadings()
    {
        dataList = new ArrayList<LogData>();
        try
        {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(getActivity().openFileInput(FILENAME_DATA)));
            dataList = LogData.loadFromFile(inputReader);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    private void saveOutReadings()
    {
        try
        {
            FileOutputStream fos = getActivity().openFileOutput(FILENAME_DATA, Context.MODE_PRIVATE);
            LogData.saveDataList(dataList, fos);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        //Read in the value from the event
        float sensorValue = event.values[0];
        double sensorVal = sensorValue;
        DecimalFormat df = new DecimalFormat("#.00");
        String toDisplay = df.format(sensorVal);
        //Set the corresponding text by checking which sensor raised the event
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                currentTemp = sensorVal;
                TextView tempText = (TextView)getActivity().findViewById(R.id.txtTempReading);
                tempText.setText(toDisplay+ " °C");
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                currentHumid = sensorVal;
                TextView humidText = (TextView)getActivity().findViewById(R.id.txtHumidtyReading);
                humidText.setText(toDisplay + " %");
                break;

            case Sensor.TYPE_PRESSURE:
                currentPressure = sensorVal;
                TextView pressureText = (TextView)getActivity().findViewById(R.id.txtPressureReading);
                pressureText.setText(toDisplay + " hPa");
                break;
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_a, container, false);
    }
    private void setUpSensors()
    {
        PackageManager PM= getActivity().getPackageManager();
        boolean temp = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_AMBIENT_TEMPERATURE);
        boolean pre = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
        boolean humid = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_RELATIVE_HUMIDITY);

        //Set up the sensors for reading in, if the phone has the correct sensors
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (temp)
        {
            mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            sensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else
        {
            TextView tempText = (TextView)getActivity().findViewById(R.id.txtTempReading);
            tempText.setText("Sensor not present on phone");
        }
        if (pre)
        {
            mPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            sensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else
        {
            TextView pressureText = (TextView)getActivity().findViewById(R.id.txtPressureReading);
            pressureText.setText("Sensor not present on phone");
        }
        if (humid)
        {
            mHumid = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            sensorManager.registerListener(this, mHumid, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else
        {
            TextView humidText = (TextView)getActivity().findViewById(R.id.txtHumidtyReading);
            humidText.setText("Sensor not present on phone");
        }



    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        locationOptions = new ArrayList<String>();
        currentHumid = 0;
        currentPressure = 0;
        currentTemp = 0;
        //flushAllData(); // Use to clean out all data


        readInLocations();
        setUpSensors();


        Button logReadingBtn = (Button)getActivity().findViewById(R.id.btnSaveReading);
        // bind the take reading method to the click of the button
        logReadingBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){takeReadings();
            }
        });
        Button addLocationBtn = (Button)getActivity().findViewById(R.id.btnAddNewLocation);
        //Bind the click of the location button to the add new location method
        addLocationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){addNewLocation();
                populateSpinner();
                hideSoftKeyboard();
                EditText userInput = (EditText)getActivity().findViewById(R.id.editTxtNewLocation);
                userInput.setText("");

            }});
        Button removeLocationButton = (Button)getActivity().findViewById(R.id.btnRemoveSelectedLocation);
        removeLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                removeSelectedLocation();
            }
        });
        locationSpinner = (Spinner)getActivity().findViewById(R.id.spinnerLocations);
        locationSpinner.setPrompt("Select a location to tag the reading as");


        // Load in all saved file data
        readInReadings();
        populateSpinner();

    }

    //Hides the keyboard when the add location button is pressed
    private void hideSoftKeyboard()
    {
        if(getActivity().getCurrentFocus()!=null && getActivity().getCurrentFocus() instanceof EditText )
        {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            EditText userInput = (EditText)getActivity().findViewById(R.id.editTxtNewLocation);
            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
        }
    }
    @Override
    public void isNowVisible()
    {
        readInReadings();
    }
}
