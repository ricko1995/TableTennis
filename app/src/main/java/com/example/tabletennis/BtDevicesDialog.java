package com.example.tabletennis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class BtDevicesDialog extends AppCompatDialogFragment {

    int position;

    public BtDevicesDialog(int position) {
        this.position = position;
    }

    private SelectedDeviceListener listener;
    private Spinner btDeviceSpinner;
    private String deviceNameLocal = "";
    private String macAddressLocal = "";
    private List<String> devicesNameList = new ArrayList<>();
    private List<String> macAddressList = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view =inflater.inflate(R.layout.bt_device_dialog, null);
        builder.setView(view)
                .setTitle("Select Bluetooth Device")
                .setPositiveButton("OK", (dialog, which) -> {
                    int position = btDeviceSpinner.getSelectedItemPosition();
                    deviceNameLocal = devicesNameList.get(position);
                    macAddressLocal = macAddressList.get(position);
                    listener.onConfirmedBtDeviceSelected(position, deviceNameLocal,macAddressLocal);
                })
                .setNegativeButton("Cancel", ((dialog, which) -> {}));

        btDeviceSpinner = view.findViewById(R.id.btDeviceSpinner);

        devicesNameList = BtSend.pairedDevicesName();
        macAddressList = BtSend.pairedDevicesMac();

        ArrayAdapter<String> adapterArray = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, devicesNameList);
        adapterArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        btDeviceSpinner.setAdapter(adapterArray);

        btDeviceSpinner.setSelection(this.position);



        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SelectedDeviceListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement SelectedDeviceListener");
        }
    }

    public interface SelectedDeviceListener{
        void onConfirmedBtDeviceSelected(int pos, String deviceName, String macAddress);
    }
}
