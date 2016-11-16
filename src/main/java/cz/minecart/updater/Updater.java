package cz.minecart.updater;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Minecart server updater.
 *
 * @author Minecart team
 */
public class Updater extends javax.swing.JFrame implements HyperlinkListener {

    private static final String GAME_PATH_PROPERTY = "gamePath";
    private static final String GAME_PATH_AUTO_PROPERTY = "gamePathAuto";
    private static final String PROFILE_PATH_PROPERTY = "profilePath";
    private static final String PROFILE_PATH_AUTO_PROPERTY = "profilePathAuto";
    private static final String RUN_COMMAND_PROPERTY = "runCommand";
    private static final String RUN_COMMAND_AUTO_PROPERTY = "runCommandAuto";
    private static final String MOD_RECORD_PREFIX = "mod_";

    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("cz/minecart/updater/resources/Updater");
    private final Properties config = new Properties();
    private File configFile;

    private OsType osType = OsType.LINUX;
    private URL newsUrl;
    private URL checkUpdateUrl;
    private URL appDownloadUrl;
    private URL websiteUrl;
    private URL filesUpdateUrl;
    private URL forgeUpdateUrl;
    private URL updateDownloadUrl;

    private VersionNumbers updateVersion;
    private Set<String> currentFiles = null;
    private final Set<String> modsFiles = new HashSet<>();
    private AnimatedBanner banner;

    public Updater() {
        initComponents();
        init();
    }

