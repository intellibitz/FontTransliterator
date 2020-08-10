package sted.io

import java.awt.Component
import java.io.*
import java.util.logging.Logger
import javax.swing.JFileChooser
import javax.swing.JOptionPane

object FileHelper {
    private val logger = Logger.getLogger("sted.io.FileHelper")

    @JvmStatic
    fun openFont(parent: Component?): File? {
        return openFile("Please select Font location:", "ttf", "Fonts", parent)
    }

    @JvmStatic
    fun alertAndOpenFont(alert: String?, parent: Component?): File? {
        JOptionPane.showMessageDialog(
            parent, alert, "Missing Resource",
            JOptionPane.WARNING_MESSAGE
        )
        return openFont(parent)
    }

    @JvmStatic
    fun openFile(
        title: String?, extension: String?,
        description: String?,
        parent: Component?
    ): File? {
        val jFileChooser = JFileChooser(System.getProperty("user.dir"))
        jFileChooser.dialogTitle = title
        val fileFilterHelper = FileFilterHelper(extension, description)
        jFileChooser.fileFilter = fileFilterHelper
        val result = jFileChooser.showOpenDialog(parent)
        return if (result == JFileChooser.APPROVE_OPTION) {
            jFileChooser.selectedFile
        } else null
    }

    @JvmStatic
    fun suffixFileSeparator(path: String): String {
        if (path.endsWith(File.separator)) {
            return path
        }
        return path + File.separator
    }

    @JvmStatic
    @Throws(IOException::class)
    fun fileCopy(source: File?, dest: String?) {
        val newFile = File(dest)
        val buffer = ByteArray(4096)
        val inputStream = FileInputStream(source)
        val outputStream = FileOutputStream(newFile)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            outputStream.write(buffer, 0, len)
        }
        inputStream.close()
        outputStream.close()
    }

    /**
     * @param file the file to read
     * @return InputStream the input stread from the file
     * @throws java.io.FileNotFoundException if file cannot be read, not found
     */
    @JvmStatic
    @Throws(FileNotFoundException::class)
    fun getInputStream(file: File): InputStream {
        logger.entering(Resources::class.java.name, "getInputStream", file)
        var inputStream: InputStream?
        inputStream = if (file.isAbsolute) {
            logger.finest(
                "file is absolute.. using ClassLoader.getSystemResourceAsStream " +
                        file.absolutePath
            )
            ClassLoader
                .getSystemResourceAsStream(file.absolutePath)
        } else {
            val resource = file.path.replace('\\', '/')
            logger.finest(
                "file is relative.. using Resources.class.getClass().getResourceAsStream with " +
                        resource
            )
            ClassLoader.getSystemResourceAsStream(resource)
        }
        if (inputStream == null) {
            inputStream = FileInputStream(file)
        }
        logger.exiting(
            Resources::class.java.name, "getInputStream",
            inputStream
        )
        return inputStream
    }

    @JvmStatic
    fun getSampleFontMapPaths(path: String): Array<String>? {
        val dirFile = File(path)
        if (dirFile.isDirectory) {
            val files = dirFile.list { _, name -> name.endsWith("xml") }
            if (!files.isNullOrEmpty()) {
                for (i in files.indices) {
                    if (!files[i].contains(path)) {
                        files[i] = path + files[i]
                    }
                }
            }
            return files
        }
        return null
    }
}