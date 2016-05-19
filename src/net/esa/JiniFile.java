package net.esa;

import java.io.*;
import java.util.*;

/**
 * INIFile v.1.0
 *
 * Simple class for manage INI-files
 *
 * Created by Sergei Yeryomin on 19.05.16.
 *
 * TODO: INI-file comment management: add, remove, edit, read from file, save to file
 * TODO: proper exceptions types
 * TODO: arrays as values: writeIntegerArray / readIntegerArray etc.
 */
public class JiniFile {
    private Map<String, Properties> iniMap;
    private File file;

    /**
     * Constructs INI file object
     * @param file  file object
     * @throws IOException
     */
    public JiniFile(File file) throws IOException {
        this.file = file;

        if (!file.exists()) {
            iniMap = new TreeMap<>();
        } else {
            iniMap = parseFile(file);
        }

    }

    /**
     * Parse INI-file and builds TreeMap object,
     * where INI-file section is a key, and variables and its values is a values of Map.
     * @param   file    File object
     * @return  TreeMap \<String, Properties\> object
     * @throws  IOException
     */
    private Map<String, Properties> parseFile(File file) throws IOException{

        FileReader fr = new FileReader(file);
        int b;
        StringBuilder sb = new StringBuilder();
        List<String> lines = new LinkedList<>();

        while ((b = fr.read()) != -1) {
            char c = (char) b;
            if ((c == ';') || (c == '#')) {
                c = '\n';
            }
            if (c == '\n') {
                lines.add(sb.toString().trim());
                sb.delete(0, sb.length());
            }
            sb.append(c);
        }
        fr.close();

        Map<String, Properties> result = new TreeMap<>();

        Properties properties = new Properties();
        String section = "";
        for (String line: lines) {
            if (line.length() == 0) continue;
            if ((line.charAt(0) == '[') && (line.charAt(line.length()-1) == ']')) {
                if (!section.equals("")) {
                    result.put(section, properties);
                }
                section = (line.replace('[', '\0').replace(']', '\0').trim());
                properties = new Properties();
            } else if (line.contains("=")) {
                String[] props = line.split("=");
                if (props.length > 0) {
                    properties.setProperty(props[0].trim(), props.length == 2? props[1].trim() : "");
                }
            }
        }
        result.put(section, properties);
        return result;
    }


    /**
     * Universal method to add key-value pair to INI-file section;
     * @param section    INI-file section name
     * @param key        INI-file variable name
     * @param value      INI-file variable value
     */
    private void writeProperty(String section, String key, Object value) {
        String strVal;

        if (value.getClass() == Boolean.class) {
            strVal = ((Boolean) value) ? "1" : "0";
        } else {
            strVal = value.toString();
        }

        Properties properties;

        properties = iniMap.get(section);
        if (properties == null) {
            properties = new Properties();
            iniMap.put(section, properties);
        }

        properties.setProperty(key, strVal);
    }


    /**
     * Returns String value of given INI-file key name
     * @param section   INI-file section name
     * @param key       INI-file key name
     * @return          String key value
     */
    public String readString(String section, String key) {
        return (iniMap.get(section)).getProperty(key);
    }

    /**
     * Returns Integer value of given INI-file key name
     * @param section   INI-file section name
     * @param key       INI-file key name
     * @return          Integer key value
     */
    public Integer readInteger(String section, String key) {
        return Integer.valueOf((iniMap.get(section)).getProperty(key));
    }

    /**
     * Returns Double value of given INI-file key name
     * @param section   INI-file section name
     * @param key       INI-file key name
     * @return          Double key value
     */
    public Double readDouble(String section, String key) {
        return Double.valueOf((iniMap.get(section)).getProperty(key));
    }

    /**
     * Returns Boolean value of given INI-file key name
     * @param section   INI-file section name
     * @param key       INI-file key name
     * @return          Boolean key value
     */
    public Boolean readBoolean(String section, String key) {
        return (iniMap.get(section)).getProperty(key).equals("1");
    }

    /**
     * Sets String value to a given INI-file key
     * @param section   INI-file section name
     * @param key       INI-file key name
     * @param value     key String value
     */
    public void writeString(String section, String key, String value) {
        writeProperty(section, key, value);
    }

    /**
     * Sets Integer value to a given INI-file key
     * @param section   INI-file section name
     * @param key       INI-file key name
     * @param value     key Integer value
     */
    public void writeInteger(String section, String key, Integer value) {
        writeProperty(section, key, value);
    }

    /**
     * Sets Double value to a given INI-file key
     * @param section   INI-file section name
     * @param key       INI-file key name
     * @param value     key Double value
     */
    public void writeDouble(String section, String key, Double value) {
        writeProperty(section, key, value);
    }

    /**
     * Sets Boolean value to a given INI-file key
     * @param section   INI-file section name
     * @param key       INI-file key name
     * @param value     key Boolean value
     */
    public void writeBoolean(String section, String key, Boolean value) {
        writeProperty(section, key, value);
    }


    /**
     * Saves changes to file
     * @throws IOException
     */
    public void save() throws IOException {
        try (PrintStream ps = new PrintStream(file)) {
            ps.print(this);
        }
    }


    /**
     * Returns set of INI-file sections names
     * @return  Set of INI-file sections names
     */
    public Set<String> getSections() {
        return iniMap.keySet();
    }

    /**
     * Returns an properties object for given INI-file section
     * @param section   INI-file section name
     * @return          Properties object
     */
    public Properties getSectionProperties(String section) {
        return iniMap.get(section);
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(Map.Entry<String, Properties> entry: iniMap.entrySet()) {
            String section = entry.getKey();
            Properties properties = iniMap.get(section);
            result.append("[").append(section).append("]\r\n");
            for (String prop: properties.stringPropertyNames()) {
                result.append(prop).append("=");
                result.append(properties.getProperty(prop));
                result.append("\r\n");
            }
        }
        return result.toString();
    }

}
