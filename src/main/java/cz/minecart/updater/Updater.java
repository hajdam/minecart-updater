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

import java.awt.CardLayout;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Minecart updater.
 *
 * @author Minecart team
 */
public class Updater {

    private final ResourceBundle serverConfiguration = ResourceBundle.getBundle("cz/minecart/updater/resources/ServerConfiguration");
    private final ResourceBundle updaterBundle = ResourceBundle.getBundle("cz/minecart/updater/resources/Updater");

    private static final String GAME_PATH_PROPERTY = "gamePath";
    private static final String GAME_PATH_AUTO_PROPERTY = "gamePathAuto";
    private static final String PROFILE_PATH_PROPERTY = "profilePath";
    private static final String PROFILE_PATH_AUTO_PROPERTY = "profilePathAuto";
    private static final String RUN_COMMAND_PROPERTY = "runCommand";
    private static final String RUN_COMMAND_AUTO_PROPERTY = "runCommandAuto";
    private static final String MOD_RECORD_PREFIX = "mod_";

    private OsType osType = OsType.LINUX;
    private URL newsUrl;
    private URL checkUpdateUrl;
    private URL appDownloadUrl;
    private URL websiteUrl;
    private URL filesUpdateUrl;
    private URL forgeUpdateUrl;
    private URL updateDownloadUrl;
    private String profileName;

    private LogListener logListener = null;

    public void init() {
        // Detect operating system type
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            osType = OsType.WINDOWS;
        } else if (osName.contains("mac")) {
            osType = OsType.MAC;
        }
        log(Level.INFO, "Detekován operační systém: " + osType.name());
    }

    private void performUpdate() {
        // Perform checking for updates
        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    newsUrl = new URI(serverConfiguration.getString("news_url")).toURL();
                    checkUpdateUrl = new URI(serverConfiguration.getString("update_url")).toURL();
                    filesUpdateUrl = new URI(serverConfiguration.getString("update_files_url")).toURL();
                    forgeUpdateUrl = new URI(serverConfiguration.getString("update_forge_url")).toURL();
                    appDownloadUrl = new URI(serverConfiguration.getString("download_laucher_url")).toURL();
                    websiteUrl = new URI(serverConfiguration.getString("website_url")).toURL();
                    Logger.getLogger(UpdaterFrame.class.getName()).log(Level.INFO, "Používá se web: " + websiteUrl);
                    updateDownloadUrl = new URI(serverConfiguration.getString("update_download_url")).toURL();
                    profileName = serverConfiguration.getString("profileName");
                    profilePathLabel.setText("Cesta k minecraft profilu (" + profileName + ")");
                    banner.setVersion("Verze aktualizátoru: " + updaterConfigurationBundle.getString("Application.version"));
                } catch (URISyntaxException | MalformedURLException ex) {
                    Logger.getLogger(UpdaterFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                // Load news content
                loadNewsContent();

                // Check for application update
                CheckAppUpdateResult appUpdate = checkForAppUpdate();
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
                        UpdatePlan updatePlan = checkForModsUpdate();
                        switch (updatePlan.resultType) {
                            case UPDATE_FOUND: {
                                ((CardLayout) controlPanel.getLayout()).show(controlPanel, "update");
                                ModsUpdateResult modsUpdateResult = performModsUpdate(updatePlan);
                                switch (modsUpdateResult) {
                                    case DOWNLOAD_ERROR: {
                                        connectionIssues();
                                        break;
                                    }
                                    case UPDATE_OK: {
                                        actionSucessful("Nastavení bylo aktualizováno.");
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
                                actionSucessful("Nastavení je aktuální");
                                break;
                            }
                            case NO_TARGET_DIRECTORY: {
                                actionFailed(updatePlan.errorMessage == null ? "Složka minecraft profilu je neplatná. Nastavte ji ručně." : updatePlan.errorMessage);
                                break;
                            }
                            case NO_TARGET_MOD_DIRECTORY: {
                                actionFailed("Nepodařilo se najít složku Minecraft profilu");
                                break;
                            }
                            default: {
                                actionFailed("Nepodařilo se zkontrolovat aktualizaci");
                                break;
                            }
                        }

                        break;
                    }

                    default: {
                        errorIconLabel.setText("Došlo k neznámé chybě");
                        ((CardLayout) controlPanel.getLayout()).show(controlPanel, "error");
                        break;
                    }
                }
                checkUpdateButton.setEnabled(true);
            }
        });

        updateThread.start();
    }

    public URL getAppDownloadUrl() {
        return appDownloadUrl;
    }

    public URL getWebsiteUrl() {
        return websiteUrl;
    }

    public void log(Level level, String message) {
        Logger.getLogger(Updater.class.getName()).log(level, message);

        if (logListener != null) {
            logListener.log(level, message);
        }
    }

    public LogListener getLogListener() {
        return logListener;
    }

    public void setLogListener(LogListener logListener) {
        this.logListener = logListener;
    }

    private static class UpdatePlan {

        String profilePath;
        CheckModsUpdateResult resultType;
        String errorMessage;
        Set<String> downloadMods;
        Set<String> deleteMods;

        public UpdatePlan(CheckModsUpdateResult resultType, String errorMessage) {
            this.resultType = resultType;
            this.errorMessage = errorMessage;
        }

        public UpdatePlan(CheckModsUpdateResult resultType, String profilePath, Set<String> downloadMods, Set<String> deleteMods) {
            this.resultType = resultType;
            this.profilePath = profilePath;
            this.downloadMods = downloadMods;
            this.deleteMods = deleteMods;
        }
    }

    public static enum CheckAppUpdateResult {
        UPDATE_URL_NOT_SET,
        NO_CONNECTION,
        CONNECTION_ISSUE,
        NOT_FOUND,
        NO_UPDATE_AVAILABLE,
        UPDATE_FOUND
    }

    public static enum CheckModsUpdateResult {
        UPDATE_URL_NOT_SET,
        NO_CONNECTION,
        CONNECTION_ISSUE,
        NOT_FOUND,
        NO_UPDATE_AVAILABLE,
        NO_TARGET_DIRECTORY,
        NO_TARGET_MOD_DIRECTORY,
        UPDATE_FOUND
    }

    public static enum ModsUpdateResult {
        UPDATE_OK,
        DOWNLOAD_ERROR
    }

    public static enum OsType {
        WINDOWS,
        MAC,
        LINUX
    }

    public static interface LogListener {

        void log(Level level, String message);
    }
}
