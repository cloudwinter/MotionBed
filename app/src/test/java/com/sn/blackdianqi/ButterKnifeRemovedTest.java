package com.sn.blackdianqi;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Guards the ButterKnife -> DataBinding migration.
 */
public class ButterKnifeRemovedTest {

    @Test
    public void javaSourcesMustNotReferenceButterKnife() throws IOException {
        File javaDir = resolve("src/main/java");
        List<String> hits = new ArrayList<>();
        collectHits(javaDir, ".java", hits);
        if (!hits.isEmpty()) {
            fail("ButterKnife still referenced:\n" + String.join("\n", hits));
        }
    }

    @Test
    public void gradleMustEnableDataBindingAndDropButterKnife() throws IOException {
        File gradle = resolve("build.gradle");
        String content = new String(Files.readAllBytes(gradle.toPath()), StandardCharsets.UTF_8);
        String lower = content.toLowerCase(Locale.US);
        if (lower.contains("butterknife")) {
            fail("app/build.gradle still contains butterknife");
        }
        assertTrue("dataBinding must be enabled in app/build.gradle", content.contains("dataBinding"));
    }

    private static void collectHits(File dir, String suffix, List<String> hits) throws IOException {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (child.isDirectory()) {
                collectHits(child, suffix, hits);
            } else if (child.getName().endsWith(suffix)) {
                List<String> lines = Files.readAllLines(child.toPath(), StandardCharsets.UTF_8);
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).toLowerCase(Locale.US).contains("butterknife")) {
                        hits.add(child.getPath() + ":" + (i + 1) + ":" + lines.get(i).trim());
                    }
                }
            }
        }
    }

    private static File resolve(String relativeToApp) {
        File[] candidates = new File[]{
                new File(relativeToApp),
                new File("app", relativeToApp)
        };
        for (File candidate : candidates) {
            if (candidate.exists()) {
                return candidate;
            }
        }
        fail("Cannot find " + relativeToApp + " from cwd=" + new File(".").getAbsolutePath());
        return null;
    }
}
