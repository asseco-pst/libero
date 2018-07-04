package com.exictos.devops.helpers

class FileUtils {

    /**
     * Copies originFile to destinationFolder
     *
     * @param originFile
     * @param destinationFolder
     * @return the destination file path
     */
    static String copyFile(String originFile, String destinationFolder)
    {
        def src = new File(originFile)
        def dst = new File("${destinationFolder}/${src.getName()}")
        dst << src.bytes
        return dst.getAbsolutePath()
    }

    /**
     * Renames file at filePath to newName
     *
     * @param filePath
     * @param newName
     * @return file path to renamed file
     */
    static String renameFile(String filePath, String newName)
    {
        def file = new File(filePath)
        file.renameTo("${file.getParent()}/${newName}")
        return "${file.getParent()}${File.separator}${newName}"
    }

    /**
     * Unzip a zip file at filePath on the same directory
     *
     * @param filePath
     * @return the extracted folder
     */
    static String unzip(String filePath)
    {
        def file = new File(filePath)
        file.unzip(new File(file.getParent()))
        file.getAbsolutePath().replace(".zip", "")
    }

    static boolean removeFolder(String folder)
    {
        def file = new File(folder)
        file.deleteDir()
    }

}
