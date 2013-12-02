package com.niz.punk;

import com.badlogic.gdx.math.MathUtils;

public class NameGenerator {
private String[] townPre = {"Pine", "Meadow", "Valley", "Leaf", "Twig", 
		"Apple", "Fig", "Kirk", "Shadow", "Castle", "Fort", "Snow", "Rain", "Water", "River", "Bath", "Chiicken", "Dog"};

private String[] townPost = {"wood", "dale", "vale", "ville", "ton", "town", "worth", "burg", 
		"berg", "gate", "ham", " Heath", "'s Rock", " Gutter", " Hole", "'s Abbey", "'s Moore"};


private static String[] nordicFirstM = {"Aabel", "Aaku", "Abel", "Agnar", "Ahmad", "Bartol", "Bastian", "Baraldur", 
		"Bentt", "Beinar", "Barth", "Caleb", "Caelius", "Christer", "Clarus", "Cort", "Cennet", 
		"Dahl", "Dagur", "Dex", "Detlev", "Edvard", "Egild", "Edwin", "Einokki", "Edlan", "Elner", 
		"Emelius", "Ejnar", "Eko", "Ewen", "Fabius", "Falko", "Feliks",
		"Filip", "Frands", "Finne", "Flavius", "Gabrel", "Gernold", "Giles", "Gregar", "Gudhmund",
		"Haakko", "Haapi", "Hakan", "Haldan", "Hallfred", "Hagrup", "Hedebert", "Heine", "Herjolfr", "HErmes", "Hinrek", "Holmr", "Holgar",
		"Ingarth", "Ingeman", "Ikko", "Jakob", "Jan", "Jasper", "Jason", "Kjaran", "Kollur", "Kristinn", "Lambert", "Lars", "Lev"
		, "Magnus", "Marino", "Mikael", "Narfi", "Oddi", "Ormur", "Oliver", "Pstrek", "Petur", "Ragnar", "Randver", "Reynir", "Samson", "Siggeir"
		, "Stefan", "Steinmar", "Styr", "Svartur", "Tristan", "Tindur", "Tobias", "Valr", "Vagn", "Vakur", "Vigmar", "Ymir"};

private static String[] nordicFirstF = {"Abela", "Agnes", "Alda", "Alexa", "Anika", "Anja", "Belinda", "Bella", "Brynhildur", "Brynja", 
		"Camilla", "Daniela", "Dora", "Emelia", "Elma", "Ella", "Finna", "Gabriela", "Geira", "Hadda", "Halla", "Hermina", 
		"Hilma", "Hiln", "Iris", "Irena", "Irma", "Kamila", "Kara", "Klara", "Kristey", "Kristn", "Lara", "Linde", "Maria", "Luisa"
		,"Petra", "Ragna", "Rakel", "Regina", "Rita", "MAgan", "Olina", "Olafina", "Runa", "Rebekka", "Ruth", "Selma", "Sesila", "Sol", "Sveita", "Sylvja", "Solva",
		"Ulfa", "Ulfina","Una"};

private String[] hillPre = {"Carn",  "Creagh", "Cleit", "Meall", "Meol", "Frithe", "Leana", "Moine"};
private String[] hillSuf = {"each", "anach", "dubh", "bhan", "fionn", "geal", "Dearg", "Ruadh", "Odhar", "Donn", "Gorm", "Fiath", "Breac", "Beag","Coire", "Eag", "Garbh", "LEc"};


private String[] worldPre = {"Au", "Bel", "Bos", "Fra", "Fin", "Mor", "Ire", "Ita", "Nor", "Ter", "Druim", "Imir"};

private String[] worldSuf = {"stria", "ria", "tium", "tia", "nia", "mark", "rus", "nium", "nia", "raine", "taine", "gia", "gary", "land"};

//galron martak 

private String[] orcPre = {"An", "Ak", "Bat", "Drac", "Du", "Koth", "Tor", "Yat", "Gor", "Div"};

private String[] orcSuf = {"la", "dich", "ron", "tak", "loth", "ragh", "ral", "loth", "den", "ris", "pa"};

public String getTownName(){
	String s = "";
	s += townPre[MathUtils.random(townPre.length-1)];
	s += townPost[MathUtils.random(townPost.length-1)];
	return s;
}
public String getWorldName(){
	String s = "";
	s += worldPre[MathUtils.random(worldPre.length-1)];
	s += worldSuf[MathUtils.random(worldSuf.length-1)];
	return s;
}

public String getHillName(){
	String s = "";
	s += hillPre[MathUtils.random(hillPre.length-1)];
	s += hillSuf[MathUtils.random(hillSuf.length-1)];
	return s;
}

public static String getName(int gender){
	if (gender == 0) return nordicFirstM[MathUtils.random(nordicFirstM.length-1)];
	return nordicFirstF[MathUtils.random(nordicFirstF.length-1)];
}

}
