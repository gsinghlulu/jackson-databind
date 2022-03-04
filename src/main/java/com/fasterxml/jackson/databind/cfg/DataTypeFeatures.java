package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.core.util.JacksonFeature;
import com.fasterxml.jackson.core.util.VersionUtil;

/**
 * Immutable value class that contains settings for multiple
 * {@link DataTypeFeature} enumerations.
 *
 * @since 2.14
 */
public class DataTypeFeatures
{
    protected final static int FEATURE_INDEX_ENUM = 0;
    protected final static int FEATURE_INDEX_JSON_NODE = 1;

    private final int _enabledFor1, _enabledFor2;

    private final int _explicitFor1, _explicitFor2;

    protected DataTypeFeatures(int enabledFor1, int explicitFor1,
            int enabledFor2, int explicitFor2)  {
        _enabledFor1 = enabledFor1;
        _explicitFor1 = explicitFor1;
        _enabledFor2 = enabledFor2;
        _explicitFor2 = explicitFor2;
    }

    public static DataTypeFeatures defaultFeatures() {
        return DefaultHolder.getDefault();
    }

    private DataTypeFeatures _with(int enabledFor1, int explicitFor1,
            int enabledFor2, int explicitFor2) {
        if ((_enabledFor1 == enabledFor1) && (_explicitFor1 == explicitFor1)
                && (_enabledFor2 == enabledFor2) && (_explicitFor2 == explicitFor2)) {
            return this;
        }
        return new DataTypeFeatures(enabledFor1, explicitFor1,
                enabledFor2, explicitFor2);
    }

    /**
     * Mutant factory method that returns an instance with given Feature
     * explicitly enabled.
     *
     * @param f {@link DataTypeFeature} to explicitly enable in this set
     *
     * @return Existing instance if there is no change (feature was already explicitly set),
     *    or a new instance with feature explicitly set (if it was not).
     */
    public DataTypeFeatures with(DataTypeFeature f) {
        final int mask = f.getMask();
        switch (f.featureIndex()) {
        case 0:
            return _with(_enabledFor1 | mask, _explicitFor1 | mask,
                    _enabledFor2, _explicitFor2);
        case 1:
            return _with(_enabledFor1, _explicitFor1,
                    _enabledFor2 | mask, _explicitFor2 | mask);
        default:
            VersionUtil.throwInternal();
            return this;
        }
    }

    /**
     * Accessor for getting value of specified feature in this set, regardless of
     * whether explicit defined or not (if not explicitly enabled/disabled returns
     * default value, {@link DataTypeFeature#enabledByDefault()}.
     *
     * @param f Feature to check
     *
     * @return True if the specified Feature is enabled in this set either explicitly
     *    or by having enabled by default (if not explicit enabled or disabled).
     */
    public boolean isEnabled(DataTypeFeature f) {
        switch (f.featureIndex()) {
        case 0:
            return f.enabledIn(_enabledFor1);
        case 1:
            return f.enabledIn(_enabledFor2);
        default:
            VersionUtil.throwInternal();
            return false;
        }
    }

    /**
     * Accessor for checking whether given feature has been explicitly enabled or
     * disabled in this set or not: if not it has the default value.
     *
     * @param f Feature to check
     *
     * @return Whether given feature has been explicitly set (enabled or disabled)
     *   in this set or not.
     */
    public boolean isExplicitlySet(DataTypeFeature f) {
        switch (f.featureIndex()) {
        case 0:
            return f.enabledIn(_explicitFor1);
        case 1:
            return f.enabledIn(_explicitFor2);
        default:
            VersionUtil.throwInternal();
            return false;
        }
    }

    /**
     * Accessor for getting explicit state of given feature in this set
     * iff explicitly set, or {@code null} if not explicitly set (default value)
     *
     * @param f Feature to check
     *
     * @return {@code Boolean.TRUE} if Feature has been explicitly enabled in this set;
     *    {@code Boolean.FALSE} if Feature has been explicitly disabled in this set; or
     *    {@code null} if Feature still has its default value.
     */
    public Boolean getExplicitState(DataTypeFeature f) {
        switch (f.featureIndex()) {
        case 0:
            if (f.enabledIn(_explicitFor1)) {
                return f.enabledIn(_enabledFor1);
            }
            return null;
        case 1:
            if (f.enabledIn(_explicitFor2)) {
                return f.enabledIn(_enabledFor2);
            }
            return null;
        default:
            VersionUtil.throwInternal();
            return null;
        }
    }

    /*
    /**********************************************************************
    /* Helper classes
    /**********************************************************************
     */

    /**
     * Helper class to avoid cyclic dependencies when initializing default
     * instance for {@link DataTypeFeatures}.
     */
    private static class DefaultHolder
    {
        private static final DataTypeFeatures DEFAULT_FEATURES;
        static {
            DEFAULT_FEATURES = new DataTypeFeatures(
                    collectDefaults(EnumFeature.values()), 0,
                    collectDefaults(JsonNodeFeature.values()), 0
                    );
        }

        private static <F extends Enum<F> & JacksonFeature> int collectDefaults(F[] features)
        {
            int flags = 0;
            for (JacksonFeature f : features) {
                if (f.enabledByDefault()) {
                    flags |= f.getMask();
                }
            }
            return flags;
        }

        public static DataTypeFeatures getDefault() {
            return DEFAULT_FEATURES;
        }
    }
}
