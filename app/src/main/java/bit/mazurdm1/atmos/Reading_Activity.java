package bit.mazurdm1.atmos;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class Reading_Activity extends ActionBarActivity
        implements  SensorEventListener  {

    private CharSequence mTitle;
    private SensorManager sensorManager;
    private Sensor mPressure;
    private double currentPressure;
    private Sensor mTemp;
    private double currentTemp;
    private Sensor mHumid;
    private double currentHumid;
    private List<LogData> dataList;
    private List<String> locationOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reading);

        locationOptions = new ArrayList<String>();
        currentHumid = 0;
        currentPressure = 0;
        currentTemp = 0;

        //TODO Work on spinner


        //Set up the sensors for reading in
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mHumid = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mHumid, SensorManager.SENSOR_DELAY_NORMAL);
        Button logReadingBtn = (Button)findViewById(R.id.btnSaveReading);
        // bind the take reading method to the click of the button
        logReadingBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){takeReadings();
            }
        });
        Button addLocationBtn = (Button)this.findViewById(R.id.btnAddNewLocation);
        //Bind the click of the location button to the add new location method
        addLocationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){addNewLocation();
            }});


        // Load in all saved file data
        try
        {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("SavedDataFile")));
            dataList = LogData.loadFromFile(inputReader);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        sensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mHumid, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    protected void onStop()
    {
        //Saves the data list out to a file when the program stops
        try
        {
            FileOutputStream fos = openFileOutput("SavedDataFile", Context.MODE_PRIVATE);
            LogData.saveDataList(dataList, fos);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    private void takeReadings()
    {
        LogData newReading;
        newReading = new LogData(currentTemp, currentPressure, currentHumid);
        dataList.add(newReading);
    }
    private void addNewLocation()
    {
        String locationName = "";
        EditText userInput = (EditText)findViewById(R.id.editTxtNewLocation);
        locationName = userInput.getText().toString();
        if (locationName != "")
        {
            locationOptions.add(locationName);
        }
        else
        {
            Toast.makeText(this, "To add a location you just give it a name", Toast.LENGTH_LONG).show();
        }
    }


    private void readInLocations()//TODO Test reading in from file for tag options
    {
        try
        {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("Locations")));
            String inputString;
            while((inputString=inputReader.readLine()) != null)
            {
                locationOptions.add(inputString);
            }
        }
        catch(Exception ex) // TODO add specific error catches
        {
            throw new Error("Generic error, I'm a bad person");
        }
    }


    private void saveOutLocations()//TODO Test Saving out locations works
    {
        FileOutputStream fos = null;
        try
        {
            fos = openFileOutput("Locations", Context.MODE_PRIVATE);
            for(String txt : locationOptions)
            {
                fos.write(txt.getBytes());
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }





    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }






    @Override
    public void onSensorChanged(SensorEvent event)
    {
        //Read in the value from the event
        float sensorValue = event.values[0];
        double sensorVal = sensorValue;
        DecimalFormat df = new DecimalFormat("#.00");
        String toDisplay = df.format(sensorVal).toString();
        //Set the corrasponding text by checking which sensor raised the event
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                currentTemp = sensorVal;
                TextView tempText = (TextView)findViewById(R.id.txtTempReading);
                tempText.setText(toDisplay);
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                currentHumid = sensorVal;
                TextView humidText = (TextView)findViewById(R.id.txtHumidtyReading);
                humidText.setText(toDisplay);
                break;

            case Sensor.TYPE_PRESSURE:
                currentPressure = sensorVal;
                TextView pressureText = (TextView)findViewById(R.id.txtPressureReading);
                pressureText.setText(toDisplay);
                break;

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
