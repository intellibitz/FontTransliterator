package intellibitz.sted.ui;

import intellibitz.sted.event.IMessageEventSource;
import intellibitz.sted.event.IMessageListener;
import intellibitz.sted.event.MessageEvent;
import intellibitz.sted.fontmap.FontMap;
import intellibitz.sted.fontmap.FontMapEntries;
import intellibitz.sted.fontmap.FontMapEntry;
import intellibitz.sted.util.Resources;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MappingTableModel
        extends AbstractTableModel
        implements IMessageEventSource {
    private static final String[] names = new String[]
            {
                    Resources.getResource(Resources.TITLE_TABLE_COLUMN_SYMBOL1),
                    Resources.getResource(Resources.TITLE_TABLE_COLUMN_EQUALS),
                    Resources.getResource(Resources.TITLE_TABLE_COLUMN_SYMBOL2),
                    Resources.getResource(
                            Resources.TITLE_TABLE_COLUMN_FIRST_LETTER),
                    Resources.getResource(
                            Resources.TITLE_TABLE_COLUMN_LAST_LETTER),
                    Resources.getResource(
                            Resources.TITLE_TABLE_COLUMN_FOLLOWED_BY),
                    Resources.getResource(
                            Resources.TITLE_TABLE_COLUMN_PRECEDED_BY)
            };
    private FontMap fontMap;
    private FontMapEntries fontMapEntries;
    private List<FontMapEntry> entries;
    private IMessageListener messageListener;
    private MessageEvent messageEvent;

    public MappingTableModel() {
        super();
        messageEvent = new MessageEvent(this);
    }

    public void setFontMap(FontMap fontMap) {
        this.fontMap = fontMap;
        fontMapEntries = fontMap.getEntries();
        entries = new ArrayList<FontMapEntry>(fontMapEntries.values());
        Collections.sort(entries, Collections.reverseOrder());
        super.fireTableDataChanged();
    }

    public int getRowCount() {
        if (fontMapEntries == null) {
            return 0;
        }
        return fontMapEntries.size();
    }


    public Object getValueAt(int row, int col) {
        if (col == 1) {
            return Resources.EQUALS;
        }
        if (entries != null && !entries.isEmpty()) {
            final FontMapEntry entry = (FontMapEntry) entries.get(row);
            if (col == 0) {
                return entry.getFrom();
            } else if (col == 2) {
                return entry.getTo();
            } else if (col == 3) {
                if (entry.isBeginsWith()) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            } else if (col == 4) {
                if (entry.isEndsWith()) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            } else if (col == 5) {
                final String val = entry.getFollowedBy();
                return val == null ? Resources.EMPTY_STRING : val;
            } else if (col == 6) {
                final String val = entry.getPrecededBy();
                return val == null ? Resources.EMPTY_STRING : val;
            }
            return Resources.EQUALS;
        }
        return Resources.EQUALS;
    }

    public FontMapEntry getValueAt(int row) {
        if (entries != null && !entries.isEmpty()) {
            return (FontMapEntry) entries.get(row);
        }
        return null;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (aValue != null) {
            final FontMapEntry entry = (FontMapEntry) entries.get(rowIndex);
            if (entry != null) {
                switch (columnIndex) {
                    case 0:
                        if (!entry.getFrom().equals(aValue)) {
                            final FontMapEntry edited =
                                    (FontMapEntry) entry.clone();
                            edited.setFrom(aValue.toString());
                            // do a strick check - just like add
                            if (!fontMapEntries.isValid(edited)) {
                                fireMessagePosted(
                                        "Already mapped - Invalid Edit");
                                break;
                            }
                            edited.setFrom(entry.getFrom());
                            edited.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.getUndo().add(edited);
                            entry.setFrom(aValue.toString());
                            entry.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.reKey(edited, entry);
                            fontMap.setDirty(true);
                            super.fireTableCellUpdated(rowIndex, columnIndex);
                        }
                        break;
                    case 2:
                        if (!entry.getTo().equals(aValue)) {
                            final FontMapEntry edited =
                                    (FontMapEntry) entry.clone();
                            edited.setTo(aValue.toString());
                            if (!fontMapEntries.isValidEdit(edited)) {
                                fireMessagePosted(
                                        "Already mapped - Invalid Edit");
                                break;
                            }
                            edited.setTo(entry.getTo());
                            edited.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.getUndo().add(edited);
                            entry.setTo(aValue.toString());
                            entry.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.reKey(edited, entry);
                            fontMap.setDirty(true);
                            super.fireTableCellUpdated(rowIndex, columnIndex);
                        }
                        break;
                    case 3:
                        final boolean begins = (Boolean) aValue;
                        if (!entry.isBeginsWith() == begins) {
                            final FontMapEntry edited =
                                    (FontMapEntry) entry.clone();
                            edited.setBeginsWith(begins);
                            if (!fontMapEntries.isValidEdit(edited)) {
                                fireMessagePosted(
                                        "Already mapped - Invalid Edit");
                                break;
                            }
                            edited.setBeginsWith(entry.isBeginsWith());
                            edited.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.getUndo().add(edited);
                            entry.setBeginsWith(begins);
                            entry.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.reKey(edited, entry);
                            fontMap.setDirty(true);
                            super.fireTableCellUpdated(rowIndex, columnIndex);
                        }
                        break;
                    case 4:
                        final boolean ends = (Boolean) aValue;
                        if (!entry.isEndsWith() == ends) {
                            final FontMapEntry edited =
                                    (FontMapEntry) entry.clone();
                            edited.setEndsWith(ends);
                            if (!fontMapEntries.isValidEdit(edited)) {
                                fireMessagePosted(
                                        "Already mapped - Invalid Edit");
                                break;
                            }
                            edited.setEndsWith(entry.isEndsWith());
                            edited.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.getUndo().add(edited);
                            entry.setEndsWith(ends);
                            entry.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.reKey(edited, entry);
                            fontMap.setDirty(true);
                            super.fireTableCellUpdated(rowIndex, columnIndex);
                        }
                        break;
                    case 5:
                        if (!aValue.equals(entry.getFollowedBy())) {
                            if (entry.getFollowedBy() == null &&
                                    Resources.EMPTY_STRING.equals(aValue)) {
                                break;
                            }
                            final FontMapEntry edited =
                                    (FontMapEntry) entry.clone();
                            edited.setFollowedBy(aValue.toString());
                            if (!fontMapEntries.isValidEdit(edited)) {
                                fireMessagePosted(
                                        "Already mapped - Invalid Edit");
                                break;
                            }
                            edited.setFollowedBy(entry.getFollowedBy());
                            edited.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.getUndo().add(edited);
                            entry.setFollowedBy(aValue.toString());
                            entry.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.reKey(edited, entry);
                            fontMap.setDirty(true);
                            super.fireTableCellUpdated(rowIndex, columnIndex);
                        }
                        break;
                    case 6:
                        if (!aValue.equals(entry.getPrecededBy())) {
                            if (entry.getPrecededBy() == null &&
                                    Resources.EMPTY_STRING.equals(aValue)) {
                                break;
                            }
                            final FontMapEntry edited =
                                    (FontMapEntry) entry.clone();
                            edited.setPrecededBy(aValue.toString());
                            if (!fontMapEntries.isValidEdit(edited)) {
                                fireMessagePosted(
                                        "Already mapped - Invalid Edit");
                                break;
                            }
                            edited.setPrecededBy(entry.getPrecededBy());
                            edited.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.getUndo().add(edited);
                            entry.setPrecededBy(aValue.toString());
                            entry.setStatus(Resources.ENTRY_STATUS_EDIT);
                            fontMapEntries.reKey(edited, entry);
                            fontMap.setDirty(true);
                            super.fireTableCellUpdated(rowIndex, columnIndex);
                        }
                        break;
                }
            }
        }
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return !(col == 1);
    }

    public int getColumnCount() {
        return names.length;
    }

    public String getColumnName(int column) {
        return names[column];
    }

    public FontMap getFontMap() {
        return fontMap;
    }

    private void fireMessagePosted(String message) {
        messageEvent.setMessage(message);
        messageListener.messagePosted(messageEvent);
    }

    public void fireMessagePosted() {
        messageListener.messagePosted(messageEvent);
    }

    public void addMessageListener(IMessageListener messageListener) {
        this.messageListener = messageListener;
    }
}


