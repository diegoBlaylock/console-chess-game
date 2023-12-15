package edu.blaylock.server.database.fields;

/**
 * List of attributes for a column.
 *
 * @param unique        Enforce uniqueness constraint
 * @param index         Index column
 * @param primary       Primary index
 * @param maxLength     Used for varchar
 * @param nullable      Can be null
 * @param autoIncrement Used by int field
 */
public record ColumnAttributes(boolean unique, boolean index, boolean primary, int maxLength,
                               boolean nullable, boolean autoIncrement) {

    public static AttributeBuilder builder() {
        return new AttributeBuilder();
    }

    /**
     * Unique - false
     * Index - false
     * Primary - false
     * maxLength - 255
     * nullable - true
     * autoincrement - false
     */
    public static ColumnAttributes defaultAttrs() {
        return builder().build();
    }

    /**
     * Helper to create column attributes
     */
    public static class AttributeBuilder {
        private boolean unique = false;
        private boolean index = false;
        private boolean primary = false;
        private int maxLength = 255;

        private boolean nullable = true;
        private boolean autoIncrement = false;

        /**
         * Make Unique
         *
         * @return this instance
         */
        public AttributeBuilder unique() {
            unique = true;
            return this;
        }

        /**
         * Index attribute
         *
         * @return this instance
         */
        public AttributeBuilder index() {
            index = true;
            return this;
        }

        /**
         * Make primary
         *
         * @return this instance
         */
        public AttributeBuilder primary() {
            primary = true;
            return this;
        }

        /**
         * Max length (used by a couple of field types)
         *
         * @param newLength Specify length
         * @return this instance
         */
        public AttributeBuilder maxLength(int newLength) {
            maxLength = newLength;
            return this;
        }

        /**
         * Make non null
         *
         * @return this instance
         */
        public AttributeBuilder notNull() {
            nullable = false;
            return this;
        }

        /**
         * Add autoincrement attribute
         *
         * @return this instance
         */
        public AttributeBuilder autoIncrement() {
            autoIncrement = true;
            return this;
        }

        public ColumnAttributes build() {
            return new ColumnAttributes(unique, index, primary, maxLength, nullable, autoIncrement);
        }

    }
}
