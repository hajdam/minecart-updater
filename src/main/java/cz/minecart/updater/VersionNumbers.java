package cz.minecart.updater;

/**
 * Simple structure for application version.
 */
public class VersionNumbers {

    private VersionNumbersFormat format;
    private int major;
    private int minor;
    private int release;
    private int patch;

    public VersionNumbers() {
    }

    public void versionFromString(String version) {
        int minorPos = version.indexOf(".");
        major = Integer.valueOf(version.substring(0, minorPos));
        int releasePos = version.indexOf(".", minorPos + 1);
        minor = Integer.valueOf(version.substring(minorPos + 1, releasePos));
        int patchPos = version.indexOf(".", releasePos + 1);
        if (patchPos > 0) {
            format = VersionNumbersFormat.MAJOR_MINOR_RELEASE_PATCH;
            release = Integer.valueOf(version.substring(releasePos + 1, patchPos));
            patch = Integer.valueOf(version.substring(patchPos + 1));
        } else {
            format = VersionNumbersFormat.MAJOR_MINOR_PATCH;
            patch = Integer.valueOf(version.substring(releasePos + 1));
        }
    }

    public String versionAsString() {
        if (format == null) {
            return null;
        }

        switch (format) {
            case MAJOR_MINOR_PATCH: {
                return major + "." + minor + "." + patch;
            }
            case MAJOR_MINOR_RELEASE_PATCH: {
                return major + "." + minor + "." + release + "." + patch;
            }
            default:
                throw new IllegalStateException("Unexpected format " + format.name());
        }
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getRelease() {
        return release;
    }

    public void setRelease(int release) {
        this.release = release;
    }

    public int getPatch() {
        return patch;
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    public boolean isGreaterThan(VersionNumbers updateVersion) {
        if (major > updateVersion.major) {
            return true;
        }
        if (major == updateVersion.major && minor > updateVersion.minor) {
            return true;
        }
        if (minor == updateVersion.minor) {
            switch (format) {
                case MAJOR_MINOR_PATCH: {
                    switch (updateVersion.format) {
                        case MAJOR_MINOR_PATCH: {
                            if (patch > updateVersion.patch) {
                                return true;
                            }
                            break;
                        }

                        case MAJOR_MINOR_RELEASE_PATCH: {
                            if (updateVersion.release == 0 && patch > updateVersion.patch) {
                                return true;
                            }
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unpexpected format type " + updateVersion.format.name());
                    }

                    break;
                }
                case MAJOR_MINOR_RELEASE_PATCH: {
                    switch (updateVersion.format) {
                        case MAJOR_MINOR_PATCH: {
                            if (release > 0) {
                                return true;
                            }
                            if (release == 0 && patch > updateVersion.patch) {
                                return true;
                            }
                            break;
                        }

                        case MAJOR_MINOR_RELEASE_PATCH: {
                            if (release > updateVersion.release) {
                                return true;
                            }
                            if (release == updateVersion.release && patch > updateVersion.patch) {
                                return true;
                            }
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unpexpected format type " + updateVersion.format.name());
                    }

                    break;
                }
                default:
                    throw new IllegalStateException("Unpexpected format type " + format.name());
            }
        }

        return false;
    }

    public static enum VersionNumbersFormat {
        MAJOR_MINOR_PATCH, MAJOR_MINOR_RELEASE_PATCH
    }
}
