package org.contikios.cooja.dialogs;

import org.apache.log4j.Logger;
import org.contikios.cooja.Cooja;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.WWVB.WWVBMedium;
import org.contikios.cooja.WWVB.WWVBTransmitter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

/**
 * please help i am not so good at ui :((((
 *
 * Created by vyasalwar on 8/6/18.
 */

public class WWVBSettings extends JDialog {
    private static Logger logger = Logger.getLogger(WWVBSettings.class);
    private final static Dimension LABEL_SIZE = new Dimension(150, 25);

    private WWVBTransmitter transmitter;
    private Simulation simulation;

    private RadioButtonsBox radioButtonsBox;
    private CheckBoxesBox checkBoxesBox;
    private DateTimeBox dateTimeBox;
    private UpdateBox updateBox;

    private WWVBSettings(Simulation simulation) {
        this.simulation = simulation;
        this.transmitter = null; // new WWVBTransmitter();

        setTitle("WWVB Transmitter Settings");
        setSize(400, 600);
        setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setVisible(false);
            }
        });

        radioButtonsBox = new RadioButtonsBox();
        checkBoxesBox = new CheckBoxesBox();
        dateTimeBox = new DateTimeBox();
        updateBox = new UpdateBox();

        Box main = Box.createVerticalBox();
        main.add(checkBoxesBox.getBox());
        main.add(new JSeparator());
        main.add(radioButtonsBox.getBox());
        main.add(new JSeparator());
        main.add(dateTimeBox.getBox());
        main.add(new JSeparator());
        main.add(updateBox.getBox());

        main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(main);
        pack();

        if (!(simulation.getRadioMedium() instanceof WWVBMedium)){
            checkBoxesBox.disableTransmitterCheckbox();
            updateBox.error("WWVB Medium Required to enable WWVB Transmitter");
        }

    }


    private void enableComponents(){
        radioButtonsBox.enable();
        checkBoxesBox.enable();
        dateTimeBox.enable();
        updateBox.enable();
    }

    private void disableComponents(){
        radioButtonsBox.disable();
        checkBoxesBox.disable();
        dateTimeBox.disable();
        updateBox.disable();
    }

    // Sections of the dialog
    private interface WWVBBoxInterface {
        public Box getBox();
        public void enable();
        public void disable();
    }

    private class CheckBoxesBox implements WWVBBoxInterface {
        private Box buttonControlBox;
        private JCheckBox enableTransmitterCheckbox;
        private LinkedList<JCheckBox> otherCheckBoxes;
        private JSpinner utcOffsetSpinner;

        public CheckBoxesBox(){
            buttonControlBox = Box.createVerticalBox();
            otherCheckBoxes = new LinkedList<>();
            enableTransmitterCheckbox = new JCheckBox("Enable Transmitter");
            enableTransmitterCheckbox.addActionListener(enableTransmitterAction);

            buttonControlBox.add(Box.createHorizontalGlue());
            buttonControlBox.add(enableTransmitterCheckbox);
            buttonControlBox.add(Box.createHorizontalGlue());

            JCheckBox leapSecondCheckbox = new JCheckBox("Enable Leap Second");
            leapSecondCheckbox.addActionListener(leapSecondIndicatorAction);
            buttonControlBox.add(leapSecondCheckbox);
            otherCheckBoxes.add(leapSecondCheckbox);

            SpinnerNumberModel model = new SpinnerNumberModel(0, -.9, .9, .1);
            utcOffsetSpinner = new JSpinner(model);
            utcOffsetSpinner.setMaximumSize(new Dimension(150, 25));
            JLabel offsetLabel = new JLabel("UTC Correction");
            offsetLabel.setLabelFor(utcOffsetSpinner);
            utcOffsetSpinner.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    transmitter.setUtcCorrection(getOffsetValue());
                }
            });

            buttonControlBox.add(utcOffsetSpinner);
            buttonControlBox.add(offsetLabel);

            disable();
        }

        public int getOffsetValue(){
            return (int) Math.floor((10 * (double) utcOffsetSpinner.getValue()));
        }

        public Box getBox() {
            return this.buttonControlBox;
        }

        public void enable(){
            for (JCheckBox checkBox: otherCheckBoxes)
                checkBox.setEnabled(true);

            utcOffsetSpinner.setEnabled(true);
        }

        public void disable(){
            for (JCheckBox checkBox: otherCheckBoxes)
                checkBox.setEnabled(false);

            utcOffsetSpinner.setEnabled(false);
        }

        public void disableTransmitterCheckbox(){
            enableTransmitterCheckbox.setEnabled(false);
        }
    }

    private class RadioButtonsBox implements WWVBBoxInterface {
        private Box radioBox;
        private ButtonGroup dstButtons;

        public RadioButtonsBox(){
            radioBox = Box.createVerticalBox();
            JLabel dstOptionsLabel = new JLabel("DST Options");
            dstOptionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            dstOptionsLabel.setHorizontalAlignment(SwingConstants.LEFT);
            radioBox.add(dstOptionsLabel);
            dstButtons = new ButtonGroup();
            JRadioButton noDstButton = new JRadioButton("No DST (Code = 00)");
            noDstButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    transmitter.setDstCode(WWVBTransmitter.DSTCode.NO_DST);
                }
            });
            noDstButton.setSelected(true);

            JRadioButton dstEndingButton = new JRadioButton("DST Ends Today (Code = 01)");
            dstEndingButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    transmitter.setDstCode(WWVBTransmitter.DSTCode.DST_ENDS);
                }
            });
            JRadioButton dstBeginsButton = new JRadioButton("DST Begins Today (Code = 10)");
            dstBeginsButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    transmitter.setDstCode(WWVBTransmitter.DSTCode.DST_BEGINS);
                }
            });
            JRadioButton dstInEffectButton = new JRadioButton("DST Currently in Effect (Code = 11)");
            dstInEffectButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    transmitter.setDstCode(WWVBTransmitter.DSTCode.DST_IN_EFFECT);
                }
            });

            dstButtons.add(noDstButton);
            dstButtons.add(dstEndingButton);
            dstButtons.add(dstBeginsButton);
            dstButtons.add(dstInEffectButton);

            JPanel radioControlPanel = new JPanel();
            radioControlPanel.setLayout(new BoxLayout(radioControlPanel, BoxLayout.Y_AXIS));
            radioControlPanel.add(noDstButton);
            radioControlPanel.add(dstEndingButton);
            radioControlPanel.add(dstBeginsButton);
            radioControlPanel.add(dstInEffectButton);

            radioBox.add(radioControlPanel);
            disable();
        }

        public Box getBox(){
            return radioBox;
        }

        public void disable(){
            Enumeration<AbstractButton> buttonEnumeration =  dstButtons.getElements();
            while (buttonEnumeration.hasMoreElements()){
                AbstractButton button = buttonEnumeration.nextElement();
                button.setEnabled(false);
            }
        }

        public void enable(){
            Enumeration<AbstractButton> buttonEnumeration =  dstButtons.getElements();
            while (buttonEnumeration.hasMoreElements()){
                AbstractButton button = buttonEnumeration.nextElement();
                button.setEnabled(true);
            }
        }
    }

    private class DateTimeBox {
        private final String[] dateTimeLabelNames = new String[]{
                "Year:", "Month (1-12):", "Day (1-31):", "Hour (0-23):", "Minute (0-59):"};
        private String[] dateTimeValues = new String[]{"1970", "1", "1", "0", "0"};
        private Box dateTimesBox;
        private LinkedList<JFormattedTextField> textFieldList;


        public DateTimeBox() {
            textFieldList = new LinkedList<>();
            dateTimesBox = Box.createVerticalBox();
            for (int i = 0; i < dateTimeLabelNames.length; i++){
                String name = dateTimeLabelNames[i];
                addEntry(dateTimesBox, name, i);
                dateTimesBox.add(Box.createVerticalStrut(10));
            }
            disable();
        }

        public String[] getDateTimeValues(){
            return dateTimeValues;
        }

        private void addEntry(JComponent parent, String name, final int i){

            Box childBox = Box.createHorizontalBox();
            JLabel childLabel = new JLabel(name);
            final JFormattedTextField childTextField = new JFormattedTextField();
            childTextField.setValue(dateTimeValues[i]);
            childBox.add(childLabel);
            childBox.add(childTextField);

            childTextField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    dateTimeValues[i] = childTextField.getText();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    dateTimeValues[i] = childTextField.getText();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    dateTimeValues[i] = childTextField.getText();
                }
            });



            childLabel.setPreferredSize(LABEL_SIZE);
            childTextField.setPreferredSize(LABEL_SIZE);

            textFieldList.add(childTextField);
            parent.add(childBox);
        }

        public Box getBox(){
            return this.dateTimesBox;
        }

        public void enable(){
            for (JFormattedTextField textField: textFieldList) {
                textField.setEnabled(true);
            }
        }

        public void disable(){
            for (JFormattedTextField textField: textFieldList) {
                textField.setEnabled(false);
            }
        }


    }

    private class UpdateBox implements WWVBBoxInterface {
        private Box updateBox;
        private JButton updateButton;
        private JLabel displayResult;

        public UpdateBox(){
            updateBox = Box.createHorizontalBox();
            updateButton = new JButton("Update");
            updateButton.setHorizontalAlignment(SwingConstants.LEFT);
            updateButton.addActionListener(parseDateAction);
            updateBox.setAlignmentX(Component.CENTER_ALIGNMENT);
            updateBox.add(updateButton);

            updateBox.add(Box.createGlue());

            displayResult = new JLabel();
            displayResult.setAlignmentX(Component.RIGHT_ALIGNMENT);
            updateBox.add(displayResult);

            disable();
        }

        public void display(String text){
            displayResult.setForeground(Color.BLACK);
            displayResult.setText(text);
        }

        public void error(String text){
            displayResult.setForeground(Color.RED);
            displayResult.setText(text);
        }

        public Box getBox(){
            return this.updateBox;
        }

        public void enable(){
            updateButton.setEnabled(true);
            display("WWVB Enabled.");
        }

        public void disable(){
            updateButton.setEnabled(false);
            display("WWVB Disabled.");
        }
    }

    public static void showDialog(JDesktopPane parent, Simulation simulation) {
        if (Cooja.isVisualizedInApplet()) {
            return;
        }
        WWVBSettings dialog = new WWVBSettings(simulation);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    // Actions
    private Action parseDateAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String[] dateTimeValues = dateTimeBox.getDateTimeValues();
                int[] daysInMonth = new int[]{
                        31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
                };

                int year = Integer.parseInt(dateTimeValues[0]);
                int month = Integer.parseInt(dateTimeValues[1]) - 1;
                int day = Integer.parseInt(dateTimeValues[2]);
                int hour = Integer.parseInt(dateTimeValues[3]);
                int minute = Integer.parseInt(dateTimeValues[4]);

                // For leap days
                if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
                    daysInMonth[1] += 1;

                if (month < 0 || month > 11 ||
                        day < 1 || day > daysInMonth[month] ||
                        hour < 0 || hour > 23 ||
                        minute < 0 || minute > 59)
                    throw new NumberFormatException();

                transmitter.setDate(year, month, day, hour, minute);
                updateBox.display("Time set success.");

            } catch (NumberFormatException ex){
                updateBox.error("Invalid Date.");
            }

        }
    };

    private Action enableTransmitterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox src = (JCheckBox)e.getSource();

            if (src.isSelected()) {
                transmitter = new WWVBTransmitter();
                simulation.setWWVBTransmitter(transmitter);
                enableComponents();
                parseDateAction.actionPerformed(null);
                updateBox.display("Transmitter enabled.");
            } else {
                transmitter = null;
                simulation.setWWVBTransmitter(null);
                disableComponents();
                simulation.setWWVBTransmitter(null);
                updateBox.display("Transmitter disabled.");
            }
        }
    };

    private Action leapSecondIndicatorAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox src = (JCheckBox)e.getSource();
            if (src.isSelected()) {
                transmitter.setLeapSecondIndicator(true);
                updateBox.display("Leap Second Enabled");
            }
            else {
                transmitter.setLeapSecondIndicator(false);
                updateBox.display("Leap Second Disabled");
            }
        }
    };
}
