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
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
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
    private String profileName;

    private VersionNumbers updateVersion;
    private Set<String> currentFiles = null;
    private final Set<String> modsFiles = new HashSet<>();
    private AnimatedBanner banner;

    public Updater() {
        initComponents();
        init();
    }

    private void init() {
        registerLogger();
        newsTextPane.addHyperlinkListener(this);
        configFile = new File("./minecart-updater.cfg");
        if (configFile.exists()) {
            try {
                try (FileInputStream configInput = new FileInputStream(configFile)) {
                    config.load(configInput);
                }
                Logger.getLogger(Updater.class.getName()).log(Level.INFO, "Načten konfigurační soubor: " + configFile.getName());
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
        Logger.getLogger(Updater.class.getName()).log(Level.INFO, "Detekován operační systém: " + osType.name());

        String gamePath = config.getProperty(GAME_PATH_PROPERTY, "");
        boolean gamePathAuto = Boolean.valueOf(config.getProperty(GAME_PATH_AUTO_PROPERTY, Boolean.TRUE.toString()));
        gamePathCheckBox.setSelected(gamePathAuto);
        updateGamePathVisibility();
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
        warningPlayButton = new javax.swing.JButton();
        warningPlayIconLabel = new javax.swing.JLabel();
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

        profilePathLabel.setText("Cesta k Minecraft profilu");

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

        checkUpdateButton.setText("Vyhledat aktualizace");
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
                        .addGap(214, 554, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        optionsScrollPane.setViewportView(optionsInnerPanel);

        optionsPanel.add(optionsScrollPane);

        tabbedPane.addTab("Nastavení", optionsPanel);

        logPanel.setLayout(new java.awt.BorderLayout());

        logTextArea.setEditable(false);
        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        logTextArea.setText("Aplikační log:\n");
        logScrollPane.setViewportView(logTextArea);

        logPanel.add(logScrollPane, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Log", logPanel);

        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.CardLayout());

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

        warningPlayButton.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        warningPlayButton.setText("Zavřít");
        warningPlayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warningPlayButtonActionPerformed(evt);
            }
        });

        warningPlayIconLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        warningPlayIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/minecart/updater/resources/images/icons/png/48x48/actions/dialog-no.png"))); // NOI18N
        warningPlayIconLabel.setText("Došlo k chybě");

        javax.swing.GroupLayout warningPanelLayout = new javax.swing.GroupLayout(warningPanel);
        warningPanel.setLayout(warningPanelLayout);
        warningPanelLayout.setHorizontalGroup(
            warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, warningPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(warningPlayIconLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 481, Short.MAX_VALUE)
                .addComponent(warningPlayButton)
                .addContainerGap())
        );
        warningPanelLayout.setVerticalGroup(
            warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, warningPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(warningPlayIconLabel)
                    .addComponent(warningPlayButton))
                .addContainerGap())
        );

        controlPanel.add(warningPanel, "warning");

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

        controlPanel.add(forgePanel, "forge");

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        setSize(new java.awt.Dimension(806, 548));
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

    private void checkUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkUpdateButtonActionPerformed
        checkUpdateButton.setEnabled(false);
        ((CardLayout) controlPanel.getLayout()).show(controlPanel, "checking");
        Logger.getLogger(Updater.class.getName()).log(Level.INFO, "\nNové hledání aktualizací...");
        performUpdate();
    }//GEN-LAST:event_checkUpdateButtonActionPerformed

    private void warningPlayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warningPlayButtonActionPerformed
        playButtonActionPerformed(evt);
    }//GEN-LAST:event_warningPlayButtonActionPerformed

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
    private javax.swing.JPanel warningPanel;
    private javax.swing.JButton warningPlayButton;
    private javax.swing.JLabel warningPlayIconLabel;
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
                    Logger.getLogger(Updater.class.getName()).log(Level.INFO, "Používá se web: " + websiteUrl);
                    updateDownloadUrl = new URI(resourceBundle.getString("update_download_url")).toURL();
                    profileName = resourceBundle.getString("profileName");
                    profilePathLabel.setText("Cesta k minecraft profilu (" + profileName + ")");
                    banner.setVersion("Verze aktualizátoru: " + resourceBundle.getString("Application.version"));
                } catch (URISyntaxException | MalformedURLException ex) {
                    Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
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

    private void actionSucessful(String okMessage) {
        if (!runCommandTextField.getText().isEmpty()) {
            playButton.setText("Hrát >>");
        }
        playIconLabel.setText(okMessage);
        ((CardLayout) controlPanel.getLayout()).show(controlPanel, "play");
    }

    private void actionFailed(String errorMessage) {
        if (!runCommandTextField.getText().isEmpty()) {
            warningPlayButton.setText("Hrát >>");
        }
        warningPlayIconLabel.setText(errorMessage);
        ((CardLayout) controlPanel.getLayout()).show(controlPanel, "warning");
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
            Logger.getLogger(Updater.class.getName()).log(Level.INFO, "Novinky načteny");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private CheckAppUpdateResult checkForAppUpdate() {
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
                Logger.getLogger(Updater.class.getName()).log(Level.INFO, "Dostupná verze aktualizátoru z internetu: " + line);
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

    private UpdatePlan checkForModsUpdate() {
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
                    Logger.getLogger(Updater.class.getName()).log(Level.WARNING, "Seznam modů na webu je prázdný");
                    return new UpdatePlan(CheckModsUpdateResult.NOT_FOUND, null);
                }
            }

            // Compare list of mods
            Set<String> installedMods = new HashSet<>();
            Set<String> downloadMods = new HashSet<>();
            Set<String> deleteMods = new HashSet<>();
            Set<String> remoteMods = new HashSet<>();
            currentFiles = getModRecords();

            ProfilePathResult profilePathResult = getProfilePath();
            String profilePath = profilePathResult.profilePath;
            if (profilePath == null) {
                return new UpdatePlan(CheckModsUpdateResult.NO_TARGET_DIRECTORY, profilePathResult.errorMessage);
            }

            String profileModsDir = profilePath + File.separator + "mods";
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
                Logger.getLogger(Updater.class.getName()).log(Level.WARNING, "Modů ke stažení: " + downloadMods.size());
                Logger.getLogger(Updater.class.getName()).log(Level.WARNING, "Modů ke smazání: " + deleteMods.size());
                return new UpdatePlan(CheckModsUpdateResult.UPDATE_FOUND, profilePath, downloadMods, deleteMods);
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

    private ModsUpdateResult performModsUpdate(UpdatePlan updatePlan) {
        String profilePath = updatePlan.profilePath;
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
            currentFiles.add(mod);
        }

        // Delete all files not present in new mods list
        updateProgressBar.setIndeterminate(true);
        for (String mod : updatePlan.deleteMods) {
            File targetFile = new File(profileModsDir + File.separator + mod);
            if (targetFile.exists()) {
                if (targetFile.delete()) {
                    currentFiles.remove(mod);
                }
            }
        }

        Logger.getLogger(Updater.class.getName()).log(Level.INFO, "Aktualizace byla provedena");
        return ModsUpdateResult.UPDATE_OK;
    }

    private void connectionIssues() {
        actionFailed("Došlo k problému s připojením");
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

        int index = 0;
        for (String mod : currentFiles) {
            config.setProperty(MOD_RECORD_PREFIX + index, mod);
            index++;
        }
        config.setProperty(MOD_RECORD_PREFIX + index, "");

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

    private String getGamePath() {
        if (!gamePathCheckBox.isSelected()) {
            return gamePathTextField.getText();
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

    private ProfilePathResult getProfilePath() {
        String profilePath = null;
        if (profilePathCheckBox.isSelected()) {
            JSONParser parser = new JSONParser();
            String gamePath = getGamePath();
            Logger.getLogger(Updater.class.getName()).log(Level.INFO, "Cesta ke konfiguraci: " + gamePath);
            File profilesFile = new File(gamePath + File.separator + "launcher_profiles.json");
            if (!profilesFile.exists()) {
                return new ProfilePathResult(null, "Nepodařilo se najít soubor s profily");
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
                    return new ProfilePathResult(null, "Nepodařilo se najít profil " + profileName);
                }
                Logger.getLogger(Updater.class.getName()).log(Level.WARNING, "Název profilu: " + profileName);
                String minecartProfileDir = (String) minecartProfile.get("gameDir");
                if (minecartProfileDir == null || minecartProfileDir.isEmpty()) {
                    profilePath = gamePath;
                } else {
                    profilePath = minecartProfileDir;
                }
                Logger.getLogger(Updater.class.getName()).log(Level.WARNING, "Cesta k profilu: " + profilePath);
            } catch (ParseException | FileNotFoundException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            profilePath = profilePathTextField.getText();
        }

        return new ProfilePathResult(profilePath, null);
    }

    private void registerLogger() {
        Logger.getLogger(Updater.class.getName()).addHandler(new StreamHandler() {
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

    private static class ProfilePathResult {

        public ProfilePathResult(String profilePath, String errorMessage) {
            this.profilePath = profilePath;
            this.errorMessage = errorMessage;
        }

        String profilePath;
        String errorMessage;
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
}
