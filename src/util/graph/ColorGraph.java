package util.graph;

import util.intset.IntSet;

import java.util.Stack;

public class ColorGraph {
    static int NOCOLOR = -1;
    public Graph G;
    public int R;
    public int K;
    public IntSet enleves;
    public IntSet deborde;
    public int[] couleur;
    public Node[] int2Node;
    private Stack<Integer> pile;

    public ColorGraph(Graph G, int K, int[] phi) {
        this.G = G;
        this.K = K;
        pile = new Stack<Integer>();
        R = G.nodeCount();
        couleur = new int[R];
        enleves = new IntSet(R);
        deborde = new IntSet(R);
        int2Node = G.nodeArray();
        for (int v = 0; v < R; v++) {
            int preColor = phi[v];
            if (preColor >= 0 && preColor < K)
                couleur[v] = phi[v];
            else
                couleur[v] = NOCOLOR;
        }
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* associe une couleur à tous les sommets se trouvant dans la pile */
    /*-------------------------------------------------------------------------------------------------------------*/

    public void selection() {
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* récupère les couleurs des voisins de t */
    /*-------------------------------------------------------------------------------------------------------------*/

    public IntSet couleursVoisins(int t) {
        throw new RuntimeException("Not implemented yet");
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* recherche une couleur absente de colorSet */
    /*-------------------------------------------------------------------------------------------------------------*/

    public int choisisCouleur(IntSet colorSet) {
        throw new RuntimeException("Not implemented yet");
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* calcule le nombre de voisins du sommet t */
    /*-------------------------------------------------------------------------------------------------------------*/

    public int nbVoisins(int t) {
        throw new RuntimeException("Not implemented yet");
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /* simplifie le graphe d'interférence g                                                                        */
    /* la simplification consiste à enlever du graphe les temporaires qui ont moins de k voisins                   */
    /* et à les mettre dans une pile                                                                               */
    /* à la fin du processus, le graphe peut ne pas être vide, il s'agit des temporaires qui ont au moins k voisin */
    /*-------------------------------------------------------------------------------------------------------------*/

    public int simplification() {
        throw new RuntimeException("Not implemented yet");
    }

    /*-------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------------------------------------*/

    public void debordement() {
        throw new RuntimeException("Not implemented yet");
    }


    /*-------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------------------------------------*/

    public void coloration() {
        this.simplification();
        this.debordement();
        this.selection();
    }

    void affiche() {
        System.out.println("vertex\tcolor");
        for (int i = 0; i < R; i++) {
            System.out.println(i + "\t" + couleur[i]);
        }
    }


}
