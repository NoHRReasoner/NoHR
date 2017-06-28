package pt.unl.fct.di.novalincs.nohr.plugin;

/**
 *
 */
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
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import layout.SpringUtilities;
import org.protege.editor.owl.model.event.EventType;
import org.semanticweb.owlapi.model.AxiomType;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLInferenceEngine;

/**
 * The NoHR preferences panel.
 *
 * @author Nuno Costa
 */
public class NoHRPreferencesPanel extends OWLPreferencesPanel {

    private static final long serialVersionUID = 5160621423685035123L;
    private static final Dimension MAX_HEIGHT_DIMENSION = new Dimension(Integer.MAX_VALUE, 1);

    private DLInferenceEngine dLInferenceEngine;
    private JComboBox<DLInferenceEngine> dLInferenceEngineComboBox;
    private boolean dLInferenceEngineEL;
    private JCheckBox dLInferenceEngineELCheckBox;
    private boolean dLInferenceEngineQL;
    private JCheckBox dLInferenceEngineQLCheckBox;
    private boolean dLInferenceEngineRL;
    private JCheckBox dLInferenceEngineRLCheckBox;
    private File koncludeBinary;
    private JTextField koncludeBinaryTextField;
    private final NoHRPreferences preferences;
    private File xsbDirectory;
    private JTextField xsbDirectoryTextField;

    private JPanel reasonerPanel;
    private JPanel unsupportedAxiomsPanel;

    private boolean ignoreAllUnsupportedAxioms;
    private final Set<AxiomType<?>> ignoredUnsupportedAxioms;
    private final Set<JCheckBox> unsupportedAxiomsCheckBoxes;

    public NoHRPreferencesPanel() {
        preferences = NoHRPreferences.getInstance();
        dLInferenceEngine = preferences.getDLInferenceEngine();
        dLInferenceEngineEL = preferences.getDLInferenceEngineEL();
        dLInferenceEngineQL = preferences.getDLInferenceEngineQL();
        dLInferenceEngineRL = preferences.getDLInferenceEngineRL();

        koncludeBinary = preferences.getKoncludeBinary();
        xsbDirectory = preferences.getXsbDirectory();
        ignoredUnsupportedAxioms = preferences.getIgnoredUnsupportedAxioms();
        ignoreAllUnsupportedAxioms = preferences.getIgnoreAllUnsupportedAxioms();

        unsupportedAxiomsCheckBoxes = new HashSet<>();
    }

    @Override
    public void applyChanges() {
        preferences.setDLInferenceEngine(dLInferenceEngine);
        preferences.setDLInferenceEngineEL(dLInferenceEngineEL);
        preferences.setDLInferenceEngineQL(dLInferenceEngineQL);
        preferences.setDLInferenceEngineRL(dLInferenceEngineRL);
        preferences.setIgnoreAllUnsupportedAxioms(ignoreAllUnsupportedAxioms);
        preferences.setIgnoredUnsupportedAxioms(ignoredUnsupportedAxioms);
        preferences.setKoncludeBinary(koncludeBinary);
        preferences.setXsbDirectory(xsbDirectory);

        NoHRInstance.getInstance().requestRestart();
    }

