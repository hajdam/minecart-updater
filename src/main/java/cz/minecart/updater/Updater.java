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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Minecart updater.
 *
 * @author Minecart team
 */
public class Updater {

    private final ResourceBundle updaterConfigurationBundle = ResourceBundle.getBundle("cz/minecart/updater/resources/UpdaterConfiguration");
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
    private String applicationVersion;

    private URL newsUrl;
    private URL checkUpdateUrl;
    private URL appDownloadUrl;
    private URL websiteUrl;
    private URL filesUpdateUrl;
    private URL forgeUpdateUrl;
    private URL updateDownloadUrl;
    private String profileName;

    private final Properties config = new Properties();
    private File configFile;

    private String gamePath;
    private boolean gamePathAuto;
    private String profilePath;
    private boolean profilePathAuto;
    private String runCommand;
    private boolean runCommandAuto;

    private VersionNumbers updateVersion;
    private Set<String> currentFiles = null;
    private String newsContent = null;
    private final Set<String> modsFiles = new HashSet<>();

    private LogListener logListener = null;

    public void init() {
        // Detect operating system type
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            osType = OsType.WINDOWS;
        } else if (osName.contains("mac")) {
            osType = OsType.MAC;
        }
        log(Level.INFO, updaterBundle.getString("operatingSystemDetected") + osType.name());

        profileName = updaterConfigurationBundle.getString("profileName");
        applicationVersion = updaterConfigurationBundle.getString("Application.version");

