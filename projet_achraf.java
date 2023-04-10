import java.util.*;

public class Demineur {

    private final Grid plateau;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";

    private final String symbMine = ANSI_RED + "*" + ANSI_RESET;
    private final String symbDec = " ";
    private final String symbCache = "■";         // "\u25A0" =  ■,  \u2588" =  █
    private final String symbMarque = ANSI_GREEN + symbCache + ANSI_RESET;

    private boolean affMines = false;

    private int nbCaseDecouvrit;
    private int nbCaseMarque;

    public enum Direction {
        HAUT_GAUCHE, HAUT, HAUT_DROITE, GAUCHE, DROITE, BAS_GAUCHE, BAS, BAS_DROITE
    }


    Demineur(int LONG, int LARG, int PRCTG){
        this.plateau = new Grid(LONG, LARG, PRCTG);
        this.nbCaseDecouvrit = 0;
        this.nbCaseMarque = 0;
        this.plateau.afficher();
    }
    //methodes de jeux
    public boolean decouvreCase(int X, int Y) {
        boolean finJeux = false;
        Case laCase = plateau.getCase(X, Y);
        laCase.setSelectionne(false);
        if (!laCase.estDecouverte() && !laCase.estMarque()) {
            laCase.setDecouverte(true);
            if (laCase.aUneMine()) finJeux = true;
            this.nbCaseDecouvrit += 1 + laCase.nettVoisinage();
        }else{
            System.out.println("la case est marque ou decouverte\n");
        }
        return finJeux;
    }
    public void marqueCase(int X, int Y) {
        Case laCase = plateau.getCase(X, Y);
        laCase.setSelectionne(false);
        if (!laCase.estMarque() && !laCase.estDecouverte()) {
            laCase.setMarque(true);
            this.nbCaseMarque += 1;
        }else{
            System.out.println("impossible de marquer cette case\n");
        }
    }

    public boolean selectCase(int X, int Y) {
        Case laCase = plateau.getCase(X, Y);
        int LONG = plateau.getDimension()[0];
        int LARG = plateau.getDimension()[1];
        if(X < 0 || X > LONG - 1 || Y < 0 || Y > LARG - 1){
            System.out.println("invalide coordonnes");
            return false;
        }
        laCase.setSelectionne(true);
        plateau.afficher();
        return true;
    }
    public void unselectCase(int X, int Y){
        Case laCase = plateau.getCase(X, Y);
        if (laCase.estSelectionne()){
            laCase.setSelectionne(false);
        }
    }

    public boolean getAffMines(){
        return this.affMines;
    }
    public void setAffMines(boolean aff){
        this.affMines = aff;
    }

    public Grid getPlateau(){
        return this.plateau;
    }

    public int getNbCaseDecouvrit(){ return this.nbCaseDecouvrit;}
    public int getNbCaseMarque(){ return this.nbCaseMarque;}

    public void affScore(long TIME_S, int nbCelMarq){
        System.out.print("\nTemps passé    |      " + TIME_S / 60 + ":" + TIME_S % 60 +"\n"
                        +"cellues marqué |      " + nbCelMarq + "\n"
                        +"score          |      " + TIME_S * 4 + nbCelMarq * 2 + "\n");
    }

    class Grid {
        private final int nbLigne;
        private final int nbCols;
        private final float prctg;
        private int nbMines;
        //private final List<List<Case>> plateau;
        private final Case plateau;


        Grid(int nbLigne, int nbCols, float prctg) {
            this.nbLigne = nbLigne;
            this.nbCols = nbCols;
            this.prctg = prctg;
            this.nbMines = 0;
            //this.plateau = new ArrayList<List<Case>>();
            this.plateau = new Case(prctg);
            this.initialize();
            this.lieCases();
        }

        private void initialize() {
            Case laCase = plateau;
            if (laCase.aUneMine()){
                nbMines += 1;
            }
            Case copie;
            Case lineHead = plateau;

            for (int i = 0; i < nbLigne; i++) {
                if(i%2==0){
                    lineHead = laCase;
                }
                for (int j = 0; j < nbCols-1; j++) { // why nbCols-1

                    if(i%2 == 0) {
                        copie = laCase;
                        laCase = laCase.setVoisine(Direction.DROITE, new Case(prctg));
                        laCase.setVoisine(Direction.GAUCHE, copie);
                    }
                    else{
                        copie = laCase;
                        laCase = laCase.setVoisine(Direction.GAUCHE, new Case(prctg));
                        laCase.setVoisine(Direction.DROITE, copie);
                    }
                    if (laCase.aUneMine()){
                        nbMines += 1;
                    }
                }
                if(i%2 == 1){
                    lineHead.setVoisine(Direction.BAS, laCase);
                }
                if(i < nbLigne-1) {
                    copie = laCase;
                    laCase = laCase.setVoisine(Direction.BAS, new Case(prctg));
                    if (laCase.aUneMine()){
                        nbMines += 1;
                    };
                    laCase.setVoisine(Direction.HAUT, copie);
                }
            }
        }

