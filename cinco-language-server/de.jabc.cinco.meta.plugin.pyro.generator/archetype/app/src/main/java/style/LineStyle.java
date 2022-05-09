package style;

public enum LineStyle {
    /**
     * The '<em><b>DASH</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DASH_VALUE
     * @generated
     * @ordered
     */
    DASH(4, "DASH", "DASH"),

    /**
     * The '<em><b>DASHDOT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DASHDOT_VALUE
     * @generated
     * @ordered
     */
    DASHDOT(1, "DASHDOT", "DASHDOT"),

    /**
     * The '<em><b>DASHDOTDOT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DASHDOTDOT_VALUE
     * @generated
     * @ordered
     */
    DASHDOTDOT(2, "DASHDOTDOT", "DASHDOTDOT"),

    /**
     * The '<em><b>DOT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DOT_VALUE
     * @generated
     * @ordered
     */
    DOT(3, "DOT", "DOT"),

    /**
     * The '<em><b>SOLID</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SOLID_VALUE
     * @generated
     * @ordered
     */
    SOLID(0, "SOLID", "SOLID"),

    /**
     * The '<em><b>UNSPECIFIED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #UNSPECIFIED_VALUE
     * @generated
     * @ordered
     */
    UNSPECIFIED(5, "UNSPECIFIED", "UNSPECIFIED");

    /**
     * The '<em><b>DASH</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DASH</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DASH
     * @model
     * @generated
     * @ordered
     */
    public static final int DASH_VALUE = 4;

    /**
     * The '<em><b>DASHDOT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DASHDOT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DASHDOT
     * @model
     * @generated
     * @ordered
     */
    public static final int DASHDOT_VALUE = 1;

    /**
     * The '<em><b>DASHDOTDOT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DASHDOTDOT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DASHDOTDOT
     * @model
     * @generated
     * @ordered
     */
    public static final int DASHDOTDOT_VALUE = 2;

    /**
     * The '<em><b>DOT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DOT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DOT
     * @model
     * @generated
     * @ordered
     */
    public static final int DOT_VALUE = 3;

    /**
     * The '<em><b>SOLID</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>SOLID</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #SOLID
     * @model
     * @generated
     * @ordered
     */
    public static final int SOLID_VALUE = 0;

    /**
     * The '<em><b>UNSPECIFIED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>UNSPECIFIED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #UNSPECIFIED
     * @model
     * @generated
     * @ordered
     */
    public static final int UNSPECIFIED_VALUE = 5;

    /**
     * An array of all the '<em><b>Line Style</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final LineStyle[] VALUES_ARRAY =
            new LineStyle[] {
                    DASH,
                    DASHDOT,
                    DASHDOTDOT,
                    DOT,
                    SOLID,
                    UNSPECIFIED,
            };

    /**
     * A public read-only list of all the '<em><b>Line Style</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final java.util.List<LineStyle> VALUES = java.util.Collections.unmodifiableList(java.util.Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Line Style</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param literal the literal.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static LineStyle get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            LineStyle result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Line Style</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param name the name.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static LineStyle getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            LineStyle result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Line Style</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the integer value.
     * @return the matching enumerator or <code>null</code>.
     * @generated
     */
    public static LineStyle get(int value) {
        switch (value) {
            case DASH_VALUE: return DASH;
            case DASHDOT_VALUE: return DASHDOT;
            case DASHDOTDOT_VALUE: return DASHDOTDOT;
            case DOT_VALUE: return DOT;
            case SOLID_VALUE: return SOLID;
            case UNSPECIFIED_VALUE: return UNSPECIFIED;
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
    private LineStyle(int value, String name, String literal) {
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

} //LineStyle
