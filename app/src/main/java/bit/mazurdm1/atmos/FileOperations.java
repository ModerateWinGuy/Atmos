package bit.mazurdm1.atmos;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 26-May-15.
 */
public class FileOperations
{

    //takes in a file name and activity and returns a list of the lines that the file contains
    public static List<String> readInFile(String fileName, Activity activity)
    {
        //Create an array to save all the data to
        List<String> fileList = new ArrayList<String>();

        try
        {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(activity.openFileInput(fileName)));
            String inputString;
            while((inputString=inputReader.readLine()) != null)
            {
                fileList.add(inputString); // Add all the data to the list
            }

            inputReader.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return fileList;
    }

    //Takes a list, a filename and the activity and saves the list out to a file of the provided name
    public static void saveToFile(List<String> toSave,String fileName, Activity activity)
    {
        //Read in a list of all used location tags so the spinner can be populated with them
        try
        {
            FileOutputStream fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
            PrintWriter pr = new PrintWriter(fos);

            for(String txt : toSave)
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

}
