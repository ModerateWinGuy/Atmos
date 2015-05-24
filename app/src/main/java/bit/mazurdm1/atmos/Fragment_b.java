package bit.mazurdm1.atmos;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_b extends Fragment
{
    private static final String LOCATION_FILENAME = "Locations";
    private static final String LOG_FILENAME = "SavedDataFile";


    public Fragment_b()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_b, container, false);
    }


}
