package vn.ngh.epubreader.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {
    private static final int BUFFER_SIZE = 1024 * 2;

    /**
     * Unzip archive file all
     *
     * @param zipFile
     * @param targetDir
     * @throws IOException
     */
    public static final void unzipAll(File zipFile, File targetDir) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zentry = null;

        // if exists remove
        if (targetDir.exists()) {
            FileUtils.deleteDirectory(targetDir);
            targetDir.mkdirs();
        } else {
            targetDir.mkdirs();
        }

        // unzip all entries
        while ((zentry = zis.getNextEntry()) != null) {
            String fileNameToUnzip = zentry.getName();
            File targetFile = new File(targetDir, fileNameToUnzip);

            // if directory
            if (zentry.isDirectory()) {
                (new File(targetFile.getAbsolutePath())).mkdirs();
            } else {
                // make parent dir
                (new File(targetFile.getParent())).mkdirs();

                unzipEntry(zis, targetFile);
            }
        }
        zis.close();
    }

    /**
     * Unzip one entry
     *
     * @param zis
     * @param targetFile
     * @return
     * @throws IOException
     */
    @SuppressWarnings("resource")
    private static final File unzipEntry(ZipInputStream zis, File targetFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(targetFile);

        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        while ((len = zis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }

        return targetFile;
    }

}
