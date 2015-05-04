package nl.knaw.dans.ft.checksum;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.GetDatastream;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.response.AddDatastreamResponse;
import com.yourmediashelf.fedora.client.response.GetDatastreamResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;
import com.yourmediashelf.fedora.client.response.ModifyDatastreamResponse;

public class UpdateOldStreamsTest extends AbstractChecksumTest
{
    
    private String pid = CHECKSUM_PREFIX + ":" + "update";
    
    @Test
    public void unversionedUpdate() throws Exception {
        ingestOne(false);
        updateWithChecksum();
    }

    public void ingestOne(boolean versionable) throws Exception {
        System.out.println("purged: " + purgeExistingDOBs());
        
        IngestResponse r1 = new Ingest(pid)
            .label("An object to carry a non-versioned datastream")
            .execute();
        assertTrue(r1.getStatus() == 201);
        System.out.println("Ingested object " + pid);
        
        
        File file = new File("pom.xml");
        AddDatastreamResponse r2 = new AddDatastream(pid, "test")
            .controlGroup("M")
            .mimeType("application/octet-stream")
            .dsLabel("A non-versioned datastream")
            .versionable(versionable)
            .content(file)
            .execute();
        assertTrue(r2.getStatus() == 201);
        System.out.println("Added datastream, versionable=" + versionable);
    }
    
    public void updateWithChecksum() throws Exception {
        GetDatastreamResponse r1 = new GetDatastream(pid, "test").execute();
        assertEquals(r1.getDatastreamProfile().getDsVersionID(), "1.0");
        ModifyDatastreamResponse r2 = new ModifyDatastream(pid, "test").checksumType("SHA-1").execute();
        assertTrue(r2.getStatus() == 200);
        GetDatastreamResponse r3 = new GetDatastream(pid, "test").execute();
        assertEquals(r3.getDatastreamProfile().getDsVersionID(), "2.0");
    }
    

}
