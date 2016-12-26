package com.sekwah.advancedportals.util;

import com.sekwah.advancedportals.AdvancedPortalsPlugin;

import java.io.*;

/**
 * Used to find and save default files when the usual existing files are not there on startup. For example plugin config
 * portal locations, language files etc. There is some built into base spigot but thats soley for configs.
 * This just makes it more organised and able to save any file if the origional does not exist.
 *
 * @author sekwah41
 */
public class DefaultLoader {

    private static File dataFolder = AdvancedPortalsPlugin.getInstance().getDataFolder();

    /**
     * Copies the default file, defaults to true to keep true to the name
     *
     * @param fileLoc
     * @return
     */
    public static boolean copyDefaultFile(String fileLoc) {
        return copyDefaultFile(fileLoc, true);
    }

    public static void copyDefaultFiles(boolean override, String... fileLocs) {
        for (String fileLoc : fileLocs) {
            copyDefaultFile(fileLoc, override);
        }
    }

    /**
     * Copies the specified file out of the plugin and into the plugins folder.
     *
     * @param fileLoc
     * @return if the file is copied, will be false if override is false and the file already existed.
     */
    public static boolean copyDefaultFile(String fileLoc, boolean overwrite) {
        File outFile = new File(dataFolder, fileLoc);
        if (!outFile.exists()) {
            outFile.getParentFile().mkdirs();
        }
        if (!outFile.exists() || overwrite) {
            try {
                InputStream inputStream = DefaultLoader.class.getClassLoader().getResourceAsStream(fileLoc);
                FileOutputStream outStream = new FileOutputStream(outFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outStream.write(buf, 0, len);
                }
                inputStream.close();
                outStream.close();
            } catch (NullPointerException e) {
                e.printStackTrace();
                AdvancedPortalsPlugin.getInstance().getLogger().warning("Could not load " + fileLoc + ". The file does" +
                        "not exist or there has been an error reading the file.");
                return false;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                AdvancedPortalsPlugin.getInstance().getLogger().warning("Could not create " + fileLoc);
            } catch (IOException e) {
                e.printStackTrace();
                AdvancedPortalsPlugin.getInstance().getLogger().warning("File error reading " + fileLoc);
            }
        }
        return true;
    }

    /**
     * A method to try to grab the files from the plugin and if its in the plugin folder load from there instead.
     * <p>
     * TODO add loading from the plugin folder first rather than straight from the plugin.
     *
     * @param lang
     * @param location
     * @return
     */
    public static InputStream loadResource(Lang lang, String location) {
        File inFile = new File(dataFolder, location);
        if (inFile.exists() && !inFile.isDirectory()) {
            try {
                return new FileInputStream(inFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                copyDefaultFile(location, false);
                return lang.getClass().getClassLoader().getResourceAsStream(location);
            } catch (NullPointerException e) {
                e.printStackTrace();
                AdvancedPortalsPlugin.getInstance().getLogger().warning("Could not load " + location + ". The file does" +
                        "not exist or there has been an error reading the file.");
                return null;
            }
        }
    }

}