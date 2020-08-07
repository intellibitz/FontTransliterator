package sted.ui;

import sted.event.FontMapChangeEvent;
import sted.event.FontMapChangeListener;
import sted.event.IStatusListener;
import sted.event.StatusEvent;
import sted.fontmap.FontMap;
import sted.util.Resources;
import sted.widgets.GCButton;
import sted.widgets.MemoryBar;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.StringTokenizer;

public class StatusPanel
        extends JPanel
        implements TableModelListener,
        ListSelectionListener,
        FontMapChangeListener,
        IStatusListener {
    private JProgressBar progressBar;
    private JLabel counter;
    private JLabel flag;
    private JLabel lock;
    private JLabel status;
    private final MemoryBar memoryBar;

    private STEDWindow stedWindow;

    private static final String COUNTER_INIT = "0:0";

    public StatusPanel(STEDWindow stedWindow) {
        super();
        this.stedWindow = stedWindow;
        setBorder(BorderFactory.createLineBorder(getForeground()));
        final GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.ipadx = 1;
        status = new JLabel();
        gridBagLayout.setConstraints(status, gridBagConstraints);
        add(status);
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
        gridBagConstraints.weightx = 0;
        gridBagLayout.setConstraints(progressBar, gridBagConstraints);
        add(progressBar);
        final JPanel panel1 = new JPanel();
        panel1.setBorder(BorderFactory.createLineBorder(getForeground()));
        counter = new JLabel();
        initCounter();
        panel1.add(counter);
        gridBagLayout.setConstraints(panel1, gridBagConstraints);
        add(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createLineBorder(getForeground()));
        flag = new JLabel();
        panel2.add(flag);
        gridBagLayout.setConstraints(panel2, gridBagConstraints);
        add(panel2);
        final JPanel panel3 = new JPanel();
        panel3.setBorder(BorderFactory.createLineBorder(getForeground()));
        lock = new JLabel();
        panel3.add(lock);
        gridBagLayout.setConstraints(panel3, gridBagConstraints);
        add(panel3);
        memoryBar = new MemoryBar();
        gridBagLayout.setConstraints(memoryBar, gridBagConstraints);
        add(memoryBar);
        final Icon imageIcon = Resources
                .getSystemResourceIcon(Resources.getSetting(Resources.ICON_GC));
        final GCButton gcButton = new GCButton
                (imageIcon, Resources.getSystemResourceIcon(
                        Resources.getSetting(Resources.ICON_GC2)));
        gridBagLayout.setConstraints(gcButton, gridBagConstraints);
        add(gcButton);
    }

    public void runMemoryBar() {
        // update memory status every 2 seconds
        final Timer timer = new Timer(2000, memoryBar);
        timer.start();
    }

    public void clear() {
        clearStatus();
        clearProgress();
        initCounter();
        setCleanFlag();
        setLockFlag(true);
    }

    public void setStatus(String msg) {
        status.setText(Resources.SPACE + msg);
    }

    private void clearStatus() {
        status.setText(Resources.EMPTY_STRING);
    }

    public void clearProgress() {
        progressBar.setStringPainted(false);
        progressBar.setMinimum(0);
        progressBar.setMaximum(0);
        progressBar.setValue(0);
    }

    private void initCounter() {
        counter.setText(COUNTER_INIT);
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    private void setTotalCount(int total) {
        if (total < 1) {
            initCounter();
        } else {
            final StringTokenizer stringTokenizer =
                    new StringTokenizer(counter.getText(), Resources.COLON);
            final StringBuffer stringBuffer = new StringBuffer();
            if (stringTokenizer.hasMoreTokens()) {
                stringBuffer.append(stringTokenizer.nextToken());
            } else {
                stringBuffer.append(String.valueOf(0));
            }
            stringBuffer.append(Resources.COLON);
            stringBuffer.append(String.valueOf(total));
            counter.setText(stringBuffer.toString());
        }
    }

    private void setCurrentCount(int curr) {
        if (curr >= 0) {
            final StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(String.valueOf(curr + 1));
            stringBuffer.append(Resources.COLON);
            final StringTokenizer stringTokenizer =
                    new StringTokenizer(counter.getText(), Resources.COLON);
            if (stringTokenizer.hasMoreTokens()) {
                // skip the current count
                stringTokenizer.nextToken();
                // keep the total count
                stringBuffer.append(stringTokenizer.nextToken());
            }
            counter.setText(stringBuffer.toString());
        }
    }

    public void setNeatness(FontMap fontMap) {
        clearProgress();
        setLockFlag(!fontMap.isFileWritable());
        if (fontMap.isDirty()) {
            setDirtyFlag();
        } else {
            setCleanFlag();
        }
    }

    private void setCleanFlag() {
        flag.setIcon(Resources.getCleanIcon());
    }

    private void setDirtyFlag() {
        flag.setIcon(Resources.getDirtyIcon());
    }

    public void setLockFlag(boolean flag) {
        if (flag) {
            lock.setIcon(Resources.getLockIcon());
        } else {
            lock.setIcon(Resources.getUnLockIcon());
        }
    }

    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {
        final TableModel tableModel = (TableModel) e.getSource();
        setTotalCount(tableModel.getRowCount());
        setNeatness(stedWindow.getDesktop()
                .getFontMap());
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e) {
        final ListSelectionModel listSelectionModel =
                (ListSelectionModel) e.getSource();
        setCurrentCount(listSelectionModel.getMaxSelectionIndex());
    }

    public void stateChanged(FontMapChangeEvent e) {
        setNeatness(e.getFontMap());
    }

    public void statusPosted(StatusEvent event) {
        setStatus(event.getStatus());
    }
}
