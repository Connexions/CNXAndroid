/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.utils;

import java.util.StringTokenizer;

import android.util.Log;

/**
 * Utility class for Files
 * @author Ed Woodward
 * Original code from http://www.artima.com/forums/flat.jsp?forum=1&thread=151666
 *
 */
public class FileUtil
{
    /**
     * creates displayable file size
     * @param fileLength - int file length in bytes
     * @return String - displayable files size such as 4.2 MB
     */
    public static String getFileSize(int fileLength)
    {
        //Log.d("FileUtil.getFileSize()", "fileLength: " + fileLength);
        //Log.d("FileUtil.getFileSize()", "fileLengthStr: " + fileLengthStr);
        int fileLengthDigitCount = String.valueOf(fileLength).length();
        double fileLengthLong = fileLength;
        //double decimalVal = 0.0;
        String howBig = "";
        double fileSizeKB = 0;

        if(fileLength > 0)
        {
            if(fileLengthDigitCount < 5)
            {
                fileSizeKB = Math.abs(fileLengthLong);
                howBig = "Byte(s)";
            }
            else if(fileLengthDigitCount >= 5 && fileLengthDigitCount <=6)
            {
                fileSizeKB = Math.abs((fileLengthLong/1024));
                howBig = "KB";
            }
            else if(fileLengthDigitCount >= 7 && fileLengthDigitCount <= 9)
            {
                fileSizeKB = Math.abs(fileLengthLong/(1024*1024));
                howBig = "MB";
            }
            else if(fileLengthDigitCount >9)
            {
                fileSizeKB = Math.abs((fileLengthLong/(1024*1024*1024)));
                decimalVal = fileLengthLong%(1024*1024*1024);
                howBig = "GB";
            }
        }
        return getRoundedValue(fileSizeKB) + " " + howBig;
     }

        /**
         * Rounds decimal value
         * @param decimalVal - the number to round
         * @return String
         */
        private static String getRoundedValue(double decimalVal)
        {
            long beforeDecimalValue = decimalTokenize(decimalVal,1);
            long afterDecimalValue = decimalTokenize(decimalVal,2);
            long dividerVal = divider(String.valueOf(afterDecimalValue).length()-1);
            String finalResult=String.valueOf(beforeDecimalValue)+"."+String.valueOf(afterDecimalValue/dividerVal) ;
    
            return finalResult;
        }

        /**
         * @param argLength
         * @return
         */
        private static long divider(long argLength)
        {
            long varDivider=1;
    
            for(int i=0;i<(argLength-1);i++)
            {
                varDivider=varDivider*10;
            }
    
            return varDivider;
        }

        /**
         * Returns value on either side of decimal point
         * @param decimalVal
         * @param position
         * @return long value on given side of the decimal
         */
        private static long decimalTokenize(double decimalVal,int position)
        {
            long returnDecimalVal=0;
            String strDecimalVal="";
    
            if(decimalVal >0)
                strDecimalVal = String.valueOf(decimalVal);
    
            if(strDecimalVal.length()>0)
            {
                StringTokenizer decimalToken = new StringTokenizer(strDecimalVal,".");
        
                if(position==1)
                {
                    returnDecimalVal = Long.parseLong(decimalToken.nextToken());
                }
                else if(position==2)
                {
                    decimalToken.nextToken();
                    returnDecimalVal = Long.parseLong(decimalToken.nextToken());
                }
            }
            return returnDecimalVal;
        }


}
