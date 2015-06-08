package bit.mazurdm1.atmos;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Fragment_c extends Fragment implements FragmentHasBecomeVisible
{
    private static final String FILENAME_LOCATIONS = "Locations";
    private static final String FILENAME_DATA = "SavedDataFile";

    private List<LogData> dataList;
    private List<String> locations;
    private GraphView graph;
    private Spinner locationSpinner;

    public Fragment_c()
    {
        // Required empty public constructor
    }

    // Use the onActivityCreated method for setup as the main activity has always been fully created before it is run so it avoids
    // methods not having access to the main activity
    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        readInData();
        readInLocations();
        locationSpinner = (Spinner)getActivity().findViewById(R.id.spinnerLocations);
        graph = new GraphView(getActivity());
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        //series.a
    }

    private void readInData()
    {
        dataList = new ArrayList<LogData>();
        try
        {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(getActivity().openFileInput(FILENAME_DATA)));
            dataList = LogData.loadFromFile(inputReader);
            inputReader.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void readInLocations()
    {
        locations = FileOperations.readInFile(FILENAME_LOCATIONS,getActivity());
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_c_layout, container, false);
    }


    @Override
    public void onDetach()
    {
        super.onDetach();

    }

    @Override
    public void isNowVisible()
    {
        ArrayAdapter<String> locationsForSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, locations);
        locationsForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationsForSpinner);
    }

}
