package bit.mazurdm1.atmos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 23-May-15.
 */
public class LogData
{
    private double temp;     // In degrees C
    private double pressure; // in HPA
    private double humidity; // In %
    private Timestamp stamp;

    // default constructor for when the time of creation is the timestamp
    public LogData(double sTemp, double sPressure, double sHumidity)
    {
        temp = sTemp;
        pressure = sPressure;
        humidity = sHumidity;
        stamp = new Timestamp(System.currentTimeMillis());
    }
    //Constructor for when timestamp needs to be explicitly set (ie during reading old data)
    public LogData(double sTemp, double sPressure, double sHumidity, int milsTimeStamp)
    {
        temp = sTemp;
        pressure = sPressure;
        humidity = sHumidity;
        stamp = new Timestamp(milsTimeStamp);
    }

    // used for changing the string back into an instance of the LogData class
    private static LogData loadData(String loadString)
    {
        double loadTemp, loadPressure, loadHumidity;
        int timeStamp;
        loadTemp = Double.parseDouble(loadString.split(",")[0]);
        loadPressure = Double.parseDouble(loadString.split(",")[1]);
        loadHumidity = Double.parseDouble(loadString.split(",")[2]);
        timeStamp = Integer.parseInt(loadString.split(",")[3]);

        return new LogData(loadTemp, loadPressure, loadHumidity, timeStamp);
    }

    //Used for converting the data to a string for saving
    @Override
    public String toString()
    {return saveData();}
    private  String saveData()
    {
        return Double.toString(temp) +","+ Double.toString(pressure) +","+Double.toString(humidity)
                +","+ Long.toString(stamp.getTime());
    }

    //The writer will write out the whole list so it expects a FileOutputStream that will overwrite the old file
    public static void saveDataList(List<LogData> list, FileOutputStream fos)
    {
        for( LogData i : list )
        {
            try
            {
                fos.write(i.toString().getBytes());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static List<LogData> loadFromFile(BufferedReader inputReader)
    {
        List<LogData> logDataList = new ArrayList<LogData>();
        //Read in the file anc convert them back to instances of the LogData object then add them to the list
        try {
            String inputString;

            while ((inputString = inputReader.readLine()) != null)
            {
                logDataList.add(loadData(inputString));
            }
            inputReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return logDataList;

    }

}