    private JComboBox<DLInferenceEngine> createDLInferenceEngineComboBox(DLInferenceEngine dLInferenceEngine) {
        final JComboBox ret;

        ret = new JComboBox<>();
        ret.setModel(new javax.swing.DefaultComboBoxModel<>(new DLInferenceEngine[]{DLInferenceEngine.HERMIT, DLInferenceEngine.KONCLUDE}));

        if (dLInferenceEngine != null) {
            ret.setSelectedItem(dLInferenceEngine);
        }

        ret.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDLInferenceEngine((DLInferenceEngine) ret.getSelectedItem());
            }
        });

        return ret;
    }

    private JCheckBox createDLInferenceEngineELCheckBox(boolean dLInferenceEngineEL) {
        final JCheckBox ret;

        ret = new JCheckBox("Use DL Inference Engine for EL profile", dLInferenceEngineEL);

        ret.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDLInferenceEngineEL(ret.isSelected());
            }
        });

        return ret;
    }

    private JCheckBox createDLInferenceEngineQLCheckBox(boolean dLInferenceEngineQL) {
        final JCheckBox ret;

        ret = new JCheckBox("Use DL Inference Engine for QL profile", dLInferenceEngineQL);

        ret.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDLInferenceEngineQL(ret.isSelected());
            }
        });

        return ret;
    }

    private JCheckBox createDLInferenceEngineRLCheckBox(boolean dLInferenceEngineRL) {
        final JCheckBox ret;

        ret = new JCheckBox("Use DL Inference Engine for RL profile", dLInferenceEngineRL);

        ret.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDLInferenceEngineRL(ret.isSelected());
            }
        });

        return ret;
    }

    private JButton createKoncludeBinaryOpenButton() {
        final JButton result = new JButton("Open...");

        result.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();

                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (preferences.getKoncludeBinary() != null) {
                    fc.setSelectedFile(preferences.getKoncludeBinary());
                }

                final int returnVal = fc.showOpenDialog(NoHRPreferencesPanel.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    setKoncludeBinary(fc.getSelectedFile());
                }
            }
        });

        return result;
    }

    private JTextField createKoncludeBinaryTextField(File koncludeBinary) {
        final JTextField result;

        if (koncludeBinary == null) {
            result = new JTextField(10);
        } else {
            result = new JTextField(koncludeBinary.getPath());
        }

        result.setEditable(false);

        return result;
    }

    private JButton createXsbDirectoryOpenButton() {
        final JButton result = new JButton("Open...");

        result.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();

                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (preferences.getXsbDirectory() != null) {
                    fc.setSelectedFile(preferences.getXsbDirectory());
                }

                final int returnVal = fc.showOpenDialog(NoHRPreferencesPanel.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    setXsbDirectory(fc.getSelectedFile());
                }
            }
        });

        return result;
    }

    private JTextField createXsbDirectoryTextField(File xsbDirectory) {
        final JTextField result;

        if (xsbDirectory == null) {
            result = new JTextField(10);
        } else {
            result = new JTextField(xsbDirectory.getPath());
        }

        result.setEditable(false);

        return result;
    }

    private JPanel createUnsupportedAxiomsPanel() {
        final JPanel result = new JPanel();

        result.setBorder(BorderFactory.createTitledBorder("Treat Unsupported Axioms as Warnings"));

        final JCheckBox allAxiomsCheckBox = new JCheckBox("All axioms", ignoreAllUnsupportedAxioms);

        allAxiomsCheckBox.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ignoreAllUnsupportedAxioms = allAxiomsCheckBox.isSelected();
                preferences.setIgnoreAllUnsupportedAxioms(ignoreAllUnsupportedAxioms);

                for (JCheckBox i : unsupportedAxiomsCheckBoxes) {
                    i.setEnabled(!ignoreAllUnsupportedAxioms);
                }
            }
        });

        final JSeparator sep = new JSeparator();

        result.add(allAxiomsCheckBox);
        result.add(sep);

        for (AxiomType<?> i : preferences.getIgnorableAxioms()) {
            final JCheckBox axiomCheckBox = new JCheckBox(i.getName(), ignoredUnsupportedAxioms.contains(i));

            unsupportedAxiomsCheckBoxes.add(axiomCheckBox);
            axiomCheckBox.setEnabled(!ignoreAllUnsupportedAxioms);

            axiomCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (axiomCheckBox.isSelected()) {
                        ignoredUnsupportedAxioms.add(AxiomType.getAxiomType(axiomCheckBox.getText()));
                    } else {
                        ignoredUnsupportedAxioms.remove(AxiomType.getAxiomType(axiomCheckBox.getText()));
                    }

                    preferences.setIgnoredUnsupportedAxioms(ignoredUnsupportedAxioms);
                }
            });

            result.add(axiomCheckBox);
        }

        return result;
    }

    @Override
    public void dispose() throws Exception {
    }

    @Override
    public void initialise() throws Exception {
        xsbDirectoryTextField = createXsbDirectoryTextField(preferences.getXsbDirectory());
        koncludeBinaryTextField = createKoncludeBinaryTextField(preferences.getKoncludeBinary());
        dLInferenceEngineComboBox = createDLInferenceEngineComboBox(preferences.getDLInferenceEngine());
        dLInferenceEngineELCheckBox = createDLInferenceEngineELCheckBox(preferences.getDLInferenceEngineEL());
        dLInferenceEngineQLCheckBox = createDLInferenceEngineQLCheckBox(preferences.getDLInferenceEngineQL());
        dLInferenceEngineRLCheckBox = createDLInferenceEngineRLCheckBox(preferences.getDLInferenceEngineRL());
        unsupportedAxiomsPanel = createUnsupportedAxiomsPanel();

        reasonerPanel = new JPanel();

        reasonerPanel.add(new JLabel("XSB Directory"));
        reasonerPanel.add(xsbDirectoryTextField);
        reasonerPanel.add(createXsbDirectoryOpenButton());

        reasonerPanel.add(new JLabel("DL Inference Engine"));
        reasonerPanel.add(dLInferenceEngineComboBox);
        reasonerPanel.add(new JPanel());

        reasonerPanel.add(new JPanel());
        reasonerPanel.add(dLInferenceEngineELCheckBox);
        reasonerPanel.add(new JPanel());

        reasonerPanel.add(new JPanel());
        reasonerPanel.add(dLInferenceEngineQLCheckBox);
        reasonerPanel.add(new JPanel());

//        reasonerPanel.add(new JPanel());
//        reasonerPanel.add(dLInferenceEngineRLCheckBox);
//        reasonerPanel.add(new JPanel());
        reasonerPanel.add(new JLabel("Konclude Binary"));
        reasonerPanel.add(koncludeBinaryTextField);
        reasonerPanel.add(createKoncludeBinaryOpenButton());

        reasonerPanel.add(unsupportedAxiomsPanel);

        add(reasonerPanel);
        add(unsupportedAxiomsPanel);

        setLayout();
    }

    private void setDLInferenceEngine(DLInferenceEngine value) {
        dLInferenceEngine = value;
        dLInferenceEngineComboBox.setSelectedItem(value);
    }

    private void setDLInferenceEngineEL(boolean value) {
        dLInferenceEngineEL = value;
        dLInferenceEngineELCheckBox.setSelected(value);
    }

    private void setDLInferenceEngineQL(boolean value) {
        dLInferenceEngineQL = value;
        dLInferenceEngineQLCheckBox.setSelected(value);
    }

    private void setDLInferenceEngineRL(boolean value) {
        dLInferenceEngineRL = value;
        dLInferenceEngineRLCheckBox.setSelected(value);
    }

    private void setKoncludeBinary(File value) {
        koncludeBinary = value;
        koncludeBinaryTextField.setText(value.getPath());
    }

    private void setLayout() {
        for (final Component component : getComponents()) {
            component.setMaximumSize(MAX_HEIGHT_DIMENSION);
        }

        setLayout(new SpringLayout());
        SpringUtilities.makeCompactGrid(this, 2, 1, 3, 3, 10, 10);

        reasonerPanel.setLayout(new SpringLayout());
        SpringUtilities.makeCompactGrid(reasonerPanel, 5, 3, 3, 3, 10, 10);

        unsupportedAxiomsPanel.setLayout(new SpringLayout());
        SpringUtilities.makeCompactGrid(unsupportedAxiomsPanel, unsupportedAxiomsPanel.getComponentCount(), 1, 10, 10, 10, 10);
    }

    private void setXsbDirectory(File value) {
        xsbDirectory = value;
        xsbDirectoryTextField.setText(xsbDirectory.getPath());
    }
}
