package bit.mazurdm1.atmos;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

public class Fragment_a extends Fragment implements SensorEventListener
{


    public Fragment_a()
    {
        // Required empty public constructor
    }
    private static final String LOCATION_FILENAME = "Locations";
    private static final String LOG_FILENAME = "SavedDataFile";

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
    private void deleteLogFile()
    {
        try // for clearing the files when i make mistakes
        {
            FileOutputStream fos = getActivity().openFileOutput(LOG_FILENAME, Context.MODE_PRIVATE);
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
            FileOutputStream fos = getActivity().openFileOutput(LOCATION_FILENAME, Context.MODE_PRIVATE);
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
        try
        {
            FileOutputStream fos = getActivity().openFileOutput(LOG_FILENAME, Context.MODE_PRIVATE);
            LogData.saveDataList(dataList, fos);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
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
    private void takeReadings()
    {
        LogData newReading;
        newReading = new LogData(currentTemp, currentPressure, currentHumid, locationOptions.get(locationSpinner.getSelectedItemPosition()));
        dataList.add(newReading);
        Toast.makeText(getActivity(), "Reading saved", Toast.LENGTH_SHORT).show();
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
        }
        else
        {
            Toast.makeText(getActivity(), "To add a location you just give it a name", Toast.LENGTH_LONG).show();
        }
    }
    private void readInLocations()
    {
        try
        {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(getActivity().openFileInput(LOCATION_FILENAME)));
            String inputString;
            while((inputString=inputReader.readLine()) != null)
            {
                locationOptions.add(inputString);
            }

            inputReader.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void saveOutLocations()
    {
        //Read in a list of all used location tags so the spinner can be populated with them
        try
        {
            FileOutputStream fos = getActivity().openFileOutput(LOCATION_FILENAME, Context.MODE_PRIVATE);
            PrintWriter pr = new PrintWriter(fos);

            for(String txt : locationOptions)
            {
                pr.println(txt);
            }

            pr.flush();
            pr.close();
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
        String toDisplay = df.format(sensorVal).toString();
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
    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        locationOptions = new ArrayList<>();
        currentHumid = 0;
        currentPressure = 0;
        currentTemp = 0;
        //deleteLocationTagFile();
        //deleteLogFile();

        //TODO Work on spinner
        readInLocations();

        //Set up the sensors for reading in
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mHumid = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mHumid, SensorManager.SENSOR_DELAY_NORMAL);
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
            }});
        locationSpinner = (Spinner)getActivity().findViewById(R.id.spinnerLocations);
        locationSpinner.setPrompt("Select a location to tag the reading as");


        // Load in all saved file data
        try
        {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(getActivity().openFileInput(LOG_FILENAME)));
            dataList = LogData.loadFromFile(inputReader);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        populateSpinner();

    }


}
