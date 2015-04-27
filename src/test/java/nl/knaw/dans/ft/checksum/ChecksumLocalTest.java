package nl.knaw.dans.ft.checksum;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.response.FedoraResponse;

public class ChecksumLocalTest extends AbstractChecksumTest
{
    
    //GetDatastreamDissemination for files ~ 27000-30000
    // so most ingest time is network overhead.
    @Test
    public void verifyChecksumsLocal() throws Exception {
        purgeExistingDOBs();
        ingestDOBs();
        
        long time = 0L;
        File[] testFiles = getTestFiles();
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

}
