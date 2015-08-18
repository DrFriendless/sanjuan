// Copyright (C) 2005  John Farrell
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package sanjuan;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.net.URL;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.event.*;

/**
 * @author John Farrell (friendless@optushome.com.au)
 */
public class NumberOfPlayersDialog extends JDialog {
    private JFrame frame;
    private Locale[] languages;
    private JComboBox defaultLanguage;
    private JLabel players;
    private PlayerWidget[] playerWidgets;
    private ButtonGroup countGroup;
    private JButton ok;

    NumberOfPlayersDialog(JFrame frame) {
        super(frame, "San Juan", true);
        this.frame = frame;
        JPanel panel = new JPanel(new BorderLayout(Constants.GAP, Constants.GAP));
        panel.setBorder(Constants.EMPTY_BORDER);
        JPanel top = new JPanel(new HCodeLayout("", Constants.GAP));
        defaultLanguage = buildLanguageComboBox();
        top.add(buildNumberOfPlayersWidget());
        top.add("x", new JPanel());
        top.add(defaultLanguage);
        defaultLanguage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setUILocale(getDefaultLocale());
            }
        });
        panel.add(top, BorderLayout.NORTH);
        JPanel players = new JPanel(new GridLayout(2, 2, Constants.GAP, Constants.GAP));
        playerWidgets = new PlayerWidget[4];
        for (int i=0; i<4; i++) {
            players.add(playerWidgets[i] = new PlayerWidget());
        }
        panel.add(players, BorderLayout.CENTER);
        getContentPane().add(panel);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttons.add(ok);
        panel.add(buttons, BorderLayout.SOUTH);
        pack();
    }

    private void setUILocale(Locale locale) {
        players.setText(Translation.getResource(locale, "label.players"));
        ok.setText(Translation.getResource(locale, "button.ok"));
        String newRobot = Translation.getResource(locale, "label.robot");
        for (int i=0; i<playerWidgets.length; i++) {
            playerWidgets[i].setRobotString(newRobot, locale);
        }
    }

    class PlayerWidget extends JPanel {
        private JComboBox playerTypeCombo;
        private JComboBox lang;
        private String robot = Translation.inDefaultLanguage("label.robot");

        PlayerWidget() {
            super(new HCodeLayout("", Constants.GAP));
            playerTypeCombo = new JComboBox(new String[]{robot});
            playerTypeCombo.setEditable(true);
            JTextField tf = (JTextField) playerTypeCombo.getEditor().getEditorComponent();
            tf.getDocument().addDocumentListener(
                    new DocumentListener() {
                        public void insertUpdate(DocumentEvent e) {
                            handle(e);
                        }

                        public void removeUpdate(DocumentEvent e) {
                            handle(e);
                        }

                        public void changedUpdate(DocumentEvent e) {
                            handle(e);
                        }

                        private void handle(DocumentEvent e) {
                            try {
                                textUpdated(e.getDocument().getText(0, e.getDocument().getLength()));
                            } catch (BadLocationException ex) {
                            }
                        }
                    }
            );
            add(playerTypeCombo);
            lang = buildLanguageComboBox();
            lang.setEnabled(false);
            playerTypeCombo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() != ItemEvent.SELECTED) return;
                    textUpdated(playerTypeCombo.getSelectedItem());
                }
            });
            add(lang);
        }

        private void textUpdated(Object item) {
            lang.setEnabled(!robot.equals(item.toString()));
        }

        AbstractPlayer getPlayer(JFrame frame) {
            if (playerTypeCombo.getSelectedItem().toString().equals(Translation.inDefaultLanguage("label.robot"))) {
                return new ComputerPlayer();
            } else {
                return new HumanPlayer(frame, playerTypeCombo.getSelectedItem().toString(), (Locale) lang.getSelectedItem());
            }
        }

        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            playerTypeCombo.setEnabled(enabled);
            lang.setEnabled(enabled && !robot.equals(playerTypeCombo.getSelectedItem().toString()));
        }

        public void setRobotString(String newRobot, Locale locale) {
            Object value = playerTypeCombo.getSelectedItem();
            if (value.equals(robot)) {
                value = newRobot;
                lang.setSelectedItem(locale);
            }
            playerTypeCombo.setModel(new DefaultComboBoxModel(new String[] { newRobot }));
            playerTypeCombo.setSelectedItem(value);
            robot = newRobot;
        }
    }

    private Component buildNumberOfPlayersWidget() {
        JPanel panel = new JPanel(new HCodeLayout());
        panel.add(players = new JLabel(Translation.inDefaultLanguage("label.players")));
        countGroup = new ButtonGroup();
        JRadioButton[] counts = new JRadioButton[3];
        for (int i=0; i<counts.length; i++) {
            String text = "" + (i+2);
            counts[i] = new JRadioButton(text);
            counts[i].setSelected(i == counts.length - 1);
            counts[i].setActionCommand(text);
            counts[i].setMnemonic(KeyEvent.VK_2 + i);
            countGroup.add(counts[i]);
            panel.add(counts[i]);
            counts[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int count = Integer.parseInt(e.getActionCommand());
                    for (int i=0; i<playerWidgets.length; i++) {
                        playerWidgets[i].setEnabled(i < count);
                    }
                }
            });
        }
        return panel;
    }

    private JComboBox buildLanguageComboBox() {
        if (languages == null) {
            languages = getLanguages();
        }
        JComboBox langs = new JComboBox(languages);
        langs.setRenderer(new FlagRenderer());
        langs.setSelectedItem(Locale.ENGLISH);
        return langs;
    }

    public Locale getDefaultLocale() {
        return (Locale) defaultLanguage.getSelectedItem();
    }

    public AbstractPlayer[] getPlayers() {
        int noofPlayers = Integer.parseInt(countGroup.getSelection().getActionCommand());
        AbstractPlayer[] result = new AbstractPlayer[noofPlayers];
        for (int i=0; i<result.length; i++) {
            result[i] = playerWidgets[i].getPlayer(frame);
        }
        return result;
    }

    Locale[] getLanguages() {
        List<Locale> locales = new ArrayList<Locale>();
        ResourceBundle lang = ResourceBundle.getBundle("Languages");
        for (Enumeration<String> en = lang.getKeys(); en.hasMoreElements(); ) {
            String key = en.nextElement();
            locales.add(new Locale(key));
        }
        return locales.toArray(new Locale[locales.size()]);
    }

    class FlagRenderer extends JLabel implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setOpaque(true);
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            Locale locale = (Locale) value;
            setText("");
            URL u = getClass().getClassLoader().getResource("Images/flags/" + locale.getLanguage() + ".png");
            setIcon(new ImageIcon(u));
            return this;
        }
    }
}
