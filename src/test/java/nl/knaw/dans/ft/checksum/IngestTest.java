package nl.knaw.dans.ft.checksum;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;

import nl.knaw.dans.ft.AbstractFedoraTest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.FindObjects;
import com.yourmediashelf.fedora.client.request.GetDatastream;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.PurgeObject;
import com.yourmediashelf.fedora.client.response.AddDatastreamResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;
import com.yourmediashelf.fedora.client.response.GetDatastreamResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;

public class IngestTest extends AbstractFedoraTest
{
    // Generate test files first with scripts/generate_files.sh
    // This will create 28files, total 1Gb
    
    @Test
    public void checksumValidTest() throws Exception {
        //purgeExistingDOBs();
        //ingestDOBs();
        for (int i = 1; i <= 10; i++) {
            System.out.println(verifyChecksums());
        }
    }
    
    private long verifyChecksums() throws Exception {
        long time = 0L;
        File[] testFiles = new File("test-files").listFiles();
        for (File file : testFiles) {
            String pid = CHECKSUM_PREFIX + ":" + file.getName();
            //
            long start = System.currentTimeMillis();
            GetDatastreamResponse response = new GetDatastream(pid, "test")
                    .validateChecksum(true)
                    .execute();
            
            assertTrue(response.isChecksumValid());           
            long duration = System.currentTimeMillis() - start;
            //
            time += duration;
        }
        return time;
    }
    
    //GetDatastreamDissemination for files ~ 27000-30000
    // so most ingest time is network overhead.
    //@Test
    public void verifyChecksumsLocal() throws Exception {
        long time = 0L;
        File[] testFiles = new File("test-files").listFiles();
        for (File file : testFiles) {
            String pid = CHECKSUM_PREFIX + ":" + file.getName();
            
            FileInputStream fis = new FileInputStream(file);
            String checksum1 = DigestUtils.sha1Hex(fis);
            fis.close();
            //
            long start = System.currentTimeMillis();
            FedoraResponse response = new GetDatastreamDissemination(pid, "test")
                    .execute();
            String checksum2 = DigestUtils.sha1Hex(response.getEntityInputStream());
            response.close();
            assertEquals(checksum1, checksum2);
            long duration = System.currentTimeMillis() - start;
            time += duration;
        }
        System.out.println("Total validationt time: " + time);
    }
    
    //@Test
    public void tenIngestsTests() throws Exception {
        for (int i = 1; i <= 10; i++) {
            ingestTest();
        }
    }
    
    //@Test
    public void ingestTest() throws Exception {
        purgeExistingDOBs();
        calculateTestFilesLength();
        System.out.println(ingestDOBs());
    }
    
    private long ingestDOBs() throws Exception {
        File[] testFiles = new File("test-files").listFiles();
        long time = 0L;
        // create objects
        for (File file : testFiles) {
            String pid = CHECKSUM_PREFIX + ":" + file.getName();
            FileInputStream fis = new FileInputStream(file);
            String checksum = DigestUtils.sha1Hex(fis);
            fis.close();

            //
            long start = System.currentTimeMillis();
            IngestResponse ingResponse = new Ingest(pid)
                    .label("obj-" + file.getName())
                    .execute();
            AddDatastreamResponse adsResponse = new AddDatastream(pid, "test")
                    .controlGroup("M")
                    .checksumType("SHA-1")
                    .checksum(checksum) // if not valid: org.fcrepo.server.errors.ValidationException: Checksum Mismatch
                    .mimeType("application/octet-stream")
                    .content(file)
                    .execute();
            long duration = System.currentTimeMillis() - start;
            //
            assertEquals(201, ingResponse.getStatus());
            assertEquals(201, adsResponse.getStatus());
            assertFalse(adsResponse.isChecksumValid()); // always false
            time += duration;
            
            //System.out.println("Ingest: size=" + file.length()/1000000L + " duration=" + duration);
        }
        //System.out.println("Total ingest time: " + time);
        return time;
    }
    
    private long calculateTestFilesLength() {
        File[] testFiles = new File("test-files").listFiles();
        long size = 0L;
        for (File file : testFiles) {
            size += file.length();
        }
        //System.out.println(testFiles.length + " test files: " + FileUtils.byteCountToDisplaySize(size));
        return size;
    }
    
    private void purgeExistingDOBs() throws Exception {
        FindObjectsResponse response = new FindObjects()
                .query("pid~" + CHECKSUM_PREFIX + "*")
                .pid()
                .maxResults(100)
                .execute();
        assertEquals(200, response.getStatus());
        for (String pid : response.getPids()) {
            FedoraResponse pr = new PurgeObject(pid).execute();
            assertEquals(200, pr.getStatus());
        }
        //System.out.println("Purged " + response.getPids().size() + " '" + CHECKSUM_PREFIX + "' test objects.");
    }
    

}