        loadConfiguration();
    }

    public LoadNewsResult loadNewsContent() {
        try (InputStream newsStream = newsUrl.openStream()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            do {
                length = newsStream.read(buffer);

                if (length >= 0) {
                    result.write(buffer, 0, length);
                }
            } while (length >= 0);

            newsContent = result.toString("UTF-8");
            log(Level.INFO, updaterBundle.getString("newsContentLoaded"));
            return LoadNewsResult.OK;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }

        return LoadNewsResult.FAILED;
    }

    public CheckAppUpdateResult checkForAppUpdate() {
        if (checkUpdateUrl == null) {
            return CheckAppUpdateResult.UPDATE_URL_NOT_SET;
        }

        try {
            try (InputStream checkUpdateStream = checkUpdateUrl.openStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(checkUpdateStream))) {
                String line = reader.readLine();
                if (line == null) {
                    return CheckAppUpdateResult.NOT_FOUND;
                }
                updateVersion = new VersionNumbers();
                log(Level.INFO, updaterBundle.getString("availableApplicationVersion") + line);
                updateVersion.versionFromString(line);
            }

            // Compare versions
            if (updateVersion.isGreaterThan(getVersionNumbers())) {
                return CheckAppUpdateResult.UPDATE_FOUND;
            }

            return CheckAppUpdateResult.NO_UPDATE_AVAILABLE;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            return CheckAppUpdateResult.NOT_FOUND;
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            return CheckAppUpdateResult.CONNECTION_ISSUE;
        } catch (Exception ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            return CheckAppUpdateResult.CONNECTION_ISSUE;
        }
    }

    public UpdatePlan checkForModsUpdate() {
        if (filesUpdateUrl == null) {
            return new UpdatePlan(CheckModsUpdateResult.UPDATE_URL_NOT_SET, null);
        }

        modsFiles.clear();
        try {
            try (InputStream checkUpdateStream = filesUpdateUrl.openStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(checkUpdateStream))) {
                String line = reader.readLine();
                while (line != null && !line.isEmpty()) {
                    modsFiles.add(line);
                    line = reader.readLine();
                }
                if (modsFiles.isEmpty()) {
                    log(Level.WARNING, updaterBundle.getString("listOfModsIsEmpty"));
                    return new UpdatePlan(CheckModsUpdateResult.NOT_FOUND, null);
                }
            }

            // Compare list of mods
            Set<String> installedMods = new HashSet<>();
            Set<String> downloadMods = new HashSet<>();
            Set<String> deleteMods = new HashSet<>();
            Set<String> remoteMods = new HashSet<>();
            currentFiles = getModRecords();

            ProfilePathResult profilePathResult = buildProfilePath();
            String resultProfilePath = profilePathResult.profilePath;
            if (resultProfilePath == null) {
                return new UpdatePlan(CheckModsUpdateResult.NO_TARGET_DIRECTORY, profilePathResult.errorMessage);
            }

            String profileModsDir = resultProfilePath + File.separator + "mods";
            if (!new File(profileModsDir).exists()) {
                return new UpdatePlan(CheckModsUpdateResult.NO_TARGET_MOD_DIRECTORY, null);
            }

            // List installed mods
            File modsDirectory = new File(profileModsDir);
            for (File modFile : modsDirectory.listFiles()) {
                installedMods.add(modFile.getName().toLowerCase());
            }

            // Add missing mods to download list
            for (String mod : modsFiles) {
                remoteMods.add(mod.toLowerCase());
                if (!installedMods.contains(mod.toLowerCase())) {
                    downloadMods.add(mod);
                }
            }

            // Add mods which are no longer needed to delete list
            for (String mod : currentFiles) {
                File targetFile = new File(profileModsDir + File.separator + mod);
                if (targetFile.exists() && !remoteMods.contains(mod.toLowerCase())) {
                    deleteMods.add(mod);
                }
            }

            if (!downloadMods.isEmpty() || !deleteMods.isEmpty()) {
                log(Level.WARNING, updaterBundle.getString("modsToDownloadCount") + downloadMods.size());
                log(Level.WARNING, updaterBundle.getString("modsToDeleteCount") + deleteMods.size());
                return new UpdatePlan(CheckModsUpdateResult.UPDATE_FOUND, resultProfilePath, downloadMods, deleteMods);
            }

            return new UpdatePlan(CheckModsUpdateResult.NO_UPDATE_AVAILABLE, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            return new UpdatePlan(CheckModsUpdateResult.NOT_FOUND, null);
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            return new UpdatePlan(CheckModsUpdateResult.CONNECTION_ISSUE, null);
        }
    }

    private ProfilePathResult buildProfilePath() {
        String resultProfilePath = null;
        if (profilePathAuto) {
            JSONParser parser = new JSONParser();
            String resultGamePath = buildGamePath();
            log(Level.INFO, updaterBundle.getString("pathToConfiguration") + resultGamePath);
            File profilesFile = new File(resultGamePath + File.separator + "launcher_profiles.json");
            if (!profilesFile.exists()) {
                return new ProfilePathResult(null, updaterBundle.getString("profilesFileNotFound"));
            }

            try {
                FileReader fileReader = new FileReader(profilesFile);
                JSONObject profileFile = (JSONObject) parser.parse(fileReader);
                JSONObject profiles = (JSONObject) profileFile.get("profiles");
                JSONObject minecartProfile = null;
                minecartProfile = (JSONObject) profiles.get(profileName);
                if (minecartProfile == null) {
                    // Scan for alternative names
                    Set keySet = profiles.keySet();
                    for (Object key : keySet) {
                        if (key instanceof String && ((String) key).equalsIgnoreCase(profileName)) {
                            profileName = (String) key;
                            minecartProfile = (JSONObject) profiles.get(key);
                            break;
                        }
                    }
                }
                if (minecartProfile == null) {
                    return new ProfilePathResult(null, updaterBundle.getString("profileNotFound") + profileName);
                }
                log(Level.INFO, updaterBundle.getString("profileName") + profileName);
                String minecartProfileDir = (String) minecartProfile.get("gameDir");
                if (minecartProfileDir == null || minecartProfileDir.isEmpty()) {
                    resultProfilePath = resultGamePath;
                } else {
                    resultProfilePath = minecartProfileDir;
                }
                log(Level.INFO, updaterBundle.getString("pathToProfile") + resultProfilePath);
            } catch (ParseException | FileNotFoundException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            resultProfilePath = profilePath;
        }

        return new ProfilePathResult(resultProfilePath, null);
    }

    private String buildGamePath() {
        if (!gamePathAuto) {
            return gamePath;
        }

        switch (osType) {
            case LINUX: {
                return System.getProperty("user.home") + File.separator + ".minecraft";
            }
            case WINDOWS: {
                return System.getenv("APPDATA") + File.separator + ".minecraft";
            }
            case MAC: {
                return System.getProperty("user.home") + "Library/Application Support/minecraft";
            }
        }

        return null;
    }

    public ModsUpdateResult performModsUpdate(UpdatePlan updatePlan, UpdatePlanObserver statusObserver) {
        String resultProfilePath = updatePlan.profilePath;
        String profileModsDir = resultProfilePath + File.separator + "mods";
        int downloadModsSize = updatePlan.downloadMods.size();
        if (downloadModsSize > 0 && statusObserver != null) {
            statusObserver.reportProgress(false, 0);
        }

        // Add new files not present in previous set
        int index = 0;
        for (String mod : updatePlan.downloadMods) {
            String modsUrlPath;
            try {
                modsUrlPath = updateDownloadUrl.toURI().toString() + "/mods";
            } catch (URISyntaxException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
                return ModsUpdateResult.DOWNLOAD_ERROR;
            }

            File targetFile = new File(profileModsDir + File.separator + mod);

            try {
                URL modFileUrl = new URI(modsUrlPath + "/" + URLEncoder.encode(mod, "UTF-8").replaceAll("\\+", "%20")).toURL();
                try (InputStream modFileStream = modFileUrl.openStream()) {
                    targetFile.createNewFile();
                    try (OutputStream targetFileStream = new FileOutputStream(targetFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        do {
                            length = modFileStream.read(buffer);

                            if (length >= 0) {
                                targetFileStream.write(buffer, 0, length);
                            }
                        } while (length >= 0);
                    }
                }
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
                return ModsUpdateResult.DOWNLOAD_ERROR;
            }
            index++;
            if (statusObserver != null) {
                statusObserver.reportProgress(false, (index * 100) / downloadModsSize);
            }
            currentFiles.add(mod);
        }

        // Delete all files not present in new mods list
        if (statusObserver != null) {
            statusObserver.reportProgress(true, 0);
        }
        for (String mod : updatePlan.deleteMods) {
            File targetFile = new File(profileModsDir + File.separator + mod);
            if (targetFile.exists()) {
                if (targetFile.delete()) {
                    currentFiles.remove(mod);
                }
            }
        }

        log(Level.INFO, updaterBundle.getString("updatePerformed"));
        return ModsUpdateResult.UPDATE_OK;
    }

    public VersionNumbers getVersionNumbers() {
        String releaseString = updaterConfigurationBundle.getString("Application.version");
        VersionNumbers versionNumbers = new VersionNumbers();
        versionNumbers.versionFromString(releaseString);
        return versionNumbers;
    }

    public Set<String> getModRecords() {
        Set<String> files = new HashSet<>();
        int index = 0;
        String mod;
        do {
            mod = config.getProperty(MOD_RECORD_PREFIX + index, null);
            if (mod != null && !mod.isEmpty()) {
                files.add(mod);
            }
            index++;
        } while (mod != null);

        return files;
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

    public void loadConfiguration() {
        configFile = new File("./minecart-updater.cfg");
        if (configFile.exists()) {
            try {
                try (FileInputStream configInput = new FileInputStream(configFile)) {
                    config.load(configInput);
                }
                log(Level.INFO, updaterBundle.getString("pathToLoadedConfigurationFile") + configFile.getName());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        gamePath = config.getProperty(GAME_PATH_PROPERTY, "");
        gamePathAuto = Boolean.valueOf(config.getProperty(GAME_PATH_AUTO_PROPERTY, Boolean.TRUE.toString()));
        profilePath = config.getProperty(PROFILE_PATH_PROPERTY, "");
        profilePathAuto = Boolean.valueOf(config.getProperty(PROFILE_PATH_AUTO_PROPERTY, Boolean.TRUE.toString()));
        runCommand = config.getProperty(RUN_COMMAND_PROPERTY, "");
        runCommandAuto = Boolean.valueOf(config.getProperty(RUN_COMMAND_AUTO_PROPERTY, Boolean.TRUE.toString()));
    }

    public void saveConfiguration() {
        config.setProperty(GAME_PATH_PROPERTY, gamePath);
        config.setProperty(GAME_PATH_AUTO_PROPERTY, Boolean.toString(gamePathAuto));

        config.setProperty(PROFILE_PATH_PROPERTY, profilePath);
        config.setProperty(PROFILE_PATH_AUTO_PROPERTY, Boolean.toString(profilePathAuto));

        config.setProperty(RUN_COMMAND_PROPERTY, runCommand);
        config.setProperty(RUN_COMMAND_AUTO_PROPERTY, Boolean.toString(runCommandAuto));

        /**
         * Saves lists of currently installed mods
         */
        int index = 0;
        if (currentFiles != null) { 
            for (String mod : currentFiles) {
                config.setProperty(MOD_RECORD_PREFIX + index, mod);
                index++;
            }
            config.setProperty(MOD_RECORD_PREFIX + index, "");
        }

        FileOutputStream configOutput;
        try {
            configOutput = new FileOutputStream(configFile);
            config.store(configOutput, "Minecart Updater");
            configOutput.close();
            log(Level.INFO, updaterBundle.getString("configurationSaved"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadServerConfiguration() {
        try {
            newsUrl = new URI(serverConfiguration.getString("news_url")).toURL();
            checkUpdateUrl = new URI(serverConfiguration.getString("update_url")).toURL();
            filesUpdateUrl = new URI(serverConfiguration.getString("update_files_url")).toURL();
            forgeUpdateUrl = new URI(serverConfiguration.getString("update_forge_url")).toURL();
            appDownloadUrl = new URI(serverConfiguration.getString("download_laucher_url")).toURL();
            websiteUrl = new URI(serverConfiguration.getString("website_url")).toURL();
            log(Level.INFO, updaterBundle.getString("websiteInUse") + websiteUrl);
            updateDownloadUrl = new URI(serverConfiguration.getString("update_download_url")).toURL();
        } catch (URISyntaxException | MalformedURLException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getGamePath() {
        return gamePath;
    }

    public void setGamePath(String gamePath) {
        this.gamePath = gamePath;
    }

    public boolean isGamePathAuto() {
        return gamePathAuto;
    }

    public void setGamePathAuto(boolean gamePathAuto) {
        this.gamePathAuto = gamePathAuto;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public boolean isProfilePathAuto() {
        return profilePathAuto;
    }

    public void setProfilePathAuto(boolean profilePathAuto) {
        this.profilePathAuto = profilePathAuto;
    }

    public String getRunCommand() {
        return runCommand;
    }

    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
    }

    public boolean isRunCommandAuto() {
        return runCommandAuto;
    }

    public void setRunCommandAuto(boolean runCommandAuto) {
        this.runCommandAuto = runCommandAuto;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public String getFrameTitle() {
        return updaterConfigurationBundle.getString("Frame.title");
    }

    public String getFrameIconPath() {
        return updaterConfigurationBundle.getString("Frame.icon");
    }

    public static class ProfilePathResult {

        public ProfilePathResult(String profilePath, String errorMessage) {
            this.profilePath = profilePath;
            this.errorMessage = errorMessage;
        }

        String profilePath;
        String errorMessage;
    }

    public static class UpdatePlan {

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

    public static interface UpdatePlanObserver {

        /**
         * Reports state of the update progress.
         *
         * @param indeterminate indeterminate flag
         * @param progress progress value (0 to 100)
         */
        void reportProgress(boolean indeterminate, int progress);
    }

    public static enum LoadNewsResult {
        OK,
        FAILED
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