        private void lieCases() {
            for (int i = 0; i < nbLigne; i++) {
                for (int j = 0; j < nbCols; j++) {
                    Case laCase = getCase(i, j);

                    boolean H_G = i != 0 && j != 0;
                    boolean H = i != 0;
                    boolean H_D = i != 0 && j != nbCols - 1;
                    boolean G = j != 0;
                    boolean D = j != nbCols - 1;
                    boolean B_G = i != nbLigne - 1 && j != 0;
                    boolean B = i != nbLigne - 1;
                    boolean B_D = i != nbLigne - 1 && j != nbCols - 1;

                    if (H_G) {
                        laCase.setVoisine(Direction.HAUT_GAUCHE, getCase(i - 1,j - 1));
                        if (getCase(i - 1,j - 1).aUneMine()) laCase.augNbMine();
                    }
                    if (H) {
                        laCase.setVoisine(Direction.HAUT, getCase(i - 1, j));
                        if (getCase(i - 1, j).aUneMine()) laCase.augNbMine();
                    }
                    if (H_D) {
                        laCase.setVoisine(Direction.HAUT_DROITE, getCase(i - 1, j + 1));
                        if (getCase(i - 1, j + 1).aUneMine()) laCase.augNbMine();
                    }
                    if (G) {
                        laCase.setVoisine(Direction.GAUCHE, getCase(i, j - 1));
                        if (getCase(i, j - 1).aUneMine()) laCase.augNbMine();
                    }
                    if (D) {
                        laCase.setVoisine(Direction.DROITE, getCase(i, j + 1));
                        if (getCase(i, j + 1).aUneMine()) laCase.augNbMine();
                    }
                    if (B_G) {
                        laCase.setVoisine(Direction.BAS_GAUCHE, getCase(i + 1, j - 1));
                        if (getCase(i + 1, j - 1).aUneMine()) laCase.augNbMine();
                    }
                    if (B) {
                        laCase.setVoisine(Direction.BAS, getCase(i + 1, j));
                        if (getCase(i + 1, j).aUneMine()) laCase.augNbMine();
                    }
                    if (B_D) {
                        laCase.setVoisine(Direction.BAS_DROITE, getCase(i + 1, j + 1));
                        if (getCase(i + 1, j + 1).aUneMine()) laCase.augNbMine();
                    }
                }
            }
        }

        public void afficher() {
            String symb;
            Case laCase;
            clear.clearConsole();
            int nbDigColMax = String.valueOf(nbCols - 1).length();
            // les nombres
            System.out.print(" ".repeat(nbDigColMax + 1) + "  ");
            for (int i = 0; i < nbCols; i++) {
                int nbEspace = nbDigColMax - String.valueOf(i).length() + 1;
                if (nbDigColMax == 1) nbEspace += 1;
                System.out.print(i + " ".repeat(nbEspace));
            }

            // les _
            System.out.print("\n" + " ".repeat(nbDigColMax + 1) + "  ");
            for (int i = 0; i < nbCols; i++) {

                System.out.print("_  ");
            }

            // nb ligne + les cases
            System.out.println();
            for (int i = 0; i < nbLigne; i++) {
                System.out.print(i + " ".repeat(nbDigColMax - String.valueOf(i).length() + 1) + "| ");
                for (int j = 0; j < nbCols; j++) {

                    laCase = getCase(i, j);
                    symb = laCase.getSymb();
                    if(affMines){
                        if (laCase.aUneMine()) symb = symbMine;
                    }
                    System.out.print(symb + "  ");
                }
                System.out.println();
            }
        }

        public Case getCase(int X, int Y) {
            Case laCase = this.plateau;
            for(int i = 0; i < X; i++){
                laCase = laCase.getCasesVoisins().get(Direction.BAS);
            }
            for(int j = 0; j < Y; j++){
                laCase = laCase.getCasesVoisins().get(Direction.DROITE);
            }
            return laCase;
        }

        public int[] getDimension(){
            int[] par = new int[2];
            par[0] = this.nbLigne;
            par[1] = this.nbCols;
            return par;
        }
        public int getNbMines(){
            return this.nbMines;
        }



    }
    public class Case {
        private boolean mine; // true si la case contient une mine
        private boolean marque; // true si la case est marquée
        private boolean decouverte; // true si la case est découverte
        private boolean selectionne;
        private int nbMinesVoisin; // nombre de cases adjacentes contenant des mines
        EnumMap<Direction, Case> casesVoisins; // tableau contenant les voisins de la case (je pense qu il faut ajouter private)

        public Case(float prctg) {
            this.marque = false;
            this.decouverte = false;
            this.selectionne = false;
            this.nbMinesVoisin = 0;
            this.casesVoisins = new EnumMap<>(Direction.class);
            setMine(prctg);
        }

        public Case setVoisine(Direction index, Case voisin) {
            this.casesVoisins.put(index, voisin);
            return voisin;
        }

