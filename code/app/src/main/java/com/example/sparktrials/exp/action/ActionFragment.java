package com.example.sparktrials.exp.action;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.sparktrials.IdManager;
import com.example.sparktrials.QrScannerActivity;
import com.example.sparktrials.R;
import com.example.sparktrials.exp.DraftManager;
import com.example.sparktrials.models.Experiment;

import com.example.sparktrials.models.GeoLocation;
import com.example.sparktrials.models.QrCode;
import com.example.sparktrials.models.Trial;
import com.example.sparktrials.models.TrialBinomial;
import com.google.zxing.WriterException;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.LOCATION_SERVICE;

/**
 * This class handles all the UI related to the action tab - uploading/creating trials
 */
public class ActionFragment extends Fragment implements LocationListener {
    View view;
    TextView trialsNumber;
    TextView trialsCount;
    private ActionFragmentManager manager;
    private IdManager idManager;
    String id;

    Button leftButton;
    Button rightButton;
    Button uploadButton;
    Button recordNumButton;
    Button generateQR;
    Button registerBarcode;
    Button deleteTrials;
    EditText valueEditText;
    Button middleButton;

    LocationManager locationManager;
    boolean hasLocationSet;
    boolean enforceLocation;
    MutableLiveData<GeoLocation> currentLocation;

    /**
     * Initializing actionfragment manager
     * @param experiment
     */
    public ActionFragment(Experiment experiment){
        this.manager= new ActionFragmentManager(experiment);
        hasLocationSet = experiment.hasLocationSet();
        if (hasLocationSet) {
            currentLocation = new MutableLiveData<>();
        } else {
            currentLocation = new MutableLiveData<>(null);
        }
        enforceLocation = experiment.getReqLocation();
    }

