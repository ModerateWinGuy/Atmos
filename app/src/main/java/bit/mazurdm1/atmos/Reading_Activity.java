package bit.mazurdm1.atmos;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.GenericSignatureFormatError;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class Reading_Activity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, SensorEventListener  {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
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

    private Button addLocationBtn;
    private Button logReadingBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        locationOptions = new ArrayList<String>();
        currentHumid = 0;
        currentPressure = 0;
        currentTemp = 0;
        logReadingBtn = (Button)findViewById(R.id.btnSaveReading);
        // bind the take reading method to the click of the button
        logReadingBtn.setOnClickListener(new View.OnClickListener(){        // ERROR is occurring here
            @Override
            public void onClick(View v){takeReadings();
            }});
        addLocationBtn = (Button)findViewById(R.id.btnAddNewLocation);
        //Bind the click of the location button to the add new location method
        addLocationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){addNewLocation();
            }});
        //TODO Work on spinner

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        //Set up the sensors for reading in
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mHumid = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mHumid, SensorManager.SENSOR_DELAY_NORMAL);

        // Load in all saved file data
        try
        {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("SavedDataFile")));
            dataList = LogData.loadFromFile(inputReader);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
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




    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ReadDataFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.reading_, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ReadDataFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ReadDataFragment newInstance(int sectionNumber) {
            ReadDataFragment fragment = new ReadDataFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ReadDataFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_reading, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Reading_Activity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
