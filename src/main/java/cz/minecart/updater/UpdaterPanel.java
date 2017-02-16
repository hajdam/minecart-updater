/*
 * Copyright 2017 Minecart Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.minecart.updater;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import javax.swing.JFileChooser;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;

/**
 * Minecart updater frame.
 *
 * @author Minecart team
 */
public class UpdaterPanel extends javax.swing.JPanel implements HyperlinkListener {

    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("cz/minecart/updater/resources/UpdaterPanel");

    private AnimatedBanner banner;
    private final Updater updater = new Updater();

    public UpdaterPanel() {
        initComponents();
        init();
    }

    private void init() {
        registerLogger();
        updater.init();
        newsTextPane.addHyperlinkListener(this);

        gamePathCheckBox.setSelected(updater.isProfilePathAuto());
        updateGamePathVisibility();
        gamePathTextField.setText(updater.getGamePath());

        profilePathTextField.setText(updater.getProfilePath());
        profilePathCheckBox.setSelected(updater.isProfilePathAuto());
        updateProfilePathVisibility();

        runCommandTextField.setText(updater.getRunCommand());

        banner = new AnimatedBanner();
        headerPanel.add(banner, BorderLayout.CENTER);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isVisible()) {
                    banner.animate();
                }
            }
        }, 0, 70);
    }

    public void performUpdate() {
        // Perform checking for updates
        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                updater.loadServerConfiguration();
                profilePathLabel.setText(resourceBundle.getString("profilePathLabel.textPrefix") + updater.getProfileName() + resourceBundle.getString("profilePathLabel.textPostfix"));
                banner.setVersion(resourceBundle.getString("bannerApplicationVersion") + updater.getApplicationVersion());

                // Load news content
                Updater.LoadNewsResult newsContentResult = updater.loadNewsContent();
                if (newsContentResult == Updater.LoadNewsResult.OK) {
                    newsTextPane.setText(updater.getNewsContent());
                }

                // Check for application update
                Updater.CheckAppUpdateResult appUpdate = updater.checkForAppUpdate();
                switch (appUpdate) {
                    case UPDATE_FOUND: {
                        ((CardLayout) controlPanel.getLayout()).show(controlPanel, "newApp");
                        break;
                    }
                    case NOT_FOUND:
                    case CONNECTION_ISSUE: {
                        connectionIssues();
                        break;
                    }
                    case NO_UPDATE_AVAILABLE: {
                        Updater.UpdatePlan updatePlan = updater.checkForModsUpdate();
                        switch (updatePlan.resultType) {
                            case UPDATE_FOUND: {
                                ((CardLayout) controlPanel.getLayout()).show(controlPanel, "update");
                                Updater.ModsUpdateResult modsUpdateResult = updater.performModsUpdate(updatePlan, new Updater.UpdatePlanObserver() {
                                    @Override
                                    public void reportProgress(boolean indeterminate, int progress) {
                                        updateProgressBar.setIndeterminate(indeterminate);
                                        updateProgressBar.setValue(progress);
                                        updateProgressBar.repaint();
                                    }
                                });

                                switch (modsUpdateResult) {
                                    case DOWNLOAD_ERROR: {
                                        connectionIssues();
                                        break;
                                    }
                                    case UPDATE_OK: {
                                        actionSucessful(resourceBundle.getString("updateSuccessful"));
                                        break;
                                    }
                                }
                                break;
                            }
                            case NOT_FOUND:
                            case NO_CONNECTION:
                            case CONNECTION_ISSUE: {
                                connectionIssues();
                                break;
                            }
                            case NO_UPDATE_AVAILABLE: {
                                actionSucessful(resourceBundle.getString("updateNotNeeded"));
                                break;
                            }
                            case NO_TARGET_DIRECTORY: {
                                actionFailed(updatePlan.errorMessage == null ? resourceBundle.getString("updateErrorProfilePathInvalid") : updatePlan.errorMessage);
                                break;
                            }
                            case NO_TARGET_MOD_DIRECTORY: {
                                actionFailed(resourceBundle.getString("updateErrorProfileNotFound"));
                                break;
                            }
                            default: {
                                actionFailed(resourceBundle.getString("updateErrorCheckFailed"));
                                break;
                            }
                        }

                        break;
                    }

                    default: {
                        errorIconLabel.setText(resourceBundle.getString("updateErrorUnknown"));
                        ((CardLayout) controlPanel.getLayout()).show(controlPanel, "error");
                        break;
                    }
                }
                checkUpdateButton.setEnabled(true);
            }
        });

        updateThread.start();
    }

    private void actionSucessful(String okMessage) {
        if (!runCommandTextField.getText().isEmpty()) {
            playButton.setText(resourceBundle.getString("playButton.playText"));
        }
        playIconLabel.setText(okMessage);
        ((CardLayout) controlPanel.getLayout()).show(controlPanel, "play");
    }

    private void actionFailed(String errorMessage) {
        if (!runCommandTextField.getText().isEmpty()) {
            warningCloseButton.setText(resourceBundle.getString("playButton.playText"));
        }
        warningIconLabel.setText(errorMessage);
        ((CardLayout) controlPanel.getLayout()).show(controlPanel, "warning");
    }

    private void connectionIssues() {
        actionFailed("Došlo k problému s připojením");
    }

    private void updateGamePathVisibility() {
        gamePathTextField.setEnabled(!gamePathCheckBox.isSelected());
        gamePathButton.setEnabled(!gamePathCheckBox.isSelected());
    }

    private void updateProfilePathVisibility() {
        profilePathTextField.setEnabled(!profilePathCheckBox.isSelected());
        profilePathButton.setEnabled(!profilePathCheckBox.isSelected());
    }

    public void save() {
        updater.setGamePath(gamePathTextField.getText());
        updater.setGamePathAuto(gamePathCheckBox.isSelected());
        updater.setProfilePath(profilePathTextField.getText());
        updater.setProfilePathAuto(profilePathCheckBox.isSelected());
        updater.setRunCommand(runCommandTextField.getText());
        updater.setRunCommandAuto(runCommandCheckBox.isSelected());

        updater.saveConfiguration();
    }

    private void registerLogger() {
        Logger.getLogger(UpdaterPanel.class.getName()).addHandler(new StreamHandler() {
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush();
                try {
                    logTextArea.append("\n");
                    String message = record.getMessage();
                    if (message != null) {
                        logTextArea.append(message);
                    }
                    Throwable thrown = record.getThrown();
                    if (thrown != null) {
                        logTextArea.append("Exception: " + thrown.toString());
                    }
                } catch (Exception ex) {
                    // ignore
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        runCommandCheckBox = new javax.swing.JCheckBox();
        headerPanel = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        newsScrollPane = new javax.swing.JScrollPane();
        newsTextPane = new javax.swing.JTextPane();
        optionsPanel = new javax.swing.JPanel();
        optionsScrollPane = new javax.swing.JScrollPane();
        optionsInnerPanel = new javax.swing.JPanel();
        gamePathLabel = new javax.swing.JLabel();
        gamePathCheckBox = new javax.swing.JCheckBox();
        gamePathTextField = new javax.swing.JTextField();
        gamePathButton = new javax.swing.JButton();
        profilePathLabel = new javax.swing.JLabel();
        profilePathCheckBox = new javax.swing.JCheckBox();
        profilePathTextField = new javax.swing.JTextField();
        profilePathButton = new javax.swing.JButton();
        runCommandLabel = new javax.swing.JLabel();
        runCommandTextField = new javax.swing.JTextField();
        runCommandButton = new javax.swing.JButton();
        checkUpdateButton = new javax.swing.JButton();
        logPanel = new javax.swing.JPanel();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        controlPanel = new javax.swing.JPanel();
        checkingPanel = new javax.swing.JPanel();
        checkingIconLabel = new javax.swing.JLabel();
        errorPanel = new javax.swing.JPanel();
        errorIconLabel = new javax.swing.JLabel();
        warningPanel = new javax.swing.JPanel();
        warningCloseButton = new javax.swing.JButton();
        warningIconLabel = new javax.swing.JLabel();
        playPanel = new javax.swing.JPanel();
        playButton = new javax.swing.JButton();
        playIconLabel = new javax.swing.JLabel();
        updatePanel = new javax.swing.JPanel();
        updateIconLabel = new javax.swing.JLabel();
        updateProgressBar = new javax.swing.JProgressBar();
        newAppPanel = new javax.swing.JPanel();
        newAppDownloadButton = new javax.swing.JButton();
        newAppIconLabel = new javax.swing.JLabel();
        forgePanel = new javax.swing.JPanel();
        forgeIconLabel = new javax.swing.JLabel();

        runCommandCheckBox.setText("Detekovat příkaz automaticky");
        runCommandCheckBox.setEnabled(false);

        setLayout(new java.awt.BorderLayout());

        headerPanel.setBackground(new java.awt.Color(0, 0, 0));
        headerPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        headerPanel.setMinimumSize(new java.awt.Dimension(0, 170));
        headerPanel.setPreferredSize(new java.awt.Dimension(0, 170));
        headerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                headerPanelMouseClicked(evt);
            }
        });
        headerPanel.setLayout(new java.awt.BorderLayout());
        add(headerPanel, java.awt.BorderLayout.NORTH);

        newsTextPane.setEditable(false);
        newsTextPane.setContentType("text/html"); // NOI18N
        newsTextPane.setText(resourceBundle.getString("newsTextPane.text")); // NOI18N
        newsScrollPane.setViewportView(newsTextPane);

        tabbedPane.addTab(resourceBundle.getString("newsTab.title"), newsScrollPane); // NOI18N

        optionsPanel.setLayout(new javax.swing.BoxLayout(optionsPanel, javax.swing.BoxLayout.LINE_AXIS));

        gamePathLabel.setText(resourceBundle.getString("gamePathLabel.text")); // NOI18N

        gamePathCheckBox.setSelected(true);
        gamePathCheckBox.setText(resourceBundle.getString("gamePathCheckBox.text")); // NOI18N
        gamePathCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gamePathCheckBoxActionPerformed(evt);
            }
        });

        gamePathTextField.setEnabled(false);

        gamePathButton.setText(resourceBundle.getString("gamePathButton.text")); // NOI18N
        gamePathButton.setEnabled(false);
        gamePathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gamePathButtonActionPerformed(evt);
            }
        });

        profilePathLabel.setText(resourceBundle.getString("profilePathLabel.text")); // NOI18N

        profilePathCheckBox.setSelected(true);
        profilePathCheckBox.setText(resourceBundle.getString("profilePathCheckBox.text")); // NOI18N
        profilePathCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilePathCheckBoxActionPerformed(evt);
            }
        });

        profilePathTextField.setEnabled(false);

        profilePathButton.setText(resourceBundle.getString("profilePathButton.text")); // NOI18N
        profilePathButton.setEnabled(false);
        profilePathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilePathButtonActionPerformed(evt);
            }
        });

        runCommandLabel.setText(resourceBundle.getString("runCommandLabel.text")); // NOI18N

        runCommandTextField.setText("java -jar Minecart.jar");

        runCommandButton.setText(resourceBundle.getString("runCommandButton.text")); // NOI18N
        runCommandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runCommandButtonActionPerformed(evt);
            }
        });

        checkUpdateButton.setText(resourceBundle.getString("checkUpdateButton.text")); // NOI18N
        checkUpdateButton.setEnabled(false);
        checkUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkUpdateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout optionsInnerPanelLayout = new javax.swing.GroupLayout(optionsInnerPanel);
        optionsInnerPanel.setLayout(optionsInnerPanelLayout);
        optionsInnerPanelLayout.setHorizontalGroup(
            optionsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(optionsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionsInnerPanelLayout.createSequentialGroup()
                        .addComponent(runCommandLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(optionsInnerPanelLayout.createSequentialGroup()
                        .addGroup(optionsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(optionsInnerPanelLayout.createSequentialGroup()
                                .addComponent(profilePathTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(profilePathButton))
                            .addGroup(optionsInnerPanelLayout.createSequentialGroup()
                                .addComponent(gamePathTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gamePathButton))
                            .addGroup(optionsInnerPanelLayout.createSequentialGroup()
                                .addComponent(runCommandTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(runCommandButton))
                            .addGroup(optionsInnerPanelLayout.createSequentialGroup()
                                .addGroup(optionsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(gamePathLabel)
                                    .addComponent(gamePathCheckBox)
                                    .addComponent(profilePathLabel)
                                    .addComponent(profilePathCheckBox))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(optionsInnerPanelLayout.createSequentialGroup()
                        .addComponent(checkUpdateButton)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        optionsInnerPanelLayout.setVerticalGroup(
            optionsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gamePathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gamePathCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gamePathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gamePathButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profilePathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profilePathCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(profilePathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(profilePathButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(runCommandLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionsInnerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(runCommandTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(runCommandButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkUpdateButton)
                .addContainerGap(43, Short.MAX_VALUE))
        );

        optionsScrollPane.setViewportView(optionsInnerPanel);

        optionsPanel.add(optionsScrollPane);

        tabbedPane.addTab(resourceBundle.getString("optionsTab.title"), optionsPanel); // NOI18N

        logPanel.setLayout(new java.awt.BorderLayout());

        logTextArea.setEditable(false);
        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        logTextArea.setText(resourceBundle.getString("logTextArea.text")); // NOI18N
        logScrollPane.setViewportView(logTextArea);

        logPanel.add(logScrollPane, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab(resourceBundle.getString("logTab.title"), logPanel); // NOI18N

        add(tabbedPane, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.CardLayout());

        checkingIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        checkingIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/apps/internet-web-browser-7.png"))); // NOI18N
        checkingIconLabel.setText(resourceBundle.getString("checkingIconLabel.text")); // NOI18N
        checkingIconLabel.setToolTipText("");

        javax.swing.GroupLayout checkingPanelLayout = new javax.swing.GroupLayout(checkingPanel);
        checkingPanel.setLayout(checkingPanelLayout);
        checkingPanelLayout.setHorizontalGroup(
            checkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkingIconLabel)
                .addContainerGap(364, Short.MAX_VALUE))
        );
        checkingPanelLayout.setVerticalGroup(
            checkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, checkingPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(checkingIconLabel)
                .addContainerGap())
        );

        controlPanel.add(checkingPanel, "checking");

        errorIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        errorIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/dialog-no.png"))); // NOI18N
        errorIconLabel.setText(resourceBundle.getString("errorIconLabel.text")); // NOI18N
        errorIconLabel.setToolTipText("");

        javax.swing.GroupLayout errorPanelLayout = new javax.swing.GroupLayout(errorPanel);
        errorPanel.setLayout(errorPanelLayout);
        errorPanelLayout.setHorizontalGroup(
            errorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(errorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(errorIconLabel)
                .addContainerGap(541, Short.MAX_VALUE))
        );
        errorPanelLayout.setVerticalGroup(
            errorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, errorPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorIconLabel)
                .addContainerGap())
        );

        controlPanel.add(errorPanel, "error");

        warningCloseButton.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        warningCloseButton.setText(resourceBundle.getString("warningCloseButton.text")); // NOI18N
        warningCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warningCloseButtonActionPerformed(evt);
            }
        });

        warningIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        warningIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/dialog-no.png"))); // NOI18N
        warningIconLabel.setText(resourceBundle.getString("warningIconLabel.text")); // NOI18N

        javax.swing.GroupLayout warningPanelLayout = new javax.swing.GroupLayout(warningPanel);
        warningPanel.setLayout(warningPanelLayout);
        warningPanelLayout.setHorizontalGroup(
            warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, warningPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(warningIconLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 415, Short.MAX_VALUE)
                .addComponent(warningCloseButton)
                .addContainerGap())
        );
        warningPanelLayout.setVerticalGroup(
            warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, warningPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(warningIconLabel)
                    .addComponent(warningCloseButton))
                .addContainerGap())
        );

        controlPanel.add(warningPanel, "warning");

        playButton.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        playButton.setText(resourceBundle.getString("playButton.text")); // NOI18N
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        playIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        playIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/dialog-ok-5.png"))); // NOI18N
        playIconLabel.setText(resourceBundle.getString("playIconLabel.text")); // NOI18N

        javax.swing.GroupLayout playPanelLayout = new javax.swing.GroupLayout(playPanel);
        playPanel.setLayout(playPanelLayout);
        playPanelLayout.setHorizontalGroup(
            playPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, playPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playIconLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 405, Short.MAX_VALUE)
                .addComponent(playButton)
                .addContainerGap())
        );
        playPanelLayout.setVerticalGroup(
            playPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, playPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(playPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(playIconLabel)
                    .addComponent(playButton))
                .addContainerGap())
        );

        controlPanel.add(playPanel, "play");

        updateIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        updateIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/upgrade_misc.png"))); // NOI18N
        updateIconLabel.setText(resourceBundle.getString("updateIconLabel.text")); // NOI18N

        updateProgressBar.setIndeterminate(true);

        javax.swing.GroupLayout updatePanelLayout = new javax.swing.GroupLayout(updatePanel);
        updatePanel.setLayout(updatePanelLayout);
        updatePanelLayout.setHorizontalGroup(
            updatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateIconLabel)
                .addGap(18, 18, 18)
                .addComponent(updateProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                .addContainerGap())
        );
        updatePanelLayout.setVerticalGroup(
            updatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updatePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(updatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(updateIconLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(updateProgressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        controlPanel.add(updatePanel, "update");

        newAppDownloadButton.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        newAppDownloadButton.setText(resourceBundle.getString("newAppDownloadButton.text")); // NOI18N
        newAppDownloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAppDownloadButtonActionPerformed(evt);
            }
        });

        newAppIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        newAppIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/update-product.png"))); // NOI18N
        newAppIconLabel.setText(resourceBundle.getString("newAppIconLabel.text")); // NOI18N

        javax.swing.GroupLayout newAppPanelLayout = new javax.swing.GroupLayout(newAppPanel);
        newAppPanel.setLayout(newAppPanelLayout);
        newAppPanelLayout.setHorizontalGroup(
            newAppPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newAppPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newAppIconLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addComponent(newAppDownloadButton)
                .addContainerGap())
        );
        newAppPanelLayout.setVerticalGroup(
            newAppPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newAppPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(newAppPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(newAppIconLabel)
                    .addComponent(newAppDownloadButton))
                .addContainerGap())
        );

        controlPanel.add(newAppPanel, "newApp");

        forgeIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        forgeIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/upgrade_misc.png"))); // NOI18N
        forgeIconLabel.setText(resourceBundle.getString("forgeIconLabel.text")); // NOI18N

        javax.swing.GroupLayout forgePanelLayout = new javax.swing.GroupLayout(forgePanel);
        forgePanel.setLayout(forgePanelLayout);
        forgePanelLayout.setHorizontalGroup(
            forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(forgePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(forgeIconLabel)
                .addContainerGap(453, Short.MAX_VALUE))
        );
        forgePanelLayout.setVerticalGroup(
            forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(forgePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(forgeIconLabel)
                .addContainerGap())
        );

        controlPanel.add(forgePanel, "forge");

        add(controlPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void headerPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_headerPanelMouseClicked
        BareBonesBrowserLaunch.openDesktopURL(updater.getWebsiteUrl());
    }//GEN-LAST:event_headerPanelMouseClicked

    private void gamePathCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gamePathCheckBoxActionPerformed
        updateGamePathVisibility();
    }//GEN-LAST:event_gamePathCheckBoxActionPerformed

    private void gamePathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gamePathButtonActionPerformed
        String gamePath = gamePathTextField.getText();
        File gamePathFile = new File(gamePath);
        if (!gamePathFile.exists() || gamePath == null || gamePath.isEmpty()) {
            gamePathFile = new java.io.File(".");
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(gamePathFile);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            gamePathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_gamePathButtonActionPerformed

    private void profilePathCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profilePathCheckBoxActionPerformed
        updateProfilePathVisibility();
    }//GEN-LAST:event_profilePathCheckBoxActionPerformed

    private void profilePathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profilePathButtonActionPerformed
        String profilePath = profilePathTextField.getText();
        File profilePathFile = new File(profilePath);
        if (!profilePathFile.exists() || profilePath == null || profilePath.isEmpty()) {
            profilePathFile = new java.io.File(".");
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(profilePathFile);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            profilePathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_profilePathButtonActionPerformed

    private void runCommandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runCommandButtonActionPerformed
        String gamePath = gamePathTextField.getText();
        File gamePathFile = new File(gamePath);
        if (!gamePathFile.exists() || gamePath == null || gamePath.isEmpty()) {
            gamePathFile = new java.io.File(".");
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(gamePathFile);
        chooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName().toLowerCase();
                return fileName.endsWith(".exe") || fileName.endsWith(".jar");
            }

            @Override
            public String getDescription() {
                return resourceBundle.getString("fileChooser.executableFilesDescription");
            }
        });
        chooser.setAcceptAllFileFilterUsed(true);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            runCommandTextField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_runCommandButtonActionPerformed

    private void checkUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkUpdateButtonActionPerformed
        checkUpdateButton.setEnabled(false);
        ((CardLayout) controlPanel.getLayout()).show(controlPanel, "checking");
        Logger.getLogger(UpdaterPanel.class.getName()).log(Level.INFO, resourceBundle.getString("updateRecheck"));
        performUpdate();
    }//GEN-LAST:event_checkUpdateButtonActionPerformed

    private void warningCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warningCloseButtonActionPerformed
        playButtonActionPerformed(evt);
    }//GEN-LAST:event_warningCloseButtonActionPerformed

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        String runCommand = runCommandTextField.getText();
        if (!runCommand.isEmpty()) {
            try {
                Process p = Runtime.getRuntime().exec(runCommandTextField.getText());
            } catch (IOException ex) {
                Logger.getLogger(UpdaterPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        save();
        System.exit(0);
    }//GEN-LAST:event_playButtonActionPerformed

    private void newAppDownloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAppDownloadButtonActionPerformed
        BareBonesBrowserLaunch.openDesktopURL(updater.getAppDownloadUrl());
    }//GEN-LAST:event_newAppDownloadButtonActionPerformed

    /**
     * Opens hyperlink in external browser.
     *
     * @param event hyperlink event
     */
    @Override
    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            BareBonesBrowserLaunch.openURL(event.getURL().toExternalForm());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton checkUpdateButton;
    private javax.swing.JLabel checkingIconLabel;
    private javax.swing.JPanel checkingPanel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JLabel errorIconLabel;
    private javax.swing.JPanel errorPanel;
    private javax.swing.JLabel forgeIconLabel;
    private javax.swing.JPanel forgePanel;
    private javax.swing.JButton gamePathButton;
    private javax.swing.JCheckBox gamePathCheckBox;
    private javax.swing.JLabel gamePathLabel;
    private javax.swing.JTextField gamePathTextField;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel logPanel;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JButton newAppDownloadButton;
    private javax.swing.JLabel newAppIconLabel;
    private javax.swing.JPanel newAppPanel;
    private javax.swing.JScrollPane newsScrollPane;
    private javax.swing.JTextPane newsTextPane;
    private javax.swing.JPanel optionsInnerPanel;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JScrollPane optionsScrollPane;
    private javax.swing.JButton playButton;
    private javax.swing.JLabel playIconLabel;
    private javax.swing.JPanel playPanel;
    private javax.swing.JButton profilePathButton;
    private javax.swing.JCheckBox profilePathCheckBox;
    private javax.swing.JLabel profilePathLabel;
    private javax.swing.JTextField profilePathTextField;
    private javax.swing.JButton runCommandButton;
    private javax.swing.JCheckBox runCommandCheckBox;
    private javax.swing.JLabel runCommandLabel;
    private javax.swing.JTextField runCommandTextField;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JLabel updateIconLabel;
    private javax.swing.JPanel updatePanel;
    private javax.swing.JProgressBar updateProgressBar;
    private javax.swing.JButton warningCloseButton;
    private javax.swing.JLabel warningIconLabel;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables

    public String getFrameTitle() {
        String title = updater.getFrameTitle();
        return title == null ? resourceBundle.getString("title") : title;
    }

    public String getFrameIconPath() {
        String frameIconPath = updater.getFrameIconPath();
        return frameIconPath == null ? resourceBundle.getString("frameIconPath") : frameIconPath;
    }
}