        public EnumMap<Direction, Case> getCasesVoisins(){
            return  casesVoisins;
        }

        public void setMine(float prctg){
            float randomNb = (new Random()).nextFloat(100.0f);
            this.mine = randomNb <= prctg;
        }

        public boolean aUneMine(){
            return this.mine;
        }
        // useless? public void setMine(boolean aMine) {this.mine = aMine;}

        public void augNbMine(){
            this.nbMinesVoisin += 1;
        }
        public void setMarque(boolean estMarque){
            this.marque = estMarque;
        }
        public boolean estMarque() {return this.marque;}

        public void setDecouverte(boolean estDec){
            this.decouverte = estDec;
        }
        public boolean estDecouverte() {return this.decouverte;}

        public boolean estSelectionne() {return this.selectionne;}
        public void setSelectionne(boolean estSec) {this.selectionne = estSec;}
        public int getNbMinesVoisin() { return this.nbMinesVoisin;}


        public String getSymb(){
            String symb;
            if (this.decouverte) {

                if (this.mine) symb = symbMine;
                else if (this.nbMinesVoisin != 0) symb = ANSI_BLUE + Integer.toString(this.nbMinesVoisin) + ANSI_RESET;
                else symb = symbDec;
            }
            else {
                if (this.marque) symb = symbMarque;
                else symb = symbCache;
                if (this.selectionne) symb = "\033[45m" + symb + "\033[0m";
            }
            return symb;
        }

        public int nettVoisinage(){ //wrong return value
            int nbCaseDec = 0;
            if(this.getNbMinesVoisin() == 0 && !this.aUneMine()) {
                for (Direction direction : Direction.values()) {
                    Case caseVoisine = this.casesVoisins.getOrDefault(direction, null);
                    if(caseVoisine != null && !caseVoisine.estDecouverte() && !caseVoisine.estMarque()) {
                        caseVoisine.setDecouverte(true);
                        nbCaseDec += (1 + caseVoisine.nettVoisinage());
                    }
                }
            }
            return nbCaseDec;
        }
    }

    public static void main(String[] args) {
        long startTime = 0;

        String gagner = "\n" +
                "   ____      _       ____    _   _   U _____ u   ____     \n" +
                "U /\"___|uU  /\"\\  uU /\"___|u | \\ |\"|  \\| ___\"|/U |  _\"\\ u  \n" +
                "\\| |  _ / \\/ _ \\/ \\| |  _ /<|  \\| |>  |  _|\"   \\| |_) |/  \n" +
                " | |_| |  / ___ \\  | |_| | U| |\\  |u  | |___    |  _ <    \n" +
                "  \\____| /_/   \\_\\  \\____|  |_| \\_|   |_____|   |_| \\_\\   \n" +
                "  _)(|_   \\\\    >>  _)(|_   ||   \\\\,-.<<   >>   //   \\\\_  \n" +
                " (__)__) (__)  (__)(__)__)  (_\")  (_/(__) (__) (__)  (__) \n";

        int LONG = 10;
        int LARG = 10;
        int PRCTG = 10;

        Demineur jeux = new Demineur(LONG, LARG, PRCTG);

        boolean tousDec = false;
        boolean perdue = false;

        Scanner scanner = new Scanner(System.in);
        String[] rep = {""};

        while (!perdue && !(tousDec = (jeux.getNbCaseDecouvrit() == (LONG * LARG - jeux.plateau.getNbMines())))) {

            startTime = System.currentTimeMillis();

            System.out.print("\nselectionez une case X Y (ou '/q' pour quitter):");String inputS = scanner.nextLine();
            rep = inputS.split(" ");

            if (rep[0].equals("/q")) {
                break;
            } else if (rep[0].equals("/win")) {
                jeux.setAffMines(true);
                tousDec = true;
                break;
            /*
            } else if (rep[0].equals("/am")) {
                jeux.setAffMines(true);
            }*/

            int X = Integer.parseInt(rep[0]);
            int Y = Integer.parseInt(rep[1]);


            if (jeux.selectCase(X, Y)) {

                System.out.print("\n[1]decouvrir [2]marquer [3]annuler:");
                int inputI = scanner.nextInt();
                scanner.nextLine(); // consume newline character
                switch (inputI) {
                    case 1 -> jeux.setAffMines((perdue = jeux.decouvreCase(X, Y)));
                    case 2 -> jeux.marqueCase(X, Y);
                    case 3 -> jeux.unselectCase(X, Y);
                    default -> System.out.println("option inconnue");
                }
            }
            jeux.getPlateau().afficher();
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        if (tousDec && !perdue) {
            jeux.affScore(elapsedTime/1000, jeux.getNbCaseMarque());
            System.out.print(gagner);
        }else {
            System.out.print("perdue\n");
        }

        //System.out.println("the elapsed time is " + elapsedTime / (1000 * 60) + ":" + elapsedTime / 1000);

        System.out.println("\ncode exited");
    }
}