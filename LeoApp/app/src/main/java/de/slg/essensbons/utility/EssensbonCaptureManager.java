package de.slg.essensbons.utility;

import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import de.slg.essensbons.activity.ScanActivity;

public class EssensbonCaptureManager extends CaptureManager {
    private ScanActivity activity;

    public EssensbonCaptureManager(ScanActivity activity, DecoratedBarcodeView barcodeView) {
        super(activity, barcodeView);
        this.activity = activity;
    }

    @Override
    protected void returnResult(BarcodeResult rawResult) {
        activity.notifyResult(rawResult.getText());
    }
}