package com.example.alicia.drawing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;

public class MainActivity extends Activity implements OnClickListener {

    private ImageButton openBtn, saveBtn, newBtn, deleteBtn;
    private TextView textBox;
    private DrawingView drawView;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = (DrawingView)findViewById(R.id.drawing);
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
//                    System.out.println("UP!!");
                    char toAddOn = drawView.getCharacterReturned();
                    System.out.println(toAddOn);
                    String newText = textBox.getText().toString()+toAddOn;
                    textBox.setText(newText);
                }
                return false;
            }
        }

        );

        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        openBtn = (ImageButton)findViewById(R.id.load_btn);
        openBtn.setOnClickListener(this);

        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        deleteBtn = (ImageButton)findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(this);

        textBox = (TextView)findViewById(R.id.entertext);

        fileName = "";

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.new_btn){
//            System.out.println("NEW BUTTON CLICKED!");
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Please enter a new file name");

            final EditText input1 = new EditText(this);
            newDialog.setView(input1);

            newDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    drawView.clearOldChars();
                    dialog.dismiss();
                    fileName = input1.getText().toString();
                    textBox.setText("");
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        else if(view.getId()==R.id.load_btn){
//            System.out.println("LOAD BUTTON CLICKED!");
            FileChooserDialog dialog = new FileChooserDialog(this);
            dialog.loadFolder(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
            dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
                public void onFileSelected(Dialog source, File file) throws FileNotFoundException {
                    source.hide();
                    Toast toast = Toast.makeText(source.getContext(), "File selected: " + file.getName(), Toast.LENGTH_LONG);
                    toast.show();
                    drawView.loadFile(file);
                    drawView.redraw();
                }
                public void onFileSelected(Dialog source, File folder, String name) {
                    source.hide();
                    Toast toast = Toast.makeText(source.getContext(), "File created: " + folder.getName() + "/" + name, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            dialog.show();
        }
        else if(view.getId()==R.id.save_btn){
//            System.out.println("SAVE BUTTON CLICKED!");
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            final EditText input1 = new EditText(this);
            if(fileName.equals("")){
                saveDialog.setMessage("No file name yet - please enter a new file name to save.");
                saveDialog.setView(input1);
            }
            else{
                saveDialog.setMessage("Save drawing to device Gallery?");
            }
            saveDialog.setPositiveButton("Save", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    if(fileName.equals(""))
                        fileName = input1.getText().toString();

                    drawView.setDrawingCacheEnabled(true);

                    String relativePath = "/DCIM/Camera/"+ fileName +".jpg";
                    File f = new File(Environment.getExternalStorageDirectory() + relativePath);
                    boolean imageSaved = false;
                    try {
                        f.createNewFile();
                        PrintWriter fo = new PrintWriter(f);
                        fo.write(drawView.getSerializedCharacters());
                        fo.close();
                        imageSaved = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(imageSaved){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
        else if(view.getId()==R.id.delete_btn){
//            System.out.println("DELETE BUTTON CLICKED!");
            FileChooserDialog dialog = new FileChooserDialog(this);
            dialog.loadFolder(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
            dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
                public void onFileSelected(Dialog source, File file) {
                    source.hide();
                    file.delete();
                    Toast toast = Toast.makeText(source.getContext(), "File deleted: " + file.getName(), Toast.LENGTH_LONG);
                    toast.show();
                }
                public void onFileSelected(Dialog source, File folder, String name) {
                    source.hide();
                    Toast toast = Toast.makeText(source.getContext(), "File created: " + folder.getName() + "/" + name, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            dialog.show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
