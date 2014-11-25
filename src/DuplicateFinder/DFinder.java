package DuplicateFinder;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class receives directory path and algorithm choice. It generates list of
 * all files of the directory and gathers required information and search the
 * duplicate files.
 */
public class DFinder extends Thread
{

    final int MAX = 100000;
    String[][] fpaths = new String[MAX][3];
    String[] fhashValues = new String[MAX];
    long[] fsizes = new long[MAX];
    long[] crc32hashes = new long[MAX];
    int[][] duplicounter = new int[MAX][100];
    java.util.List<int[]> dcList = new ArrayList<>();
    boolean[] dupliflag = new boolean[MAX];

    int iter = 0;
    int choice = 0;
    public String dirName;
    public UserInterface fileChoser;

    /**
     * This is the constructor method for the class DFinder. It initializes some
     * variable and revives UserInterface class Object reference.
     *
     * @param f This receives the UserInterface Object reference.
     * @param dirname This receives the directory path.
     * @param algoChoice This receives the value that selects the algorithm to
     * apply.
     */
    public DFinder(UserInterface f, String dirname, int algoChoice)
    {
        fileChoser = f;
        dirName = dirname;
        choice = algoChoice;
    }

    /**
     * This runs the thread which is called in the buttonClicked method in
     * UserInterface class.
     */
    @Override
    public void run()
    {

        int filecount;
        filecount = listAll(this.dirName);
        try
        {
            showingInfo(filecount);
        } catch (Exception ex)
        {
            Logger.getLogger(DFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method traverse the all files from selected directory recursively
     * and collect informations.
     *
     * @param dirName This receives the directory name that is chosen by the
     * user.
     * @return iteration value changed. At the end of the recursive calling, this is
     * the total number of files in the directory.
     */
    public int listAll(String dirName)
    {

        File directory = new File(dirName);
        String fp, fname, ext;

        //get all the files from a directory
        File[] fList = directory.listFiles();

        for (File file : fList)
        {
            if (file.isFile())
            {
                fp = file.getAbsolutePath();
                fname = file.getName();
                
                //for getting file extension
                ext = "";

                int i = fp.lastIndexOf('.');
                int p = Math.max(fp.lastIndexOf('/'), fp.lastIndexOf('\\'));

                if (i > p)
                {
                    ext = fp.substring(i + 1);
                }
                if(true)
                {
                    fpaths[iter][0] = fp;
                    fpaths[iter][1] = fname;
                    
                    fpaths[iter][2] = ext;
                    fsizes[iter] = file.length();
                    
                    //shows the current file scanning progress.
                    fileChoser.currentStatus.setText("currently Scanning path: " + fpaths[iter][0]
                            + "\n" + "Scanned: " + iter + " files");
                    
                    iter++;
                }

            } else if (file.isDirectory())
            {
                listAll(file.getAbsolutePath());
            }
        }
        return iter;
    }

    /**
     * This method searches the duplicate files matching the file sizes.
     *
     * @param arrData This receives the file sizes information.
     * @param c This receives the total number of the file.
     * @throws Exception
     */
    public void dupliSearch(long[] arrData, int c) throws Exception
    {
        int col;

        for (int i = 0; i < c; i++)
        {

            col = 1;
            for (int j = i + 1; j < c; j++)
            {
                if (fsizes[i] == fsizes[j] && dupliflag[j] == false)
                {
                    {
                        dupliflag[i] = true;
                        dupliflag[j] = true;

                        if (duplicounter[i][0] == 0)
                        {
                            duplicounter[i][0]++;
                            duplicounter[i][col++] = i;
                        }
                        fileChoser.currentStatus.setText("searching duplicate for : " + fpaths[i][1]);
                        duplicounter[i][0]++;
                        duplicounter[i][col++] = j;
                    }
                }
            }
            if (duplicounter[i][0] >= 1)
            {
                dcList.add(duplicounter[i]);
            }
        }
    }

    /**
     * This method searches the duplicate files matching the file sizes first
     * and find duplicates by verifying their MD5 Hash values .
     *
     * @param arrData This receives the file sizes information.
     * @param c This receives the total number of the file.
     * @throws Exception
     */
    public void dupliSearchMD5(long[] arrData, int c) throws Exception
    {
        int col;

        for (int i = 0; i < c; i++)
        {
            //shows the current progress.
            fileChoser.currentStatus.setText("Files left to compare: " + (c - i)
                    + "\n" + "searching duplicate for : " + fpaths[i][1]);

            col = 1;
            for (int j = i + 1; j < c; j++)
            {
                if (fsizes[i] == fsizes[j] && dupliflag[j] == false)
                {
                    //shows the current progress.
                    fileChoser.currentStatus.setText("Files left to compare : " + (c - i)
                            + "\n" + "comparing MD5CheckSUM for the followings: " + "\n" + fpaths[i][0]
                            + "     with" + " " + fpaths[j][0]);
                    //checks the MD5 Hash values.
                    if (MD5CheckSum.hashing(fpaths[i][0]).equals(MD5CheckSum.hashing(fpaths[j][0])))
                    {
                        dupliflag[i] = true;
                        dupliflag[j] = true;

                        if (duplicounter[i][0] == 0)
                        {
                            duplicounter[i][0]++;
                            duplicounter[i][col++] = i;
                        }
                        duplicounter[i][0]++;
                        duplicounter[i][col++] = j;
                    }
                }
            }
            if (duplicounter[i][0] >= 1)
            {
                //making a ArrayList of the duplicate files' paths.
                dcList.add(duplicounter[i]);
            }
        }
    }

    /**
     * This method searches the duplicate files matching the file sizes first
     * and find duplicates by verifying their CRC32 Hash values .
     *
     * @param arrData This receives the file sizes information.
     * @param c This receives the total number of the file.
     * @throws Exception
     */
    public void dupliSearchCRC32(long[] arrData, int c) throws Exception
    {
        int col;

        for (int i = 0; i < c; i++)
        {
            //shows the current progress.
            fileChoser.currentStatus.setText("Files left to compare: " + (c - i)
                    + "\n" + "searching duplicate for : " + fpaths[i][1]);
            col = 1;
            for (int j = i + 1; j < c; j++)
            {
                if (fsizes[i] == fsizes[j] && dupliflag[j] == false)
                {
                    //shows the current progress.
                    fileChoser.currentStatus.setText("Files left to compare : " + (c - i) + "\n"
                            + "\n" + "comparing CRC32CheckSUM for the followings: " + "\n" + fpaths[i][0]
                            + "     with" + " " + fpaths[j][0]);
                    //checks the CRC32 Hash values.
                    if (CRC32CheckSum.doChecksum(fpaths[i][0]) == CRC32CheckSum.doChecksum(fpaths[j][0]))
                    {
                        dupliflag[i] = true;
                        dupliflag[j] = true;

                        if (duplicounter[i][0] == 0)
                        {
                            duplicounter[i][0]++;
                            duplicounter[i][col++] = i;
                        }
                        duplicounter[i][0]++;
                        duplicounter[i][col++] = j;
                    }
                }
            }
            if (duplicounter[i][0] >= 1)
            {
                //making a ArrayList of the duplicate files' paths.
                dcList.add(duplicounter[i]);
            }
        }
    }

    /**
     * This method calls the chosen method for searching duplicates and shows
     * the output in the window.
     *
     * @param count This receives the total number of files.
     * @throws Exception
     */
    public void showingInfo(int count) throws Exception
    {
        int num, n, incident, totalDfiles, dPathNum;
        String dFilePath, dFileExt;
        String output = "";
        long dFileSZ;
        
        fileChoser.duplicatesShow.setText(output);//this line prevent appending output with out for previous directory.
        
        if (count == 0)
        {
            fileChoser.currentStatus.setText("Directry Empty" + "\n");
            fileChoser.duplicatesShow.setText("NO FILES HERE !!");
        } 
       else
        {
            //chose the algorithm for searching duplicates.
            if (choice == 1)
            {
                dupliSearch(fsizes, count);
            } else if (choice == 2)
            {
                dupliSearchMD5(fsizes, count);
            } else if (choice == 3)
            {
                dupliSearchCRC32(fsizes, count);
            }

            incident = dcList.size();
            totalDfiles = 0;

            //updates current progress.
            fileChoser.currentStatus.setText("Genarating Duplicate list ... ");

            //traverse the ArrayList and shows the output.
            for (int k = 0; k < incident; k++)
            {
                num = dcList.get(k)[0];
                totalDfiles += (num - 1);
                if (num >= 1)
                {
                    output = "\nfollwing " + num + " instances are equal :" + "\n";
                    fileChoser.duplicatesShow.append(output);

                    for (n = 1; n <= num; n++)
                    {
                        dPathNum = dcList.get(k)[n];
                        dFilePath = fpaths[dPathNum][0];
                        dFileExt = fpaths[dPathNum][2];
                        dFileSZ = fsizes[dPathNum];
                        output = "file type : ." + dFileExt + "  file size: " + dFileSZ + "bytes " + "  " + dFilePath + "\n";

                        fileChoser.duplicatesShow.append(output);
                    }
                    output = "\n";
                }
            }
            if (totalDfiles == 0)
            {
                fileChoser.currentStatus.setText("NO duplicates found" + "\n");
            } else
            {
                fileChoser.currentStatus.setText("Total " + totalDfiles + " duplicates of " + incident
                        + " files FOUND !!!" + "\n");
            }
        }

    }
}
