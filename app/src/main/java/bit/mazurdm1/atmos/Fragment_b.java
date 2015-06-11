package bit.mazurdm1.atmos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class Fragment_b extends Fragment implements FragmentHasBecomeVisible
{
    private static final String FILENAME_LOCATIONS = "Locations";
    private static final String FILENAME_DATA = "SavedDataFile";
    private List<LogData> dataList;
    private List<LogData> displayData;
    private List<String> locations;
    private ListView list;
    private Spinner locationSpinner;
    private BaseAdapter adapter;

    public Fragment_b()
    {
        // Required empty public constructor
    }

    private void readInData()
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

    //Pass in a string to filter for that location, or the word "All" for all data
    private void filterDisplayData(String filter)
    {
        displayData = new ArrayList<LogData>();
        for (LogData item : dataList)
        {
            if(filter != "All")
            {
                if (item.getLocationTag().equals(filter))
                {
                    displayData.add(item);
                }
            }
            else
            {
                displayData.add(item);
            }
        }
    }
    private void readInLocations()
    {
        locations = FileOperations.readInFile(FILENAME_LOCATIONS,getActivity());
        locations.add(0,"All");
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
    private void populateSpinner()
    {

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setPrompt("Location filter");

        locationSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        getActivity()));
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                refreshList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_b, container, false);
    }

    public void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        readInData();
        readInLocations();
        locationSpinner = (Spinner)getActivity().findViewById(R.id.spinnerlocationFilter);
        filterDisplayData("All");
        adapter = new BaseAdapter()
        {
            @Override
            public int getCount()
            {
                return displayData.size();
            }

            @Override
            public Object getItem(int i)
            {
                return displayData.get(i);
            }

            @Override
            public long getItemId(int i)
            {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup)
            {
                LayoutInflater inflater = getLayoutInflater(savedInstanceState);
                View row = inflater.inflate(R.layout.readings_list_layout, viewGroup, false);
                TextView tempText = (TextView) row.findViewById(R.id.txtTempText);
                TextView humidText = (TextView) row.findViewById(R.id.txtHumidityText);
                TextView pressureText = (TextView) row.findViewById(R.id.txtPressureTxt);
                TextView locationText = (TextView) row.findViewById(R.id.txtLocationText);
                TextView timeText = (TextView) row.findViewById(R.id.txtTimeStampText);
                DecimalFormat df = new DecimalFormat("#.00");

                tempText.setText(df.format(displayData.get(i).getTemp()) + " C");
                humidText.setText(df.format(displayData.get(i).getHumidity()) + " %");
                pressureText.setText(df.format(displayData.get(i).getPressure()) + " hPa");
                locationText.setText(displayData.get(i).getLocationTag());
                Timestamp ts = displayData.get(i).getStamp();
                timeText.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ts));

                return row;
            }
        };
        list = (ListView) getActivity().findViewById(R.id.listDataDisplay);
        list.setAdapter(adapter);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final int selectedItem = i;

                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to delete this?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {

                        public void onClick(DialogInterface dialog, int which)
                        {
                            // Remove the deleted item and save the new dataset back to the file
                            dataList.remove(dataList.indexOf(displayData.get(selectedItem)));

                            Toast.makeText(getActivity(), "Reading", Toast.LENGTH_SHORT).show();
                            refreshList();
                            saveOutReadings();

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
                    return false;
                }
            }
        );

    }

    private void refreshList()
    {
        int selectedItem = locationSpinner.getSelectedItemPosition() - 1;
        if(selectedItem >= 0)
        {
            filterDisplayData(locations.get(selectedItem));

        }
        else
        {
            filterDisplayData("All");
        }


        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                adapter.notifyDataSetChanged();
            }
        });
    }

    //Method that is called when the fragment is swiped too, so that if any new readings have been added they can be loaded up and displayed in the list
    @Override
    public void isNowVisible()
    {
        readInData();
        readInLocations();
        refreshList();
        populateSpinner();

    }
}
