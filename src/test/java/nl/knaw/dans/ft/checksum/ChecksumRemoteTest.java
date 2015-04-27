package nl.knaw.dans.ft.checksum;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.yourmediashelf.fedora.client.request.GetDatastream;
import com.yourmediashelf.fedora.client.response.GetDatastreamResponse;

public class ChecksumRemoteTest extends AbstractChecksumTest
{
    
    @Test
    public void checksumValidTest() throws Exception {
        calculateTestFilesLength();
        //purgeExistingDOBs();
        //ingestDOBs();
        
        for (int i = 1; i <= 10; i++) {
            System.out.println(verifyChecksums());
        }
    }
    
    private long verifyChecksums() throws Exception {
        long time = 0L;
        File[] testFiles = getTestFiles();
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

}