    private void init() {
        newsTextPane.addHyperlinkListener(this);
        configFile = new File("./minecart-updater.cfg");
        if (configFile.exists()) {
            try {
                try (FileInputStream configInput = new FileInputStream(configFile)) {
                    config.load(configInput);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Detect operating system type
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            osType = OsType.WINDOWS;
        } else if (osName.contains("mac")) {
            osType = OsType.MAC;
        }

        String gamePath = config.getProperty(GAME_PATH_PROPERTY, "");
        boolean gamePathAuto = Boolean.valueOf(config.getProperty(GAME_PATH_AUTO_PROPERTY, Boolean.TRUE.toString()));
        gamePathCheckBox.setSelected(gamePathAuto);
        updateGamePathVisibility();
        if (gamePathAuto && (gamePath == null || gamePath.isEmpty())) {
            gamePath = getDefaultGamePath();
        }
        gamePathTextField.setText(gamePath);

        String profilePath = config.getProperty(PROFILE_PATH_PROPERTY, "");
        profilePathTextField.setText(profilePath);
        boolean profilePathAuto = Boolean.valueOf(config.getProperty(PROFILE_PATH_AUTO_PROPERTY, Boolean.TRUE.toString()));
        profilePathCheckBox.setSelected(profilePathAuto);
        updateProfilePathVisibility();

        String runCommand = config.getProperty(RUN_COMMAND_PROPERTY, "");
        runCommandTextField.setText(runCommand);
        boolean runCommandAuto = Boolean.valueOf(config.getProperty(RUN_COMMAND_AUTO_PROPERTY, Boolean.TRUE.toString()));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dispose();
                System.exit(0);
            }
        });

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

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
        controlPanel = new javax.swing.JPanel();
        errorPanel = new javax.swing.JPanel();
        errorIconLabel = new javax.swing.JLabel();
        checkingPanel = new javax.swing.JPanel();
        checkingIconLabel = new javax.swing.JLabel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Minecart Reloaded Updater");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icon.png")).getImage());
        setSize(new java.awt.Dimension(781, 384));

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
        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        newsTextPane.setEditable(false);
        newsTextPane.setContentType("text/html"); // NOI18N
        newsTextPane.setText("<html>\n  <head>\n\n  </head>\n  <body>\n<p>Novinky se načítají...</p>\n  </body>\n</html>\n");
        newsScrollPane.setViewportView(newsTextPane);

        tabbedPane.addTab("Novinky", newsScrollPane);

        optionsPanel.setLayout(new javax.swing.BoxLayout(optionsPanel, javax.swing.BoxLayout.LINE_AXIS));

        gamePathLabel.setText("Cesta k Minecraftu");

        gamePathCheckBox.setSelected(true);
        gamePathCheckBox.setText("Výchozí cesta");
        gamePathCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gamePathCheckBoxActionPerformed(evt);
            }
        });

        gamePathTextField.setEnabled(false);

        gamePathButton.setText("Vybrat...");
        gamePathButton.setEnabled(false);
        gamePathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gamePathButtonActionPerformed(evt);
            }
        });

        profilePathLabel.setText("Cesta k Minecraft profilu (minecart.cz)");

        profilePathCheckBox.setSelected(true);
        profilePathCheckBox.setText("Detekovat cestu automaticky");
        profilePathCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilePathCheckBoxActionPerformed(evt);
            }
        });

        profilePathTextField.setEnabled(false);

        profilePathButton.setText("Vybrat...");
        profilePathButton.setEnabled(false);
        profilePathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilePathButtonActionPerformed(evt);
            }
        });

        runCommandLabel.setText("Příkaz pro spuštění Minecraftu");

        runCommandTextField.setText("java -jar Minecart.jar");

        runCommandButton.setText("Vybrat...");
        runCommandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runCommandButtonActionPerformed(evt);
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
                        .addContainerGap())))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        optionsScrollPane.setViewportView(optionsInnerPanel);

        optionsPanel.add(optionsScrollPane);

        tabbedPane.addTab("Nastavení", optionsPanel);

        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.CardLayout());

        errorIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        errorIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/dialog-no.png"))); // NOI18N
        errorIconLabel.setText("Došlo k chybě při zpracování");
        errorIconLabel.setToolTipText("");

        javax.swing.GroupLayout errorPanelLayout = new javax.swing.GroupLayout(errorPanel);
        errorPanel.setLayout(errorPanelLayout);
        errorPanelLayout.setHorizontalGroup(
            errorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(errorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(errorIconLabel)
                .addContainerGap(447, Short.MAX_VALUE))
        );
        errorPanelLayout.setVerticalGroup(
            errorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, errorPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorIconLabel)
                .addContainerGap())
        );

        controlPanel.add(errorPanel, "error");

        checkingIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        checkingIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/apps/internet-web-browser-7.png"))); // NOI18N
        checkingIconLabel.setText("Kontroluji dostupnost aktualizací...");
        checkingIconLabel.setToolTipText("");

        javax.swing.GroupLayout checkingPanelLayout = new javax.swing.GroupLayout(checkingPanel);
        checkingPanel.setLayout(checkingPanelLayout);
        checkingPanelLayout.setHorizontalGroup(
            checkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkingIconLabel)
                .addContainerGap(380, Short.MAX_VALUE))
        );
        checkingPanelLayout.setVerticalGroup(
            checkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, checkingPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(checkingIconLabel)
                .addContainerGap())
        );

        controlPanel.add(checkingPanel, "checking");

        playButton.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        playButton.setText("Zavřít");
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        playIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        playIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/dialog-ok-5.png"))); // NOI18N
        playIconLabel.setText("Nastavení je aktuální");

        javax.swing.GroupLayout playPanelLayout = new javax.swing.GroupLayout(playPanel);
        playPanel.setLayout(playPanelLayout);
        playPanelLayout.setHorizontalGroup(
            playPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, playPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playIconLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 407, Short.MAX_VALUE)
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
        updateIconLabel.setText("Nastavení se aktualizuje...");

        updateProgressBar.setIndeterminate(true);

        javax.swing.GroupLayout updatePanelLayout = new javax.swing.GroupLayout(updatePanel);
        updatePanel.setLayout(updatePanelLayout);
        updatePanelLayout.setHorizontalGroup(
            updatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateIconLabel)
                .addGap(18, 18, 18)
                .addComponent(updateProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
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
        newAppDownloadButton.setText("STÁHNOUT");
        newAppDownloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAppDownloadButtonActionPerformed(evt);
            }
        });

        newAppIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        newAppIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/update-product.png"))); // NOI18N
        newAppIconLabel.setText("Je k dispozici novější verze této aplikace");

        javax.swing.GroupLayout newAppPanelLayout = new javax.swing.GroupLayout(newAppPanel);
        newAppPanel.setLayout(newAppPanelLayout);
        newAppPanelLayout.setHorizontalGroup(
            newAppPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newAppPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newAppIconLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 147, Short.MAX_VALUE)
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
        forgeIconLabel.setText("Aktualizuje se forge...");

        javax.swing.GroupLayout forgePanelLayout = new javax.swing.GroupLayout(forgePanel);
        forgePanel.setLayout(forgePanelLayout);
        forgePanelLayout.setHorizontalGroup(
            forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(forgePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(forgeIconLabel)
                .addContainerGap(515, Short.MAX_VALUE))
        );
        forgePanelLayout.setVerticalGroup(
            forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(forgePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(forgeIconLabel)
                .addContainerGap())
        );

        controlPanel.add(forgePanel, "update");

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        setSize(new java.awt.Dimension(806, 538));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        String runCommand = runCommandTextField.getText();
        if (!runCommand.isEmpty()) {
            try {
                Process p = Runtime.getRuntime().exec(runCommandTextField.getText());
            } catch (IOException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        save();
        System.exit(0);
    }//GEN-LAST:event_playButtonActionPerformed

    private void newAppDownloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAppDownloadButtonActionPerformed
        BareBonesBrowserLaunch.openDesktopURL(appDownloadUrl);
    }//GEN-LAST:event_newAppDownloadButtonActionPerformed

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

    private void gamePathCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gamePathCheckBoxActionPerformed
        updateGamePathVisibility();
    }//GEN-LAST:event_gamePathCheckBoxActionPerformed

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

    private void profilePathCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profilePathCheckBoxActionPerformed
        updateProfilePathVisibility();
    }//GEN-LAST:event_profilePathCheckBoxActionPerformed

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
                return "Spustitelné soubory (*.exe, *.jar)";
            }
        });
        chooser.setAcceptAllFileFilterUsed(true);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            runCommandTextField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_runCommandButtonActionPerformed

    private void headerPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_headerPanelMouseClicked
        BareBonesBrowserLaunch.openDesktopURL(websiteUrl);
    }//GEN-LAST:event_headerPanelMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Updater.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Updater app = new Updater();
                app.setVisible(true);
                app.performUpdate();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables

    private void performUpdate() {
        // Perform checking for updates
        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    newsUrl = new URI(resourceBundle.getString("news_url")).toURL();
                    checkUpdateUrl = new URI(resourceBundle.getString("update_url")).toURL();
                    filesUpdateUrl = new URI(resourceBundle.getString("update_files_url")).toURL();
                    forgeUpdateUrl = new URI(resourceBundle.getString("update_forge_url")).toURL();
                    appDownloadUrl = new URI(resourceBundle.getString("download_laucher_url")).toURL();
                    websiteUrl = new URI(resourceBundle.getString("website_url")).toURL();
                    updateDownloadUrl = new URI(resourceBundle.getString("update_download_url")).toURL();
                    banner.setVersion("Verze aktualizátoru: " + resourceBundle.getString("Application.version"));
                } catch (URISyntaxException | MalformedURLException ex) {
                    Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
                }

                // Load news content
                loadNewsContent();

                // Check for application update
                CheckUpdatesResult appUpdate = checkForAppUpdate();
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
                    case NO_TARGET_DIRECTORY: {
                        playIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/dialog-no.png")));
                        playIconLabel.setText("Složka minecraft profilu je neplatná. Nastavte ji ručně.");
                        checkingEnded();
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
                                        playIconLabel.setText("Nastavení bylo aktualizováno.");
                                        checkingEnded();
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
                                checkingEnded();
                                break;
                            }
                            case NO_TARGET_DIRECTORY: {
                                playIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/dialog-no.png")));
                                playIconLabel.setText("Nepodařilo se najít složku Minecraft profilu");
                                checkingEnded();
                                break;
                            }
                            default: {
                                playIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/dialog-no.png")));
                                playIconLabel.setText("Nepodařilo se zkontrolovat aktualizaci");
                                checkingEnded();
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
            }
        });

        updateThread.start();
    }

    private void checkingEnded() {
        if (!runCommandTextField.getText().isEmpty()) {
            playButton.setText("Hrát >>");
        }
        ((CardLayout) controlPanel.getLayout()).show(controlPanel, "play");
    }

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

    private void loadNewsContent() {
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

            String news = result.toString("UTF-8");
            newsTextPane.setText(news);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private CheckUpdatesResult checkForAppUpdate() {
        if (checkUpdateUrl == null) {
            return CheckUpdatesResult.UPDATE_URL_NOT_SET;
        }

        try {
            try (InputStream checkUpdateStream = checkUpdateUrl.openStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(checkUpdateStream))) {
                String line = reader.readLine();
                if (line == null) {
                    return CheckUpdatesResult.NOT_FOUND;
                }
                updateVersion = new VersionNumbers();
                updateVersion.versionFromString(line);
            }

            // Compare versions
            if (updateVersion.isGreaterThan(getVersionNumbers())) {
                return CheckUpdatesResult.UPDATE_FOUND;
            }

            return CheckUpdatesResult.NO_UPDATE_AVAILABLE;
        } catch (FileNotFoundException ex) {
            return CheckUpdatesResult.NOT_FOUND;
        } catch (IOException ex) {
            return CheckUpdatesResult.CONNECTION_ISSUE;
        } catch (Exception ex) {
            return CheckUpdatesResult.CONNECTION_ISSUE;
        }
    }

    private UpdatePlan checkForModsUpdate() {
        if (filesUpdateUrl == null) {
            return new UpdatePlan(CheckUpdatesResult.UPDATE_URL_NOT_SET);
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
                    return new UpdatePlan(CheckUpdatesResult.NOT_FOUND);
                }
            }

            // Compare list of mods
            Set<String> downloadMods = new HashSet<>();
            Set<String> deleteMods = new HashSet<>();
            currentFiles = getModRecords();

            String profilePath = getProfilePath();
            if (profilePath == null) {
                return new UpdatePlan(CheckUpdatesResult.NO_TARGET_DIRECTORY);
            }

            String profileModsDir = profilePath + File.separator + "mods";
            System.out.println("ModsDir: " + profileModsDir);
            if (!new File(profileModsDir).exists()) {
                return new UpdatePlan(CheckUpdatesResult.NO_TARGET_DIRECTORY);
            }

            // Add missing mods to download list
            for (String mod : modsFiles) {
                File targetFile = new File(profileModsDir + File.separator + mod);
                if (!targetFile.exists()) {
                    downloadMods.add(mod);
                }
            }

            // Add mods which are no longer needed to delete list
            for (String mod : currentFiles) {
                File targetFile = new File(profileModsDir + File.separator + mod);
                if (targetFile.exists() && !modsFiles.contains(mod)) {
                    deleteMods.add(mod);
                }
            }

            if (!downloadMods.isEmpty() || !deleteMods.isEmpty()) {
                return new UpdatePlan(CheckUpdatesResult.UPDATE_FOUND, downloadMods, deleteMods);
            }

            return new UpdatePlan(CheckUpdatesResult.NO_UPDATE_AVAILABLE);
        } catch (FileNotFoundException ex) {
            return new UpdatePlan(CheckUpdatesResult.NOT_FOUND);
        } catch (IOException ex) {
            return new UpdatePlan(CheckUpdatesResult.CONNECTION_ISSUE);
        }
    }

    private ModsUpdateResult performModsUpdate(UpdatePlan updatePlan) {
        String profilePath = getProfilePath();
        String profileModsDir = profilePath + File.separator + "mods";
        int downloadModsSize = updatePlan.downloadMods.size();
        if (downloadModsSize > 0) {
            updateProgressBar.setIndeterminate(false);
            updateProgressBar.setValue(0);
            updateProgressBar.setMaximum(100);
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
            updateProgressBar.setValue((index * 100) / downloadModsSize);
            updateProgressBar.repaint();
        }

        // Delete all files not present in new mods list
        updateProgressBar.setIndeterminate(true);
        for (String mod : updatePlan.deleteMods) {
            File targetFile = new File(profileModsDir + File.separator + mod);
            if (targetFile.exists()) {
                targetFile.delete();
            }
        }

        // Save list of files for next update
        saveModRecords(modsFiles);
        return ModsUpdateResult.UPDATE_OK;
    }

    private void connectionIssues() {
        playIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/dialog-no.png")));
        playIconLabel.setText("Došlo k problému s připojením");
        checkingEnded();
    }

    public VersionNumbers getVersionNumbers() {
        String releaseString = resourceBundle.getString("Application.version");
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

    public void saveModRecords(Set<String> mods) {
        int index = 0;
        for (String mod : mods) {
            config.setProperty(MOD_RECORD_PREFIX + index, mod);
            index++;
        }
        config.setProperty(MOD_RECORD_PREFIX + index, "");
    }

    @Override
    public void dispose() {
        save();
        super.dispose();
    }

    private void save() {
        config.setProperty(GAME_PATH_PROPERTY, gamePathTextField.getText());
        config.setProperty(GAME_PATH_AUTO_PROPERTY, Boolean.toString(gamePathCheckBox.isSelected()));

        config.setProperty(PROFILE_PATH_PROPERTY, profilePathTextField.getText());
        config.setProperty(PROFILE_PATH_AUTO_PROPERTY, Boolean.toString(profilePathCheckBox.isSelected()));

        config.setProperty(RUN_COMMAND_PROPERTY, runCommandTextField.getText());
        config.setProperty(RUN_COMMAND_AUTO_PROPERTY, Boolean.toString(runCommandCheckBox.isSelected()));

        FileOutputStream configOutput;
        try {
            configOutput = new FileOutputStream(configFile);
            config.store(configOutput, "Minecart Updater");
            configOutput.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getDefaultGamePath() {
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

    private String getProfilePath() {
        String profilePath = null;
        if (profilePathCheckBox.isSelected()) {
            JSONParser parser = new JSONParser();
            String gamePath = getDefaultGamePath();
            File profilesFile = new File(gamePath + File.separator + "launcher_profiles.json");
            if (!profilesFile.exists()) {
                return null;
            }

            try {
                FileReader fileReader = new FileReader(profilesFile);
                JSONObject profileFile = (JSONObject) parser.parse(fileReader);
                JSONObject profiles = (JSONObject) profileFile.get("profiles");
                JSONObject minecartProfile = (JSONObject) profiles.get("minecart.cz");
                String minecartProfileDir = (String) minecartProfile.get("gameDir");
                if (minecartProfileDir == null || minecartProfileDir.isEmpty()) {
                    profilePath = gamePath;
                } else {
                    profilePath = minecartProfileDir;
                }
            } catch (ParseException | FileNotFoundException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            profilePath = profilePathTextField.getText();
        }

        return profilePath;
    }

    private void updateGamePathVisibility() {
        gamePathTextField.setEnabled(!gamePathCheckBox.isSelected());
        gamePathButton.setEnabled(!gamePathCheckBox.isSelected());
    }

    private void updateProfilePathVisibility() {
        profilePathTextField.setEnabled(!profilePathCheckBox.isSelected());
        profilePathButton.setEnabled(!profilePathCheckBox.isSelected());
    }

    private static class UpdatePlan {

        CheckUpdatesResult resultType;
        Set<String> downloadMods;
        Set<String> deleteMods;

        public UpdatePlan(CheckUpdatesResult resultType) {
            this.resultType = resultType;
        }

        public UpdatePlan(CheckUpdatesResult resultType, Set<String> downloadMods, Set<String> deleteMods) {
            this.resultType = resultType;
            this.downloadMods = downloadMods;
            this.deleteMods = deleteMods;
        }
    }

    /**
     * Enumeration of result types.
     */
    public static enum CheckUpdatesResult {
        UPDATE_URL_NOT_SET,
        NO_CONNECTION,
        CONNECTION_ISSUE,
        NOT_FOUND,
        NO_UPDATE_AVAILABLE,
        NO_TARGET_DIRECTORY,
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
}
