package dev.kawcher.myfirebasepractise2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import dev.kawcher.myfirebasepractise2.Database.DBHelper;
import dev.kawcher.myfirebasepractise2.Model.InputModel;

public class MainActivity extends AppCompatActivity {

    private Button logoutBtn, saveBtn, importBtn, exportBtn;
    private EditText nameEt1, nameEt2;
    private ListView listView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    ProgressDialog dialog;
    private String TAG = "TAG";
    private DBHelper dbHelper;
    private String folderDirectory = "/sdcard/new_dir";
    String uomPath = "/sdcard/new_dir/Import_Excel/uom.xls";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFilePermissions();
        init();
        loadData();

        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dbHelper.getAllLocalUser().size()<=0){
                    Toast.makeText(MainActivity.this, "nothing to import", Toast.LENGTH_SHORT).show();
                    return;
                }
                readUomExcelData(uomPath);
            }
        });

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInputExcelFile();
            }
        });


     /*   logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });*/

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input1 = nameEt1.getText().toString();
                String input2 = nameEt2.getText().toString();
                if (input1.isEmpty()) {
                    nameEt1.setError("required");
                    return;
                }
                if (input2.isEmpty()) {
                    nameEt2.setError("required");
                    return;
                }

                InputModel model = new InputModel(input1, input2);

                long id = dbHelper.insertUser(model);

                if (id > 0) {
                    nameEt1.setText("");
                    nameEt2.setText("");
                    loadData();
                } else {
                    Toast.makeText(MainActivity.this, "fail to insert", Toast.LENGTH_SHORT).show();
                }

                /*database.child("FoodList").push().setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "insert", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
            }
        });
    }

    private void loadData() {
        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        ArrayList<InputModel> list1 = dbHelper.getAllLocalUser();

        list.clear();
        for (int i = 0; i < list1.size(); i++) {
            InputModel model = list1.get(i);
            list.add(model.getInput1() + " - " + model.getInput2());
        }
        listView.deferNotifyDataSetChanged();

      /*  database.child("FoodList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String value = snapshot1.getValue().toString();
                    list.add(value);
                }
                listView.deferNotifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    }

    private void init() {
        database = FirebaseDatabase.getInstance().getReference();
        // logoutBtn = findViewById(R.id.logout);
        saveBtn = findViewById(R.id.save);
        listView = findViewById(R.id.listview);
        nameEt1 = findViewById(R.id.name_et);
        nameEt2 = findViewById(R.id.name_et2);
        importBtn = findViewById(R.id.importd);
        exportBtn = findViewById(R.id.export);
        dbHelper = new DBHelper(MainActivity.this);
    }

    private void checkFilePermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionCheck = this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            }
            if (permissionCheck != 0) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
                }
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    private void createInputExcelFile() {
        checkFilePermissions();
        // File filePath = new File(Environment.getExternalStorageDirectory() + "/Demo.xls");
        Workbook wb = new HSSFWorkbook();
        Cell cell = null;
        //CellStyle cellStyle=wb.createCellStyle();
        // cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        // cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //Now we are creating sheet
        Sheet sheet = null;
        sheet = wb.createSheet("Input_Report");
        //Now column and row


        ArrayList<InputModel> list = dbHelper.getAllLocalUser();


        // cell.setCellStyle(cellStyle);

        sheet.setColumnWidth(0, (12 * 200));
        sheet.setColumnWidth(1, (30 * 200));

        for (int i = 0; i < list.size(); i++) {
            Row row1 = sheet.createRow(i);

            cell = row1.createCell(0);
            cell.setCellValue(list.get(i).getInput1());

            cell = row1.createCell(1);
            cell.setCellValue(list.get(i).getInput2());


            sheet.setColumnWidth(0, (12 * 200));
            sheet.setColumnWidth(1, (30 * 200));

        }

       /* String folderName = "Import_Excel";
        String fileName = folderName +"invoice"  + ".xls";


        String path = Environment.getExternalStorageDirectory() + File.separator + folderName + File.separator + fileName;
        Log.e(TAG, "path: "+path );
*/
        String folderName = "Import_Excel";
        String fileName = "uom" + ".xls";
        String path = folderDirectory + File.separator + folderName + File.separator + fileName;

        File file = new File(folderDirectory + File.separator + folderName);
        if (!file.exists()) {
            file.mkdirs();
        }

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(path);
            wb.write(outputStream);
            // ShareViaEmail(file.getParentFile().getName(),file.getName());
            Log.e(TAG, "uom path: " + path);
            Toast.makeText(getApplicationContext(), "Excel Created in " + path, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Not OK", Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
    }

    private void readUomExcelData(String uomPath) {
        File inputFile = new File(uomPath);

        try {


            InputStream inputStream = new FileInputStream(inputFile);
            // XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            Workbook wb = new HSSFWorkbook();
            Workbook workbook = WorkbookFactory.create(inputStream);
            HSSFSheet sheet = (HSSFSheet) workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            StringBuilder sb = new StringBuilder();

            //outter loop, loops through rows

            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                //inner loop, loops through columns
                for (int c = 0; c < cellsCount; c++) {

                    String uomName = getCellAsString(row, c, formulaEvaluator);

                    sb.append(uomName + ", ");
                    /*}*/
                }
                sb.append(":");
            }

            ExcelHelper excelHelper = new ExcelHelper(MainActivity.this);
            ArrayList<InputModel> list = excelHelper.getUomList(sb);
            dbHelper.deleteAllInput();
            for (int i = 0; i < list.size(); i++) {
                dbHelper.insertUser(list.get(i));
            }
            loadData();

        } catch (FileNotFoundException e) {
            Log.e(TAG, "readExcelData: FileNotFoundException. " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "readExcelData: Error reading inputstream. " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "exception: " + e.getMessage());
        }
    }

    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("MM/dd/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = "" + numericValue;
                    }
                    break;
                case STRING:

                    value = "" + cellValue.getStringValue();
                    break;
                case BOOLEAN:

                    String v = "" + cellValue.getBooleanValue();
                    value = v.toLowerCase();
                    break;

                default:

            }
        } catch (NullPointerException e) {

            Log.e(TAG, "getCellAsString: NullPointerException: " + e.getMessage());
        }
        return value;
    }
}