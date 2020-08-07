package sted.widgets;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

/**
 * Component to be used as tabComponent; Contains a JLabel to show the text and
 * a JButton to close the tab it belongs to
 */
public class ButtonTabComponent
        extends JPanel
        implements ActionListener {
    private JLabel tabTitle;
    protected ActionListener actionListener = null;

    public ButtonTabComponent(String title, final JTabbedPane pane) {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        setOpaque(false);

        //make JLabel read titles from JTabbedPane
        tabTitle = new JLabel(title);
        add(tabTitle);
        //add more space between the tabTitle and the button
        tabTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        //tab button
        JButton tabButton = new TabButton();
        //Close the proper tab by clicking the button
        tabButton.addActionListener(this);
        add(tabButton);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    public JLabel getTabTitle() {
        return tabTitle;
    }

    public void actionPerformed(ActionEvent e) {
        //TODO: HACKETY HACK! DOUBLE CHECK THIS IS SAFE..
        e.setSource(this);
        actionListener.actionPerformed(e);
    }

    public void addActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    private class TabButton
            extends JButton {
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }

        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1,
                    getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta,
                    getHeight() - delta - 1);
            g2.dispose();
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}


