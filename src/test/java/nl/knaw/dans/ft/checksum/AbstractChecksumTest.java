package nl.knaw.dans.ft.checksum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import nl.knaw.dans.ft.AbstractFedoraTest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.FindObjects;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.request.PurgeObject;
import com.yourmediashelf.fedora.client.response.AddDatastreamResponse;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;
import com.yourmediashelf.fedora.client.response.ModifyDatastreamResponse;

public abstract class AbstractChecksumTest extends AbstractFedoraTest
{
    
    public static final String TEST_FILES_DIR = 
            //"test-files";
            //"test-files2";
            //"test-files3";
            //"test-files4";
            "test-files6";
    
    protected File[] getTestFiles() {
        return new File(TEST_FILES_DIR).listFiles();
    }
    
    protected long calculateTestFilesLength() {
        File[] testFiles = getTestFiles();
        long size = 0L;
        for (File file : testFiles) {
            size += file.length();
        }
        System.out.println(testFiles.length + " test files: " + FileUtils.byteCountToDisplaySize(size));
        return size;
    }
    
    protected List<String> findObjects() throws Exception {
        FindObjectsResponse response = new FindObjects()
            .query("pid~" + CHECKSUM_PREFIX + "*")
            .pid()
            .maxResults(100)
            .execute();
        return response.getPids();
    }
    
    protected int purgeExistingDOBs() throws Exception {
        int count = 0;
        List<String> pids;
        while (!(pids = findObjects()).isEmpty()) {
            for (String pid : pids) {
                try
                {
                    new PurgeObject(pid).execute();
                    count += 1;
                }
                catch (Exception e)
                {
                    // Fedora at this speed of creating and purging objects is not synchronized.
                }
            }
            Thread.sleep(5000);
        }
        return count;
    }
    
    protected long ingestDOBs() throws Exception {
        File[] testFiles = getTestFiles();
        long time = 0L;
        int count = 0;
        // create objects
        for (File file : testFiles) {
            String pid = CHECKSUM_PREFIX + ":" + file.getName();
//            FileInputStream fis = new FileInputStream(file);
//            String checksum = DigestUtils.sha1Hex(fis);
//            fis.close();

            //
            count += 1;
            if (count % 50 == 0) Thread.sleep(5000);
            long start = System.currentTimeMillis();
            IngestResponse ingResponse = new Ingest(pid)
                    .label("obj-" + file.getName())
                    .execute();
            AddDatastreamResponse adsResponse = new AddDatastream(pid, "test")
                    .controlGroup("M")
                    //.checksumType("SHA-1")
                    //.checksum(checksum) // if not valid: org.fcrepo.server.errors.ValidationException: Checksum Mismatch
                    .mimeType("application/octet-stream")
                    .content(file)
                    .execute();
            long duration = System.currentTimeMillis() - start;
            //
            assertEquals(201, ingResponse.getStatus());
            assertEquals(201, adsResponse.getStatus());
            assertFalse(adsResponse.isChecksumValid()); // always false
            time += duration;
            // only for updateTest
            new ModifyDatastream(pid, "test").versionable(false).execute();
        }
        return time;
    }

}
