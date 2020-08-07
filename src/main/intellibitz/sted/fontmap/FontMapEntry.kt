package sted.fontmap

import sted.fontmap.ITransliterate.IEntry
import sted.io.Resources

/**
 * Represents a FontMap Entry. All Mappings are transformed to a FontMapEntry.
 */
class FontMapEntry() : IEntry, Comparable<Any?>, Cloneable {
    override var from: String? = null
    override var to: String? = null
    var isBeginsWith = false
    var isEndsWith = false

    private var conditional = Resources.ENTRY_CONDITIONAL_AND
    var id: String
        private set
    private var status = -1

    constructor(from: String?, to: String?) : this() {
        this.from = from
        this.to = to
    }

    val isAdded: Boolean
        get() = Resources.ENTRY_STATUS_ADD == status
    val isEdited: Boolean
        get() = Resources.ENTRY_STATUS_EDIT == status
    val isDeleted: Boolean
        get() = Resources.ENTRY_STATUS_DELETE == status

    private fun getConditional(): String {
        return conditional
    }

    fun setConditional(conditional: String?) {
        when {
            Resources.ENTRY_CONDITIONAL_AND.equals(conditional, ignoreCase = true) -> {
                this.conditional = Resources.ENTRY_CONDITIONAL_AND
            }
            Resources.ENTRY_CONDITIONAL_OR.equals(conditional, ignoreCase = true) -> {
                this.conditional = Resources.ENTRY_CONDITIONAL_OR
            }
            Resources.ENTRY_CONDITIONAL_NOT.equals(conditional, ignoreCase = true) -> {
                this.conditional = Resources.ENTRY_CONDITIONAL_NOT
            }
            else -> {
                throw IllegalArgumentException(conditional)
            }
        }
    }

    fun setBeginsWith(beginsWith: String?) {
        isBeginsWith = java.lang.Boolean.parseBoolean(beginsWith)
    }

    fun setEndsWith(endsWith: String?) {
        isEndsWith = java.lang.Boolean.parseBoolean(endsWith)
    }

    var followedBy: String? = null
        set(value) {
            if (!value?.isBlank()!!) {
                field = value
            }
        }

    var precededBy: String? = null
        set(value) {
            if (!value?.isBlank()!!) {
                field = value
            }
        }

    val isValid: Boolean
        get() = (from != null && to != null && Resources.EMPTY_STRING != from
                && Resources.EMPTY_STRING != to
                && from != to)

    fun setStatus(status: Int) {
        this.status = status
    }

    /**
     * @return true if atleast one of the rules is set
     */
    val isRulesSet: Boolean
        get() = (isBeginsWith
                || isEndsWith
                || followedBy != null && followedBy!!.isNotEmpty() || precededBy != null && precededBy!!.isNotEmpty())

    /**
     * returns a string representation of a fontmap entry
     *
     * @return String
     */
    override fun toString(): String {
        return from +
                Resources.ENTRY_TOSTRING_DELIMITER +
                to +
                Resources.ENTRY_TOSTRING_DELIMITER +
                isBeginsWith +
                Resources.ENTRY_TOSTRING_DELIMITER +
                isEndsWith +
                Resources.ENTRY_TOSTRING_DELIMITER +
                followedBy +
                Resources.ENTRY_TOSTRING_DELIMITER +
                precededBy +
                Resources.ENTRY_TOSTRING_DELIMITER +
                getConditional()
    }

    override fun compareTo(other: Any?): Int {
        if (other is FontMapEntry) {
            if (equals(other)) {
                return 0 //To change body of implemented methods use Options | File Templates.
            } else {
                if (from != other.from) {
                    return from!!.compareTo(other.from!!)
                }
                if (to != other.to) {
                    return to!!.compareTo(other.to!!)
                }
                if (isBeginsWith != other.isBeginsWith) {
                    return 1
                }
                if (isEndsWith != other.isEndsWith) {
                    return 1
                }
                if (conditional != other.getConditional()) {
                    return conditional.compareTo(other.getConditional())
                }
                var `val` = if (followedBy == null) other.followedBy == null else followedBy == other.followedBy
                if (!`val`) {
                    return if (followedBy != null && other.followedBy != null) {
                        followedBy!!.compareTo(other.followedBy!!)
                    } else 1
                }
                `val` = if (precededBy == null) other.precededBy == null else precededBy == other.precededBy
                if (!`val`) {
                    return if (precededBy != null && other.precededBy != null) {
                        precededBy!!.compareTo(other.precededBy!!)
                    } else 1
                }
            }
        }
        return -1 //To change body of implemented methods use Options | File Templates.
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is FontMapEntry) {
            return false
        }
        if (from != other.from) {
            return false
        }
        if (to != other.to) {
            return false
        }
        if (isBeginsWith != other.isBeginsWith) {
            return false
        }
        if (isEndsWith != other.isEndsWith) {
            return false
        }
        if (conditional != other.getConditional()) {
            return false
        }
        //        if (status != target.getStatus()) return false;
        val `val` = if (followedBy == null) other.followedBy == null else followedBy == other.followedBy
        if (!`val`) {
            return false
        }
        return if (precededBy == null) other.precededBy == null else precededBy == other.precededBy
    }

    override fun hashCode(): Int {
        var result: Int = from.hashCode()
        result = 29 * result + to.hashCode()
        result = 29 * result + conditional.hashCode()
        result = 29 * result + if (isBeginsWith) 1 else 0
        result = 29 * result + if (isEndsWith) 1 else 0
        result = 29 * result + if (followedBy != null) followedBy.hashCode() else 0
        result = 29 * result + if (precededBy != null) precededBy.hashCode() else 0
        return result
    }

    public override fun clone(): Any {
//            throws CloneNotSupportedException {
        try {
            val cloned = super.clone() as FontMapEntry
            cloned.id = id
            return cloned
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace() //To change body of catch statement use Options | File Templates.
        }
        return 0
    }

    init {
        id = Resources.getId().toString()
    }
}