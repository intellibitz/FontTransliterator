package sted.fontmap

import sted.fontmap.ITransliterate.IEntry
import sted.io.Resources

/**
 * Represents a FontMap Entry. All Mappings are transformed to a FontMapEntry.
 */
class FontMapEntry : IEntry, Comparable<Any?>, Cloneable {
    override lateinit var from: String
    override lateinit var to: String
    var isBeginsWith = false
    var isEndsWith = false

    private var conditional = "AND"
    lateinit var id: String
        private set
    private var status = -1

    fun init(from: String, to: String) {
        this.from = from
        this.to = to
        id = Resources.id.toString()
    }

    val isAdded: Boolean
        get() = 1 == status
    val isEdited: Boolean
        get() = 2 == status
    val isDeleted: Boolean
        get() = 3 == status

    private fun getConditional(): String {
        return conditional
    }

    fun setConditional(conditional: String?) {
        when {
            "AND".equals(conditional, ignoreCase = true) -> {
                this.conditional = "AND"
            }
            "OR".equals(conditional, ignoreCase = true) -> {
                this.conditional = "OR"
            }
            "NOT".equals(conditional, ignoreCase = true) -> {
                this.conditional = "NOT"
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
        get() = (from.isNotEmpty() && to.isNotEmpty() && from != to)

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
                ":" +
                to +
                ":" +
                isBeginsWith +
                ":" +
                isEndsWith +
                ":" +
                followedBy +
                ":" +
                precededBy +
                ":" +
                getConditional()
    }

    override fun compareTo(other: Any?): Int {
        if (other is FontMapEntry) {
            if (equals(other)) {
                return 0 //To change body of implemented methods use Options | File Templates.
            } else {
                if (from != other.from) {
                    return from.compareTo(other.from)
                }
                if (to != other.to) {
                    return to.compareTo(other.to)
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

}