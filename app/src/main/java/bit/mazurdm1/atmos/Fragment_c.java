package bit.mazurdm1.atmos;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
        setUpSpinner();
        graph = (GraphView)getActivity().findViewById(R.id.graph);
        generateGraphButton = (Button) getActivity().findViewById(R.id.btnReGenerateGraph);
        generateGraphButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int selectedItem = locationSpinner.getSelectedItemPosition();
                if (selectedItem >= 0)
                {
                    regenerateGraph(locations.get(selectedItem));
                } else
                {
                    regenerateGraph("All");
                }
            }
        });
        //Automatically populate the graph with all of the data
        regenerateGraph("All");
    }

    private void setUpSpinner()
    {
        String[] spinnerOptions = locations.toArray(new String[locations.size()]);
        locationSpinner = (Spinner)getActivity().findViewById(R.id.spinnerFilterLocations);
        ArrayAdapter<String> locationsForSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerOptions);

        locationSpinner.setAdapter(locationsForSpinner);
        locationSpinner.setPrompt("Select a speific Location");
    }

    private void regenerateGraph(String addOption)
    {
        List<DataPoint> dataToAdd = createListOfData(addOption);
        DataPoint[] toAdd = dataToAdd.toArray(new DataPoint[dataToAdd.size()]);
        LineGraphSeries theSeries = new LineGraphSeries(toAdd);
        if (dataToAdd.size()>1) // To avoid null errors from empty lists if the user does not select an option
        {
            theSeries.setThickness(4);
            theSeries.setDrawDataPoints(true);
            theSeries.setColor(Color.BLUE);
            graph.setTitle("Temperate Over Time");
            graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
            graph.getGridLabelRenderer().setVerticalLabelsVisible(true);
            graph.getViewport().setXAxisBoundsManual(true);

            graph.getViewport().setMinX(dataToAdd.get(0).getX());
            graph.getViewport().setMaxX(dataToAdd.get(dataToAdd.size() - 1).getX());

            graph.removeAllSeries();
            graph.addSeries(theSeries);
            graph.refreshDrawableState();
        }
        else
        {
            Toast.makeText(getActivity(), "Two readings needed for graphing", Toast.LENGTH_SHORT).show();
        }
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
    //Populates the locations list
    private void readInLocations()
    {
        locations = FileOperations.readInFile(FILENAME_LOCATIONS,getActivity());
        locations.add(0,"All");
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    /*
     * Method that creates a list data that matches the location filter
     * @pram locationOptions a string to filter which locations to put in the list
     */
    private List<DataPoint> createListOfData(String locationOptions)
    {
        // pass in "All" string to add in all data
        List<DataPoint> data = new ArrayList<DataPoint>();
        // A offset to make the timestamp more manageable
        double offset = dataList.get(0).getStamp().getTime();
        for(LogData dataPoint : dataList)
        {
            if(locationOptions == "All")
            {
                data.add(new DataPoint(dataPoint.getStamp().getTime() - offset, dataPoint.getTemp()));
            }
            else
            {
                if (dataPoint.getLocationTag().equals(locationOptions) )
                {
                    data.add(new DataPoint(dataPoint.getStamp().getTime() - offset, dataPoint.getTemp()));
                }
            }
        }
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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
        readInLocations();
        readInData();
        setUpSpinner();
    }

}
