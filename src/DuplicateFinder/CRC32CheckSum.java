
package DuplicateFinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
     * This class generates the CRC32 Hash value.
     */
public class CRC32CheckSum
{
    /**
     * This method calculate the CRC32 checksum.
     * @param fileName This receives the file directory.
     * @return checksum This is the CRC32 checksum for the file.
     */
    public static long doChecksum(String fileName)
    {
        long checksum = 0;
        try
        {
            CheckedInputStream cis = null;
            long fileSize = 0;
            try
            {
                // Computer CRC32 checksum
                cis = new CheckedInputStream(
                        new FileInputStream(fileName), new CRC32());

                fileSize = new File(fileName).length();

            } catch (FileNotFoundException e)
            {
                System.exit(1);
            }

            byte[] buf = new byte[128];
            while (cis.read(buf) >= 0)
            {
            }

            checksum = cis.getChecksum().getValue();           
            

        } catch (IOException e)
        {
            System.exit(1);
        }
       return checksum;
    }    
}
