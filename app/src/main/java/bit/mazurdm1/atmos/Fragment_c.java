package bit.mazurdm1.atmos;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private Button generateGraphButton;

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
        generateGraphButton = (Button) getActivity().findViewById(R.id.btnReGenerateGraph);
        generateGraphButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                regenerateGraph();
            }
        });
        //Automatically populate the graph with all of the data
        List<DataPoint> dataToAdd = createListOfData("");
        DataPoint[] toAdd = dataToAdd.toArray(new DataPoint[dataToAdd.size()]);
        graph.addSeries(new LineGraphSeries(toAdd));
        //series.a
    }

    private void regenerateGraph()
    {
        List<DataPoint> dataToAdd = createListOfData(locationSpinner.getSelectedItem().toString());
        DataPoint[] toAdd = dataToAdd.toArray(new DataPoint[dataToAdd.size()]);
        graph.removeAllSeries();
        graph.addSeries(new LineGraphSeries(toAdd));
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
    private List<DataPoint> createListOfData(String locationOptions)
    {
        // pass in a blank string to add in all data
        List<DataPoint> data = new ArrayList<DataPoint>();
        for(LogData dataPoint : dataList)
        {
            if(locationOptions == "")
            {
                data.add(new DataPoint(dataPoint.getStamp().getTime(), dataPoint.getTemp())); //TODO check if long will work instead of double
            }
            else
            {
                if (dataPoint.getLocationTag() == locationOptions)
                {
                    data.add(new DataPoint(dataPoint.getStamp().getTime(), dataPoint.getTemp()));
                }
            }
        }
        return data;
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
