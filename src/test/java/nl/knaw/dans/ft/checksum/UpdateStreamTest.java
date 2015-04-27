package nl.knaw.dans.ft.checksum;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.GetDatastream;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.response.GetDatastreamResponse;
import com.yourmediashelf.fedora.client.response.ModifyDatastreamResponse;

public class UpdateStreamTest extends AbstractChecksumTest
{
    
    @Test
    public void tenUpdateTest() throws Exception {
        purgeExistingDOBs();
        ingestDOBs();
        calculateTestFilesLength();
        Thread.sleep(20000);
        for (int i = 1; i <= 10; i++) {
            System.out.println(updateTest());
        }
    }
    
    private long updateTest() throws Exception {
        long time = 0L;
        File[] testFiles = getTestFiles();
        for (File file : testFiles) {
            String pid = CHECKSUM_PREFIX + ":" + file.getName();
            String versionId1 = getVersion(pid);
            //
            long start = System.currentTimeMillis();
            //new ModifyDatastream(pid, "test").versionable(false).execute();
            ModifyDatastreamResponse r2 = new ModifyDatastream(pid, "test").checksumType("SHA-1").execute();
            //new ModifyDatastream(pid, "test").versionable(true).execute();
            long duration = System.currentTimeMillis() - start;
            time += duration;
            //
            assertTrue(r2.getStatus() == 200);
            String versionId2 = getVersion(pid);
            assertNotEquals(versionId1, versionId2);
            ModifyDatastreamResponse r3 = new ModifyDatastream(pid, "test").checksumType("DISABLED").execute();
            assertTrue(r3.getStatus() == 200);
        }
        return time;
    }

    public String getVersion(String pid) throws FedoraClientException
    {
        GetDatastreamResponse response = new GetDatastream(pid, "test")
            .execute();
        assertTrue(response.getDatastreamProfile().getDsVersionable().equalsIgnoreCase("false"));
        return response.getDatastreamProfile().getDsVersionID();
    }

}
