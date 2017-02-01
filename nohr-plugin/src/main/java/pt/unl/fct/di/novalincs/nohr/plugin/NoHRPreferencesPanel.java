/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin;

/*
 * #%L
 * nohr-plugin
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import layout.SpringUtilities;

/**
 * The NoHR preferences panel.
 *
 * @author Nuno Costa
 */
public class NoHRPreferencesPanel extends OWLPreferencesPanel {

    /**
     *
     */
    private static final long serialVersionUID = 5160621423685035123L;

    private static final Dimension MAX_HEIGHT_DIMENSION = new Dimension(Integer.MAX_VALUE, 1);

    private JTextField txtXSBBinDirectory;

    private JTextField txtKoncludeBin;

    private File xsbBinDirectory;

    private File koncludeBin;

    private final NoHRPreferences preferences;

    public NoHRPreferencesPanel() {
        preferences = NoHRPreferences.getInstance();
    }

    @Override
    public void applyChanges() {
        preferences.setXSBBinDirectory(xsbBinDirectory);
        preferences.setKoncludeBin(koncludeBin);
    }

    private JButton createOpenButton() {
        final JButton result = new JButton("Open");
        result.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (preferences.getXSBBinDirectory() != null) {
                    fc.setSelectedFile(preferences.getXSBBinDirectory());
                }
                final int returnVal = fc.showOpenDialog(NoHRPreferencesPanel.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    setXsbDir(fc.getSelectedFile());
                }
            }
        });
        return result;
    }

    private JButton createKoncludeBinOpenButton() {
        final JButton result = new JButton("Open");

        result.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();

                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (preferences.getKoncludeBin() != null) {
                    fc.setSelectedFile(preferences.getKoncludeBin());
                }

                final int returnVal = fc.showOpenDialog(NoHRPreferencesPanel.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    setKoncludeBin(fc.getSelectedFile());
                }
            }
        });

        return result;
    }

    private JTextField createXSBDirectoryTextField(File xsbDir) {
        final JTextField result;
        if (xsbDir == null) {
            result = new JTextField(10);
        } else {
            result = new JTextField(xsbDir.getPath());
        }
        result.setEditable(false);
        return result;
    }

    private JTextField createKoncludeBinTextField(File koncludeBin) {
        final JTextField result;

        if (koncludeBin == null) {
            result = new JTextField(10);
        } else {
            result = new JTextField(koncludeBin.getPath());
        }

        result.setEditable(false);

        return result;
    }

    @Override
    public void dispose() throws Exception {
    }

    @Override
    public void initialise() throws Exception {
        txtXSBBinDirectory = createXSBDirectoryTextField(preferences.getXSBBinDirectory());
        txtKoncludeBin = createKoncludeBinTextField(preferences.getKoncludeBin());

        add(new JLabel("XSB directory"));
        add(txtXSBBinDirectory);
        add(createOpenButton());

        add(new JLabel("Konclude Binary"));
        add(txtKoncludeBin);
        add(createKoncludeBinOpenButton());

        setLayout();
    }

    private void setLayout() {
        for (final Component component : getComponents()) {
            component.setMaximumSize(MAX_HEIGHT_DIMENSION);
        }

        setLayout(new SpringLayout());
        SpringUtilities.makeCompactGrid(this, 2, 3, 3, 3, 3, 3);
    }

    private void setXsbDir(File xsbDir) {
        xsbBinDirectory = xsbDir;
        txtXSBBinDirectory.setText(xsbBinDirectory.getPath());
    }

    private void setKoncludeBin(File koncludeBin) {
        this.koncludeBin = koncludeBin;
        txtKoncludeBin.setText(koncludeBin.getPath());
    }
}
