package nl.knaw.dans.ft.checksum;

import org.apache.commons.io.IOUtils;

import com.yourmediashelf.fedora.client.request.GetDatastream;
import com.yourmediashelf.fedora.client.response.GetDatastreamResponse;

import nl.knaw.dans.ft.AbstractFedoraTest;

public class APItest extends AbstractFedoraTest
{

    public static void main(String[] args) throws Exception
    {
        connectionTest(); // set the client
        APItest api = new APItest();
        api.getDatastream();

    }
    
    public void getDatastream() throws Exception {
        GetDatastreamResponse r = new GetDatastream("ftchcksm:500Kb.1000", "test").execute();
        
        // contains the datastream information as xml, not the content
        //String profile = IOUtils.toString(r.getEntityInputStream());
        //System.out.println(profile);
        
        System.out.println(r.getStatus());
        System.out.println(r.getType());
        System.out.println(r.isChecksumValid());
        System.out.println(r.getDatastreamProfile().getDsChecksum());
        System.out.println(r.getDatastreamProfile().getDsChecksumType());
    }

}
