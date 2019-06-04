package org.atlanmod;



import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RAPLMonitor {

    private static Map<String, File> probes;
    private static final Logger LOGGER = Logger.getLogger("RAPLMonitor");

    private long ujBefore;
    private long nsBefore;

    static {
        LOGGER.info("Initializing the software power meter");

        probes = new HashMap<>();

        File raplRoot = new File("/sys/class/powercap/");
        if (!raplRoot.exists()) {
            LOGGER.warning("No RAPL found! Is your Linux kernel > 2.6.31 and your CPU Intel ?");
            throw new IllegalArgumentException("No RAPL");
        }

        try {
            Files.walk(raplRoot.toPath(), 2)
                    .filter(f -> f.toFile().isDirectory())
                    .filter(f -> new File(f.toFile(), "energy_uj").exists())
                    .filter(f -> new File(f.toFile(), "name").exists())
                    .forEach(f -> {
                        try {
                             probes.put(IOUtils.toString(new FileInputStream(new File(f.toFile(), "name")), Charset.defaultCharset()),
                                    new File(f.toFile(), "energy_uj"));
                        } catch (IOException e) {
                            LOGGER.log(Level.WARNING, "Couldn't load the RAPL", e);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the proc RAPL to get the energ consumed at Core level
     *
     * @return a {@link Long} containing the energy consumed in microJoules
     * @throws IOException
     */
    public static Long getEnergy() {
        File file = probes.get("core\n");

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String value = bufferedReader.readLine();
            return Long.valueOf(value);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

}