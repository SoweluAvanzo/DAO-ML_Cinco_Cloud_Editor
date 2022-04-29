package de.jabc.cinco.meta.core.utils.generator;

public enum ReservedKeyWords {
	Abstract("abstract"),
	Continue ("continue"),
	For ("for"),
	New ("new"),
	Switch ("switch"),
	Assert ("assert"),
	Default ("default"),
	Goto ("goto"),
	Package ("package"),
	Synchronized ("synchronized"),
	Boolean ("boolean"),
	Do ("do"),
	If ("if"),
	Private ("private"),
	This ("this"),
	Break ("break"),
	Double ("double"),
	Implements ("implements"),
	Protected ("protected"),
	Throw ("throw"),
	Byte ("byte"),
	Else ("else"),
	Import ("import"),
	Public ("public"),
	Throws ("throws"),
	Case ("case"),
	Enum ("enum"),
	Instanceof ("instance"),
	Return ("return"),
	Transient ("transient"),
	Catch ("catch"),
	Extends ("extends"),
	Int ("int"),
	Short ("short"),
	Try ("try"),
	Char ("char"),
	Final ("final"),
	Interface ("interface"),
	Static ("static"),
	Void ("void"),
	Class ("class"),
	Finally ("finally"),
	Long ("long"),
	Strictfp ("strictfp"),
	Volatile ("volatile"),
	Const ("const"),
	Float ("float"),
	Native ("native"),
	Super ("super"),
	While ("while"),
	True ("true"),
	False ("false");
	
	private String value;

	private ReservedKeyWords(String keyWord) {
		value = keyWord;
	}

	public String getKeyword() {
		return value;
	}	
}
