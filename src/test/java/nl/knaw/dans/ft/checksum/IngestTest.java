package nl.knaw.dans.ft.checksum;

import org.junit.Test;

public class IngestTest extends AbstractChecksumTest
{
    // Generate test files first with scripts/generate_files.sh
    // This will create 28files, total size 1Gb
    
    
    @Test
    public void tenIngestsTests() throws Exception {
        calculateTestFilesLength();
        
        for (int i = 1; i <= 10; i++) {
            ingest();
        }
    }
    
    private void ingest() throws Exception {
        purgeExistingDOBs();
        Thread.sleep(60000); // give fedora time to recover
        purgeExistingDOBs();
        Thread.sleep(10000); // give fedora time to recover
        System.out.println(ingestDOBs());
    }
    

}
