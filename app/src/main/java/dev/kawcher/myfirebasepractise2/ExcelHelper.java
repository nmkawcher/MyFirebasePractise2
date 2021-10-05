package dev.kawcher.myfirebasepractise2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;

import dev.kawcher.myfirebasepractise2.Model.InputModel;

public class ExcelHelper {
    private Context context;
    private String TAG="TAG";

    public ExcelHelper(Context context) {
        this.context = context;
    }

    private ArrayList<InputModel> uomList;

    //due another good receive

    public ArrayList<InputModel> getUomList(StringBuilder mStringBuilder) {
        uomList = new ArrayList<>();

        // splits the sb into rows.
        String[] rows = mStringBuilder.toString().split(":");

        //Add to the ArrayList<XYValue> row by row
        for (int i = 0; i < rows.length; i++) {
            //Split the columns of the rows
            String[] columns = rows[i].split(",");

            //use try catch to make sure there are no "" that try to parse into doubles.
            try {



                String input1 = String.valueOf(columns[0]);
                String input2 = String.valueOf(columns[1]);


                //add the the uploadData ArrayList
                uomList.add(new InputModel(input1,input2));

            } catch (NumberFormatException e) {

                Log.e(TAG, "parseStringBuilder: NumberFormatException: " + e.getMessage());

            }
        }

        return uomList;
    }


}
