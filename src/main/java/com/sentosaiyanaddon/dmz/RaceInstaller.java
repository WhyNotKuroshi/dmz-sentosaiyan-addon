package com.sentosaiyanaddon.dmz;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.sentosaiyanaddon.dmz.config.SentoConfig;

public final class RaceInstaller {

    private static final String RACE_NAME = "sentosaiyan";
    private static final String REVAMP_MOD_ID = "dmzrevamp";

    // Paths— separados por variante (base vs revamp)
    // Estrutura no jar:
    //   /racecontent/dragonminez/races/sentosaiyan/base/forms/*.json
    //   /racecontent/dragonminez/races/sentosaiyan/revamp/forms/*.json

    private static final String BASE_ROOT =
            "/racecontent/dragonminez/races/" + RACE_NAME + "/base/";
    private static final String REVAMP_ROOT =
            "/racecontent/dragonminez/races/" + RACE_NAME + "/revamp/";

    // Arquivos gerenciados pelo installer, todos tem variante base/revamp.
    private static final String[] MANAGED_FILES = {
            "forms/sentosaiyanoozaru.json",
            "forms/supersentosaiyan.json",
            "forms/legendaryforms.json",
            "character.json",
            "stats.json"
    };

    private static final String MARKER_FILE = ".sento_source";

    private RaceInstaller() {
    }

    // API publica
    /* Chamado no boot, instala arquivos que faltam.
     * Se a fonte (base vs revamp) mudou, forca reinstalacao completa, isso ai e pq o mod e compatível com o revamp
     *
     * Respeita RACE_INSTALLER_ENABLED: se o modpack author desligou nao toca em nada
     */
    public static void install() {
        if (!SentoConfig.RACE_INSTALLER_ENABLED.get()) {
            System.out.println("[SentoSaiyanAddon] Race installer disabled by config.");
            return;
        }

        Path racesDir = getRacesDir();
        String desired = desiredSource();

        if (sourceChanged(racesDir, desired)) {
            forceInstall();
            return;
        }

        // So instala arquivos que faltam (primeira instalacao, arquivo deletado, etc).
        String root = variantRoot(desired);
        for (String relative : MANAGED_FILES) {
            Path target = racesDir.resolve(relative);
            if (!Files.exists(target)) {
                copyFromJar(root + relative, target);
            }
        }
    }

    /* Reinstala TODOS os arquivos, sobrescrevendo os que ja existem.
     * So funciona quando e chamado por:
     *   - install() 
     *   - /dmzsento reset config 
     *
     * Ignora RACE_INSTALLER_ENABLED — se um admin rodou o comando e pq ele quer isso mesmo.*/
    public static void forceInstall() {
        Path racesDir = getRacesDir();
        String source = desiredSource();
        String root = variantRoot(source);

        for (String relative : MANAGED_FILES) {
            copyFromJar(root + relative, racesDir.resolve(relative));
        }

        writeMarker(racesDir, source);
    }

    public static String desiredSource() {
        return ModList.get().isLoaded(REVAMP_MOD_ID) ? "revamp" : "base";
    }

    // Herlpers 

    private static String variantRoot(String source) {
        return source.equals("revamp") ? REVAMP_ROOT : BASE_ROOT;
    }

    private static Path getRacesDir() {
        return FMLPaths.CONFIGDIR.get()
                .resolve("dragonminez")
                .resolve("races")
                .resolve(RACE_NAME);
    }

    private static boolean sourceChanged(Path racesDir, String desired) {
        Path marker = racesDir.resolve(MARKER_FILE);
        if (!Files.exists(marker))
            return true;
        try {
            String existing = Files.readString(marker, StandardCharsets.UTF_8).trim();
            return !existing.equals(desired);
        } catch (IOException e) {
            System.err.println("[SentoSaiyanAddon] Failed to read marker: " + e.getMessage());
            return true;
        }
    }

    private static void writeMarker(Path racesDir, String source) {
        try {
            Files.createDirectories(racesDir);
            Files.writeString(racesDir.resolve(MARKER_FILE), source, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[SentoSaiyanAddon] Failed to write marker: " + e.getMessage());
        }
    }

    private static void copyFromJar(String resourcePath, Path target) {
        try {
            Files.createDirectories(target.getParent());

            try (InputStream in = RaceInstaller.class.getResourceAsStream(resourcePath)) {
                if (in == null) {
                    System.err.println("[SentoSaiyanAddon] Asset not found: " + resourcePath);
                    return;
                }
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[SentoSaiyanAddon] Installed: " + target);
            }
        } catch (IOException e) {
            System.err.println("[SentoSaiyanAddon] Failed to install " + resourcePath
                    + ": " + e.getMessage());
        }
    }
}