package com.langbly.omegat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.omegat.util.Preferences;

/**
 * Configuration dialog for the Langbly Translate plugin.
 * Allows users to set their API key and select a region endpoint.
 */
public class LangblyConfigDialog extends JDialog {

    private static final String PREF_API_KEY = "langbly.api.key";
    private static final String PREF_BASE_URL = "langbly.api.base.url";
    private static final String DEFAULT_BASE_URL = "https://api.langbly.com";

    private static final String[] ENDPOINTS = {
        "https://api.langbly.com",
        "https://eu.langbly.com"
    };

    private static final String[] ENDPOINT_LABELS = {
        "Global (api.langbly.com)",
        "EU (eu.langbly.com)"
    };

    private final JTextField apiKeyField;
    private final JComboBox<String> endpointCombo;

    public LangblyConfigDialog(Window parent) {
        super(parent, "Langbly Translate Configuration", ModalityType.APPLICATION_MODAL);

        setLayout(new BorderLayout(8, 8));
        setMinimumSize(new Dimension(420, 180));
        setLocationRelativeTo(parent);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // API Key
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("API Key:"), gbc);

        apiKeyField = new JTextField(28);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(apiKeyField, gbc);

        // Endpoint
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Region:"), gbc);

        endpointCombo = new JComboBox<>(ENDPOINT_LABELS);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(endpointCombo, gbc);

        // Help text
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel helpLabel = new JLabel("<html><small>Get your API key at langbly.com/dashboard</small></html>");
        formPanel.add(helpLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            save();
            dispose();
        });
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);

        // Load current values
        load();
        pack();
    }

    private void load() {
        String key = Preferences.getPreference(PREF_API_KEY);
        if (key != null) {
            apiKeyField.setText(key);
        }

        String url = Preferences.getPreference(PREF_BASE_URL);
        if (url != null && url.contains("eu.langbly.com")) {
            endpointCombo.setSelectedIndex(1);
        } else {
            endpointCombo.setSelectedIndex(0);
        }
    }

    private void save() {
        Preferences.setPreference(PREF_API_KEY, apiKeyField.getText().trim());
        Preferences.setPreference(PREF_BASE_URL, ENDPOINTS[endpointCombo.getSelectedIndex()]);
    }
}