    /**
     * Initializes the idManager to get the user ID for the action fragment manager
     * @param context
     */
    @Override
    public void onAttach(@NotNull Context context){
        super.onAttach(context);
        idManager= new IdManager(context);
        id= idManager.getUserId();
        manager.setProfile(id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (manager.getOpen()) {
            view = inflater.inflate(R.layout.fragment_action, container, false);
            trialsNumber=view.findViewById(R.id.trials_completed);
            trialsCount=view.findViewById(R.id.trials_count);
            leftButton = view.findViewById(R.id.action_bar_pass);
            rightButton = view.findViewById(R.id.action_bar_fail);
            uploadButton = view.findViewById(R.id.action_bar_upload_trials);
            recordNumButton = view.findViewById(R.id.action_bar_recordnum);
            generateQR = view.findViewById(R.id.action_bar_generateQR);
            registerBarcode = view.findViewById(R.id.action_bar_register_barcode);
            deleteTrials = view.findViewById(R.id.action_bar_delete_trials);
            valueEditText = view.findViewById(R.id.countvalue_editText);
            middleButton = view.findViewById(R.id.action_bar_addCount);
            manager.setDraftManager(new DraftManager(getActivity()));
            updateView();

            if (hasLocationSet) {
                getLocation();
                final Observer<GeoLocation> nameObserver = new Observer<GeoLocation>() {
                    @Override
                    public void onChanged(@Nullable final GeoLocation newLoc) {
                        if (newLoc != null) {
                            if (manager.isLocationEnforced() && !manager.isWithinRegion(newLoc)) {
                                // If trial locations are enforced to be within the region and the user
                                // is within the region.
                                hideViews();

                                String message = "You are currently outside the region specified by the experiment owner.";
                                trialsCount.setVisibility(View.VISIBLE);
                                trialsCount.setText(message);
                            } else {
                                // If the new location is within radius of experiment region or trials
                                // are not enforced to be within region.
                                updateView();
                                showViews();
                            }
                        }
                    }
                };
                currentLocation.observe(getViewLifecycleOwner(), nameObserver);
            } else {
                showViews();
            }
        }
        else {
            view = inflater.inflate(R.layout.fragment_closed_action, container, false);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (enforceLocation) {
            final Observer<GeoLocation> nameObserver = new Observer<GeoLocation>() {
                @Override
                public void onChanged(@Nullable final GeoLocation newLoc) {
                    String locString = "(" + currentLocation.getValue().getLat() + ", "
                                            + currentLocation.getValue().getLon() + ")";
                    Log.d("Fetched Location", locString);
                }
            };
            currentLocation.observe(this, nameObserver);
        }
    }

    /**
     * saves a QrCode, passed in as a bitmap as a png file on the device
     * @param code
     *  The bitmap referring to the QrCode
     * @param id
     *  The Id of the QrCode, currently used as the name for the QrCode
     * @throws IOException
     */
    public void saveQrCode(Bitmap code, String id, Double value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        code.compress(Bitmap.CompressFormat.PNG, 90, bytes);
        String fileName = "Experiment_" + manager.getTitle() + "_Value_" + value.toString();
        File f = new File(getContext().getExternalFilesDir("QrCodes"), fileName + ".png");
        boolean fc = f.createNewFile();
        if(fc){
            Log.d("File Creation", "Created file " + f.getAbsolutePath());
        } else {
            Log.d("File Creation", "File already exists");
        }
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
    }

    /**
     * Updates the view whenever a user enters a trial. Updates the number of trials displayed on the screen.
     */
    public void updateView(){
        trialsCount.setText("");
        int trials=manager.getPreUploadedNTrials();
        Log.d("NUM Is", String.valueOf(trials));
        int minimumNumberTrials = manager.getMinNTrials();
        //Updates the textview showing the trial count.
        trialsCount.setText("Trials Count: "+(manager.getNTrials()-manager.getPreUploadedNTrials()));
        //If the experiment has a minimum number of trials we add that the trials number
        if (minimumNumberTrials>0)
            trialsNumber.setText(""+trials+"/"+minimumNumberTrials);
        else
            trialsNumber.setText(""+trials);
    }

    /**
     * Makes the required Views visible.
     */
    private void showViews() {
        uploadButton.setVisibility(View.VISIBLE);
        generateQR.setVisibility(View.VISIBLE);
        registerBarcode.setVisibility(View.VISIBLE);
        deleteTrials.setVisibility(View.VISIBLE);
        //If the type of experiment is a binomial trial we present the appropriate UI
        if (manager.getType().equals("binomial trials".toLowerCase())) {
            leftButton.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.VISIBLE);
            //When the user clicks on the pass button, we call the addtrial method in the manager
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager.addBinomialTrial(true, currentLocation.getValue());
                    updateView();
                }
            });
            //If the user clicks on the fail button we add a fail trial
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager.addBinomialTrial(false, currentLocation.getValue());
                    updateView();
                }
            });
            generateQR.setOnClickListener((v) -> {
                binomialQrCodeDialog();
            });
            registerBarcode.setOnClickListener((v) -> {
                binomialRegBarcode();
            });
        } else if (manager.getType().equals("Non-Negative Integer Counts".toLowerCase())) {
            recordNumButton.setVisibility(View.VISIBLE);
            valueEditText.setVisibility(View.VISIBLE);
            //Adds a trial and shows an alert dialog if the user enters an invalid input
            recordNumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String valueString = valueEditText.getText().toString();
                    Integer result;
                    try {
                        result = Integer.parseInt(valueString);
                        manager.addNonNegIntTrial(result, currentLocation.getValue());
                        updateView();
                    } catch (NumberFormatException e) {
                        AlertDialog builder = new AlertDialog.Builder(getContext())
                                .setTitle("ERROR")
                                .setMessage("You must enter a number")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
            });
            generateQR.setOnClickListener((v) -> {
                nonNegQrCountCodeDialog();
            });
            registerBarcode.setOnClickListener((v) -> {
                nonNegRegBarcode();
            });
        } else if (manager.getType().equals("Measurement Trials".toLowerCase())) {
            recordNumButton.setVisibility(View.VISIBLE);
            valueEditText.setVisibility(View.VISIBLE);
            valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER |
                    InputType.TYPE_NUMBER_FLAG_DECIMAL |
                    InputType.TYPE_NUMBER_FLAG_SIGNED);
            //Creates a measururment trial if the input is valid, if not shows an alert dialog box asking the user to enter valid input
            recordNumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double result;
                    String valueString = valueEditText.getText().toString();
                    try {
                        result = Double.parseDouble(valueString);
                        manager.addMeasurementTrial(result, currentLocation.getValue());
                        updateView();
                    } catch (NumberFormatException e) {
                        AlertDialog builder = new AlertDialog.Builder(getContext())
                                .setTitle("ERROR")
                                .setMessage("You must enter a number")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
            });
            generateQR.setOnClickListener((v) -> {
                measureQrCodeDialog();
            });
            registerBarcode.setOnClickListener((v) -> {
                measureRegBarcode();
            });
        } else if (manager.getType().equals("Counts".toLowerCase())) {
            middleButton.setVisibility(View.VISIBLE);
            middleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Adds count trial then updates view
                    manager.addCountTrial(currentLocation.getValue());
                    updateView();
                }
            });
            generateQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            generateQR.setOnClickListener((v) -> {
                countQrCodeDialog();
            });
            registerBarcode.setOnClickListener((v) -> {
                countRegBarcode();
            });
        }
        //Calls the manager uploadtrials method
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.uploadTrials();
                updateView();
            }
        });
        //Calls the manager delete trials method
        deleteTrials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.deleteTrials();
                updateView();
            }
        });
    }

    /**
     * Hides all views that could alter trials (add, delete, generate QR codes, etc.).
     */
    private void hideViews() {
        leftButton.setVisibility(View.INVISIBLE);
        rightButton.setVisibility(View.INVISIBLE);
        uploadButton.setVisibility(View.INVISIBLE);
        recordNumButton.setVisibility(View.INVISIBLE);
        generateQR.setVisibility(View.INVISIBLE);
        registerBarcode.setVisibility(View.INVISIBLE);
        deleteTrials.setVisibility(View.INVISIBLE);
        valueEditText.setVisibility(View.INVISIBLE);
    }

    /**
     * Gets the updated location of the device, and causes onLocationChanged() to be called as
     * a callback function.
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation() {
        try {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,5, this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Called when the location of the device changes
     * @param location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Update the currentLocation of the user
        GeoLocation cLoc = new GeoLocation(location.getLatitude(), location.getLongitude());
        currentLocation.setValue(cLoc);
    }

    /**
     * Generates an alert dialog asking the user what value they want to attach to a QrCode for a
     * binomial trial
     */
    public void binomialQrCodeDialog(){
        AlertDialog.Builder biDialog = new AlertDialog.Builder(getContext());
        biDialog.setTitle("Select QR Code Value");
        String path = getContext().getExternalFilesDir("QrCodes").toString();
        biDialog.setMessage("QrCode will save into \n" + path);
        final Spinner selection = new Spinner(getContext());
        String[] items = new String[]{"Pass", "Fail"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        selection.setAdapter(adapter);
        biDialog.setView(selection);
        biDialog.setPositiveButton("GENERATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = selection.getSelectedItem().toString();
                QrCode generated;
                if(value.equals("Pass")){
                    generated = manager.createQrCodeObject(1.0);
                } else {
                    generated = manager.createQrCodeObject(0.0);
                }
                Bitmap qrMap = null;
                try {
                    qrMap = manager.IdToQrCode(generated.getQrId());
                } catch(WriterException writerException){
                    Log.d("QrGen", writerException.getMessage());
                }
                manager.uploadQR(generated);
                Log.d("Generated", generated.getQrId());
                try {
                    saveQrCode(qrMap, generated.getQrId(), generated.getValue());
                } catch (IOException e) {
                    Log.d("QrSave", e.getMessage());
                }
                dialog.dismiss();
            }
        });
        biDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = biDialog.create();
        alert.show();
    }

    /**
     * Generates an alert dialog asking the user what value they want to attach to a Barcode for a
     * binomial trial
     */
    public void binomialRegBarcode() {
        AlertDialog.Builder biDialog = new AlertDialog.Builder(getContext());
        biDialog.setTitle("Select Bar Code Value");
        final Spinner selection = new Spinner(getContext());
        String[] items = new String[]{"Pass", "Fail"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        selection.setAdapter(adapter);
        biDialog.setView(selection);
        biDialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), QrScannerActivity.class);
                intent.putExtra("ScanReg", 1);
                intent.putExtra("ExpId", manager.getExpId());
                intent.putExtra("Value", selection.getSelectedItem().toString().equals("Pass") ? 1.0 : 0.0);
                intent.putExtra("TrialType", "binomial trials");
                startActivityForResult(intent, 1);
                dialog.dismiss();
            }
        });
        biDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = biDialog.create();
        alert.show();
    }

    /**
     * Generates an alert dialog confirming a user wants to generate a QrCode for count trials
     */
    public void countQrCodeDialog(){
        AlertDialog.Builder coDialog = new AlertDialog.Builder(getContext());
        coDialog.setTitle("Qr code will create a new count trial");
        String path = getContext().getExternalFilesDir("QrCodes").toString();
        coDialog.setMessage("QrCode will save into \n" + path);
        coDialog.setPositiveButton("GENERATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                QrCode generated;
                generated = manager.createQrCodeObject(1.0);
                Bitmap qrMap = null;
                try {
                    qrMap = manager.IdToQrCode(generated.getQrId());
                } catch(WriterException writerException){
                    Log.d("QrGen", writerException.getMessage());
                }
                manager.uploadQR(generated);
                Log.d("Generated", generated.getQrId());
                try {
                    saveQrCode(qrMap, generated.getQrId(), generated.getValue());
                } catch (IOException e) {
                    Log.d("QrSave", e.getMessage());
                }
                Log.d("Generated", generated.getQrId());
                dialog.dismiss();
            }
        });
        coDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = coDialog.create();
        alert.show();
    }

    /**
     * Generates an alert dialog confirming a user wants to register a barcode for a count trial
     */
    public void countRegBarcode() {
        AlertDialog.Builder coDialog = new AlertDialog.Builder(getContext());
        coDialog.setTitle("Barcode will create a new count trial");
        coDialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), QrScannerActivity.class);
                intent.putExtra("ScanReg", 1);
                intent.putExtra("ExpId", manager.getExpId());
                intent.putExtra("Value", 1.0);
                intent.putExtra("TrialType", "counts");
                startActivityForResult(intent, 1);
                dialog.dismiss();
            }
        });
        coDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = coDialog.create();
        alert.show();
    }

    /**
     * Generates an alert dialog asking the user what value they want to attach to a QrCode for a
     * non-negative count trial
     */
    public void nonNegQrCountCodeDialog(){
        AlertDialog.Builder nncoDialog = new AlertDialog.Builder(getContext());
        nncoDialog.setTitle("Enter QR Code Value");
        String path = getContext().getExternalFilesDir("QrCodes").toString();
        nncoDialog.setMessage("QrCode will save into \n" + path);
        final EditText value = new EditText(getContext());
        value.setHint("Count");
        value.setInputType(InputType.TYPE_CLASS_NUMBER);
        nncoDialog.setView(value);
        nncoDialog.setPositiveButton("GENERATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double input = Double.parseDouble(value.getText().toString());
                QrCode generated = manager.createQrCodeObject(input);
                Bitmap qrMap = null;
                try {
                    qrMap = manager.IdToQrCode(generated.getQrId());
                } catch(WriterException writerException){
                    Log.d("QrGen", writerException.getMessage());
                }
                manager.uploadQR(generated);
                Log.d("Generated", generated.getQrId());
                try {
                    saveQrCode(qrMap, generated.getQrId(), generated.getValue());
                } catch (IOException e) {
                    Log.d("QrSave", e.getMessage());
                }
                Log.d("Generated", generated.getQrId());
                dialog.dismiss();
            }
        });
        nncoDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = nncoDialog.create();
        alert.show();
    }

    /**
     * Generates an alert dialog asking the user what value they want to attach to a barcode for a
     * non-negative count trial
     */
    public void nonNegRegBarcode() {
        AlertDialog.Builder nncoDialog = new AlertDialog.Builder(getContext());
        nncoDialog.setTitle("Enter QR Code Value");
        final EditText value = new EditText(getContext());
        value.setHint("Count");
        value.setInputType(InputType.TYPE_CLASS_NUMBER);
        nncoDialog.setView(value);
        nncoDialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), QrScannerActivity.class);
                intent.putExtra("ScanReg", 1);
                intent.putExtra("ExpId", manager.getExpId());
                intent.putExtra("Value", Double.parseDouble(value.getText().toString()));
                intent.putExtra("TrialType", "non-negative integer counts");
                startActivityForResult(intent, 1);
                dialog.dismiss();
            }
        });
        nncoDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = nncoDialog.create();
        alert.show();
    }

    /**
     * Generates an alert dialog asking the user what value they want to attach to a QrCode for a
     * measurement trial
     */
    public void measureQrCodeDialog(){
        AlertDialog.Builder measDialog = new AlertDialog.Builder(getContext());
        measDialog.setTitle("Enter QR Code Value");
        String path = getContext().getExternalFilesDir("QrCodes").toString();
        measDialog.setMessage("QrCode will save into \n" + path);
        final EditText value = new EditText(getContext());
        value.setHint("Measurement");
        value.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        measDialog.setView(value);
        measDialog.setPositiveButton("GENERATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double input = Double.parseDouble(value.getText().toString());
                QrCode generated = manager.createQrCodeObject(input);
                Bitmap qrMap = null;
                try {
                    qrMap = manager.IdToQrCode(generated.getQrId());
                } catch(WriterException writerException){
                    Log.d("QrGen", writerException.getMessage());
                }
                manager.uploadQR(generated);
                Log.d("Generated", generated.getQrId());
                try {
                    saveQrCode(qrMap, generated.getQrId(), generated.getValue());
                } catch (IOException e) {
                    Log.d("QrSave", e.getMessage());
                }
                Log.d("Generated", generated.getQrId());
                dialog.dismiss();
            }
        });
        measDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = measDialog.create();
        alert.show();
    }

    /**
     * Generates an alert dialog asking the user what value they want to attach to a barcode for a
     * measurement trial
     */
    public void measureRegBarcode() {
        AlertDialog.Builder measDialog = new AlertDialog.Builder(getContext());
        measDialog.setTitle("Enter QR Code Value");
        final EditText value = new EditText(getContext());
        value.setHint("Measurement");
        value.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        measDialog.setView(value);
        measDialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), QrScannerActivity.class);
                intent.putExtra("ScanReg", 1);
                intent.putExtra("ExpId", manager.getExpId());
                intent.putExtra("Value", Double.parseDouble(value.getText().toString()));
                intent.putExtra("TrialType", "measurement trials");
                startActivityForResult(intent, 1);
                dialog.dismiss();
            }
        });
        measDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = measDialog.create();
        alert.show();
    }
}