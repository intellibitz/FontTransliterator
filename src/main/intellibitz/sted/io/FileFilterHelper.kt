package sted.io

import java.io.File
import java.util.*
import javax.swing.filechooser.FileFilter

/**
 * A convenience implementation of FileFilter that filters out all files except
 * for those type extensions that it knows about.
 * Extensions are of the type ".foo", which is typically found on Windows and
 * Unix boxes, but not on Macinthosh. Case is ignored.
 * Example - create a new filter that filerts out all files but gif and jpg
 * image files:
 * JFileChooser chooser = new JFileChooser(); FileFilterHelper filter = new
 * FileFilterHelper( new String{"gif", "jpg"}, "JPEG & GIF Images")
 * chooser.addChoosableFileFilter(filter); chooser.showOpenDialog(this);
 */
class FileFilterHelper() : FileFilter() {
    private var filters: MutableMap<String?, FileFilterHelper?>?
    private var description: String? = null
    private var fullDescription: String? = null

    /**
     * Creates a file filter that accepts the given file type. Example: new
     * FileFilterHelper("jpg", "JPEG Image Images");
     * Note that the "." before the extension is not needed. If provided, it
     * will be ignored.
     *
     * @see .addExtension
     */
    constructor(extension: String?, description: String?) : this() {
        extension?.let { addExtension(it) }
        description?.let { setDescription(it) }
    }

    /**
     * Return true if this file should be shown in the directory pane, false if
     * it shouldn't.
     * Files that begin with "." are ignored.
     *
     * @see .getExtension
     *
     * @see javax.swing.filechooser.FileFilter.accept
     */
    override fun accept(f: File): Boolean {
        if (f.isDirectory) {
            return true
        }
        val extension = getExtension(f)
        return extension != null && filters!![getExtension(f)] != null
    }

    /**
     * Adds a filetype "dot" extension to filter against.
     * For example: the following code will create a filter that filters out all
     * files except those that end in ".jpg" and ".tif":
     *
     *
     * FileFilterHelper filter = new FileFilterHelper();
     * filter.addExtension("jpg"); filter.addExtension("tif");
     *
     *
     * Note that the "." before the extension is not needed and will be
     * ignored.
     */
    private fun addExtension(extension: String) {
        if (filters == null) {
            filters = Hashtable(5)
        }
        filters!![extension.toLowerCase()] = this
        fullDescription = null
    }

    /**
     * Returns the human readable description of this filter. For example: "JPEG
     * and GIF Image Files (*.jpg, *.png)"
     *
     * @see FileFilter.getDescription
     */
    override fun getDescription(): String {
        if (fullDescription == null) {
            if (description == null || isExtensionListInDescription) {
                fullDescription = if (description == null) "(" else "$description ("
                // build the description from the extension list
                val extensions: Iterator<String?> = filters!!.keys.iterator()
                fullDescription += "." + extensions.next()
                while (extensions.hasNext()) {
                    fullDescription += ", " + extensions.next()
                }
                fullDescription += ")"
            } else {
                fullDescription = description
            }
        }
        return fullDescription!!
    }

    /**
     * Sets the human readable description of this filter. For example:
     * filter.setDescription("Gif and JPG Images");
     */
    private fun setDescription(description: String?) {
        this.description = description
        fullDescription = null
    }

    /**
     * Returns whether the extension list (.jpg, .png, etc) should show up in
     * the human readable description.
     *
     *
     * Only relevent if a description was provided in the constructor or using
     * setDescription();
     */
    private val isExtensionListInDescription: Boolean
        get() = true

    companion object {
        /**
         * Return the extension portion of the file's name .
         *
         * @see .getExtension
         *
         * @see FileFilter.accept
         */
        fun getExtension(f: File?): String? {
            if (f != null) {
                val filename = f.name
                val i = filename.lastIndexOf('.')
                if (i > 0 && i < filename.length - 1) {
                    return filename.substring(i + 1).toLowerCase()
                }
            }
            return null
        }
    }

    /**
     * Creates a file filter. If no filters are added, then all files are
     * accepted.
     *
     * @see .addExtension
     */
    init {
        filters = HashMap()
    }
}