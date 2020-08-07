package sted.ui;

import sted.event.FontMapChangeEvent;
import sted.event.FontMapChangeListener;
import sted.fontmap.FontMap;
import sted.fontmap.FontMapEntry;
import sted.util.Resources;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;

public class MappingRulesPanel
        extends JPanel
        implements TableModelListener,
        FontMapChangeListener,
        ListSelectionListener {
    private FontMap fontMap;
    private JTextField followedText;
    private JTextField precededText;
    private JTextField word2;
    private JTextField word1;
    private JCheckBox beginsWithCheck;
    private JCheckBox endsWithCheck;
    private TableModel tableModel;
    private JLabel ruleTitle;
    private JLabel followedByTitle;
    private JLabel precededByTitle;

    public MappingRulesPanel() {
        super();
    }

    public void init() {
        final TitledBorder titledBorder = BorderFactory.createTitledBorder(
                Resources.getResource(Resources.TITLE_MAPPING_RULE));
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        setBorder(titledBorder);

        final GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);

        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        //
        word1 = new JTextField();
        word1.setEditable(false);
        word1.setEnabled(false);
        word1.setHorizontalAlignment(JLabel.RIGHT);
        gridBagLayout.setConstraints(word1, gridBagConstraints);
        add(word1);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        JLabel eqLabel = new JLabel(" = ");
        gridBagLayout.setConstraints(eqLabel, gridBagConstraints);
        add(eqLabel);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        word2 = new JTextField();
        word2.setEditable(false);
        word2.setEnabled(false);
        gridBagLayout.setConstraints(word2, gridBagConstraints);
        add(word2);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 1;
        ruleTitle = new JLabel(Resources.RULE_TITLE);
        gridBagLayout.setConstraints(ruleTitle, gridBagConstraints);
        add(ruleTitle);

        //
        //
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridwidth = 2;
        beginsWithCheck = new JCheckBox();
        beginsWithCheck.setText(Resources.getResource(
                Resources.TITLE_TABLE_COLUMN_FIRST_LETTER));
        beginsWithCheck.setEnabled(false);
        gridBagLayout.setConstraints(beginsWithCheck, gridBagConstraints);
        add(beginsWithCheck);

        gridBagConstraints.gridx = 2;
//        gridBagConstraints.gridwidth = 2;
        endsWithCheck = new JCheckBox();
        endsWithCheck.setText(Resources.getResource(
                Resources.TITLE_TABLE_COLUMN_LAST_LETTER));
        endsWithCheck.setEnabled(false);
        gridBagLayout.setConstraints(endsWithCheck, gridBagConstraints);
        add(endsWithCheck);
        //
        //
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridwidth = 2;
        followedByTitle = new JLabel(Resources.FOLLOWED_BY);
        followedByTitle.setEnabled(false);
        gridBagLayout.setConstraints(followedByTitle, gridBagConstraints);
        add(followedByTitle);

        gridBagConstraints.gridx = 2;
//        gridBagConstraints.gridwidth = 2;
        followedText = new JTextField();
        followedText.setEditable(false);
        followedText.setEnabled(false);
        gridBagLayout.setConstraints(followedText, gridBagConstraints);
        add(followedText);

        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridwidth = 2;
        precededByTitle = new JLabel(Resources.PRECEDED_BY);
        precededByTitle.setEnabled(false);
        gridBagLayout.setConstraints(precededByTitle, gridBagConstraints);
        add(precededByTitle);

        gridBagConstraints.gridx = 2;
//        gridBagConstraints.gridwidth = 2;
        precededText = new JTextField();
        precededText.setEditable(false);
        precededText.setEnabled(false);
        //
        gridBagLayout.setConstraints(precededText, gridBagConstraints);
        add(precededText);
        //
        setVisible(true);
    }

    public void load() {
    }

    private void setFontMap(FontMap fontMap) {
        this.fontMap = fontMap;
        clear();
        reset();
    }

    private void reset() {
        word1.setFont(fontMap.getFont1());
        word2.setFont(fontMap.getFont2());
        followedText.setFont(fontMap.getFont2());
        precededText.setFont(fontMap.getFont2());
        ruleTitle.setFont(fontMap.getFont2());
    }

    private void clear() {
        word1.setText(Resources.EMPTY_STRING);
        word2.setText(Resources.EMPTY_STRING);
        followedText.setText(Resources.EMPTY_STRING);
        precededText.setText(Resources.EMPTY_STRING);
        beginsWithCheck.setSelected(false);
        endsWithCheck.setSelected(false);
        ruleTitle.setText(Resources.RULE_TITLE);
    }

    private void load(FontMapEntry entry) {
        clear();
        if (entry != null) {
            word1.setText(entry.getFrom());
            word2.setText(entry.getTo());
            if (entry.isRulesSet()) {
                ruleTitle.setText("If <" + word1.getText() + "> is: ");
                beginsWithCheck.setSelected(entry.isBeginsWith());
                endsWithCheck.setSelected(entry.isEndsWith());
                String val = entry.getFollowedBy();
                if (val != null) {
                    followedText.setText(val);
                }
                val = entry.getPrecededBy();
                if (val != null) {
                    precededText.setText(val);
                }
            }
            updateUI();
        }
    }

    public void stateChanged(FontMapChangeEvent e) {
        setFontMap(e.getFontMap());
    }

    public void tableChanged(TableModelEvent e) {
        tableModel = (TableModel) e.getSource();
        if (tableModel.getValueAt(0, 0) == null) {
            setEnabled(false);
        } else {
            load(((MappingTableModel) tableModel).getValueAt(e.getFirstRow()));
            setEnabled(true);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        final ListSelectionModel listSelectionModel =
                (ListSelectionModel) e.getSource();
        final int row = listSelectionModel.getMinSelectionIndex();
        if (row > -1) {
            load(((MappingTableModel) tableModel).getValueAt(row));
        }
    }

}

