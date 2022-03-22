package com.dalvikjvm;

import java.awt.*;
import java.util.HashMap;

public class JVMConfig {
    public static class JREVersionInfo
    {
        public JREVersionInfo(JREVersion jreVersion, String vendor, String version, String vendorURL)
        {
            this.jreVersion = jreVersion;
            this.vendor = vendor;
            this.version = version;
            this.vendorURL = vendorURL;
        }

        public JREVersion jreVersion;
        public String vendor;
        public String version;
        public String vendorURL;
    };

    // Java versions we support
    enum JREVersion
    {
        JRE_8,
    };

    // JREs that we can emulate
    enum EmulatedJREVersion
    {
        ORACLE_8,
    };

    // Define JRE versions
    public static JREVersionInfo[] JRE_VERSION_INFO = {
            new JREVersionInfo(JREVersion.JRE_8, "Oracle Corporation", "1.8.0_281", "http://java.oracle.com/"),
    };

    public JVMConfig()
    {
        desktopParameters = new HashMap<String, String>();
        appletParameters = new HashMap<String, String>();
    }

    public JREVersionInfo getJREVersionInfo()
    {
        return JRE_VERSION_INFO[emulatedJREVersion.ordinal()];
    }

    public EmulatedJREVersion emulatedJREVersion;
    public String classPath;
    public String workingDirectory;
    public String classMain;
    public boolean applet;

    // Applet Parameters
    public String appletCodeBase;
    public Dimension appletSize;
    public HashMap<String, String> appletParameters;

    // Desktop Parameters
    public HashMap<String, String> desktopParameters;
}
