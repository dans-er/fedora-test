package nl.knaw.dans.ft.checksum;

import static org.junit.Assert.*;

import java.io.File;

import nl.knaw.dans.ft.AbstractFedoraTest;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.FindObjects;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.PurgeObject;
import com.yourmediashelf.fedora.client.response.AddDatastreamResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;

public class IngestTest extends AbstractFedoraTest
{
    
    @Test
    public void ingestTest() throws Exception {
        purgeExistingDOBs();
        calculateTestFilesLength();
        ingestDOBs();
    }
    
    private long ingestDOBs() throws Exception {
        File[] testFiles = new File("test-files").listFiles();
        long time = 0L;
        // create objects
        for (File file : testFiles) {
            String pid = CHECKSUM_PREFIX + ":" + file.getName();
            //
            long start = System.currentTimeMillis();
            IngestResponse ingResponse = new Ingest(pid)
                    .label("obj-" + file.getName())
                    .execute();
            AddDatastreamResponse adsResponse = new AddDatastream(pid, "test")
                    .controlGroup("M")
                    .checksumType("SHA-1")
                    .mimeType("application/octet-stream")
                    .content(file)
                    .execute();
            long duration = System.currentTimeMillis() - start;
            //
            assertEquals(201, ingResponse.getStatus());
            assertEquals(201, adsResponse.getStatus());
            //assertFalse(adsResponse.isChecksumValid());
            //assertTrue(adsResponse.isChecksumValid());
            time += duration;
            
            //System.out.println("Ingest: size=" + file.length()/1000000L + " duration=" + duration);
        }
        System.out.println("Total ingest time: " + time);
        return time;
    }
    
    private long calculateTestFilesLength() {
        File[] testFiles = new File("test-files").listFiles();
        long size = 0L;
        for (File file : testFiles) {
            size += file.length();
        }
        System.out.println(testFiles.length + " test files: " + FileUtils.byteCountToDisplaySize(size));
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
        System.out.println("Purged " + response.getPids().size() + " '" + CHECKSUM_PREFIX + "' test objects.");
    }
    

}
