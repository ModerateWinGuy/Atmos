package bit.mazurdm1.atmos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class Fragment_b extends Fragment implements FragmentHasBecomeVisible
{
    private static final String LOCATION_FILENAME = "Locations";
    private static final String LOG_FILENAME = "SavedDataFile";
    private List<LogData> dataList;
    private ListView list;
    private BaseAdapter adapter;

    public Fragment_b()
    {
        // Required empty public constructor
    }
    public void readInData()
    {
        dataList = new ArrayList<LogData>();
        try
        {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(getActivity().openFileInput(LOG_FILENAME)));
            dataList = LogData.loadFromFile(inputReader);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_b, container, false);
    }

    public void onActivityCreated (final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        readInData();
        adapter = new BaseAdapter()
        {

            @Override
            public int getCount()
            {
                return dataList.size();
            }

            @Override
            public Object getItem(int i)
            {
                return dataList.get(i);
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

                tempText.setText(df.format(dataList.get(i).getTemp()) +  " C");
                humidText.setText(df.format(dataList.get(i).getHumidity()) + " %");
                pressureText.setText(df.format(dataList.get(i).getPressure()) + " hPa");
                locationText.setText(dataList.get(i).getLocationTag());
                Timestamp ts = dataList.get(i).getStamp();
                timeText.setText(ts.toString());

                return row;
            }
        };
        list = (ListView) getActivity().findViewById(R.id.listDataDisplay);
        list.setAdapter(adapter);

    }


    @Override
    public void isNowVisible()
    {
        readInData();
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                adapter.notifyDataSetChanged();
            }
        });

    }
}
