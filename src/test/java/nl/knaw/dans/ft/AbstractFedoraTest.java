package nl.knaw.dans.ft;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.DescribeRepository;
import com.yourmediashelf.fedora.client.request.FedoraRequest;
import com.yourmediashelf.fedora.client.response.DescribeRepositoryResponse;

public abstract class AbstractFedoraTest
{
    
    public static final String FEDORA_VERSION = "3.8.0";
    public static final String CHECKSUM_PREFIX = "ftchcksm";
    
    @BeforeClass
    public static void connectionTest() throws Exception {
        FedoraCredentials credentials = 
                new FedoraCredentials("http://localhost:18080/fedora", "fedoraAdmin", "fedoraAdmin");
        FedoraClient fedora = new FedoraClient(credentials);
        FedoraRequest.setDefaultClient(fedora);
        
        DescribeRepositoryResponse response = new DescribeRepository().execute();
        String version = response.getRepositoryVersion();
        assertEquals(FEDORA_VERSION, version);
        System.out.println("Connection tested");
    }
    

}
