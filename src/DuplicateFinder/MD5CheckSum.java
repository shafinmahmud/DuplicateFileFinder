package DuplicateFinder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
     * This class generates the MD5 Hash value.
     * @throws Exception
     */
public class MD5CheckSum
{
    /**
     * This method calculate the MD5 checksum.
     * @param directoryName This receives the file directory.
     * @return checksum This is the CRC32 checksum for the file.
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.FileNotFoundException
     */
    public static String hashing(String directoryName) throws NoSuchAlgorithmException,
            FileNotFoundException, IOException
    {

        MessageDigest md = MessageDigest.getInstance("MD5");

        FileInputStream is = new FileInputStream(directoryName);

        byte[] buffer = new byte[8192];
        int read = 0;

        while ((read = is.read(buffer)) > 0)
        {
            md.update(buffer, 0, read);
        }

        byte[] md5 = md.digest();
        BigInteger bi = new BigInteger(1, md5);

        return bi.toString(16);

    }
}
