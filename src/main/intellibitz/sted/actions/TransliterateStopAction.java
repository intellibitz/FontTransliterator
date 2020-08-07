package sted.actions;

import sted.fontmap.Converter;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TransliterateStopAction
        extends AbstractAction {
    private Converter converter;

    public TransliterateStopAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        stop();
        setEnabled(false);
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    private void stop() {
        if (converter != null && converter.isAlive()) {
            converter.setStopRequested(true);
            converter.interrupt();
            converter.setSuccess(false);
            converter.setMessage("Stopped Conversion");
            converter.fireThreadRunFinished();
        }
    }

}
