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
import java.io.FileFilter;
import java.nio.file.Path;

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

	private File xsbBinDirectory;

	private final NoHRPreferences preferences;

	public NoHRPreferencesPanel() {
		preferences = NoHRPreferences.getInstance();
	}

	@Override
	public void applyChanges() {
		preferences.setXSBBinDirectory(xsbBinDirectory);
	}

	private JButton createOpenButton() {
		final JButton result = new JButton("Open");
		result.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				final int returnVal = fc.showOpenDialog(NoHRPreferencesPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
					setXsbDir(fc.getSelectedFile());
			}
		});
		return result;
	}

	private JTextField createXSBDirectoryTextField(File xsbDir) {
		final JTextField result;
		if (xsbDir == null)
			result = new JTextField(10);
		else
			result = new JTextField(xsbDir.getPath());
		result.setEditable(false);
		return result;
	}

	@Override
	public void dispose() throws Exception {
	}

	@Override
	public void initialise() throws Exception {
		txtXSBBinDirectory = createXSBDirectoryTextField(preferences.getXSBBinDirectory());
		add(new JLabel("XSB directory"));
		add(txtXSBBinDirectory);
		add(createOpenButton());
		setLayout();
	}

	private void setLayout() {
		for (final Component component : getComponents())
			component.setMaximumSize(MAX_HEIGHT_DIMENSION);
		setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this, 1, 3, 3, 3, 3, 3);
	}

	private void setXsbDir(File xsbDir) {
		File xsbBinDirectory = null;
		final File xsbConfigDir = xsbDir.toPath().resolve("config").toFile();
		if (!xsbConfigDir.exists()) {
			Messages.invalidXSBDirectory(this, xsbConfigDir.toPath());
			return;
		}
		final File[] platformDirs = xsbConfigDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		if (platformDirs.length < 1) {
			Messages.invalidXSBDirectory(this, xsbConfigDir.toPath().resolve("<platform>"));
			return;
		}
		if (platformDirs.length > 1) {
			final String[] platforms = new String[platformDirs.length];
			for (int i = 0; i < platformDirs.length; i++)
				platforms[i] = platformDirs[i].toPath().getFileName().toString();
			final String platform = Messages.selectPlataform(this, platforms);
			xsbBinDirectory = xsbConfigDir.toPath().resolve(platform).toFile();
		} else if (platformDirs.length == 1)
			xsbBinDirectory = platformDirs[0];
		final Path expectedPath = xsbBinDirectory.toPath().resolve("bin").resolve("xsb");
		final Path winExpectedPath = xsbBinDirectory.toPath().resolve("bin").resolve("xsb.exe");
		if (!expectedPath.toFile().exists() && !winExpectedPath.toFile().exists()) {
			Messages.invalidXSBDirectory(this, expectedPath);
			return;
		}
		this.xsbBinDirectory = xsbBinDirectory;
		txtXSBBinDirectory.setText(xsbBinDirectory.getPath());
	}
}
