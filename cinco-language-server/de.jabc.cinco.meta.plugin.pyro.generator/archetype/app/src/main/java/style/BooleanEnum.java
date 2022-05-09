package style;

public enum BooleanEnum {
    /**
     * The '<em><b>True</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #TRUE_VALUE
     * @generated
     * @ordered
     */
    TRUE(1, "true", "true"),

    /**
     * The '<em><b>False</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FALSE_VALUE
     * @generated
     * @ordered
     */
    FALSE(0, "false", "false"),

    /**
     * The '<em><b>Undef</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #UNDEF_VALUE
     * @generated
     * @ordered
     */
    UNDEF(-1, "undef", "undef");

    /**
     * The '<em><b>True</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>True</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #TRUE
     * @model name="true"
     * @generated
     * @ordered
     */
    public static final int TRUE_VALUE = 1;

    /**
     * The '<em><b>False</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>False</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #FALSE
     * @model name="false"
     * @generated
     * @ordered
     */
    public static final int FALSE_VALUE = 0;

    /**
     * The '<em><b>Undef</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Undef</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #UNDEF
     * @model name="undef"
     * @generated
     * @ordered
     */
    public static final int UNDEF_VALUE = -1;

    /**
     * An array of all the '<em><b>Boolean Enum</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final BooleanEnum[] VALUES_ARRAY =
            new BooleanEnum[] {
                    TRUE,
                    FALSE,
                    UNDEF,
            };

    /**
     * A public read-only list of all the '<em><b>Boolean Enum</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final java.util.List<BooleanEnum> VALUES = java.util.Collections.unmodifiableList(java.util.Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Boolean Enum</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param literal the literal.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static BooleanEnum get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            BooleanEnum result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Boolean Enum</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param name the name.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static BooleanEnum getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            BooleanEnum result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Boolean Enum</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the integer value.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static BooleanEnum get(int value) {
        switch (value) {
            case TRUE_VALUE: return TRUE;
            case FALSE_VALUE: return FALSE;
            case UNDEF_VALUE: return UNDEF;
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final int value;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String name;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String literal;

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private BooleanEnum(int value, String name, String literal) {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getValue() {
        return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Returns the literal value of the enumerator, which is its string representation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        return literal;
    }

} //BooleanEnum
